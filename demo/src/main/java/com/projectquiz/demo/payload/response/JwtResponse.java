package com.projectquiz.demo.payload.response;

import java.util.List;

import lombok.Data;

@Data
public class JwtResponse {
  private String token;
  private String type = "Bearer";
  private String id;
  private String username;
  private List<String> roles;

  public JwtResponse(String accessToken, String id, String username, List<String> roles) {
    this.token = accessToken;
    this.id = id;
    this.username = username;
    this.roles = roles;
  }
}
