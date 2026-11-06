# StudyHub — Target Architecture

**Version:** 1.0  
**Date:** 2026-11-04  
**Status:** Phase 0 — Authoritative design target, do not implement until Phase 1 is authorized  
**Author:** Phase 0 Audit (Claude Sonnet 4.6, session https://claude.ai/code/session_01GrXQBHN1FfE3b1PuBWsGTt)

---

## 1. Architecture Overview

StudyHub is a multi-discipline learning platform targeting IT certifications (CompTIA, IBM), medical licensing (USMLE Step 1), and Nigerian secondary/tertiary exams (WAEC, NECO, JAMB). The platform supports Web (primary), iOS, and Android (Phase 2 native).

The target architecture follows a **React SPA + Spring Boot modular monolith + MySQL** pattern. This is a deliberate choice for a solo developer: a modular monolith provides clean domain boundaries, is simpler to operate than microservices, and can be split later if scale demands it.

```
┌─────────────────────────────────────────────────────────────┐
│                        CLIENTS                              │
│  ┌─────────────┐  ┌──────────────┐  ┌──────────────────┐  │
│  │   Web SPA   │  │  iOS Native  │  │  Android Native  │  │
│  │  React 18   │  │ React Native │  │  React Native    │  │
│  │  Vite 5     │  │   + Expo     │  │    + Expo        │  │
│  └──────┬──────┘  └──────┬───────┘  └────────┬─────────┘  │
└─────────┼────────────────┼───────────────────┼─────────────┘
          │                │                   │
          └────────────────┼───────────────────┘
                           │  HTTPS / REST + JWT
                           │
┌──────────────────────────▼────────────────────────────────┐
│               Spring Boot Modular Monolith                 │
│                                                            │
│  ┌────────────────────────────────────────────────────┐   │
│  │                    API Gateway Layer                │   │
│  │  - JWT authentication (Spring Security)             │   │
│  │  - Rate limiting (Bucket4j or similar)              │   │
│  │  - Request validation                               │   │
│  │  - CORS configuration                               │   │
│  └─────────────────────────┬──────────────────────────┘   │
│                             │                              │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌─────────────┐  │
│  │ Identity │ │Curriculum│ │Assessment│ │  Progress   │  │
│  │ Domain   │ │+Content  │ │ Domain   │ │  Domain     │  │
│  │          │ │ Domain   │ │          │ │             │  │
│  └──────────┘ └──────────┘ └──────────┘ └─────────────┘  │
│                                                            │
│  ┌────────────────────────────────────────────────────┐   │
│  │                 AI Orchestration Layer              │   │
│  │  - Lesson tutor (grounded in approved content)     │   │
│  │  - Explanation generator                           │   │
│  │  - Hint provider                                   │   │
│  │  - Incorrect answer explainer                      │   │
│  │  - Revision session generator                      │   │
│  │  - Usage tracking + cost controls                  │   │
│  └────────────────────────────────────────────────────┘   │
└──────────────────────────┬────────────────────────────────┘
                           │
          ┌────────────────┼────────────────┐
          │                │                │
   ┌──────▼──────┐  ┌──────▼──────┐  ┌──────▼──────┐
   │    MySQL    │  │  AI Provider│  │  File Store │
   │  (Primary)  │  │  (via proxy)│  │  (content   │
   │             │  │  Anthropic  │  │   assets)   │
   └─────────────┘  └─────────────┘  └─────────────┘
```

---

## 2. Frontend Architecture

### 2.1 Web SPA (Phase 1)

**Technology:** React 18 + TypeScript + Vite 5 + Tailwind CSS 3

**Routing:** Switch from `HashRouter` to `BrowserRouter` with server-configured fallback to `index.html`.

```
BrowserRouter
  /                         → Dashboard / Home
  /explore                  → Explore (browse disciplines, courses)
  /learn/:disciplineId      → Learn (active lesson view)
  /practice/:disciplineId   → Practice (quizzes, assessments, labs)
  /progress                 → My Progress (mastery dashboard)
  /settings                 → Settings
  /auth/login               → Login
  /auth/register            → Registration (Phase 1: invite/controlled)
```

**Navigation areas (five, as designed):**
1. **Home** — Personalized dashboard with active goals, XP, streak, recommendations
2. **Explore** — Browse disciplines, courses, difficulty tiers; search
3. **Learn** — Lesson viewer with integrated AI Tutor
4. **Practice** — Standalone quiz, mock exams, lab exercises
5. **My Progress** — Mastery maps, XP history, streak calendar, goal tracking

**State management:**
- TanStack Query (server state — API responses, caching, invalidation)
- Zustand (UI state — navigation state, modal visibility, user preferences)
- localStorage (UI preferences only: theme, font size — not user data)

**Form handling:** React Hook Form + Zod validation

**API client:** Axios or fetch with interceptor for JWT header injection + 401 handling

### 2.2 Native (Phase 2)

**Technology:** React Native + Expo (Managed Workflow)

**Shared:** Business logic, TypeScript types, Zod schemas, API client configuration

**Not shared:** UI components (platform-native patterns differ from web), navigation (React Navigation vs React Router)

**Strategy:** API-first design in Phase 1 ensures native apps are first-class clients. No web-only API contracts.

---

## 3. Backend Architecture

### 3.1 Modular Monolith Structure

**Technology:** Spring Boot 3.x + Java 21 + Spring Security + Spring Data JPA

**Deployment:** Docker container on a cloud VPS (DigitalOcean, Render, or similar) for Phase 1 controlled pilot.

```
com.alphasoft.studyhub/
  ├── StudyHubApplication.java
  ├── common/           # Shared: exceptions, pagination, response envelopes
  ├── config/           # Security config, CORS, rate limiting
  ├── identity/         # Users, roles, authentication, JWT
  │     ├── api/        # REST controllers
  │     ├── domain/     # Entities, repositories
  │     └── service/    # Business logic
  ├── curriculum/       # Disciplines, courses, lessons, content lifecycle
  │     ├── api/
  │     ├── domain/
  │     └── service/
  ├── assessment/       # Questions, exams, submissions, scoring
  │     ├── api/
  │     ├── domain/
  │     └── service/
  ├── progress/         # Mastery records, XP, streaks, goals
  │     ├── api/
  │     ├── domain/
  │     └── service/
  └── ai/               # AI orchestration, grounding, usage tracking
        ├── api/
        ├── domain/
        └── service/
```

### 3.2 Core Domains

#### Identity Domain
- User registration (Phase 1: controlled/invite-only)
- Authentication (email + password → JWT access + refresh tokens)
- Roles: `LEARNER`, `CONTENT_REVIEWER`, `ADMIN`
- Password reset, email verification
- Subscription tier tracking (Freemium model)

#### Curriculum + Content Domain
- Discipline management (Cybersecurity, IBM Python+ML, AI Engineering, USMLE Step 1, WAEC/NECO/JAMB)
- Course and lesson hierarchy (Discipline → Course → Module → Lesson)
- Content lifecycle: `DRAFT → IN_REVIEW → APPROVED → PUBLISHED → DEPRECATED`
- **Medical content rule:** AI-generated content for USMLE cannot auto-publish; must be reviewed and approved by a qualified reviewer before `PUBLISHED` state
- Lesson types: Notes, Video, Lab, Quiz, Cheatsheet
- Content versioning

#### Assessment Domain
- Question bank (MCQ, Multi-select, PBQ, Lab-based)
- Exam creation (timed mock exams, topic-specific quizzes, revision sets)
- Submission processing and scoring
- Incorrect answer explanation delivery (AI-grounded)
- Exam attempt history

#### Progress Domain
- Mastery per topic/competency (not completion percentage)
- XP and level system (migrated from frontend)
- Streak tracking (server-authoritative)
- Goal management (learner sets goal, system tracks toward it)
- Learning lifecycle state: `GOAL → DIAGNOSTIC → PATHWAY → LESSON → EVALUATE → GAP_IDENTIFY → REVISION → MASTERY`
- Cross-device: state lives on server, not in localStorage

### 3.3 AI Orchestration Layer

**All AI calls route through the backend. No AI provider keys in frontend code.**

**Phase 1 AI capabilities:**

| Capability | Trigger | Grounding | Output |
|-----------|---------|-----------|--------|
| Lesson Tutor | Learner asks question during lesson | Lesson content (approved) | Conversational explanation |
| Simplified Explanation | Learner taps "Explain simply" | Lesson content (approved) | Simplified text |
| Guided Hints | Learner requests hint on quiz question | Question + answer options | Hint (never reveals answer) |
| Incorrect Answer Explainer | Learner answers wrong | Question + correct answer + explanation | Why they were wrong + correct reasoning |
| Revision Session | Learner starts revision | Weak topic areas from progress data | Targeted revision content |

**Backend AI contract:**
```java
// All AI requests go through this interface — provider can be swapped
public interface AIOrchestrationService {
    AIResponse generateLessonTutorResponse(TutorRequest request);
    AIResponse generateSimplifiedExplanation(ExplanationRequest request);
    AIResponse generateHint(HintRequest request);
    AIResponse generateIncorrectAnswerExplanation(IncorrectAnswerRequest request);
    AIResponse generateRevisionSession(RevisionRequest request);
}
```

**Controls required at launch:**
- Usage tracking per user per day (prevent runaway costs)
- Rate limiting per user (bucket per minute)
- Maximum token limits per request type
- Cost per request logging
- Circuit breaker (graceful degradation if AI provider unavailable)
- Content grounding: prompts include relevant approved lesson content, never raw user input alone
- AI-generated content in USMLE path → `DRAFT` status only, never auto-publish

---

## 4. Database Architecture

### 4.1 Technology

**MySQL 8.x** — Chosen for strong tooling ecosystem, ACID compliance, compatibility with Spring Data JPA, and low operational overhead for a solo developer.

### 4.2 Core Schema (Conceptual)

```sql
-- Identity
users (id, email, password_hash, role, subscription_tier, created_at, ...)
refresh_tokens (id, user_id, token_hash, expires_at, revoked)

-- Curriculum
disciplines (id, name, slug, description, icon, active)
courses (id, discipline_id, title, slug, description, sequence_order)
modules (id, course_id, title, sequence_order)
lessons (id, module_id, title, lesson_type, content_status, content_body, created_by, approved_by, published_at)
-- lesson_type: NOTES | VIDEO | LAB | QUIZ | CHEATSHEET
-- content_status: DRAFT | IN_REVIEW | APPROVED | PUBLISHED | DEPRECATED

-- Assessment
questions (id, lesson_id, question_type, body, options_json, correct_answer, explanation, domain, difficulty)
-- question_type: MCQ | MULTI | PBQ
exams (id, course_id, title, time_limit_minutes, question_count)
exam_attempts (id, user_id, exam_id, started_at, completed_at, score, answers_json)

-- Progress
user_lesson_progress (id, user_id, lesson_id, status, completed_at)
-- status: NOT_STARTED | IN_PROGRESS | COMPLETED
user_mastery (id, user_id, topic_id, mastery_score, last_assessed_at)
user_xp (id, user_id, total_xp, current_level, streak_days, last_active_date)
user_goals (id, user_id, discipline_id, target_exam, target_date, created_at)

-- AI Usage
ai_usage_log (id, user_id, capability, tokens_used, cost_usd, created_at)
```

---

## 5. Authentication and Security

### 5.1 Authentication Flow

```
1. POST /api/auth/login {email, password}
2. Backend validates → issues JWT access token (15min) + refresh token (7 days)
3. Frontend stores:
   - Access token: in-memory only (NOT localStorage)
   - Refresh token: httpOnly cookie (NOT accessible to JavaScript)
4. API requests include: Authorization: Bearer <access_token>
5. On 401: frontend silently refreshes using POST /api/auth/refresh
6. On refresh failure: redirect to login
```

**Critical:** Access tokens must NOT be stored in localStorage (XSS vulnerability). In-memory storage with refresh cookie is the correct pattern.

### 5.2 Security Requirements

- HTTPS everywhere (no HTTP in production)
- CORS: allow only known client origins
- CSP headers
- SQL injection prevention via JPA parameterized queries (no raw SQL)
- Input validation on all API endpoints (Bean Validation + Zod on frontend)
- Secrets managed via environment variables, never committed to source control
- Rate limiting on auth endpoints (brute force protection)
- Rate limiting on AI endpoints (cost protection)

---

## 6. Content Delivery

### 6.1 Phase 1

- Lesson content served via REST API as JSON
- Rich text content: Markdown stored in DB, rendered on frontend (preserve existing MarkdownRenderer)
- Lab content: structured JSON (steps array) served via API
- No CDN required for Phase 1 controlled pilot scale

### 6.2 Phase 2+

- Asset CDN for images, videos (not required for Phase 1; all current content is text/code)
- Full-text search (Elasticsearch or MySQL FULLTEXT — decision deferred)
- Content caching strategy (Redis or database query caching — deferred)

---

## 7. Deployment Architecture

### 7.1 Phase 1 (Controlled Pilot)

```
                ┌────────────────────────────┐
                │     GitHub (source)        │
                │  main branch protection    │
                │  PR-required merges        │
                └─────────────┬──────────────┘
                              │
                    GitHub Actions CI/CD
                              │
          ┌───────────────────┼───────────────────┐
          │                   │                   │
   ┌──────▼──────┐    ┌───────▼───────┐   ┌──────▼──────┐
   │  Run tests  │    │ Build Docker  │   │ Deploy Web  │
   │  Frontend   │    │ image (BE)    │   │ SPA (CDN or │
   │  Backend    │    └───────┬───────┘   │ static host)│
   └─────────────┘            │           └─────────────┘
                       ┌──────▼──────┐
                       │   Cloud VPS │
                       │  Docker     │
                       │  Spring Boot│
                       │  MySQL      │
                       └─────────────┘
```

**Phase 1 target: single VPS deployment** (DigitalOcean Droplet or Render.com). Docker Compose manages backend + MySQL. Web SPA deployed to CDN (Cloudflare Pages, Vercel, or Netlify — TBD).

### 7.2 Environments

- **Local development:** `docker-compose up` for backend + MySQL; Vite dev server for frontend
- **Staging:** Identical to production, separate VPS or Docker context, controlled data
- **Production:** Pilot environment, controlled access

---

## 8. PWA Assessment

**Question from Phase 0:** Should StudyHub support PWA (Progressive Web App) on web, in addition to native iOS/Android in Phase 2?

**Analysis:**
- PWA can provide offline capability, install prompts, push notifications on web
- Native Phase 2 (React Native + Expo) provides first-class iOS/Android experience
- PWA and native are not mutually exclusive
- For the Phase 1 controlled pilot (web-only), PWA would add meaningful value: offline study, home screen install
- `vite-plugin-pwa` with Workbox makes PWA implementation tractable

**Recommendation:** Add PWA support in Phase 1 (after core features are stable). Implement:
- Web App Manifest with StudyHub branding
- Service Worker with network-first strategy for API, cache-first for static assets
- Offline fallback page

**Decision required by owner before Phase 1 implementation begins.**

---

## 9. Subscription and Monetization Architecture

**Model:** Freemium with localized pricing (Nigeria: NGN, global: USD)

**Tiers (Phase 1 scope — to be confirmed):**
- Free: limited content access, limited AI interactions per day, no mock exams
- Pro: full content access, expanded AI usage, unlimited mock exams, offline (PWA)
- (Team/Enterprise: post-Phase 1)

**Backend requirements:**
- Subscription status stored on `users` table
- Subscription checks on protected endpoints via Spring Security annotations
- Localized pricing logic (at minimum: NGN vs USD display, Paystack + Stripe)
- Payment provider integration: **deferred to post-Phase 1** unless owner prioritizes

---

## 10. Decisions Required Before Phase 1

The following are explicitly left for owner approval before implementation begins:

1. **PWA in Phase 1?** (Recommended: yes, after core features stable)
2. **Hosting provider for Phase 1 backend?** (Render, Railway, DigitalOcean, Fly.io)
3. **Static hosting for Phase 1 SPA?** (Cloudflare Pages, Vercel, Netlify)
4. **Payment integration in Phase 1 or later?** (Paystack + Stripe, or deferred)
5. **Invite-only registration for pilot, or open registration?**
6. **Pilot user count and disciplines for Phase 1?**
7. **USMLE content — who is the qualified reviewer for AI-generated medical content?**

---

*End of STUDYHUB_TARGET_ARCHITECTURE.md*
