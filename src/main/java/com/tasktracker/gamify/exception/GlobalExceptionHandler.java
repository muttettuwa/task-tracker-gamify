package com.tasktracker.gamify.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Global Exception Handler for REST API
 * Handles all exceptions and returns standardized error responses
 *
 * SonarQube Optimizations:
 * - Specific exception handlers instead of generic catch-all
 * - Structured error responses with correlation IDs
 * - Proper logging levels
 * - Security-conscious error messages (no sensitive data exposure)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String ERROR_ID_KEY = "errorId";
    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String STATUS_KEY = "status";
    private static final String ERROR_KEY = "error";
    private static final String MESSAGE_KEY = "message";
    private static final String ERRORS_KEY = "errors";
    private static final String PATH_KEY = "path";

    /**
     * Handle Bean Validation errors (@Valid, @NotBlank, @Email, etc.)
     * Returns 400 Bad Request with validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {

        String errorId = generateErrorId();
        Map<String, Object> response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                "One or more fields have validation errors",
                request,
                errorId
        );

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        response.put(ERRORS_KEY, fieldErrors);

        logger.warn("Validation errors [errorId={}]: {}", errorId, fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle authentication exceptions
     */
    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(
            Exception ex, WebRequest request) {

        String errorId = generateErrorId();
        Map<String, Object> response = buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Authentication Failed",
                "Invalid credentials or authentication token",
                request,
                errorId
        );

        logger.warn("Authentication failure [errorId={}]: {}", errorId, ex.getClass().getSimpleName());
        // Don't log the exception details to avoid exposing sensitive information

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Handle access denied exceptions
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {

        String errorId = generateErrorId();
        Map<String, Object> response = buildErrorResponse(
                HttpStatus.FORBIDDEN,
                "Access Denied",
                "You do not have permission to access this resource",
                request,
                errorId
        );

        logger.warn("Access denied [errorId={}]: {}", errorId, ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * Handle resource not found exceptions
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {

        String errorId = generateErrorId();
        Map<String, Object> response = buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "Resource Not Found",
                ex.getMessage(),
                request,
                errorId
        );

        logger.warn("Resource not found [errorId={}]: {} with {}: {}",
                errorId, ex.getResourceName(), ex.getFieldName(), ex.getFieldValue());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handle registration exceptions
     */
    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<Map<String, Object>> handleRegistrationException(
            RegistrationException ex, WebRequest request) {

        String errorId = generateErrorId();
        Map<String, Object> response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Registration Failed",
                ex.getMessage(),
                request,
                errorId
        );

        // If specific field is mentioned, add it to errors
        if (ex.getField() != null) {
            Map<String, String> fieldErrors = new HashMap<>();
            fieldErrors.put(ex.getField(), ex.getMessage());
            response.put(ERRORS_KEY, fieldErrors);
        }

        logger.warn("Registration failure [errorId={}]: {}", errorId, ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle authentication failure exceptions
     */
    @ExceptionHandler(AuthenticationFailureException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationFailureException(
            AuthenticationFailureException ex, WebRequest request) {

        String errorId = generateErrorId();
        Map<String, Object> response = buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Authentication Failed",
                "Invalid email or password",
                request,
                errorId
        );

        logger.warn("Authentication failure [errorId={}]: {}", errorId, ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        String errorId = generateErrorId();
        Map<String, Object> response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid Request",
                "The request contains invalid parameters",
                request,
                errorId
        );

        logger.warn("Invalid argument [errorId={}]: {}", errorId, ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle all other unhandled exceptions
     * SonarQube: This is intentionally broad as a safety net
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex, WebRequest request) {

        String errorId = generateErrorId();
        Map<String, Object> response = buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred. Please contact support with error ID: " + errorId,
                request,
                errorId
        );

        // Log full stack trace for debugging but don't expose to client
        logger.error("Unhandled exception [errorId={}]", errorId, ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Build standardized error response
     */
    private Map<String, Object> buildErrorResponse(
            HttpStatus status,
            String error,
            String message,
            WebRequest request,
            String errorId) {

        Map<String, Object> response = new HashMap<>();
        response.put(ERROR_ID_KEY, errorId);
        response.put(TIMESTAMP_KEY, LocalDateTime.now().toString());
        response.put(STATUS_KEY, status.value());
        response.put(ERROR_KEY, error);
        response.put(MESSAGE_KEY, message);
        response.put(PATH_KEY, extractPath(request));

        return response;
    }

    /**
     * Extract request path from WebRequest
     */
    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

    /**
     * Generate unique error ID for correlation
     */
    private String generateErrorId() {
        return UUID.randomUUID().toString();
    }
}
