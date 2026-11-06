# StudyHub — Decision Record

**Version:** 1.3  
**Date:** 2026-11-05  
**Status:** Phase 0 complete — Phase 1 approved and in progress. All confirmed decisions below are authoritative unless explicitly superseded by the owner.  
**Author:** Phase 0 Audit (Claude Sonnet 4.6, session https://claude.ai/code/session_01GrXQBHN1FfE3b1PuBWsGTt)  
**Changelog:**
- v1.0 (2026-11-04): Initial Phase 0 decisions
- v1.1 (2026-11-05): Owner approval corrections applied — DR-003, DR-009, DR-013, DR-014, DR-019, DR-020 revised; DR-OPEN-001 resolved; DR-OPEN-008 reclassified; new DR-021 (pilot success criteria) and DR-022 (testing acceptance criteria) added
- v1.2 (2026-11-05): DR-023 added — existing bootcamp protection and parallel development constraint; supersedes any roadmap implication that the existing application would be disrupted during development
- v1.3 (2026-11-05): DR-OPEN-002 and DR-OPEN-010 resolved — pilot registration model (invite-only) and pilot scope (10 learners, Cybersecurity + IBM Python/ML, $25/week AI ceiling) approved by owner; Sprint 1 authorized

---

## Purpose

This document records every architectural and product decision made during Phase 0. Each decision has:

- A unique ID (DR-NNN)
- Status: **Confirmed** (locked), **Deferred** (not yet decided), or **Superseded** (changed by a later decision)
- The decision itself
- The rationale
- The consequences (what this rules in or out)

Confirmed decisions are authoritative. Reopening a confirmed decision requires an explicit instruction from the owner, logged here as a superseding entry.

---

## 1. Platform Decisions

### DR-001 — Platform Support
**Status:** Confirmed  
**Date confirmed:** Pre-Phase 0

**Decision:** StudyHub supports native iOS, native Android, and Web.

**Rationale:** A multi-discipline learning platform targeting both a global (IT certs) and Nigerian (WAEC/NECO/JAMB) audience requires mobile-first reach. Native iOS and Android provide first-class mobile experiences. Web is included as the primary surface for the Phase 1 controlled pilot.

**Consequences:**
- Web is the primary surface for Phase 1 (controlled pilot)
- Native iOS and Android are confirmed targets but implemented in Phase 2
- Native must be accounted for in Phase 0 architecture and feasibility audit (done — see STUDYHUB_TARGET_ARCHITECTURE.md)
- Native implementation follows approval gate: Phase 1 retrospective → Phase 2 authorization
- API design in Phase 1 must be API-first (no web-only contracts); see DR-012-NATIVE below

---

### DR-002 — Web Phase 1 is Controlled Pilot
**Status:** Confirmed  
**Date confirmed:** Pre-Phase 0

**Decision:** The web deployment in Phase 1 is a controlled pilot — not a public launch. Access is limited to invited learners.

**Rationale:** A controlled pilot reduces risk, allows for content and AI quality monitoring, and enables course-correction before public launch.

**Consequences:**
- Registration may be invite-only or PIN-gated (specific mechanism: Deferred — see DR-OPEN-002)
- Pilot size is bounded — not an open public launch
- AI usage costs are predictable and manageable during pilot
- Feedback can be collected directly from known users
- Pilot success must be evaluated against measurable criteria before Phase 2 approval (see DR-021)

---

### DR-003 — Repository Identity
**Status:** Confirmed (implementation timing revised — v1.1)  
**Date confirmed:** Phase 0  
**Revised:** 2026-11-05

**Decision:** The platform will be named StudyHub, owned by AlphaSoft LLC. The repository will be renamed from `bootcamp-hub` to `studyhub`.

**Timing constraint (added v1.1):** The repository rename must NOT occur until:
1. The Phase 1 replacement deployment is fully operational and verified in production
2. A documented rollback plan exists for the rename
3. The owner has explicitly approved the rename timing

**Rationale:** Renaming the repository before the replacement deployment is ready risks breaking the existing GitHub Pages deployment without a live successor. The existing bootcamp-hub serves real learners and must not be disrupted without a proven replacement.

**Consequences:**
- Repository rename (`AlphaSoftJB/bootcamp-hub` → `AlphaSoftJB/studyhub`) is deferred to late Phase 1 Sprint 11–12 or post-pilot, not Sprint 1
- Sprint 1 must NOT include the repository rename
- `package.json` name, `vite.config.ts` base path, and GitHub Pages configuration are updated at rename time
- Existing GitHub Pages URL remains live until the new deployment is verified
- All internal documentation uses "StudyHub" from this point forward regardless of repository name

---

## 2. AI Integration Decisions

### DR-004 — AI Scope (Phase 1)
**Status:** Confirmed  
**Date confirmed:** Pre-Phase 0

**Decision:** Phase 1 AI capabilities are limited to these five:
1. Lesson-specific AI Tutor (Q&A during lesson)
2. Simplified explanations (on request)
3. Examples and guided hints (on request during quiz)
4. Explanations of incorrect assessment answers (auto-triggered)
5. Revision sessions based on approved learning content

**Rationale:** These five capabilities directly support learner outcomes without requiring autonomous agents or unreviewed content generation. They are grounded in approved content and have predictable cost and risk profiles.

**Consequences:**
- Adaptive learning pathways are deferred (not Phase 1)
- Predictive mastery modelling is deferred
- Autonomous agents are deferred
- Complex personalized curriculum generation is deferred
- All five capabilities must be grounded in approved lesson content — no free-form generation
- AI-generated content in USMLE path → DRAFT state only, never auto-publish (see DR-007)

---

### DR-005 — AI Architecture (Backend Only)
**Status:** Confirmed  
**Date confirmed:** Phase 0

**Decision:** All AI calls route through the backend. No AI provider API keys are exposed to web or native clients.

**Rationale:** Exposing provider keys to clients creates cost and abuse risks that cannot be controlled. Backend orchestration enables rate limiting, usage tracking, cost controls, and provider switching without client changes.

**Consequences:**
- Backend must implement `AIOrchestrationService` interface before any AI feature ships
- Frontend makes calls to `/api/ai/*` endpoints only — never to provider APIs directly
- Native apps in Phase 2 use the same backend endpoints — no native-specific AI plumbing
- Provider can be changed (Anthropic → another) without client changes
- Rate limiting and cost controls are backend-enforced, not client-enforced

---

### DR-006 — AI Controls Required at Launch
**Status:** Confirmed  
**Date confirmed:** Phase 0

**Decision:** The following controls must be in place before any AI feature is deployed to production:
- Per-user daily usage tracking
- Per-user rate limiting (bucket per minute)
- Maximum token limits per request type (configurable)
- Cost-per-request logging in `ai_usage_log` table
- Circuit breaker (graceful degradation if AI provider is unavailable)
- Content grounding: every prompt includes approved lesson content as context

**Rationale:** Without these controls, a small number of users could incur significant costs or receive unreliable AI responses. These are non-negotiable prerequisites for pilot launch.

**Consequences:**
- Sprint 9–10 (AI Integration) must implement all six controls before moving to Sprint 11
- AI features do not ship to staging until controls are verified
- Freemium tier differentiation can use the rate-limiting infrastructure already in place

---

### DR-007 — Medical Content AI Rule
**Status:** Confirmed  
**Date confirmed:** Phase 0

**Decision:** AI-generated content on the USMLE pathway must never automatically become published educational content. AI-generated USMLE content lands in DRAFT state only. A qualified medical professional must review and approve before it reaches PUBLISHED state.

**Rationale:** Medical education errors have patient safety implications. Automated content publishing on a medical learning path is not acceptable without human expert review.

**Consequences:**
- Backend content lifecycle enforcement: USMLE + AI-generated → DRAFT only (backend enforced, not just convention)
- A qualified medical reviewer must be identified before USMLE content pipeline begins (see DR-OPEN-007)
- Content workflow UI for reviewer must be built before USMLE launch (may be post-Phase 1)
- USMLE content without a reviewer in place → no USMLE in Phase 1

---

## 3. Architecture Decisions

### DR-008 — Target Stack
**Status:** Confirmed  
**Date confirmed:** Phase 0

**Decision:** Target platform is React SPA + Spring Boot modular monolith + MySQL.

**Frontend:** React 18 + TypeScript + Vite 5 + Tailwind CSS 3  
**Backend:** Spring Boot 3.x + Java 21 + Spring Security + Spring Data JPA  
**Database:** MySQL 8.x  
**Native (Phase 2):** React Native + Expo (Managed Workflow)

**Rationale:**
- React frontend: existing codebase, strong ecosystem, shared types with React Native
- Spring Boot: strong security ecosystem (Spring Security), mature JPA, familiar to enterprise Java ecosystem, excellent documentation for a developer learning backend
- Modular monolith: simpler to operate than microservices for a solo developer; clean domain boundaries allow future extraction if needed
- MySQL: ACID compliance, strong tooling, compatible with Spring Data JPA, low operational overhead

**Consequences:**
- Developer must learn Spring Boot if not already proficient — budget 30% extra on early backend sprints
- Microservices are not the target — do not design for them in Phase 1 or 2
- PostgreSQL is not chosen — do not use PostgreSQL-specific SQL features or assume PG in any design
- Flyway for database migrations (not Liquibase, not raw SQL)

---

### DR-009 — Authentication and Session Security
**Status:** Confirmed (expanded — v1.1)  
**Date confirmed:** Phase 0  
**Expanded:** 2026-11-05

**Decision:** JWT access tokens stored in-memory only (never localStorage). Refresh tokens in httpOnly, Secure, SameSite=Strict cookies (never accessible to JavaScript). The following security controls are required at launch:

**Token management:**
- Access token: short-lived (15 minutes), stored in-memory only, never persisted
- Refresh token: longer-lived (7–30 days), httpOnly + Secure + SameSite=Strict cookie, never accessible to JS
- Refresh token rotation: every use of a refresh token issues a new token and invalidates the old one
- Refresh token revocation: backend maintains a revocation store; logout invalidates the token server-side
- Reuse detection: if a rotated (already-invalidated) refresh token is presented, the entire family is revoked

**Session controls:**
- Session expiration: idle timeout + absolute maximum session length enforced server-side
- Silent refresh: Axios interceptor attempts silent token refresh on 401 before redirecting to login
- Concurrent session policy: decision deferred — default is allow concurrent sessions; owner may restrict

**Transport and application security:**
- CSRF protection: since refresh tokens are in SameSite=Strict cookies, CSRF is mitigated; double-submit cookie pattern added as defence-in-depth for state-changing endpoints
- CORS: backend whitelist of allowed origins; no wildcard `*` in production
- Secure cookies: `Secure` flag enforced in production (requires HTTPS); `HttpOnly` always set

**Account security:**
- Password reset: time-limited, single-use token sent to verified email; invalidated on use or expiry
- Email verification: required before login is fully enabled for new accounts
- Password requirements: minimum 12 characters; bcrypt hashing (cost factor ≥ 12)

**Rationale:** A platform handling learner progress, subscription data, and AI interactions requires a complete authentication security baseline. The initial DR-009 addressed token storage; this expansion addresses the full session security surface.

**Consequences:**
- GistSyncContext pattern of storing tokens in localStorage must NOT be replicated
- Sprint 1–2 auth implementation must include: JWT issue/refresh, CSRF, CORS config, bcrypt, email verification flow, password reset flow
- Native apps (Phase 2) use a different token storage mechanism (SecureStore for Expo)
- HTTPS is required in production — not optional (see also Sprint 11–12 deployment tasks)

---

### DR-010 — Routing Change
**Status:** Confirmed  
**Date confirmed:** Phase 0

**Decision:** Replace `HashRouter` with `BrowserRouter` in Phase 1. Requires server-configured fallback to `index.html` for all routes.

**Rationale:** HashRouter was required for GitHub Pages static hosting (which cannot handle client-side routes). Phase 1 moves to a proper CDN or static host that supports fallback configuration. BrowserRouter produces cleaner URLs and is required for PWA and social sharing.

**Consequences:**
- GitHub Pages is no longer the deployment target for Phase 1
- Hosting provider must support SPA fallback (all modern CDN providers do)
- All existing route paths (`/`, `/cyber`, `/ibm`, `/ai`) must be remapped to the new five-area structure
- `vite.config.ts` base path `'/bootcamp-hub/'` changes to `'/'`

---

### DR-011 — State Management
**Status:** Confirmed  
**Date confirmed:** Phase 0

**Decision:** Server state via TanStack Query; UI state via Zustand; localStorage for UI preferences only (theme, font size).

**Rationale:** TanStack Query handles caching, background refresh, and loading/error states for API data, eliminating significant boilerplate. Zustand replaces the existing context-heavy pattern for UI state. localStorage retention is strictly limited to non-sensitive UI preferences.

**Consequences:**
- Remove all learning progress from localStorage (progress moves to backend)
- Remove GistSyncContext entirely (cross-device sync is now server-authoritative)
- XPContext, SettingsContext logic migrates: XP → backend; theme/fontSize → Zustand + localStorage
- No user data (progress, goals, scores) in localStorage in Phase 1 or later

---

### DR-012 — Content Lifecycle
**Status:** Confirmed  
**Date confirmed:** Phase 0

**Decision:** All content follows a five-state lifecycle: `DRAFT → IN_REVIEW → APPROVED → PUBLISHED → DEPRECATED`. Only `PUBLISHED` content is visible to learners.

**Consequence (added v1.1):** Direct SQL is NOT an acceptable normal publishing mechanism. Content status transitions must go through controlled backend APIs or a validated import process that enforces lifecycle rules. Direct SQL access to the `content_status` column is restricted to emergency recovery only, requires owner approval, and must be logged.

**Rationale:** Content quality control is essential for an educational platform. The lifecycle ensures reviewed, accurate content reaches learners. Bypassing it via direct SQL creates an uncontrolled pathway that circumvents safeguards.

**Consequences:**
- Backend enforces content status filtering on all learner-facing content endpoints
- Existing bootcamp content (Cybersecurity, IBM) seeds as `PUBLISHED` after validation — via seed script API calls, not direct SQL inserts
- AI-generated content seeds as `DRAFT` (enforced at the AI orchestration layer)
- USMLE AI-generated content requires qualified reviewer approval before `APPROVED` (see DR-007)
- An admin/reviewer content management interface is needed before USMLE launch
- Seed script (Sprint 3–4) calls the content API to load content; it does not INSERT directly into content tables

---

### DR-013 — Mastery Model
**Status:** Confirmed (evidence-based approach required — v1.1)  
**Date confirmed:** Phase 0  
**Revised:** 2026-11-05

**Decision:** Progress is tracked as competency mastery per topic/domain. Mastery is an evidence-based score, not a single-assessment calculation.

**Evidence requirements for mastery:**
A mastery score for a topic must incorporate multiple factors. A single assessment score alone must never automatically establish or confirm mastery.

Required factors:
1. **Consistency:** performance across multiple assessment attempts, not a single result
2. **Difficulty weighting:** harder questions count more toward mastery than easy recall questions
3. **Recency:** more recent assessments are weighted more heavily than older ones (mastery degrades if not reinforced)
4. **Volume:** a minimum number of distinct questions answered before mastery is considered established
5. **Practical performance:** where labs or applied tasks exist, lab completion contributes to mastery alongside quiz results

**Minimum mastery establishment criteria (Phase 1 baseline):**
- Minimum 3 distinct assessment attempts on a topic before mastery score is stabilized
- Minimum 15 unique questions answered per topic domain
- Recency decay: mastery score reduces over time without reinforcement (configurable decay rate, default: 10% per 30 days inactive)

**What mastery is not:**
- Not a completion percentage ("read 80% of lessons")
- Not a single exam score (90% on one exam ≠ mastery)
- Not irreversible (a lapse in practice can reduce mastery score)

**Rationale:** A single assessment score is a noisy signal — it reflects one day's performance, one question set, and may include guessing. Evidence-based mastery more accurately predicts genuine competency and exam readiness.

**Consequences:**
- `user_mastery` table must store: topic_id, attempt_count, question_count, weighted_score, last_assessed_at, recency_factor
- Mastery calculation is a backend service, not a frontend computation
- Mastery algorithm implementation is a Phase 1 Sprint 7–8 deliverable (defined before Sprint 9–10 AI integration, which references mastery for revision sessions)
- Home dashboard shows mastery map, not completion bar
- XP and streaks remain (engagement mechanism) but mastery is the primary progress signal

---

### DR-014 — PWA and Responsive Web
**Status:** Confirmed (clarified — v1.1)  
**Date confirmed:** Phase 0  
**Clarified:** 2026-11-05

**Decision:**
- **Responsive web is required** for the Phase 1 pilot. All pages must be functional and usable on mobile browsers without installation. This is not optional.
- **PWA capabilities** (offline support, home screen install, push notifications via Service Worker) are conditional. They are not a pilot prerequisite and are deferred to a separate owner decision after core functionality is stable.

**Rationale:** Responsive web ensures mobile learners can use the platform on day one. PWA adds capability but adds complexity and testing burden. Separating the two prevents PWA scope from blocking the pilot.

**Consequences:**
- Sprint 11–12 must include mobile QA on iOS Safari + Android Chrome (responsive, not PWA)
- PWA implementation (`vite-plugin-pwa`, service worker, manifest) is NOT in Sprint 11–12 unless owner separately approves it after reviewing pilot readiness
- DR-OPEN-006 (PWA in Phase 1?) is answered: responsive web yes; PWA conditional on a separate decision

---

### DR-015-NATIVE — Phase 1 API Native Compatibility
**Status:** Confirmed  
**Date confirmed:** 2026-11-05

**Decision:** Phase 1 backend API contracts must be designed for native client compatibility from the start. No web-only endpoints. No web-specific assumptions baked into API contracts.

**Rationale:** React Native + Expo apps in Phase 2 must be first-class API clients. Rework to remove web-only assumptions after the fact is expensive.

**Required constraints on Phase 1 API design:**
- All authentication flows must be compatible with token-based auth as used by React Native (SecureStore) — not dependent on cookies
- API responses must not embed HTML or assume a browser context
- File/media URLs must be absolute, not relative to a web root
- All endpoints returning lists must support pagination from day one
- Error response format must be consistent and machine-parseable (not HTML error pages)

**Consequences:**
- API-first review checkpoint added to Sprint 3–4 (before content API is finalized): confirm no web-only contracts exist
- Phase 2 native implementation does not require backend changes for existing feature set

---

## 4. Product Decisions

### DR-015 — Freemium Model
**Status:** Confirmed  
**Date confirmed:** Pre-Phase 0

**Decision:** StudyHub uses a freemium subscription model with localized pricing (NGN / USD).

**Rationale:** Freemium lowers the barrier to initial engagement. Localized pricing makes the platform accessible to Nigerian learners for whom USD pricing may be prohibitive.

**Consequences:**
- Backend `users` table includes `subscription_tier` field
- Payment integration: Paystack (NGN) + Stripe (USD) — deferred to post-Phase 1 (see DR-OPEN-005)
- Free tier limitations must be defined before Phase 1 launch (AI usage caps are the primary lever)
- Phase 1 pilot may be entirely free for invited users

---

### DR-016 — Visual Direction
**Status:** Confirmed  
**Date confirmed:** Phase 0

**Decision:** StudyHub uses a dark-first visual design. Core palette: deep navy/charcoal backgrounds (`#07090f` base), violet/indigo accents, teal/green for progress indicators. Typography: Inter (body) + JetBrains Mono (code). The existing design token system in `src/index.css` is preserved and extended.

**Rationale:** The existing design system is well-constructed, cohesive, and appropriate for a technical learning platform. Dark-first reduces eye strain for learners in extended study sessions.

**Consequences:**
- Light theme is supported (existing implementation preserved) but dark is the default
- New discipline-specific accent tokens extend rather than replace the existing token system
- Full visual direction document: `docs/design/STUDYHUB_VISUAL_DIRECTION.md`
- Native apps (Phase 2) are inspired by but not bound to these tokens — React Native design is built separately

---

### DR-017 — Five Navigation Areas
**Status:** Confirmed  
**Date confirmed:** Phase 0

**Decision:** StudyHub has five navigation areas: Home, Explore, Learn, Practice, My Progress.

**Rationale:** These five areas map cleanly to the learner journey: discover (Explore), engage (Home/Learn), assess (Practice), and reflect (My Progress). The existing four-item nav (Hub/Cyber/IBM/AI) is discipline-specific and does not scale to a multi-discipline platform.

**Consequences:**
- Current `NAV_ITEMS` array in `App.tsx` is replaced with five-area navigation in Phase 1
- Discipline selection moves from navigation to Explore page (browse and select, then enter Learn mode)
- Current bottom nav (mobile) and side nav (desktop) layouts are preserved but content changes
- SideNav width `w-20 lg:w-56` is an appropriate responsive pattern — preserved

---

### DR-018 — Accessibility Target
**Status:** Confirmed  
**Date confirmed:** Phase 0

**Decision:** StudyHub targets WCAG 2.2 AA compliance.

**Rationale:** Accessibility is a baseline requirement for an educational platform. AA is the standard that most accessibility regulations and procurement requirements reference.

**Consequences:**
- `eslint-plugin-jsx-a11y` added in Sprint 11–12 and all P0 violations fixed before pilot launch
- Color contrast ratios must meet AA minimums for text and interactive elements
- Keyboard navigation must work for all primary learner flows
- Screen reader support is required for lesson content, quiz questions, and navigation
- WCAG 2.2 AA audit is included in Phase 1 hardening sprint (Sprint 11–12)

---

### DR-021 — Pilot Success Criteria
**Status:** Confirmed  
**Date confirmed:** 2026-11-05

**Decision:** Phase 1 pilot must be evaluated against defined, measurable success criteria before Phase 2 is authorized.

**Pilot success metrics (minimum thresholds for Phase 2 authorization):**

| Category | Metric | Minimum threshold |
|----------|--------|-------------------|
| Onboarding | % of invited users who complete registration and first lesson | ≥ 70% |
| Engagement | Average lessons completed per active user per week | ≥ 3 |
| Retention | % of users active at week 4 of pilot | ≥ 50% |
| Assessment | Average improvement in mastery score from first to second assessment attempt per domain | Positive improvement in ≥ 60% of users |
| AI quality | % of AI responses rated useful by learners (in-app thumbs up/down) | ≥ 70% positive |
| AI cost | AI cost per active user per week | Within pre-approved budget (owner sets before pilot) |
| Feedback | % of pilot users who complete post-pilot survey | ≥ 60% |
| Reliability | Uptime during pilot period | ≥ 99% measured availability |

**What to do if thresholds are not met:** Any metric below threshold triggers a retrospective before Phase 2 authorization. Missing ≥ 3 metrics requires a remediation plan approved by the owner.

**Rationale:** Without defined success criteria, the Phase 1 → Phase 2 decision would be subjective. Measurable thresholds make the retrospective and authorization process objective.

**Consequences:**
- Sprint 11–12 must include in-app AI feedback collection (thumbs up/down on AI responses)
- Backend must track: registration completion, lesson completion per user per week, return visits, mastery score change over time
- Pilot cost budget for AI must be set by owner before pilot launch (see DR-OPEN-010)

---

### DR-022 — Testing Acceptance Criteria
**Status:** Confirmed  
**Date confirmed:** 2026-11-05

**Decision:** Testing targets are defined by acceptance criteria for critical workflows, not by test count alone. The following critical workflows must have explicit acceptance tests before pilot launch:

**Critical workflow tests required:**

| Workflow | Acceptance criteria |
|----------|-------------------|
| Registration and login | New user can register, verify email, log in, and access a lesson without errors |
| Session refresh | A user active for > 15 minutes can continue without re-login (silent refresh working) |
| Authorization | An unauthenticated request to any protected endpoint returns 401; a learner cannot access admin endpoints (403) |
| Lesson completion | Completing a lesson awards correct XP, updates mastery, and persists to database |
| Assessment scoring | A submitted exam is scored correctly server-side; score matches expected values for a known question set |
| Mastery calculation | Mastery scores update correctly after multiple assessment attempts with known inputs |
| Progress migration | A learner's progress from session A is fully visible in session B (different device/browser) |
| Content lifecycle | A DRAFT lesson is not visible to a learner; a PUBLISHED lesson is visible |
| AI safety controls | An AI request that exceeds the daily rate limit returns a graceful error, not a provider error |
| Backup and restore | A database backup can be restored to a clean instance and all data verified intact |

**Test implementation approach:**
- Vitest unit tests: mastery algorithm, XP engine, scoring logic
- Spring Boot integration tests (MockMvc): all critical endpoint behaviors including 401/403 cases
- Playwright E2E tests: registration/login, complete lesson, take exam, view progress
- Manual QA checklist: mobile Safari + Android Chrome; backup/restore procedure documented and tested

**Rationale:** Test count targets ("write 20 Vitest tests") measure effort, not correctness. Acceptance criteria for critical workflows measure whether the system does what it must do.

**Consequences:**
- Sprint 11–12 task list is updated to reference these acceptance criteria rather than raw test counts
- Each critical workflow test must pass before the pilot is opened to invited users
- Backup and restore procedure documented in `docs/operations/` (new, to be created in Sprint 11–12)

---

## 5. Data and Migration Decisions

### DR-019 — Existing Content Migration Approach
**Status:** Confirmed (learner data preservation requirement added — v1.1)  
**Date confirmed:** Phase 0  
**Revised:** 2026-11-05

**Decision:** Existing bootcamp content (Cybersecurity, IBM Python+ML) migrates to the database via a one-time Node.js seed script that calls the backend content API. AI Engineering content requires structural mapping before migration can begin.

**Learner data preservation requirement (added v1.1):** Before removing GistSync or any localStorage-based progress, the following must be completed:
1. **Document all existing progress keys and formats** — a full inventory of every localStorage key used for progress (lesson completion, XP, streaks, lab completion flags, exam attempts, goals) with their data formats
2. **Define and test a migration/export process** — provide existing learners with a way to export their progress before the transition; if any pilot users have existing bootcamp-hub progress, provide a migration path to StudyHub
3. **Validation** — the migration/export process must be tested against real localStorage snapshots before GistSync or localStorage progress keys are removed
4. **Existing learner progress is not disposable** — do not remove progress data or the GistSyncContext until the export/migration process is documented, tested, and communicated to affected users

**Rationale:** A seed script validates the API contract and respects the content lifecycle middleware. Direct SQL insertion bypasses application logic and can introduce inconsistencies.

**Consequences:**
- Seed script is a Phase 1 Sprint 3–4 deliverable
- **New Sprint 1–2 deliverable:** localStorage progress key inventory document (`docs/migration/LEARNER_DATA_INVENTORY.md`)
- **New Sprint 1–2 deliverable:** learner data export/migration tool (or documented process)
- `cyberDays.js` (103 days), `ibmCurriculum.js` (57 days), all question files, and all lab files are inputs to the seed script
- AI Engineering (`aiMonths.js`, `aiQuiz.js`, `aiLabs.js`, `aiNotes.js`) blocked on structural mapping decision (see DR-OPEN-009)
- `cyberLabsNew.js` (72 entries, zero imports) requires investigation and owner decision before migration (see DR-OPEN-008 — revised below)
- Stable ID mapping required: `lab_done_1-1` localStorage keys → database IDs for any pilot user progress migration

---

### DR-020 — GistSync Removal
**Status:** Confirmed (preconditions required before removal — v1.1)  
**Date confirmed:** Phase 0  
**Revised:** 2026-11-05

**Decision:** `GistSyncContext` and all Gist sync functionality are removed in Phase 1. Cross-device progress sync is replaced by server-side storage. However, GistSync must NOT be removed until the learner data preservation preconditions in DR-019 are met.

**Removal preconditions:**
1. Full localStorage progress key inventory is complete (`docs/migration/LEARNER_DATA_INVENTORY.md` exists)
2. Learner data export/migration process is documented and tested
3. Any affected users have been notified and given the opportunity to export their data

**Rationale:** GistSync was acceptable for a personal single-user tool. It is not acceptable for a multi-user platform: PAT tokens in localStorage, single-user GitHub account dependency, no real-time sync. Server-side progress (see DR-011) makes GistSync obsolete. Learner progress, however, is not obsolete — it must be preserved.

**Consequences:**
- GistSync removal is NOT a Sprint 1–2 task — it follows the data preservation work
- `src/context/GistSyncContext.tsx` is deleted only after preconditions are met
- Gist sync section removed from `SettingsPanel.tsx` at the same time
- `LS_TOKEN_KEY = 'gist_token'` localStorage key is cleared and not reused

---

## 6. Open Decisions (Required from Owner Before or During Phase 1)

These are explicitly unresolved. Items marked **[Blocks Sprint 1]** must be answered before development begins.

| ID | Question | Impact |
|----|----------|--------|
| DR-OPEN-001 | ~~Repository rename timing~~ | **Resolved by DR-003 v1.1:** rename deferred to Sprint 11–12 or post-pilot, after deployment verified |
| DR-OPEN-002 | ~~Pilot registration: invite-only (manual), PIN/token gated, or open?~~ | **Resolved:** Invite-only with unique single-use invitation codes. See DR-024. |
| DR-OPEN-003 | Hosting for Phase 1 backend: Render, Railway, DigitalOcean, Fly.io? **[Blocks Sprint 11–12]** | Affects deployment sprint |
| DR-OPEN-004 | Static hosting for Phase 1 SPA: Cloudflare Pages, Vercel, Netlify? **[Blocks Sprint 11–12]** | Affects deployment sprint |
| DR-OPEN-005 | Payment integration in Phase 1 or deferred? | If Phase 1: add ~30hrs to Sprint 11–12 |
| DR-OPEN-006 | ~~PWA in Phase 1?~~ | **Resolved by DR-014 v1.1:** responsive web required; PWA conditional on separate decision after core features stable |
| DR-OPEN-007 | USMLE: who is the qualified medical content reviewer? | USMLE cannot launch without an answer |
| DR-OPEN-008 | `cyberLabsNew.js`: integrate into Cybersecurity content or discard? | **Requires investigation first** — 72 entries must be inspected and reported before a decision is made. This is a pre-decision research task, not a blocked decision. |
| DR-OPEN-009 | AI Engineering curriculum mapping: who does the structural mapping work, and by when? | AI Engineering content cannot migrate until this is done |
| DR-OPEN-010 | ~~Pilot size: how many invited learners, which disciplines at launch, and what is the weekly AI cost budget per user?~~ | **Resolved:** 10 learners, Cybersecurity + IBM Python/ML, $25/week aggregate AI ceiling. See DR-025. |

---

---

### DR-024 — Pilot Registration Model
**Status:** Confirmed  
**Date confirmed:** 2026-11-05 (owner approval, Sprint 1 authorization)

**Decision:** Pilot registration is invite-only. Public registration is disabled. Each invitation is a unique, single-use code. An invitation that has been used cannot be reused. An admin can issue, revoke, and track invitation codes.

**Pilot capacity (Phase 1):** 10 invited learners maximum.

**Admin capabilities required:**
- Issue new invitation codes (individually or in batch)
- Revoke an unused invitation code before it is redeemed
- View all codes: status (unused / redeemed / revoked), issue date, redemption date, redeemed-by user
- Codes must not expose user PII in listing views unless redeemed

**Registration flow:**
1. Admin issues invitation code
2. Owner distributes code to invitee out-of-band (email, message)
3. Invitee registers at `/register?code=<code>` — code is validated before account creation
4. On successful registration the code is marked `REDEEMED` and linked to the new user
5. A code that is already redeemed or revoked returns a clear error — it cannot create another account

**Rationale:** Invite-only registration gives the owner full control over pilot cohort composition and ensures the pilot population is known, intentional, and bounded.

**Consequences:**
- `invitation_codes` table required in Sprint 1 schema
- Admin invitation management endpoints required in Sprint 1
- `/register` endpoint validates invitation code before creating user record
- Sprint 1 UI includes admin invitation management view
- Public registration endpoint (`/register` without valid code) returns 403 or redirects to a holding page
- DR-002 "access is limited to invited learners" is now operationalized

---

### DR-025 — Pilot Scope and AI Budget
**Status:** Confirmed  
**Date confirmed:** 2026-11-05 (owner approval, Sprint 1 authorization)

**Decision:** Phase 1 pilot parameters are as follows.

**Pilot scope:**
- Initial pilot capacity: 10 learners
- Disciplines at launch: Cybersecurity and IBM Python + ML (AI Engineering deferred — see DR-OPEN-009; USMLE deferred — see DR-007 and DR-OPEN-007)
- AI Tutor enabled for all pilot learners from day one

**AI cost controls:**
- Weekly AI spending ceiling: **$25 per week aggregate for the entire pilot** (not per user)
- Per-user weekly AI usage tracking is required
- Aggregate pilot-level cost monitoring is required (not just per-user)
- Configurable rate limits: per-user per-minute bucket + daily cap
- Spending alerts: admin is notified when aggregate weekly spend reaches 80% of ceiling ($20)
- Hard limit enforcement: at 100% of ceiling, AI features are suspended for all users until the next billing period or the owner raises the limit — no unbounded usage under any circumstances
- All AI spend is logged to `ai_usage_log` with: user_id, timestamp, feature type, tokens in, tokens out, estimated cost, model used

**Cost monitoring dashboard (admin):**
- Current week spend vs. ceiling
- Per-user breakdown
- Feature-type breakdown (which AI capability costs most)
- Alert history

**Rationale:** A $25/week aggregate ceiling is appropriate for a 10-learner pilot where AI is enabled for all users. The aggregate (not per-user) model simplifies management. Hard enforcement prevents billing surprises. The 80% alert gives the owner time to intervene before the ceiling is hit.

**Consequences:**
- Sprint 1 schema must include `ai_usage_log` table structure (even if AI features ship in Sprint 9–10, the log table is designed now)
- AI Orchestration module (Sprint 9–10) must implement hard limit enforcement, not just logging
- Admin dashboard (Sprint 11–12) must include cost monitoring view
- DR-021 AI cost metric ("within pre-approved budget") is now operationalized: budget = $25/week aggregate
- USMLE and AI Engineering are NOT in pilot scope; no content migration or AI tutor for those disciplines until separately authorized

---

## 7. Operating Constraints

### DR-023 — Existing Bootcamp Protection and Parallel Development
**Status:** Confirmed — Non-negotiable constraint  
**Date confirmed:** 2026-11-05  
**Priority:** Takes precedence over implementation convenience and the original migration schedule

**Decision:** The existing `bootcamp-hub` application must remain fully operational throughout the entire StudyHub development and pilot preparation process. StudyHub is developed alongside the existing application, not at its expense. The owner is currently using the existing application to prepare for active certification exams and must be able to continue doing so without interruption.

---

#### What must remain intact at all times

The following must remain operational and unmodified until the owner explicitly authorizes the transition (see replacement approval gate below):

- All existing cybersecurity and IBM Python+ML lessons, notes, quizzes, exams, labs, PBQs, and PT labs
- All existing routes (`/cyber`, `/ibm`, `/ai`, `/hub` and their sub-routes)
- All existing React components, context providers, and data files
- Existing localStorage progress (lesson completion flags, XP, streaks, lab/PBQ completion, exam attempts, goals)
- GistSync functionality (`GistSyncContext`) — do not remove or disable until transition is authorized
- Existing navigation, font size controls, theme preferences
- The GitHub Pages deployment at its current URL

---

#### Operating strategy

**1. Treat the existing application as the operational baseline.**  
Every sprint begins with the existing application working. Every sprint ends with the existing application still working. This is not aspirational — it is verified (see Sprint Reporting, below).

**2. Build StudyHub alongside, not on top of.**  
- Sprint 1 creates a new Spring Boot backend in a separate directory, not inside the existing React project
- New frontend routes, components, and state management are introduced incrementally without removing existing ones
- New state management (TanStack Query, Zustand) is added alongside existing contexts, not as a replacement, until transition is authorized
- Separate local development environments: existing bootcamp runs independently of the new backend

**3. Isolate all shared-state changes.**  
- Do not modify localStorage key names or formats used by the existing application
- Do not alter existing data files (`cyberDays.js`, `ibmCurriculum.js`, `aiMonths.js`, `cyberLabsNew.js`, etc.)
- Do not change existing context providers until their replacement is fully tested and transition authorized
- Do not introduce changes that could corrupt existing learner progress

**4. Maintain two operational paths simultaneously.**  
| Path | State | Owner access |
|------|-------|--------------|
| Existing bootcamp (GitHub Pages) | Always operational | Available for study at all times |
| StudyHub dev/staging | Incrementally built | Available for review and testing |

---

#### Replacement approval gate

The existing bootcamp must NOT be disabled, redirected, renamed, or retired until ALL of the following conditions are confirmed in writing by the owner:

1. StudyHub has passed all DR-022 critical workflow acceptance tests
2. All existing curriculum content (Cyber + IBM) has been migrated and validated in StudyHub (lesson counts, quiz counts, lab content, assessment results verified)
3. StudyHub is deployed to production and independently accessible at its own URL
4. A tested rollback procedure exists and has been executed in a staging environment
5. The owner has personally verified the new learning experience end-to-end
6. The owner has issued explicit written authorization to retire the existing bootcamp

No single condition is waivable. The gate is all-or-nothing.

---

#### Sprint reporting requirement

Every sprint report must include a **Bootcamp Protection Checklist** section with explicit confirmation of:

| Check | Expected answer |
|-------|----------------|
| Was the existing bootcamp affected by any change this sprint? | No (or: Yes — [describe what and why, with owner approval reference]) |
| Is the existing study functionality (lessons, quizzes, labs, exams) fully operational? | Yes |
| Was any existing content or learner data modified or deleted? | No |
| Were any existing localStorage keys modified or removed? | No |
| Were any existing data files (`cyberDays.js`, `ibmCurriculum.js`, etc.) changed? | No |
| Regression verification method used: | [e.g. manual smoke test: loaded Cyber Day 1, completed a quiz, verified XP updated] |

If any check cannot be answered with the expected answer, work stops and the owner is notified before proceeding.

---

#### If a planned change risks disrupting the existing bootcamp

**Stop. Do not proceed. Request approval.**

The sprint report for that sprint must describe:
- What change was planned
- Why it risks disrupting the existing application
- What alternatives were considered
- What approval is requested

Owner approval is required in writing before any change that could affect the existing bootcamp is implemented.

---

**Rationale:** The owner has active certification exams for which the existing bootcamp is the primary study tool. A development process that disrupts active study is not acceptable regardless of the development benefits. StudyHub adds capability; it does not subtract existing capability during the transition.

**Consequences:**
- Sprint 1 creates a new Spring Boot project directory and new React routes; it does not modify existing routes or components
- The `GistSyncContext` removal in DR-020 is deferred to after the replacement approval gate, not Sprint 5–6
- Sprint estimates already account for the parallel-development overhead (separate environments, regression checks)
- Any sprint task that is found to require modifying the existing application unexpectedly is flagged, re-scoped, or deferred
- The roadmap sprint order is designed so that early sprints (1–6) are purely additive; structural replacement begins only in later sprints after replacement functionality is verified

---

*End of STUDYHUB_DECISION_RECORD.md*
