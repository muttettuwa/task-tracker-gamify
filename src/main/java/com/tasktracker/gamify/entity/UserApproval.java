package com.tasktracker.gamify.entity;

import com.tasktracker.gamify.enums.ApprovalStatus;
import com.tasktracker.gamify.enums.ApprovalType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity representing approval workflows for user actions
 * Tracks registration approvals, role changes, and other administrative approvals
 */
@Entity
@Table(name = "user_approval")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_info_id", nullable = false)
    private UserInfo userInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ApprovalType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApprovalStatus status;

    @Column(length = 500)
    private String requestDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private UserInfo approvedBy;

    @Column
    private LocalDateTime approvedAt;

    @Column(length = 500)
    private String approvalNotes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime systemInsertedTs;

    @Column(nullable = false)
    private LocalDateTime systemUpdatedTs;

    @Column(nullable = false)
    private Boolean logicallyDeleted = false;

    @PrePersist
    protected void onCreate() {
        systemInsertedTs = LocalDateTime.now();
        systemUpdatedTs = LocalDateTime.now();
        if (logicallyDeleted == null) {
            logicallyDeleted = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        systemUpdatedTs = LocalDateTime.now();
    }
}
