package com.tasktracker.gamify.repository;

import com.tasktracker.gamify.entity.Organization;
import com.tasktracker.gamify.entity.UserInfo;
import com.tasktracker.gamify.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for UserInfo entity
 */
@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

    /**
     * Find user by email
     */
    Optional<UserInfo> findByEmail(String email);

    /**
     * Find user by ID with organization (eager fetch to avoid lazy loading issues)
     */
    @Query("SELECT u FROM UserInfo u LEFT JOIN FETCH u.organization WHERE u.id = :id")
    Optional<UserInfo> findByIdWithOrganization(@Param("id") Long id);

    /**
     * Find user by email (case-insensitive)
     */
    Optional<UserInfo> findByEmailIgnoreCase(String email);

    /**
     * Check if user with email exists
     */
    boolean existsByEmail(String email);

    /**
     * Find all non-deleted users
     */
    List<UserInfo> findByLogicallyDeletedFalse();

    /**
     * Find users by organization
     */
    List<UserInfo> findByOrganizationAndLogicallyDeletedFalse(Organization organization);

    /**
     * Find users by status
     */
    List<UserInfo> findByStatusAndLogicallyDeletedFalse(UserStatus status);

    /**
     * Find users by organization and status
     */
    List<UserInfo> findByOrganizationAndStatusAndLogicallyDeletedFalse(
        Organization organization, UserStatus status);

    /**
     * Search users by name (first name or last name)
     */
    @Query("SELECT u FROM UserInfo u WHERE u.logicallyDeleted = false AND " +
           "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<UserInfo> searchUsers(@Param("searchTerm") String searchTerm);

    /**
     * Count active users in organization
     */
    @Query("SELECT COUNT(u) FROM UserInfo u WHERE u.organization = :organization " +
           "AND u.status = 'APPROVED' AND u.logicallyDeleted = false")
    long countActiveUsersByOrganization(@Param("organization") Organization organization);
}
