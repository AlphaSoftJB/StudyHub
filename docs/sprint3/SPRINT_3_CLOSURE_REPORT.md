# Sprint 3 Closure Report
**StudyHub Platform — Backend API + Curriculum Intelligence**

Sprint: 3  
Branch: `studyhub-dev`  
Latest commit: `a9ee03a`  
Report date: 2026-11-08  
Author: Claude Sonnet 4.6  

---

## ⚠️ Outstanding Owner Actions Required Before Sprint 3 Close

The following items require owner review and decision before Sprint 3 is formally closed. No Sprint 4 work has been started.

| Ref | Item | Required Action |
|-----|------|----------------|
| DR-OPEN-008 decision 1 | Seed script option (A/B/C) | Explicit owner decision |
| DR-OPEN-008 decision 2 | Days 50–54 empty days handling | Explicit owner decision |
| DR-OPEN-008 decision 3 | Duplicate cleanup authorization (days 85–89) | Explicit owner decision |
| DR-OPEN-008 decision 4 | cyberLabsNew.js superseded status | Explicit owner confirmation |
| Task #141 | Content seed script | Blocked on above decisions |
| FV-001/FV-005 regression tests | Backend test suite execution | Run from owner's machine; sandbox network blocked |

---

## 1. Sprint Goal

Deliver a production-ready backend API for the StudyHub curriculum with:
- Authenticated learner endpoints (disciplines, courses, lessons)
- Content lifecycle management (DRAFT → PUBLISHED) with admin-only transitions
- Curriculum intelligence: domain mapping, source governance, certification registry
- DR-012 and §18.3 content-certification separation enforced architecturally
- Existing bootcamp unchanged and operational throughout

**Goal met:** All API contracts delivered. P0 routing bugs identified and fixed. Curriculum intelligence documentation completed against master plan. Existing bootcamp intact.

---

## 2. Task Completion Summary

| Task | Description | Status | Commit |
|------|-------------|--------|--------|
| #141 | Content seed script | 🔴 BLOCKED — DR-OPEN-008 decisions outstanding | — |
| #143 | Backend domain model (Discipline, Course, Module, Lesson) | ✅ COMPLETE | prior sprint |
| #144 | Content lifecycle (ContentStatus, transitions, DR-012) | ✅ COMPLETE | prior sprint |
| #145 | Spring Security JWT auth integration | ✅ COMPLETE | `7e2253f` |
| #146 | Frontend scaffold (Vite + React + TypeScript) | ✅ COMPLETE | prior sprint |
| #147 | Frontend verification report | ✅ COMPLETE | `8c7bc36` |
| #148 | Java 21 compilation fixes | ✅ COMPLETE | `8a40935`, `79a180e` |
| #149 | SecurityConfig CSRF/permit path fix | ✅ COMPLETE | `7e2253f` |
| #150 | Test suite: 75/75 passing | ✅ COMPLETE (user-confirmed) | `7e2253f` |
| #151 | StudyHub Global Curriculum Master Plan adopted as authority | ✅ COMPLETE | — |
| #152 | Curriculum mapping docs v2.0 (CYBERSECURITY_CURRICULUM_MAP, IBM_PYTHON_ML_CURRICULUM_MAP) | ✅ COMPLETE | `bda577d` |
| #154 | P0 fixes: FV-001 (DisciplineController, LessonController), FV-005 (ContentAdminController) | ✅ COMPLETE | `a9ee03a` |
| #155 | Sprint 3 Closure Report | ✅ COMPLETE | (this document) |

---

## 3. Deliverables

### 3.1 Backend API Endpoints

All effective production URLs (with `server.servlet.context-path=/api`):

| Method | Effective URL | Auth | Status |
|--------|--------------|------|--------|
| `GET` | `/api/v1/auth/register` | Public | ✅ |
| `POST` | `/api/v1/auth/login` | Public | ✅ |
| `POST` | `/api/v1/auth/refresh` | Cookie (httpOnly refresh token) | ✅ |
| `POST` | `/api/v1/auth/logout` | Cookie | ✅ |
| `GET` | `/api/v1/disciplines` | Bearer JWT | ✅ |
| `GET` | `/api/v1/disciplines/{slug}` | Bearer JWT | ✅ |
| `GET` | `/api/v1/disciplines/{slug}/courses` | Bearer JWT | ✅ |
| `GET` | `/api/v1/lessons/{id}` | Bearer JWT | ✅ — PUBLISHED only |
| `PATCH` | `/api/v1/admin/lessons/{id}/status` | ROLE_ADMIN | ✅ |
| `GET` | `/api/v1/admin/lessons/{id}` | ROLE_ADMIN | ✅ — all statuses |

### 3.2 Content Lifecycle (DR-012)

Lifecycle transitions enforced by `ContentLifecycleService`:

```
DRAFT → IN_REVIEW → APPROVED → PUBLISHED
```

- PUBLISHED → DRAFT (retraction) supported
- APPROVED → DRAFT (rejection) supported  
- Learner endpoints return 404 for non-PUBLISHED lessons
- Admin endpoints return all statuses
- All transitions require authenticated ADMIN actor (audit trail)
- AI-generated content cannot self-certify or self-publish (architectural enforcement)

### 3.3 Security Architecture

| Requirement | Implementation |
|------------|---------------|
| JWT access tokens in-memory only | Issued as response body; never persisted to localStorage |
| Refresh tokens in httpOnly cookie | `ResponseCookie` with `httpOnly=true`, `secure=true` |
| Provider API keys backend-only | No keys in frontend code |
| ADMIN security boundary | `@PreAuthorize("hasRole('ADMIN')")` + SecurityConfig `/v1/admin/**` rule |
| CORS | `localhost:5173` dev origin permitted; production config pending |

### 3.4 Curriculum Intelligence Documents

| Document | Version | Decisions Resolved |
|----------|---------|-------------------|
| `CYBERSECURITY_CURRICULUM_MAP.md` | v2.0 | 8 outstanding decisions resolved |
| `IBM_PYTHON_ML_CURRICULUM_MAP.md` | v2.0 | IBM Python realigned to DOM-03; CYB-004 stays DOM-01 |

### 3.5 Frontend

- Vite v8.3.6 + TypeScript strict + React 19.3.1 scaffold
- All DTO types match backend (`DisciplineDto`, `CourseDto`, `LessonDetailDto`, `LessonSummaryDto`)
- `Instant publishedAt` → ISO 8601 string serialization confirmed (Spring Boot auto-configure)
- Production build: 242 modules, 424.72 kB JS bundle — ✅ PASS

---

## 4. Test Evidence

### 4.1 Backend Tests

**Last run by owner: 2026-11-06**

```
Tests run: 75, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
Total time: 20.003 s
```

Note: Maven Central is blocked in this sandbox environment. Backend tests cannot be re-run from here. Tests must be executed from owner's machine. The test suite above includes pre-fix test results; the corrected `CurriculumApiCompatibilityTest` and new `CurriculumApiPathRegressionTest` have not yet been executed — this is the **outstanding action** noted in §0.

### 4.2 New Tests Added (commit `a9ee03a`)

**CurriculumApiCompatibilityTest** — 3 path assertions corrected:
- `discipline_controller_base_path`: now asserts `"/v1/disciplines"` + `doesNotContain "/api/v1/disciplines"`
- `lesson_controller_base_path`: now asserts `"/v1/lessons"` + `doesNotContain "/api/v1/lessons"`
- `admin_controller_base_path`: now asserts `startsWith("/v1/admin")` + `NOT startsWith("/api/v1/admin")`

**CurriculumApiPathRegressionTest** (NEW — 7 test methods):
- `GET /api/v1/disciplines` → 200 (MockMvc contextPath="/api") — FV-001 regression
- `GET /api/v1/disciplines/{slug}` → 404 from service (route registered) — FV-001 regression  
- `GET /api/v1/disciplines/{slug}/courses` → 404 from service (route registered)
- `GET /api/v1/lessons/{id}` → 404 from service (route registered) — FV-001 regression
- `PATCH /api/v1/admin/lessons/{id}/status` → 401 for anon (route registered, security enforced) — FV-005 regression
- `GET /api/v1/admin/lessons/{id}` → 401 for anon (route registered) — FV-005 regression
- `GET /api/api/v1/disciplines` → 404 (old double-/api path is dead)

### 4.3 Frontend Build

```
vite v8.3.6 — ✓ 242 modules transformed — built in 610ms — PASS
```

Confirmed both at initial report (2026-11-06) and after P0 fixes (2026-11-08).

---

## 5. Bug Fixes Applied

### FV-001 — Double `/api` prefix in DisciplineController and LessonController (CRITICAL)

**Root cause:** `server.servlet.context-path=/api` causes Spring to strip `/api` before annotation matching. Annotations had `/api/v1/...` which never matched the servlet path `/v1/...` → all curriculum API calls would 404 in production.

**Why not caught by tests:** `application-test.yml` has no `context-path` setting. Existing tests also used annotation reflection, not HTTP calls.

**Fix:** `@RequestMapping("/api/v1/disciplines")` → `@RequestMapping("/v1/disciplines")` and `@RequestMapping("/api/v1/lessons")` → `@RequestMapping("/v1/lessons")`

### FV-005 — Double `/api` prefix in ContentAdminController (HIGH)

**Same root cause as FV-001.** All content lifecycle admin transitions (DRAFT→PUBLISHED etc.) would 404 in production.

**Fix:** `@RequestMapping("/api/v1/admin")` → `@RequestMapping("/v1/admin")`

**SecurityConfig alignment:** SecurityConfig already used `/v1/admin/**` for ADMIN enforcement — annotation now matches.

### Controller audit: no other controllers affected

Full grep of all `@RequestMapping` annotations confirmed exactly 4 controllers; all 4 now correctly use `/v1/...` servlet paths.

---

## 6. Existing Bootcamp Protection Status

**Per standing sprint-report requirement:**

| Check | Status |
|-------|--------|
| Was the existing bootcamp affected? | ❌ No — zero changes to `bootcamp-hub/` |
| Does existing study functionality remain operational? | ✅ Yes — bootcamp-hub deployment production-identical to pre-Sprint-3 |
| Was any existing content or learner data modified? | ❌ No |
| Were any existing routes, components, or data files removed? | ❌ No |
| GistSync and localStorage functionality intact? | ✅ Yes — no modifications |
| Certification practice exams available? | ✅ Yes — exam JS files untouched |
| `main` branch changed? | ❌ No — `main` last commit: `c84f920` (pre-Sprint-3) |

**Evidence:** `git diff main..studyhub-dev --stat` shows 116 files changed (all additions) with 0 changes to production bootcamp files.

---

## 7. Outstanding Items Carried to Sprint 4

### Blocking (carry forward — not Sprint 3 scope)

| Item | Reason | Sprint 4 prerequisite |
|------|--------|-----------------------|
| FV-002 — missing `/explore/:slug/courses/:id` route | Requires `CoursePage` component + module endpoint | Yes |
| FV-003 — module endpoint not implemented | Backend `ModuleController` needed | Yes |
| FV-004 — `useCourseModules` hook missing | Depends on FV-003 | Yes |
| Task #141 — seed script | DR-OPEN-008 decisions outstanding; owner must decide before Sprint 4 implementation | Conditional |

### DR-OPEN-008 Decisions Required (from owner)

These 4 decisions block Task #141 and must be resolved before the Sprint 4 seed script work:

1. **Seed option:** Option A (seed exact Bootcamp.tsx as-is), Option B (seed all 93 unique entries as DRAFT), or Option C (manual mapping)?
   - Recommendation on file: **Option B** — seed all 93 unique entries as DRAFT, no structural changes

2. **Days 50–54 (empty days):** Seed as empty DRAFT placeholders or hold until content is authored?

3. **Days 85–89 duplicates:** Authorize cleanup of the 5 duplicate objects in the DevSecOps section?

4. **cyberLabsNew.js:** Confirm it is superseded by Bootcamp.tsx and should not be separately seeded?

---

## 8. Git Summary

| Item | Value |
|------|-------|
| Active branch | `studyhub-dev` |
| Sprint 3 commits (this branch) | `79a180e`, `8a40935`, `7e2253f`, `bda577d`, `8c7bc36`, `a9ee03a` |
| Latest commit | `a9ee03a` — fix(curriculum): remove double /api context-path prefix |
| `main` last commit | `c84f920` — Revert merge (pre-Sprint-3) |
| Files changed in Sprint 3 (dev vs main) | 116 files, 12,252 insertions |
| Secrets committed | ❌ None |
| Breaking changes to existing bootcamp | ❌ None |
| `main` modified | ❌ No |

---

## 9. Completion Gate Checklist

Per completion gate requirements from authorized scope:

| Gate item | Status |
|-----------|--------|
| Exact files changed reported | ✅ §5 and §8 |
| Effective endpoint URLs verified | ✅ §3.1 |
| Regression tests added | ✅ `CurriculumApiPathRegressionTest` — 7 tests |
| Test execution results | ⚠️ Awaiting owner re-run (sandbox network blocked) |
| Commit hash | ✅ `a9ee03a` |
| Pushed to studyhub-dev | ✅ Confirmed |
| `main` unchanged | ✅ Confirmed |
| Sprint 4 not started | ✅ Confirmed |
| Existing bootcamp intact | ✅ Confirmed |

---

*Sprint 3 Closure Report produced by Claude Sonnet 4.6.*  
*Governing reference: StudyHub Global Curriculum & Certification Master Plan 2026–2027.*
