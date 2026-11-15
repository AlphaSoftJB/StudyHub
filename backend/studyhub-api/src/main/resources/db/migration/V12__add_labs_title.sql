-- V12: Add missing title column to labs table
--
-- The labs table was created in V9 without a title column.
-- Lab.java entity requires title NOT NULL VARCHAR(300).
-- Since labs has no data yet, we add with a placeholder default,
-- then remove the default to match the entity constraint.

ALTER TABLE labs
    ADD COLUMN title VARCHAR(300) NOT NULL DEFAULT '' AFTER lesson_id;

ALTER TABLE labs
    ALTER COLUMN title DROP DEFAULT;
