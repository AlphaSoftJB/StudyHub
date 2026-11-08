package com.alphasoft.studyhub.curriculum.api;

import com.alphasoft.studyhub.curriculum.api.dto.ContentStatusTransitionRequest;
import com.alphasoft.studyhub.curriculum.api.dto.LessonDetailDto;
import com.alphasoft.studyhub.curriculum.service.ContentLifecycleService;
import com.alphasoft.studyhub.curriculum.service.CurriculumQueryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Content administration endpoints — ROLE_ADMIN only.
 *
 * <p>DR-012: AI-generated content must not automatically become published educational
 * content. All transitions require an authenticated admin actor; the actor ID is
 * recorded in audit columns on the lesson.
 *
 * <p>DR-012: AI must not independently certify learner mastery.
 * Publish decisions always require a human admin actor (this endpoint).
 *
 * <p>Path convention: annotation uses the servlet path (without
 * {@code server.servlet.context-path=/api}). Effective production URL: /api/v1/admin/*.
 * SecurityConfig {@code /v1/admin/**} rule matches the same servlet path.
 */
@RestController
@RequestMapping("/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class ContentAdminController {

    private final ContentLifecycleService lifecycleService;
    private final CurriculumQueryService  queryService;

    public ContentAdminController(ContentLifecycleService lifecycleService,
                                  CurriculumQueryService queryService) {
        this.lifecycleService = lifecycleService;
        this.queryService     = queryService;
    }

    /**
     * PATCH /api/v1/admin/lessons/{id}/status
     *
     * <p>Advances or regresses a lesson through the DR-012 lifecycle.
     * Returns the updated lesson (all statuses visible to admin).
     *
     * @param principal the Spring Security principal; userId extracted for audit
     */
    @PatchMapping("/lessons/{id}/status")
    public ResponseEntity<?> transitionLessonStatus(
            @PathVariable Long id,
            @Valid @RequestBody ContentStatusTransitionRequest request,
            @AuthenticationPrincipal Object principal) {

        Long actorUserId = extractUserId(principal);

        try {
            var updated = lifecycleService.transitionLesson(id, request.targetStatus(), actorUserId);
            return ResponseEntity.ok(LessonDetailDto.fromAdmin(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/v1/admin/lessons/{id}
     * Returns a lesson regardless of content status.
     */
    @GetMapping("/lessons/{id}")
    public ResponseEntity<LessonDetailDto> getLessonAdmin(@PathVariable Long id) {
        return queryService.getLesson(id)
                           .map(LessonDetailDto::fromAdmin)
                           .map(ResponseEntity::ok)
                           .orElse(ResponseEntity.notFound().build());
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Long extractUserId(Object principal) {
        // Principal is a UserDetails-based object set by JwtAuthFilter (Sprint 2).
        // The JWT subject is the user ID as a string.
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails ud) {
            try { return Long.parseLong(ud.getUsername()); } catch (NumberFormatException ignored) {}
        }
        // Fallback: return null; audit columns will be null for the transition
        return null;
    }
}
