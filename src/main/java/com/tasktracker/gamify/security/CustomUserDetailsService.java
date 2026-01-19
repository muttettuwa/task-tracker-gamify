package com.tasktracker.gamify.security;

import com.tasktracker.gamify.entity.UserInfo;
import com.tasktracker.gamify.entity.UserRole;
import com.tasktracker.gamify.repository.UserInfoRepository;
import com.tasktracker.gamify.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Custom UserDetailsService implementation for loading user-specific data
 *
 * SonarQube Optimizations:
 * - Added logging for security events
 * - Improved exception messages
 * - Transactional read-only for performance
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserInfoRepository userInfoRepository;
    private final UserRoleRepository userRoleRepository;

    /**
     * Load user by email (username)
     *
     * @param email User email
     * @return UserDetails object
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Loading user by email");

        // Find user by email
        UserInfo userInfo = userInfoRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found with email");
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        // Get user roles
        List<UserRole> userRoles = userRoleRepository.findByUserInfoAndLogicallyDeletedFalse(userInfo);

        if (userRoles.isEmpty()) {
            logger.warn("User found but has no roles assigned - userId: {}", userInfo.getId());
        }

        logger.debug("Successfully loaded user with {} roles", userRoles.size());

        // Return custom UserDetails
        return new CustomUserDetails(userInfo, userRoles);
    }

    /**
     * Load user by ID (useful for JWT authentication)
     *
     * @param id User ID
     * @return UserDetails object
     * @throws UsernameNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        logger.debug("Loading user by ID");

        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        UserInfo userInfo = userInfoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID");
                    return new UsernameNotFoundException("User not found with id: " + id);
                });

        List<UserRole> userRoles = userRoleRepository.findByUserInfoAndLogicallyDeletedFalse(userInfo);

        if (userRoles.isEmpty()) {
            logger.warn("User found but has no roles assigned - userId: {}", id);
        }

        logger.debug("Successfully loaded user with {} roles", userRoles.size());

        return new CustomUserDetails(userInfo, userRoles);
    }
}
