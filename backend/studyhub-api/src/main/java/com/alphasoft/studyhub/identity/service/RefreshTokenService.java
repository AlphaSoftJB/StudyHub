package com.alphasoft.studyhub.identity.service;

import com.alphasoft.studyhub.config.StudyHubProperties;
import com.alphasoft.studyhub.identity.domain.RefreshToken;
import com.alphasoft.studyhub.identity.domain.User;
import com.alphasoft.studyhub.identity.repository.RefreshTokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

/**
 * Manages refresh token lifecycle: creation, validation, rotation, and revocation.
 *
 * Security model (DR-009):
 *  - Raw token = 32 cryptographically-random bytes, Base64URL-encoded (43 chars).
 *  - Stored value = hex SHA-256 of raw token (64 hex chars); raw NEVER persisted.
 *  - Each login starts a new family (UUID v4); rotated tokens inherit the family.
 *  - On USE of a valid token: atomic rotation — new token issued, old revoked.
 *  - On USE of a revoked/unknown token: entire family revoked (reuse attack detection).
 *  - Cookie attributes: httpOnly, SameSite=Strict, Secure (configurable for local dev).
 */
@Service
public class RefreshTokenService {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenService.class);

    static final String COOKIE_NAME = "refresh_token";

    private final RefreshTokenRepository repo;
    private final StudyHubProperties     props;
    private final SecureRandom           rng  = new SecureRandom();

    public RefreshTokenService(RefreshTokenRepository repo, StudyHubProperties props) {
        this.repo  = repo;
        this.props = props;
    }

    // ── Token creation ──────────────────────────────────────────────────────

    /**
     * Creates a new refresh token family for the user (called at login).
     * Stores the SHA-256 hash; returns the raw token to embed in the cookie.
     *
     * @param user the authenticated user
     * @return raw refresh token value (never stored)
     */
    @Transactional
    public String createRefreshToken(User user) {
        String rawToken  = generateRawToken();
        String tokenHash = sha256Hex(rawToken);
        String familyId  = UUID.randomUUID().toString();

        Instant expiresAt = Instant.now().plusMillis(props.jwt().refreshExpiryMs());

        RefreshToken token = new RefreshToken(user, tokenHash, familyId, expiresAt);
        repo.save(token);

        log.debug("Refresh token created for userId={} familyId={}", user.getId(), familyId);
        return rawToken;
    }

    // ── Token rotation ──────────────────────────────────────────────────────

    /**
     * Rotates a refresh token: validates the incoming token, revokes it,
     * issues a new one in the same family.
     *
     * <p>If the token is not found or already revoked, the entire family is
     * revoked as a reuse-attack countermeasure.
     *
     * @param rawToken raw token from the httpOnly cookie
     * @return new raw token for the rotated session
     * @throws RefreshTokenException if the token is invalid/expired/revoked
     */
    @Transactional
    public RotationResult rotate(String rawToken) {
        String hash = sha256Hex(rawToken);

        Optional<RefreshToken> maybeToken = repo.findByTokenHash(hash);

        if (maybeToken.isEmpty()) {
            // Token not found — possible stolen token; nothing to revoke
            log.warn("Refresh token rotate: token not found (possible theft or replay)");
            throw new RefreshTokenException("Invalid refresh token");
        }

        RefreshToken existing = maybeToken.get();

        if (existing.isRevoked()) {
            // Reuse detected — revoke entire family (DR-009 token rotation)
            log.warn("SECURITY: Refresh token reuse detected for familyId={}; revoking entire family",
                    existing.getFamilyId());
            repo.revokeAllByFamilyId(existing.getFamilyId(), Instant.now());
            throw new RefreshTokenException("Refresh token reuse detected; please log in again");
        }

        if (existing.isExpired()) {
            existing.revoke();
            repo.save(existing);
            log.debug("Refresh token rotate: token expired for userId={}", existing.getUser().getId());
            throw new RefreshTokenException("Refresh token has expired");
        }

        // Valid token — revoke the old one, issue a new one in the same family
        User   user     = existing.getUser();
        String familyId = existing.getFamilyId();

        existing.revoke();
        repo.save(existing);

        String newRawToken  = generateRawToken();
        String newTokenHash = sha256Hex(newRawToken);
        Instant newExpiry   = Instant.now().plusMillis(props.jwt().refreshExpiryMs());

        RefreshToken newToken = new RefreshToken(user, newTokenHash, familyId, newExpiry);
        repo.save(newToken);

        log.debug("Refresh token rotated for userId={} familyId={}", user.getId(), familyId);
        return new RotationResult(user, newRawToken);
    }

    // ── Revocation ──────────────────────────────────────────────────────────

    /**
     * Revokes the specific token in the cookie (single-device logout).
     *
     * @param rawToken raw token from the httpOnly cookie
     */
    @Transactional
    public void revoke(String rawToken) {
        String hash = sha256Hex(rawToken);
        repo.findByTokenHash(hash).ifPresent(token -> {
            token.revoke();
            repo.save(token);
            log.debug("Refresh token revoked for userId={}", token.getUser().getId());
        });
    }

    /**
     * Revokes ALL active refresh tokens for a user (all-device logout).
     *
     * @param userId the user's database ID
     */
    @Transactional
    public void revokeAllForUser(Long userId) {
        int count = repo.revokeAllByUserId(userId, Instant.now());
        log.debug("Revoked {} refresh tokens for userId={}", count, userId);
    }

    // ── Cookie helpers ──────────────────────────────────────────────────────

    /**
     * Writes the refresh token as an httpOnly cookie onto the response.
     * Cookie attributes come from StudyHubProperties (domain, secure, sameSite).
     *
     * @param response  the HTTP response
     * @param rawToken  the raw (unhashed) refresh token value
     */
    public void setRefreshCookie(HttpServletResponse response, String rawToken) {
        StudyHubProperties.Cookie cookieProps = props.cookie();

        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, rawToken)
                .httpOnly(true)
                .secure(cookieProps.secure())
                .sameSite(cookieProps.sameSite())
                .domain(cookieProps.domain())
                .path("/api/v1/auth")  // Scope the cookie to auth endpoints only
                .maxAge(props.jwt().refreshExpiryMs() / 1000)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /**
     * Clears the refresh token cookie (logout).
     *
     * @param response the HTTP response
     */
    public void clearRefreshCookie(HttpServletResponse response) {
        StudyHubProperties.Cookie cookieProps = props.cookie();

        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, "")
                .httpOnly(true)
                .secure(cookieProps.secure())
                .sameSite(cookieProps.sameSite())
                .domain(cookieProps.domain())
                .path("/api/v1/auth")
                .maxAge(0)   // immediate expiry
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /**
     * Extracts the raw refresh token from the request cookies.
     *
     * @param request the HTTP request
     * @return the raw token value, or empty if the cookie is absent
     */
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return Optional.empty();
        return Arrays.stream(cookies)
                .filter(c -> COOKIE_NAME.equals(c.getName()))
                .map(Cookie::getValue)
                .filter(v -> v != null && !v.isBlank())
                .findFirst();
    }

    // ── private helpers ──────────────────────────────────────────────────────

    private String generateRawToken() {
        byte[] bytes = new byte[32];
        rng.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            // SHA-256 is mandated by the JVM spec — this cannot happen
            throw new IllegalStateException("SHA-256 not available", ex);
        }
    }

    // ── Inner types ──────────────────────────────────────────────────────────

    /**
     * Result of a successful token rotation — carries the user and the new raw token.
     */
    public record RotationResult(User user, String newRawToken) {}
}
