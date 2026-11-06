# StudyHub — Current State Audit

**Version:** 1.0  
**Date:** 2026-11-04  
**Status:** Phase 0 — Authoritative baseline, do not modify without owner approval  
**Author:** Phase 0 Audit (Claude Sonnet 4.6, session https://claude.ai/code/session_01GrXQBHN1FfE3b1PuBWsGTt)

---

## 1. Executive Summary

The existing repository (`bootcamp-hub`, locally named `bootcamp-react`) is a production-deployed, single-developer SPA built in Vite + React + TypeScript + Tailwind CSS. It contains substantial, working educational content across three IT-certification bootcamps (Cybersecurity, IBM Python + ML, AI Engineering) and a fully functional gamification engine.

**What this codebase is:**  
A polished, mobile-responsive study companion for one developer's personal learning. It has real users (GitHub Pages deployment, cross-device Gist sync) and real content (thousands of quiz questions, notes entries, and lab steps).

**What this codebase is not:**  
A multi-tenant platform. It has no backend, no database, no authentication, no content management system, no test coverage, no CI/CD pipeline, and no scalable content delivery mechanism.

**Phase 0 verdict:**  
The codebase is a strong content and UI foundation. Its architecture is fundamentally incompatible with the StudyHub product vision and must be replaced rather than incrementally patched. The content and design system are valuable assets that migrate cleanly. No existing code should be modified during Phase 0.

---

## 2. Repository Identity

| Property | Current Value | Required Value |
|----------|--------------|----------------|
| Repository name | `bootcamp-hub` | `studyhub` |
| npm package name | `bootcamp-react` | `studyhub` |
| Vite base path | `/bootcamp-hub/` | `/studyhub/` |
| GitHub Pages URL | `alphasoftjb.github.io/bootcamp-hub/` | `alphasoftjb.github.io/studyhub/` |
| Deployment target | `dist/` → `repo-push/` → push to main | TBD (Phase 1) |

---

## 3. Technology Stack

### 3.1 Frontend (Existing)

| Dependency | Version | Classification | Notes |
|------------|---------|----------------|-------|
| React | 18.3.1 | Core | Good — matches target |
| TypeScript | 5.2.2 | Core | Good — strict mode on |
| Vite | 5.3.4 | Build tool | Good — keep for Phase 1 |
| Tailwind CSS | 3.4.17 | Styling | Good — keep for Phase 1 |
| React Router | 6.30.6 | Routing | Good — matches target |
| lucide-react | 1.61.0 | Icons | Good |
| autoprefixer | 10.6.1 | **Tech debt** | Listed as runtime dep; should be devDependency |
| postcss | 8.5.31 | **Tech debt** | Listed as runtime dep; should be devDependency |

### 3.2 Missing Stack Components

| Component | Current Status | Required for Phase 1 |
|-----------|---------------|---------------------|
| Test framework (Vitest/Jest) | **Absent** | Yes |
| State management (Zustand) | **Absent** | Yes (server state grows complex) |
| Data fetching (TanStack Query) | **Absent** | Yes (once backend exists) |
| PWA plugin (vite-plugin-pwa) | **Absent** | Evaluate during Phase 1 |
| Form validation (React Hook Form / Zod) | **Absent** | Yes |
| CI/CD (.github/workflows) | **Absent** | Yes — before Phase 1 ships |
| Backend | **Absent** | Yes — Spring Boot (Phase 1) |
| Database | **Absent** | Yes — MySQL (Phase 1) |
| Authentication | **Absent** | Yes (Phase 1) |
| Docker / containerization | **Absent** | Yes (Phase 1 backend) |

### 3.3 Build Configuration

**`vite.config.ts`:**
- Base path: `/bootcamp-hub/` — requires update on rename
- Output: `dist/` directory
- `chunkSizeWarningLimit: 2000` — elevated; indicates large data files shipped in JS bundle
- No path aliases (`@/` shorthand absent — will cause friction as codebase grows)
- No PWA plugin

**`tsconfig.app.json`:**
- `strict: true` — good
- `allowJs: true` — required; all data files are plain `.js`
- `checkJs: false` — data files unchecked; acceptable for now
- No path aliases defined

**`.eslintrc.cjs`:**
- Standard React + TypeScript + react-refresh rules
- No custom rules; no accessibility linting (eslint-plugin-jsx-a11y absent)

---

## 4. Application Architecture

### 4.1 Routing

```
HashRouter (react-router-dom)
  /          → HomePage
  /cyber     → CyberPage
  /ibm       → IBMPage
  /ai        → AIPage
```

- **HashRouter** is used because GitHub Pages cannot handle client-side routing without server configuration. This is a valid workaround for static hosting.
- Phase 1 (Spring Boot backend) must switch to `BrowserRouter` and configure the server to serve `index.html` for all routes.
- Route structure maps one-to-one with bootcamps. The planned five navigation areas (Home, Explore, Learn, Practice, My Progress) require a complete routing redesign.

### 4.2 Layout Shell (`App.tsx`)

The app uses a persistent shell (`AppShell`) with:
- **SideNav** (desktop, `md:flex`, width `w-20 lg:w-56`) — icon + text navigation
- **BottomNav** (mobile, `md:hidden`) — 4-item icon nav
- **FontSizeBar** — sticky top bar for text size control
- **XPBar** — global experience points display
- Main content area: `max-w-3xl mx-auto` with `border-x` on desktop

**Assessment:** The layout pattern (persistent side rail + scrollable content column) is good and maps well to the StudyHub IA. However, nav items are hardcoded to the three current bootcamps. The nav must become data-driven and support the five planned areas.

### 4.3 State Architecture

All application state currently lives in one of three places:

| State Location | Contents | Migration path |
|----------------|----------|---------------|
| `localStorage` | All progress (`lab_done_*`), XP/streak (`bootcamp_xp_v1`), settings (`bh_theme`, `bh_fontSize`), Gist token + ID | Backend (Phase 1) |
| React Context (`XPContext`) | In-memory XP state; reads/writes to localStorage | Replace with server-side progress API |
| React Context (`SettingsContext`) | Theme and font size; reads/writes to localStorage | UI preferences can remain in localStorage |
| React Context (`GistSyncContext`) | Cross-device sync via GitHub Gist API | Replace with backend sync (Phase 1) |

**Critical finding:** The localStorage key scheme `lab_done_${id}` uses inconsistent delimiters (`lab_done_${id}-` and `lab_done_${id}_` both observed). This must be fully documented before migration.

### 4.4 Cross-Device Sync (GistSyncContext)

The Gist sync implementation:
- Collects all `lab_done_*` keys from localStorage
- Creates a private GitHub Gist (`bootcamp-hub-progress.json`) on first push
- Updates that same Gist on subsequent pushes
- On pull: wipes all existing `lab_done_*` keys and replaces with Gist content

**Security assessment:**
- GitHub PAT is stored in `localStorage` in plain text — acceptable for a single-user personal tool, unacceptable for a multi-tenant platform
- Gist ID stored in localStorage — no security concern
- Token scope is `gist` only — appropriately scoped
- **Migration:** This entire mechanism is replaced by the backend progress API in Phase 1. PAT storage in localStorage must NOT carry forward.

### 4.5 XP & Gamification Engine (`XPContext`)

```
Levels: Rookie(0) → Apprentice(500) → Practitioner(1500) → 
        Engineer(3500) → Expert(6000) → Master(10000)

Events:
  markDayDone(key) → +100 XP (once per key, via localStorage guard)
  recordQuizWin(key, score, total) → +50 XP if ≥70%, +70 XP if 100%
  Streak bonus: +25 XP tracked but not currently awarded on events

XP storage key: 'bootcamp_xp_v1'
Streak calculation: based on lastActiveDate (ISO date string) vs. yesterday
```

**Assessment:** The gamification model is well-designed. The level thresholds and event types map well to the StudyHub competency model. Phase 1 must move all this state to the backend.

---

## 5. Data Architecture

### 5.1 Content Data Files

All content is stored as plain JavaScript `.js` files exported as module-level constants. There is no CMS, no database, no API. Data files are bundled directly into the JavaScript output.

**Consequence of bundling:** The `chunkSizeWarningLimit: 2000` in vite.config indicates Vite is warning about large chunks. All content is shipped to the client on first load. This is unacceptable for a multi-discipline platform with USMLE + WAEC/NECO/JAMB content.

#### Cybersecurity Bootcamp Data

| File | Export | Size | Shape | Notes |
|------|--------|------|-------|-------|
| `cyberDays.js` | `CYBER_DAYS` | 115 entries | `{id, phase, week, notes, quiz, exercises, practical}` | 103/115 populated |
| `cyberCertExam.js` | `CYBER_CERT_EXAM` | 100 questions | Object `{'0'…'99'}` with `{type, domain, q, options, answer, explanation}` |  |
| `networkCertExam.js` | `NETWORK_CERT_EXAM` | 100 questions | Same shape | |
| `linuxCertExam.js` | `LINUX_CERT_EXAM` | 100 questions | Same shape | |
| `pythonCertExam.js` | `PYTHON_CERT_EXAM` | 100 questions | Same shape | |
| `devSecOpsCertExam.js` | `DEVSECOPS_CERT_EXAM` | 100 questions | Same shape | |
| `networkPBQLabs.js` | `NETWORK_PBQ_LABS` | 40 PBQ drills | Flat object with topic keys | 6 topic areas |
| `networkPTLabs.js` | `NETWORK_PT_LABS` | 24 labs | Object keyed `'1-1'` through `'5-24'` | Packet Tracer labs |
| `securityPlusLabs.js` | `SECURITY_PLUS_LABS` | 18 labs | Same shape as PT labs | |
| `cyberLabs.js` | `CYBER_LABS` | 76 entries | Object keyed `1…N` | Actively imported |
| `cyberLabsNew.js` | — | 72 entries | Same shape | **ORPHAN — zero imports** |

**⚠️ cyberLabsNew.js is an orphan file.** It has 72 entries and is imported nowhere in the codebase. It appears to be a draft or replacement for `cyberLabs.js` that was never integrated. It must be reviewed before migration: either integrate its content or remove it. Do not delete during Phase 0.

#### IBM Python + ML Bootcamp Data

| File | Export | Size | Shape | Notes |
|------|--------|------|-------|-------|
| `ibmCurriculum.js` | `IBM_CURRICULUM` | 57 days across 3 phases | Object `{'0','1','2'}` → weeks → days | Phase keys are numeric strings |
| `ibmLabs.js` | `IBM_LABS` | 55 entries | `{title, tool, difficulty, objective, steps[]}` | `steps[]` are strings, some are code |

#### AI Engineering Bootcamp Data

| File | Export | Size | Shape | Notes |
|------|--------|------|-------|-------|
| `aiMonths.js` | `AI_MONTHS` | 13 months defined | `{id, phase, phaseTitle, title, days[]}` | **ALL weeks/days arrays EMPTY** |
| `aiQuiz.js` | `AI_QUIZ` | 375 items | Quiz question objects | |
| `aiNotes.js` | `AI_NOTES` | 375+ items | Note objects | |
| `aiLabs.js` | `AI_LABS` | 390 entries | Lab objects | |
| `aiLabSetup.js` | `AI_LAB_SETUP` | 348 lines | Object `{1…13}` per month | Environment setup + weekly tasks |
| `aiCertExam.js` | `AI_CERT_EXAM` | 96 questions | `{type, domain, q, options, answer, explanation}` | |

**⚠️ AI Engineering has a structural decoupling problem.** The `aiMonths.js` curriculum structure (the backbone) has 13 months with empty `days[]` arrays. All the actual content (375 quiz items, 375+ notes, 390 labs) exists in separate files and is wired together by index in the AIPage component, not by structured curriculum references. The curriculum is a shell; the content is floating. This must be reconciled before migration.

### 5.2 Data Quality Summary

| Bootcamp | Curriculum Complete | Assessment Ready | Labs Complete | Known Issues |
|----------|--------------------|--------------------|---------------|--------------|
| Cybersecurity | 103/115 days (90%) | ✅ 500 questions | ✅ 76+ labs | Orphan `cyberLabsNew.js` |
| IBM Python + ML | ✅ 57 days | ✅ 100 questions | ✅ 55 labs | Numeric phase keys |
| AI Engineering | ❌ 0/13 months populated | ✅ 96 questions | ✅ 390 labs | Days array structurally empty |

### 5.3 TypeScript Type Definitions (`src/types/index.ts`)

Types defined: `LabStep`, `Lab`, `QuizQuestion`, `CyberDay`, `IBMDayRaw`, `IBMWeek`, `IBMCourse`, `IBMDay`, `AIDay`, `AIMonth`.

**Assessment:** Types are defined but inconsistently used — data files are plain `.js` and are untyped. The type system exists for component props, not data integrity. Phase 1 requires JSON Schema validation or Zod schemas at the API boundary.

---

## 6. UI Components

### 6.1 Component Inventory

| Component | Function | Quality | Notes |
|-----------|----------|---------|-------|
| `CertExam.tsx` | Full mock exam engine: MCQ, Multi-select, PBQ | High | Well-structured; supports 3 question types |
| `QuizEngine.tsx` | Inline quiz within day content | Good | Per-day quiz with XP integration |
| `MarkdownRenderer.tsx` | Renders markdown notes content | Good | Custom renderer, no external library |
| `HandsOnLabsTab.tsx` | Displays lab content with steps | Good | |
| `PacketTracerLabsTab.tsx` | PT lab viewer | Good | |
| `ExercisesTab.tsx` | Exercise display | Good | |
| `DailyLab.tsx` | Daily lab wrapper | Good | |
| `SettingsPanel.tsx` | Settings bottom sheet (theme, font, Gist sync) | High | Mobile-first bottom sheet pattern |
| `XPBar.tsx` | Persistent XP/level display | Good | |
| `XPToast.tsx` | XP gain notification | Good | |

**Notable:** `CertExam.tsx` is the most architecturally complete component. Its question type system (MCQ/Multi/PBQ) is reusable across disciplines and should be preserved during migration.

### 6.2 Design System (`index.css`)

The app has a mature CSS custom property design system:

```css
/* Background layers */
--bg-base:     #07090f   /* Deepest layer */
--bg-surface:  #0e1420   /* Cards/panels */
--bg-card:     rgba(15, 23, 42, 0.6)
--bg-input:    #1e293b
--bg-elevated: #1e293b

/* Borders */
--border:    #1e293b
--border-md: #334155

/* Text */
--text-primary:   #f1f5f9
--text-secondary: #94a3b8
--text-muted:     #475569
--text-dim:       #1e293b

/* Code */
--code-bg:   #0f172a
--code-text: #6ee7b7
```

Light theme is fully implemented via `[data-theme="light"]` overrides.

**Typography:** Inter (body) + JetBrains Mono (code) via Google Fonts. Font size is dynamically controlled by JavaScript (SettingsContext sets `document.documentElement.style.fontSize`).

**Assessment:** The design token system is well-structured and maps closely to the StudyHub visual direction (deep navy/charcoal backgrounds, violet/indigo accents). The existing tokens are a strong starting point for the Phase 1 design system.

---

## 7. Accessibility Assessment

**Current state: Partial compliance, not audited against WCAG 2.2 AA.**

Positive observations:
- Semantic HTML used in most components
- `aria-label` present on icon-only buttons (font size controls)
- Color contrast appears reasonable in dark mode (not measured)
- Touch targets generally adequate on mobile

Known gaps:
- No eslint-plugin-jsx-a11y — accessibility issues not caught at lint time
- Font resize control does not persist focus correctly in some cases
- No skip navigation links
- Color is used as the only differentiator for active nav state in some contexts
- No keyboard navigation audit performed
- Screen reader testing not performed

**Target:** WCAG 2.2 AA required for Phase 1 release.

---

## 8. Performance Assessment

**Bundle size concern:** Data files are bundled into the JS output. With the current content volume, the production bundle is substantial. Adding USMLE + WAEC/NECO/JAMB content to the same bundling model would create an unacceptably large initial load.

**No lazy loading:** All four page components are imported statically in `App.tsx`. React Router `lazy()` with `Suspense` is not used.

**No code splitting:** Vite's default code splitting is not explicitly configured. Large data files likely land in the main bundle.

**No PWA:** No service worker, no offline capability, no install prompt.

**Rendering:** Standard React SPA rendering. No SSR, no SSG.

---

## 9. Security Assessment

| Risk | Severity | Notes |
|------|----------|-------|
| GitHub PAT in localStorage | Medium (acceptable for personal tool) | **Must not carry forward** to multi-user platform |
| No backend = no server-side auth | High (for multi-user) | Phase 1 must implement proper auth |
| No secrets in source code | ✅ Clean | No .env files, no hardcoded tokens found |
| No CSP headers | Medium | Static hosting limitation; Phase 1 backend must set headers |
| No rate limiting | N/A currently | Required in Phase 1 (AI endpoints especially) |
| External API calls from frontend | Acceptable (Gist API only) | AI API calls must route through backend in Phase 1 |

---

## 10. What Must Be Built New

The following do not exist and must be built from scratch in Phase 1:

1. **Spring Boot modular monolith backend** — Identity, Curriculum+Content, Assessment, Progress domains
2. **MySQL database** — All data currently in flat JS files
3. **Authentication system** — JWT + Spring Security
4. **Content management lifecycle** — Draft → In Review → Approved → Published → Deprecated
5. **AI integration layer** — Backend orchestration, grounding, rate limiting, usage tracking
6. **Testing infrastructure** — Vitest (frontend), JUnit 5 (backend), Playwright (E2E)
7. **CI/CD pipeline** — GitHub Actions
8. **New routing structure** — Five navigation areas replacing current four hardcoded routes
9. **Multi-discipline content model** — Unified schema that handles IT certs, USMLE, WAEC/NECO/JAMB

---

## 11. What Migrates from the Existing Codebase

| Asset | Migration Action |
|-------|-----------------|
| Design token system (`index.css`) | Carry forward as-is into Phase 1; serves as design foundation |
| `CertExam.tsx` component | Migrate; adapt for server-fetched question data |
| `QuizEngine.tsx` | Migrate; adapt for server-fetched data |
| `MarkdownRenderer.tsx` | Migrate as-is |
| XP/Level model (thresholds and events) | Migrate logic to backend; replicate UI |
| `SettingsContext` (theme/font) | Keep localStorage-based settings; these are UI preferences, not data |
| Cyber content (notes, quizzes, labs) | Load into CMS/database as Phase 1 seed data |
| IBM content | Load into CMS/database |
| AI content | Load into CMS/database (after resolving structural decoupling) |

| Asset | Do Not Migrate |
|-------|---------------|
| `GistSyncContext` | Replaced by backend progress API |
| XP state from localStorage | Cannot migrate existing user data to backend without identity |
| `cyberLabsNew.js` | Orphan — must be reviewed first |
| `App.css` | Unused Vite scaffold boilerplate |

---

## 12. Known Technical Debt

| Item | Severity | Effort | Notes |
|------|----------|--------|-------|
| `autoprefixer` / `postcss` as runtime deps | Low | Trivial | Move to devDependencies |
| Zero test coverage | Critical | High | Required before Phase 1 ships |
| Zero CI/CD | Critical | Medium | Required before Phase 1 ships |
| `cyberLabsNew.js` orphan | Medium | Low | Review and either integrate or remove |
| AI curriculum structural decoupling | Medium | Medium | Reconcile before content migration |
| Data files bundled in JS | High | High | Requires backend/API move |
| No path aliases in vite/tsconfig | Low | Trivial | Add `@/` alias in Phase 1 |
| No lazy route loading | Medium | Low | Add before Phase 1 adds more routes |
| localStorage key inconsistency (`-` vs `_`) | Medium | Low | Document and standardize before migration |
| Mixed `.js` / `.tsx` data files | Low | Medium | Migrate data to TypeScript in Phase 1 |

---

*End of STUDYHUB_CURRENT_STATE_AUDIT.md*
