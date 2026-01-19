package com.tasktracker.gamify.config;

import com.tasktracker.gamify.security.CustomUserDetailsService;
import com.tasktracker.gamify.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security Configuration with JWT Authentication
 * Uses Spring Security 6+ (no WebSecurityConfigurerAdapter)
 *
 * SonarQube Optimizations:
 * - BCryptPasswordEncoder with explicit strength (non-deprecated)
 * - Constants for endpoint paths
 * - Comprehensive security configuration
 * - Method-level security enabled
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    // BCrypt strength constant (10-12 is recommended for production)
    private static final int BCRYPT_STRENGTH = 10;

    // Endpoint path constants
    private static final String AUTH_ENDPOINTS = "/auth/**";
    private static final String ACTUATOR_HEALTH = "/actuator/health";
    private static final String ACTUATOR_INFO = "/actuator/info";
    private static final String ACTUATOR_ENDPOINTS = "/actuator/**";
    private static final String ADMIN_ENDPOINTS = "/admin/**";
    private static final String USER_ENDPOINTS = "/user/**";
    private static final String SUPERVISOR_ENDPOINTS = "/supervisor/**";

    // Role constants
    private static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";
    private static final String ROLE_SUPERVISOR = "SUPERVISOR";
    private static final String ROLE_USER = "USER";

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Password encoder bean using BCrypt with explicit strength
     * Strength 10 provides good balance between security and performance
     *
     * @return BCryptPasswordEncoder with strength 10
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCRYPT_STRENGTH);
    }

    /**
     * Authentication manager bean
     * Spring Security 6+ will auto-configure DaoAuthenticationProvider
     * when UserDetailsService and PasswordEncoder beans are present
     *
     * @param config AuthenticationConfiguration
     * @return AuthenticationManager
     * @throws Exception if authentication manager cannot be created
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Security filter chain with JWT authentication
     * Configures:
     * - CSRF disabled (stateless JWT authentication)
     * - Stateless session management
     * - Public endpoints (no authentication)
     * - Role-based access control
     * - JWT authentication filter
     *
     * Note: DaoAuthenticationProvider is auto-configured by Spring Security
     * when UserDetailsService and PasswordEncoder beans are present
     *
     * @param http HttpSecurity configuration
     * @return Configured SecurityFilterChain
     * @throws Exception if security filter chain cannot be configured
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize -> authorize
                        // Public endpoints - no authentication required
                        .requestMatchers(AUTH_ENDPOINTS).permitAll()
                        .requestMatchers(ACTUATOR_HEALTH, ACTUATOR_INFO).permitAll()

                        // Admin endpoints - only SUPER_ADMIN
                        .requestMatchers(ADMIN_ENDPOINTS).hasRole(ROLE_SUPER_ADMIN)

                        // User endpoints - accessible by USER, SUPERVISOR, SUPER_ADMIN
                        .requestMatchers(USER_ENDPOINTS).hasAnyRole(ROLE_USER, ROLE_SUPERVISOR, ROLE_SUPER_ADMIN)

                        // Supervisor endpoints - accessible by SUPERVISOR, SUPER_ADMIN
                        .requestMatchers(SUPERVISOR_ENDPOINTS).hasAnyRole(ROLE_SUPERVISOR, ROLE_SUPER_ADMIN)

                        // All other actuator endpoints require authentication
                        .requestMatchers(ACTUATOR_ENDPOINTS).authenticated()

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
