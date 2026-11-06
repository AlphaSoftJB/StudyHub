# Cybersecurity Curriculum Map
**Sprint 3 — Curriculum Intelligence Directive deliverable**

Version: 1.0  
Date: 2026-11-06  
Scope: CompTIA Network+ (N10-009), Security+ (SY0-701), CySA+ — Pilot discipline  
Source content: `cyberDays.js` (103/115 days), 5 exam files (500 questions), `cyberLabs.js` (76 labs)  
Status: **DRAFT — Requires owner validation before seed script execution**

---

## 1. Discipline Structure (StudyHub Model)

```
Discipline: Cybersecurity
│
├── Course: CompTIA Network+ (N10-009)        [Days 1–44]
│     ├── Module 1: Network Fundamentals       [~Days 1–6]
│     ├── Module 2: OSI & TCP/IP Models        [~Days 7–10]
│     ├── Module 3: IP Addressing & Subnetting [~Days 11–16]
│     ├── Module 4: Network Infrastructure     [~Days 17–22]
│     ├── Module 5: Network Operations         [~Days 23–28]
│     ├── Module 6: Network Security           [~Days 29–34]
│     ├── Module 7: Network Troubleshooting    [~Days 35–39]
│     └── Module 8: Practice & Certification  [~Days 40–44]
│
├── Course: CompTIA Security+ (SY0-701)        [Days 45–80]
│     ├── Module 1: Threats, Attacks & Vulnerabilities [~Days 45–52]
│     ├── Module 2: Architecture & Design      [~Days 53–58]
│     ├── Module 3: Implementation             [~Days 59–64]
│     ├── Module 4: Operations & Incident Response [~Days 65–70]
│     ├── Module 5: Governance, Risk, Compliance [~Days 71–76]
│     └── Module 6: Practice & Certification  [~Days 77–80]
│
└── Course: CompTIA CySA+ (CS0-003)            [Days 81–115]
      ├── Module 1: Security Operations        [~Days 81–87]
      ├── Module 2: Vulnerability Management   [~Days 88–94]
      ├── Module 3: Incident Response          [~Days 95–101]
      ├── Module 4: Reporting & Communication  [~Days 102–108]
      └── Module 5: Practice & Certification  [~Days 109–115]
```

**Coverage gap:** Days 104–115 (12 days, end of CySA+) are empty placeholder slots in the source data.

---

## 2. Official CompTIA Objective Mapping

### 2.1 Network+ N10-009 Domain Coverage

| Domain | CompTIA Weight | Coverage in cyberDays.js |
|--------|---------------|--------------------------|
| 1.0 Networking Concepts | 23% | ✅ Days 1–10 (OSI, TCP/IP, protocols) |
| 2.0 Network Implementation | 20% | ✅ Days 11–20 (switching, routing, wireless) |
| 3.0 Network Operations | 18% | ✅ Days 21–28 (monitoring, optimization) |
| 4.0 Network Security | 20% | ✅ Days 29–34 (firewalls, VPN, AAA) |
| 5.0 Network Troubleshooting | 20% | ✅ Days 35–44 (methodology, tools) |

**Assessment:** Strong coverage across all 5 domains. The 44-day block aligns well with N10-009 objective weights. Subnetting and IPv6 addressing appear well-covered (days 13–16 range).

**Question bank alignment:** `networkCertExam.js` (100 questions) covers N10-009 domains with MCQ, Multi-select, and PBQ types. PBQ questions include subnetting, switching, ACL, and topology scenarios.

### 2.2 Security+ SY0-701 Domain Coverage

| Domain | CompTIA Weight | Coverage in cyberDays.js |
|--------|---------------|--------------------------|
| 1.0 General Security Concepts | 12% | ✅ Days 45–48 |
| 2.0 Threats, Vulnerabilities & Mitigations | 22% | ✅ Days 49–56 |
| 3.0 Security Architecture | 18% | ✅ Days 57–62 |
| 4.0 Security Operations | 28% | ✅ Days 63–72 |
| 5.0 Security Program Management & Oversight | 20% | ✅ Days 73–80 |

**Assessment:** Coverage appears comprehensive. SY0-701 (released 2023) is the current exam version. The bootcamp notes reference current threat intelligence, cloud security (IaaS/PaaS/SaaS), and zero-trust architecture — consistent with SY0-701 objectives.

**Question bank alignment:** `cyberCertExam.js` (100 questions) covers SY0-701 domains. Includes PBQ scenarios for incident response and configuration tasks.

### 2.3 CySA+ CS0-003 Domain Coverage

| Domain | CompTIA Weight | Coverage in cyberDays.js |
|--------|---------------|--------------------------|
| 1.0 Security Operations | 33% | ✅ Days 81–87 |
| 2.0 Vulnerability Management | 30% | ✅ Days 88–94 |
| 3.0 Incident Response Management | 20% | ✅ Days 95–101 |
| 4.0 Reporting and Communication | 17% | ⚠️ Days 102–115 (only ~2 days populated; 12 missing) |

**Coverage gap identified:** Domain 4.0 (Reporting and Communication, 17% of exam) is under-represented. Only approximately 2 populated days cover this domain vs. the ~12 days expected at this curriculum density. The 12 missing days (104–115) fall in this domain.

**Action required (Phase 1 content work):** Author or source 12 days of CySA+ reporting, communication, and documentation content to complete Domain 4.0 coverage.

### 2.4 Additional Certifications (cyberLabsNew.js scope)

The file comment in `cyberLabsNew.js` mentions lab coverage for:
- **Linux+** (XK0-005) — not a separate course in `cyberDays.js` but covered in the Python and DevSecOps sections via Linux command-line labs
- **Python** — `pythonCertExam.js` (100 questions); Python content embedded in Cybersecurity days covering scripting and automation
- **DevSecOps** — `devSecOpsCertExam.js` (100 questions); DevSecOps content in later Security+ and CySA+ days

These may warrant separate sub-courses or tracks within the Cybersecurity discipline, or be organized as supplemental modules within existing courses.

---

## 3. Lab Coverage Assessment

### 3.1 cyberLabs.js (76 entries — integrated)

Coverage by tool/topic:
- **Network analysis:** Wireshark, nmap, tcpdump
- **Penetration testing:** Metasploit, Burp Suite, Hydra
- **SIEM/monitoring:** Wazuh, Suricata, Splunk (basic)
- **Cryptography:** OpenSSL, GPG
- **Scripting:** Python (network automation, log parsing)
- **Hardening:** iptables, SELinux, fail2ban

**Mapping to courses:** These labs span Network+, Security+, and CySA+ content. At seed time each lab needs a `lesson_id` FK to the appropriate lesson in the correct module.

### 3.2 cyberLabsNew.js (72 entries — pending disposition)

Covers days 22–98 scope per file comment. Likely fills in lab coverage for the mid-curriculum period where `cyberLabs.js` may be sparse. See `CYBER_LABS_NEW_INSPECTION_REPORT.md` for full disposition options.

### 3.3 Lab Mapping Strategy

At seed time:
1. Each lab entry is created as a `LAB`-type `Lesson` with `ContentStatus.DRAFT`
2. The lesson FK (`module_id`) is assigned by matching the lab's topic/tool to the appropriate module
3. A companion `Lab` row stores `steps_json` (serialized from `steps: string[]`)
4. Labs from `cyberLabsNew.js` (if Option B chosen) get an additional `topic_tag` of `"review_required"` to flag them for curriculum review

---

## 4. Question Bank Mapping

### 4.1 Existing question shapes → StudyHub Question entity

| Existing type | StudyHub QuestionType | Notes |
|--------------|----------------------|-------|
| `mcq` | MCQ | `answer` (integer) → `correct_answer`; `options` → `options_json` |
| `multi` | MULTI | `answers` (int[]) → `correct_answers_json`; `options` → `options_json` |
| `pbq` | PBQ | `tasks` array → `tasks_json`; scenario in `body` |

### 4.2 Domain tagging

Each question carries a `domain` field in the source data. This maps directly to the `domain` column on the `Question` entity. The `topic_tag` column will be populated from the question content during seed preparation.

**Domain values in source data (representative):**
- Network+: `"Network Fundamentals"`, `"IP Addressing"`, `"Network Security"`, etc.
- Security+: `"Threats"`, `"Architecture"`, `"Operations"`, `"GRC"`, etc.
- CySA+: (derived from question content — not explicitly labeled in all cases)

---

## 5. Seed Script Prerequisites

Before the seed script can be finalized (task #141):

1. ✅ DB schema stable (V7–V10 migrations committed)
2. ✅ Content model entities committed
3. ⏳ **Owner decision on cyberLabsNew.js** (DR-OPEN-008 options A/B/C)
4. ⏳ **Owner validation of this curriculum map** (module boundaries, course names)
5. ⏳ IBM curriculum map completed (companion document)

---

## 6. Validation Checklist (Owner Review)

Before this document is used to drive the seed script, the owner should confirm:

- [ ] Course names match what learners expect (e.g., "CompTIA Network+" vs. "Network+ N10-009")
- [ ] Module boundaries are correct (days assigned to each module)
- [ ] The 12 missing CySA+ days are accepted as a known gap (to be addressed in Phase 1)
- [ ] Linux+/Python/DevSecOps content organisation decision (sub-courses vs. supplemental modules)
- [ ] cyberLabsNew.js disposition (Option A/B/C from inspection report)

---

*Document produced by Claude Sonnet 4.6, Sprint 3. Source: `STUDYHUB_CONTENT_AUDIT.md`. Official CompTIA objective weights sourced from CompTIA exam objectives documents (N10-009 published 2024; SY0-701 published 2023; CS0-003 published 2023).*
