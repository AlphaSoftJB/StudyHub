package com.alphasoft.studyhub.health;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Sprint 1 — Actuator health endpoint tests.
 *
 * Verifies:
 *  1. GET /actuator/health returns HTTP 200 without authentication.
 *  2. Response body contains {"status":"UP"}.
 *  3. Any other endpoint returns HTTP 401 (SecurityConfig is locking down all others).
 *
 * Note: context-path is /api (application.yml), but MockMvc uses the servlet path
 * directly — the full path in a real server would be /api/actuator/health.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HealthEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /actuator/health returns 200 UP without authentication")
    void health_endpoint_returns_up() throws Exception {
        mockMvc.perform(get("/actuator/health")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("GET /actuator/health is accessible anonymously (no auth header needed)")
    void health_endpoint_is_public() throws Exception {
        // No authentication header — expect 200, not 401
        mockMvc.perform(get("/actuator/health"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET on a non-existent endpoint returns 401 when unauthenticated")
    void non_public_endpoint_requires_auth() throws Exception {
        // Any unknown path that isn't /actuator/health should be blocked
        mockMvc.perform(get("/api/v1/some-protected-resource"))
            .andExpect(status().isUnauthorized());
    }
}
