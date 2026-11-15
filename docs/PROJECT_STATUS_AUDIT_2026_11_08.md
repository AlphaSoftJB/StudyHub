# AlphaSoft LLC — StudyHub Project Status Audit

**Audit type:** Read-only factual progress assessment  
**Audit date:** 2026-11-08  
**Auditor:** Claude Sonnet 4.6  
**Branch inspected:** `studyhub-dev` (commit `957cc5a`)  
**Production branch:** `main` (commit `c84f920` — pre-Sprint-3, unchanged)  
**Audit method:** Direct repository inspection — files read, not assumed. No code was modified, committed, or pushed.  
**Constraint:** Previous sprint completion claims were NOT taken at face value. Each claim was verified against source files, test classes, and git history.

---

## Section 1 — Project Identification

| Field | Value |
|-------|-------|
| Product name | StudyHub |
| Organization | AlphaSoft LLC |
| Repository | `AlphaSoftJB/bootcamp-hub` (rename to `studyhub` deferred — DR-003) |
| Active dev branch | `studyhub-dev` |
| Production branch | `main` (GitHub Pages, unchanged from pre-Sprint-3) |
| Backend stack | Spring Boot 3.3.5 · Java 21 · MySQL · Flyway · JJWT 0.12.6 |
| Frontend stack | React 19.3.1 · TypeScript · Vite 8.3.6 · React Router · Axios |
| Phase | Phase 1 — Web-First Controlled Pilot (Sprint 3 of 12 complete) |
| Governing roadmap | `docs/architecture/STUDYHUB_IMPLEMENTATION_ROADMAP.md` v1.2 |
| Pilot scope (authorized) | Cybersecurity (DOM-01) + IBM Python/ML (DOM-03) · max 10 learners · $25/week AI ceiling |
| Commercial goal | Controlled paid pilot — not public launch |

---

## Section 2 — Development Progress

### 2.1 Overall Sprint Completion

The roadmap authorizes **12 sprints** across 20–26 weeks. Three sprints are complete.

| Sprints | Scope | Status |
|---------|-------|--------|
| Sprint 1–2 | Foundation: auth domain, DB schema, security, JWT | ✅ Complete (verified) |
| Sprint 3 | Core data layer: curriculum domain, content lifecycle, frontend scaffold | ✅ Complete (verified) |
| Sprint 4–12 | Progress domain, assessment, AI integration, hardening, launch | ❌ Not started |

### 2.2 Backend Completeness

**Implemented and verified against source files:**

| Component | Evidence | Status |
|-----------|----------|--------|
| User domain (users, roles, invitations, refresh tokens) | V1–V5 migrations + domain classes | ✅ Schema complete |
| Auth endpoints (register, login, refresh, logout, check) | `AuthController.java` — 5 endpoints, full implementation | ✅ Implemented |
| Invite-only registration with SERIALIZABLE isolation + pessimistic lock | `RegistrationService.java` lines confirmed | ✅ Implemented |
| Curriculum domain (Discipline, Course, Module, Lesson, Exam, Lab, Question) | V7–V10 migrations + 9 domain classes | ✅ Schema complete |
| Content lifecycle DRAFT→IN\_REVIEW→APPROVED→PUBLISHED | `ContentLifecycleService.java` — full state machine | ✅ Implemented |
| DR-012: AI content cannot self-publish | Transitions require `actorUserId`; no auto-advance code path | ✅ Enforced architecturally |
| Discipline/Course/Lesson learner endpoints (3 controllers) | `DisciplineController`, `LessonController` — confirmed `/v1/...` paths | ✅ Implemented |
| Content admin PATCH/GET endpoints | `ContentAdminController` — ROLE\_ADMIN, confirmed `/v1/admin` path | ✅ Implemented |
| JWT security (access token in-memory, refresh in httpOnly cookie) | `SecurityConfig.java` + `AuthController.java` verified | ✅ Implemented |
| CSRF protection | `CsrfCorsTest.java` exists; SecurityConfig CSRF config verified | ✅ Implemented |
| Startup secret validator | `StartupSecretValidator.java` — blocks boot with placeholder secrets | ✅ Implemented |
| FV-001 / FV-005 routing fix | `a9ee03a` — annotation paths corrected; regression tests added | ✅ Fixed |
| Module endpoint | No `ModuleController.java` found | ❌ Not implemented |
| Progress domain (learner progress, quiz history, spaced repetition) | No progress domain classes found | ❌ Not implemented |
| Assessment domain (exam attempts, scoring, mastery algorithm) | Schema exists (V10), no service layer | ❌ Not implemented |
| AI integration (content generation, revision sessions) | No AI integration code | ❌ Not implemented |
| Notification / email service | None found | ❌ Not implemented |
| Rate limiting / cost controls per AI call | None found | ❌ Not implemented (required by DR-006) |
| Admin UI backend (content management beyond status transitions) | Only PATCH/GET lesson endpoints; no bulk operations | ⚠️ Partial |

**Backend completeness (implemented vs. roadmap):** Sprints 1–3 scope delivered. Sprints 4–12 scope entirely absent.

### 2.3 Frontend Completeness

**Source files inspected:** `App.tsx`, `ExplorePage.tsx`, `LessonPage.tsx`, `useCurriculum.ts`, `client.ts`, `curriculum.ts`, `types/curriculum.ts`, `package.json`

| Component | Evidence | Status |
|-----------|----------|--------|
| Vite + TypeScript + React 19 scaffold | `package.json` v0.0.0; build confirms 242 modules | ✅ Scaffolded |
| Explore page (discipline list + detail) | `ExplorePage.tsx` — ExploreListPage + ExploreDisciplinePage | ✅ Implemented |
| Lesson viewer (Markdown render, breadcrumb) | `LessonPage.tsx` + `MarkdownRenderer.tsx` | ✅ Implemented |
| Curriculum API hooks | `useDisciplines`, `useCourses`, `useLesson` | ✅ Implemented |
| In-memory token store + 401 handling | `client.ts` — no localStorage, confirms security requirement | ✅ Implemented |
| Curriculum DTO types | `types/curriculum.ts` — 4 types matching backend | ✅ Implemented |
| Login page / registration UI | No login or register component found anywhere in `src/` | ❌ Not implemented |
| `/explore/:slug/courses/:id` route (CoursePage) | Route absent from `App.tsx`; `CourseRow` links to dead URL (FV-002) | ❌ Not implemented |
| Token refresh logic | Comment in `client.ts`: "refresh logic belongs in AuthContext, Sprint 4" | ❌ Not implemented |
| Auth context / session management | No `AuthContext.tsx` or similar found | ❌ Not implemented |
| Progress UI (quiz, exam, spaced repetition) | None | ❌ Not implemented |
| Assessment UI | None | ❌ Not implemented |
| Admin UI (content management) | None | ❌ Not implemented |
| AI session UI | None | ❌ Not implemented |
| Test framework | No `*.test.ts`, no `*.spec.ts`, no `__tests__` directories found | ❌ Not configured |
| Breadcrumb navigation | Placeholder string in `LessonPage.tsx` | ⚠️ Stub only |

**Frontend completeness:** Explore + Lesson views are functional display scaffolds. No auth UI, no route for courses, no test coverage, no progress or assessment UI.

### 2.4 Database

| Item | Status |
|------|--------|
| V1–V10 Flyway migrations | ✅ All present (`create_roles`, `create_users`, `create_user_roles`, `create_invitation_codes`, `create_refresh_tokens`, `add_invitation_code_revocation`, `create_disciplines`, `create_courses_modules_lessons`, `create_questions_and_labs`, `create_exams`) |
| Schema covers curriculum domain fully | ✅ Disciplines, courses, modules, lessons, questions, labs, exams |
| Content lifecycle columns on lessons | ✅ V8 includes `content_status`, `published_by`, `published_at` |
| `.gitkeep` placeholder in migration dir | ✅ Present — indicates directory is tracked but partially populated |
| Seed data | ❌ No seed script exists; `Task #141` blocked on DR-OPEN-008 |
| Progress/analytics tables | ❌ Not in any migration |

### 2.5 Infrastructure and DevOps

| Item | Status |
|------|--------|
| CI/CD pipeline | ❌ No `.github/workflows/` directory found |
| Dockerfile | ❌ Not found |
| `docker-compose.yml` | ❌ Not found |
| `.env.example` or deployment template | ❌ Not found |
| Production environment docs | ❌ No hosting provider selected (DR-OPEN-003 unresolved) |
| Staging environment | ❌ None |
| Deployment scripts | ❌ None |
| Monitoring / alerting | ❌ None |
| Logging configuration (structured / centralized) | Default Spring Boot logging only |
| Health endpoint | ✅ `HealthEndpointTest.java` exists; Actuator on classpath |

### 2.6 Content

| Item | Status |
|------|--------|
| Curriculum mapping docs (Cybersecurity v2.0, IBM Python/ML v2.0) | ✅ Authored in `docs/curriculum/` |
| Seed script | ❌ Blocked — DR-OPEN-008 decisions outstanding |
| Published lessons in database | ❌ No data; database schema only |
| AI-generated content pipeline | ❌ Not implemented |

---

## Section 3 — Quality and Security

### 3.1 Test Coverage

**Backend:**

| Test class | Type | Scope | Last known result |
|------------|------|-------|-------------------|
| `AuthApiTest` | Integration (`@SpringBootTest`) | Auth endpoints, happy path + errors | Passing (75/75, 2026-11-06) |
| `ConcurrencyTest` | Integration | Invite-only SERIALIZABLE isolation under concurrent requests | Passing |
| `CsrfCorsTest` | Integration | CSRF and CORS configuration | Passing |
| `JwtServiceTest` | Unit | JWT signing, expiry, claims | Passing |
| `RefreshTokenServiceTest` | Unit | Refresh token creation, rotation, invalidation | Passing |
| `CurriculumApiCompatibilityTest` | Integration (`@SpringBootTest`) | Controller bean wiring, annotation paths, DTO shapes | Updated in `a9ee03a`; awaiting re-run |
| `CurriculumApiPathRegressionTest` | MockMvc (`@WebMvcTest`) | Effective HTTP routing with `contextPath("/api")` — FV-001/FV-005 regression | Added in `a9ee03a`; awaiting re-run |
| `CurriculumMigrationTest` | Integration | Flyway migrations apply cleanly | Passing |
| `CurriculumSchemaValidationTest` | Integration | Schema columns, indexes, constraints | Passing |
| `HealthEndpointTest` | Integration | Actuator health endpoint | Passing |
| `FlywayMigrationTest` | Integration | V1–Vn migrations in sequence | Passing |
| `SchemaValidationTest` | Integration | Core user/auth schema | Passing |

**Last confirmed run:** 2026-11-06 by owner, 75/75 passing. **New tests in `a9ee03a` have not been executed** (Maven Central blocked in sandbox; owner re-run required).

**Frontend:** **Zero tests.** No test framework configured. No test files exist.

**Integration/E2E tests:** None.

**Coverage against DR-022 acceptance criteria:** Partial. Auth and schema paths covered. No progress, assessment, AI integration, or learner workflow tests.

### 3.2 Known Bugs and Open Issues

| ID | Severity | Description | Status |
|----|----------|-------------|--------|
| FV-001 | ~~Critical~~ | Double `/api` prefix in DisciplineController + LessonController | ✅ Fixed (`a9ee03a`) |
| FV-005 | ~~High~~ | Double `/api` prefix in ContentAdminController | ✅ Fixed (`a9ee03a`) |
| FV-002 | Medium | Missing `/explore/:slug/courses/:id` React Router route — dead link from `CourseRow` | 🔴 Open (Sprint 4 scope) |
| FV-003 | Medium | Module endpoint (`GET /api/v1/disciplines/:slug/courses/:id/modules`) not implemented | 🔴 Open (Sprint 4 scope) |
| FV-004 | Low | `useCourseModules` frontend hook missing (depends on FV-003) | 🔴 Open (Sprint 4 scope) |
| AUTH-UI | Critical (for pilot) | No login or registration UI — authenticated endpoints unreachable from browser | 🔴 Open (Sprint 4 scope) |
| TOKEN-REFRESH | High | Token refresh not implemented in frontend — sessions expire with no recovery | 🔴 Open (Sprint 4 scope) |

### 3.3 Security Posture

**Strengths (verified against source):**

- JWT access tokens issued as response body only; frontend stores in memory (`client.ts` confirmed); never written to `localStorage`
- Refresh tokens set via `ResponseCookie` with `httpOnly=true`; JavaScript cannot read them
- `StartupSecretValidator` aborts boot if placeholder secrets (`CHANGE_ME*`, `changeme`) are present — prevents accidental production deployment with dev defaults
- `COOKIE_SECURE` defaults to `false` for local dev; production must set env var `COOKIE_SECURE=true`
- `@PreAuthorize("hasRole('ADMIN')")` on `ContentAdminController`; SecurityConfig `/v1/admin/**` rule aligned
- Invite-only registration with SERIALIZABLE isolation and pessimistic write lock prevents race conditions on code redemption
- Pilot cap (10 learners) enforced in `RegistrationService`
- No API keys in frontend source code (confirmed by inspection)
- DR-012 enforced architecturally — no auto-publish code path exists

**Weaknesses / risks:**

| Risk | Severity | Notes |
|------|----------|-------|
| `DB_PASSWORD:changeme` default in `application.yml` | High | Protected by `StartupSecretValidator` — only safe if validator runs and env var is set |
| `COOKIE_SECURE:false` default | High | HTTPS cookies disabled in dev; must be overridden in production |
| No CORS production config | Medium | Currently allows `localhost:5173`; production origin not configured |
| No rate limiting | High | Required by DR-006; not implemented. Brute-force attacks on auth endpoints unmitigated |
| No AI cost controls | High | Required by DR-006. AI budget of $25/week is a decision but has no enforcement code |
| No input sanitization audit | Unknown | Markdown content bodies accepted from admin; no XSS review documented |
| No dependency audit | Unknown | `pom.xml` and `package.json` have not been scanned for CVEs |
| No TLS configuration | Unknown | No SSL termination config; assumed at reverse proxy layer with no documentation |

### 3.4 Data Protection

| Requirement | Status |
|-------------|--------|
| Learner data inventory documented | ✅ `docs/operations/LEARNER_DATA_INVENTORY.md` exists |
| PII storage minimized | ✅ Schema: email + hashed password only |
| Refresh token rotation on use | ✅ Implemented in `RefreshTokenService` |
| Existing bootcamp data not modified | ✅ Confirmed — zero changes to `bootcamp-hub/` |
| Backup strategy | ❌ Not documented |
| Data retention policy | ❌ Not documented |
| GDPR/privacy policy | ❌ Not present |

---

## Section 4 — Production Readiness

### 4.1 Deployment

| Item | Status |
|------|--------|
| Hosting provider selected | ❌ DR-OPEN-003 unresolved |
| Backend deployment config (Dockerfile, Helm, PaaS manifest) | ❌ None |
| Frontend static hosting config | ❌ None |
| CI/CD pipeline | ❌ None |
| Environment variable documentation | ❌ No `.env.example` |
| Database provisioning / migration runbook | ❌ Not documented |
| Rollback procedure | ❌ Not documented |

### 4.2 Observability

| Item | Status |
|------|--------|
| Structured logging | ❌ Default Spring Boot logging; no JSON/structured output |
| Centralized log aggregation | ❌ Not configured |
| Error tracking (Sentry, Rollbar, etc.) | ❌ Not integrated |
| Metrics / APM | ❌ Not configured |
| Uptime monitoring | ❌ Not configured |
| Health check endpoint | ✅ Actuator health endpoint present (`HealthEndpointTest` exists) |

### 4.3 Operational Support

| Item | Status |
|------|--------|
| Runbook for common failures | ❌ Not present |
| On-call escalation plan | ❌ N/A (solo developer) |
| Incident response procedure | ❌ Not documented |
| Support contact for pilot learners | ❌ Not defined |

### 4.4 Performance

| Item | Status |
|------|--------|
| Load testing | ❌ Not performed |
| Database query optimization | ❌ Indexes present on schema (slug, active); no query analysis |
| Frontend bundle size | ✅ 424.72 kB JS (acceptable for pilot) |
| CDN / caching config | ❌ Not configured |

**Production readiness verdict:** The application cannot be deployed to any production environment in its current state. Hosting provider is unselected, there is no Dockerfile, no CI/CD, no production environment variables documented, and no deployment runbook. The `StartupSecretValidator` will prevent accidental deployment with weak credentials, but there is nothing to deploy to.

---

## Section 5 — Commercial Readiness

### 5.1 Monetization and Payments

| Item | Status |
|------|--------|
| Freemium model defined (DR-015) | ✅ Decision documented |
| Payment integration | ❌ No Stripe, Paddle, or any payment code |
| Subscription management | ❌ Not implemented |
| Pricing page | ❌ Not built |
| Billing portal | ❌ Not built |
| DR-OPEN-005 (payment in Phase 1?) | ⚠️ Decision deferred — to be answered before Sprint 11 |

### 5.2 Marketing and Customer Acquisition

| Item | Status |
|------|--------|
| Marketing website | ❌ Not built |
| Landing page | ❌ Not built |
| Product positioning / messaging | ❌ Not documented beyond internal architecture docs |
| App store listings | ❌ N/A (web only in Phase 1) |
| Email marketing / drip sequences | ❌ Not built |
| SEO | ❌ Not configured |

### 5.3 Onboarding and Customer Experience

| Item | Status |
|------|--------|
| Login / registration UI | ❌ Not built |
| Onboarding flow for invited learners | ❌ Not built |
| Email invitation delivery mechanism | ❌ Not implemented |
| In-app help / documentation | ❌ Not built |
| Support channel (email, chat, ticketing) | ❌ Not defined |

### 5.4 Pilot-Specific Commercial Readiness

The pilot is defined as a **controlled, invite-only** cohort of up to 10 learners. The minimum viable commercial pilot requires:

| Requirement | Status |
|-------------|--------|
| Backend auth (invite codes, JWT, session) | ✅ Implemented |
| Frontend auth UI (login form + invitation entry) | ❌ Not built |
| Curriculum content (disciplines, courses, lessons seeded) | ❌ No seed data |
| Learner can browse course catalog | ✅ Backend + partial frontend |
| Learner can read a lesson | ✅ Backend + frontend viewer |
| Learner receives invitation email | ❌ No email service |
| Learner can pay (if any fee for pilot) | ❌ No payment integration |
| AI-assisted learning session | ❌ Not implemented |
| Progress tracking | ❌ Not implemented |

---

## Section 6 — Outstanding Work

### P0 — Pilot Blocker (Must complete before any pilot user can log in)

| ID | Item | Description | Estimated effort |
|----|------|-------------|-----------------|
| P0-01 | Login / registration UI | No auth UI exists; invited learner cannot log in | Sprint 4 (2–3 days) |
| P0-02 | Token refresh in frontend | Sessions expire with no recovery mechanism | Sprint 4 (1 day) |
| P0-03 | FV-002: CoursePage route | `/explore/:slug/courses/:id` dead link from course list | Sprint 4 (1–2 days) |
| P0-04 | Curriculum seed data | Database is empty; no content for pilot | Blocked on DR-OPEN-008 decisions then Sprint 4 (2–3 days) |
| P0-05 | Backend test re-run | 10 new test methods in `a9ee03a` unexecuted | Owner action — 30 min |
| P0-06 | Hosting provider selection | No deployment target exists | Owner decision (DR-OPEN-003) |

### P1 — Required for Pilot Launch (Must complete before first pilot session)

| ID | Item | Description | Estimated effort |
|----|------|-------------|-----------------|
| P1-01 | CI/CD pipeline | No automated build, test, or deploy | Sprint 11–12 scope; start earlier for safety |
| P1-02 | Dockerfile + deployment config | Nothing deployable | Sprint 5–6 scope |
| P1-03 | Production CORS config | Currently `localhost:5173` only | Sprint 11 |
| P1-04 | Rate limiting on auth endpoints | Required by DR-006; brute-force unmitigated | Sprint 5–6 |
| P1-05 | FV-003: Module endpoint | Backend `GET /api/v1/.../modules` missing | Sprint 4 |
| P1-06 | AI cost controls and enforcement | Required by DR-006; $25/week decision has no code | Sprint 9–10 |
| P1-07 | Email invitation delivery | Invited learners cannot receive invite codes | Sprint 5–6 |
| P1-08 | Progress domain | No learner progress tracking | Sprint 5–6 |
| P1-09 | Frontend test framework | Zero test coverage on UI | Sprint 4 |

### P2 — Required Before Commercial Expansion (Post-pilot)

| ID | Item | Description |
|----|------|-------------|
| P2-01 | Assessment domain (quiz, exam, scoring, mastery) | Sprint 7–8 |
| P2-02 | AI integration (content generation, revision sessions) | Sprint 9–10 |
| P2-03 | Payment integration | DR-OPEN-005 decision required first |
| P2-04 | Monitoring and alerting | Logging, metrics, error tracking |
| P2-05 | Performance testing | Load testing before scaling beyond 10 learners |
| P2-06 | GDPR / privacy policy | Required for any EU learners |
| P2-07 | Admin UI | Content management beyond API calls |
| P2-08 | Frontend E2E test coverage | Per DR-022 acceptance criteria |

### P3 — Commercial Launch Desirable

| ID | Item | Description |
|----|------|-------------|
| P3-01 | Marketing website and landing page | Pre-sales and conversion |
| P3-02 | Native mobile (iOS/Android) | Phase 2; not Phase 1 |
| P3-03 | USMLE pathway | Requires medical reviewer (DR-OPEN-007 unresolved) |
| P3-04 | Additional 6 learning domains (DOM-04 through DOM-08) | Post-pilot expansion |
| P3-05 | Repository rename (`bootcamp-hub` → `studyhub`) | DR-003 — deferred to Sprint 11–12 |

### Outstanding Owner Decisions (Blocking items)

| Decision ref | Question | Blocking |
|--------------|----------|---------|
| DR-OPEN-003 | Backend hosting provider | P0-06; all deployment work |
| DR-OPEN-005 | Payment integration in Phase 1? | P2-03 |
| DR-OPEN-007 | USMLE medical reviewer identity | USMLE content pipeline |
| DR-OPEN-008 decisions 1–4 | Seed script option, days 50–54, days 85–89 duplicates, cyberLabsNew.js | P0-04 |

---

## Section 7 — Final Assessment

### 7.1 Verified Completion Status

| Sprint | Authorized scope | Verified complete |
|--------|-----------------|-------------------|
| Sprint 1–2 | Auth domain, DB schema V1–V6, JWT security, CSRF/CORS | ✅ Yes |
| Sprint 3 | Curriculum domain, content lifecycle, frontend scaffold, curriculum docs | ✅ Yes (with caveats: seed data blocked, new regression tests unexecuted) |
| Sprint 4–12 | All remaining work | ❌ Not started |

**Verified deliverables:** 10 Flyway migrations, 55 Java production source files, 12 test classes, 75 tests passing (pre-fix), 2 curriculum mapping docs, 1 Sprint 3 closure report, 1 frontend verification report.

**Not verified (awaiting owner action):** 10 new test methods in `CurriculumApiPathRegressionTest` and updated `CurriculumApiCompatibilityTest` from commit `a9ee03a` — Maven Central blocked in sandbox; owner must run `mvn test` from local machine.

### 7.2 Production Readiness

**The application is not deployable in its current state.** There is no Dockerfile, no deployment configuration, no hosting provider selected, no CI/CD pipeline, and no environment variable documentation. The backend would refuse to start in production until valid JWT secrets are provided via environment variables (enforced by `StartupSecretValidator`), but there is nowhere to deploy it to.

### 7.3 Commercial Launch Readiness

**The application is not commercially launchable.** There is no login UI, no onboarding flow, no content seeded in the database, no AI integration, no progress tracking, and no payment integration. A pilot learner invited today cannot log in.

### 7.4 Top 5 Remaining Tasks (Priority Order)

1. **P0-01 / P0-02:** Auth UI (login form) + token refresh — zero pilot activity without this
2. **P0-04:** Curriculum seed data — zero content without this (blocked on DR-OPEN-008)
3. **P0-06 + P1-02:** Hosting provider decision + Dockerfile — nothing deployable without this
4. **P0-03 / P1-05:** CoursePage route + Module endpoint — curriculum navigation broken
5. **P1-01:** CI/CD — every deployment is a manual operation until this exists

### 7.5 Major Risks

| Risk | Severity | Mitigation |
|------|----------|------------|
| No deployment target selected after 3 sprints | High | Owner must resolve DR-OPEN-003 before Sprint 4 or deployment work cannot begin |
| Seed data blocked on 4 owner decisions (DR-OPEN-008) | High | Owner must make decisions before Sprint 4 content work |
| Zero frontend tests | High | Any refactor or Sprint 4 auth UI work has no safety net |
| New regression tests unexecuted | Medium | Owner re-run required; risk low given test logic is sound |
| Rate limiting absent (DR-006 violation) | Medium | Auth endpoints exposed to brute force until Sprint 5–6 |
| No monitoring — blind to production failures | Medium | First failure in production will be discovered by learners, not operators |
| Medical reviewer for USMLE unresolved (DR-OPEN-007) | Low for Phase 1 | USMLE excluded from pilot scope; risk deferred |

### 7.6 Recommended Next Milestone

**Sprint 4 scope (prerequisite to any pilot activity):**
1. Auth UI (login, registration with invite code)
2. Token refresh in frontend (AuthContext)
3. CoursePage route + Module endpoint (FV-002, FV-003)
4. Frontend test framework setup
5. Owner resolves DR-OPEN-008 → seed script execution
6. Owner resolves DR-OPEN-003 → Dockerfile + deployment config begins

**Prerequisite to Sprint 4 start:** Owner reviews Sprint 3 closure report and explicitly authorizes.

### 7.7 Estimated Time to Pilot Launch

Per roadmap v1.2: **20–26 weeks total at 15–20 hours/week**. Sprint 3 complete = approximately weeks 5–6 of the roadmap. Remaining: **~15–21 weeks** (Sprints 4–12). No sprint compression is possible without sacrificing DR-022 test acceptance criteria.

---

## Section 8 — Final Recommendation

```
REQUIRES SIGNIFICANT WORK
```

**Basis:**

The StudyHub platform has a solid, correctly-implemented security foundation (JWT, invite-only auth, content lifecycle enforcement, secret validation). The curriculum domain backend is production-quality within its scope. Sprint 3 P0 bugs were identified and correctly fixed with appropriate regression coverage.

However, the platform is **3 of 12 sprints complete** against a 20–26 week roadmap. The following are verified absent:

- Login UI (a pilot learner cannot log in)
- Curriculum seed data (the database is empty)
- Deployment infrastructure (no Dockerfile, no hosting provider, no CI/CD)
- Progress and assessment domains (the core learner experience)
- AI integration (the differentiated product capability)
- Frontend tests (zero)

The recommendation reflects the actual state of the work, not the ambition of the architecture. The architecture is sound. The implementation is at an early stage. No shortcuts exist — the remaining sprints represent real, well-scoped work that cannot be bypassed without sacrificing the security and quality constraints the owner has explicitly required.

**Recommended label:** READY FOR CONTROLLED PILOT is the correct target for Sprint 11–12 completion, not the current state.

---

*This audit was conducted read-only against commit `957cc5a` on branch `studyhub-dev` on 2026-11-08. No code was modified, committed, or pushed. All findings are based on direct file inspection; no prior completion claims were assumed accurate without verification.*

*Audit produced by Claude Sonnet 4.6.*  
*Session: https://claude.ai/code/session_01GrXQBHN1FfE3b1PuBWsGTt*
