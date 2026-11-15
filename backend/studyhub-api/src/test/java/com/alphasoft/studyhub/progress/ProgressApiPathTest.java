package com.alphasoft.studyhub.progress;

import com.alphasoft.studyhub.identity.security.JwtPrincipal;
import com.alphasoft.studyhub.identity.security.JwtService;
import com.alphasoft.studyhub.progress.api.LearnerDashboardController;
import com.alphasoft.studyhub.progress.api.ProgressController;
import com.alphasoft.studyhub.progress.domain.LessonProgressStatus;
import com.alphasoft.studyhub.progress.dto.DashboardResponse;
import com.alphasoft.studyhub.progress.dto.LessonCompleteResponse;
import com.alphasoft.studyhub.progress.dto.ProgressSummaryResponse;
import com.alphasoft.studyhub.progress.service.ProgressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Sprint 5 — Progress API path and authorization regression tests.
 *
 * <p>These tests verify:
 * <ul>
 *   <li>All progress endpoints are reachable at their expected URLs (no 404 routing failures)</li>
 *   <li>Unauthenticated requests return 401 (not 404 — which would mean the route is missing)</li>
 *   <li>Authenticated requests reach the controller and return expected shapes</li>
 * </ul>
 *
 * <p>Test strategy: {@code @WebMvcTest} with context-path simulation via
 * {@code MockMvc.contextPath("/api")} — same pattern as {@code CurriculumApiPathRegressionTest}.
 */
@WebMvcTest(controllers = {
    ProgressController.class,
    LearnerDashboardController.class
})
class ProgressApiPathTest {

    @Autowired
    private WebApplicationContext wac;

    @MockBean
    private ProgressService progressService;

    @MockBean
    private JwtService jwtService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .defaultRequest(get("/").contextPath("/api"))
                .build();
    }

    /** Creates an authenticated SecurityContext with a JwtPrincipal for user id=1. */
    private static UsernamePasswordAuthenticationToken learnerAuth() {
        JwtPrincipal principal = new JwtPrincipal(1L, "alice@example.com");
        return new UsernamePasswordAuthenticationToken(
                principal, null,
                List.of(new SimpleGrantedAuthority("ROLE_LEARNER")));
    }

    // ── Unauthenticated access returns 401 (route registered) ────────────────

    @Nested
    @DisplayName("Unauthenticated requests — 401, not 404")
    class UnauthenticatedAccess {

        @Test
        @WithAnonymousUser
        @DisplayName("POST /api/v1/progress/lessons/{id}/complete → 401 for anonymous")
        void complete_lesson_requires_auth() throws Exception {
            mockMvc.perform(post("/api/v1/progress/lessons/1/complete").contextPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                   .andExpect(result -> {
                       int status = result.getResponse().getStatus();
                       if (status == 404) throw new AssertionError(
                               "REGRESSION: POST /api/v1/progress/lessons/{id}/complete returned 404 — route not registered");
                       if (status != 401 && status != 403) throw new AssertionError(
                               "Expected 401/403, got " + status);
                   });
        }

        @Test
        @WithAnonymousUser
        @DisplayName("GET /api/v1/progress/me → 401 for anonymous")
        void get_my_progress_requires_auth() throws Exception {
            mockMvc.perform(get("/api/v1/progress/me").contextPath("/api")
                        .accept(MediaType.APPLICATION_JSON))
                   .andExpect(result -> {
                       int status = result.getResponse().getStatus();
                       if (status == 404) throw new AssertionError(
                               "REGRESSION: GET /api/v1/progress/me returned 404 — route not registered");
                       if (status != 401 && status != 403) throw new AssertionError(
                               "Expected 401/403, got " + status);
                   });
        }

        @Test
        @WithAnonymousUser
        @DisplayName("GET /api/v1/learner/dashboard → 401 for anonymous")
        void get_dashboard_requires_auth() throws Exception {
            mockMvc.perform(get("/api/v1/learner/dashboard").contextPath("/api")
                        .accept(MediaType.APPLICATION_JSON))
                   .andExpect(result -> {
                       int status = result.getResponse().getStatus();
                       if (status == 404) throw new AssertionError(
                               "REGRESSION: GET /api/v1/learner/dashboard returned 404 — route not registered");
                       if (status != 401 && status != 403) throw new AssertionError(
                               "Expected 401/403, got " + status);
                   });
        }
    }

    // ── Authenticated requests ────────────────────────────────────────────────

    @Nested
    @DisplayName("Authenticated requests — service invoked, 200 returned")
    class AuthenticatedAccess {

        @BeforeEach
        void stubService() {
            when(progressService.completeLesson(anyLong(), anyLong()))
                    .thenReturn(new LessonCompleteResponse(
                            1L, 1L, LessonProgressStatus.COMPLETED,
                            Instant.now(), 10, 10));

            when(progressService.getMyProgress(anyLong(), any()))
                    .thenReturn(new PageImpl<>(List.of(
                            new ProgressSummaryResponse(1L, 1L, "OSI Model",
                                    LessonProgressStatus.COMPLETED, Instant.now(), 10))));

            when(progressService.getDashboard(anyLong()))
                    .thenReturn(new DashboardResponse("Alice", 10, 1L, Collections.emptyList()));
        }

        @Test
        @DisplayName("POST /api/v1/progress/lessons/{id}/complete → 200")
        void complete_lesson_returns_200() throws Exception {
            mockMvc.perform(post("/api/v1/progress/lessons/1/complete").contextPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .with(authentication(learnerAuth())))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.status").value("COMPLETED"))
                   .andExpect(jsonPath("$.xpAwarded").value(10));
        }

        @Test
        @DisplayName("GET /api/v1/progress/me → 200 with paged content")
        void get_my_progress_returns_200() throws Exception {
            mockMvc.perform(get("/api/v1/progress/me").contextPath("/api")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(learnerAuth())))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.content").isArray())
                   .andExpect(jsonPath("$.content[0].lessonTitle").value("OSI Model"));
        }

        @Test
        @DisplayName("GET /api/v1/learner/dashboard → 200 with dashboard fields")
        void get_dashboard_returns_200() throws Exception {
            mockMvc.perform(get("/api/v1/learner/dashboard").contextPath("/api")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(learnerAuth())))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.displayName").value("Alice"))
                   .andExpect(jsonPath("$.totalXp").value(10))
                   .andExpect(jsonPath("$.completedLessons").value(1))
                   .andExpect(jsonPath("$.courses").isArray());
        }
    }
}
