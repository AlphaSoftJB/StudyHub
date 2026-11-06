package com.alphasoft.studyhub.auth;

import com.alphasoft.studyhub.identity.domain.InvitationCode;
import com.alphasoft.studyhub.identity.domain.Role;
import com.alphasoft.studyhub.identity.domain.User;
import com.alphasoft.studyhub.identity.repository.InvitationCodeRepository;
import com.alphasoft.studyhub.identity.repository.RoleRepository;
import com.alphasoft.studyhub.identity.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Sprint 2 — Auth API integration tests.
 *
 * Runs against the full Spring context with H2 (test profile).
 *
 * Tests:
 *  1. POST /v1/auth/register — valid invite → 201 with access token.
 *  2. POST /v1/auth/register — invalid/unknown invite code → 400.
 *  3. POST /v1/auth/register — duplicate email → 400.
 *  4. POST /v1/auth/register — missing fields → 400 with field errors.
 *  5. POST /v1/auth/login — valid credentials → 200 with access token + cookie.
 *  6. POST /v1/auth/login — wrong password → 401.
 *  7. POST /v1/auth/login — unknown email → 401.
 *  8. POST /v1/auth/refresh — valid cookie → 200 with new access token.
 *  9. POST /v1/auth/logout — clears refresh cookie → 204.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthApiTest {

    @Autowired MockMvc          mockMvc;
    @Autowired ObjectMapper     objectMapper;
    @Autowired UserRepository   userRepository;
    @Autowired RoleRepository   roleRepository;
    @Autowired InvitationCodeRepository inviteRepo;
    @Autowired PasswordEncoder  passwordEncoder;

    private User  adminUser;
    private Role  learnerRole;

    @BeforeEach
    void setUp() {
        // The admin user is needed to create invite codes
        // Flyway V1 seeds ROLE_LEARNER and ROLE_ADMIN
        learnerRole = roleRepository.findByName("ROLE_LEARNER")
                .orElseThrow(() -> new IllegalStateException("ROLE_LEARNER not found"));
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN not found"));

        adminUser = new User("admin@test.com", passwordEncoder.encode("Admin@1234"), "Admin");
        adminUser.addRole(adminRole);
        userRepository.save(adminUser);
    }

    // ── Helper: create a fresh usable invite code ────────────────────────────

    private String createUsableInvite() {
        String code = UUID.randomUUID().toString();
        InvitationCode invite = new InvitationCode(
            code, adminUser, Instant.now().plusSeconds(3600));
        inviteRepo.save(invite);
        return code;
    }

    // ── 1. Register — valid ──────────────────────────────────────────────────

    @Test
    @DisplayName("1. Register with valid invite → 201, accessToken in body, refresh cookie set")
    void register_valid_invite_returns_201() throws Exception {
        String inviteCode = createUsableInvite();

        MvcResult result = mockMvc.perform(post("/v1/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                    "email",          "learner@test.com",
                    "password",       "Password@123",
                    "displayName",    "Test Learner",
                    "invitationCode", inviteCode
                ))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accessToken").isNotEmpty())
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.userId").isNumber())
            .andReturn();

        // Verify refresh cookie was set
        String setCookie = result.getResponse().getHeader("Set-Cookie");
        assertThat(setCookie).contains("refresh_token=");
        assertThat(setCookie).contains("HttpOnly");
        assertThat(setCookie).contains("SameSite=Strict");
    }

    // ── 2. Register — invalid invite ────────────────────────────────────────

    @Test
    @DisplayName("2. Register with unknown invite code → 400")
    void register_unknown_invite_returns_400() throws Exception {
        mockMvc.perform(post("/v1/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                    "email",          "learner2@test.com",
                    "password",       "Password@123",
                    "displayName",    "Test Learner",
                    "invitationCode", UUID.randomUUID().toString()
                ))))
            .andExpect(status().isBadRequest());
    }

    // ── 3. Register — duplicate email ────────────────────────────────────────

    @Test
    @DisplayName("3. Register with duplicate email → 400")
    void register_duplicate_email_returns_400() throws Exception {
        // First registration
        String invite1 = createUsableInvite();
        mockMvc.perform(post("/v1/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                    "email",          "dup@test.com",
                    "password",       "Password@123",
                    "displayName",    "First",
                    "invitationCode", invite1
                ))))
            .andExpect(status().isCreated());

        // Second registration with same email but new invite
        String invite2 = createUsableInvite();
        mockMvc.perform(post("/v1/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                    "email",          "dup@test.com",
                    "password",       "Password@123",
                    "displayName",    "Second",
                    "invitationCode", invite2
                ))))
            .andExpect(status().isBadRequest());
    }

    // ── 4. Register — validation errors ─────────────────────────────────────

    @Test
    @DisplayName("4. Register with missing fields → 400 with fieldErrors")
    void register_missing_fields_returns_400() throws Exception {
        mockMvc.perform(post("/v1/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                    "email", "not-an-email"
                    // password, displayName, invitationCode all missing
                ))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    // ── 5. Login — valid ─────────────────────────────────────────────────────

    @Test
    @DisplayName("5. Login with valid credentials → 200, accessToken, refresh cookie")
    void login_valid_credentials_returns_200() throws Exception {
        // Create a learner directly
        User learner = new User("active@test.com",
                                passwordEncoder.encode("Secure@Pass1"), "Active");
        learner.addRole(learnerRole);
        userRepository.save(learner);

        MvcResult result = mockMvc.perform(post("/v1/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                    "email",    "active@test.com",
                    "password", "Secure@Pass1"
                ))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").isNotEmpty())
            .andReturn();

        String setCookie = result.getResponse().getHeader("Set-Cookie");
        assertThat(setCookie).contains("refresh_token=");
        assertThat(setCookie).contains("HttpOnly");
    }

    // ── 6. Login — wrong password ────────────────────────────────────────────

    @Test
    @DisplayName("6. Login with wrong password → 401")
    void login_wrong_password_returns_401() throws Exception {
        User learner = new User("user6@test.com",
                                passwordEncoder.encode("CorrectPass@1"), "User6");
        learner.addRole(learnerRole);
        userRepository.save(learner);

        mockMvc.perform(post("/v1/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                    "email",    "user6@test.com",
                    "password", "WrongPass@1"
                ))))
            .andExpect(status().isUnauthorized());
    }

    // ── 7. Login — unknown email ─────────────────────────────────────────────

    @Test
    @DisplayName("7. Login with unknown email → 401")
    void login_unknown_email_returns_401() throws Exception {
        mockMvc.perform(post("/v1/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                    "email",    "nobody@test.com",
                    "password", "Whatever@1"
                ))))
            .andExpect(status().isUnauthorized());
    }

    // ── 8. Refresh — using valid cookie ─────────────────────────────────────

    @Test
    @DisplayName("8. POST /v1/auth/refresh with valid cookie → 200 with new accessToken")
    void refresh_valid_cookie_returns_200() throws Exception {
        // Login first to get a refresh cookie
        User learner = new User("refresh@test.com",
                                passwordEncoder.encode("Pass@12345"), "Refresher");
        learner.addRole(learnerRole);
        userRepository.save(learner);

        MvcResult loginResult = mockMvc.perform(post("/v1/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                    "email",    "refresh@test.com",
                    "password", "Pass@12345"
                ))))
            .andExpect(status().isOk())
            .andReturn();

        String setCookie = loginResult.getResponse().getHeader("Set-Cookie");
        assertThat(setCookie).isNotNull();

        // Extract cookie value from "refresh_token=<value>; ..."
        String cookieValue = extractCookieValue(setCookie, "refresh_token");

        // Use it to refresh
        mockMvc.perform(post("/v1/auth/refresh")
                .with(csrf())
                .cookie(new jakarta.servlet.http.Cookie("refresh_token", cookieValue)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    // ── 9. Logout — clears cookie ────────────────────────────────────────────

    @Test
    @DisplayName("9. POST /v1/auth/logout → 204 and clears refresh cookie")
    void logout_returns_204_and_clears_cookie() throws Exception {
        User learner = new User("logout@test.com",
                                passwordEncoder.encode("Pass@12345"), "Logouter");
        learner.addRole(learnerRole);
        userRepository.save(learner);

        MvcResult loginResult = mockMvc.perform(post("/v1/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                    "email",    "logout@test.com",
                    "password", "Pass@12345"
                ))))
            .andExpect(status().isOk())
            .andReturn();

        String setCookie = loginResult.getResponse().getHeader("Set-Cookie");
        String cookieValue = extractCookieValue(setCookie, "refresh_token");

        MvcResult logoutResult = mockMvc.perform(post("/v1/auth/logout")
                .with(csrf())
                .cookie(new jakarta.servlet.http.Cookie("refresh_token", cookieValue)))
            .andExpect(status().isNoContent())
            .andReturn();

        // Verify the cookie was cleared (Max-Age=0)
        String clearedCookie = logoutResult.getResponse().getHeader("Set-Cookie");
        assertThat(clearedCookie).contains("Max-Age=0");
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private String extractCookieValue(String setCookieHeader, String cookieName) {
        if (setCookieHeader == null) return null;
        for (String part : setCookieHeader.split(";")) {
            String trimmed = part.trim();
            if (trimmed.startsWith(cookieName + "=")) {
                return trimmed.substring(cookieName.length() + 1);
            }
        }
        return null;
    }
}
