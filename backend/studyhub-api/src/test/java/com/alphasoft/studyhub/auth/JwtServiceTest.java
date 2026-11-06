package com.alphasoft.studyhub.auth;

import com.alphasoft.studyhub.config.StudyHubProperties;
import com.alphasoft.studyhub.identity.security.JwtService;
import com.alphasoft.studyhub.identity.security.JwtValidationException;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for JwtService — no Spring context required.
 *
 * Tests:
 *  1. Valid token is accepted and claims extracted correctly.
 *  2. Expired token throws JwtValidationException.
 *  3. Token with wrong signature throws JwtValidationException.
 *  4. Tampered claims throw JwtValidationException.
 *  5. extractUserId returns correct Long from sub claim.
 *  6. extractRoles returns correct list.
 */
class JwtServiceTest {

    private JwtService jwtService;

    // 32+ chars required for HMAC-SHA256
    private static final String TEST_SECRET      = "test_access_secret_min32chars_xxx";
    private static final String DIFFERENT_SECRET = "different_secret_min32chars_xxxxx";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(buildProps(TEST_SECRET, 900_000L)); // 15 min
    }

    @Test
    @DisplayName("1. Valid token: issueAccessToken → validateAndParse succeeds")
    void valid_token_accepted() {
        String token = jwtService.issueAccessToken(42L, "alice@example.com",
                List.of("ROLE_LEARNER"));

        assertThatNoException().isThrownBy(() -> jwtService.validateAndParse(token));
    }

    @Test
    @DisplayName("2. Expired token throws JwtValidationException")
    void expired_token_rejected() {
        // Issue a token with 1ms expiry
        JwtService shortLived = new JwtService(buildProps(TEST_SECRET, 1L));
        String token = shortLived.issueAccessToken(1L, "user@example.com", List.of());

        // Sleep enough for it to expire
        try { Thread.sleep(50); } catch (InterruptedException ignored) {}

        assertThatThrownBy(() -> jwtService.validateAndParse(token))
                .isInstanceOf(JwtValidationException.class)
                .hasMessageContaining("expired");
    }

    @Test
    @DisplayName("3. Token signed with wrong key throws JwtValidationException")
    void wrong_signature_rejected() {
        JwtService other = new JwtService(buildProps(DIFFERENT_SECRET, 900_000L));
        String tokenFromOther = other.issueAccessToken(1L, "user@example.com", List.of());

        assertThatThrownBy(() -> jwtService.validateAndParse(tokenFromOther))
                .isInstanceOf(JwtValidationException.class);
    }

    @Test
    @DisplayName("4. Tampered token (modified payload) throws JwtValidationException")
    void tampered_token_rejected() {
        String token = jwtService.issueAccessToken(1L, "user@example.com", List.of("ROLE_LEARNER"));

        // Flip one character in the payload section (index 1)
        String[] parts = token.split("\\.");
        String tamperedPayload = parts[1].substring(0, parts[1].length() - 1) + "X";
        String tampered = parts[0] + "." + tamperedPayload + "." + parts[2];

        assertThatThrownBy(() -> jwtService.validateAndParse(tampered))
                .isInstanceOf(JwtValidationException.class);
    }

    @Test
    @DisplayName("5. extractUserId returns the Long from the sub claim")
    void extract_user_id() {
        String token = jwtService.issueAccessToken(99L, "user@example.com", List.of());
        Claims claims = jwtService.validateAndParse(token);

        assertThat(jwtService.extractUserId(claims)).isEqualTo(99L);
    }

    @Test
    @DisplayName("6. extractRoles returns the full roles list")
    void extract_roles() {
        List<String> roles = List.of("ROLE_LEARNER", "ROLE_ADMIN");
        String token = jwtService.issueAccessToken(1L, "user@example.com", roles);
        Claims claims = jwtService.validateAndParse(token);

        assertThat(jwtService.extractRoles(claims)).containsExactlyInAnyOrderElementsOf(roles);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private StudyHubProperties buildProps(String accessSecret, long expiryMs) {
        return new StudyHubProperties(
            new StudyHubProperties.Jwt(accessSecret, expiryMs,
                                       "test_refresh_secret_min32chars_xx", 3_600_000L),
            new StudyHubProperties.Cookie("localhost", false, "Strict"),
            new StudyHubProperties.Cors(List.of("http://localhost:5173")),
            new StudyHubProperties.Pilot(10),
            new StudyHubProperties.Ai(new BigDecimal("25.00"), new BigDecimal("20.00"))
        );
    }
}
