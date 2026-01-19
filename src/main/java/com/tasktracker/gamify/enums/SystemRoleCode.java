package com.tasktracker.gamify.enums;

/**
 * System role codes enumeration
 */
public enum SystemRoleCode {
    SUPER_ADMIN("Super Administrator - Full system access"),
    SUPERVISOR("Supervisor - Manage organization users and tasks"),
    USER("Regular User - Standard task management"),
    GUEST("Guest - Read-only access");

    private final String description;

    SystemRoleCode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
