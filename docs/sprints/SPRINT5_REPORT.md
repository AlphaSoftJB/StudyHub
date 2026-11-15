# Sprint 5 Report — Learner Progress Tracking & Dashboard

**Branch:** `studyhub-dev`  
**Date:** 2026-11-15  
**Sprint goal:** Persistent lesson-completion tracking, XP ledger, and learner dashboard — both backend API and frontend UI.

---

## 1. What Was Built

### Backend

| File | Purpose |
|------|---------|
| `V11__create_progress_tables.sql` | `user_lesson_progress` + `user_xp` tables with FK constraints and unique key `uq_user_lesson(user_id, lesson_id)` |
| `progress/domain/LessonProgressStatus.java` | Enum: `IN_PROGRESS`, `COMPLETED` |
| `progress/domain/UserLessonProgress.java` | JPA entity; factory `startFor(user, lesson)`; idempotent domain method `complete(xp)` |
| `progress/domain/UserXp.java` | JPA entity using `@MapsId`; factory `createFor(user)`; `addXp(amount)` |
| `progress/repository/UserLessonProgressRepository.java` | CRUD + 5 aggregate JPQL queries (see §3) |
| `progress/repository/UserXpRepository.java` | `findByUserId` |
| `progress/dto/{LessonCompleteRequest,LessonCompleteResponse,ProgressSummaryResponse,DashboardResponse}.java` | Request/response records with `from()` factories |
| `progress/service/ProgressService.java` | `completeLesson`, `getMyProgress`, `getDashboard` |
| `progress/api/ProgressController.java` | `POST /v1/progress/lessons/{id}/complete`, `GET /v1/progress/me` |
| `progress/api/LearnerDashboardController.java` | `GET /v1/learner/dashboard` |

### Frontend

| File | Purpose |
|------|---------|
| `src/types/progress.ts` | TypeScript mirrors of all 5 backend DTOs |
| `src/api/progress.ts` | `progressApi.{completeLesson, getMyProgress, getDashboard}` |
| `src/hooks/useProgress.ts` | `useDashboard`, `useMyProgress`, `useLessonCompletion` |
| `src/pages/LearnerDashboard.tsx` | Stats tiles + per-course progress bars + empty state |
| `src/pages/LessonPage.tsx` | Added "✓ Mark as Complete" button with completion state |
| `src/App.tsx` | Added `/dashboard` protected route + Dashboard nav link |

---

## 2. Design Decisions Enforced

### DR-022 — Completion ≠ Mastery
`LessonCompleteResponse` deliberately has no mastery field.  
`useLessonCompletion` hook comment explicitly restates DR-022.  
Test `DR-022: LessonCompleteResponse has no mastery field` verifies structurally via reflection.

### DR-023 — GistSync Preserved
New progress API runs entirely in parallel. No localStorage, GistSync, or existing bootcamp code was touched. The existing bootcamp remains fully operational.

### DR-015-NATIVE — Pagination from Day One
All list endpoints accept `Pageable`. `getMyProgress` defaults `size=20`, `sort=updatedAt DESC`.

### Authorization Invariant
`userId` is extracted from the JWT principal inside every service method. No endpoint accepts a userId from the request body or query parameters — prevents cross-user data access.

### Idempotent Completion
`UserLessonProgress.complete(xp)` checks `status == COMPLETED` before mutating. The service computes `xpAwarded = progress.getXpAwarded() - xpBefore` to detect whether XP was just awarded, so the XP ledger is never incremented twice.

---

## 3. Repository Layer — Aggregate Queries

Three aggregate JPQL queries avoid N+1 for the dashboard:

```
findDistinctCourseIdsByUserId(userId)
  → SELECT m.course.id … GROUP BY m.course.id ORDER BY MAX(ulp.updatedAt) DESC

countPublishedLessonsPerCourse(courseIds)
  → SELECT m.course.id, COUNT(l.id) … WHERE l.contentStatus = PUBLISHED … GROUP BY m.course.id

countCompletedPerCourse(userId, courseIds)
  → SELECT m.course.id, COUNT(ulp.id) … WHERE ulp.status = 'COMPLETED' … GROUP BY m.course.id
```

Result sets are `List<Object[]>` → `Map<Long,Long>` via stream + `Collectors.toMap`. Completion percentage: `Math.min(100, completed * 100L / published)`.

---

## 4. Test Coverage

### Backend (written; run locally — Maven Central unreachable in sandbox)

**ProgressServiceTest.java** — 8 unit tests, Mockito, no Spring context:
- PS-01: First completion returns COMPLETED + correct XP
- PS-02: Idempotent — repeated completion does not re-award XP
- PS-03: 404 for non-published lesson
- PS-04: XP ledger created lazily on first completion
- PS-05: `getMyProgress` returns paged results for the correct user
- PS-06: Dashboard shows correct completion percentage (2/4 = 50%)
- PS-07: Dashboard returns empty courses list when no progress exists
- DR-022: Structural check — `LessonCompleteResponse` has no mastery field

**ProgressApiPathTest.java** — 6 `@WebMvcTest` tests:
- Unauthenticated → 401 (not 404) for all 3 endpoints
- Authenticated → 200 with correct JSON shapes for all 3 endpoints

### Frontend

42/42 Vitest tests pass — full regression including Auth, RequireAuth, Login, Register, CourseDetail, and all existing Sprint 4 tests.

Production build: `vite build` succeeds, 251 modules, 447 KB JS / 141 KB gzip.

---

## 5. Existing Bootcamp Protection Checklist

| Check | Result |
|-------|--------|
| Existing bootcamp affected? | **No** — no changes to any bootcamp file |
| Existing study functionality operational? | **Yes** — all 42 Vitest tests pass; GistSync untouched |
| Existing content or learner data modified? | **No** |
| Evidence from regression tests | 42/42 Vitest pass; production build clean |

The existing bootcamp at `index.html`, `ai.html`, `ibm.html`, `cyber.html` and all associated JavaScript data files remain completely unchanged. The new StudyHub platform continues to be developed in a separate frontend/backend codebase.

---

## 6. Security Checklist

| Item | Status |
|------|--------|
| Access tokens in localStorage? | ✗ Never — module-level variable in `client.ts` only |
| Refresh token security | Existing backend httpOnly cookie, unchanged |
| CSRF double-submit | Unchanged — POST to `/v1/progress/lessons/{id}/complete` goes through `apiClient` which sends `X-XSRF-TOKEN` |
| userId from request body/params? | ✗ Never — always from `@AuthenticationPrincipal JwtPrincipal` |
| AI-generated content auto-published? | N/A — progress tracking only |
| AI certifying mastery? | ✗ — DR-022 enforced; no mastery field |

---

## 7. Commits Pending

Changes are staged and ready to commit to `studyhub-dev`. Awaiting authorization per MANDATORY GIT VERSION CONTROL policy.

**Files changed:**
```
backend/studyhub-api/src/main/resources/db/migration/V11__create_progress_tables.sql
backend/studyhub-api/src/main/java/com/alphasoft/studyhub/progress/  (11 new files)
backend/studyhub-api/src/test/java/com/alphasoft/studyhub/progress/  (2 new files)
frontend/web/src/types/progress.ts
frontend/web/src/api/progress.ts
frontend/web/src/hooks/useProgress.ts
frontend/web/src/pages/LearnerDashboard.tsx
frontend/web/src/pages/LessonPage.tsx       (completion button added)
frontend/web/src/App.tsx                    (/dashboard route + nav link)
docs/sprints/SPRINT5_REPORT.md
```

**Not changed:** `main` branch, GitHub Pages, existing bootcamp files.
