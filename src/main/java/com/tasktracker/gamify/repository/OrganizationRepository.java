package com.tasktracker.gamify.repository;

import com.tasktracker.gamify.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Organization entity
 */
@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    /**
     * Find organization by code
     */
    Optional<Organization> findByCode(String code);

    /**
     * Find active organization by code
     */
    Optional<Organization> findByCodeAndIsActiveTrue(String code);

    /**
     * Find all active organizations
     */
    List<Organization> findByIsActiveTrue();

    /**
     * Check if organization with code exists
     */
    boolean existsByCode(String code);

    /**
     * Find organizations by name containing (case-insensitive search)
     */
    @Query("SELECT o FROM Organization o WHERE LOWER(o.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Organization> searchByName(String name);
}
