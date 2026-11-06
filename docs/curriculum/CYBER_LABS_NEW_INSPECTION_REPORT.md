# cyberLabsNew.js Inspection Report
**DR-OPEN-008 — Disposition Decision Required**

Authored: 2026-11-06  
Sprint: 3  
Status: **PENDING OWNER DECISION** (options and recommendation below)

---

## 1. What Was Inspected

**File:** `src/data/cyberLabsNew.js` in the existing Cybersecurity Bootcamp codebase  
**Inspection method:** Source file read in prior session; structural findings captured in `STUDYHUB_CONTENT_AUDIT.md`

---

## 2. Structural Findings

| Property | Value |
|----------|-------|
| Entries | 72 |
| File comment | `"NEW LABS — days 22-98 (Network+, Security+, Linux+, Python, Cybersecurity, DevSecOps)"` |
| Data shape | Same as `cyberLabs.js`: `{title, tool, difficulty: 1-5, objective, steps: string[]}` |
| Import count in codebase | **Zero** — file is never imported anywhere |
| Status in bootcamp | Unintegrated — written but not wired into the UI |
| Existing `cyberLabs.js` | 76 entries (days 1–21 + general labs); same shape |

**Shape comparison:**
```javascript
// cyberLabs.js entry (existing, integrated)
{
  title: "Network Traffic Capture with Wireshark",
  tool: "Wireshark",
  difficulty: 2,
  objective: "Capture and analyze HTTP/HTTPS traffic",
  steps: ["Open Wireshark...", "Select interface eth0...", ...]
}

// cyberLabsNew.js entry (same shape, never integrated)
{
  title: "<lab title for days 22–98 range>",
  tool: "<tool>",
  difficulty: 1-5,
  objective: "...",
  steps: [...]
}
```

**Scope coverage from file comment:**
- Network+ labs (days 22–?)
- Security+ labs (?–?)
- Linux+ labs (?–?)
- Python labs (?–?)
- Cybersecurity labs (?–98)
- DevSecOps labs (?–?)

The file appears to be the **continuation** of `cyberLabs.js` written to cover the later days (22–98) of the 103-day cybersecurity curriculum. `cyberLabs.js` appears to cover earlier days.

---

## 3. Assessment

### Is this dead code?
**No.** The file comment explicitly labels it "NEW LABS" for days 22–98. It was written intentionally to extend the lab coverage but was not integrated before development paused. It represents real curriculum work.

### Are entries likely duplicates of cyberLabs.js?
**Unlikely significant duplication.** `cyberLabs.js` appears to cover earlier curriculum days; `cyberLabsNew.js` is labeled for days 22–98. They likely complement rather than duplicate each other. However, without entry-by-entry comparison (blocked: file not in this repo), there may be topical overlap at the boundary.

### Quality risk?
**Low-medium.** The shape is identical to the existing integrated file. Steps are written in the same format (mix of shell commands and Python code). Quality is assumed comparable but not verified against official CompTIA/Linux+ objectives — that verification is part of the curriculum mapping work (task #139).

### Migration complexity?
**Low.** Same data shape as `cyberLabs.js`. The seed script already needs to handle `cyberLabs.js`; adding `cyberLabsNew.js` is incremental work, not a new integration pattern.

---

## 4. Disposition Options

### Option A: Include in migration (merge with cyberLabs.js entries)
Treat `cyberLabsNew.js` as the continuation of `cyberLabs.js`. Seed both files into the unified lab/lesson pool for the Cybersecurity discipline. All entries become `LAB`-type lessons with `ContentStatus.DRAFT` — they go through the content review lifecycle before publishing.

**Pros:** Preserves existing work; maximizes lab coverage; entries enter as DRAFT so quality is validated before learners see them.  
**Cons:** Requires a full deduplication pass against `cyberLabs.js` before seeding; some entries may need alignment against official CompTIA objectives (curriculum mapping, task #139).

### Option B: Stage for curriculum review only
Import `cyberLabsNew.js` entries into the seed data as `DRAFT` with a flag/tag marking them as `review_required`. Do not publish any until the CompTIA curriculum mapping (task #139) has validated them.

**Pros:** No risk of publishing unvalidated content; still preserves the work.  
**Cons:** Slightly more seed script complexity (a status/tag flag per entry).

### Option C: Exclude from Sprint 3 seed script
Acknowledge the file, document it, and defer its migration to a later sprint after the curriculum mapping is complete. The file remains untouched in the bootcamp codebase (DR-023 compliant).

**Pros:** Cleanest Sprint 3 scope boundary; curriculum mapping (task #139) will produce the right context for how these labs fit official objectives.  
**Cons:** Defers 72 potential lab entries.

---

## 5. Recommendation

**Option B** — stage for curriculum review — is the safest path given that:

1. The seed script is blocked (execution control #5) until curriculum mapping is complete anyway.
2. Curriculum mapping (task #139) will validate both `cyberLabs.js` and `cyberLabsNew.js` against CompTIA objectives.
3. The DR-012 lifecycle (DRAFT → PUBLISHED) already provides the right gate for unvalidated content.
4. Option B is functionally equivalent to Option A but makes the validation requirement explicit in the data.

The recommendation does not require discarding any work. All 72 entries are preserved. They simply cannot reach `PUBLISHED` status until a human reviewer (not AI) advances them through the lifecycle.

---

## 6. What This Decision Does NOT Affect

- The existing bootcamp remains unmodified (DR-023, existing bootcamp protection).
- The `cyberLabsNew.js` file stays in the bootcamp codebase.
- The Sprint 3 seed script remains blocked until curriculum mapping is validated (execution control #5).
- DR-012 AI content controls remain in force regardless of option chosen.

---

## 7. Required Owner Decision

**To: StudyHub project owner**  
**Decision needed:** Choose Option A, B, or C (or provide alternate direction) before the seed script is executed.

This decision does not block the remaining Sprint 3 completion gates (schema ✅, API ✅, curriculum research #139, frontend views #142, tests #143). It only affects the seed script (task #141, already blocked pending curriculum mapping).

---

*Report produced by Claude Sonnet 4.6, Sprint 3. Source material: `STUDYHUB_CONTENT_AUDIT.md` §3.3. Full entry-by-entry comparison with `cyberLabs.js` deferred to seed script preparation (task #141) pending owner direction.*
