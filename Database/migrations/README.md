# Database Migrations Documentation

## Overview

This directory contains versioned SQL migrations for the **OnLineInternship.in** production database schema.
Migrations preserve existing tables and records. Schema additions, widening, and documented nullability changes must be reviewed for backward compatibility before execution.

The central source of truth for base production schema is:
`Database/onlineinternship.sql`

---

## Migration Catalog

| Migration File | Purpose | Impact on Existing Data | Rollback Available |
|---|---|---|---|
| `V001__add_student_indexes.sql` | Adds secondary indexes on `student.email`, `student.phone`, and `student.referalCode` | None (indexes only) | Yes (`ALTER TABLE student DROP INDEX ...`) |
| `V002__create_engineering_year_marks.sql` | Creates `engineering_year_marks` table for storing 1-to-many year-wise engineering scores (First Year, Second Year, Third Year, Final Year) per student | None (new additive table, foreign-keyed to `student.id`) | Yes (`DROP TABLE IF EXISTS engineering_year_marks;`) |
| `V003__create_platform_certificates.sql` | Creates `platform_certificates` for exam name, score, and achievement date, which are not stored by the production `certificate` PDF table | None (new additive table, foreign-keyed to `student.id`) | Yes (`DROP TABLE IF EXISTS platform_certificates;`) |
| `V004__enhance_assignment_management.sql` | Adds master assignment fields, a typed allocation reference, duplicate-allocation protection, and nullable legacy fields with no source in the admin workflow | Preserves existing rows; legacy allocations remain unlinked, and existing assignments default to inactive | Conditional; see rollback instructions in the migration (new metadata is discarded) |

---

## Migration Safety Guidelines

1. **No Table Drops or Truncations**: Migrations must never drop, truncate, or rename existing production tables.
2. **Backward Compatibility**: Existing table names, columns, and primary keys are retained. Any widening or nullability changes must be documented and preserve existing values.
3. **Prefix Indexing**: MySQL `VARCHAR(500)` columns indexed with `(191)` prefix to guarantee full compatibility with InnoDB 767-byte limit under `utf8mb4` as well as `latin1`.
4. **Execution Protocol**:
   - Test on the development/staging MySQL database first.
   - Run during maintenance windows or standard deployment pipelines.
   - Never run raw untracked DDL on production without change documentation.

---

## Applying Migrations

### Local Environment
```bash
mysql -u root -p onlineinternship < Database/migrations/V001__add_student_indexes.sql
mysql -u root -p onlineinternship < Database/migrations/V002__create_engineering_year_marks.sql
mysql -u root -p onlineinternship < Database/migrations/V003__create_platform_certificates.sql
mysql -u root -p onlineinternship < Database/migrations/V004__enhance_assignment_management.sql
```

### CloudJiffy / Remote Production
Log in to the CloudJiffy phpMyAdmin or MySQL CLI console and execute the SQL migration files against the target production database in sequence.
