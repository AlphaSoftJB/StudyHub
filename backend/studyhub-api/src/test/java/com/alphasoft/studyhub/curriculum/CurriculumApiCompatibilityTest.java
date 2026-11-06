package com.alphasoft.studyhub.curriculum;

import com.alphasoft.studyhub.curriculum.api.ContentAdminController;
import com.alphasoft.studyhub.curriculum.api.DisciplineController;
import com.alphasoft.studyhub.curriculum.api.LessonController;
import com.alphasoft.studyhub.curriculum.api.dto.CourseDto;
import com.alphasoft.studyhub.curriculum.api.dto.DisciplineDto;
import com.alphasoft.studyhub.curriculum.api.dto.LessonDetailDto;
import com.alphasoft.studyhub.curriculum.api.dto.LessonSummaryDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Sprint 3 — DR-015-NATIVE API-first compatibility assertions.
 *
 * These tests do NOT perform HTTP calls; they verify the structural
 * shape of the Content API without requiring a running network:
 *
 *  1. Controller beans load in the Spring context.
 *  2. Expected REST endpoints are present (annotation-based inspection).
 *  3. DTOs are Java records (safe to serialize; no inheritance coupling).
 *  4. DTO fields cover the minimum native-client contract.
 *  5. Admin endpoints are distinct from learner endpoints (security boundary).
 *
 * Full HTTP contract tests (MockMvc / @WebMvcTest) are deferred to
 * deployment environment where Spring Security test dependencies are available.
 */
@SpringBootTest
@ActiveProfiles("test")
class CurriculumApiCompatibilityTest {

    @Autowired private DisciplineController   disciplineController;
    @Autowired private LessonController       lessonController;
    @Autowired private ContentAdminController contentAdminController;

    // ── Bean wiring ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("DisciplineController, LessonController and ContentAdminController beans load")
    void all_curriculum_controllers_load() {
        assertThat(disciplineController).isNotNull();
        assertThat(lessonController).isNotNull();
        assertThat(contentAdminController).isNotNull();
    }

    // ── Base path assertions ──────────────────────────────────────────────────

    @Test
    @DisplayName("DisciplineController is mapped to /api/v1/disciplines")
    void discipline_controller_base_path() {
        RequestMapping rm = DisciplineController.class.getAnnotation(RequestMapping.class);
        assertThat(rm).isNotNull();
        assertThat(rm.value()).contains("/api/v1/disciplines");
    }

    @Test
    @DisplayName("LessonController is mapped to /api/v1/lessons")
    void lesson_controller_base_path() {
        RequestMapping rm = LessonController.class.getAnnotation(RequestMapping.class);
        assertThat(rm).isNotNull();
        assertThat(rm.value()).contains("/api/v1/lessons");
    }

    @Test
    @DisplayName("ContentAdminController is mapped to /api/v1/admin")
    void admin_controller_base_path() {
        RequestMapping rm = ContentAdminController.class.getAnnotation(RequestMapping.class);
        assertThat(rm).isNotNull();
        assertThat(Arrays.stream(rm.value()).anyMatch(v -> v.startsWith("/api/v1/admin")))
            .as("Admin controller must be under /api/v1/admin/* to enforce security boundary")
            .isTrue();
    }

    // ── Endpoint presence ─────────────────────────────────────────────────────

    @Test
    @DisplayName("DisciplineController exposes GET / (list) and GET /{slug}/courses")
    void discipline_controller_has_required_endpoints() {
        Class<?> cls = DisciplineController.class;
        boolean hasListEndpoint = hasGetMapping(cls, "listDisciplines");
        boolean hasCoursesEndpoint = hasGetMapping(cls, "getCourses");
        assertThat(hasListEndpoint).as("listDisciplines GET endpoint must exist").isTrue();
        assertThat(hasCoursesEndpoint).as("getCourses GET endpoint must exist").isTrue();
    }

    @Test
    @DisplayName("ContentAdminController exposes PATCH status transition endpoint")
    void admin_controller_has_patch_status_endpoint() {
        boolean hasPatch = Arrays.stream(ContentAdminController.class.getDeclaredMethods())
            .anyMatch(m -> m.isAnnotationPresent(PatchMapping.class));
        assertThat(hasPatch)
            .as("ContentAdminController must have a PATCH endpoint for lifecycle transitions")
            .isTrue();
    }

    // ── DTO shape (DR-015-NATIVE) ─────────────────────────────────────────────

    @Test
    @DisplayName("DisciplineDto is a Java record — safe for JSON serialization")
    void discipline_dto_is_record() {
        assertThat(DisciplineDto.class.isRecord()).isTrue();
    }

    @Test
    @DisplayName("CourseDto is a Java record")
    void course_dto_is_record() {
        assertThat(CourseDto.class.isRecord()).isTrue();
    }

    @Test
    @DisplayName("LessonSummaryDto is a Java record")
    void lesson_summary_dto_is_record() {
        assertThat(LessonSummaryDto.class.isRecord()).isTrue();
    }

    @Test
    @DisplayName("LessonDetailDto is a Java record")
    void lesson_detail_dto_is_record() {
        assertThat(LessonDetailDto.class.isRecord()).isTrue();
    }

    @Test
    @DisplayName("DisciplineDto has required native-client fields: id, name, slug, description")
    void discipline_dto_has_minimum_fields() {
        var components = Arrays.stream(DisciplineDto.class.getRecordComponents())
            .map(rc -> rc.getName())
            .toList();
        assertThat(components).contains("id", "name", "slug", "description");
    }

    @Test
    @DisplayName("LessonDetailDto has contentBody field — required for lesson viewer")
    void lesson_detail_dto_has_content_body() {
        var components = Arrays.stream(LessonDetailDto.class.getRecordComponents())
            .map(rc -> rc.getName())
            .toList();
        assertThat(components)
            .as("LessonDetailDto must include contentBody for the lesson view")
            .contains("contentBody");
    }

    @Test
    @DisplayName("LessonSummaryDto does NOT have contentBody — lightweight listing DTO")
    void lesson_summary_dto_excludes_content_body() {
        var components = Arrays.stream(LessonSummaryDto.class.getRecordComponents())
            .map(rc -> rc.getName())
            .toList();
        assertThat(components)
            .as("LessonSummaryDto must not include contentBody (too heavy for listings)")
            .doesNotContain("contentBody");
    }

    // ── Controllers are @RestController (not @Controller) ────────────────────

    @Test
    @DisplayName("All curriculum controllers are @RestController — no HTML view resolution")
    void controllers_are_rest_controllers() {
        assertThat(DisciplineController.class.isAnnotationPresent(RestController.class)).isTrue();
        assertThat(LessonController.class.isAnnotationPresent(RestController.class)).isTrue();
        assertThat(ContentAdminController.class.isAnnotationPresent(RestController.class)).isTrue();
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private boolean hasGetMapping(Class<?> cls, String methodName) {
        return Arrays.stream(cls.getDeclaredMethods())
            .filter(m -> m.getName().equals(methodName))
            .anyMatch(m -> m.isAnnotationPresent(GetMapping.class));
    }
}
