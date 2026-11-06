# StudyHub

Multi-discipline learning platform for IT certifications, medical (USMLE) and academic (WAEC/NECO/JAMB) preparation.  
Built by **AlphaSoft LLC**.

---

## Repository structure

```
studyhub/
├── frontend/
│   ├── web/                 # React + Vite web application (Sprint 1)
│   └── mobile/              # React Native + Expo (future phase)
│
├── backend/
│   └── studyhub-api/        # Spring Boot 3.3.5 modular monolith (Java 21)
│
├── docs/
│   ├── architecture/        # Decision records, roadmap, audits
│   ├── migration/           # Learner data inventory and migration plan
│   ├── design/              # Visual direction and UX documentation
│   └── operations/          # Runbooks, deployment procedures (future)
│
├── infrastructure/          # IaC, Docker, deployment config (future)
├── scripts/                 # Developer utility scripts (future)
└── README.md
```

---

## Development workflow

| Branch | Purpose |
|---|---|
| `main` | Stable, reviewed code only. Never commit directly. |
| `studyhub-dev` | Active development branch. All Sprint work goes here. |
| `feature/*` | Optional feature branches for substantial independent work. |

**Rule:** `main` ← PR from `studyhub-dev` only, with explicit owner approval. No direct pushes.

---

## Related repositories

| Repository | Purpose |
|---|---|
| [`AlphaSoftJB/bootcamp-hub`](https://github.com/AlphaSoftJB/bootcamp-hub) | Existing bootcamp (live, do not modify) |
| [`AlphaSoftJB/studyhub`](https://github.com/AlphaSoftJB/studyhub) | This repository — new platform |

**The existing bootcamp at `bootcamp-hub` must remain operational and unmodified throughout StudyHub development.**

---

## Backend quick start (local dev)

Requires: Java 21, MySQL 8, Maven 3.9+

```bash
# Set required environment variables (never commit these)
export DB_URL=jdbc:mysql://localhost:3306/studyhub
export DB_USER=studyhub
export DB_PASSWORD=your_local_password
export JWT_ACCESS_SECRET=your_32plus_char_secret
export JWT_REFRESH_SECRET=your_32plus_char_secret

cd backend/studyhub-api
mvn spring-boot:run
```

API available at `http://localhost:8080/api`

---

## Status

**Phase 1 Sprint 1 — in setup.** See `docs/architecture/STUDYHUB_IMPLEMENTATION_ROADMAP.md` for the full plan and `docs/architecture/STUDYHUB_DECISION_RECORD.md` for all architectural decisions.
