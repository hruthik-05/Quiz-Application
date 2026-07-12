package com.projectquiz.demo.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectquiz.demo.models.Role;
import com.projectquiz.demo.models.User;
import com.projectquiz.demo.payload.request.LoginRequest;
import com.projectquiz.demo.payload.request.SignupRequest;
import com.projectquiz.demo.payload.response.UserInfoResponse;
import com.projectquiz.demo.payload.response.MessageResponse;
import com.projectquiz.demo.repositories.UserRepository;
import com.projectquiz.demo.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  SecurityContextRepository securityContextRepository;

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);
    securityContextRepository.saveContext(context, request, response);
    
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();    
    List<String> roles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

    return ResponseEntity.ok(new UserInfoResponse(
                         userDetails.getId(), 
                         userDetails.getUsername(), 
                         roles));
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Username is already taken!"));
    }
    

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
        return ResponseEntity
            .badRequest()
            .body(new MessageResponse("Error: Email is already in use!"));
    }


    User user = new User();
    user.setUsername(signUpRequest.getUsername());
    user.setEmail(signUpRequest.getEmail()); 
    user.setProvider("local"); 
    user.setPassword(encoder.encode(signUpRequest.getPassword()));

    Set<String> strRoles = signUpRequest.getRoles();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      roles.add(Role.USER);
    } else {
      strRoles.forEach(role -> {
        switch (role.toLowerCase()) {
        case "admin":
          roles.add(Role.ADMIN);
          break;
        default:
          roles.add(Role.USER);
        }
      });
    }

    user.setRoles(roles);
    userRepository.save(user);

    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }

  @org.springframework.web.bind.annotation.GetMapping("/me")
  public ResponseEntity<?> getMe() {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
          return ResponseEntity.status(401).body(new MessageResponse("Error: Not authenticated"));
      }

      UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
      List<String> roles = userDetails.getAuthorities().stream()
          .map(item -> item.getAuthority())
          .collect(Collectors.toList());

      return ResponseEntity.ok(new UserInfoResponse(
                           userDetails.getId(), 
                           userDetails.getUsername(), 
                           roles));
  }

  @PostMapping("/signout")
  public ResponseEntity<?> logoutUser(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session != null) {
      session.invalidate();
    }
    SecurityContextHolder.clearContext();
    return ResponseEntity.ok(new MessageResponse("Log out successful"));
  }
}
