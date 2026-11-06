package com.alphasoft.studyhub.identity.security;

import com.alphasoft.studyhub.config.StudyHubProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Issues and validates JWT access tokens using JJWT 0.12.6.
 *
 * Security constraints (DR-009):
 *  - Tokens signed with HS256; secret from StudyHubProperties (env-overridable).
 *  - Token content is NEVER logged — only token IDs (jti) are logged on validation failure.
 *  - Access tokens are short-lived (default 15 minutes); expiry configurable.
 *  - Throws JwtValidationException for all failure modes — callers never see JJWT internals.
 */
@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private static final String CLAIM_EMAIL  = "email";
    private static final String CLAIM_ROLES  = "roles";

    private final SecretKey    accessKey;
    private final long         accessExpiryMs;

    public JwtService(StudyHubProperties props) {
        StudyHubProperties.Jwt jwt = props.jwt();
        this.accessKey      = Keys.hmacShaKeyFor(
                jwt.accessSecret().getBytes(StandardCharsets.UTF_8));
        this.accessExpiryMs = jwt.accessExpiryMs();
    }

    // ── Token issuance ──────────────────────────────────────────────────────

    /**
     * Issues a signed access token for the given user.
     *
     * @param userId      the user's database ID (sub claim)
     * @param email       the user's email address (email claim)
     * @param roles       the user's role names, e.g. ["ROLE_LEARNER"] (roles claim)
     * @return            a compact, signed JWT string
     */
    public String issueAccessToken(Long userId, String email, List<String> roles) {
        Instant now     = Instant.now();
        Instant expires = now.plusMillis(accessExpiryMs);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claims(Map.of(
                    CLAIM_EMAIL, email,
                    CLAIM_ROLES, roles
                ))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expires))
                .signWith(accessKey)
                .compact();
    }

    // ── Token validation ────────────────────────────────────────────────────

    /**
     * Validates and parses a JWT access token.
     *
     * @param token the compact JWT string from the Authorization header
     * @return      parsed claims
     * @throws JwtValidationException if the token is expired, malformed, or has an invalid signature
     */
    public Claims validateAndParse(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(accessKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException ex) {
            // Log only that it expired, never the token itself
            log.debug("JWT access token expired");
            throw new JwtValidationException("Token has expired", ex);
        } catch (JwtException ex) {
            log.debug("JWT access token invalid: {}", ex.getMessage());
            throw new JwtValidationException("Token is invalid", ex);
        }
    }

    // ── Claim extraction helpers ────────────────────────────────────────────

    /**
     * Extracts the user ID (subject claim) from already-validated claims.
     * Caller must have called {@link #validateAndParse(String)} first.
     */
    public Long extractUserId(Claims claims) {
        return Long.parseLong(claims.getSubject());
    }

    /**
     * Extracts the email claim from already-validated claims.
     */
    public String extractEmail(Claims claims) {
        return claims.get(CLAIM_EMAIL, String.class);
    }

    /**
     * Extracts the roles list from already-validated claims.
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(Claims claims) {
        Object raw = claims.get(CLAIM_ROLES);
        if (raw instanceof List<?>) {
            return (List<String>) raw;
        }
        return List.of();
    }
}
