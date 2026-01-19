package com.tasktracker.gamify.controller;

import com.tasktracker.gamify.dto.RegisterUserRequest;
import com.tasktracker.gamify.dto.RegisterUserResponse;
import com.tasktracker.gamify.service.UserRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Registration Controller
 * Handles user registration endpoints
 *
 * Endpoints:
 * - POST /api/auth/register - Register new user
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class RegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    private final UserRegistrationService userRegistrationService;

    /**
     * Register a new user
     *
     * @param request Registration request with user details
     * @return Registration response with user ID, email, status, and message
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        logger.info("Registration request received for email");

        RegisterUserResponse response = userRegistrationService.registerNewUser(request);

        logger.info("Registration successful - User ID: {}, Status: {}", response.getId(), response.getStatus());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
