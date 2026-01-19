package com.tasktracker.gamify.exception;

/**
 * Exception thrown when user registration fails due to business logic validation
 */
public class RegistrationException extends RuntimeException {

    private final String field;

    public RegistrationException(String message) {
        super(message);
        this.field = null;
    }

    public RegistrationException(String message, String field) {
        super(message);
        this.field = field;
    }

    public RegistrationException(String message, Throwable cause) {
        super(message, cause);
        this.field = null;
    }

    public String getField() {
        return field;
    }
}
