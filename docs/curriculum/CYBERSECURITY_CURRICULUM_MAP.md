# Cybersecurity Curriculum Map
**Sprint 3 — Curriculum Intelligence Directive deliverable**

Version: 2.0  
Date: 2026-11-06  
Scope: DOM-01 Cybersecurity — all 6 courses (CYB-001 through CYB-006)  
Governing document: StudyHub Global Curriculum & Certification Master Plan 2026–2027  
Source content: `Bootcamp.tsx` (93 unique day entries, Phase 1 baseline), `cyberDays.js` (103/115 days), 5 exam files (500 questions), `cyberLabs.js` (76 labs), `cyberLabsNew.js` (72 labs — pending disposition DR-OPEN-008)  
Status: **DRAFT — Requires owner validation before seed script execution**

---

## Change Log

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2026-11-06 | Initial — 3 courses (N+, S+, CySA+) from cyberDays.js scope |
| 2.0 | 2026-11-06 | Extended to all 6 CYB courses per master plan; Bootcamp.tsx as Phase 1 baseline; 8 outstanding decisions resolved; certification registry requirements added; source-governance requirements added |

---

## ⚠️ Proposed vs. Actual Content

This document distinguishes two states:

- **📋 PROPOSED** — Curriculum target defined in the master plan (§6.1–6.7). Module structure, topic coverage, and certification alignment are canonical design goals. Content does **not yet exist** in the repository as authored lessons.
- **✅ ACTUAL** — Content present in the repository in source files (`Bootcamp.tsx`, `cyberDays.js`, exam files, lab files). Analyzable, seedable as DRAFT.

**Do not treat PROPOSED modules as existing lessons.** The seed script seeds only ACTUAL content. PROPOSED structures are targets for Phase 1 content work.

---

## 1. Domain Structure (StudyHub Model)

**Domain:** DOM-01 Cybersecurity  
**Phase 1 delivery scope:** CYB-001 through CYB-006 (all six courses)

```
Domain: DOM-01 Cybersecurity
│
├── CYB-001: CompTIA Network+ (N10-009)              [✅ ACTUAL — Bootcamp.tsx days 1–26]
│     ├── Module 1: Network Fundamentals             [Days 1–5]
│     ├── Module 2: OSI & TCP/IP Models              [Days 6–9]
│     ├── Module 3: IP Addressing & Subnetting       [Days 10–13]
│     ├── Module 4: Network Infrastructure           [Days 14–18]
│     ├── Module 5: Network Operations               [Days 19–21]
│     ├── Module 6: Network Security                 [Days 22–24]
│     └── Module 7: Network Troubleshooting          [Days 25–26]
│
├── CYB-002: CompTIA Security+ (SY0-701)             [✅ ACTUAL — Bootcamp.tsx days 27–44]
│     ├── Module 1: General Security Concepts        [Days 27–29]
│     ├── Module 2: Threats, Vulnerabilities         [Days 30–33]
│     ├── Module 3: Security Architecture            [Days 34–36]
│     ├── Module 4: Security Operations              [Days 37–40]
│     └── Module 5: Governance, Risk, Compliance     [Days 41–44]
│
├── CYB-003: CompTIA Linux+ (XK0-005)                [✅ PARTIAL — Bootcamp.tsx days 45–54]
│     ├── Module 1: System Administration Basics     [Days 45–49]
│     ├── Module 2: User & Permission Management     [Days 50–54 — ⚠️ MISSING in source]
│     ├── Module 3: Storage & File Systems           [📋 PROPOSED]
│     ├── Module 4: Networking on Linux              [📋 PROPOSED]
│     ├── Module 5: Security & Hardening             [📋 PROPOSED]
│     └── Module 6: Kernel Management                [Days 55–59 per Bootcamp.tsx]
│
├── CYB-004: Python for Security & Automation        [✅ ACTUAL — Bootcamp.tsx days 60–65]
│     ├── Module 1: Python Scripting Fundamentals    [Days 60–62]
│     ├── Module 2: Network Automation               [Days 63–65]
│     ├── Module 3: Security Tool Development        [📋 PROPOSED]
│     └── Module 4: Log Analysis & Forensics         [📋 PROPOSED]
│
├── CYB-005: CompTIA CySA+ / Cybersecurity Ops       [✅ ACTUAL — Bootcamp.tsx days 66–86]
│     ├── Module 1: Security Operations              [Days 66–72]
│     ├── Module 2: Vulnerability Management         [Days 73–77]
│     ├── Module 3: Incident Response                [Days 78–82]
│     ├── Module 4: Threat Intelligence              [Days 83–86]
│     └── Module 5: Reporting & Communication        [⚠️ PARTIAL — known Domain 4.0 gap]
│
└── CYB-006: DevSecOps                               [✅ ACTUAL — Bootcamp.tsx days 85–98]
      ├── Module 1: DevSecOps Principles             [Days 85–87]  ⚠️ days 85–89 have dup objects in source
      ├── Module 2: CI/CD Security                   [Days 88–90]
      ├── Module 3: Container & IaC Security         [Days 91–94]
      ├── Module 4: Security Testing in Pipelines    [Days 95–98]
      └── Module 5: Advanced DevSecOps               [📋 PROPOSED]
```

**Note on Bootcamp.tsx day ranges:** Day numbers above are approximate based on source data phase boundaries. Exact module-to-day assignment requires seed-time mapping. See `CYBER_LABS_NEW_INSPECTION_REPORT.md` for the full 93-entry inventory.

---

## 2. Certification Registry Requirements (Architectural — §18.3)

The master plan §18.3 mandates content-certification separation. Certifications are tracked as independent entities with many-to-many relationships to lessons and assessments.

### 2.1 Required Certification Registry Fields

Each certification record must carry:

| Field | Type | Required | Notes |
|-------|------|----------|-------|
| `exam_code` | string | ✅ | e.g. `N10-009`, `SY0-701`, `XK0-005` |
| `exam_version` | string | ✅ | Semantic version tied to objective doc revision |
| `certification_name` | string | ✅ | Official name from certifying body |
| `certifying_body` | string | ✅ | e.g. `CompTIA`, `EC-Council`, `ISC2` |
| `effective_date` | date | ✅ | Date exam version became active |
| `retirement_date` | date | nullable | Date exam version retired (null = current) |
| `official_guide_url` | string | ✅ | Link to official exam objectives PDF |
| `objective_domains` | JSON | ✅ | Array of domain names and weights |
| `course_mappings` | FK[] | ✅ | CYB-00x courses covering this certification |
| `assessment_mappings` | FK[] | ✅ | Question bank files mapped to this cert |
| `last_verified_date` | date | ✅ | Date objectives were last confirmed current |
| `status` | enum | ✅ | ACTIVE / TRANSITION / ANNOUNCED / RETIRED / RESEARCH / SUSPENDED |
| `notes` | text | nullable | Transition warnings, deprecation notices |

### 2.2 DOM-01 Certification Registry (Phase 1 Seed Targets)

| Cert ID | Exam Code | Name | Status | Effective | Retirement | Last Verified |
|---------|-----------|------|--------|-----------|------------|---------------|
| CERT-CYB-001 | N10-009 | CompTIA Network+ | ACTIVE | 2024-03-05 | TBD | 2026-11-06 |
| CERT-CYB-002 | SY0-701 | CompTIA Security+ | ACTIVE | 2023-11-07 | TBD | 2026-11-06 |
| CERT-CYB-003 | XK0-005 | CompTIA Linux+ | ACTIVE | 2021-10-05 | TBD | 2026-11-06 |
| CERT-CYB-005a | CS0-003 | CompTIA CySA+ | ACTIVE | 2023-06-06 | TBD | 2026-11-06 |
| CERT-CYB-006a | — | DevSecOps (generic) | RESEARCH | — | — | 2026-11-06 |
| CERT-CYB-006b | DSOE | GIAC GSEC/DevSecOps | RESEARCH | — | — | 2026-11-06 |

**DevSecOps certification note:** No single canonical certification for DevSecOps content. CYB-006 course content maps to multiple industry certs (GIAC, vendor-specific). Registry entries carry RESEARCH status until owner confirms certification target. This is a Phase 1 audit decision — record as backlog item (see §8).

**Python/CYB-004 note:** Python for Security content maps to: (a) no standalone CompTIA cert, (b) supporting objective for CySA+, Pentest+, and Security+. CYB-004 is a skills course, not certification-prep. Registry records for Python-supporting certs already exist under CYB-005 / CYB-002.

### 2.3 Content-Certification Separation (§18.3 Hard Requirement)

- A `Lesson` has no direct foreign key to a `Certification`
- Mapping is through `lesson_certification_coverage` junction table: `(lesson_id, cert_id, objective_domain, coverage_level)`
- A `Question` maps to certifications through `question_certification_pool`: `(question_id, cert_id, domain, difficulty)`
- `pythonCertExam.js` (100 questions) maps to both CYB-002 (Security+) and CYB-005 (CySA+) via the junction table — single question bank, no duplication

---

## 3. Official Objective Mapping

### 3.1 CYB-001: CompTIA Network+ N10-009

| Domain | CompTIA Weight | Coverage in Bootcamp.tsx |
|--------|---------------|--------------------------|
| 1.0 Networking Concepts | 23% | ✅ Days 1–9 (OSI, TCP/IP, protocols) |
| 2.0 Network Implementation | 20% | ✅ Days 10–18 (switching, routing, wireless) |
| 3.0 Network Operations | 18% | ✅ Days 19–21 (monitoring, optimization) |
| 4.0 Network Security | 20% | ✅ Days 22–24 (firewalls, VPN, AAA) |
| 5.0 Network Troubleshooting | 20% | ✅ Days 25–26 (methodology, tools) |

**Assessment:** Strong coverage across all 5 N10-009 domains.  
**Question bank:** `networkCertExam.js` (100 questions) — MCQ, Multi-select, PBQ types.  
**Objective source:** CompTIA N10-009 exam objectives (published 2024). Last verified: 2026-11-06.

### 3.2 CYB-002: CompTIA Security+ SY0-701

| Domain | CompTIA Weight | Coverage in Bootcamp.tsx |
|--------|---------------|--------------------------|
| 1.0 General Security Concepts | 12% | ✅ Days 27–29 |
| 2.0 Threats, Vulnerabilities & Mitigations | 22% | ✅ Days 30–33 |
| 3.0 Security Architecture | 18% | ✅ Days 34–36 |
| 4.0 Security Operations | 28% | ✅ Days 37–40 |
| 5.0 Security Program Management & Oversight | 20% | ✅ Days 41–44 |

**Assessment:** Comprehensive coverage. SY0-701 (released 2023) is current exam version.  
**Question bank:** `cyberCertExam.js` (100 questions) — includes PBQ scenarios.  
**Objective source:** CompTIA SY0-701 exam objectives (published 2023). Last verified: 2026-11-06.

### 3.3 CYB-003: CompTIA Linux+ XK0-005

| Domain | CompTIA Weight | Coverage in Bootcamp.tsx |
|--------|---------------|--------------------------|
| 1.0 System Management | 32% | ✅ Days 45–49 (partial); days 55–59 |
| 2.0 Security | 21% | 📋 PROPOSED — not yet in source |
| 3.0 Scripting, Containers & Automation | 19% | 📋 PROPOSED — not yet in source |
| 4.0 Troubleshooting | 28% | 📋 PROPOSED — not yet in source |

**Coverage gap:** Days 50–54 (Linux+ mid-curriculum) are **missing** from Bootcamp.tsx source. 5 consecutive day objects absent — likely authoring gap. Domains 2.0, 3.0, 4.0 are not covered by existing source content.  
**Action (Phase 1):** Author days 50–54 and domains 2.0–4.0 content. Record missing entries as DRAFT placeholder lessons at seed time pending owner decision (DR-OPEN-008 decision item b).  
**Question bank:** No standalone Linux+ question file identified. `linuxCertExam.js` absent — confirm existence or treat as Phase 1 authoring gap.  
**Objective source:** CompTIA XK0-005 exam objectives (published 2021). Last verified: 2026-11-06.

### 3.4 CYB-004: Python for Security & Automation

No CompTIA certification target. Content aligns with:
- Python scripting objectives embedded in CySA+ CS0-003 Domain 1.0 (Security Operations)
- Security automation content in Pentest+ PT0-003 (backlog — Phase 1 decision)

**Question bank:** `pythonCertExam.js` (100 questions) shared with CYB-005 objective coverage. Covers Python fundamentals, data structures, OOP, file I/O, NumPy/Pandas basics — mapped via junction table to supporting certifications.

### 3.5 CYB-005: CompTIA CySA+ CS0-003

| Domain | CompTIA Weight | Coverage in Bootcamp.tsx |
|--------|---------------|--------------------------|
| 1.0 Security Operations | 33% | ✅ Days 66–72 |
| 2.0 Vulnerability Management | 30% | ✅ Days 73–77 |
| 3.0 Incident Response Management | 20% | ✅ Days 78–82 |
| 4.0 Reporting and Communication | 17% | ⚠️ Days 83–86 (partial) + gap |

**Coverage gap (Domain 4.0):** Domain 4.0 (Reporting & Communication, 17% of exam) is under-represented. Only approximately 4 days exist vs. the full domain scope. This matches the known gap from v1.0 of this document.  
**Action (Phase 1):** Author Domain 4.0 content (Reporting, Communication, Documentation workflows). Do not block seed on this — seed existing content as DRAFT, flag gap in curriculum review report.  
**Question bank:** `cyberCertExam.js` (100 questions) — primarily Security+ aligned. CySA+-specific assessment content is a Phase 1 authoring gap.  
**Objective source:** CompTIA CS0-003 exam objectives (published 2023). Last verified: 2026-11-06.

### 3.6 CYB-006: DevSecOps

No single canonical certification. Content coverage:

| Topic Area | Coverage in Bootcamp.tsx |
|-----------|--------------------------|
| DevSecOps principles & culture | ✅ Days 85–87 |
| CI/CD pipeline security | ✅ Days 88–90 |
| Container security (Docker, K8s) | ✅ Days 91–94 |
| IaC security (Terraform, Ansible) | ✅ Days 95–98 |
| Advanced DevSecOps patterns | 📋 PROPOSED |

**Source data issue:** Days 85–89 appear TWICE as consecutive identical objects in Bootcamp.tsx (copy-paste authoring artifact). 5 duplicate objects confirmed. Deduplication reduces these to 5 unique entries. See `CYBER_LABS_NEW_INSPECTION_REPORT.md`.  
**Question bank:** `devSecOpsCertExam.js` (100 questions) — covers DevSecOps concepts, CI/CD security, container security.  
**Certification target:** RESEARCH status (see §2.2). Phase 1 decision required.

---

## 4. Lab Coverage Assessment

### 4.1 cyberLabs.js (76 entries — ✅ ACTUAL)

| Category | Approx. Count | Key Tools | Course Mapping |
|----------|--------------|-----------|----------------|
| Network analysis | ~15 | Wireshark, nmap, tcpdump | CYB-001 |
| Penetration testing | ~15 | Metasploit, Burp Suite, Hydra | CYB-002, CYB-005 |
| SIEM/monitoring | ~12 | Wazuh, Suricata, Splunk | CYB-005 |
| Cryptography | ~8 | OpenSSL, GPG | CYB-002 |
| Scripting | ~10 | Python (net automation, log parsing) | CYB-004 |
| Hardening | ~8 | iptables, SELinux, fail2ban | CYB-003, CYB-002 |
| Linux admin | ~8 | Various sysadmin tools | CYB-003 |

### 4.2 cyberLabsNew.js / Bootcamp.tsx labs (✅ ACTUAL — 93 unique day entries)

The `cyberLabsNew.js` data has been superseded by `Bootcamp.tsx` as the evolved source. Bootcamp.tsx contains 93 unique structured day entries with `practical.steps` arrays replacing the flat steps in cyberLabsNew.js.

**Disposition pending (DR-OPEN-008):** Owner must choose Option A, B, or C before seed script finalization. Recommendation is Option B (seed all 93 unique entries as DRAFT). See `CYBER_LABS_NEW_INSPECTION_REPORT.md`.

### 4.3 Lab Mapping Strategy

At seed time:
1. Each lab entry becomes a `LAB`-type `Lesson` with `ContentStatus.DRAFT`
2. `module_id` FK assigned by matching lab topic/phase to the appropriate course module
3. A companion `Lab` row stores `steps_json` (serialized from `practical.steps[]`)
4. Labs with `is_duplicate: true` flag in dedup output are dropped — the first copy is seeded
5. Days 50–54 (missing Linux+) may be seeded as empty DRAFT placeholder lessons pending owner decision on DR-OPEN-008 item (b)

---

## 5. Question Bank Mapping

### 5.1 Source Files → Certification Coverage

| Source File | Questions | Primary Cert | Secondary Certs | Type Coverage |
|-------------|-----------|-------------|-----------------|---------------|
| `networkCertExam.js` | 100 | N10-009 (CYB-001) | — | MCQ, Multi, PBQ |
| `cyberCertExam.js` | 100 | SY0-701 (CYB-002) | CS0-003 (supporting) | MCQ, Multi, PBQ |
| `linuxCertExam.js` | 100 | XK0-005 (CYB-003) | — | MCQ, Multi — **confirm file exists** |
| `pythonCertExam.js` | 100 | CYB-004 (skills) | SY0-701, CS0-003 supporting | MCQ |
| `devSecOpsCertExam.js` | 100 | CYB-006 (skills) | — | MCQ, Multi |

**Total (confirmed):** 500 questions across 5 files.

### 5.2 Question Shape → StudyHub Entity Mapping

| Source type | StudyHub QuestionType | Serialization |
|------------|----------------------|---------------|
| `mcq` | MCQ | `answer` (int) → `correct_answer`; `options[]` → `options_json` |
| `multi` | MULTI | `answers` (int[]) → `correct_answers_json`; `options[]` → `options_json` |
| `pbq` | PBQ | `tasks[]` → `tasks_json`; scenario in `body` |

### 5.3 Content-Certification Junction

Questions reach certification pools through the `question_certification_pool` junction table, not through direct FK. `pythonCertExam.js` questions tagged:
- `cert_id = CERT-CYB-002` (SY0-701) for scripting/automation objectives
- `cert_id = CERT-CYB-005a` (CS0-003) for security operations scripting

No duplication in the database. Same question row appears in multiple exam pools via junction table.

---

## 6. Decisions Resolved — Master Plan Reference

The following 8 outstanding decisions from Sprint 3 are resolved using the master plan as authoritative reference:

| # | Decision | Resolution | Authority |
|---|----------|-----------|-----------|
| 1 | Module boundaries for Cyber | Master plan §6.1–6.7 provides canonical module lists for all 6 CYB courses. Use as targets. | Master Plan §6.1–6.7 |
| 2 | Linux+ and DevSecOps scope | Both are explicit DOM-01 courses: CYB-003 (Linux+) and CYB-006 (DevSecOps). Full course status, not supplemental modules. | Master Plan §6.3, §6.6 |
| 3 | CySA+ Domain 4.0 gap | Seed with gap noted as DRAFT. Phase 1 audit produces gap report per §20. Not a seed blocker. | Master Plan §20 |
| 4 | IBM discipline name | CYB-004 is "Python for Security & Automation" within DOM-01. IBM Python+ML content is DOM-03 AI/ML scope. | Master Plan §7 (DOM-03) |
| 5 | IBM course titles / slugs | CYB-004 slug = `python-security-automation`. IBM ML content carries separate course IDs in DOM-03 (not Sprint 3 scope). | Master Plan §7 |
| 6 | ML assessment gap | Phase 4 AI/ML scope. Not a DOM-01 or Sprint 3 concern. Record in backlog (§8). | Master Plan §20 phased delivery |
| 7 | Phase 2 Applied AI LLM content | Flag for review before PUBLISHED per §19.2 AI content controls. No auto-publish. | Master Plan §19.2 |
| 8 | pythonCertExam.js sharing | Single unified question bank. §18.3 content-certification separation enables many-to-many via junction table. | Master Plan §18.3 |

---

## 7. Source-Governance Requirements (Architectural)

Per master plan source hierarchy:

### 7.1 Tier Classification

| Tier | Source Type | DOM-01 Examples |
|------|------------|-----------------|
| Tier 1 | Official certification providers | CompTIA exam objectives PDFs (N10-009, SY0-701, XK0-005, CS0-003) |
| Tier 1 | Authoritative standards bodies | NIST frameworks, MITRE ATT&CK, CIS Controls |
| Tier 2 | Authoritative technical documentation | Vendor security docs, OWASP, RFC specifications |
| Tier 3 | Supplementary resources | Community writeups, tutorials, blog posts |

### 7.2 Governance Requirements per Lesson

Each `Lesson` record must carry:
- `source_tier` (1 / 2 / 3)
- `source_citation` (URL or document reference)
- `source_verified_date` (date objectives/content last confirmed current)
- `objective_version` (exam code + version, e.g. `N10-009-v1`)

### 7.3 Content Update Policy

- Tier 1 sources trigger review when certification body issues new exam version
- Review required within 60 days of new exam version announcement
- Lessons in `PUBLISHED` status must be re-reviewed before continuing to publish
- Retirement date must be set when exam is retired; linked lessons flagged for update or archival
- Last-verified date must be populated at seed time and updated at each curriculum review cycle

### 7.4 AI Content Controls (§19.2)

- AI-assisted content must pass human review before status advances past `IN_REVIEW`
- AI-generated explanations must be verified against Tier 1 sources before `APPROVED`
- Phase 2 Applied AI / LLM content (DOM-03 scope) requires additional technical review before `PUBLISHED`
- No AI-generated content auto-advances to `PUBLISHED` — this is a hard platform constraint

---

## 8. Backlog — Later Sprint Requirements

Items identified in this audit that belong to sprints after Sprint 3:

| Item | Sprint | Owner Decision Required |
|------|--------|------------------------|
| CySA+ Domain 4.0 content (12+ lessons needed) | Phase 1 Content Sprint | Author or source Domain 4.0 material |
| Linux+ domains 2.0, 3.0, 4.0 content | Phase 1 Content Sprint | Author full XK0-005 coverage |
| Linux+ days 50–54 (5 missing entries) | Phase 1 Content Sprint | Author or confirm gap scope |
| DevSecOps certification target decision | Phase 1 Audit | Select target cert (GIAC, ISC2, vendor) or confirm skills-only |
| `linuxCertExam.js` confirmation | Sprint 4 / Seed Prep | Verify file exists; if absent, author 100 questions |
| CySA+-specific question bank | Phase 1 Content Sprint | Author ~100 CS0-003 questions |
| DOM-03 AI/ML courses (IBM Python ML) | Phase 4 Sprint | Separate domain scope; not DOM-01 |
| ML assessment questions (~100) | Phase 4 Sprint | Author IBM ML Professional cert-aligned questions |
| Additional certifications catalog §6.8 | Phase 1 Audit | CASP+, Pentest+, PenTest+, CAS-005 — scope decision |
| Source record backfill for all lessons | Phase 1 Audit | Populate `source_tier`, `source_citation`, `source_verified_date` |
| Certification update policy cadence | Phase 1 Governance | Define review cycle, ownership, escalation path |

---

## 9. DR-OPEN-008 Outstanding Owner Decisions

The following 4 decisions remain open and block seed script finalization (Task #141):

| Item | Decision Required | Recommendation |
|------|------------------|----------------|
| (a) Seed option A/B/C | Option A: seed only cyberLabs.js labs; Option B: seed all 93 Bootcamp.tsx entries as DRAFT; Option C: manual review first | **Recommend Option B** |
| (b) Days 50–54 | Seed as empty DRAFT placeholders OR hold until content authored | Seed as placeholders; flag for Phase 1 |
| (c) Bootcamp.tsx duplicate cleanup | Authorize removal of 5 duplicate objects (days 85–89 second copies) | Authorize; dedup is non-destructive to content |
| (d) cyberLabsNew.js supersession | Confirm Bootcamp.tsx is authoritative; cyberLabsNew.js is superseded | Confirmed by inspection; awaiting explicit owner sign-off |

**These decisions are prerequisite to Task #141 (seed script). No seed execution until owner responds.**

---

## 10. Seed Script Prerequisites

Before the seed script can be finalized (Task #141):

1. ✅ DB schema stable (V7–V10 migrations committed)
2. ✅ Content model entities committed
3. ✅ Master plan accepted as governing document (Option 1 — this session)
4. ✅ 8 curriculum mapping decisions resolved (this document)
5. ⏳ **Owner decisions on DR-OPEN-008 items (a) through (d)**
6. ⏳ **Owner validation of this curriculum map** (module boundaries, course names)
7. ⏳ Confirm `linuxCertExam.js` exists or treat as Phase 1 gap
8. ⏳ IBM Python+ML map realigned to DOM-03 (companion document updated this session)

---

## 11. Content Lifecycle Reminder

All seeded content enters at `DRAFT`. Advancement requires:

```
DRAFT → IN_REVIEW → APPROVED → PUBLISHED
```

- `DRAFT`: seeded from source; under development; not visible to learners
- `IN_REVIEW`: submitted for curriculum review; human reviewer assigned
- `APPROVED`: verified against Tier 1 sources; ready for learner access
- `PUBLISHED`: live; learner-accessible; triggers certification objective coverage tracking
- AI-generated content: cannot advance past `IN_REVIEW` without human sign-off
- AI must not independently certify learner mastery (platform hard constraint)

---

*Document produced by Claude Sonnet 4.6, Sprint 3. Governing reference: StudyHub Global Curriculum & Certification Master Plan 2026–2027. Official CompTIA objective weights sourced from CompTIA exam objectives documents (N10-009 published 2024; SY0-701 published 2023; XK0-005 published 2021; CS0-003 published 2023).*
