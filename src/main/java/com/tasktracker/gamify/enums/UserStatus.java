package com.tasktracker.gamify.enums;

/**
 * User account status enumeration
 */
public enum UserStatus {
    PENDING("Pending approval"),
    APPROVED("Approved and active"),
    REJECTED("Rejected"),
    LOCKED("Account locked");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
