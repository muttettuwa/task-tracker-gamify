package com.tasktracker.gamify.service.impl;

import com.tasktracker.gamify.dto.RegisterUserRequest;
import com.tasktracker.gamify.dto.RegisterUserResponse;
import com.tasktracker.gamify.entity.Organization;
import com.tasktracker.gamify.entity.UserApproval;
import com.tasktracker.gamify.entity.UserInfo;
import com.tasktracker.gamify.enums.ApprovalStatus;
import com.tasktracker.gamify.enums.ApprovalType;
import com.tasktracker.gamify.enums.UserStatus;
import com.tasktracker.gamify.exception.RegistrationException;
import com.tasktracker.gamify.repository.OrganizationRepository;
import com.tasktracker.gamify.repository.UserApprovalRepository;
import com.tasktracker.gamify.repository.UserInfoRepository;
import com.tasktracker.gamify.service.UserRegistrationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * Implementation of user registration service
 *
 * Business Rules:
 * - Passwords must match
 * - Email must be unique
 * - New users start with PENDING status
 * - Creates approval record for admin/supervisor review
 * - Users cannot login until approved
 */
@Service
@RequiredArgsConstructor
public class UserRegistrationServiceImpl implements UserRegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationServiceImpl.class);
    private static final String DEFAULT_ORGANIZATION_CODE = "DEFAULT";

    private final UserInfoRepository userInfoRepository;
    private final UserApprovalRepository userApprovalRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public RegisterUserResponse registerNewUser(RegisterUserRequest request) {
        logger.info("Starting registration process for email: {}", maskEmail(request.getEmail()));

        try {
            // Step 1: Validate passwords match
            validatePasswordsMatch(request);

            // Step 2: Check email uniqueness
            validateEmailUniqueness(request.getEmail());

            // Step 3: Get default organization
            Organization defaultOrganization = getDefaultOrganization();

            // Step 4: Create UserInfo with PENDING status
            UserInfo newUser = createUserInfo(request, defaultOrganization);

            // Step 5: Create approval record
            UserApproval approval = createApprovalRecord(newUser, defaultOrganization);

            logger.info("Successfully registered user with ID: {} - Status: PENDING, Approval ID: {}",
                    newUser.getId(), approval.getId());

            // Step 6: Build and return response
            return buildSuccessResponse(newUser);

        } catch (RegistrationException e) {
            logger.warn("Registration failed for email: {} - Reason: {}",
                    maskEmail(request.getEmail()), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during registration for email: {}",
                    maskEmail(request.getEmail()), e);
            throw new RegistrationException("Registration failed due to system error. Please try again later.", e);
        }
    }

    /**
     * Validate that password and confirmPassword match
     */
    private void validatePasswordsMatch(RegisterUserRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RegistrationException("Password and confirm password do not match", "confirmPassword");
        }
    }

    /**
     * Validate that email is not already registered
     */
    private void validateEmailUniqueness(String email) {
        if (userInfoRepository.existsByEmail(email)) {
            throw new RegistrationException(
                    String.format("Email '%s' is already registered", email),
                    "email"
            );
        }
    }

    /**
     * Get default organization for new users
     */
    private Organization getDefaultOrganization() {
        return organizationRepository.findByCodeAndIsActiveTrue(DEFAULT_ORGANIZATION_CODE)
                .orElseThrow(() -> new RegistrationException(
                        "Default organization not found. Please contact support."
                ));
    }

    /**
     * Create UserInfo entity with PENDING status
     */
    private UserInfo createUserInfo(RegisterUserRequest request, Organization organization) {
        UserInfo userInfo = UserInfo.builder()
                .organization(organization)
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .birthday(request.getBirthday())
                .email(request.getEmail().toLowerCase()) // Normalize email to lowercase
                .telephone(request.getTelephone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .status(UserStatus.PENDING)
                .registeredDate(LocalDateTime.now())
                .logicallyDeleted(false)
                .build();

        return userInfoRepository.save(userInfo);
    }

    /**
     * Create approval record for registration
     */
    private UserApproval createApprovalRecord(UserInfo userInfo, Organization organization) {
        String requestDetails = String.format(
                "New user registration: %s %s (%s)",
                userInfo.getFirstName(),
                userInfo.getLastName(),
                userInfo.getEmail()
        );

        UserApproval approval = UserApproval.builder()
                .userInfo(userInfo)
                .organization(organization)
                .type(ApprovalType.REGISTRATION)
                .status(ApprovalStatus.PENDING)
                .requestDetails(requestDetails)
                .logicallyDeleted(false)
                .build();

        return userApprovalRepository.save(approval);
    }

    /**
     * Build successful registration response
     */
    private RegisterUserResponse buildSuccessResponse(UserInfo userInfo) {
        return RegisterUserResponse.builder()
                .id(userInfo.getId())
                .email(userInfo.getEmail())
                .status(userInfo.getStatus().name())
                .message("Registration successful! Your account is pending approval. " +
                        "You will be notified once your account is approved.")
                .build();
    }

    /**
     * Mask email for logging (security best practice)
     */
    private String maskEmail(String email) {
        if (!StringUtils.hasText(email) || email.length() < 3) {
            return "***";
        }

        String[] parts = email.split("@");
        if (parts.length != 2) {
            return "***";
        }

        String localPart = maskString(parts[0]);
        String domainPart = maskString(parts[1]);

        return localPart + "@" + domainPart;
    }

    /**
     * Mask a string keeping first and last character
     */
    private String maskString(String input) {
        if (input.length() <= 2) {
            return input.charAt(0) + "*";
        }
        return input.charAt(0) + "***" + input.charAt(input.length() - 1);
    }
}
