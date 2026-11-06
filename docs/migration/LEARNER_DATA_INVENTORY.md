# Learner Data Inventory

**Version:** 2.0  
**Date:** 2026-11-05  
**Status:** Confirmed from source code (all three bootcamp `.tsx` files)  
**Author:** Sprint 1 source analysis (Claude Sonnet 4.6, session https://claude.ai/code/session_01GrXQBHN1FfE3b1PuBWsGTt)  
**Purpose:** Satisfies DR-019 precondition #1 — full inventory of every key used for learner progress before any key may be removed.

> **Critical correction from v1.0:** The compiled bundle `assets/index-u5-2w4C2.js` is from a **different historical version** of the codebase, not the current bootcamps. The current bootcamp source was confirmed in `/mnt/user-data/attachments/` as the `.tsx` files provided at session start. This version documents storage as confirmed from those source files.

---

## 1. Architecture — Storage Mechanisms by Bootcamp

| Bootcamp | File | Primary Storage | localStorage? | Notes |
|---|---|---|---|---|
| Cybersecurity | `Bootcamp.tsx` | `window.storage` (Claude artifact runtime) | **NO** | 0 localStorage calls in source |
| IBM Python + ML | `Ibm_bootcamp_v4.tsx` | `localStorage` directly | **YES** | 3 keys confirmed |
| AI Engineering | `Aibootcampv3.tsx` | `localStorage` + `window.storage` (dual) | **YES** | 4 keys confirmed |
| Hub shell | `Index.html` | `localStorage` directly | **YES** | 1 key confirmed |

> **Key finding:** The Cybersecurity bootcamp uses `window.storage` — the Claude artifact runtime storage API — not browser localStorage. This means its progress data is **not in the user's browser localStorage** and is **not accessible to the new StudyHub backend via a migration export tool**. Data is stored in Anthropic's artifact backend, tied to the artifact URL.

---

## 2. Per-Bootcamp Key Details

### 2.1 Cybersecurity Bootcamp (`Bootcamp.tsx`)

**Storage mechanism:** `window.storage.set()` / `window.storage.get()` — Claude artifact runtime API

| Key | Type | Format | Description |
|---|---|---|---|
| `bootcamp_progress` | JSON string | `{"completed": {}, "fontSize": 15, "savedAt": "ISO8601"}` | All progress + font preference |

**Completed object format:** `{ [dayId]: true }` — a plain object where each key is a day ID and value is `true` when complete. Example:
```json
{
  "day-01": true,
  "day-05": true,
  "day-12": true
}
```

**Migration implication:** This data is NOT in the user's browser localStorage. It lives in the Claude artifact runtime. No traditional browser-side export is possible. If StudyHub needs to import Cybersecurity progress, the user must manually identify completed days, or a migration page must call `window.storage.get("bootcamp_progress")` while rendered as a Claude artifact.

---

### 2.2 IBM Python + ML Bootcamp (`Ibm_bootcamp_v4.tsx`)

**Storage mechanism:** Direct `localStorage` calls

| Key | Type | Format | Description |
|---|---|---|---|
| `ibc_done` | JSON string | Array of lesson IDs: `["lesson-1", "lesson-3", ...]` | Completed lesson IDs |
| `ibc_quiz` | JSON string | Object of quiz answers: `{"q1": "B", "q2": "A", ...}` | All quiz answer state |
| `ibc_fs` | JSON number (as string) | Integer 11–22 | Font size preference |

**Source-confirmed:** Lines 3656+ of `Ibm_bootcamp_v4.tsx`
```js
localStorage.setItem('ibc_done', JSON.stringify(u));       // u = completed lessons array
localStorage.setItem('ibc_quiz', JSON.stringify(quizAns)); // quizAns = answers object
localStorage.setItem('ibc_fs', fontSize);                  // number 11-22
```

**Migration implication:** Standard localStorage export is possible. These keys are stable and directly readable from the browser.

---

### 2.3 AI Engineering Bootcamp (`Aibootcampv3.tsx`)

**Storage mechanism:** Dual — `localStorage` as fast cache + `window.storage` (Claude artifact runtime) as source of truth. On mount, `window.storage` overrides localStorage if present.

| Key | Type | Format | Description |
|---|---|---|---|
| `aicamp_progress` | JSON string | `{ [moduleId + "-" + dayNum]: true }` | Per-day completion object |
| `aicamp_dark` | JSON boolean | `true` / `false` | Dark mode preference |
| `aicamp_font` | JSON number | Integer index into FONT_FAMILIES array | Font family preference |
| `aicamp_size` | JSON number | Integer index into FONT_SIZES array | Font size preference |

**Source-confirmed:** Lines 1006–1067 of `Aibootcampv3.tsx`
```js
const [dark, setDarkRaw]             = useState(() => loadLS("aicamp_dark",     true));
const [progress, setProgressRaw]     = useState(() => loadLS("aicamp_progress", {}));
const [fontFamilyIdx, setFontFamilyIdxRaw] = useState(() => loadLS("aicamp_font", 0));
const [fontSizeIdx, setFontSizeIdxRaw]     = useState(() => loadLS("aicamp_size", 1));
```

**Progress key format:** `{ [mId + "-" + dNum]: true }` — module ID + hyphen + day number. Example:
```json
{
  "m1-1": true,
  "m1-2": true,
  "m2-1": true
}
```

**Migration implication:** localStorage values are readable directly. However, `window.storage` is the authoritative source — if a user's AI bootcamp data is in `window.storage` but their localStorage was cleared, the localStorage values would be stale/empty. The dual mechanism means: if localStorage shows progress AND window.storage confirms it, data is consistent. If only window.storage has it, localStorage was cleared.

---

### 2.4 Hub Shell (`Index.html`)

**Storage mechanism:** Direct `localStorage`

| Key | Type | Format | Description |
|---|---|---|---|
| `hub_tab` | String | `"cyber"` \| `"ibm"` \| `"ai"` | Last active tab |

**Migration implication:** UI preference only — no progress data. Safe to deprecate without migration.

---

## 3. Summary Table — All Keys

| Key | Bootcamp | Type | Contains Progress? | Migratable via localStorage? |
|---|---|---|---|---|
| `bootcamp_progress` | Cybersecurity | JSON/window.storage | **YES** | **NO — artifact runtime only** |
| `ibc_done` | IBM | JSON/localStorage | **YES** | YES |
| `ibc_quiz` | IBM | JSON/localStorage | YES (quiz state) | YES |
| `ibc_fs` | IBM | number/localStorage | No (UI pref) | YES |
| `aicamp_progress` | AI Engineering | JSON/localStorage+ws | **YES** | Partial (see §2.3) |
| `aicamp_dark` | AI Engineering | boolean/localStorage | No (UI pref) | YES |
| `aicamp_font` | AI Engineering | number/localStorage | No (UI pref) | YES |
| `aicamp_size` | AI Engineering | number/localStorage | No (UI pref) | YES |
| `hub_tab` | Hub shell | string/localStorage | No (UI pref) | YES (not needed) |

**Legacy keys from old compiled bundle** (NOT in current source — do NOT migrate):

| Key | Status |
|---|---|
| `bootcamp_xp_v1` | Old version — not in current Bootcamp.tsx source |
| `lab_done_*` | Old version — not in current Bootcamp.tsx source |
| `pbq_score_*` | Old version — not in current Bootcamp.tsx source |
| `gist_token` | Old version — not in current Bootcamp.tsx source |
| `gist_id` | Old version — not in current Bootcamp.tsx source |
| `bh_theme` | Old version — not in current Bootcamp.tsx source |
| `bh_fontSize` | Old version — not in current Bootcamp.tsx source |

> **Note:** These keys were identified from bundle analysis in v1.0 of this document. They are from a historical version of the codebase. The current source does not use them. If a user has these keys in their browser, they are residue from the old version. The current app would not read them.

---

## 4. Migration Planning

### 4.1 Backend schema implications

| Key | Backend target | Migration action |
|---|---|---|
| `ibc_done` | `lesson_completions(user_id, course_id, lesson_id, completed_at)` | Export array, insert one row per lesson ID |
| `ibc_quiz` | `quiz_attempts(user_id, question_id, selected_answer, answered_at)` | Export object, insert one row per question |
| `aicamp_progress` | `lesson_completions(user_id, course_id, lesson_id, completed_at)` | Export object keys, map `mId-dNum` to lesson IDs |
| `bootcamp_progress` | `lesson_completions` — same table | Requires artifact-runtime export (special case) |
| `ibc_fs`, `aicamp_dark`, `aicamp_font`, `aicamp_size` | `user_preferences` or Zustand/localStorage | UI prefs — may carry forward in new SPA |
| `hub_tab` | Not migrated | Deprecated when hub is replaced |

### 4.2 Cybersecurity progress migration (special case)

Since `bootcamp_progress` lives in Claude artifact runtime (`window.storage`), not browser localStorage, standard migration approaches do not apply. Options:

1. **Accept progress loss** — pilot users start fresh on the new platform. Acceptable if pilot starts with new learners.
2. **In-place export page** — build a small Claude artifact that calls `window.storage.get("bootcamp_progress")` and displays the result for the user to copy. Manual migration.
3. **No migration needed** — if the Cybersecurity bootcamp remains operational (which it must per DR-023), and learners access the new StudyHub independently, no migration is required until the bootcamp is retired (which requires owner authorization per the replacement approval gate).

> **Recommended:** Option 3. Per DR-023, the existing bootcamp remains fully operational during development. Progress in the existing bootcamp stays in the existing bootcamp. New progress in StudyHub is tracked by the new backend. Migration is only needed at bootcamp retirement, which requires a separate owner authorization.

---

## 5. DR-019 Gate Status

| Precondition | Status |
|---|---|
| #1 — Full key inventory documented | **COMPLETE** (this document v2.0) |
| #2 — Export tool built and tested | **NOT STARTED** — deferred; not needed for Sprint 1 |
| #3 — Live storage dump validated | **NOT STARTED** — deferred; not needed for Sprint 1 |
| #4 — Progress backed up | **NOT STARTED** — deferred |

> GistSync removal (DR-020) is NOT a Sprint 1 task. DR-020 gate requires all DR-019 preconditions. Sprint 1 does NOT touch existing bootcamp files.

---

## 6. What Sprint 1 Does NOT Touch

Per Sprint 1 scope and DR-023 (existing bootcamp protection):

- `Bootcamp.tsx` — **NOT modified**
- `Ibm_bootcamp_v4.tsx` — **NOT modified**
- `Aibootcampv3.tsx` — **NOT modified**
- `Index.html` — **NOT modified**
- Any localStorage key listed above — **NOT removed or modified**
- `window.storage` data — **NOT accessed**

The new StudyHub SPA is a completely separate application at new routes. Zero interference with existing bootcamp storage.

---

*This document satisfies DR-019 precondition #1. v2.0 supersedes v1.0 entirely — the v1.0 bundle analysis was from a different historical version of the application. See DR-019 and DR-020 in the Decision Record for full gate requirements.*
