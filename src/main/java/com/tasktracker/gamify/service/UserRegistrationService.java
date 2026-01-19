package com.tasktracker.gamify.service;

import com.tasktracker.gamify.dto.RegisterUserRequest;
import com.tasktracker.gamify.dto.RegisterUserResponse;

/**
 * Service interface for user registration
 */
public interface UserRegistrationService {

    /**
     * Register a new user
     *
     * @param request Registration request with user details
     * @return Registration response with user ID, email, status, and message
     * @throws com.tasktracker.gamify.exception.RegistrationException if registration fails
     */
    RegisterUserResponse registerNewUser(RegisterUserRequest request);
}
