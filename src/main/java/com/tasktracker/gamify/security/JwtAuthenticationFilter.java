package com.tasktracker.gamify.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter - intercepts requests and validates JWT tokens
 *
 * SonarQube Optimizations:
 * - Added constants for magic strings
 * - Improved exception handling with specific catch blocks
 * - Better logging with structured messages
 * - Input validation
 * - NonNull annotations for parameters
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = 7;
    private static final String PUBLIC_AUTH_PATH = "/api/auth/";
    private static final String PUBLIC_HEALTH_PATH = "/api/actuator/health";
    private static final String PUBLIC_INFO_PATH = "/api/actuator/info";

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Filter incoming requests and validate JWT tokens
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = extractJwtFromRequest(request);

            if (StringUtils.hasText(jwt)) {
                processJwtToken(jwt, request);
            }

        } catch (IllegalArgumentException e) {
            logger.warn("Invalid JWT token: {}", e.getMessage());

        } catch (UsernameNotFoundException e) {
            logger.warn("User not found from JWT token: {}", e.getMessage());

        } catch (Exception ex) {
            logger.error("Unexpected error during JWT authentication", ex);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Process and validate JWT token
     */
    private void processJwtToken(String jwt, HttpServletRequest request) {
        if (!tokenProvider.validateToken(jwt)) {
            logger.debug("JWT token validation failed");
            return;
        }

        Long userId = tokenProvider.extractUserId(jwt);
        if (userId == null) {
            logger.warn("Could not extract user ID from JWT token");
            return;
        }

        UserDetails userDetails = customUserDetailsService.loadUserById(userId);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        logger.debug("Successfully authenticated user from JWT token");
    }

    /**
     * Skip JWT filter for public endpoints
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith(PUBLIC_AUTH_PATH) ||
               path.startsWith(PUBLIC_HEALTH_PATH) ||
               path.startsWith(PUBLIC_INFO_PATH);
    }

    /**
     * Extract JWT token from Authorization header
     *
     * @param request HTTP request
     * @return JWT token or null
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX_LENGTH);
        }

        return null;
    }
}
