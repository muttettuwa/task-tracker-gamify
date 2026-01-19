package com.tasktracker.gamify.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

/**
 * JWT Token Provider - generates, validates, and extracts information from JWT tokens
 *
 * SonarQube Optimizations:
 * - Input validation for all public methods
 * - Specific exception handling
 * - Reduced code duplication
 * - Better error messages
 * - No sensitive data in logs
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_ROLES = "roles";

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    /**
     * Generate JWT token for authenticated user
     *
     * @param userId User ID
     * @param email User email
     * @param roles List of user roles
     * @return JWT token string
     * @throws IllegalArgumentException if any parameter is null or invalid
     */
    public String generateToken(Long userId, String email, List<String> roles) {
        validateTokenGenerationParams(userId, email, roles);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        try {
            return Jwts.builder()
                    .subject(userId.toString())
                    .claim(CLAIM_EMAIL, email)
                    .claim(CLAIM_ROLES, roles)
                    .issuedAt(now)
                    .expiration(expiryDate)
                    .signWith(getSigningKey())
                    .compact();
        } catch (Exception e) {
            logger.error("Error generating JWT token", e);
            throw new IllegalStateException("Failed to generate JWT token", e);
        }
    }

    /**
     * Validate token generation parameters
     */
    private void validateTokenGenerationParams(Long userId, String email, List<String> roles) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("Roles cannot be null or empty");
        }
    }

    /**
     * Get signing key from secret
     */
    private SecretKey getSigningKey() {
        if (!StringUtils.hasText(jwtSecret)) {
            throw new IllegalStateException("JWT secret is not configured");
        }

        try {
            byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            logger.error("Error creating signing key", e);
            throw new IllegalStateException("Failed to create JWT signing key", e);
        }
    }

    /**
     * Extract user ID from JWT token
     *
     * @param token JWT token
     * @return User ID
     * @throws IllegalArgumentException if token is invalid
     */
    public Long extractUserId(String token) {
        validateToken(token);

        try {
            Claims claims = extractAllClaims(token);
            return Long.parseLong(claims.getSubject());
        } catch (NumberFormatException e) {
            logger.error("Invalid user ID in JWT token");
            throw new IllegalArgumentException("Invalid user ID in token", e);
        }
    }

    /**
     * Extract email from JWT token
     *
     * @param token JWT token
     * @return User email
     * @throws IllegalArgumentException if token is invalid
     */
    public String extractEmail(String token) {
        validateToken(token);
        Claims claims = extractAllClaims(token);
        return claims.get(CLAIM_EMAIL, String.class);
    }

    /**
     * Extract roles from JWT token
     *
     * @param token JWT token
     * @return List of role strings
     * @throws IllegalArgumentException if token is invalid
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        validateToken(token);
        Claims claims = extractAllClaims(token);
        return claims.get(CLAIM_ROLES, List.class);
    }

    /**
     * Extract all claims from JWT token
     *
     * @param token JWT token
     * @return Claims object
     * @throws IllegalArgumentException if token is invalid
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            logger.error("Failed to extract claims from token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    /**
     * Validate JWT token
     *
     * @param token JWT token to validate
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            logger.debug("JWT token is null or empty");
            return false;
        }

        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;

        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");

        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token structure");

        } catch (ExpiredJwtException ex) {
            logger.debug("Expired JWT token");

        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token type");

        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty");

        } catch (JwtException ex) {
            logger.error("JWT token validation error: {}", ex.getMessage());
        }

        return false;
    }

    /**
     * Check if token is expired
     *
     * @param token JWT token
     * @return true if expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        if (!StringUtils.hasText(token)) {
            return true;
        }

        try {
            Claims claims = extractAllClaims(token);
            Date expiration = claims.getExpiration();
            return expiration != null && expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            logger.debug("Token is expired");
            return true;
        } catch (JwtException e) {
            logger.error("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }

    /**
     * Get token expiration time in milliseconds
     *
     * @return Token expiration time
     */
    public long getExpirationMs() {
        return jwtExpirationMs;
    }
}
