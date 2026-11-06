-- V6: Add administrative revocation support to invitation_codes (DR-024)
--
-- DR-024 requires: "Admin can revoke an unused invitation code before it is redeemed."
-- The row is preserved for audit history when revoked (not deleted).
--
-- Also adds missing indexes on created_by and expires_at
-- (identified in Sprint 1 Validation Gate — F-DB-001).

-- Step 1: Add revocation columns
-- Note: AFTER clause omitted — H2 MySQL MODE does not support column positioning.
-- Note: Two separate ALTER TABLE statements — H2 MySQL MODE does not support
--       comma-separated multiple ADD COLUMN clauses in one statement (MySQL extension).
-- Column order does not affect correctness; MySQL production will append at end.
ALTER TABLE invitation_codes ADD COLUMN revoked    BOOLEAN    NOT NULL DEFAULT FALSE;
ALTER TABLE invitation_codes ADD COLUMN revoked_at DATETIME(6)         NULL;

-- Step 2: Add performance indexes (separate statements for H2 compatibility)
CREATE INDEX idx_invitation_codes_created_by ON invitation_codes (created_by);
CREATE INDEX idx_invitation_codes_expires_at ON invitation_codes (expires_at);
