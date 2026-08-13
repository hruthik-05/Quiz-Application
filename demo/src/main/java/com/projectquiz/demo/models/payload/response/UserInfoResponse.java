package com.projectquiz.demo.payload.response;

import java.util.List;
import lombok.Data;

@Data
public class UserInfoResponse {
  private String id;
  private String username;
  private List<String> roles;

  public UserInfoResponse(String id, String username, List<String> roles) {
    this.id = id;
    this.username = username;
    this.roles = roles;
  }
}
