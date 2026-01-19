package com.tasktracker.gamify.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Test Controller for User Endpoints
 * Demonstrates role-based access for USER, SUPERVISOR, SUPER_ADMIN
 */
@RestController
@RequestMapping("/user")
public class UserTestController {

    /**
     * User test endpoint - accessible by USER, SUPERVISOR, SUPER_ADMIN
     */
    @GetMapping("/test")
    @PreAuthorize("hasAnyRole('USER', 'SUPERVISOR', 'SUPER_ADMIN')")
    public Map<String, Object> userTest(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to user area!");
        response.put("user", authentication.getName());
        response.put("authorities", authentication.getAuthorities());
        response.put("accessLevel", "USER, SUPERVISOR, or SUPER_ADMIN");
        return response;
    }

    /**
     * User profile endpoint
     */
    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('USER', 'SUPERVISOR', 'SUPER_ADMIN')")
    public Map<String, Object> userProfile(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User Profile");
        response.put("user", authentication.getName());
        response.put("description", "This endpoint is accessible by regular users");
        return response;
    }
}
