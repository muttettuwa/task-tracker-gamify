package com.tasktracker.gamify.dto;

import lombok.*;

import java.util.List;

/**
 * Login response DTO containing JWT token and user information
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String token;
    private String type = "Bearer";
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> roles;
    private Long organizationId;
    private String organizationName;
}
