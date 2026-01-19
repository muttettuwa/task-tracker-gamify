package com.tasktracker.gamify.repository;

import com.tasktracker.gamify.entity.UserApproval;
import com.tasktracker.gamify.entity.UserInfo;
import com.tasktracker.gamify.enums.ApprovalStatus;
import com.tasktracker.gamify.enums.ApprovalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for UserApproval entity
 */
@Repository
public interface UserApprovalRepository extends JpaRepository<UserApproval, Long> {

    /**
     * Find approval by user and type
     */
    Optional<UserApproval> findByUserInfoAndTypeAndLogicallyDeletedFalse(UserInfo userInfo, ApprovalType type);

    /**
     * Find all pending approvals
     */
    List<UserApproval> findByStatusAndLogicallyDeletedFalse(ApprovalStatus status);

    /**
     * Find pending approvals by type
     */
    List<UserApproval> findByTypeAndStatusAndLogicallyDeletedFalse(ApprovalType type, ApprovalStatus status);

    /**
     * Find approvals for a specific user
     */
    List<UserApproval> findByUserInfoAndLogicallyDeletedFalse(UserInfo userInfo);
}
