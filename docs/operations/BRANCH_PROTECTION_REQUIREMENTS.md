# Branch Protection Requirements

**Date:** 2026-11-05  
**Status:** Required — pending owner action in GitHub Settings  
**Author:** Claude Sonnet 4.6, session https://claude.ai/code/session_01GrXQBHN1FfE3b1PuBWsGTt

---

## Why This Is Required

Both repositories currently have `protected: false` on their `main` branches.  
This means an accidental `git push origin main` would immediately update the live  
GitHub Pages deployment for `bootcamp-hub`, or silently overwrite `main` in `studyhub`,  
with no review gate. The only safeguard is policy.

Branch protection is a one-time GitHub UI action that creates a hard technical barrier.

---

## Repository 1: `AlphaSoftJB/bootcamp-hub`

**URL:** https://github.com/AlphaSoftJB/bootcamp-hub/settings/branches

### Rule for `main`

| Setting | Value |
|---|---|
| Branch name pattern | `main` |
| Require a pull request before merging | ✅ Enable |
| Required approvals | 1 (yourself) |
| Dismiss stale pull request approvals when new commits are pushed | ✅ Enable |
| Require status checks to pass before merging | ☐ Not required (no CI/CD exists) |
| Require branches to be up to date before merging | ✅ Enable |
| Include administrators | ✅ Enable — prevents force-push even by owner |
| Allow force pushes | ❌ Disable |
| Allow deletions | ❌ Disable |

**Risk without this:** A single `git push origin main` from the development sandbox  
immediately updates the live bootcamp at `https://alphasoftjb.github.io/bootcamp-hub/`.

---

## Repository 2: `AlphaSoftJB/StudyHub`

**URL:** https://github.com/AlphaSoftJB/StudyHub/settings/branches

### Rule for `main`

| Setting | Value |
|---|---|
| Branch name pattern | `main` |
| Require a pull request before merging | ✅ Enable |
| Required approvals | 1 (yourself) |
| Dismiss stale pull request approvals when new commits are pushed | ✅ Enable |
| Require status checks to pass before merging | ☐ Optional for now; enable when CI is configured |
| Require branches to be up to date before merging | ✅ Enable |
| Include administrators | ✅ Enable |
| Allow force pushes | ❌ Disable |
| Allow deletions | ❌ Disable |

### Rule for `studyhub-dev` (optional but recommended)

| Setting | Value |
|---|---|
| Branch name pattern | `studyhub-dev` |
| Allow force pushes | ❌ Disable |
| Allow deletions | ❌ Disable |

**Risk without this:** No guard prevents an accidental merge or direct push to `main`  
before owner review.

---

## Steps to Apply (GitHub UI)

1. Navigate to the repository → **Settings** → **Branches**
2. Under "Branch protection rules", click **Add rule**
3. Enter `main` as the branch name pattern
4. Enable the settings in the table above
5. Click **Create** (or **Save changes**)
6. Repeat for the second repository

This is a **GitHub Settings action only** — no code change, no deployment impact.

---

## Current Risk Level

| Repository | Branch | Protection | Risk |
|---|---|---|---|
| `bootcamp-hub` | `main` | ❌ None | 🔴 High — live bootcamp deployment |
| `studyhub` | `main` | ❌ None | ⚠️ Medium — dev repo, no live deployment yet |
| `studyhub` | `studyhub-dev` | ❌ None | ✅ Low — dev branch, expected |

