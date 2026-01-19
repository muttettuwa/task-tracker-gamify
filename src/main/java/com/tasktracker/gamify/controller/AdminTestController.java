package com.tasktracker.gamify.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Test Controller for Admin Endpoints
 * Demonstrates SUPER_ADMIN only access
 */
@RestController
@RequestMapping("/admin")
public class AdminTestController {

    /**
     * Admin test endpoint - only accessible to SUPER_ADMIN
     */
    @GetMapping("/test")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Map<String, Object> adminTest(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to admin area!");
        response.put("user", authentication.getName());
        response.put("authorities", authentication.getAuthorities());
        response.put("accessLevel", "SUPER_ADMIN only");
        return response;
    }

    /**
     * Admin dashboard endpoint
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Map<String, Object> adminDashboard(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Admin Dashboard");
        response.put("user", authentication.getName());
        response.put("description", "This is a protected admin endpoint");
        return response;
    }
}
