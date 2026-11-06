package com.alphasoft.studyhub.auth;

import com.alphasoft.studyhub.identity.domain.Role;
import com.alphasoft.studyhub.identity.domain.User;
import com.alphasoft.studyhub.identity.repository.RoleRepository;
import com.alphasoft.studyhub.identity.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Sprint 2 — CSRF and CORS security tests.
 *
 * Tests:
 *  CSRF:
 *   1. POST /v1/auth/refresh WITHOUT CSRF token → 403.
 *   2. POST /v1/auth/logout  WITHOUT CSRF token → 403.
 *   3. POST /v1/auth/login (exempt) WITHOUT CSRF token → not 403.
 *   4. POST /v1/auth/register (exempt) WITHOUT CSRF token → not 403.
 *
 *  CORS:
 *   5. OPTIONS preflight from allowed origin → 200 with CORS headers.
 *   6. OPTIONS preflight from disallowed origin → no CORS headers.
 *   7. POST from allowed origin → response has Access-Control-Allow-Origin.
 *   8. POST from disallowed origin → no Access-Control-Allow-Origin header.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CsrfCorsTest {

    @Autowired MockMvc          mockMvc;
    @Autowired ObjectMapper     objectMapper;
    @Autowired UserRepository   userRepository;
    @Autowired RoleRepository   roleRepository;
    @Autowired PasswordEncoder  passwordEncoder;

    private static final String ALLOWED_ORIGIN    = "http://localhost:5173";
    private static final String DISALLOWED_ORIGIN = "http://evil.example.com";

    // ── CSRF tests ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("CSRF-1. POST /v1/auth/refresh without CSRF token → 403")
    void refresh_without_csrf_returns_403() throws Exception {
        mockMvc.perform(post("/v1/auth/refresh")
                // NO csrf() post processor — sends no X-XSRF-TOKEN header
                .cookie(new jakarta.servlet.http.Cookie("refresh_token", "some-token")))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("CSRF-2. POST /v1/auth/logout without CSRF token → 403")
    void logout_without_csrf_returns_403() throws Exception {
        mockMvc.perform(post("/v1/auth/logout")
                // NO csrf() post processor
                .cookie(new jakarta.servlet.http.Cookie("refresh_token", "some-token")))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("CSRF-3. POST /v1/auth/login (CSRF-exempt) without CSRF token → not 403")
    void login_exempt_from_csrf() throws Exception {
        // Login is CSRF-exempt — missing CSRF token must NOT cause 403
        // (It may 401 or 400 for bad credentials, but not 403)
        MvcResult result = mockMvc.perform(post("/v1/auth/login")
                // NO csrf() post processor
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                    "email", "nobody@test.com",
                    "password", "Whatever@1"
                ))))
            .andReturn();

        assertThat(result.getResponse().getStatus()).isNotEqualTo(403);
    }

    @Test
    @DisplayName("CSRF-4. POST /v1/auth/register (CSRF-exempt) without CSRF token → not 403")
    void register_exempt_from_csrf() throws Exception {
        // Register is CSRF-exempt
        MvcResult result = mockMvc.perform(post("/v1/auth/register")
                // NO csrf() post processor
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                    "email", "x@test.com",
                    "password", "Pass@123",
                    "displayName", "X",
                    "invitationCode", "00000000-0000-0000-0000-000000000000"
                ))))
            .andReturn();

        assertThat(result.getResponse().getStatus()).isNotEqualTo(403);
    }

    // ── CORS tests ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("CORS-5. OPTIONS preflight from allowed origin → 200 with ACAO header")
    void preflight_allowed_origin_returns_200() throws Exception {
        mockMvc.perform(options("/v1/auth/login")
                .header("Origin", ALLOWED_ORIGIN)
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "Content-Type"))
            .andExpect(status().isOk())
            .andExpect(header().string("Access-Control-Allow-Origin", ALLOWED_ORIGIN));
    }

    @Test
    @DisplayName("CORS-6. OPTIONS preflight from disallowed origin → no ACAO header")
    void preflight_disallowed_origin_no_cors_header() throws Exception {
        mockMvc.perform(options("/v1/auth/login")
                .header("Origin", DISALLOWED_ORIGIN)
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "Content-Type"))
            .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Test
    @DisplayName("CORS-7. POST from allowed origin → response has Access-Control-Allow-Origin")
    void post_allowed_origin_has_cors_header() throws Exception {
        mockMvc.perform(post("/v1/auth/login")
                .with(csrf())
                .header("Origin", ALLOWED_ORIGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                    "email", "nobody@test.com",
                    "password", "Whatever@1"
                ))))
            .andExpect(header().string("Access-Control-Allow-Origin", ALLOWED_ORIGIN));
    }

    @Test
    @DisplayName("CORS-8. POST from disallowed origin → no Access-Control-Allow-Origin header")
    void post_disallowed_origin_no_cors_header() throws Exception {
        mockMvc.perform(post("/v1/auth/login")
                .with(csrf())
                .header("Origin", DISALLOWED_ORIGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                    "email", "nobody@test.com",
                    "password", "Whatever@1"
                ))))
            .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }
}
