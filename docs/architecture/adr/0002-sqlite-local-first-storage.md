# ADR 0002: Use SQLite for Local-First Storage

Status: Accepted

## Context

DeskFolio must run locally and support exporting all data as a single file.

## Decision

Use SQLite as the application database.

## Rationale

SQLite is embedded, reliable, widely used and stores the full database in a single file. This directly supports backup, export and portability requirements.

## Consequences

- Enable foreign keys on each connection.
- Design migrations carefully because local user databases must survive upgrades.
- Avoid database-specific assumptions that make data export/import fragile.
