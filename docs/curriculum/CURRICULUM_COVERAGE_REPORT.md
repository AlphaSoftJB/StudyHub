# Curriculum Coverage Report — Sprint 3
**StudyHub Curriculum Intelligence Directive deliverable**

Version: 1.0  
Date: 2026-11-06  
Authorized scope: Cybersecurity + IBM Python/ML (DR-025 pilot disciplines)  
Status: **DRAFT — Requires owner validation**

---

## Executive Summary

| Discipline | Source Days | StudyHub Courses | Known Gaps | Seed Ready? |
|------------|-------------|-----------------|------------|-------------|
| Cybersecurity | 103/115 days | 3 courses (N+, Sec+, CySA+) | 12 missing CySA+ days | ⏳ Pending owner validation |
| IBM Python + ML | 57/57 days | 3 courses (Python, ML, Applied AI) | No ML exam questions | ⏳ Pending owner validation |

**Seed script status:** Blocked pending owner validation of curriculum maps and cyberLabsNew.js disposition decision (execution control #5).

---

## 1. Total Content Inventory (Pilot Disciplines)

### 1.1 Lesson-equivalent content

| Type | Cybersecurity | IBM Python + ML | Total (pilot) |
|------|--------------|-----------------|---------------|
| Notes/Day lessons | 103 | 57 | 160 |
| Labs (integrated) | 76 | 55 | 131 |
| Labs (pending decision) | 72 (cyberLabsNew) | 0 | 72 |
| Cheatsheet content | Embedded in day notes | Embedded in day notes | — |
| **Total lessons** | **~179–251** | **~112** | **~291–363** |

### 1.2 Question bank

| Course | Questions | Types | Gaps |
|--------|-----------|-------|------|
| Network+ | 100 | MCQ, Multi, PBQ | None identified |
| Security+ | 100 | MCQ, Multi, PBQ | None identified |
| Linux+ | 100 | MCQ | None identified |
| Python | 100 | MCQ | Shared with IBM |
| DevSecOps | 100 | MCQ | None identified |
| ML/Applied AI | **0** | — | **Gap: ~100 questions needed (Phase 1)** |
| **Total** | **500** | | |

---

## 2. Official Objective Coverage Summary

### 2.1 Cybersecurity

| Certification | Current Version | Coverage | Confidence |
|--------------|----------------|----------|------------|
| CompTIA Network+ | N10-009 (2024) | ✅ All 5 domains | High |
| CompTIA Security+ | SY0-701 (2023) | ✅ All 5 domains | High |
| CompTIA CySA+ | CS0-003 (2023) | ⚠️ Domain 4 incomplete (12 missing days) | Medium |

**Key finding:** CySA+ Domain 4.0 (Reporting and Communication, 17% of exam) needs 12 additional days of content. This is a known gap — the source data has empty placeholder slots for days 104–115.

### 2.2 IBM Python + ML

| Course | Official Alignment | Coverage | Confidence |
|--------|-------------------|----------|------------|
| Python Fundamentals | IBM Skills Network Python course | ✅ Strong | High |
| Machine Learning Core | IBM ML Professional Certificate (Courses 1–4) | ✅ Good | High |
| Applied AI & Production | IBM WatsonX / MLOps standards | ⚠️ Phase 2 LLM days need review | Medium |

**Key finding:** Phase 2 Applied AI content (days 14–17) references LLM integration and WatsonX APIs. These should be reviewed against current IBM WatsonX documentation before publishing, as this space has evolved since the content was written.

---

## 3. Content Gaps and Actions Required

### Priority 1 — Before seed script (Sprint 3)
| Gap | Action | Owner |
|-----|--------|-------|
| cyberLabsNew.js disposition | Choose Option A/B/C (see inspection report) | Project owner |
| Curriculum map validation | Review module boundaries and course names | Project owner |
| Phase key normalization | Confirm Python/ML/AI course slug proposal | Project owner |

### Priority 2 — Phase 1 content work (post-Sprint 3)
| Gap | Action | Effort |
|-----|--------|--------|
| CySA+ Domain 4.0 (12 missing days) | Author reporting & communication content | High |
| ML/AI assessment questions (~100) | Author or source from IBM Skills Network | High |
| WatsonX/LLM content review (Phase 2 days 14–17) | Review against current IBM WatsonX docs | Medium |
| cyberLabsNew.js curriculum alignment | Validate 72 entries against official objectives | Medium |

### Priority 3 — Not Sprint 3 scope
- USMLE Step 1, Electrical, Nursing, WAEC, NECO, JAMB content (DR-025)
- AI Engineering bootcamp structural fix and migration

---

## 4. DR-025 Pilot Controls

Per DR-025, the pilot is limited to 10 learners maximum until:
- Security audit passed
- Performance under load validated
- Learner data handling verified

The curriculum maps in this report cover only the content migration plan — pilot enrollment is a separate gate not gated by curriculum readiness alone.

---

## 5. Seed Script Design Notes (for task #141)

When execution control #5 is cleared (owner validates this report), the seed script must:

1. **Create discipline records** for `cyber` and `ibm` slugs (V7 schema)
2. **Create course, module structure** as mapped in the companion documents
3. **Seed lessons from cyberDays.js**: each day → 1 `NOTES`-type lesson + inline quiz → `QUIZ`-type lesson (or questions in question bank, TBD)
4. **Seed labs from cyberLabs.js**: each entry → `LAB`-type lesson + `Lab` row with steps_json
5. **Seed questions from exam files**: normalize shape, populate `options_json`, `correct_answer`, `correct_answers_json`, `tasks_json` as appropriate per type
6. **All seeded content enters as DRAFT** — not PUBLISHED
7. **Assign exam_question_pool** rows linking exams to questions by course
8. **Handle cyberLabsNew.js** per owner direction (Option A/B/C)
9. **Normalize IBM phase keys** `'0'`/`'1'`/`'2'` to course slugs
10. **Tag questions** with `domain` and `topic_tag` from source data `domain` field

---

## 6. Document Links

| Document | Location | Status |
|----------|----------|--------|
| Cybersecurity Curriculum Map | `docs/curriculum/CYBERSECURITY_CURRICULUM_MAP.md` | Draft |
| IBM Python + ML Curriculum Map | `docs/curriculum/IBM_PYTHON_ML_CURRICULUM_MAP.md` | Draft |
| cyberLabsNew.js Inspection Report | `docs/curriculum/CYBER_LABS_NEW_INSPECTION_REPORT.md` | Draft |
| Content Audit (source of truth) | `docs/architecture/STUDYHUB_CONTENT_AUDIT.md` | v1.0 |
| Decision Record | `docs/architecture/STUDYHUB_DECISION_RECORD.md` | v1.1 |

---

*Report produced by Claude Sonnet 4.6, Sprint 3.*
