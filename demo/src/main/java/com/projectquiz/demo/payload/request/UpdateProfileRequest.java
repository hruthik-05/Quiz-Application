package com.projectquiz.demo.payload.request;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String username;
    private String email;
    private String password; 
}
