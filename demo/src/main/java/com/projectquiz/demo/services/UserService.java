package com.projectquiz.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.projectquiz.demo.models.User;
import com.projectquiz.demo.payload.request.UpdateProfileRequest;
import com.projectquiz.demo.repositories.UserRepository;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    public User updateProfile(String userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));


        if (request.getUsername() != null && !request.getUsername().isEmpty()) {

            userRepository.findByUsername(request.getUsername())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(userId)) {
                            throw new RuntimeException("Username already taken");
                        }
                    });
            user.setUsername(request.getUsername());
        }


        if (request.getEmail() != null && !request.getEmail().isEmpty()) {





             






             if (!request.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
                 throw new RuntimeException("Email already in use");
             }
             user.setEmail(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(encoder.encode(request.getPassword()));
        }

        return userRepository.save(user);
    }
}
