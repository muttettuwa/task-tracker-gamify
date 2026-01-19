package com.tasktracker.gamify.entity;

import com.tasktracker.gamify.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * UserInfo entity representing a user in the system
 */
@Entity
@Table(name = "user_info",
       uniqueConstraints = @UniqueConstraint(columnNames = "email"),
       indexes = {
           @Index(name = "idx_user_email", columnList = "email"),
           @Index(name = "idx_user_status", columnList = "status"),
           @Index(name = "idx_user_organization", columnList = "organization_id")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = {"id", "email"})
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Organization is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    @ToString.Exclude
    private Organization organization;

    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Size(max = 100, message = "Middle name must not exceed 100 characters")
    @Column(name = "middle_name", length = 100)
    private String middleName;

    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Past(message = "Birthday must be in the past")
    @Column(name = "birthday")
    private LocalDate birthday;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Telephone must be a valid phone number (10-15 digits)")
    @Column(name = "telephone", length = 20)
    private String telephone;

    @NotBlank(message = "Password hash is required")
    @Column(name = "password_hash", nullable = false, length = 255)
    @ToString.Exclude
    private String passwordHash;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status = UserStatus.PENDING;

    @Column(name = "registered_date", nullable = false, updatable = false)
    private LocalDateTime registeredDate;

    @Column(name = "system_inserted_ts", nullable = false, updatable = false)
    private LocalDateTime systemInsertedTs;

    @Column(name = "system_updated_ts", nullable = false)
    private LocalDateTime systemUpdatedTs;

    @Builder.Default
    @Column(name = "logically_deleted", nullable = false)
    private Boolean logicallyDeleted = false;

    @PrePersist
    protected void onCreate() {
        this.registeredDate = LocalDateTime.now();
        this.systemInsertedTs = LocalDateTime.now();
        this.systemUpdatedTs = LocalDateTime.now();
        if (this.status == null) {
            this.status = UserStatus.PENDING;
        }
        if (this.logicallyDeleted == null) {
            this.logicallyDeleted = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.systemUpdatedTs = LocalDateTime.now();
    }

    /**
     * Constructor for creating new user with essential fields
     */
    public UserInfo(Organization organization, String firstName, String lastName, String email, String passwordHash) {
        this.organization = organization;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.status = UserStatus.PENDING;
        this.logicallyDeleted = false;
    }

    /**
     * Utility method to get full name
     */
    public String getFullName() {
        StringBuilder fullName = new StringBuilder(firstName);
        if (middleName != null && !middleName.isBlank()) {
            fullName.append(" ").append(middleName);
        }
        fullName.append(" ").append(lastName);
        return fullName.toString();
    }
}
