package com.alphasoft.studyhub.curriculum.api;

import com.alphasoft.studyhub.curriculum.api.dto.CourseDetailDto;
import com.alphasoft.studyhub.curriculum.api.dto.LessonSummaryDto;
import com.alphasoft.studyhub.curriculum.api.dto.ModuleSummaryDto;
import com.alphasoft.studyhub.curriculum.domain.Course;
import com.alphasoft.studyhub.curriculum.domain.Module;
import com.alphasoft.studyhub.curriculum.service.CurriculumQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Course detail endpoint (Sprint 4 — FV-002 fix).
 *
 * <p>Resolves the missing route: learner navigates to /explore/:slug/courses/:id,
 * the frontend fetches GET /api/v1/courses/{id} here.
 *
 * <p>Returns course metadata plus its modules and their PUBLISHED lessons.
 * Unpublished (DRAFT, IN_REVIEW, APPROVED) lessons are excluded (DR-012).
 *
 * <p>Path convention: annotation uses the servlet path (without
 * {@code server.servlet.context-path=/api}). Effective production URL: /api/v1/courses/{id}.
 */
@RestController
@RequestMapping("/v1/courses")
public class CourseController {

    private final CurriculumQueryService queryService;

    public CourseController(CurriculumQueryService queryService) {
        this.queryService = queryService;
    }

    /**
     * GET /api/v1/courses/{id}
     *
     * Returns the course with all its modules and their published lessons.
     * Returns 404 when the course does not exist.
     *
     * Authentication: required (all curriculum endpoints, configured in SecurityConfig).
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseDetailDto> getCourse(@PathVariable Long id) {
        Optional<Course> courseOpt = queryService.getCourse(id);
        if (courseOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Course course = courseOpt.get();

        // Build module summaries with PUBLISHED lessons only (DR-012)
        List<ModuleSummaryDto> modules = queryService.getModulesByCourseId(id)
                .stream()
                .map(module -> buildModuleSummary(module))
                .toList();

        return ResponseEntity.ok(CourseDetailDto.from(course, modules));
    }

    private ModuleSummaryDto buildModuleSummary(Module module) {
        List<LessonSummaryDto> publishedLessons = queryService
                .getPublishedLessonsByModuleId(module.getId())
                .stream()
                .map(LessonSummaryDto::from)
                .toList();
        return ModuleSummaryDto.from(module, publishedLessons);
    }
}
