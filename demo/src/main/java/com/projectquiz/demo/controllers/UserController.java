package com.projectquiz.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectquiz.demo.models.User;
import com.projectquiz.demo.payload.request.UpdateProfileRequest;
import com.projectquiz.demo.security.services.UserDetailsImpl;
import com.projectquiz.demo.services.UserService;

@RestController
@RequestMapping("/api/user")
@org.springframework.web.bind.annotation.CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    UserService userService;

    @PutMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateProfile(@AuthenticationPrincipal UserDetailsImpl userDetails, 
                                           @RequestBody UpdateProfileRequest request) {
        if (request.getUsername() != null && !request.getUsername().trim().isEmpty() && request.getUsername().length() < 3) {
            return ResponseEntity.badRequest().body(new com.projectquiz.demo.payload.response.MessageResponse("Error: Username must be at least 3 characters!"));
        }
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty() && !request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return ResponseEntity.badRequest().body(new com.projectquiz.demo.payload.response.MessageResponse("Error: Invalid email format!"));
        }
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty() && request.getPassword().length() < 6) {
            return ResponseEntity.badRequest().body(new com.projectquiz.demo.payload.response.MessageResponse("Error: Password must be at least 6 characters!"));
        }
        try {
            User updatedUser = userService.updateProfile(userDetails.getId(), request);
            

            updatedUser.setPassword(null); 
            
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
