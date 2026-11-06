# StudyHub — Visual Direction

**Version:** 1.1  
**Date:** 2026-11-04  
**Status:** Phase 0 — Design system baseline, authoritative for Phase 1 implementation  
**Author:** Phase 0 Audit (Claude Sonnet 4.6, session https://claude.ai/code/session_01GrXQBHN1FfE3b1PuBWsGTt)  
**Updated:** Visual concept mockup reviewed and incorporated (v1.1)

---

## 1. Design Principles

StudyHub is a learning platform for people who are serious about acquiring credentials and skills. The visual language should communicate:

1. **Clarity over decoration** — Learning content is the foreground. UI chrome stays out of the way.
2. **Dark-first** — Extended study sessions demand a palette that reduces eye strain. Dark is default, light is an option.
3. **Progress is real** — Mastery, XP, streaks: when learners make progress, the UI makes it feel earned, not just incremented.
4. **Technical credibility** — Monospace code, precise spacing, structured hierarchy. Looks like it belongs in a professional development context.
5. **Discipline identity without chaos** — Each discipline has a distinct accent color. The core palette stays consistent; accents distinguish without fragmenting.

---

## 2. Existing Design System (Preserved and Extended)

The existing `src/index.css` contains a well-constructed design token system. It is the foundation — not replaced, only extended.

### 2.1 Existing Color Tokens (Dark Mode, Current)

```css
:root {
  color-scheme: dark;

  /* Backgrounds */
  --bg-base:     #07090f;   /* Page background — near-black with slight blue */
  --bg-surface:  #0e1420;   /* Cards, panels — slightly lifted */
  --bg-card:     rgba(15, 23, 42, 0.6);  /* Glassmorphic card surface */
  --bg-input:    #1e293b;   /* Form inputs */
  --bg-elevated: #1e293b;   /* Elevated surfaces (dropdowns, tooltips) */

  /* Borders */
  --border:      #1e293b;   /* Subtle border (barely visible) */
  --border-md:   #334155;   /* Medium border (visible dividers) */

  /* Text */
  --text-primary:   #f1f5f9;  /* Primary body text */
  --text-secondary: #94a3b8;  /* Secondary / metadata */
  --text-muted:     #475569;  /* Muted / placeholder */
  --text-dim:       #1e293b;  /* Near-invisible (decorative) */

  /* Code */
  --code-bg:    #0f172a;    /* Code block background */
  --code-text:  #6ee7b7;    /* Code text — teal/mint */
}
```

### 2.2 Existing Light Mode Tokens

Light mode overrides are defined in `[data-theme="light"]`. They are toggled via `SettingsContext` using `document.documentElement.setAttribute('data-theme', theme)`. The light theme is fully implemented and must be preserved.

---

## 3. Extended Color System (Phase 1 Additions)

The following tokens are **new additions** for Phase 1. They extend the existing system without changing any existing token values.

### 3.1 Accent Colors (Brand)

```css
:root {
  /* Primary accent — Violet/Indigo */
  --accent:         #7c3aed;   /* Violet — primary interactive color */
  --accent-light:   #a78bfa;   /* Lighter violet — hover states, secondary emphasis */
  --accent-subtle:  rgba(124, 58, 237, 0.12);  /* Accent tint — backgrounds */

  /* Progress / Success — Teal/Green */
  --success:        #10b981;   /* Progress filled, correct answers */
  --success-light:  #34d399;   /* Lighter green — hover */
  --success-subtle: rgba(16, 185, 129, 0.12);

  /* Warning / Caution */
  --warning:        #f59e0b;   /* Partial mastery, streak at risk */
  --warning-subtle: rgba(245, 158, 11, 0.12);

  /* Error / Incorrect */
  --error:          #ef4444;   /* Wrong answers, failed attempts */
  --error-subtle:   rgba(239, 68, 68, 0.12);

  /* Info / AI */
  --ai:             #06b6d4;   /* Cyan — AI Tutor interactions */
  --ai-subtle:      rgba(6, 182, 212, 0.10);
}
```

### 3.2 Discipline Accent Colors

Each discipline has a unique accent. These are **additive** — they do not replace `--accent`. Use discipline tokens only in contexts that explicitly communicate "this is Cybersecurity content" (discipline badge, course header, etc.).

```css
:root {
  /* Cybersecurity — Electric blue */
  --discipline-cyber:         #3b82f6;
  --discipline-cyber-subtle:  rgba(59, 130, 246, 0.12);

  /* IBM Python + ML — Warm amber */
  --discipline-ibm:           #f59e0b;
  --discipline-ibm-subtle:    rgba(245, 158, 11, 0.12);

  /* AI Engineering — Purple */
  --discipline-ai:            #8b5cf6;
  --discipline-ai-subtle:     rgba(139, 92, 246, 0.12);

  /* USMLE — Crimson/Medical red */
  --discipline-usmle:         #dc2626;
  --discipline-usmle-subtle:  rgba(220, 38, 38, 0.12);

  /* WAEC/NECO/JAMB — Forest green */
  --discipline-waec:          #16a34a;
  --discipline-waec-subtle:   rgba(22, 163, 74, 0.12);
}
```

### 3.3 Light Mode Extensions

All new tokens above need light mode counterparts added to `[data-theme="light"]`. General principle: shift saturation down and lighten backgrounds while maintaining WCAG AA contrast for text on the respective backgrounds.

```css
[data-theme="light"] {
  /* Existing light overrides are already in index.css */
  /* Add: */
  --accent:         #6d28d9;   /* Darker violet for light bg contrast */
  --accent-light:   #7c3aed;
  --accent-subtle:  rgba(109, 40, 217, 0.08);

  --success:        #059669;
  --success-subtle: rgba(5, 150, 105, 0.08);

  --warning:        #d97706;
  --warning-subtle: rgba(217, 119, 6, 0.08);

  --error:          #dc2626;
  --error-subtle:   rgba(220, 38, 38, 0.08);

  --ai:             #0891b2;
  --ai-subtle:      rgba(8, 145, 178, 0.08);
}
```

---

## 4. Typography

### 4.1 Typefaces (Existing, Preserved)

```css
/* Already loaded via Google Fonts in index.css */
font-family: 'Inter', system-ui, sans-serif;        /* Body */
font-family: 'JetBrains Mono', monospace;           /* Code */
```

Both faces are retained as-is. Inter is excellent for UI text. JetBrains Mono is ideal for code blocks and technical content.

### 4.2 Type Scale

The existing font size system uses `SettingsContext` to set `document.documentElement.style.fontSize` (12–24px range, default 16px). All sizes should be in `rem` to respect this.

```
--text-xs:   0.75rem   (12px at default)
--text-sm:   0.875rem  (14px)
--text-base: 1rem       (16px)
--text-lg:   1.125rem  (18px)
--text-xl:   1.25rem   (20px)
--text-2xl:  1.5rem    (24px)
--text-3xl:  1.875rem  (30px)
```

Tailwind utility classes already provide these. Use `text-sm`, `text-base`, etc. consistently. Avoid hardcoded `px` font sizes.

### 4.3 Hierarchy Rules

- **Page titles / discipline headers:** `text-2xl` or `text-3xl`, `font-bold`, `text-primary`
- **Section headings:** `text-xl`, `font-semibold`, `text-primary`
- **Card titles / lesson names:** `text-base` or `text-lg`, `font-medium`, `text-primary`
- **Body / lesson content:** `text-base`, `font-normal`, `text-primary`, `leading-relaxed`
- **Metadata / dates / counts:** `text-sm`, `text-secondary`
- **Labels / badges:** `text-xs`, `uppercase`, `tracking-wide`, `font-medium`
- **Code inline:** `font-mono`, `text-sm`, `--code-text` color on `--code-bg`

---

## 5. Spacing and Layout

### 5.1 Core Spacing

Use Tailwind's default spacing scale (4px base). Key intervals:

```
4px   (p-1)   — tight internal spacing (badge padding)
8px   (p-2)   — small gaps (icon-label)
12px  (p-3)   — compact card padding
16px  (p-4)   — standard padding, side gutter minimum
20px  (p-5)   — comfortable card padding
24px  (p-6)   — section gap
32px  (p-8)   — large section gap
48px  (p-12)  — page section separation
```

### 5.2 App Shell (Preserved from Current)

The existing app shell layout is well-constructed and carries forward:

```
Viewport (flex, h-dvh)
├── SideNav (hidden md:flex, w-20 lg:w-56)         ← desktop only
└── Right column (flex-1, flex-col, min-w-0)
    ├── FontSizeBar (sticky top, text size ±)
    ├── XPBar (sticky below FontSizeBar)
    └── Scrollable content area (flex-1, overflow-y-auto)
        └── Content (max-w-3xl, mx-auto, border-x on md+)

BottomNav (md:hidden, fixed bottom-0)               ← mobile only
```

**Changes for Phase 1:**
- SideNav items: 5 areas (Home, Explore, Learn, Practice, Progress)
- BottomNav items: same 5 areas (icons only on mobile, icon+label on `lg:w-56` sidebar)
- XPBar: fetches from backend in Phase 1 (not localStorage)

### 5.3 Card Pattern

```css
.card {
  background: var(--bg-card);           /* rgba(15, 23, 42, 0.6) */
  border: 1px solid var(--border);      /* #1e293b */
  border-radius: 0.75rem;              /* rounded-xl */
  padding: 1.25rem;                    /* p-5 */
  /* Optional: backdrop blur for glass effect on lesson cards */
}

.card:hover {
  border-color: var(--border-md);      /* #334155 */
}
```

### 5.4 Content Width

- Max content width: `max-w-3xl` (768px) for lesson content — readable line length
- Full-width allowed for: dashboards, exam views, discipline grid
- Side margin on mobile: `px-4` minimum (16px each side)

---

## 6. Component Patterns

### 6.1 Buttons

```
Primary:   bg-[--accent] text-white         hover: opacity-90
Secondary: bg-[--bg-elevated] text-primary  hover: bg-[--border-md]  border border-[--border]
Ghost:     transparent text-secondary       hover: text-primary bg-[--bg-surface]
Danger:    bg-[--error] text-white          hover: opacity-90
```

All buttons: `rounded-lg`, `font-medium`, `transition-opacity` or `transition-colors`.

Sizes:
```
sm:  px-3 py-1.5 text-sm
md:  px-4 py-2   text-base   (default)
lg:  px-6 py-3   text-lg
```

### 6.2 Badges / Tags

Discipline badges use discipline tokens:
```html
<span class="badge badge-cyber">Cybersecurity</span>
```

```css
.badge {
  font-size: 0.75rem;
  font-weight: 500;
  padding: 0.125rem 0.5rem;
  border-radius: 9999px;
  letter-spacing: 0.025em;
  text-transform: uppercase;
}
.badge-cyber  { background: var(--discipline-cyber-subtle);  color: var(--discipline-cyber); }
.badge-ibm    { background: var(--discipline-ibm-subtle);    color: var(--discipline-ibm);   }
.badge-ai     { background: var(--discipline-ai-subtle);     color: var(--discipline-ai);    }
.badge-usmle  { background: var(--discipline-usmle-subtle);  color: var(--discipline-usmle); }
.badge-waec   { background: var(--discipline-waec-subtle);   color: var(--discipline-waec);  }
```

### 6.3 Progress Indicators

**Mastery bar:**
```
Background: var(--bg-elevated)
Fill:       var(--success) for ≥80%, var(--warning) for 50–79%, var(--error) for <50%
Height: 6px, border-radius: 3px
```

**XP bar:**
```
Background: var(--bg-elevated)
Fill:       var(--accent-light) → var(--accent) gradient
Height: 8px, border-radius: 4px
```

**Streak indicator:**
```
Active streak day:   var(--warning) fill (fire orange)
Current day:         var(--accent) border pulse
Missed day:          var(--border-md) fill
```

### 6.4 AI Tutor Panel

The AI Tutor panel appears as a right-side drawer (desktop) or bottom sheet (mobile) in the lesson view. It has distinct visual treatment to differentiate it from lesson content:

```css
.ai-panel {
  background: var(--bg-surface);
  border-left: 2px solid var(--ai);        /* Cyan left border */
  /* Mobile: border-top instead of border-left */
}

.ai-message-bubble {
  background: var(--ai-subtle);
  border-radius: 0.75rem;
  border-top-left-radius: 0.125rem;        /* Chat bubble corner */
  padding: 0.75rem 1rem;
  color: var(--text-primary);
}

.ai-label {
  color: var(--ai);
  font-size: 0.75rem;
  font-weight: 600;
  letter-spacing: 0.05em;
  text-transform: uppercase;
}
```

### 6.5 Quiz and Assessment

**Question card:**
```
Background: var(--bg-surface)
Border: var(--border)
Selected option: var(--accent-subtle) border border-[--accent]
Correct option: var(--success-subtle) border border-[--success]
Incorrect option: var(--error-subtle) border border-[--error]
```

**Answer state transitions:**
- Before answer: neutral options, `--accent` on hover
- After correct: selected option turns `--success` tint, check icon
- After incorrect: selected turns `--error` tint, × icon; correct option reveals with `--success` tint
- PBQ tasks: same pattern per sub-task

### 6.6 Code Blocks

Existing `.code-block` / `.prose-dark` utility classes are preserved:
```css
.code-block {
  background: var(--code-bg);    /* #0f172a */
  color: var(--code-text);       /* #6ee7b7 — teal/mint */
  font-family: 'JetBrains Mono', monospace;
  font-size: 0.875rem;
  padding: 1rem;
  border-radius: 0.5rem;
  overflow-x: auto;
}
```

---

## 7. Iconography

**Icon set:** Lucide React (already in use in `App.tsx`). Continue using Lucide throughout Phase 1.

**Icon sizes:**
```
Navigation icons:  w-5 h-5 (20px)
In-line icons:     w-4 h-4 (16px)  — beside labels
Feature icons:     w-6 h-6 (24px)  — card headers
Illustration:      w-8 h-8 (32px)  — empty states, onboarding
```

**Icon color:** `text-secondary` by default, `text-primary` on hover/active, `text-[--accent]` for active nav item.

---

## 8. Motion and Animation

Keep motion subtle and purposeful. Users in study sessions should not be distracted.

**Rules:**
- Transitions: `150ms ease` for hover states, `200ms ease` for panel open/close
- No animations on lesson content (text, code blocks stay static)
- XP gain: a brief number increment animation is acceptable (< 500ms)
- Streak: subtle pulse on active day (once, on load — not looping)
- Page transitions: fade in (`opacity 0 → 1`, `150ms`) — no slide animations

**Avoid:**
- Auto-playing motion unrelated to user action
- Particle effects, confetti (exception: exam pass — once, brief)
- Looping animations in the content area

---

## 9. Responsive Breakpoints (Tailwind defaults, in use)

```
sm:   640px   — Rarely used directly; mobile-first base covers most
md:   768px   — SideNav appears, BottomNav hides, content max-width centers
lg:   1024px  — SideNav expands from icon-only (w-20) to labeled (w-56)
xl:   1280px  — Wider dashboard layouts (optional)
```

The existing app shell handles `md` and `lg` correctly with `hidden md:flex` and `md:hidden` patterns. These are preserved.

---

## 10. Dark / Light Mode Guidelines

### Implementation (existing pattern — preserved)

```javascript
// SettingsContext.tsx — existing
document.documentElement.setAttribute('data-theme', theme)
```

### CSS Pattern (extend, do not change)

```css
:root { /* dark values — dark-first */ }
[data-theme="light"] { /* light overrides */ }
```

### Rules for new components

1. **Never use hardcoded hex colors.** Always use CSS tokens (`var(--text-primary)`, `var(--accent)`, etc.)
2. **Never use Tailwind static color utilities** for semantic colors — use CSS token utilities (`text-[var(--text-primary)]`) or Tailwind config extensions
3. **Test in both modes** — every new component must render correctly in both dark and light
4. **Code blocks stay dark** — `.code-block` does not change in light mode (developer expectation)

---

## 11. What Not To Do

| ❌ Avoid | ✅ Instead |
|---------|----------|
| Pure white `#ffffff` backgrounds | Use token `--bg-base` or `--bg-surface` |
| Pure black `#000000` | Use `--bg-base: #07090f` |
| Bright saturated backgrounds in content area | Subtle tints with `--*-subtle` tokens |
| Gradient backgrounds on lesson cards | Solid `--bg-card` with glassmorphism |
| More than 2 typefaces | Inter + JetBrains Mono only |
| Discipline accent as primary brand color | Violet/indigo `--accent` is the brand; discipline accents are contextual |
| Looping animations | One-shot transitions only |
| `px`-sized fonts | Use `rem` to respect user font size setting |

---

## 12. Reference: Existing Utility Classes to Reuse

From `src/index.css` — these classes are already defined and must be used rather than recreating:

```
.app-bg        — Page background (--bg-base)
.surface       — Surface (--bg-surface)
.card          — Card (--bg-card, border, border-radius)
.input-bg      — Input background (--bg-input)
.divider       — Horizontal rule (--border)
.text-hi       — High emphasis text (--text-primary)
.text-med      — Medium emphasis text (--text-secondary)
.text-lo       — Low emphasis text (--text-muted)
.code-block    — Code block styling
.prose-dark    — Markdown prose styling
```

---

---

## 13. Visual Concept Reference (Uploaded Mockup)

A high-fidelity visual concept was provided by the owner on 2026-11-04. It is the authoritative visual reference for Phase 1 implementation alongside this document. Key observations:

### 13.1 Color and Palette (Confirmed by Mockup)

The mockup confirms and refines the palette direction:

- **Background:** Deep navy `#0a0e1a` range — slightly warmer/bluer than `#07090f`. Adjust `--bg-base` to `#0a0e1a` or `#080c18` to match.
- **Surface cards:** Subtle blue-tinted dark, consistent with `--bg-surface: #0e1420` direction
- **Primary accent:** Violet-purple `#7c3aed` → confirmed. The CTA buttons ("Resume Lesson", "Start Learning") and nav active state use a bright violet that matches `--accent: #7c3aed`
- **Progress teal:** The circular progress indicator uses a bright teal-cyan (`#00e5b0` range) — richer/brighter than `--success: #10b981`. Add a progress-specific token: `--progress: #00d9a3`
- **Correct answer green:** Bright `#22c55e` fill — the selected correct answer in the quiz uses a solid vivid green background
- **Bar charts (Weekly Progress):** Violet bars matching `--accent` — progress charts use the brand violet, not a separate color

### 13.2 Navigation (Confirmed by Mockup)

The sidebar nav from the mockup:
- Shows: Home, Explore, Learn, Practice, My Progress — **exactly five areas as designed** ✅
- Also shows: Achievements, Community, Settings (secondary nav below a visual separator), and "Upgrade to Pro" CTA at the bottom
- Active state: highlighted row with solid `--accent` background, white icon and label
- Inactive: ghost icon + muted label
- Logo: graduation cap icon + "StudyHub" wordmark + "Learn. Practice. Master." tagline

**Note on Achievements and Community:** These appear in the mockup but are not in the Phase 1 design. They should be treated as Phase 2+ nav items — placeholders shown as disabled or omitted from Phase 1. Do not implement them in Sprint 1–2.

**"Upgrade to Pro" banner:** The yellow/gold banner at the sidebar bottom is part of the freemium model. Plan for this in Phase 1 (the subscription tier is tracked — this is just a UI prompt).

### 13.3 Desktop Home Dashboard Layout (Mockup Detail)

The desktop home uses a **three-column card layout** in the main area:

```
┌─────────────────────────────────────────────────────────────────┐
│  Hero: "Good Morning, [Name]" — Resume card (large, 2 cols)    │  "This Week's Progress" (1 col)
│  ┌──────────────────────────────────────┐  ┌───────────────────┐│
│  │ 72% circle + lesson name + CTA       │  │ 4h 20m            ││
│  │ [Resume Lesson →]                    │  │ Bar chart week    ││
│  │ Discipline hero image (right)        │  │ Goal: 5h bar      ││
│  └──────────────────────────────────────┘  └───────────────────┘│
├──────────────────────────┬──────────────────────────────────────┤
│  Today's Learning Plan   │  Recommended for You   │ Achievements│
│  Task list with status   │  Course card + CTA     │ Badge row   │
└──────────────────────────┴──────────────────────────────────────┘
```

Key design details:
- **Circular progress:** Large donut chart showing lesson progress % — teal fill on dark track, number centered
- **Discipline hero image:** 3D-rendered glowing illustration (shield for Cybersecurity, etc.) on the resume card right side — these are decorative images, not data
- **Today's Learning Plan:** Checklist-style with icons (📖 Lesson, ▶ Quiz, 🧪 Lab) and status badges (Completed/Next/Later)
- **Weekly progress chart:** Vertical bar chart, violet bars, 7 days visible, +28% label in teal
- **Recommended for You:** Course card with 3D image, star rating, learner count, duration

### 13.4 Learning Classroom (Mockup Detail)

The classroom view (bottom-centre panel) is the most detailed UX signal in the mockup:

**Three-column layout:**
```
┌───────────────┬────────────────────────┬──────────────┐
│  Curriculum   │   Lesson Content        │  AI Tutor    │
│  (sidebar)    │   (main)                │  (sidebar)   │
│               │                         │              │
│  Module 3     │  Video/image thumbnail  │  🤖 avatar   │
│  ☑ Intro      │  Lesson title           │  "Ask me     │
│  ☑ Networks   │  Lesson description     │  anything    │
│  → Security   │                         │  about this  │
│  ○ Quiz       │  [Mark as Complete ✓]   │  lesson!"    │
│  ○ Assessment │                         │              │
│               │  [← Previous] [Next →] │  [Explain    │
│               │                         │   this]      │
│               │                         │  [Give an    │
│               │                         │   example]   │
│               │                         │  [More       │
│               │                         │   details]   │
└───────────────┴────────────────────────┴──────────────┘
```

**AI Tutor details:**
- Persistent right panel (not a drawer — always visible in desktop classroom)
- Robot avatar icon at top
- Pre-suggested prompt buttons: "Explain this", "Give an example", "More details"
- Text input implied (not shown but expected)
- Status badge on lesson: "In Progress" (green pill)

**Curriculum sidebar:**
- Module name in violet/accent
- Lesson list with check/circle indicators for completion
- Current lesson highlighted

### 13.5 Practice & Assess (Mockup Detail)

The certification practice view shows:
- Timer top-right: `00:14:32` countdown
- Question number bottom-right: `1 of 20`
- Radio button options with single-select
- Correct answer shown with **vivid green filled row** (not just a border — full background fill)
- Explanation text below: "HTTPS encrypts data using SSL/TLS."
- "Next Question →" CTA button

**Key UX note:** The mockup shows the answer immediately after selecting — this is "immediate feedback" mode. The existing `CertExam.tsx` supports timed exam mode (see answer after all questions) and immediate feedback. The mockup shows immediate mode as the default for practice.

### 13.6 My Progress (Mockup Detail)

The progress panel shows:
- **Overall Mastery donut:** 87% with "Excellent! You're on track." — same donut pattern as lesson progress
- **Tabs:** Overview / Skills / Activity
- **Skill Mastery list:** Discipline icon + name + horizontal progress bar + % (Network Security 95%, Python Programming 88%, AI & Machine Learning 82%)
- **Achievement banner:** "You're a Top Learner! Keep up the great work!" — gold/amber background, trophy icon

### 13.7 Mobile Home (Mockup Detail)

The mobile view is a simplified single-column version of the desktop home:
- Hamburger menu + "StudyHub" title + bell icon in top bar
- Welcome greeting + avatar
- Resume card: discipline + progress circle + "Resume Lesson" button
- Today's Plan: compact list with Completed/Upcoming status tags
- Bottom nav: 5 icons (Home active, Explore, Learn, Practice, Progress)

**Gradient:** The mobile resume card uses a subtle radial glow/gradient behind the content — same as desktop hero area. This is a background effect, not content.

### 13.8 Typography Observations from Mockup

- **Headings** ("Good Morning, David!", section titles): Bold, white, clean sans-serif — consistent with Inter
- **Body:** Regular weight, light grey — consistent with `--text-secondary`
- **Labels/metadata:** Small, muted — consistent with `--text-muted`
- **"StudyHub" wordmark:** Bold, white — the "y" in "Study" and "H" in "Hub" may use the accent violet
- Tagline "Learn. Practice. Master." in small muted text below the logo

### 13.9 3D Illustration Style

The mockup uses **AI-generated or 3D-rendered discipline illustrations** (glowing shield for Cybersecurity). These are:
- Used in the hero resume card (right side)
- Used in the "Recommended for You" course card
- Likely one per discipline: shield (Cyber), brain/circuit (AI), code terminal (IBM/Python), etc.

**Phase 1 implementation note:** These are decorative images, not data. They can be sourced or generated once per discipline and stored as static assets. They do not affect the data or component architecture. For the pilot, placeholder SVG/emoji equivalents are acceptable; the full illustrated set can be added in Sprint 11–12 hardening.

### 13.10 What the Mockup Does Not Show

These are gaps to fill with judgment in Phase 1:

- **Onboarding / goal-setting flow** — not shown; must be designed in Sprint 1–2
- **Empty states** (new user with no progress) — not shown
- **Error states** — not shown
- **Settings page** — not shown (beyond sidebar link)
- **Explore / browse page** — not shown; discipline grid must be designed
- **Community / Achievements pages** — not shown; treat as Phase 2+
- **AI Tutor conversation history** — mockup shows static prompts only; conversation persistence must be designed

---

*End of STUDYHUB_VISUAL_DIRECTION.md*
