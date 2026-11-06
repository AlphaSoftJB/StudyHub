package com.alphasoft.studyhub.curriculum.api;

import com.alphasoft.studyhub.curriculum.api.dto.CourseDto;
import com.alphasoft.studyhub.curriculum.api.dto.DisciplineDto;
import com.alphasoft.studyhub.curriculum.service.CurriculumQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Public (authenticated) curriculum browse endpoints.
 *
 * <p>All responses contain only PUBLISHED content (enforced in service layer).
 * No admin transitions here — those live in {@link ContentAdminController}.
 *
 * <p>DR-015-NATIVE: no web-only response shapes; usable by iOS/Android clients.
 */
@RestController
@RequestMapping("/api/v1/disciplines")
public class DisciplineController {

    private final CurriculumQueryService queryService;

    public DisciplineController(CurriculumQueryService queryService) {
        this.queryService = queryService;
    }

    /**
     * GET /api/v1/disciplines — Explore page: list of active disciplines.
     */
    @GetMapping
    public List<DisciplineDto> listDisciplines() {
        return queryService.getActiveDisciplines()
                           .stream()
                           .map(DisciplineDto::from)
                           .toList();
    }

    /**
     * GET /api/v1/disciplines/{slug} — single discipline by slug.
     */
    @GetMapping("/{slug}")
    public ResponseEntity<DisciplineDto> getDiscipline(@PathVariable String slug) {
        return queryService.getDisciplineBySlug(slug)
                           .map(DisciplineDto::from)
                           .map(ResponseEntity::ok)
                           .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/v1/disciplines/{slug}/courses — courses for a discipline.
     */
    @GetMapping("/{slug}/courses")
    public ResponseEntity<List<CourseDto>> getCourses(@PathVariable String slug) {
        // 404 if discipline slug doesn't exist / is inactive
        return queryService.getDisciplineBySlug(slug)
                           .map(d -> queryService.getCoursesByDisciplineSlug(slug)
                                                 .stream()
                                                 .map(CourseDto::from)
                                                 .toList())
                           .map(ResponseEntity::ok)
                           .orElse(ResponseEntity.notFound().build());
    }
}
