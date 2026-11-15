package com.alphasoft.studyhub.progress.service;

import com.alphasoft.studyhub.curriculum.domain.Course;
import com.alphasoft.studyhub.curriculum.repository.CourseRepository;
import com.alphasoft.studyhub.curriculum.repository.LessonRepository;
import com.alphasoft.studyhub.identity.repository.UserRepository;
import com.alphasoft.studyhub.progress.domain.LessonProgressStatus;
import com.alphasoft.studyhub.progress.domain.UserLessonProgress;
import com.alphasoft.studyhub.progress.domain.UserXp;
import com.alphasoft.studyhub.progress.dto.DashboardResponse;
import com.alphasoft.studyhub.progress.dto.DashboardResponse.CourseSummary;
import com.alphasoft.studyhub.progress.dto.LessonCompleteResponse;
import com.alphasoft.studyhub.progress.dto.ProgressSummaryResponse;
import com.alphasoft.studyhub.progress.repository.UserLessonProgressRepository;
import com.alphasoft.studyhub.progress.repository.UserXpRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Core service for learner progress operations.
 *
 * <p>All state-changing methods are {@code @Transactional}.
 * Read methods are {@code @Transactional(readOnly = true)}.
 *
 * <p>Authorization contract:
 * <ul>
 *   <li>Every method accepts {@code userId} from the JWT principal — the caller
 *       cannot read or modify another user's data.</li>
 *   <li>Controllers must pass the authenticated user's ID; they must not accept
 *       a userId from the request body or query parameters.</li>
 * </ul>
 *
 * <p>DR-022: completion ≠ mastery.  No mastery inference is made here.
 */
@Service
public class ProgressService {

    /** XP awarded per lesson completion.  Constant for Sprint 5; configurable later. */
    public static final int XP_PER_LESSON = 10;

    private final UserLessonProgressRepository progressRepo;
    private final UserXpRepository             xpRepo;
    private final UserRepository               userRepo;
    private final LessonRepository             lessonRepo;
    private final CourseRepository             courseRepo;

    public ProgressService(UserLessonProgressRepository progressRepo,
                           UserXpRepository xpRepo,
                           UserRepository userRepo,
                           LessonRepository lessonRepo,
                           CourseRepository courseRepo) {
        this.progressRepo = progressRepo;
        this.xpRepo       = xpRepo;
        this.userRepo     = userRepo;
        this.lessonRepo   = lessonRepo;
        this.courseRepo   = courseRepo;
    }

    // ── Mark lesson complete ─────────────────────────────────────────────────

    /**
     * Marks a lesson as complete for the authenticated learner.
     * Idempotent — repeated calls do not re-award XP.
     *
     * @param userId   the authenticated learner's ID (from JWT — never from request body)
     * @param lessonId the lesson to complete
     * @return response with the updated progress state and total XP
     * @throws ResponseStatusException 404 if the lesson does not exist or is not PUBLISHED
     */
    @Transactional
    public LessonCompleteResponse completeLesson(Long userId, Long lessonId) {
        // Lesson must exist and be published (DR-012)
        var lesson = lessonRepo.findPublishedById(lessonId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Lesson not found or not published: " + lessonId));

        var user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not found"));

        // Upsert progress row
        var progress = progressRepo
                .findByUserIdAndLessonId(userId, lessonId)
                .orElseGet(() -> {
                    var p = UserLessonProgress.startFor(user, lesson);
                    return progressRepo.save(p);
                });

        int xpBefore = progress.getXpAwarded();
        progress.complete(XP_PER_LESSON);
        progressRepo.save(progress);

        // Award XP only once (idempotent — xpBefore > 0 means already awarded)
        int xpAwarded = progress.getXpAwarded() - xpBefore;

        var userXp = xpRepo.findByUserId(userId)
                .orElseGet(() -> {
                    var x = UserXp.createFor(user);
                    return xpRepo.save(x);
                });

        if (xpAwarded > 0) {
            userXp.addXp(xpAwarded);
            xpRepo.save(userXp);
        }

        return LessonCompleteResponse.from(progress, userXp.getTotalXp());
    }

    // ── Progress list (DR-015-NATIVE: paginated) ─────────────────────────────

    /**
     * Returns the authenticated learner's progress records, newest first.
     *
     * @param userId   the authenticated learner's ID
     * @param pageable pagination parameters
     */
    @Transactional(readOnly = true)
    public Page<ProgressSummaryResponse> getMyProgress(Long userId, Pageable pageable) {
        return progressRepo
                .findByUserIdOrderByUpdatedAtDesc(userId, pageable)
                .map(ProgressSummaryResponse::from);
    }

    // ── Learner dashboard ────────────────────────────────────────────────────

    /**
     * Returns the learner dashboard: stats + per-course progress.
     *
     * <p>Only courses in which the learner has at least one progress record are
     * included in the per-course breakdown.  The list is ordered by most-recently-updated.
     *
     * @param userId the authenticated learner's ID
     */
    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(Long userId) {
        var user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not found"));

        // Total completed lessons
        long completedCount = progressRepo
                .countByUserIdAndStatus(userId, LessonProgressStatus.COMPLETED);

        // Total XP
        int totalXp = xpRepo.findByUserId(userId)
                .map(UserXp::getTotalXp)
                .orElse(0);

        // Courses with at least one progress record — ordered by most-recently-updated
        List<Long> courseIds = progressRepo.findDistinctCourseIdsByUserId(userId);

        if (courseIds.isEmpty()) {
            return new DashboardResponse(
                    user.getDisplayName(),
                    totalXp,
                    completedCount,
                    List.of());
        }

        // Published lesson counts per course (single aggregate query)
        Map<Long, Long> publishedPerCourse = progressRepo
                .countPublishedLessonsPerCourse(courseIds)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]));

        // Completed lesson counts per course (single aggregate query)
        Map<Long, Long> completedPerCourse = progressRepo
                .countCompletedPerCourse(userId, courseIds)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]));

        // Build summaries — one per course in courseIds order
        Map<Long, Course> courseMap = courseRepo.findAllById(courseIds)
                .stream()
                .collect(Collectors.toMap(Course::getId, c -> c));

        List<CourseSummary> summaries = courseIds.stream()
                .filter(courseMap::containsKey)
                .map(cid -> {
                    Course c = courseMap.get(cid);
                    int published  = publishedPerCourse.getOrDefault(cid, 0L).intValue();
                    int completed  = completedPerCourse.getOrDefault(cid, 0L).intValue();
                    int pct        = published == 0 ? 0 : (int) Math.min(100, (completed * 100L / published));
                    return new CourseSummary(
                            cid,
                            c.getTitle(),
                            c.getSlug(),
                            published,
                            completed,
                            pct);
                })
                .collect(Collectors.toList());

        return new DashboardResponse(
                user.getDisplayName(),
                totalXp,
                completedCount,
                summaries);
    }
}
