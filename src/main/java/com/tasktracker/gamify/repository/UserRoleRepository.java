package com.tasktracker.gamify.repository;

import com.tasktracker.gamify.entity.Organization;
import com.tasktracker.gamify.entity.SystemRole;
import com.tasktracker.gamify.entity.UserInfo;
import com.tasktracker.gamify.entity.UserRole;
import com.tasktracker.gamify.enums.SystemRoleCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for UserRole entity
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    /**
     * Find all roles for a user
     */
    List<UserRole> findByUserInfoAndLogicallyDeletedFalse(UserInfo userInfo);

    /**
     * Find active roles for a user
     */
    List<UserRole> findByUserInfoAndActiveAndLogicallyDeletedFalse(UserInfo userInfo, Boolean active);

    /**
     * Find roles for a user in specific organization
     */
    List<UserRole> findByUserInfoAndOrganizationAndLogicallyDeletedFalse(
        UserInfo userInfo, Organization organization);

    /**
     * Find active roles for a user in specific organization
     */
    List<UserRole> findByUserInfoAndOrganizationAndActiveAndLogicallyDeletedFalse(
        UserInfo userInfo, Organization organization, Boolean active);

    /**
     * Find specific user role assignment
     */
    Optional<UserRole> findByUserInfoAndOrganizationAndSystemRoleAndLogicallyDeletedFalse(
        UserInfo userInfo, Organization organization, SystemRole systemRole);

    /**
     * Check if user has specific role in organization
     */
    @Query("SELECT CASE WHEN COUNT(ur) > 0 THEN true ELSE false END FROM UserRole ur " +
           "WHERE ur.userInfo = :userInfo AND ur.organization = :organization " +
           "AND ur.systemRole.code = :roleCode AND ur.active = true AND ur.logicallyDeleted = false")
    boolean hasRole(@Param("userInfo") UserInfo userInfo,
                    @Param("organization") Organization organization,
                    @Param("roleCode") SystemRoleCode roleCode);

    /**
     * Find all users with specific role in organization
     */
    @Query("SELECT ur FROM UserRole ur WHERE ur.organization = :organization " +
           "AND ur.systemRole.code = :roleCode AND ur.active = true AND ur.logicallyDeleted = false")
    List<UserRole> findByOrganizationAndRoleCode(@Param("organization") Organization organization,
                                                   @Param("roleCode") SystemRoleCode roleCode);

    /**
     * Count users by role in organization
     */
    @Query("SELECT COUNT(ur) FROM UserRole ur WHERE ur.organization = :organization " +
           "AND ur.systemRole.code = :roleCode AND ur.active = true AND ur.logicallyDeleted = false")
    long countByOrganizationAndRoleCode(@Param("organization") Organization organization,
                                        @Param("roleCode") SystemRoleCode roleCode);
}
