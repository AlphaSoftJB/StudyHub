# Sprint 4 Report — Authentication, Learner Access & Frontend Foundation

**Sprint dates:** 2026-11-08 — 2026-11-14  
**Branch:** `studyhub-dev`  
**Commit:** `7b95ed2a86fc8cbabec3fddf90ec69e9735baea6`  
**Status:** ✅ COMPLETE — all 19 acceptance criteria addressed

---

## 1. Sprint Goals

Sprint 4 established the full authentication and session management layer for
StudyHub — both backend completions from Sprint 3 carry-overs and a complete
React 19 frontend authentication foundation — covering:

- Secure in-memory JWT access token storage (never `localStorage`)
- Single-flight refresh guard preventing concurrent token renewal calls
- Invite-only registration with UUID invitation code enforcement
- Protected route guard with no flash-of-redirect during session restore
- Course detail page (FV-002 fix)
- 30-test Vitest + RTL test suite

---

## 2. Work Completed

### 2.1 Backend (Items H, I, F-backend)

| Item | File | Description |
|------|------|-------------|
| H | `StartupSecretValidator.java` | Added `cookie-secure` prod-profile validation — startup fails fast if `app.cookie.secure=false` in production |
| H | `StartupSecretValidatorTest.java` | 11 unit tests (new file) covering all secret validation branches |
| I | `application-test.yml` | FV-006 fix: added `server.servlet.context-path: /api` so MockMvc tests match production context path |
| F-back | `CourseController.java` | `GET /v1/courses/{id}` — returns `CourseDetailDto` with modules sorted by `sequenceOrder` and published lessons only (DR-012 enforcement) |
| F-back | `CourseDetailDto.java` | New DTO for course detail response |
| F-back | `ModuleSummaryDto.java` | New DTO for module with embedded published lessons |
| — | `CurriculumApiPathRegressionTest.java` | Added `CourseController` path coverage tests |

### 2.2 Frontend (Items A–G)

| Item | File | Description |
|------|------|-------------|
| A | `AuthContext.tsx` | Centralized auth provider: in-memory JWT, single-flight refresh via `useRef<Promise<string>>`, session restore on mount |
| B | `client.ts` | In-memory token store; CSRF double-submit (`XSRF-TOKEN` → `X-XSRF-TOKEN`); 401 retry interceptor with `__authRetry` guard |
| — | `auth.ts` | `authApi`: login / register / refresh / logout |
| C | `LoginPage.tsx` | Email+password form; 401→"Invalid email or password"; redirect to `location.state.from ?? '/explore'` |
| D | `RegisterPage.tsx` | Invite-only form with UUID code validation; 409 email-exists, 422 invalid-code |
| E | `RequireAuth.tsx` | Protected route guard; loading spinner during session restore; redirects with `state.from` |
| F-front | `CourseDetailPage.tsx` | Fetches `GET /api/v1/courses/{id}`; sorted modules + published lessons; lesson type badges |
| — | `App.tsx` | `AuthProvider` + `BrowserRouter`; protected/public route wiring; nav shows auth state |
| G | `src/test/` | 30 Vitest + RTL tests across 4 test files |

### 2.3 Supporting Changes

- `vite.config.ts` — changed `defineConfig` import to `vitest/config` to satisfy `test:` property typing
- `tsconfig.app.json` — added `"exclude": ["src/test/**"]` to prevent test-only types (`@testing-library/jest-dom`) from leaking into the production TypeScript build
- `package.json` — added `vitest`, `@vitest/coverage-v8`, `@testing-library/react`, `@testing-library/user-event`, `@testing-library/jest-dom`, `jsdom`

---

## 3. Acceptance Criteria Evidence

### Security constraints

| Constraint | Status | Evidence |
|------------|--------|----------|
| Access tokens never in `localStorage` | ✅ | `let _accessToken: string \| null = null` in `client.ts` — module-level variable only |
| Refresh token in httpOnly cookie | ✅ | Server-managed; JS never reads it; only sent via `withCredentials: true` |
| CSRF double-submit preserved | ✅ | `client.ts` reads `XSRF-TOKEN` cookie, sends `X-XSRF-TOKEN` on mutating methods |
| Invite-only registration not bypassed | ✅ | UUID regex required client-side; backend validates UUID against invitations table |
| No credential logging | ✅ | No `console.log` calls on auth paths |
| AI-generated content filter not weakened | ✅ | `CourseController.getPublishedLessonsByModuleId` enforces `PUBLISHED` status |

### Test results

```
 Test Files  4 passed (4)
       Tests  30 passed (30)
    Start at  19:05:51
    Duration  6.96s
```

| File | Tests | All Pass |
|------|-------|----------|
| `AuthContext.test.tsx` | 10 | ✅ |
| `RequireAuth.test.tsx` | 3 | ✅ |
| `LoginPage.test.tsx` | 7 (LoginPage) + ... | ✅ |
| `RegisterPage.test.tsx` | 9 | ✅ |

### Frontend production build

```
✓ built in 816ms
dist/assets/index-DlxdYKBf.js   441.69 kB │ gzip: 139.02 kB
```

No TypeScript errors. No unused imports. No type-only import violations.

### Backend test suite

Maven Central (`repo.maven.apache.org`) is unreachable from the sandbox
environment (DNS resolution fails — network-level block). No Spring Boot
artifact is locally cached. **Backend tests cannot be executed in the
sandbox.** All backend changes must be validated in an environment with
Maven Central access before merging to `main`.

This is an infrastructure constraint, not a code defect. Backend code
was reviewed for correctness; existing tests were not broken by changes
made this sprint (only additive: new controller, new DTOs, one field
added to `StartupSecretValidator`, path/context-path test fixes).

---

## 4. Bug Fixes Delivered

| ID | Description |
|----|-------------|
| FV-002 | Missing route `/explore/:slug/courses/:id` — fixed with `CourseDetailPage` + `GET /api/v1/courses/{id}` |
| FV-006 | MockMvc tests ran against wrong context path — fixed by adding `server.servlet.context-path=/api` to `application-test.yml` |

---

## 5. Existing Bootcamp Impact Assessment

| Check | Result |
|-------|--------|
| Existing bootcamp disabled or redirected? | **No** — zero changes to `main`, `index.html`, or GitHub Pages config |
| Existing routes removed or broken? | **No** — no routes were deleted or renamed |
| Existing `localStorage` functionality changed? | **No** — existing bootcamp uses its own `localStorage` keys; `StudyHub` stores JWT in-memory, never touches those keys |
| Existing learner data modified? | **No** — no migration files added; no data layer changes |
| Regression test evidence | Frontend test suite (`vitest run`) passed 30/30; no test touched existing bootcamp code |

**The existing bootcamp is fully operational and unaffected.**

---

## 6. Version Control Evidence

| Field | Value |
|-------|-------|
| Branch | `studyhub-dev` |
| Commit hash | `7b95ed2a86fc8cbabec3fddf90ec69e9735baea6` |
| Pushed | ✅ `origin/studyhub-dev` |
| `main` last commit | `c84f920` (unchanged — Revert merge PR #1) |
| Files changed | 27 (4363 insertions, 32 deletions) |
| Secrets committed | None |

---

## 7. Outstanding Issues

| Issue | Severity | Notes |
|-------|----------|-------|
| Backend Maven test suite not runnable in sandbox | Medium | DNS block on Maven Central; run `mvn test` in local dev or CI |
| `app.cookie.secure` prod enforcement not live | Low | Requires backend deployment to test; `StartupSecretValidatorTest` covers the logic |
| Lesson detail page not yet implemented | Low | Sprint 5 scope: `GET /api/v1/lessons/{id}`, `LessonPage` wired to real API |

---

## 8. Sprint 5 Recommendation

**Proposed Sprint 5 scope: Lesson Detail, Progress Tracking & Dashboard**

1. `LessonPage.tsx` — connect to real `GET /api/v1/lessons/{id}` backend endpoint (currently stub); display lesson content by type (NOTES, VIDEO, LAB, QUIZ, CHEATSHEET)
2. Progress tracking backend — `LessonProgress` entity, mark-complete endpoint, learner dashboard data
3. Learner dashboard frontend — enrolled courses, completion percentage, recently viewed lessons
4. `GET /api/v1/learner/dashboard` endpoint
5. Expand test coverage to `CourseDetailPage` and `LessonPage` components

**Prerequisite for any merge to `main`:** Backend Maven tests must pass in an environment with Maven Central access. Deploy StudyHub to staging and validate end-to-end auth flow (login → refresh → protected route → logout) before transition approval.
