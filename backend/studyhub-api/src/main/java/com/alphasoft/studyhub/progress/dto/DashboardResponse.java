package com.alphasoft.studyhub.progress.dto;

import java.util.List;

/**
 * Full learner dashboard payload — returned by GET /api/v1/learner/dashboard.
 *
 * <p>Contains overall stats and per-course progress summaries.
 * Mastery data is NOT included here (DR-022 — mastery ≠ completion).
 */
public record DashboardResponse(
        /** Learner's display name. */
        String              displayName,

        /** Total XP accumulated across all lesson completions. */
        int                 totalXp,

        /** Total lessons the learner has marked complete. */
        long                completedLessons,

        /** Per-course progress snapshot. Ordered by most-recently-updated. */
        List<CourseSummary> courses
) {
    /**
     * Per-course progress on the dashboard.
     *
     * <p>{@code completionPercent} = completedInCourse / publishedInCourse * 100,
     * clamped to [0, 100].  Returns 0 when publishedInCourse == 0.
     */
    public record CourseSummary(
            Long   courseId,
            String courseTitle,
            String courseSlug,
            int    publishedLessons,
            int    completedLessons,
            int    completionPercent
    ) {}
}
