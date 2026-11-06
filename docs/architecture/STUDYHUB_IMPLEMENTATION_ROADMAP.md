# StudyHub — Implementation Roadmap

**Version:** 1.2  
**Date:** 2026-11-05  
**Status:** Phase 0 complete — Phase 1 approved with corrections. Implementation may begin after remaining open decisions are answered.  
**Author:** Phase 0 Audit (Claude Sonnet 4.6, session https://claude.ai/code/session_01GrXQBHN1FfE3b1PuBWsGTt)  
**Changelog:**
- v1.0 (2026-11-04): Initial Phase 0 roadmap
- v1.1 (2026-11-05): Owner approval corrections applied — Phase 0 marked complete; Phase 1 approved; repo rename timing corrected; learner data preservation added to Sprint 1–2; mastery algorithm added to Sprint 7–8; auth security scope expanded; content admin API requirement added; test acceptance criteria replace raw test counts; PWA clarified; pilot success criteria added to Sprint 11–12; 90-day references removed; cyberLabsNew.js investigation added
- v1.2 (2026-11-05): DR-023 parallel development constraint added — existing bootcamp protection operating strategy, sprint reporting checklist, and replacement approval gate documented throughout

---

## 0. Parallel Development Operating Constraint (DR-023)

**Non-negotiable. Takes precedence over all implementation convenience.**

The existing `bootcamp-hub` application is in active use for certification exam preparation. It must remain fully operational throughout the entire StudyHub development process. StudyHub is built alongside the existing application — not at its expense.

### What this means for every sprint

- **Additive only in early sprints:** Sprint 1–6 work is strictly additive. New backend directories, new React routes, new context providers are introduced without removing or modifying existing ones.
- **No existing file modifications:** `cyberDays.js`, `ibmCurriculum.js`, `aiMonths.js`, `cyberLabsNew.js`, and all other existing data files are read-only until the replacement approval gate is passed.
- **No localStorage key changes:** Existing progress keys are not renamed, removed, or reformatted while the existing application is active.
- **GistSync stays:** `GistSyncContext` is not removed until the replacement approval gate is passed (not Sprint 5–6 as originally planned).
- **Separate environments:** The new Spring Boot backend runs locally alongside the existing Vite dev server. They share no ports, no databases, no localStorage keys.
- **GitHub Pages stays live:** The existing GitHub Pages deployment is not redirected, renamed, or disabled during development.

### Replacement approval gate

The existing bootcamp is NOT retired until the owner confirms all five conditions in writing:
1. StudyHub passes all DR-022 critical workflow acceptance tests
2. All existing curriculum is migrated and independently verified in StudyHub
3. StudyHub is deployed and independently accessible at its own URL
4. A tested rollback procedure has been executed on staging
5. The owner has personally verified the new learning experience and issued explicit authorization

### Sprint reporting requirement

Every sprint report includes a **Bootcamp Protection Checklist** confirming:
- Existing bootcamp was not affected
- Lessons, quizzes, labs, and exams remain fully operational
- No existing content or learner data was modified
- No existing localStorage keys were changed
- Regression verification method used (at minimum: manual smoke test of one full study session)

If any check fails, work stops and the owner is notified before proceeding.

Full policy: `STUDYHUB_DECISION_RECORD.md` → DR-023.

---

## 1. Scope and Assumptions

### 1.1 Developer Capacity

**Solo developer, estimated 15–20 focused hours per week.**

This roadmap is sized for one developer working part-time. Estimates are deliberate and do not compress to match arbitrary deadlines.

**Authoritative estimate: 20–26 weeks (5–6.5 months) at 15–20 hours/week.**

There is no 90-day target for Phase 1. Any reference to a "90-day plan" in earlier strategy documents is superseded by this roadmap. The 26-week estimate is what is achievable at this capacity for a production-quality controlled pilot.

**Estimation approach:**
- Hours are realistic "focused development time" excluding context switching, debugging, learning unfamiliar technology
- Each task is estimated assuming the developer is competent in React/TypeScript; has learned Spring Boot during the planning phase; and is not writing backend Java professionally yet
- Add 30% buffer to any task involving a new technology (Spring Boot, MySQL, Docker)

### 1.2 Approval Gates

This roadmap has explicit STOP points requiring owner review and approval before the next phase begins. The developer must present deliverables and receive written approval before proceeding.

**Gate 1:** Phase 0 → Phase 1: This document + 6 other audit documents  
**→ Gate 1 approved 2026-11-05 with corrections. Phase 1 may begin after open decisions are answered.**

**Gate 2:** Phase 1 → Phase 2: Phase 1 pilot retrospective against DR-021 success criteria + native implementation plan

---

## 2. Phase 0 — Audit and Planning

**Goal:** Understand what we have, design what we're building, make decisions before writing code.  
**Status:** ✅ Complete — approved 2026-11-05  
**Duration:** ~2 weeks (2026-11-04 to 2026-11-05)

### Deliverables

| # | Deliverable | Status |
|---|-------------|--------|
| 1 | `STUDYHUB_CURRENT_STATE_AUDIT.md` | ✅ Complete |
| 2 | `STUDYHUB_TARGET_ARCHITECTURE.md` | ✅ Complete |
| 3 | `STUDYHUB_IMPLEMENTATION_ROADMAP.md` | ✅ This document |
| 4 | `STUDYHUB_CONTENT_AUDIT.md` | ✅ Complete |
| 5 | `STUDYHUB_UX_AUDIT.md` | ✅ Complete |
| 6 | `STUDYHUB_DECISION_RECORD.md` | ✅ Complete |
| 7 | `docs/design/STUDYHUB_VISUAL_DIRECTION.md` | ✅ Complete |

### Remaining open decisions before Phase 1 begins

See `STUDYHUB_DECISION_RECORD.md` section 6 for the full list. Items blocking Sprint 1:

- [ ] **DR-OPEN-002** — Pilot registration model: invite-only, PIN-gated, or open?
- [ ] **DR-OPEN-010** — Pilot size, disciplines at launch, and weekly AI cost budget per user

Items that can be deferred to Sprint 11 planning (but should be answered by then):

- [ ] **DR-OPEN-003** — Backend hosting provider
- [ ] **DR-OPEN-004** — SPA static hosting provider
- [ ] **DR-OPEN-005** — Payment integration in Phase 1?
- [ ] **DR-OPEN-007** — USMLE medical reviewer identity
- [ ] **DR-OPEN-008** — `cyberLabsNew.js` decision (pending inspection — see Sprint 1–2)
- [ ] **DR-OPEN-009** — AI Engineering curriculum mapping ownership

**→ STOP. Phase 1 implementation begins after DR-OPEN-002 and DR-OPEN-010 are answered.**

---

## 3. Phase 1 — Web-First Controlled Pilot

**Goal:** A working, deployed StudyHub web application with real users, real content, and AI-assisted learning — for a controlled set of learners on existing IT certification bootcamps.  
**Prerequisite:** Phase 0 approval ✅ + DR-OPEN-002 and DR-OPEN-010 answered  
**Estimated duration:** 20–26 weeks (5–6.5 months) at 15–20 hours/week  
**Pilot scope:** Cybersecurity + IBM Python+ML bootcamps (AI Engineering after structural fix)

### Phase 1 is NOT:

- Native iOS or Android (Phase 2)
- USMLE content (Phase 2+, unless content is ready and medical reviewer engaged)
- WAEC/NECO/JAMB (Phase 2+)
- Full adaptive learning pathways or autonomous AI agents (future phases)
- Public launch — this is a controlled pilot with invited learners

---

### 3.1 Sprint Map

**Sprint = 2 weeks, ~30 hours of work**

---

#### Sprint 1–2: Foundation (Weeks 1–4) — ~65 hours

**Goal:** Working project skeleton, local dev environment, first API endpoint. Learner data preservation documented. Existing bootcamp untouched.

**Note on repository rename:** The repository is NOT renamed in this sprint. Rename is deferred to Sprint 11–12 or post-pilot (see DR-003 v1.1).

**DR-023 constraint:** All work this sprint is additive. No existing routes, components, contexts, data files, or localStorage keys are modified. The new Spring Boot project lives in a new directory (e.g. `studyhub-backend/`). New React scaffolding is introduced at new routes only.

Tasks:
- [ ] Initialize Spring Boot project (Spring Initializr: Web, Security, Data JPA, MySQL, Validation, Actuator)
- [ ] `docker-compose.yml` for local backend + MySQL
- [ ] Configure `application.yml` / `application-local.yml` with environment variable overrides
- [ ] Implement Identity domain skeleton: User entity, UserRepository, registration, login
- [ ] JWT token issuance — in-memory access token + httpOnly SameSite=Strict refresh cookie
- [ ] Refresh token rotation and revocation (backend token store)
- [ ] CSRF protection (double-submit cookie for state-changing endpoints)
- [ ] CORS configuration (allowlist of approved origins; no wildcard in production)
- [ ] Email verification flow: verification token issued on registration, required before full login
- [ ] Password reset flow: time-limited single-use token, invalidated on use
- [ ] bcrypt password hashing (cost factor ≥ 12)
- [ ] `GET /api/health` endpoint with Actuator
- [ ] GitHub Actions CI: run frontend lint + build; run backend tests
- [ ] Vitest configured on frontend (scaffold only initially)
- [ ] Frontend: install TanStack Query + Zustand + React Hook Form + Zod
- [ ] Frontend: Axios instance with JWT interceptor (silent refresh on 401)
- [ ] Frontend: `BrowserRouter` routing (replace HashRouter)
- [ ] Frontend: Auth pages (`/auth/login`, `/auth/register`) with working API integration
- [ ] **NEW:** `docs/migration/LEARNER_DATA_INVENTORY.md` — inventory of all localStorage progress keys and formats in the existing bootcamp-hub (lesson completion flags, XP, streaks, lab completion, exam attempts, goals, GistSync token)
- [ ] **NEW:** Learner data export tool or documented process — allow existing bootcamp-hub users to export their progress before transition

Estimate: ~65 hours (increased from 60 to account for expanded auth scope and data preservation tasks)  
Checkpoint: Local signup + login working end-to-end; learner data inventory complete

---

#### Sprint 3–4: Core Data Layer (Weeks 5–8) — ~55 hours

**Goal:** Database schema for curriculum and content; seed data loaded via API; content API working.

**Note on cyberLabsNew.js:** Inspect and report on its 72 entries in this sprint (see DR-OPEN-008). Report to owner before deciding whether to include it in the seed script.

Tasks:
- [ ] MySQL schema: disciplines, courses, modules, lessons (see target architecture schema)
- [ ] Flyway for database migrations (all schema changes via migration files)
- [ ] JPA entities for all tables
- [ ] Repository interfaces
- [ ] `GET /api/disciplines` — list disciplines
- [ ] `GET /api/courses/{disciplineId}` — list courses for discipline
- [ ] `GET /api/lessons/{id}` — get lesson content
- [ ] Content lifecycle enforcement: only `PUBLISHED` lessons visible to learners
- [ ] Content admin API: endpoints for status transitions (DRAFT → IN_REVIEW → APPROVED → PUBLISHED → DEPRECATED) — required before seed script can mark content PUBLISHED
- [ ] Content seed script: load Cybersecurity (103 days) and IBM (57 days) curriculum **via content API** (not direct SQL)
- [ ] Content seed script: load 500 Cybersecurity + 100 IBM assessment questions
- [ ] Content seed script: load ~130 labs across both disciplines
- [ ] **cyberLabsNew.js inspection:** read all 72 entries, document structure, assess quality, report to owner for integration/discard decision
- [ ] API-first compatibility review: confirm no web-only contracts exist in content API (see DR-015-NATIVE)
- [ ] Frontend: Explore page (discipline listing, course listing)
- [ ] Frontend: basic Lesson view (fetch + render lesson content via MarkdownRenderer)

Estimate: ~55 hours  
Checkpoint: Can browse disciplines and read a lesson; cyberLabsNew.js inspection report delivered to owner

---

#### Sprint 5–6: Progress Domain (Weeks 9–12) — ~50 hours

**Goal:** Learning state tracked on the backend; XP and streak server-authoritative. Existing bootcamp progress untouched.

**DR-023 constraint:** GistSync is NOT removed in this sprint. The existing `GistSyncContext` remains functional throughout development. It is removed only after the replacement approval gate is passed (see DR-023 and DR-020 v1.1). New progress endpoints run in parallel with the existing localStorage/GistSync system — they do not replace it yet.

Tasks:
- [ ] MySQL schema: user_lesson_progress, user_xp, user_goals
- [ ] `POST /api/progress/lesson/{lessonId}/complete` — mark lesson complete, award XP
- [ ] `GET /api/progress/me` — get learner's overall progress summary
- [ ] `GET /api/goals` / `POST /api/goals` — create/retrieve learning goals
- [ ] XP engine on backend (port logic from XPContext.tsx)
- [ ] Streak calculation: server-authoritative, timezone-aware
- [ ] All progress API endpoints: pagination supported from day one (DR-015-NATIVE)
- [ ] Frontend: Home dashboard (goal card, resume card, discipline cards, XP/streak display)
- [ ] Frontend: My Progress page (mastery chart by domain, streak calendar, XP history)
- [ ] Frontend: XPBar updated to fetch from backend (not localStorage)
- [ ] ~~Remove GistSyncContext~~ — deferred to replacement approval gate (DR-023); GistSync remains active
- [ ] ~~Remove Gist sync from SettingsPanel~~ — deferred to replacement approval gate (DR-023)

Estimate: ~50 hours  
Checkpoint: Learning progress tracked on server; Home dashboard shows real data; GistSync removed

---

#### Sprint 7–8: Assessment Domain and Mastery Algorithm (Weeks 13–16) — ~60 hours

**Goal:** Working mock exam engine with server-side scoring; mastery algorithm implemented; quiz embedded in lessons.

**Note on mastery:** The mastery calculation algorithm must be implemented and unit-tested in this sprint before Sprint 9–10 AI integration, which references mastery for revision session targeting.

Tasks:
- [ ] MySQL schema: questions, exams, exam_attempts, user_mastery (with: topic_id, attempt_count, question_count, weighted_score, last_assessed_at, recency_factor)
- [ ] `GET /api/exams/{examId}/questions` — fetch randomized question set
- [ ] `POST /api/exams/{examId}/submit` — submit answers, score, record attempt
- [ ] `GET /api/exams/attempts/{userId}` — exam history (paginated)
- [ ] **Mastery algorithm implementation:** backend service calculating mastery from: attempt_count (min 3), question_count (min 15), weighted score, difficulty weighting, recency decay (10%/30 days default, configurable)
- [ ] **Mastery algorithm unit tests:** test suite with known inputs → expected mastery scores (required acceptance criteria, see DR-022)
- [ ] Mastery update triggered after exam submission
- [ ] Frontend: `CertExam.tsx` adapted to use server-fetched questions
- [ ] Frontend: `QuizEngine.tsx` adapted for server data
- [ ] Frontend: Practice page (exam selection, start exam, exam timer)
- [ ] Frontend: Post-exam review (score breakdown by domain, correct/incorrect review)
- [ ] Frontend: Incorrect answer review shows AI explanation placeholder (AI in next sprint)

Estimate: ~60 hours (increased from 55 to account for mastery algorithm + tests)  
Checkpoint: Can take a full 100-question mock exam with server scoring; mastery algorithm unit tests passing with known inputs

---

#### Sprint 9–10: AI Integration (Weeks 17–20) — ~65 hours

**Goal:** AI Tutor working in lesson view; incorrect answer explanations; revision sessions targeting weak mastery domains.

Tasks:
- [ ] AI orchestration service (Spring Boot): interface + Anthropic Claude implementation
- [ ] Rate limiting per user per day (Bucket4j or simple DB counter)
- [ ] Usage tracking: `ai_usage_log` table, populated on every AI call
- [ ] Cost controls: configurable max tokens per request type, circuit breaker
- [ ] `POST /api/ai/lesson-tutor` — lesson context grounded Q&A
- [ ] `POST /api/ai/explain-answer` — incorrect answer explanation (triggered automatically)
- [ ] `POST /api/ai/hint` — quiz hint (never reveals answer)
- [ ] `POST /api/ai/revision-session` — generate targeted revision content from weak mastery domains (uses DR-013 mastery scores)
- [ ] `POST /api/ai/simplify` — simplified explanation of lesson section
- [ ] Security audit: confirm no AI provider keys exposed to frontend
- [ ] Frontend: AI Tutor panel in Lesson view (three-column persistent layout per visual direction)
- [ ] Frontend: Quick prompt buttons (Explain simply, Give an example, Ask)
- [ ] Frontend: Incorrect answer explanation auto-display after wrong answer
- [ ] Frontend: Hint button in quiz (before submitting answer)
- [ ] Frontend: Revision session launcher in My Progress (weak domain → revision)
- [ ] Frontend: AI usage indicator (if rate-limited on free tier)
- [ ] Frontend: **AI response feedback** — thumbs up/down on each AI response (required for DR-021 pilot success metrics)

Estimate: ~65 hours  
Checkpoint: AI Tutor functional in lesson view; explanations working for wrong answers; in-app AI feedback collection working

---

#### Sprint 11–12: Hardening + Pilot Launch (Weeks 21–24) — ~60 hours

**Goal:** Production-ready for controlled pilot. All DR-022 acceptance criteria passing. Pilot success infrastructure in place.

**Note on repository rename:** Rename may occur in this sprint if: (a) the deployment is verified, and (b) the owner approves. It is not a launch prerequisite.

**DR-023 replacement approval gate:** Before retiring, redirecting, or disabling the existing bootcamp, all five gate conditions must be confirmed in writing by the owner. This includes the owner personally verifying the new learning experience. No single condition is waivable. The gate is confirmed at the end of this sprint, not assumed.

Tasks:
- [ ] Deploy backend to production (Docker on chosen VPS/PaaS — DR-OPEN-003)
- [ ] Deploy frontend to production (chosen CDN/static host — DR-OPEN-004)
- [ ] Configure HTTPS (Let's Encrypt or provider-managed) — required; not optional
- [ ] Environment variables in production (no secrets in code or git)
- [ ] GitHub Actions: CI/CD to staging on PR merge, production on main merge
- [ ] Accessibility audit: install eslint-plugin-jsx-a11y, fix P0 violations; manual audit of primary flows
- [ ] Font size control: test against all pages, ensure no overflow
- [ ] Mobile QA: test on iOS Safari + Android Chrome (responsive web — not PWA; see DR-014 v1.1)
- [ ] Error boundaries on all major page components
- [ ] Session expiry handling (silent refresh or redirect to login)
- [ ] **DR-022 acceptance criteria tests:** implement and verify all 10 critical workflow tests before opening to pilot users
- [ ] Backup and restore: document and test database backup/restore procedure; store in `docs/operations/BACKUP_RESTORE.md`
- [ ] Pilot metrics instrumentation: register completion tracking, lessons-per-week tracking, return visit tracking, mastery change tracking (for DR-021 success criteria)
- [ ] Load test with expected pilot user count
- [ ] Invite DR-OPEN-010 pilot users; collect feedback week 1
- [ ] Monitor: AI usage costs against budget (DR-OPEN-010), API latency, error rates
- [ ] PWA implementation: only if separately approved by owner after reviewing pilot readiness (see DR-014 v1.1)
- [ ] Repository rename (only if deployment verified + owner approves — see DR-003 v1.1)
- [ ] **DR-023 replacement gate review:** confirm all five gate conditions are met; present to owner for written authorization before any existing bootcamp retirement
- [ ] **GistSync removal** (only after owner issues replacement gate authorization): remove `GistSyncContext`, remove Gist sync from SettingsPanel, clear `gist_token` localStorage key

Estimate: ~60 hours (increased from 55 to account for expanded testing scope and pilot metrics)  
Checkpoint: All DR-022 acceptance criteria passing; pilot live with real users

---

#### Weeks 25–26: Pilot Feedback + Iteration (Buffer)

**Purpose:** Address pilot feedback. Fix bugs. This is intentional buffer — not "extra features."

**What may happen here:**
- UI improvements based on pilot user feedback
- Performance issues found in production
- Content gaps identified by real learners
- AI quality issues (hallucination, unhelpful responses)
- Accessibility issues reported by pilot users

Estimate: ~30 hours  
Checkpoint: Pilot retrospective complete against DR-021 success criteria; Phase 2 planning begins only if criteria are met

---

### 3.2 Phase 1 Total Estimate

| Sprint Block | Weeks | Hours |
|-------------|-------|-------|
| Sprint 1–2: Foundation + Data Preservation | 1–4 | 65 |
| Sprint 3–4: Data Layer + Content Admin | 5–8 | 55 |
| Sprint 5–6: Progress Domain | 9–12 | 50 |
| Sprint 7–8: Assessment + Mastery Algorithm | 13–16 | 60 |
| Sprint 9–10: AI Integration | 17–20 | 65 |
| Sprint 11–12: Hardening + Launch | 21–24 | 60 |
| Weeks 25–26: Pilot Feedback | 25–26 | 30 |
| **Total** | **26 weeks** | **385 hours** |

At 15 hours/week: **~26 weeks** (6.5 months)  
At 20 hours/week: **~19 weeks** (4.75 months)  
**Authoritative target: 20–26 weeks from Phase 1 start to pilot launch**

---

### 3.3 Phase 1 Risk Register

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| Spring Boot learning curve extends backend tasks | High | Medium | Allocate 30% buffer to first 3 backend sprints; use Spring Boot tutorials proactively |
| AI costs exceed budget in pilot | Medium | Medium | Rate limiting + usage caps per user per day; monitor weekly against DR-OPEN-010 budget |
| AI-generated explanations are inaccurate | Medium | High | Grounding in approved content only; human QA sample during pilot; in-app feedback collection |
| Content seed script issues (data format mismatches) | Medium | Medium | Test seed script on staging first; validate all 103 Cyber days before launch |
| cyberLabsNew.js decision delays content migration | Low | Low | Inspection in Sprint 3–4; report to owner; Cyber content can launch without it if needed |
| AI Engineering curriculum not mapped before launch | Medium | Low | Phase 1 launches with Cyber + IBM only; AI Engineering can follow |
| Pilot users churn before providing feedback | Low | Medium | Recruit motivated users; weekly check-in; personal feedback sessions |
| Mastery algorithm calibration requires tuning | Medium | Medium | Unit test with known inputs in Sprint 7–8; plan to adjust decay/thresholds based on pilot data |
| Repository rename breaks existing learners | Low | Medium | Rename deferred until deployment verified; existing GitHub Pages URL remains live until then |

---

## 4. Phase 2 — Native iOS and Android

**Prerequisite:** Phase 1 pilot retrospective complete against DR-021 success criteria + owner approval  
**Estimated start:** Month 7–8 from Phase 1 start  
**Technology:** React Native + Expo (Managed Workflow)

**Phase 2 is explicitly NOT designed during Phase 1.** A proper Phase 2 architecture document is produced at the Phase 1 → Phase 2 gate. This section is an intent statement only.

### High-level scope

- iOS app (App Store submission)
- Android app (Google Play submission)
- Feature parity with web pilot (same content, same AI features, same progress sync)
- Native patterns: React Navigation, platform-specific UI conventions
- Expo Over-The-Air (OTA) updates to avoid store review delays for content changes
- Push notifications for streak reminders, new content

### Native-ready decisions made in Phase 1

The Phase 1 API is designed API-first per DR-015-NATIVE. No web-only endpoints. All progress, content, and AI is served via REST. The native apps are first-class API clients — they do not require backend changes for existing features.

The web SPA's design system (tokens, component patterns) informs the native design but does not block it. React Native components are built separately.

---

## 5. Subsequent Phases (High Level Only)

After Phase 2 is approved and delivered, subsequent phases address platform expansion. These are not designed in Phase 0 or Phase 1.

### Platform Expansion
- USMLE Step 1 content (requires medical content pipeline + qualified reviewer — see DR-007)
- WAEC/NECO/JAMB content
- Subscription and payment integration (Paystack + Stripe — see DR-OPEN-005)
- Localized pricing (NGN + USD)

### Advanced Learning Intelligence
- Adaptive learning pathways (content sequencing based on performance)
- Predictive mastery modelling
- Personalized curriculum generation
- Learning analytics dashboard for self-insight

**These are explicitly deferred. They are not designed in Phase 0 and are not committed as roadmap items until Phase 2 is complete.**

---

## 6. What This Roadmap Is Not Promising

To be explicit about what is out of scope across all defined phases:

- No 90-day delivery timeline for Phase 1 — the authoritative estimate is 20–26 weeks
- No autonomous learning agents
- No AI that independently certifies learner mastery
- No adaptive curriculum generation in Phase 1
- No public marketplace of user-generated content
- No social/community features (forums, study groups) in Phase 1 or 2
- No video content pipeline in Phase 1

---

*End of STUDYHUB_IMPLEMENTATION_ROADMAP.md*
