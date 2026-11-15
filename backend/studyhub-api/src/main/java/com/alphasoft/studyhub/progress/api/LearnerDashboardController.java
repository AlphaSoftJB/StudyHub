package com.alphasoft.studyhub.progress.api;

import com.alphasoft.studyhub.identity.security.JwtPrincipal;
import com.alphasoft.studyhub.progress.dto.DashboardResponse;
import com.alphasoft.studyhub.progress.service.ProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Learner dashboard endpoint.
 *
 * <p>Effective production URL: {@code GET /api/v1/learner/dashboard}
 * (servlet path {@code /v1/learner/dashboard}; context-path {@code /api} prepended
 * by the container).
 *
 * <p>Requires authenticated learner; userId taken from JWT principal.
 */
@RestController
@RequestMapping("/v1/learner")
public class LearnerDashboardController {

    private final ProgressService progressService;

    public LearnerDashboardController(ProgressService progressService) {
        this.progressService = progressService;
    }

    /**
     * GET /api/v1/learner/dashboard
     *
     * <p>Returns the learner's overall stats and per-course progress summary.
     * Mastery data is NOT included (DR-022 — mastery ≠ completion).
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard(
            @AuthenticationPrincipal JwtPrincipal principal) {

        DashboardResponse dashboard =
                progressService.getDashboard(principal.userId());
        return ResponseEntity.ok(dashboard);
    }
}
