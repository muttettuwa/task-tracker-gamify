package com.tasktracker.gamify.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * UserRole entity representing a user's role assignment in an organization
 */
@Entity
@Table(name = "user_roles",
       indexes = {
           @Index(name = "idx_user_role_user", columnList = "user_info_id"),
           @Index(name = "idx_user_role_organization", columnList = "organization_id"),
           @Index(name = "idx_user_role_system_role", columnList = "system_role_id"),
           @Index(name = "idx_user_role_active", columnList = "active")
       },
       uniqueConstraints = @UniqueConstraint(
           columnNames = {"user_info_id", "organization_id", "system_role_id"}
       ))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_info_id", nullable = false)
    @ToString.Exclude
    private UserInfo userInfo;

    @NotNull(message = "Organization is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    @ToString.Exclude
    private Organization organization;

    @NotNull(message = "System role is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "system_role_id", nullable = false)
    @ToString.Exclude
    private SystemRole systemRole;

    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "system_inserted_ts", nullable = false, updatable = false)
    private LocalDateTime systemInsertedTs;

    @Column(name = "system_updated_ts", nullable = false)
    private LocalDateTime systemUpdatedTs;

    @Builder.Default
    @Column(name = "logically_deleted", nullable = false)
    private Boolean logicallyDeleted = false;

    @PrePersist
    protected void onCreate() {
        this.systemInsertedTs = LocalDateTime.now();
        this.systemUpdatedTs = LocalDateTime.now();
        if (this.active == null) {
            this.active = true;
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
     * Constructor for creating new user role assignment
     */
    public UserRole(UserInfo userInfo, Organization organization, SystemRole systemRole) {
        this.userInfo = userInfo;
        this.organization = organization;
        this.systemRole = systemRole;
        this.active = true;
        this.logicallyDeleted = false;
    }
}
