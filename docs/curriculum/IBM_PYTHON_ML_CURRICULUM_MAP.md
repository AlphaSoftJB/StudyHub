# IBM Python + ML Curriculum Map
**Sprint 3 — Curriculum Intelligence Directive deliverable**

Version: 2.0  
Date: 2026-11-06  
Governing document: StudyHub Global Curriculum & Certification Master Plan 2026–2027  
Domain assignment: **DOM-03 AI/ML** (not DOM-01; not a standalone discipline)  
Source content: `ibmCurriculum.js` (57 days, 3 phases), `ibmLabs.js` (55 labs), `pythonCertExam.js` (100 questions, shared with DOM-01)  
Status: **DRAFT — Requires owner validation before seed script execution**

---

## Change Log

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2026-11-06 | Initial — framed as standalone "IBM Python + ML" discipline |
| 2.0 | 2026-11-06 | Realigned to master plan: IBM ML content is DOM-03 AI/ML; CYB-004 Python content remains in DOM-01; pythonCertExam.js sharing strategy documented |

---

## ⚠️ Domain Realignment Notice

**v1.0 of this document treated IBM Python+ML as a standalone discipline.** Per the master plan:

- **CYB-004 (Python for Security & Automation)** → DOM-01 Cybersecurity. This course covers Python scripting as a security tool. The `pythonCertExam.js` question bank supports this course as its primary certification pool.
- **IBM Python Fundamentals** → DOM-03 AI/ML. Python as a data science and ML foundation language belongs in the AI/ML domain.
- **IBM Machine Learning Core** → DOM-03 AI/ML.
- **IBM Applied AI & Production** → DOM-03 AI/ML.

The three IBM courses below carry DOM-03 course IDs, assigned in the master plan phased delivery (Phase 4). **Sprint 3 scope covers Phase 1 (DOM-01) only.** This document is retained for curriculum intelligence completeness and to unblock future DOM-03 sprint planning.

**Sprint 3 action:** No seed script work for DOM-03 content. Record this map as Phase 4 prerequisite documentation.

---

## 1. Domain Structure (StudyHub Model)

**Domain:** DOM-03 AI/ML  
**Delivery phase:** Phase 4 (future sprint — not Sprint 3 scope)

```
Domain: DOM-03 AI/ML
│
├── [future] AI-001: Python Fundamentals for Data Science   [~20 days — 📋 PROPOSED]
│     ├── Module 1: Language Basics                         [📋 PROPOSED, maps to ibmCurriculum.js Phase 0 days 1–5]
│     ├── Module 2: Data Structures                         [📋 PROPOSED, maps to Phase 0 days 6–10]
│     ├── Module 3: Functions & OOP                         [📋 PROPOSED, maps to Phase 0 days 11–15]
│     └── Module 4: Practical Scripting & Libraries         [📋 PROPOSED, maps to Phase 0 days 16–20]
│
├── [future] AI-002: Machine Learning Core                  [~20 days — 📋 PROPOSED]
│     ├── Module 1: ML Concepts & Math                      [📋 PROPOSED, maps to ibmCurriculum.js Phase 1 days 1–5]
│     ├── Module 2: Supervised Learning                     [📋 PROPOSED, maps to Phase 1 days 6–10]
│     ├── Module 3: Unsupervised Learning                   [📋 PROPOSED, maps to Phase 1 days 11–15]
│     └── Module 4: Model Evaluation & Pipelines            [📋 PROPOSED, maps to Phase 1 days 16–20]
│
└── [future] AI-003: Applied AI & Production                [~17 days — 📋 PROPOSED]
      ├── Module 1: MLOps & Pipelines                       [📋 PROPOSED, maps to ibmCurriculum.js Phase 2 days 1–5]
      ├── Module 2: APIs & Model Deployment                 [📋 PROPOSED, maps to Phase 2 days 6–10]
      ├── Module 3: Advanced Topics (LLMs)                  [📋 PROPOSED, maps to Phase 2 days 11–14 — ⚠️ AI content controls apply]
      └── Module 4: Capstone Projects                       [📋 PROPOSED, maps to Phase 2 days 15–17]
```

**Note:** Course IDs above are placeholders. Final DOM-03 course IDs are assigned by the master plan at Phase 4 planning time. Source keys `'0'` / `'1'` / `'2'` from ibmCurriculum.js must be normalized to named course slugs at seed time.

---

## 2. Source Phase Key Normalization (Phase 4 Seed Prerequisite)

| Source key | Proposed course slug | Proposed course title | Domain |
|------------|---------------------|----------------------|--------|
| `'0'` | `ai-python-fundamentals` | Python Fundamentals for Data Science | DOM-03 |
| `'1'` | `ai-machine-learning-core` | Machine Learning Core | DOM-03 |
| `'2'` | `ai-applied-ai-production` | Applied AI & Production | DOM-03 |

**Owner confirmation required at Phase 4 planning:** Course titles, slugs, and IDs must be confirmed before DOM-03 seed script execution.

---

## 3. Official Curriculum Alignment

### 3.1 IBM Python Fundamentals (Phase 0 → AI-001)

Aligns with IBM's Python for Data Science, AI & Development course (IBM Skills Network / Coursera):

| Week/Topic in ibmCurriculum.js | IBM Skills Network Module | Coverage |
|-------------------------------|--------------------------|----------|
| Variables and Types (Wk 1) | Module 1: Python Basics | ✅ |
| Data Structures (Wk 2) | Module 2: Python Data Structures | ✅ |
| Functions & OOP (Wk 3) | Module 3: Python Programming Fundamentals | ✅ |
| File I/O & Error Handling (Wk 3–4) | Module 4: Working with Data | ✅ |
| NumPy, Pandas (Wk 4) | Module 5: APIs and Data Collection | ✅ |

**Assessment:** Strong alignment. Hands-on scripting exercises enhance IBM baseline.

### 3.2 Machine Learning Core (Phase 1 → AI-002)

Aligns with IBM Machine Learning Professional Certificate (Coursera) courses 1–4:

| Phase 1 Topic | IBM Certificate Module | Coverage |
|--------------|----------------------|----------|
| ML Concepts, Bias/Variance | Exploratory Data Analysis | ✅ |
| Linear & Logistic Regression | Supervised ML: Regression | ✅ |
| Decision Trees, Random Forest | Supervised ML: Classification | ✅ |
| K-Means, PCA | Unsupervised ML | ✅ |
| Cross-validation, Metrics | ML Pipelines and Deployment | ✅ |
| scikit-learn workflows | Throughout | ✅ |

**Assessment:** Good coverage of core sklearn ecosystem. Some advanced topics (ensemble methods depth, hyperparameter tuning) could be expanded.

### 3.3 Applied AI & Production (Phase 2 → AI-003)

| Phase 2 Topic | IBM/Industry Standard | Coverage |
|--------------|----------------------|----------|
| MLflow experiment tracking | IBM MLOps course / MLflow docs | ✅ |
| FastAPI model serving | FastAPI + IBM deployment guidance | ✅ |
| Docker containerization | IBM Cloud fundamentals | ✅ |
| Feature stores, pipelines | IBM Watson Studio patterns | ⚠️ Partial |
| LLM integration (Phase 2 end) | IBM WatsonX / OpenAI API | ⚠️ Noted in days 14–17 |

**⚠️ AI Content Controls:** LLM/WatsonX coverage in days 11–17 is directionally correct but requires curriculum review before `PUBLISHED` per master plan §19.2. IBM WatsonX API changed significantly in 2024–2025. **Flag for content review before advancing past IN_REVIEW.**

---

## 4. Lab Coverage Assessment

### 4.1 ibmLabs.js (55 entries — ✅ ACTUAL, Phase 4 scope)

| Category | Approx. Count | Key Tools | Course Mapping |
|----------|--------------|-----------|----------------|
| Python fundamentals | ~8 | Python stdlib, file I/O | AI-001 |
| Data analysis | ~12 | Pandas, Polars, NumPy, Matplotlib | AI-001 |
| Machine learning | ~15 | scikit-learn, MLflow, Jupyter | AI-002 |
| Deployment | ~10 | FastAPI, Docker, pytest | AI-003 |
| Advanced/Production | ~10 | LangChain, OpenAI API, Redis, Celery | AI-003 |

**Quality note:** Polars coverage is a differentiator vs. pandas-only curricula. Steps are actionable with real code snippets.

---

## 5. Question Bank Mapping

### 5.1 pythonCertExam.js (100 questions — SHARED with DOM-01)

This file is **shared** between CYB-004 (DOM-01 Cybersecurity) and AI-001 (DOM-03 AI/ML). In StudyHub, the sharing is handled via the `question_certification_pool` junction table per §18.3 content-certification separation:

- Questions tagged with `domain = "Python"` and `topic_tag` values
- Same question row appears in both CYB-004 and AI-001 exam pools via junction table
- No duplication in the database

**Domain breakdown (estimated):**

| Topic Area | Approx. Questions |
|------------|------------------|
| Core Python syntax, types | ~20 |
| Data structures (list, dict, set) | ~15 |
| Functions, closures, decorators | ~15 |
| OOP (classes, inheritance) | ~15 |
| File I/O, exceptions | ~10 |
| Libraries (os, sys, json, re) | ~10 |
| NumPy / Pandas basics | ~15 |

### 5.2 ML Assessment Gap

**No ML-specific exam file exists** in the current bootcamp. `pythonCertExam.js` covers Python fundamentals only. For AI-002 (ML Core) and AI-003 (Applied AI):

**Action (Phase 4):** Author or source ~100 ML/AI assessment questions aligned to IBM ML Professional Certificate objectives. These should cover: sklearn APIs, evaluation metrics, overfitting/underfitting, MLOps concepts, model deployment patterns.

---

## 6. Validation Checklist (Owner Review — Phase 4 Gate)

This checklist gates Phase 4 DOM-03 seed script execution, **not Sprint 3**:

- [ ] Course IDs AI-001 / AI-002 / AI-003 confirmed (or master plan assigns different IDs)
- [ ] Course titles and slugs confirmed
- [ ] Phase key normalization proposal accepted (`'0'`/`'1'`/`'2'` → course slugs)
- [ ] ML assessment gap acknowledged as Phase 4 content work
- [ ] LLM/WatsonX content in Phase 2 days 11–17 scheduled for curriculum review before publishing
- [ ] `pythonCertExam.js` sharing strategy confirmed (single question bank, tagged, multi-course via junction table)
- [ ] DOM-03 domain scope confirmed as separate from DOM-01

---

## 7. Sprint 3 Action Summary

| Item | Sprint 3 Action | Phase 4 Action |
|------|----------------|----------------|
| ibmCurriculum.js (57 days) | No seed — Phase 4 scope | Seed as DRAFT with phase key normalization |
| ibmLabs.js (55 labs) | No seed — Phase 4 scope | Seed as LAB-type lessons |
| pythonCertExam.js (100 Qs) | Acknowledge shared ownership; document junction table strategy | Execute junction table population for AI-001 pool |
| Course IDs | Record as PROPOSED placeholders | Confirm with master plan Phase 4 planning |
| AI content controls | Flag as requirement for days 11–17 | Enforce IN_REVIEW gate before PUBLISHED |

---

*Document produced by Claude Sonnet 4.6, Sprint 3. Governing reference: StudyHub Global Curriculum & Certification Master Plan 2026–2027. Official IBM curriculum alignment based on IBM Skills Network Python for Data Science course and IBM Machine Learning Professional Certificate (Coursera), current as of training data cutoff (August 2025).*
