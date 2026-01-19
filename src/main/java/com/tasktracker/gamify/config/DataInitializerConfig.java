package com.tasktracker.gamify.config;

import com.tasktracker.gamify.entity.Organization;
import com.tasktracker.gamify.entity.SystemRole;
import com.tasktracker.gamify.entity.UserInfo;
import com.tasktracker.gamify.entity.UserRole;
import com.tasktracker.gamify.enums.SystemRoleCode;
import com.tasktracker.gamify.enums.UserStatus;
import com.tasktracker.gamify.repository.OrganizationRepository;
import com.tasktracker.gamify.repository.SystemRoleRepository;
import com.tasktracker.gamify.repository.UserInfoRepository;
import com.tasktracker.gamify.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

/**
 * Data initialization configuration
 * Creates default organization, roles, and super admin user on startup
 */
@Configuration
@RequiredArgsConstructor
public class DataInitializerConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializerConfig.class);

    private final PasswordEncoder passwordEncoder;

    @Bean
    @Transactional
    public CommandLineRunner initializeData(
            OrganizationRepository organizationRepository,
            SystemRoleRepository systemRoleRepository,
            UserInfoRepository userInfoRepository,
            UserRoleRepository userRoleRepository) {

        return args -> {
            logger.info("Starting data initialization...");

            // 1. Create default organization if not exists
            Organization defaultOrg = organizationRepository.findByCode("DEFAULT_ORG")
                    .orElseGet(() -> {
                        logger.info("Creating default organization...");
                        Organization org = new Organization("Default Organization", "DEFAULT_ORG");
                        return organizationRepository.save(org);
                    });
            logger.info("Default organization: {}", defaultOrg);

            // 2. Verify system roles exist (they are created by Liquibase)
            SystemRole superAdminRole = systemRoleRepository.findByCode(SystemRoleCode.SUPER_ADMIN)
                    .orElseThrow(() -> new RuntimeException("SUPER_ADMIN role not found"));
            SystemRole supervisorRole = systemRoleRepository.findByCode(SystemRoleCode.SUPERVISOR)
                    .orElseThrow(() -> new RuntimeException("SUPERVISOR role not found"));
            SystemRole userRole = systemRoleRepository.findByCode(SystemRoleCode.USER)
                    .orElseThrow(() -> new RuntimeException("USER role not found"));
            SystemRole guestRole = systemRoleRepository.findByCode(SystemRoleCode.GUEST)
                    .orElseThrow(() -> new RuntimeException("GUEST role not found"));

            logger.info("System roles verified: SUPER_ADMIN, SUPERVISOR, USER, GUEST");

            // 3. Create default Super Admin user if not exists
            String superAdminEmail = "admin@tasktracker.com";
            String defaultPassword = "admin123";

            UserInfo superAdmin = userInfoRepository.findByEmail(superAdminEmail)
                    .orElseGet(() -> {
                        logger.info("Creating default Super Admin user...");
                        UserInfo admin = new UserInfo();
                        admin.setOrganization(defaultOrg);
                        admin.setFirstName("Super");
                        admin.setLastName("Admin");
                        admin.setEmail(superAdminEmail);
                        admin.setPasswordHash(passwordEncoder.encode(defaultPassword));
                        admin.setStatus(UserStatus.APPROVED);
                        admin.setTelephone("+1234567890");
                        return userInfoRepository.save(admin);
                    });
            logger.info("Default Super Admin user: {} ({})", superAdmin.getFullName(), superAdmin.getEmail());

            // 4. Assign SUPER_ADMIN role to the default admin user
            boolean hasRole = userRoleRepository.hasRole(superAdmin, defaultOrg, SystemRoleCode.SUPER_ADMIN);
            if (!hasRole) {
                logger.info("Assigning SUPER_ADMIN role to default admin user...");
                UserRole adminUserRole = new UserRole(superAdmin, defaultOrg, superAdminRole);
                userRoleRepository.save(adminUserRole);
                logger.info("SUPER_ADMIN role assigned successfully");
            } else {
                logger.info("Super Admin already has SUPER_ADMIN role");
            }

            // 5. Create a sample regular user (optional - for testing)
            String sampleUserEmail = "user@tasktracker.com";
            String sampleUserPassword = "user123";

            UserInfo sampleUser = userInfoRepository.findByEmail(sampleUserEmail)
                    .orElseGet(() -> {
                        logger.info("Creating sample regular user...");
                        UserInfo user = new UserInfo();
                        user.setOrganization(defaultOrg);
                        user.setFirstName("John");
                        user.setMiddleName("A.");
                        user.setLastName("Doe");
                        user.setEmail(sampleUserEmail);
                        user.setPasswordHash(passwordEncoder.encode(sampleUserPassword));
                        user.setStatus(UserStatus.APPROVED);
                        user.setTelephone("+1987654321");
                        return userInfoRepository.save(user);
                    });
            logger.info("Sample user: {} ({})", sampleUser.getFullName(), sampleUser.getEmail());

            // 6. Assign USER role to sample user
            boolean sampleUserHasRole = userRoleRepository.hasRole(sampleUser, defaultOrg, SystemRoleCode.USER);
            if (!sampleUserHasRole) {
                logger.info("Assigning USER role to sample user...");
                UserRole sampleUserRole = new UserRole(sampleUser, defaultOrg, userRole);
                userRoleRepository.save(sampleUserRole);
                logger.info("USER role assigned successfully");
            } else {
                logger.info("Sample user already has USER role");
            }

            // Log summary
            logger.info("=================================================");
            logger.info("Data Initialization Complete!");
            logger.info("=================================================");
            logger.info("Organizations: {}", organizationRepository.count());
            logger.info("System Roles: {}", systemRoleRepository.count());
            logger.info("Users: {}", userInfoRepository.count());
            logger.info("User Role Assignments: {}", userRoleRepository.count());
            logger.info("=================================================");
            logger.info("Default Test Credentials (BCrypt encoded):");
            logger.info("  Super Admin: {} / password: {}", superAdminEmail, defaultPassword);
            logger.info("  Regular User: {} / password: {}", sampleUserEmail, sampleUserPassword);
            logger.info("  NOTE: Change these credentials in production!");
            logger.info("=================================================");
        };
    }
}
