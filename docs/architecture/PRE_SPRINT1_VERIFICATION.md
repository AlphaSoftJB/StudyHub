# Pre-Sprint 1 Verification Report

**Date:** 2026-11-05  
**Branch:** `studyhub-dev` (commit `4b73ab1`)  
**Author:** Claude Sonnet 4.6, session https://claude.ai/code/session_01GrXQBHN1FfE3b1PuBWsGTt  
**Status:** COMPLETE — 6/6 checks passed with findings documented

---

## Check 1 — Live Deployment: Repository, Branch & Deployment Configuration

**Repository:** `AlphaSoftJB/bootcamp-hub` (public)  
**Default branch:** `main`  
**GitHub Pages deployment:** Served from `main` branch root directory at:
```
https://alphasoftjb.github.io/bootcamp-hub/
```

**Evidence — base path hardcoded in deployed files:**
```html
<!-- index.html -->
<script src="/bootcamp-hub/assets/index-u5-2w4C2.js"></script>
<link href="/bootcamp-hub/assets/index-BeIXDy_B.css">

<!-- 404.html SPA redirect -->
var path = window.location.pathname.replace('/bootcamp-hub', '') || '/'
var redirect = window.location.origin + '/bootcamp-hub/#' + path
```

**Deployment mechanism:** No `.github/workflows/` directory exists. GitHub Pages is configured to serve directly from the `main` branch root — no CI/CD pipeline. Deployments happen by pushing files to `main`. This is a **manual push-to-deploy** setup.

**Files currently serving the live bootcamp:**
```
index.html                         ← hub entry point
assets/index-u5-2w4C2.js          ← compiled application bundle (~4.8MB)
assets/index-BeIXDy_B.css         ← compiled stylesheet
404.html                           ← GitHub Pages SPA fallback redirect
vite.svg                           ← favicon
```

The six JS bundles and six CSS files in `assets/` are historical build artifacts from previous versions. Only `index-u5-2w4C2.js` and `index-BeIXDy_B.css` are actively referenced.

---

## Check 2 — File Integrity: Committed `.tsx` Files vs Originals

All three committed source files match every copy of their respective originals from session attachments. Each file was provided multiple times across sessions; all copies are identical.

| File | Committed SHA-256 | Attachment matches | Lines |
|---|---|---|---|
| `Bootcamp.tsx` | `b9abf2fc...b8bbe8` | ✅ 3/3 attachment copies match | 16,385 |
| `Ibm_bootcamp_v4.tsx` | `4551994b...d0207` | ✅ 2/2 attachment copies match | 4,174 |
| `Aibootcampv3.tsx` | `b973753e...94c49` | ✅ 4/4 attachment copies match | 1,898 |

**Verdict:** Committed files are verified against all available originals. No corruption or partial write.

---

## Check 3 — Source Completeness: What Is and Isn't Preserved

### Preserved ✅
| Asset | Location | Status |
|---|---|---|
| Cybersecurity bootcamp source | `docs/migration/Bootcamp.tsx` | Committed, hash-verified |
| IBM bootcamp source | `docs/migration/Ibm_bootcamp_v4.tsx` | Committed, hash-verified |
| AI Engineering bootcamp source | `docs/migration/Aibootcampv3.tsx` | Committed, hash-verified |
| Learner data inventory v2.0 | `docs/migration/LEARNER_DATA_INVENTORY.md` | Committed |
| All architecture/design docs | `docs/architecture/`, `docs/design/` | Committed on `main` |
| Live compiled bundle | `assets/index-u5-2w4C2.js` | Committed on `main` |

### Not in repository — missing dependencies ⚠️
| Missing item | Notes |
|---|---|
| `cyber.html`, `ibm.html`, `ai.html` | The compiled bundle loads bootcamps as embedded React components or via iframes — these separate HTML files were described in earlier sessions but do NOT exist in this repo. The live app is entirely contained in `index-u5-2w4C2.js`. |
| `cyberLabsNew.js` | Referenced in DR-OPEN-008 as a file requiring inspection; not present in repo. Unknown if it exists in the live deployment or was retired. |
| Vite build configuration | No `vite.config.ts`, `package.json`, or `tsconfig.json`. The compiled bundle exists but the build toolchain source is absent. The bootcamp cannot be rebuilt from sources in this repo without the build config and `node_modules`. |
| `window.storage` data | Cybersecurity progress lives in Anthropic's artifact runtime — not recoverable from this repo. |

**Critical gap:** The `.tsx` source files are preserved but the build toolchain is not. The existing live bootcamp can be served as-is from `main`, but cannot be rebuilt from source within this repository. This is an existing condition, not a regression.

### Historical build artifacts (safe to ignore)
Five older `.js` bundles and five older `.css` files remain in `assets/`. They are tracked in git but not referenced by `index.html`. They represent prior build iterations. No action needed.

---

## Check 4 — Branch Isolation: `studyhub-dev` vs Production

| Item | Verified |
|---|---|
| `studyhub-dev` branched from `main` at commit `9a1a348` | ✅ |
| `studyhub-dev` is ahead of `main` by exactly 1 commit (`4b73ab1`) | ✅ |
| No commits from `main` are missing from `studyhub-dev` | ✅ |
| `main` branch unchanged — still at `9a1a348` | ✅ |
| No GitHub Pages deployment configuration modified | ✅ |
| GitHub Pages serves ONLY from `main` branch root | ✅ |

**Isolation verdict:** `studyhub-dev` is fully isolated from the live deployment. Pushing to `studyhub-dev` has zero effect on what GitHub Pages serves. Only a merge or direct push to `main` could affect production.

---

## Check 5 — Remote, Branch Protection & Deployment Workflow

| Item | State |
|---|---|
| Remote URL | `https://github.com/AlphaSoftJB/bootcamp-hub` |
| Repo visibility | Public |
| `main` branch protection | **NONE** — `protected: false` confirmed via GitHub API |
| CI/CD workflow | **NONE** — no `.github/workflows/` directory |
| Deployment trigger | Manual push to `main` → GitHub Pages serves immediately |
| `studyhub-dev` protection | None (expected for dev branch) |

**Risk:** `main` has no branch protection rules. There is nothing preventing an accidental direct push to `main`. The only safeguard is the policy established in this session. **Recommend enabling branch protection on `main`** (require PR + review before merge) via GitHub Settings → Branches. This is a configuration change in GitHub UI, not a code change — requires your action.

---

## Check 6 — Backend Scaffold Status

The Spring Boot backend scaffold from a previous session exists in the sandbox at `/home/claude/studyhub-backend/` but is **not a git repository and not committed anywhere.**

| File | Status |
|---|---|
| `pom.xml` | Sandbox only — NOT committed |
| `src/main/resources/application.yml` | Sandbox only — NOT committed |
| `src/main/resources/application-test.yml` | Sandbox only — NOT committed |
| `src/main/java/.../StudyHubApplication.java` | Sandbox only — NOT committed |
| `src/main/java/.../StudyHubProperties.java` | Sandbox only — NOT committed |

This scaffold is at risk of loss on sandbox reset. It must be committed before Sprint 1 implementation begins. **This requires a decision on where the backend repository lives** (DR-OPEN-003 — backend hosting is unresolved). Options:
- Sub-directory in this repo: `studyhub-backend/` on `studyhub-dev`
- Separate GitHub repository: `AlphaSoftJB/studyhub-backend`

---

## Summary Table

| Check | Result | Status |
|---|---|---|
| 1. Live deployment config | `main` root → GitHub Pages, manual push-to-deploy, no CI/CD | ✅ Documented |
| 2. File integrity | All 3 `.tsx` files hash-match all original attachments | ✅ Verified |
| 3. Source completeness | Build toolchain absent; `cyberLabsNew.js` unlocated | ⚠️ Gap documented |
| 4. Branch isolation | `studyhub-dev` fully isolated; `main` unchanged | ✅ Confirmed |
| 5. Branch protection | `main` unprotected — no CI/CD, no PR rules | ⚠️ Risk documented |
| 6. Backend scaffold | Exists in sandbox only — not git-tracked anywhere | 🔴 Action required |

---

## Risks and Recommendations

### 🔴 Must resolve before implementation
1. **Backend scaffold not committed.** `/home/claude/studyhub-backend/` exists in sandbox only. Sandbox files do not persist indefinitely. Resolve DR-OPEN-003 (backend hosting) and commit the scaffold to a repository before writing any backend implementation code.

### ⚠️ Should resolve before Sprint 1 ends
2. **`main` has no branch protection.** An accidental `git push origin main` from this sandbox would immediately update the live GitHub Pages deployment with no review gate. Enable branch protection in GitHub Settings.
3. **Build toolchain not preserved.** The compiled bundle can be served but not rebuilt from source. The `.tsx` files are preserved, but `package.json`, `vite.config.ts`, and `tsconfig.json` are absent. If the bundle ever needs to be rebuilt (bug fix, new content), the build config must be recreated. No immediate action needed — existing bootcamp runs fine — but document this gap.

### ✅ No action required
4. `cyberLabsNew.js` — deferred to Sprint 3–4 per DR-OPEN-008. Not needed now.
5. Historical `assets/` bundles — not referenced, not harmful; cleanup is cosmetic.

---

## Existing Bootcamp Availability Confirmation

The live bootcamp at `https://alphasoftjb.github.io/bootcamp-hub/` is served entirely from files committed to `main`. No files in `main` were modified during this session. The working tree on `main` is clean. The bootcamp remains available for certification preparation and is completely unaffected by any work on `studyhub-dev`.

---

*Pre-Sprint 1 verification complete. Implementation may begin on `studyhub-dev` after the backend repository decision (DR-OPEN-003) is resolved and the scaffold is committed.*
