package com.tasktracker.gamify.controller;

import com.tasktracker.gamify.dto.LoginRequest;
import com.tasktracker.gamify.dto.LoginResponse;
import com.tasktracker.gamify.entity.UserInfo;
import com.tasktracker.gamify.exception.AuthenticationFailureException;
import com.tasktracker.gamify.exception.ResourceNotFoundException;
import com.tasktracker.gamify.repository.UserInfoRepository;
import com.tasktracker.gamify.security.CustomUserDetails;
import com.tasktracker.gamify.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Authentication Controller
 * Handles user authentication and JWT token generation
 *
 * SonarQube Optimizations:
 * - Specific exception handling instead of generic catch
 * - Removed unused variables
 * - Improved logging with structured messages
 * - Security-conscious error messages (no information disclosure)
 * - Constants for magic strings
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private static final String ROLE_PREFIX = "ROLE_";
    private static final String BEARER_TYPE = "Bearer";

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserInfoRepository userInfoRepository;

    /**
     * Login endpoint - authenticates user and returns JWT token
     *
     * @param loginRequest Login credentials (email and password)
     * @return JWT token and user information
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {

        String userEmail = loginRequest.getEmail();

        try {
            // Authenticate user
            Authentication authentication = authenticateUser(userEmail, loginRequest.getPassword());

            // Get authenticated user details
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            // Load user info with organization
            UserInfo userInfo = loadUserInfo(userDetails.getId());

            // Extract role names
            List<String> roles = extractRoles(authentication);

            // Generate JWT token
            String token = tokenProvider.generateToken(
                    userDetails.getId(),
                    userDetails.getEmail(),
                    roles
            );

            // Build and return response
            LoginResponse response = buildLoginResponse(token, userInfo, roles);

            logger.info("Successful login for user: {}", maskEmail(userEmail));

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            logger.warn("Failed login attempt - invalid credentials for user: {}", maskEmail(userEmail));
            throw new AuthenticationFailureException("Invalid email or password", e);

        } catch (UsernameNotFoundException e) {
            logger.warn("Failed login attempt - user not found: {}", maskEmail(userEmail));
            throw new AuthenticationFailureException("Invalid email or password", e);

        } catch (InternalAuthenticationServiceException e) {
            logger.warn("Failed login attempt - authentication service error for user: {}", maskEmail(userEmail));
            throw new AuthenticationFailureException("Invalid email or password", e);

        } catch (AuthenticationException e) {
            logger.warn("Failed login attempt - authentication error for user: {}", maskEmail(userEmail));
            throw new AuthenticationFailureException("Invalid email or password", e);
        }
    }

    /**
     * Authenticate user with credentials
     */
    private Authentication authenticateUser(String email, String password) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
    }

    /**
     * Load user info with organization from database
     */
    private UserInfo loadUserInfo(Long userId) {
        return userInfoRepository.findByIdWithOrganization(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    /**
     * Extract role names from authentication
     */
    private List<String> extractRoles(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace(ROLE_PREFIX, ""))
                .collect(Collectors.toList());
    }

    /**
     * Build login response with user information
     */
    private LoginResponse buildLoginResponse(String token, UserInfo userInfo, List<String> roles) {
        return LoginResponse.builder()
                .token(token)
                .type(BEARER_TYPE)
                .userId(userInfo.getId())
                .email(userInfo.getEmail())
                .firstName(userInfo.getFirstName())
                .lastName(userInfo.getLastName())
                .roles(roles)
                .organizationId(userInfo.getOrganization().getId())
                .organizationName(userInfo.getOrganization().getName())
                .build();
    }

    /**
     * Mask email for logging (security best practice)
     * Example: john.doe@example.com becomes j***e@e*****e.com
     */
    private String maskEmail(String email) {
        if (email == null || email.length() < 3) {
            return "***";
        }

        String[] parts = email.split("@");
        if (parts.length != 2) {
            return "***";
        }

        String localPart = maskString(parts[0]);
        String domainPart = maskString(parts[1]);

        return localPart + "@" + domainPart;
    }

    /**
     * Mask a string keeping first and last character
     */
    private String maskString(String input) {
        if (input.length() <= 2) {
            return input.charAt(0) + "*";
        }
        return input.charAt(0) + "***" + input.charAt(input.length() - 1);
    }
}
