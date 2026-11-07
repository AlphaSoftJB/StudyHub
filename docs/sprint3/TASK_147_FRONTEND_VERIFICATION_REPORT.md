# Task #147 — Frontend Verification Report
**Sprint 3**

Date: 2026-11-06  
Branch: `studyhub-dev` (commit `bda577d`)  
Scope: Verify frontend integration against backend API; identify discrepancies; confirm existing bootcamp intact

---

## Summary

| Area | Status |
|------|--------|
| Frontend TypeScript build | ✅ PASS — `tsc -b && vite build` succeeds, 0 errors |
| Frontend type coverage | ✅ PASS — DTO fields match frontend types |
| Backend tests (last run by user) | ✅ PASS — 75 tests, 0 failures, 0 errors |
| Existing bootcamp (bootcamp-hub) | ✅ INTACT — Bootcamp.tsx present and unmodified (1,533,649 bytes) |
| `main` branch unchanged | ✅ CONFIRMED — no changes pushed to production |
| **API route mapping (production)** | 🔴 **DEFECT FV-001** — curriculum controllers will return 404 in production |
| Course detail route | 🔴 **DEFECT FV-002** — `/explore/:slug/courses/:id` has no route handler |
| Module-level navigation | 🟡 **GAP FV-003** — module endpoint deferred; breadcrumb placeholder present |
| `useCourseModules` hook | 🟡 **GAP FV-004** — no hook exists for module listing (by design — deferred) |
| `ContentAdminController` path | 🔴 **DEFECT FV-005** — same double-/api bug as FV-001 |

**Blocking defects before any production deployment:** FV-001, FV-002, FV-005.  
**Non-blocking gaps (by design — deferred to Sprint 4):** FV-003, FV-004.

---

## 1. Frontend Build Verification

```
> web@0.0.0 build
> tsc -b && vite build

vite v8.3.6 building client environment for production...
✓ 242 modules transformed.
dist/index.html                   0.45 kB
dist/assets/index-DGNrK5qb.css    1.78 kB
dist/assets/index-BzDIj6R9.js   424.72 kB

✓ built in 701ms
```

**Result: PASS.** TypeScript strict-mode type check and Vite production build both succeed. No compilation errors, no type errors.

---

## 2. DTO Field Mapping — Frontend vs. Backend

### 2.1 Discipline

| Frontend (`curriculum.ts`) | Backend (`DisciplineDto.java`) | Match |
|---------------------------|-------------------------------|-------|
| `id: number` | `Long id` | ✅ |
| `name: string` | `String name` | ✅ |
| `slug: string` | `String slug` | ✅ |
| `description: string \| null` | `String description` (nullable) | ✅ |
| `icon: string \| null` | `String icon` (nullable) | ✅ |
| `sequenceOrder: number` | `int sequenceOrder` | ✅ |

### 2.2 Course

| Frontend (`curriculum.ts`) | Backend (`CourseDto.java`) | Match |
|---------------------------|--------------------------|-------|
| `id: number` | `Long id` | ✅ |
| `disciplineId: number` | `Long disciplineId` | ✅ |
| `title: string` | `String title` | ✅ |
| `slug: string` | `String slug` | ✅ |
| `description: string \| null` | `String description` (nullable) | ✅ |
| `sequenceOrder: number` | `int sequenceOrder` | ✅ |

### 2.3 LessonDetail

| Frontend (`curriculum.ts`) | Backend (`LessonDetailDto.java`) | Match |
|---------------------------|--------------------------------|-------|
| `id: number` | `Long id` | ✅ |
| `moduleId: number` | `Long moduleId` | ✅ |
| `title: string` | `String title` | ✅ |
| `lessonType: LessonType` | `LessonType lessonType` | ✅ |
| `contentStatus: ContentStatus` | `ContentStatus contentStatus` | ✅ |
| `contentBody: string \| null` | `String contentBody` (nullable) | ✅ |
| `sequenceOrder: number` | `int sequenceOrder` | ✅ |
| `publishedAt: string \| null` | `Instant publishedAt` | ✅ (Spring Boot auto-serialize: Instant → ISO 8601 string via JavaTimeModule) |

### 2.4 LessonSummary

| Frontend (`curriculum.ts`) | Backend (`LessonSummaryDto.java`) | Match |
|---------------------------|---------------------------------|-------|
| `id: number` | `Long id` | ✅ |
| `moduleId: number` | `Long moduleId` | ✅ |
| `title: string` | `String title` | ✅ |
| `lessonType: LessonType` | `LessonType lessonType` | ✅ |
| `sequenceOrder: number` | `int sequenceOrder` | ✅ |
| _(no contentBody)_ | _(no contentBody)_ | ✅ |

**All DTO fields: MATCH.** No serialization mismatches. Spring Boot 3.x auto-registers `JavaTimeModule` and serializes `Instant` as ISO 8601 string.

---

## 3. API Route Contract Analysis

### 3.1 Frontend URL Calls

| Call | URL constructed |
|------|----------------|
| `curriculumApi.getDisciplines()` | `http://localhost:8080/api/v1/disciplines` |
| `curriculumApi.getDiscipline(slug)` | `http://localhost:8080/api/v1/disciplines/{slug}` |
| `curriculumApi.getCourses(slug)` | `http://localhost:8080/api/v1/disciplines/{slug}/courses` |
| `curriculumApi.getLesson(id)` | `http://localhost:8080/api/v1/lessons/{id}` |

(VITE_API_URL defaults to `http://localhost:8080`; Vite proxy at dev-time passes `/api/*` through to `:8080` unchanged.)

### 3.2 Backend Effective URLs (Production)

```
server.servlet.context-path = /api
```

Spring Security and Spring MVC both match against the **servlet path** (post-context-path stripping). The `/api` prefix is stripped before annotation matching.

| Controller | `@RequestMapping` annotation | Spring strips `/api` → servlet path matched | Effective URL served |
|------------|------------------------------|---------------------------------------------|---------------------|
| `AuthController` | `/v1/auth` | `/v1/auth` | `/api/v1/auth` ✅ |
| `DisciplineController` | `/api/v1/disciplines` | `/v1/disciplines` ← annotation `/api/v1/disciplines` **NO MATCH** | 404 🔴 |
| `LessonController` | `/api/v1/lessons` | `/v1/lessons` ← annotation `/api/v1/lessons` **NO MATCH** | 404 🔴 |
| `ContentAdminController` | `/api/v1/admin` | `/v1/admin` ← annotation `/api/v1/admin` **NO MATCH** | 404 🔴 |

**Root cause:** `AuthController` was fixed in commit `7e2253f` (SecurityConfig CSRF paths) and uses the correct `/v1/auth` pattern. The three curriculum controllers were written with `/api/` in the annotation, which duplicates the context-path prefix and causes a 404 in production.

**Why tests pass:** `application-test.yml` has **no `context-path`**, so in the test profile `/api/v1/disciplines` is a valid servlet path and the annotation matches. `CurriculumApiCompatibilityTest` asserts the annotation value directly, not the HTTP response — so it passes without catching the runtime URL mismatch.

---

## 4. Defect Register

### 🔴 FV-001 — Double `/api` prefix in curriculum controllers (BLOCKING)

**Severity:** Critical — prevents all curriculum API calls in production  
**Affected files:**
- `backend/studyhub-api/src/main/java/com/alphasoft/studyhub/curriculum/api/DisciplineController.java`
- `backend/studyhub-api/src/main/java/com/alphasoft/studyhub/curriculum/api/LessonController.java`

**Reproduction:** Deploy with `server.servlet.context-path=/api`. `GET /api/v1/disciplines` → 404.

**Fix required:**

```java
// DisciplineController — change:
@RequestMapping("/api/v1/disciplines")
// to:
@RequestMapping("/v1/disciplines")

// LessonController — change:
@RequestMapping("/api/v1/lessons")
// to:
@RequestMapping("/v1/lessons")
```

**Also fix `CurriculumApiCompatibilityTest` path assertions:**
```java
// discipline_controller_base_path():
assertThat(rm.value()).contains("/v1/disciplines");   // was "/api/v1/disciplines"

// lesson_controller_base_path():
assertThat(rm.value()).contains("/v1/lessons");        // was "/api/v1/lessons"
```

**Precedent:** `AuthController` uses `/v1/auth` (correct); comment in that file even documents the context-path pattern. Curriculum controllers must follow the same convention.

---

### 🔴 FV-002 — Missing course detail route in React Router (BLOCKING)

**Severity:** High — clicking "View course →" from the discipline page navigates to a NotFound page

**Affected file:** `frontend/web/src/App.tsx`

**Reproduction:** Navigate to `/explore/cybersecurity` → click "View course →" on any course card → lands on NotFound (the route `/explore/:slug/courses/:id` is not registered).

**Root cause:** `ExplorePage.tsx` `CourseRow` component links to:
```
/explore/${disciplineSlug}/courses/${course.id}
```
But `App.tsx` registers no route for this pattern:
```jsx
<Route path="/explore" element={<ExploreListPage />} />
<Route path="/explore/:slug" element={<ExploreDisciplinePage />} />
// ← /explore/:slug/courses/:id is MISSING
```

**Fix required:** Add a `CoursePage` component and route:
```jsx
<Route path="/explore/:slug/courses/:id" element={<CoursePage />} />
```
The `CoursePage` would fetch modules/lessons for the course (blocked on FV-003 — module endpoint not yet implemented). Acceptable Sprint 4 scope if course navigation is not needed for acceptance testing.

---

### 🔴 FV-005 — Double `/api` prefix in ContentAdminController (BLOCKING for admin ops)

**Severity:** High — all content lifecycle transitions (DRAFT → PUBLISHED) will 404 in production

**Affected file:**
- `backend/studyhub-api/src/main/java/com/alphasoft/studyhub/curriculum/api/ContentAdminController.java`

**Fix required:**
```java
// Change:
@RequestMapping("/api/v1/admin")
// To:
@RequestMapping("/v1/admin")
```

**SecurityConfig** already correctly uses `/v1/admin/**` for `hasRole("ADMIN")` matching — no change needed there.

**Also fix `CurriculumApiCompatibilityTest`:**
```java
// admin_controller_base_path():
assertThat(Arrays.stream(rm.value()).anyMatch(v -> v.startsWith("/v1/admin")))  // was "/api/v1/admin"
```

---

### 🟡 FV-003 — Module endpoint not implemented (Non-blocking — by design)

**Severity:** Medium — no module-level navigation; breadcrumb on LessonPage is a placeholder

**Evidence:** `LessonPage.tsx` comment:
```jsx
{/* Breadcrumb placeholder — real nav wired after modules endpoint exists */}
```

**Expected endpoint (not yet built):**
- `GET /api/v1/disciplines/{slug}/courses/{courseId}/modules` → module listing with lessons
- Or `GET /api/v1/courses/{id}/modules`

**Action:** Deferred to Sprint 4. Record as Sprint 4 backlog item. Not a blocker for Sprint 3 closure.

---

### 🟡 FV-004 — No `useCourseModules` hook (Non-blocking — by design)

**Severity:** Low — `LessonSummary` frontend type exists, hook not needed until module endpoint exists

**Evidence:** `curriculum.ts` defines `LessonSummary` interface but `useCurriculum.ts` has no corresponding hook.

**Action:** Add `useCourseModules(courseId)` hook in Sprint 4 when module endpoint is built. No Sprint 3 action needed.

---

## 5. Existing Bootcamp Verification

**The existing bootcamp is confirmed intact and unaffected by Sprint 3 work.**

| Check | Evidence |
|-------|----------|
| `Bootcamp.tsx` present and unmodified | 1,533,649 bytes — matches prior inspection |
| `bootcamp-hub/` directory structure intact | `index.html`, `assets/`, `docs/` present |
| `main` branch unchanged | Last commit on `main`: `c84f920` (Revert merge) — no Sprint 3 changes pushed to production |
| No routes/components removed from bootcamp | Confirmed — no edits to `bootcamp-hub/` this session or prior Sprint 3 sessions |
| Learner localStorage functionality | Not modified — no changes to bootcamp data files |
| GistSync | Not modified |
| Certification practice exams | Available — exam JS files in `bootcamp-hub/` are untouched |

---

## 6. Sprint Bootcamp Protection Status

Per standing sprint-report requirement:

- **Was the existing bootcamp affected?** No.
- **Does existing study functionality remain operational?** Yes — bootcamp-hub deployment is production-identical to pre-Sprint-3.
- **Was any existing content or learner data modified?** No.
- **Evidence from regression tests:** Frontend build passes. Backend 75/75 tests pass (user confirmed). No changes to any file under `bootcamp-hub/`.

---

## 7. Recommended Next Actions

### Before Production Deployment (Sprint 4 prerequisite)

| Priority | Action | File | Type |
|----------|--------|------|------|
| P0 | Fix `@RequestMapping` in DisciplineController | `DisciplineController.java` | Bug fix — 1 line |
| P0 | Fix `@RequestMapping` in LessonController | `LessonController.java` | Bug fix — 1 line |
| P0 | Fix `@RequestMapping` in ContentAdminController | `ContentAdminController.java` | Bug fix — 1 line |
| P0 | Update `CurriculumApiCompatibilityTest` path assertions | `CurriculumApiCompatibilityTest.java` | Test fix — 3 lines |
| P1 | Add `/explore/:slug/courses/:id` route + CoursePage | `App.tsx` + new `CoursePage.tsx` | Feature — Sprint 4 |
| P2 | Implement module listing endpoint | New `ModuleController.java` | Feature — Sprint 4 |
| P2 | Add `useCourseModules` hook | `useCurriculum.ts` | Feature — Sprint 4 |

### Immediate (Sprint 3 closure scope — owner approval required)

The P0 bugs (FV-001, FV-005) are single-line annotation changes plus test updates. They are low-risk, high-value fixes that should be applied before Sprint 3 is considered closed. Requesting owner authorization to apply these 4 fixes and re-run the test suite.

---

## 8. Git State

| Item | Value |
|------|-------|
| Active branch | `studyhub-dev` |
| Latest commit | `bda577d` — docs(curriculum): update mapping docs to v2.0 per master plan |
| `main` last commit | `c84f920` — Revert merge (pre-Sprint-3 state) |
| Uncommitted changes | None |
| Files changed in Sprint 3 (dev branch) | 114 files, 11,618 insertions |

---

*Report produced by Claude Sonnet 4.6, Sprint 3, Task #147.*
