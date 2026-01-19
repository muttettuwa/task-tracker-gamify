package com.tasktracker.gamify.repository;

import com.tasktracker.gamify.entity.SystemRole;
import com.tasktracker.gamify.enums.SystemRoleCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for SystemRole entity
 */
@Repository
public interface SystemRoleRepository extends JpaRepository<SystemRole, Long> {

    /**
     * Find system role by code
     */
    Optional<SystemRole> findByCode(SystemRoleCode code);

    /**
     * Check if system role with code exists
     */
    boolean existsByCode(SystemRoleCode code);
}
