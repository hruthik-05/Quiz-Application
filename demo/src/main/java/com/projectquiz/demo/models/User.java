package com.projectquiz.demo.models;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;
    @org.springframework.data.mongodb.core.index.Indexed(unique = true)
    private String username;
    @org.springframework.data.mongodb.core.index.Indexed(unique = true)
    private String email;
    private String password;
    private String provider; 
    private String providerId;
    private Set<Role> roles;

}
