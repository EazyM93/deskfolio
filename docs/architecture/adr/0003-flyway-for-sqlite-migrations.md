# ADR 0003: Use Flyway for SQLite Migrations

Status: Accepted

## Context

The database schema will evolve over time. Local user data must be migrated safely between application versions.

## Decision

Use Flyway SQL migrations for SQLite schema versioning.

## Rationale

Flyway provides a mature migration history mechanism and supports SQLite for foundational migration capabilities. This is more maintainable than a custom migration table in the first version.

## Consequences

- Migration files live under `src/main/resources/db/migration/`.
- Migrations must be reviewed carefully because they affect user data.
- Exceptional future data transformations may be handled by an application-level migration service if Flyway is not expressive enough.
