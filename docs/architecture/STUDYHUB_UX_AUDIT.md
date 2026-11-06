# StudyHub — UX Audit

**Version:** 1.0  
**Date:** 2026-11-04  
**Status:** Phase 0 — Authoritative UX baseline and design direction  
**Author:** Phase 0 Audit (Claude Sonnet 4.6, session https://claude.ai/code/session_01GrXQBHN1FfE3b1PuBWsGTt)

---

## 1. Executive Summary

The existing UI is well-crafted for a single-developer personal tool. It has a consistent dark-first design system, good mobile responsiveness, and an effective persistent layout (sidebar + content column). The core interaction patterns are solid and migrate well to StudyHub.

The fundamental UX problem is not quality — it is scope. The current navigation exposes three hardcoded bootcamps. StudyHub requires a platform-wide IA supporting multi-discipline browsing, authenticated learner state, goal-setting, and AI-assisted learning — none of which exist today. The UI must be rebuilt around the new IA, not patched.

**What is preserved:** Visual identity, design token system, gamification UI patterns, component interaction patterns (`CertExam`, quiz flows, lab viewers).

**What is rebuilt:** Information architecture, navigation structure, home/dashboard, explore experience, lesson view (with AI Tutor), progress view.

---

## 2. Current State Evaluation

### 2.1 Navigation

**Current:** 4-item nav (Hub / Cyber / IBM / AI) — hardcoded to current bootcamps.

**Mobile (bottom nav):**
- 4 items with icon + 10px label
- Active state uses per-bootcamp color (red for Cyber, orange for IBM, violet for AI)
- Touch targets ~40px — acceptable

**Desktop (side rail):**
- Collapsible: icon-only at `w-20`, expanded at `lg:w-56`
- Active state: colored tint background + colored icon
- Footer hint text shows content counts (small, supplementary detail)

**Assessment:** The layout pattern (persistent side rail on desktop, bottom bar on mobile) is exactly right for StudyHub. The color-per-item active state is attractive but doesn't scale to 5 areas with multi-discipline content. The active indicator pattern should evolve toward a consistent accent color system.

### 2.2 Home Page

**Current:** Grid of 3 bootcamp cards with progress rings, XP/level display, streak counter, and resume buttons.

**Strengths:** Clear at-a-glance progress summary. XP/level display motivates return visits. Progress rings are an effective visual.

**Gaps for StudyHub:**
- No personalized learning recommendations
- No goal display ("Your target: CompTIA Security+ on [date]")
- No AI-assisted next-action suggestion
- Assumes exactly 3 disciplines (hardcoded CAMPS array)
- No onboarding state for new learners

### 2.3 Lesson View (CyberPage, IBMPage, AIPage)

**Current (CyberPage as reference):**
- Phase selector → week expander → day list → day detail
- Day detail has tabs: Notes | Quiz | Exercises | Labs | PT Labs
- Notes tab renders markdown via `MarkdownRenderer`
- Quiz tab uses embedded `QuizEngine`
- Cert Exam accessible from phase header

**Strengths:**
- Hierarchical navigation (phase → week → day) is effective for a linear curriculum
- Tab pattern for different content types within a day works well
- Quiz immediately follows notes — good learning flow
- Cert Exam is a polished mock exam experience

**Gaps for StudyHub:**
- No AI Tutor panel or entry point
- No "explain this" / "show me an example" / "give me a hint" affordance
- No lesson completion tracking UI (progress persists but no explicit "mark complete" UX)
- No next lesson navigation
- No visibility into mastery state from within a lesson
- Phase/week hierarchy is curriculum-specific — won't generalize to all disciplines

### 2.4 Settings Panel

**Current:** Bottom sheet with theme toggle, font size controls, and GitHub Gist sync.

**Assessment:** The bottom sheet pattern is well-implemented. Font size control is a genuinely useful accessibility feature that should be preserved. The Gist sync section will be removed (replaced by backend sync) but the visual pattern can inform similar settings.

### 2.5 XP and Gamification

**Current:** Persistent `XPBar` at top of content area showing current XP, level badge, level name, and progress bar to next level. `XPToast` shows earned XP notifications.

**Assessment:** The gamification system is well-designed. The persistent XP bar is motivating without being intrusive. The level names (Rookie → Apprentice → Practitioner → Engineer → Expert → Master) are well-calibrated for a learning platform.

---

## 3. Target Information Architecture

### 3.1 Five Navigation Areas

```
StudyHub
├── Home          — Personalized dashboard (XP, streak, goals, recommendations)
├── Explore       — Browse disciplines, courses, search content
├── Learn         — Active lesson viewer with AI Tutor
├── Practice      — Standalone assessments, mock exams, revision sessions
└── My Progress   — Mastery map, XP history, streak calendar, goal tracking
```

### 3.2 Key User Flows

#### New Learner Onboarding
```
Register → Set Goal (target exam + date) → Take Diagnostic → 
Get Recommended Learning Path → Begin First Lesson
```

#### Daily Learning Loop
```
Home (see streak, XP, recommended next lesson) → 
Learn (open lesson → read notes → AI Tutor if needed → complete quiz → 
       check in correct/incorrect explanations → mark complete) → 
XP earned → Home updated
```

#### Assessment Flow
```
Practice → Select exam type → Take timed mock exam → 
Review answers (AI explanations for wrong answers) → 
See domain breakdown → Weak areas surfaced → 
Recommended revision session generated
```

#### Revision Flow
```
My Progress (see weak topics) → Start Revision Session → 
Practice delivers targeted question set → 
Improvement in mastery score reflected
```

---

## 4. Wireframes — Desktop

> These wireframes use ASCII notation. See `docs/design/STUDYHUB_VISUAL_DIRECTION.md` for visual design system and refined mockups.

### 4.1 App Shell (Desktop)

```
┌──────────────────────────────────────────────────────────────────┐
│ ┌──────────┐ ┌──────────────────────────────────────────────────┐│
│ │          │ │  [XP Bar: Level "Engineer" ████░░░░ 3,420/3,500] ││
│ │  SIDE    │ ├──────────────────────────────────────────────────┤│
│ │  RAIL    │ │                                                   ││
│ │          │ │            [Main Content Area]                    ││
│ │ 🏠 Home  │ │                                                   ││
│ │ 🔍 Explore│ │         max-width: 768px, centered               ││
│ │ 📚 Learn  │ │         border-x on desktop                      ││
│ │ ✏️ Practice│ │                                                   ││
│ │ 📊 Progress│ │                                                   ││
│ │          │ │                                                   ││
│ │ ─────── │ │                                                   ││
│ │ ⚙️ Settings│ │                                                   ││
│ │ 👤 Profile │ │                                                   ││
│ └──────────┘ └──────────────────────────────────────────────────┘│
└──────────────────────────────────────────────────────────────────┘

Side rail: w-16 (icon only) | lg:w-56 (icon + label)
Active item: violet/indigo tint + filled icon (consistent accent, not per-item color)
```

### 4.2 Home / Dashboard (Desktop)

```
┌── Home ──────────────────────────────────────────────────────────┐
│                                                                   │
│  Good morning, Maewada 👋                                        │
│  Streak: 🔥 7 days   XP: 3,420   Level: Engineer                │
│                                                                   │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │  Your Goal                                               │    │
│  │  CompTIA Security+   [Target: Jan 30, 2027]             │    │
│  │  ████████████░░░░░░░░  60% complete                     │    │
│  │                                 [Continue →]            │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                   │
│  Continue where you left off                                      │
│  ┌────────────────────────────┐                                  │
│  │ Day 45 · Security+         │                                  │
│  │ Cryptography Fundamentals  │                                  │
│  │ 15 min · Quiz pending      │                                  │
│  │              [Resume →]    │                                  │
│  └────────────────────────────┘                                  │
│                                                                   │
│  Your disciplines                                                 │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐                │
│  │🛡️ CyberSec │  │🤖 IBM ML   │  │🧠 AI Eng   │                │
│  │ 45/115 ✓  │  │ 12/57 ✓   │  │ 0/13 mos   │                │
│  └────────────┘  └────────────┘  └────────────┘                │
│                                                                   │
└───────────────────────────────────────────────────────────────────┘
```

### 4.3 Explore (Desktop)

```
┌── Explore ───────────────────────────────────────────────────────┐
│                                                                   │
│  What do you want to learn?                                      │
│  ┌────────────────────────────────────────────────────┐         │
│  │  🔍  Search courses, topics, disciplines...        │         │
│  └────────────────────────────────────────────────────┘         │
│                                                                   │
│  IT Certifications                                                │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │ 🛡️ Cyber     │  │ 🤖 IBM ML    │  │ 🧠 AI Eng    │          │
│  │ 3 courses    │  │ 1 course     │  │ 1 course     │          │
│  │ 500 Q's      │  │ 100 Q's      │  │ 96 Q's       │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
│                                                                   │
│  Medical Licensing                                                │
│  ┌──────────────┐                                                │
│  │ 🩺 USMLE     │                                                │
│  │ Coming soon  │                                                │
│  └──────────────┘                                                │
│                                                                   │
│  Nigerian Exams                                                   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │ 📝 WAEC      │  │ 📝 NECO      │  │ 📝 JAMB      │          │
│  │ Coming soon  │  │ Coming soon  │  │ Coming soon  │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
│                                                                   │
└───────────────────────────────────────────────────────────────────┘
```

### 4.4 Learn — Lesson View (Desktop)

```
┌── Learn ─────────────────────────────────────────────────────────┐
│                                                                   │
│  ← Security+  /  Week 2  /  Day 45                              │
│  Cryptography Fundamentals                            [⊕ Complete]│
│  ─────────────────────────────────────────────────────────────── │
│                                                                   │
│  [Notes]  [Quiz]  [Labs]  [Cheatsheet]                          │
│                                                                   │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                                                          │    │
│  │   Lesson content (MarkdownRenderer)                     │    │
│  │                                                          │    │
│  │   Symmetric vs Asymmetric Encryption                    │    │
│  │                                                          │    │
│  │   Symmetric encryption uses the same key for             │    │
│  │   encryption and decryption...                           │    │
│  │                                                          │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                   │
│  ┌── AI Tutor ─────────────────────────────────────────────┐    │
│  │  💬 Ask a question about this lesson                     │    │
│  │  ┌──────────────────────────────────────────────────┐  │    │
│  │  │  "What's the difference between AES and RSA?"    │  │    │
│  │  └──────────────────────────────────────────────────┘  │    │
│  │  [Explain simply]  [Give me an example]  [Ask →]       │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                   │
│  ← Previous lesson          Next lesson: PKI Deep Dive →        │
│                                                                   │
└───────────────────────────────────────────────────────────────────┘
```

### 4.5 Practice — Mock Exam (Desktop)

```
┌── Practice ──────────────────────────────────────────────────────┐
│                                                                   │
│  CompTIA Security+ Mock Exam                       ⏱ 44:32      │
│  Question 23 of 100          Domain: Cryptography                │
│                                                                   │
│  ─────────────────────────────────────────────────────────────── │
│                                                                   │
│  A security administrator needs to implement a solution that     │
│  allows two parties who have never met to securely exchange      │
│  encryption keys over an untrusted network. Which solution       │
│  BEST meets this requirement?                                    │
│                                                                   │
│  ○  A.  Symmetric key exchange using AES-256                    │
│  ○  B.  Diffie-Hellman key exchange                             │
│  ○  C.  Digital certificate with RSA                            │
│  ○  D.  HMAC with SHA-256                                       │
│                                                                   │
│  ─────────────────────────────────────────────────────────────── │
│                                                                   │
│  [Flag for review]           [← Prev]      [Next →]            │
│                                                                   │
└───────────────────────────────────────────────────────────────────┘
```

### 4.6 My Progress (Desktop)

```
┌── My Progress ───────────────────────────────────────────────────┐
│                                                                   │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ 🔥 7 day streak    ⚡ 3,420 XP    🏆 Engineer            │    │
│  │ Next: Expert (3,500 XP)   ████████████████░░  97%       │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                   │
│  Mastery by Domain — CompTIA Security+                           │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ Cryptography         ████████████░░░  80%  ✅ Strong     │    │
│  │ Network Security     ████████░░░░░░░  55%  ⚠️  Needs work │    │
│  │ Identity & Access    ██████████████  93%  ✅ Strong     │    │
│  │ Threat Intelligence  ████░░░░░░░░░░  28%  🔴 Weak       │    │
│  │ Incident Response    ░░░░░░░░░░░░░░   0%  🔴 Not started │    │
│  └─────────────────────────────────────────────────────────┘    │
│                      [Start Revision: Threat Intelligence]       │
│                                                                   │
│  Activity — Last 30 days                                         │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │  [Streak calendar — github contribution graph style]    │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                   │
└───────────────────────────────────────────────────────────────────┘
```

---

## 5. Wireframes — Mobile

### 5.1 Mobile Shell

```
┌────────────────────────────┐
│ [XP Bar: Eng ████░ 3420]  │  ← persistent, below status bar
│ ─────────────────────────  │
│                             │
│      [Page content]        │
│                             │
│                             │
│                             │
│ ─────────────────────────  │
│ 🏠   🔍   📚   ✏️   📊    │  ← persistent bottom nav
└────────────────────────────┘
```

### 5.2 Home — Mobile

```
┌────────────────────────────┐
│ [XP: Eng ████░ 97%]       │
│ ─────────────────────────  │
│                             │
│ Good morning 👋            │
│ 🔥 7 days · 3,420 XP      │
│                             │
│ ┌──────────────────────┐  │
│ │ Security+ Goal        │  │
│ │ Jan 30 ████░░  60%  │  │
│ │          [Continue →] │  │
│ └──────────────────────┘  │
│                             │
│ Resume                     │
│ ┌──────────────────────┐  │
│ │ Day 45 · Cryptography │  │
│ │ Quiz pending           │  │
│ │          [Resume →]   │  │
│ └──────────────────────┘  │
│                             │
│ ─────────────────────────  │
│ 🏠   🔍   📚   ✏️   📊    │
└────────────────────────────┘
```

### 5.3 Lesson View — Mobile

```
┌────────────────────────────┐
│ [XP Bar]                   │
│ ─────────────────────────  │
│ ← Security+ / Day 45      │
│ Cryptography               │
│ [Notes][Quiz][Labs][Sheet] │
│ ─────────────────────────  │
│                             │
│  Symmetric vs Asymmetric  │
│                             │
│  Symmetric encryption     │
│  uses the same key for    │
│  encryption and...        │
│                             │
│  [Read more ↓]            │
│                             │
│ ─────────────────────────  │
│ ┌──────────────────────┐  │
│ │ 💬 Ask AI Tutor...   │  │
│ └──────────────────────┘  │
│ ─────────────────────────  │
│ 🏠   🔍   📚   ✏️   📊    │
└────────────────────────────┘
```

### 5.4 Quiz — Mobile (Mid-question)

```
┌────────────────────────────┐
│ [XP Bar]                   │
│ ─────────────────────────  │
│ Quiz · Day 45  Q 2 of 5   │
│ ─────────────────────────  │
│                             │
│ Which algorithm is an      │
│ asymmetric encryption      │
│ standard?                  │
│                             │
│ ┌──────────────────────┐  │
│ │ A  AES-256           │  │  ← selected (violet tint)
│ └──────────────────────┘  │
│ ┌──────────────────────┐  │
│ │ B  RSA               │  │
│ └──────────────────────┘  │
│ ┌──────────────────────┐  │
│ │ C  DES               │  │
│ └──────────────────────┘  │
│ ┌──────────────────────┐  │
│ │ D  3DES              │  │
│ └──────────────────────┘  │
│                             │
│      [Submit Answer]       │
│ ─────────────────────────  │
│ 🏠   🔍   📚   ✏️   📊    │
└────────────────────────────┘
```

---

## 6. AI Tutor UX

The AI Tutor is a core Phase 1 differentiator. Its UX must feel integrated, not bolted on.

### 6.1 Entry Points

| Context | AI Tutor Entry |
|---------|---------------|
| During lesson (notes) | Persistent "Ask AI Tutor" panel below content |
| During lesson (notes) | "Explain this" tooltip on highlighted text |
| After incorrect quiz answer | Automatic AI explanation appears (no prompt needed) |
| Practice session | "Get a hint" button (before answering) |
| My Progress (weak topic) | "Start AI revision session" button |

### 6.2 Conversation Panel (Lesson View)

```
┌── AI Tutor ─────────────────────────────────────────────────┐
│  💬 Lesson Tutor — grounded in this lesson's content only   │
│                                                              │
│  ┌────────────────────────────────────────────────────────┐ │
│  │ AI: I can help you understand Cryptography             │ │
│  │ Fundamentals. What would you like to know?             │ │
│  └────────────────────────────────────────────────────────┘ │
│                                                              │
│  Quick prompts:                                             │
│  [Explain simply] [Give an example] [What's on the exam?]  │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Type your question...                               │  │
│  └──────────────────────────────────────────────────────┘  │
│                                              [Ask →]        │
└──────────────────────────────────────────────────────────────┘
```

**Constraints visible to user:**
- "Grounded in this lesson's content only" — sets expectation
- Usage indicator: "12 of 15 AI questions used today" (if rate-limited on free tier)

### 6.3 Incorrect Answer Explanation (Auto-triggered)

```
┌── Explanation ──────────────────────────────────────────────┐
│  ✗  You answered: A — AES-256                               │
│  ✓  Correct answer: B — Diffie-Hellman                      │
│                                                              │
│  AES-256 is a symmetric encryption algorithm — it uses the  │
│  same key for both encryption and decryption, so it         │
│  doesn't work for key exchange over an untrusted network.   │
│                                                              │
│  Diffie-Hellman allows two parties to establish a shared    │
│  secret over a public channel without ever transmitting     │
│  the secret itself. This is the foundational algorithm      │
│  behind HTTPS and TLS key exchange.                         │
│                                                              │
│  [Study this topic more]  [Next question →]                 │
└──────────────────────────────────────────────────────────────┘
```

---

## 7. UX Gaps and Accessibility Requirements

### 7.1 Gaps to Address in Phase 1

| Gap | Priority | Notes |
|-----|----------|-------|
| Onboarding flow (new learner) | High | Goal setting → diagnostic → pathway |
| Empty states (no content yet, no progress) | High | "Getting started" guidance |
| Error states (API failure, AI unavailable) | High | Graceful degradation messages |
| Loading states (skeleton screens) | Medium | Especially for API-fetched content |
| Offline state (PWA, if implemented) | Medium | "You're offline, here's cached content" |
| Session expiry handling | High | Silent JWT refresh or redirect to login |
| Subscription upsell (Free → Pro) | Medium | Non-intrusive gate for premium features |

### 7.2 Accessibility Requirements (WCAG 2.2 AA)

**Must be implemented in Phase 1:**

| Requirement | Current Status | Notes |
|-------------|---------------|-------|
| Color contrast ≥ 4.5:1 for normal text | Unknown (not measured) | Measure dark + light themes |
| Color contrast ≥ 3:1 for large text | Unknown | Measure |
| Focus indicators visible | Partial | Add consistent focus ring |
| Skip navigation link | Absent | Add before Phase 1 |
| All interactive elements keyboard-accessible | Partial | Audit fully |
| Screen reader compatibility | Unknown | Test with VoiceOver + NVDA |
| `eslint-plugin-jsx-a11y` | Absent | Add to lint config |
| Alt text for all images/icons | Partial | Icon-only buttons need `aria-label` (some present) |
| Form labels (auth forms, settings inputs) | Absent (no auth yet) | Required for Phase 1 auth |
| No color as sole differentiator | Partial | Active nav uses color + position — acceptable |
| Text resize to 200% without horizontal scroll | Unknown | Test |

**Font size control:** The existing text resize control (12–24px range, 1px step) is a genuine accessibility feature. It must be preserved and must work correctly with all layout components (no overflow, no truncation).

---

## 8. Design System Continuity

The existing design token system in `index.css` is the foundation for Phase 1 UI. Key decisions:

**Preserve:**
- Color token naming convention (`--bg-base`, `--text-primary`, etc.)
- Dark-first design approach
- Inter (body) + JetBrains Mono (code) typography
- CSS custom properties for theming (not Tailwind's dark mode class)

**Extend in Phase 1:**
- Add accent color tokens: `--accent-primary` (violet #6366f1), `--accent-success` (teal/green), `--accent-warning`, `--accent-error`
- Add discipline-specific color tokens (for content cards, not for navigation)
- Add spacing scale tokens for consistent component spacing
- Formalize the component token library (card, input, button variants)

**See:** `docs/design/STUDYHUB_VISUAL_DIRECTION.md` for the full visual design system specification.

---

## 9. Component Reuse Assessment

| Component | Reuse Decision | Phase 1 Action |
|-----------|---------------|----------------|
| `CertExam.tsx` | ✅ Reuse | Adapt to accept server-fetched question data |
| `QuizEngine.tsx` | ✅ Reuse | Adapt to accept server-fetched data |
| `MarkdownRenderer.tsx` | ✅ Reuse as-is | No changes needed |
| `HandsOnLabsTab.tsx` | ✅ Reuse | Minor adaptation |
| `XPBar.tsx` | ✅ Reuse | Update to use server-fetched XP state |
| `XPToast.tsx` | ✅ Reuse | No changes needed |
| `SettingsPanel.tsx` | ⚠️ Partial | Keep theme/font sections; remove Gist section |
| `SideNav` / `BottomNav` (in App.tsx) | ⚠️ Rebuild | Update to 5-area IA, data-driven nav items |
| `HomePage.tsx` | 🔄 Rebuild | Personalized dashboard replaces static bootcamp grid |
| `CyberPage.tsx` | 🔄 Rebuild | Generic lesson view template replaces discipline-specific page |
| `IBMPage.tsx` | 🔄 Rebuild | Same — generic lesson view |
| `AIPage.tsx` | 🔄 Rebuild | Same — generic lesson view |

---

*End of STUDYHUB_UX_AUDIT.md*
