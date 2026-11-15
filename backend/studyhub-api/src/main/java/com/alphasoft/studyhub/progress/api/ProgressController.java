package com.alphasoft.studyhub.progress.api;

import com.alphasoft.studyhub.identity.security.JwtPrincipal;
import com.alphasoft.studyhub.progress.dto.LessonCompleteRequest;
import com.alphasoft.studyhub.progress.dto.LessonCompleteResponse;
import com.alphasoft.studyhub.progress.dto.ProgressSummaryResponse;
import com.alphasoft.studyhub.progress.service.ProgressService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Learner progress API.
 *
 * <p>All endpoints require an authenticated learner (enforced by {@link
 * com.alphasoft.studyhub.config.SecurityConfig}).
 *
 * <p>Authorization: the authenticated user's ID is taken from the JWT
 * principal — the caller cannot specify a different user's ID.
 *
 * <p>Path convention: annotation uses the servlet path (without
 * {@code server.servlet.context-path=/api}).
 * Effective production URLs: {@code /api/v1/progress/…}
 */
@RestController
@RequestMapping("/v1/progress")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    /**
     * POST /api/v1/progress/lessons/{lessonId}/complete
     *
     * <p>Marks the lesson as complete for the authenticated learner.
     * Idempotent — repeated calls return the existing state without re-awarding XP.
     *
     * @return 200 with the updated progress state; 404 if lesson not found / not published
     */
    @PostMapping("/lessons/{lessonId}/complete")
    public ResponseEntity<LessonCompleteResponse> completeLesson(
            @PathVariable Long lessonId,
            @RequestBody(required = false) LessonCompleteRequest body,
            @AuthenticationPrincipal JwtPrincipal principal) {

        LessonCompleteResponse response =
                progressService.completeLesson(principal.userId(), lessonId);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/progress/me?page=0&size=20
     *
     * <p>Returns the authenticated learner's progress records, newest first.
     * DR-015-NATIVE: paginated from day one.
     */
    @GetMapping("/me")
    public ResponseEntity<Page<ProgressSummaryResponse>> getMyProgress(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PageableDefault(size = 20, sort = "updatedAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        Page<ProgressSummaryResponse> page =
                progressService.getMyProgress(principal.userId(), pageable);
        return ResponseEntity.ok(page);
    }
}
