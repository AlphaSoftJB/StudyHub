package com.alphasoft.studyhub.curriculum.api;

import com.alphasoft.studyhub.curriculum.api.dto.LessonDetailDto;
import com.alphasoft.studyhub.curriculum.service.CurriculumQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Learner-facing lesson fetch.
 * Returns 404 for any lesson that is not PUBLISHED (DR-012).
 *
 * <p>Path convention: annotation uses the servlet path (without
 * {@code server.servlet.context-path=/api}). Effective production URL: /api/v1/lessons.
 */
@RestController
@RequestMapping("/v1/lessons")
public class LessonController {

    private final CurriculumQueryService queryService;

    public LessonController(CurriculumQueryService queryService) {
        this.queryService = queryService;
    }

    /**
     * GET /api/v1/lessons/{id}
     * (Servlet path: /v1/lessons/{id}; context-path /api prepended by container.)
     * Returns 404 when id doesn't exist or lesson is not PUBLISHED.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LessonDetailDto> getLesson(@PathVariable Long id) {
        return queryService.getPublishedLesson(id)
                           .map(LessonDetailDto::from)
                           .map(ResponseEntity::ok)
                           .orElse(ResponseEntity.notFound().build());
    }
}
