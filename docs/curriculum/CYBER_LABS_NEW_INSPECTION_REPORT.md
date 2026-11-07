# DR-OPEN-008 — Bootcamp.tsx Lab Content Inspection Report
**Disposition Decision Required**

Authored: 2026-11-06 (updated same day — extended scope)  
Sprint: 3  
Status: **PENDING OWNER DECISION** (findings, options, and recommendation below)

---

## 1. Scope Correction — What Was Actually Inspected

The original version of this report (produced earlier in Sprint 3) examined
`cyberLabsNew.js` as a standalone file (72 entries, never imported).

This update supersedes it. During deeper investigation of the sandbox, it was
established that `cyberLabsNew.js` was an **intermediate work file**. Its content
evolved into the inline `DAYS` array inside `Bootcamp.tsx` — a 1.5MB single-file
React component that IS live in the existing bootcamp. The 7-phase structure in
`Bootcamp.tsx` matches the `cyberLabsNew.js` file comment exactly:

> `"NEW LABS — days 22-98 (Network+, Security+, Linux+, Python, Cybersecurity, DevSecOps)"`

**This report now covers all 93 unique day entries in `Bootcamp.tsx`.**

The existing bootcamp protection constraint is unaffected — `Bootcamp.tsx` is
read-only for inspection; nothing below proposes modifying it.

---

## 2. Structural Summary

| Property | Value |
|----------|-------|
| Source file | `bootcamp-hub/docs/migration/Bootcamp.tsx` |
| File size | ~1.5 MB |
| Total day objects in source | 98 (including 5 duplicated copies) |
| **Unique day entries** | **93** |
| Days with practical/lab content | 93/93 (100%) |
| Phases | 7 (Network+, Security+, Linux+, Python, Cybersecurity, DevSecOps, plus existing N+/S+/CySA+ in `cyberDays.js`) |
| Structural issues | 2 (see §4) |

**Phase breakdown (unique entries):**

| Phase | Days | Day range | Notes |
|-------|------|-----------|-------|
| Network+ | 26 | 1–26 | Includes day 16 (out of sort order in source, correct content) |
| Security+ | 18 | 27–44 | Includes day 36 (out of sort order in source, correct content) |
| Linux+ | 8 | 45–57 | Days 50–54 missing (see §4.2) |
| Python | 6 | 58–63 | Complete |
| Cybersecurity | 21 | 64–84 | Complete |
| DevSecOps | 14 | 85–98 | Days 85–89 have duplicate copies in source (see §4.1) |
| **Total** | **93** | 1–98 | |

---

## 3. Full Entry Table

All 93 unique entries. ⚠️ = entry has a duplicate copy in source that must be
removed before migration. All entries have `has_practical: true`.

| Day | ID | Phase | Steps | Issue | Title |
|-----|----|-------|-------|-------|-------|
| 1 | 1-1 | Network+ | 4 | | The OSI Model — Layers 1 to 4 |
| 2 | 1-2 | Network+ | 3 | | The OSI Model — Layers 5 to 7 |
| 3 | 1-3 | Network+ | 3 | | Network Topologies & Types |
| 4 | 1-4 | Network+ | 4 | | Common Ports — The Complete Reference |
| 5 | 1-5 | Network+ | 4 | | Common Ports — Part 2 & Troubleshooting with netstat |
| 6 | 2-6 | Network+ | 4 | | IPv4 Addressing & Address Classes |
| 7 | 2-7 | Network+ | 3 | | Binary Math for Networking |
| 8 | 2-8 | Network+ | 3 | | Subnetting — CIDR and the Magic Number Method |
| 9 | 2-9 | Network+ | 3 | | Subnetting Practice & IPv6 Addressing |
| 10 | 2-10 | Network+ | 3 | | Switches, Routers & Layer 2/3 Devices |
| 11 | 3-11 | Network+ | 3 | | VLANs, Trunking & Inter-VLAN Routing |
| 12 | 3-12 | Network+ | 3 | | Routing Protocols — Static, RIP, OSPF, BGP |
| 13 | 3-13 | Network+ | 3 | | Wireless Standards — 802.11 Deep Dive |
| 14 | 3-14 | Network+ | 3 | | DHCP & DNS — Network Services Deep Dive |
| 15 | 3-15 | Network+ | 3 | | Network Services — NTP, SNMP, Syslog & QoS |
| 16 | 4-16 | Network+ | 3 | | Cabling, Connectors & Physical Media |
| 17 | 4-17 | Network+ | 2 | | Policies, Compliance & Business Continuity |
| 18 | 4-18 | Network+ | 2 | | Network Security — Firewalls, IDS/IPS & DMZ |
| 19 | 4-19 | Network+ | 2 | | Authentication, AAA & Access Control |
| 20 | 4-20 | Network+ | 2 | | Wireless Security — WPA3, 802.1X & Attacks |
| 21 | 5-21 | Network+ | 2 | | Hardware Troubleshooting Tools |
| 22 | 5-22 | Network+ | 2 | | Software Troubleshooting Commands |
| 23 | 5-23 | Network+ | 2 | | Network Performance & High Availability |
| 24 | 5-24 | Network+ | 2 | | Network+ Practice Exam Day |
| 25 | 6-25 | Network+ | 2 | | Network+ Weak Area Drills & Targeted Review |
| 26 | 6-26 | Network+ | 2 | | Network+ Final Exam Prep — Ports, Acronyms & Strategy |
| 27 | 7-27 | Security+ | 2 | | Security+ Intro — Threats, Vulnerabilities & Attack Types |
| 28 | 7-28 | Security+ | 3 | | Cryptography Fundamentals |
| 29 | 7-29 | Security+ | 2 | | PKI, Certificates & Trust Chains |
| 30 | 7-30 | Security+ | 2 | | Identity & Access Management (IAM) |
| 31 | 8-31 | Security+ | 2 | | Network Security — Segmentation, Zero Trust & Secure Protocols |
| 32 | 8-32 | Security+ | 2 | | Application Security & Secure Coding |
| 33 | 8-33 | Security+ | 2 | | Security Operations — Incident Response & Forensics |
| 34 | 9-34 | Security+ | 2 | | Cloud Security & Virtualisation |
| 35 | 9-35 | Security+ | 2 | | Risk Management, Compliance & Governance |
| 36 | 10-36 | Security+ | 2 | | Wireless, Mobile & Embedded Security |
| 37 | 10-37 | Security+ | 2 | | Vulnerability Management & Penetration Testing |
| 38 | 10-38 | Security+ | 2 | | Physical Security & Social Engineering |
| 39 | 11-39 | Security+ | 2 | | Data Privacy, Protection & Secure Destruction |
| 40 | 11-40 | Security+ | 2 | | Endpoint Security — Hardening, EDR & Patch Management |
| 41 | 11-41 | Security+ | 2 | | Resilience, Backup Strategy & Business Continuity |
| 42 | 11-42 | Security+ | 2 | | Advanced Attacks — DNS, MITM, Credential & Supply Chain |
| 43 | 11-43 | Security+ | 2 | | Security+ Targeted Review — High-Frequency Exam Topics |
| 44 | 11-44 | Security+ | 2 | | Security+ Final Exam Prep — Complete Review |
| 45 | 12-45 | Linux+ | 2 | | Linux Filesystem Hierarchy & Navigation |
| 46 | 12-46 | Linux+ | 2 | | File Permissions & Ownership |
| 47 | 12-47 | Linux+ | 2 | | Users, Groups & Sudo Management |
| 48 | 12-48 | Linux+ | 2 | | Package Management — APT, YUM/DNF & RPM |
| 49 | 12-49 | Linux+ | 2 | | Process Management & System Services |
| 50 | — | Linux+ | — | ❌ MISSING | (Networking & Firewall Configuration — expected) |
| 51 | — | Linux+ | — | ❌ MISSING | (Storage, LVM & Filesystems — expected) |
| 52 | — | Linux+ | — | ❌ MISSING | (Shell Scripting & Automation — expected) |
| 53 | — | Linux+ | — | ❌ MISSING | (SSH, Remote Access & Security Hardening — expected) |
| 54 | — | Linux+ | — | ❌ MISSING | (Linux+ Practice Exam Day — expected) |
| 55 | 13-55 | Linux+ | 2 | | Kernel Management, Boot Process & Systemd |
| 56 | 14-56 | Linux+ | 2 | | Log Management, Monitoring & Troubleshooting |
| 57 | 14-57 | Linux+ | 2 | | Linux+ Final Exam Prep — Complete Review |
| 58 | 17-58 | Python | 2 | | Python Fundamentals — Variables, Types & Control Flow |
| 59 | 17-59 | Python | 2 | | Functions, Error Handling & Modules |
| 60 | 17-60 | Python | 2 | | File I/O, JSON & Regular Expressions |
| 61 | 18-61 | Python | 2 | | OOP, Network Requests & Security Automation |
| 62 | 19-62 | Python | 2 | | Python for Security — Crypto, APIs & Real Tools |
| 63 | 19-63 | Python | 2 | | Python Final Exam Prep — Complete Review |
| 64 | 22-64 | Cybersecurity | 2 | | SOC Operations & SIEM Fundamentals |
| 65 | 22-65 | Cybersecurity | 2 | | Threat Hunting & MITRE ATT&CK |
| 66 | 23-66 | Cybersecurity | 2 | | Incident Response — Full Lifecycle |
| 67 | 23-67 | Cybersecurity | 2 | | Red Team / Blue Team — Attack & Defence |
| 68 | 26-68 | Cybersecurity | 2 | | Malware Analysis — Static & Dynamic |
| 69 | 26-69 | Cybersecurity | 2 | | Vulnerability Management & Risk Assessment |
| 70 | 26-70 | Cybersecurity | 2 | | Network Security Architecture — Zero Trust & Segmentation |
| 71 | 27-71 | Cybersecurity | 2 | | Application Security — OWASP, SAST & Secure SDLC |
| 72 | 27-72 | Cybersecurity | 2 | | Cloud Security — Shared Responsibility, CSPM & Cloud IR |
| 73 | 27-73 | Cybersecurity | 2 | | Identity & Access Management — PAM, SSO & Identity Governance |
| 74 | 27-74 | Cybersecurity | 2 | | Applied Cryptography — PKI, TLS & Certificate Management |
| 75 | 27-75 | Cybersecurity | 2 | | Cybersecurity Final Exam Prep — CySA+ Complete Review |
| 76 | 28-76 | Cybersecurity | 2 | | Digital Forensics — Memory, Disk & Timeline Analysis |
| 77 | 29-77 | Cybersecurity | 2 | | Penetration Testing — Methodology & Common Techniques |
| 78 | 30-78 | Cybersecurity | 2 | | Threat Intelligence — CTI Cycle & Operationalisation |
| 79 | 31-79 | Cybersecurity | 2 | | Compliance, Governance & Security Frameworks |
| 80 | 31-80 | Cybersecurity | 2 | | Security Automation & SOAR |
| 81 | 32-81 | Cybersecurity | 2 | | Wireless & Physical Security |
| 82 | 33-82 | Cybersecurity | 2 | | Data Privacy, DLP & Information Classification |
| 83 | 34-83 | Cybersecurity | 2 | | Advanced Persistent Threats & Nation-State Tactics |
| 84 | 34-84 | Cybersecurity | 2 | | Cybersecurity Career Capstone — Portfolio & Practical Skills |
| 85 | 35-85 | DevSecOps | 2 | ⚠️ DUP | DevSecOps Foundations — Shift Left & CI/CD Security |
| 86 | 36-86 | DevSecOps | 2 | ⚠️ DUP | Container Security — Docker & Kubernetes |
| 87 | 37-87 | DevSecOps | 2 | ⚠️ DUP | Infrastructure as Code Security — Terraform & CloudFormation |
| 88 | 38-88 | DevSecOps | 2 | ⚠️ DUP | Secrets Management — Vault, OIDC & Runtime Injection |
| 89 | 39-89 | DevSecOps | 2 | ⚠️ DUP | Supply Chain Security — SBOM, Signing & Provenance |
| 90 | 40-90 | DevSecOps | 2 | | Supply Chain Security — SBOM, Sigstore & Dependency Management |
| 91 | 41-91 | DevSecOps | 2 | | Runtime Security — Falco, eBPF & Application Monitoring |
| 92 | 42-92 | DevSecOps | 2 | | Security Testing — DAST, Fuzzing & Bug Bounty |
| 93 | 43-93 | DevSecOps | 2 | | GitOps Security & Pipeline Hardening |
| 94 | 44-94 | DevSecOps | 2 | | Zero Trust for Microservices — mTLS & API Gateway Security |
| 95 | 45-95 | DevSecOps | 2 | | Cloud-Native Security — AWS/Azure/GCP Security Posture |
| 96 | 46-96 | DevSecOps | 2 | | DevSecOps Maturity & Cultural Transformation |
| 97 | 47-97 | DevSecOps | 2 | | DevSecOps Capstone — End-to-End Secure Pipeline |
| 98 | 48-98 | DevSecOps | 2 | | DevSecOps Final Review — CKS, CCSP & Career Readiness |

**Legend:** ⚠️ DUP = entry with duplicate copy in source (5 entries, days 85–89);
❌ MISSING = day number absent from source (5 entries, days 50–54).

---

## 4. Structural Issues

### 4.1 Duplicate Objects in Source — Days 85–89 (5 entries)

**What it is:** In `Bootcamp.tsx`, days 85–89 each appear **twice** as consecutive
identical `{id, day, phase, ...}` objects — the same entry copy-pasted back-to-back.
This is a copy-paste authoring artifact, not a naming collision between distinct
entries. Days 90–98 appear only once and are not affected.

**Effect on current bootcamp:** The existing bootcamp renders data from `Bootcamp.tsx`
but the duplicate handling depends on how the DAYS array is consumed. If iterated
directly, users would see days 85–89 twice in the schedule. Worth verifying in the
existing UI, but this is a source data bug regardless.

**Migration action:** The seed script must deduplicate on `(id, day)` before inserting.
The 5 extra copies are dropped; 93 unique entries are seeded.

**Bootcamp.tsx fix (separate decision):** The source file should also be corrected,
but this is a change to the existing bootcamp component and requires the owner's
explicit authorization under the bootcamp protection constraint. Flagged here for
awareness only — no fix will be made without approval.

### 4.2 Missing Days 50–54 — Linux+ Section

**What it is:** Day numbers 50–54 do not exist in the DAYS array. The sequence jumps
from day 49 (Process Management & System Services) to day 55 (Kernel Management,
Boot Process & Systemd). Based on the surrounding curriculum topics and the Linux+
CompTIA XK0-005 exam objectives, the gap likely covers:

| Day | Expected topic |
|-----|----------------|
| 50 | Networking & Firewall Configuration (`iptables`, `ufw`, `ss`, `netstat`) |
| 51 | Storage, LVM & Filesystems (partitioning, `fdisk`, `lvm`, `ext4`/`xfs`) |
| 52 | Shell Scripting & Automation (bash scripts, cron, variables, loops) |
| 53 | SSH, Remote Access & Security Hardening (`sshd`, key auth, `fail2ban`) |
| 54 | Linux+ Practice Exam Day |

**Effect on current bootcamp:** These 5 days simply don't appear in the learner's
schedule. The learner moves directly from day 49 to day 55 with no warning.

**Migration action:** The seed script should insert 5 placeholder lesson records
for days 50–54 with `ContentStatus.DRAFT` and a note field indicating the content
is missing. These become visible gaps in the StudyHub curriculum view — intentional,
so a content author can see and fill them. Do NOT create AI-generated content to fill
these gaps without owner approval and content review.

---

## 5. Relationship to cyberLabsNew.js (Original DR-OPEN-008 Subject)

The original report examined `cyberLabsNew.js` (72 entries, never imported, same lab
shape as `cyberLabs.js`). This extended inspection establishes:

- `cyberLabsNew.js` was an intermediate work file; its data was reworked and expanded
  into the structured `DAYS` array inside `Bootcamp.tsx`
- `Bootcamp.tsx` has a richer data model (each entry has `objective`, `practical.steps`
  with `instruction`/`command`/`explain` fields, `quiz`) vs. the simpler
  `{title, tool, difficulty, objective, steps: string[]}` shape of `cyberLabsNew.js`
- The `cyberLabs.js` file (76 entries, integrated) covers supplemental tool-focused labs;
  `Bootcamp.tsx` DAYS covers the day-by-day structured curriculum labs
- For migration: `Bootcamp.tsx` DAYS are the **primary curriculum**; `cyberLabs.js` are
  **supplemental/tool-reference labs**; `cyberLabsNew.js` is superseded by `Bootcamp.tsx`
  and can be decommissioned after owner confirms this relationship

---

## 6. Quality Assessment

| Dimension | Assessment |
|-----------|------------|
| Coverage | 93/98 expected days present (gaps are Linux+ days 50–54, a known hole) |
| Lab content | 100% of entries have `practical` with structured `steps` |
| Step depth | Network+ days average 3–4 steps; later phases average 2 steps (acceptable for overview-level labs) |
| Data shape | Consistent throughout; well-suited for direct seed script extraction |
| Duplicate risk | 5 entries have copy-paste duplicates in source; trivially resolved at seed time |
| Title uniqueness | Days 89 and 90 are near-duplicates in title ("Supply Chain Security — SBOM…") but are distinct in content (different tools: Sigstore vs. plain SBOM review) |
| Phase alignment | All entries have correct `phase` labels matching the 6-phase curriculum structure |
| Exam alignment | Titles align with CompTIA N10-009, SY0-701, XK0-005, and DevSecOps exam objectives |

Overall quality: **Good**. This is production-quality curriculum data, written
intentionally with structured lab steps. The structural issues (duplicates, missing
days) are authoring artifacts, not content quality problems.

---

## 7. Disposition Options

### Option A — Seed all 93 unique entries as DRAFT LAB lessons

Extract all 93 unique entries from `Bootcamp.tsx`, deduplicate on `(id, day)`,
and seed into the StudyHub content database as `LAB`-type lessons with
`ContentStatus.DRAFT`. Insert 5 placeholder DRAFT records for missing days 50–54
with a `content_missing: true` flag.

**Pros:** Maximizes content coverage; structural issues resolved at seed time;
  DRAFT status means nothing reaches learners before review.  
**Cons:** Seed script must handle deduplication logic and the richer data model
  (extracting `practical.steps` into the target lesson schema).

### Option B — Seed all 93 entries as DRAFT, flag days 85–89 for source fix review

Same as Option A, but additionally flag days 85–89 in a migration notes column to
alert the content reviewer that the source has duplicate objects needing cleanup.
Pairs with a separate (owner-authorized) fix to `Bootcamp.tsx`.

**Pros:** Preserves the audit trail; source data issue is visible to content reviewers.  
**Cons:** Minimal extra complexity.

### Option C — Defer Bootcamp.tsx migration; migrate cyberLabs.js only

Migrate only `cyberLabs.js` (76 integrated supplemental labs) in the Sprint 3 seed
script, and treat `Bootcamp.tsx` migration as a separate, later deliverable.

**Pros:** Cleanest Sprint 3 scope boundary; no structural issues to resolve now.  
**Cons:** Defers 93 primary curriculum lab entries; `Bootcamp.tsx` migration becomes
  a separate tracked task.

---

## 8. Recommendation

**Option B.**

The data quality is high, deduplication is trivial, and the DRAFT lifecycle gate
already prevents unreviewed content from reaching learners. Option B adds the
source-fix audit trail at zero extra implementation cost.

Specific actions requested from owner:

1. **Approve Option A, B, or C** (or provide alternate direction) for the seed script.
2. **Confirm whether days 50–54 should be seeded as empty placeholders** or held out
   until content is authored.
3. **Authorize (or defer) the duplicate cleanup in `Bootcamp.tsx`** (days 85–89) —
   this is a change to the live bootcamp component and requires explicit approval.
4. **Confirm the cyberLabsNew.js decommission**: if `Bootcamp.tsx` is confirmed as
   the evolved replacement, `cyberLabsNew.js` can be annotated as superseded (not
   deleted until owner confirms).

---

## 9. What This Decision Does NOT Affect

- The existing bootcamp remains accessible and unmodified (bootcamp protection constraint).
- No seed script will be executed until: (a) this disposition is approved, (b) the 8
  curriculum mapping decisions are resolved, and (c) owner explicitly authorizes execution.
- DR-012 AI content controls remain in force: no DRAFT entry can reach PUBLISHED
  without a human reviewer advancing it through the lifecycle.
- Sprint 3 closure report (Task #148) will be produced after this decision is received
  and Task #147 (frontend verification) is complete.

---

## 10. Inspection Metadata

| Item | Value |
|------|-------|
| Source file inspected | `bootcamp-hub/docs/migration/Bootcamp.tsx` |
| Inspection method | Python regex extraction against 1.5MB source file |
| Extracted data | `/tmp/claude-0/-home-claude/69d23d79-6b93-5784-aa4a-fe2cd92db7ce/scratchpad/days_clean.json` |
| Original DR-OPEN-008 subject | `src/data/cyberLabsNew.js` (72 entries, superseded) |
| Report author | Claude Sonnet 4.6, Sprint 3, session https://claude.ai/code/session_01GrXQBHN1FfE3b1PuBWsGTt |
| Previous report version | Superseded by this document |

---

*Report produced by Claude Sonnet 4.6, Sprint 3. Source: `Bootcamp.tsx` DAYS array
structural extraction. Covers all 93 unique entries plus 5 duplicates and 5 missing days.
No content was modified. No seed script was executed. No bootcamp functionality was affected.*
