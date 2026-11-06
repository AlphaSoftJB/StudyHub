# StudyHub — Content Audit

**Version:** 1.0  
**Date:** 2026-11-04  
**Status:** Phase 0 — Authoritative content baseline  
**Author:** Phase 0 Audit (Claude Sonnet 4.6, session https://claude.ai/code/session_01GrXQBHN1FfE3b1PuBWsGTt)

---

## 1. Summary

The existing repository contains substantial, production-quality educational content across three IT-certification bootcamps. This content represents a significant asset — hundreds of hours of curriculum design — and must migrate cleanly to the StudyHub content model. No content exists yet for USMLE Step 1 or WAEC/NECO/JAMB disciplines.

| Discipline | Curriculum | Assessment | Labs | AI-Ready | Migration Status |
|------------|-----------|-----------|------|----------|-----------------|
| Cybersecurity (Network+/Security+/CySA+) | 103/115 days | 500 questions (5 exams) | 76+ labs + 40 PBQ + 24+18 PT | Requires redesign | Ready (after structural reconciliation) |
| IBM Python + ML | 57 days (3 phases) | 100 questions | 55 labs | Requires redesign | Ready |
| AI Engineering | 0/13 months populated | 96 questions | 390 labs | Requires redesign | ⚠️ Structural fix required first |
| USMLE Step 1 | ❌ None | ❌ None | ❌ None | — | Build from scratch |
| WAEC/NECO/JAMB | ❌ None | ❌ None | ❌ None | — | Build from scratch |

---

## 2. Content Model (Target)

Before detailing existing content, this is the content model all content must conform to in StudyHub:

```
Discipline
  └── Course (e.g., "CompTIA Network+", "USMLE Step 1 Cardiology")
        └── Module (e.g., "Subnetting", "Cardiac Physiology")
              └── Lesson
                    ├── Type: NOTES | VIDEO | LAB | QUIZ | CHEATSHEET
                    ├── Status: DRAFT | IN_REVIEW | APPROVED | PUBLISHED | DEPRECATED
                    └── AI context: approved content used to ground AI tutor responses
```

**Content lifecycle rule:** No content reaches `PUBLISHED` state without an `APPROVED` gate.  
**Medical content rule:** USMLE AI-generated content must be reviewed by a qualified medical professional before `APPROVED`.

---

## 3. Cybersecurity Bootcamp Content

### 3.1 Curriculum Structure

**Source file:** `src/data/cyberDays.js`  
**Total curriculum:** 115 days across 3 phases

```
Phase 1: CompTIA Network+     (Days 1–44)
Phase 2: CompTIA Security+    (Days 45–80)  
Phase 3: CompTIA CySA+        (Days 81–115)

Populated: 103/115 days (days 104–115 are empty placeholder slots)
Missing: 12 days at end of Phase 3 (CySA+)
```

**Day data shape:**
```javascript
{
  id: "1-1",           // phase-day identifier
  phase: "Network+",
  week: 1,
  notes: "...",        // markdown content (can be long — 2000+ words)
  quiz: [...],         // array of QuizQuestion objects
  exercises: "...",    // markdown exercises
  practical: "..."     // markdown practical content
}
```

**Quality assessment:** High. Notes are detailed, well-structured markdown covering exam objectives. Quiz questions are aligned to CompTIA exam domains.

### 3.2 Assessment Content

| Exam | File | Questions | Types | Coverage |
|------|------|-----------|-------|----------|
| CompTIA Network+ | `networkCertExam.js` | 100 | MCQ, Multi, PBQ | CompTIA N10-009 domains |
| CompTIA Security+ | `cyberCertExam.js` | 100 | MCQ, Multi, PBQ | CompTIA SY0-701 domains |
| CompTIA Linux+ | `linuxCertExam.js` | 100 | MCQ | Linux fundamentals |
| Python Fundamentals | `pythonCertExam.js` | 100 | MCQ | Python core |
| DevSecOps | `devSecOpsCertExam.js` | 100 | MCQ | DevSecOps practices |

**Question shape (consistent across all exams):**
```javascript
// MCQ
{ type: 'mcq', domain: "...", q: "...", options: [...], answer: 0, explanation: "..." }

// Multi-select
{ type: 'multi', domain: "...", q: "...", options: [...], answers: [0, 2], explanation: "..." }

// Performance-Based Question  
{ type: 'pbq', title: "...", scenario: "...", domain: "...",
  tasks: [{ instruction: "...", options: [...], answer: 0, explanation: "..." }] }
```

**Data access note:** All exam files export an **object with numeric string keys** (`{'0': {...}, '1': {...}, ...}`), not an array. Access via `Object.values(EXAM_DATA)` or `EXAM_DATA['0']`. The existing `CertExam.tsx` component handles this correctly.

### 3.3 Lab Content

#### Hands-On Labs (`cyberLabs.js`)
- 76 entries, keyed `{1: {...}, 2: {...}, ...}`
- Shape: `{title, tool, difficulty: 1-5, objective, steps: string[]}`
- Steps are either shell commands or Python code strings
- Tools represented: Wireshark, nmap, Python, Metasploit, Wazuh, Suricata, OpenSSL, and more

#### ⚠️ Orphan File: `cyberLabsNew.js`
- 72 entries, same shape as `cyberLabs.js`
- File comment: "NEW LABS — days 22-98 (Network+, Security+, Linux+, Python, Cybersecurity, DevSecOps)"
- **Zero imports anywhere in the codebase**
- Status: This file was written but never integrated. It is NOT dead code — it is unfinished work
- **Migration action required:** Compare against `cyberLabs.js` before migration. Determine if entries are duplicates, replacements, or supplements. Do not delete during Phase 0.

#### Packet Tracer Labs (`networkPTLabs.js`, `securityPlusLabs.js`)
- Network+: 24 labs, keyed `'1-1'` through `'5-24'` (5 sections × up to ~5 labs)
- Security+: 18 labs, same key structure
- These require Cisco Packet Tracer software — external dependency
- Assessment: These are valuable but create a tool dependency. For StudyHub, consider whether Packet Tracer labs should become supplemental (not in the core learning path) or be replaced with browser-based lab environments.

#### PBQ Drill Labs (`networkPBQLabs.js`)
- 40 PBQ drills across 6 topic areas: subnetting, switching, ACL, wireless, topology, CLI
- These are performance-based questions in scenario format — not lab exercises
- These feed directly into the CertExam PBQ question type

### 3.4 Cybersecurity Content Migration Plan

| Content Type | Migration Action | Priority |
|-------------|-----------------|----------|
| `cyberDays.js` (103 days of notes + quiz) | Seed database via migration script | High |
| 500 assessment questions (5 exams) | Seed database question bank | High |
| `cyberLabs.js` (76 hands-on labs) | Seed database as LAB-type lessons | High |
| `cyberLabsNew.js` (72 entries) | Review first → merge or discard | Medium |
| Packet Tracer labs | Seed as supplemental/external content | Low |
| PBQ drills | Seed as PBQ question type in question bank | Medium |
| Missing 12 days (104–115) | Complete during Phase 1 content work | Medium |

---

## 4. IBM Python + ML Bootcamp Content

### 4.1 Curriculum Structure

**Source file:** `src/data/ibmCurriculum.js`  
**Total curriculum:** 57 days across 3 phases

```
Phase 0 (key: '0'): Python Fundamentals     — ~20 days
Phase 1 (key: '1'): Machine Learning Core   — ~20 days
Phase 2 (key: '2'): Applied AI & Production — ~17 days

Note: Phase keys are numeric strings '0', '1', '2' (not named 'python', 'ml', etc.)
```

**Day data shape:**
```javascript
{
  day: 1,
  week: 1,
  topic: "Python Basics",
  subtopic: "Variables and Types",
  lecture: "...",       // optional markdown lecture content
  lab: "...",           // optional lab reference
  cheatsheet: "...",    // optional markdown cheatsheet
  notes: "...",         // optional markdown notes
  quiz: [...]           // optional quiz questions
}
```

**Quality assessment:** Good. Well-organized curriculum with clear weekly themes. Lab references exist but are loose — most labs are in the separate `ibmLabs.js` file.

### 4.2 Assessment Content

| File | Questions | Notes |
|------|-----------|-------|
| `pythonCertExam.js` | 100 MCQ | Shared with Cybersecurity bootcamp (Python content) |

**Note:** IBM Python + ML uses the same `pythonCertExam.js` file as the Cybersecurity bootcamp's Python section. In StudyHub, the question bank should be unified — questions tagged by domain/topic can appear in multiple courses without duplication.

### 4.3 Lab Content

**Source file:** `src/data/ibmLabs.js`  
**55 lab entries**

Shape:
```javascript
{
  title: "Data Exploration with Polars",
  tool: "Polars + Python",
  difficulty: 2,  // 1-5
  objective: "...",
  steps: [        // mix of string instructions and code
    "pip install polars",
    "import polars as pl",
    "df = pl.read_csv('data.csv')",
    // ...
  ]
}
```

**Tools represented:** Python, Pandas, Polars, MLflow, FastAPI, Docker, scikit-learn, Jupyter, pytest

**Quality assessment:** Good. Practical, hands-on steps. Code snippets are complete enough to follow.

### 4.4 IBM Content Migration Plan

| Content Type | Migration Action | Priority |
|-------------|-----------------|----------|
| `ibmCurriculum.js` (57 days) | Seed database; normalize numeric phase keys to named phases | High |
| `ibmLabs.js` (55 labs) | Seed database as LAB-type lessons | High |
| Python assessment questions | Merge into unified question bank, tagged `Python` | High |

---

## 5. AI Engineering Bootcamp Content

### 5.1 ⚠️ Structural Decoupling Problem

The AI Engineering bootcamp has a critical structural issue that must be resolved before migration.

**`aiMonths.js`** defines the curriculum backbone:
```javascript
// 13 months defined, each looks like:
{
  id: 1,
  phase: 1,
  phaseTitle: "Python & ML Foundations",
  title: "Python Programming",
  subtitle: "...",
  icon: "🐍",
  color: "#a78bfa",
  days: []  // ← ALL EMPTY
}
```

**All 13 months have empty `days[]` arrays.** The curriculum is a structural shell only.

The actual content (375 quiz items, 375+ notes, 390 labs) exists in separate files (`aiQuiz.js`, `aiNotes.js`, `aiLabs.js`) and is wired together **by array index** in `AIPage.tsx` — the page picks item at `AI_QUIZ[dayIndex]`, `AI_NOTES[dayIndex]`, `AI_LABS[dayIndex]`. This creates an implicit, fragile coupling with no structural backbone.

**Before migration:** The AI Engineering content must be explicitly mapped — each quiz item, note, and lab must be assigned to a specific month/week/day in the curriculum structure.

### 5.2 Assessment Content

| File | Questions | Shape | Notes |
|------|-----------|-------|-------|
| `aiCertExam.js` | 96 questions | `{type, domain, q, options, answer, explanation}` | AI/ML focused domains |

**Domains observed:** `RAG`, `LangChain`, `Embeddings`, `Vector Databases`, `Production AI`, `Evaluation`, `Claude API`, `MLOps`

### 5.3 Quiz Content (`aiQuiz.js`)
- 375 items
- Same `QuizQuestion` shape: `{q, options: string[], answer: number, explanation?: string}`
- Topics span 13 months of AI Engineering curriculum

### 5.4 Notes Content (`aiNotes.js`)
- 375+ items
- Assumed shape: corresponds 1:1 with quiz items by index (needs verification)

### 5.5 Lab Content

**`aiLabs.js`:** 390 lab entries  
Shape: `{title, tool, difficulty: 1-5, objective, steps: string[]}`

**`aiLabSetup.js`:** 348 lines, 13 months of environment setup  
Shape per month: `{title, environment, setup: string[], tools: [{name, purpose, free}], weeklyLabs: [{week, title, tasks}], dailyHabit, resources}`

This is high-quality setup content that contextualizes the labs. It should become the first lesson in each month's module.

### 5.6 AI Engineering Content Migration Plan

| Content Type | Migration Action | Priority |
|-------------|-----------------|----------|
| `aiMonths.js` (13 month shells) | Use as curriculum skeleton only | Low (blocked on mapping) |
| Structural mapping (day assignment) | Map 375 quiz/notes/labs to months/weeks/days | **High — required before migration** |
| `aiCertExam.js` (96 questions) | Seed question bank | Medium |
| `aiQuiz.js` (375 items) | Seed question bank after mapping | Medium |
| `aiLabs.js` (390 labs) | Seed as LAB lessons after mapping | Medium |
| `aiLabSetup.js` (13 month setups) | Seed as first lesson per month | Medium |
| `aiNotes.js` (375+ items) | Seed as NOTES lessons after mapping | Medium |

---

## 6. Missing Disciplines

### 6.1 USMLE Step 1

**Current status:** No content exists.

**Content requirements for Phase 1 (minimum viable):**
- At least 2 organ system modules with full lesson content
- Question bank: 200+ USMLE-style questions (single best answer, clinical vignettes)
- High-yield fact sheets per module
- AI Tutor grounding corpus: approved, accurate medical content only

**Special constraints:**
- All AI-generated content requires qualified medical professional review before `APPROVED` state
- Diagram/image content (anatomical diagrams) may require licensing — research required
- Content must cite authoritative sources (First Aid, Robbins Pathology, etc.)
- USMLE Step 1 format: single best answer (SBA), clinical vignettes, 1-minute/question pace

**Who creates it:** Owner + qualified medical content contributor (must be identified before Phase 1 USMLE content begins).

### 6.2 WAEC / NECO / JAMB

**Current status:** No content exists.

**Content requirements for Phase 1 (minimum viable):**
- At least one subject fully covered (recommend: Mathematics, as universal)
- 200+ past-paper style questions
- Nigerian exam format specifics: objective + theory sections
- Subject list to cover: Mathematics, English Language, Biology, Chemistry, Physics (prioritized — owner to confirm)

**Localization requirements:**
- Nigerian English (UK spelling conventions)
- Nigerian curriculum alignment (WAEC syllabus, JAMB Brochure)
- Content organized by exam year where past papers are used

**Who creates it:** Owner + Nigerian curriculum expert.

---

## 7. Content Volume Projections

| Phase | Disciplines | Est. Lessons | Est. Questions | Est. Labs |
|-------|------------|--------------|---------------|-----------|
| Phase 1 launch | Cybersecurity + IBM Python+ML | ~175 | ~700 | ~130 |
| Phase 1 expanded | + AI Engineering (after mapping) | ~550 | ~1,170 | ~520 |
| Phase 2 | + USMLE (2 modules) | +80 | +400 | — |
| Phase 2 | + WAEC/NECO/JAMB (3 subjects) | +120 | +600 | — |

---

## 8. Content Quality Standards

For all content entering the StudyHub database, the following standards apply:

### Notes / Lessons
- Written in Markdown
- Clear learning objective stated at top
- Accurate and verifiable against authoritative sources
- Reviewed for grammar and clarity
- Approximate reading time: 10–20 minutes per lesson
- Code examples tested and runnable

### Assessment Questions
- Clear, unambiguous question stem
- Exactly one correct answer (MCQ) or precisely defined correct answers (Multi)
- All distractors are plausible (not obviously wrong)
- Explanation required for all questions
- Domain/topic tag required
- Difficulty rating: 1 (Foundational) to 5 (Expert)
- For USMLE: clinical vignette format required; reviewed by qualified medical professional

### Labs
- Clear objective statement
- Prerequisites listed (software, accounts, etc.)
- Steps tested and reproducible
- Expected output described where relevant
- Difficulty rating 1–5

---

## 9. Content Migration Implementation Notes

The migration from flat JS files to the database requires a **seed script** (Java or Node.js) to:

1. Parse each data file
2. Transform to the target content schema
3. Assign content lifecycle status (`APPROVED` / `PUBLISHED` for existing production content)
4. Load into MySQL via the backend API or directly via database migration (Flyway)

**Recommended approach:** Write a one-time Node.js migration script that calls the backend content API to seed data. This validates the API contract and respects the content lifecycle middleware.

**Data integrity concern:** The `cyberDays.js` file uses `id` values like `"1-1"` (phase-day) and progress is tracked via `lab_done_1-1` localStorage keys. The database will assign surrogate integer IDs. The migration must generate a stable ID mapping to allow any existing progress data to be migrated for pilot users.

---

*End of STUDYHUB_CONTENT_AUDIT.md*
