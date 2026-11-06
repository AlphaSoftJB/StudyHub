# IBM Python + ML Curriculum Map
**Sprint 3 — Curriculum Intelligence Directive deliverable**

Version: 1.0  
Date: 2026-11-06  
Scope: IBM Python Fundamentals, Machine Learning Core, Applied AI — Pilot discipline  
Source content: `ibmCurriculum.js` (57 days, 3 phases), `ibmLabs.js` (55 labs), `pythonCertExam.js` (100 questions)  
Status: **DRAFT — Requires owner validation before seed script execution**

---

## 1. Discipline Structure (StudyHub Model)

```
Discipline: IBM Python + ML
│
├── Course: Python Fundamentals              [Phase 0, ~20 days]
│     ├── Module 1: Language Basics          [Days 1–5]
│     ├── Module 2: Data Structures          [Days 6–10]
│     ├── Module 3: Functions & OOP          [Days 11–15]
│     └── Module 4: Practical Scripting      [Days 16–20]
│
├── Course: Machine Learning Core            [Phase 1, ~20 days]
│     ├── Module 1: ML Concepts & Math       [Days 1–5]
│     ├── Module 2: Supervised Learning      [Days 6–10]
│     ├── Module 3: Unsupervised Learning    [Days 11–15]
│     └── Module 4: Model Evaluation         [Days 16–20]
│
└── Course: Applied AI & Production          [Phase 2, ~17 days]
      ├── Module 1: MLOps & Pipelines        [Days 1–5]
      ├── Module 2: APIs & Deployment        [Days 6–10]
      ├── Module 3: Advanced Topics (LLMs)   [Days 11–14]
      └── Module 4: Capstone Projects        [Days 15–17]
```

**Source phase keys:** `'0'` (Python Fundamentals), `'1'` (ML Core), `'2'` (Applied AI). These numeric string keys must be normalized to named identifiers in the seed script.

---

## 2. Official Curriculum Alignment

### 2.1 IBM Python Fundamentals Alignment

The curriculum aligns with IBM's Python for Data Science, AI & Development course (IBM Skills Network / Coursera). Key topic alignment:

| Week/Topic in ibmCurriculum.js | IBM Skills Network Module | Coverage |
|-------------------------------|--------------------------|----------|
| Variables and Types (Wk 1) | Module 1: Python Basics | ✅ |
| Data Structures (Wk 2) | Module 2: Python Data Structures | ✅ |
| Functions & OOP (Wk 3) | Module 3: Python Programming Fundamentals | ✅ |
| File I/O & Error Handling (Wk 3–4) | Module 4: Working with Data | ✅ |
| NumPy, Pandas (Wk 4) | Module 5: APIs and Data Collection | ✅ |

**Assessment:** Strong alignment with IBM Skills Network Python course objectives. The bootcamp adds hands-on scripting exercises beyond the IBM baseline, which is an enhancement.

### 2.2 Machine Learning Core Alignment

Aligns with IBM Machine Learning Professional Certificate (Coursera) courses 1–4:

| Phase 1 Topic | IBM Certificate Module | Coverage |
|--------------|----------------------|----------|
| ML Concepts, Bias/Variance | Exploratory Data Analysis | ✅ |
| Linear & Logistic Regression | Supervised ML: Regression | ✅ |
| Decision Trees, Random Forest | Supervised ML: Classification | ✅ |
| K-Means, PCA | Unsupervised ML | ✅ |
| Cross-validation, Metrics | ML Pipelines and Deployment | ✅ |
| scikit-learn workflows | Throughout | ✅ |

**Assessment:** Good alignment. Covers the core sklearn ecosystem. Some advanced topics (ensemble methods depth, hyperparameter tuning) could be expanded in Phase 1 content work.

### 2.3 Applied AI & Production Alignment

| Phase 2 Topic | IBM/Industry Standard | Coverage |
|--------------|----------------------|----------|
| MLflow experiment tracking | IBM MLOps course / MLflow docs | ✅ |
| FastAPI model serving | FastAPI + IBM deployment guidance | ✅ |
| Docker containerization | IBM Cloud fundamentals | ✅ |
| Feature stores, pipelines | IBM Watson Studio patterns | ⚠️ Partial |
| LLM integration (Phase 2 end) | IBM WatsonX / OpenAI API | ⚠️ Noted in days 14–17 |

**Assessment:** Phase 2 is the most forward-looking section. LLM/WatsonX coverage in the last few days is directionally correct but may need curriculum review to align with current IBM WatsonX API (significant changes in 2024–2025). **Flag for content review before PUBLISHED.**

---

## 3. Lab Coverage Assessment

### 3.1 ibmLabs.js (55 entries)

Coverage breakdown by tool/domain:

| Category | Approx. Count | Key Tools |
|----------|--------------|-----------|
| Python fundamentals | ~8 | Python stdlib, file I/O |
| Data analysis | ~12 | Pandas, Polars, NumPy, Matplotlib |
| Machine learning | ~15 | scikit-learn, MLflow, Jupyter |
| Deployment | ~10 | FastAPI, Docker, pytest |
| Advanced/Production | ~10 | LangChain, OpenAI API, Redis, Celery |

**Quality assessment:** Good. Steps are actionable with real code snippets. Polars coverage is a differentiator (more current than pandas-only curricula).

### 3.2 Lab Mapping to Courses

Labs map straightforwardly to the 3-phase course structure:
- Python fundamentals + data analysis labs → Python Fundamentals course
- ML labs → Machine Learning Core course  
- Deployment + advanced labs → Applied AI & Production course

Each lab becomes a `LAB`-type lesson with a companion `Lab` row for `steps_json`.

---

## 4. Question Bank Mapping

### 4.1 pythonCertExam.js (100 MCQ questions)

**Key point:** This file is **shared** between the Cybersecurity Bootcamp (Python section) and the IBM bootcamp. In StudyHub:
- Questions are tagged with `domain = "Python"` and appropriate `topic_tag` values
- The same question can appear in both the Cybersecurity and IBM Python courses' exam pools
- No duplication in the database — `exam_question_pool` junction table handles multi-course membership

**Domain breakdown (estimated from content):**

| Topic Area | Approx. Questions |
|------------|------------------|
| Core Python syntax, types | ~20 |
| Data structures (list, dict, set) | ~15 |
| Functions, closures, decorators | ~15 |
| OOP (classes, inheritance) | ~15 |
| File I/O, exceptions | ~10 |
| Libraries (os, sys, json, re) | ~10 |
| NumPy / Pandas basics | ~15 |

### 4.2 ML Assessment Gap

**No ML-specific exam file exists** in the current bootcamp. `pythonCertExam.js` covers Python fundamentals only. For the ML Core and Applied AI courses, assessment content needs to be authored in Phase 1.

**Action required (Phase 1):** Author or source ~100 ML/AI assessment questions aligned to IBM ML Professional Certificate objectives. These should cover: sklearn APIs, evaluation metrics, overfitting/underfitting, MLOps concepts, model deployment patterns.

---

## 5. Seed Script Prerequisites

Before the seed script can be finalized (task #141):

1. ✅ DB schema stable (V7–V10 migrations)
2. ✅ Content model entities committed
3. ⏳ **Owner validation of this curriculum map** (phase-to-course naming, module boundaries)
4. ⏳ ML assessment gap acknowledged and scoped (Phase 1 content work)
5. ⏳ Phase key normalization decision: `'0'`/`'1'`/`'2'` → course slugs

**Phase key normalization proposal:**

| Source key | Proposed course slug | Proposed course title |
|------------|---------------------|----------------------|
| `'0'` | `python-fundamentals` | Python Fundamentals |
| `'1'` | `machine-learning-core` | Machine Learning Core |
| `'2'` | `applied-ai-production` | Applied AI & Production |

---

## 6. Validation Checklist (Owner Review)

Before this document is used to drive the seed script, the owner should confirm:

- [ ] Discipline name: "IBM Python + ML" vs. "IBM Data Science & AI" vs. another name
- [ ] Course titles match learner expectations
- [ ] Phase key normalization proposal accepted (or provide alternate slugs/titles)
- [ ] ML assessment gap acknowledged as Phase 1 work (not Sprint 3 blocker)
- [ ] LLM/WatsonX content in Phase 2 days 14–17 to be reviewed before publishing
- [ ] `pythonCertExam.js` sharing strategy accepted (single question bank, tagged, multi-course)

---

*Document produced by Claude Sonnet 4.6, Sprint 3. Source: `STUDYHUB_CONTENT_AUDIT.md` §4. Official IBM curriculum alignment based on IBM Skills Network Python for Data Science course and IBM Machine Learning Professional Certificate (Coursera), current as of training data cutoff.*
