# CODEX.md

This file is DeskFolio's permanent engineering handbook for Codex. It must be committed to Git and kept concise. Do not store active tasks, session summaries, temporary plans, backlog state or progress logs here. Those belong in `.ai/`, which is local-only and ignored by Git.

`CLAUDE.md` remains available for developers who use Claude. Codex should use this file as its canonical agent handbook.

## Mission

DeskFolio is a local-first native macOS desktop application for personal investment portfolio tracking. It is not a web app. It must eventually ship as a double-clickable macOS `.app`.

## Architecture Principles

Prioritize, in order:

1. Maintainability over cleverness
2. Readability over brevity
3. Long-term scalability over short-term convenience
4. Simplicity over unnecessary abstraction
5. Consistency over personal preference
6. Explicitness over implicit behavior
7. SOLID principles over quick implementations
8. Composition over inheritance
9. Small, focused and testable components
10. Documentation as part of the software

Use a layered desktop architecture:

- `domain`: business concepts and rules
- `dto`: data transfer objects for UI/service boundaries
- `validation`: reusable validation rules
- `repository`: persistence contracts
- `persistence`: SQLite and Flyway implementations
- `service`: use cases and business orchestration
- `ui`: JavaFX views, FXML, controls and components
- `controller`: JavaFX controllers
- `navigation`: module routing and screen state
- `configuration`: app paths, preferences and runtime configuration
- `theme`: design tokens, CSS and theme switching
- `util`: narrow technical helpers only

Dependencies should point inward. UI depends on services. Services depend on repositories and domain. Domain depends on nothing application-specific.

## Technology Rules

- Java version: 26
- Build tool: Maven
- Desktop UI: JavaFX
- Database: SQLite
- Migration strategy: Flyway SQL migrations
- Packaging: Maven plus `jpackage` for macOS `.app`
- Logging: SLF4J API with Logback backend
- Tests: JUnit 5, AssertJ, Mockito where useful

Avoid introducing dependencies without documenting the reason in `docs/architecture/dependency-strategy.md` and, when architectural, an ADR.

## Database Rules

- SQLite is required because the whole database must be exportable as one file.
- Enable foreign keys on every connection.
- Use Flyway for schema versioning.
- Keep migrations backward-compatible where practical.
- Prefer normalized tables for stable concepts such as asset types, movement types, currencies and portfolios.
- Store monetary values as integer minor units or fixed-scale decimal text. Do not use floating point for money.
- Store percentages such as TER as fixed-scale decimal text.
- Use UTC timestamps for technical audit fields and local dates for user financial dates.

## UI Rules

The UI should feel modern and native, inspired by IntelliJ IDEA, Apple design, Notion and Raycast.

- Support dark mode from the beginning.
- Prefer FXML for stable screens and Scene Builder-friendly layout.
- Use programmatic JavaFX only for highly dynamic/reusable components.
- Keep controllers thin. Controllers coordinate UI events and delegate business logic to services.
- Avoid old Swing-like visual density, default unstyled controls and inconsistent spacing.
- Navigation must be modular so future sections can be added without rewriting the shell.

## Testing Rules

Use the test pyramid:

- Domain and service unit tests first
- Repository integration tests with temporary SQLite databases
- UI smoke tests for navigation and critical flows when infrastructure exists
- Migration tests for Flyway schema changes

Every feature must include tests appropriate to its risk.

## Feature Workflow

Use spec-driven vertical slices:

1. Read `.ai/project-status.md`, `.ai/current-task.md` and `.ai/backlog.md`
2. Read the relevant committed specs under `docs/`
3. Confirm the task is `READY`
4. Implement the smallest vertical slice
5. Run relevant checks
6. Update documentation when conventions or architecture change
7. Update `.ai/` memory and task statuses

Do not start broad refactors during feature work unless the task explicitly requires it.

## AI Memory Rules

Codex must update local AI memory after meaningful work:

- `.ai/project-status.md`
- `.ai/current-task.md`
- `.ai/backlog.md`
- `.ai/known-issues.md`
- `.ai/session-summary.md`

When the user says "Continue working" or "Riprendi il lavoro", inspect `.ai/` first and resume from the current task without asking where to continue unless the memory is contradictory or blocked.

## Documentation Maintenance

Update this file only when long-term conventions, architecture or workflow guidance changes.

Do not append stale notes. Replace obsolete guidance. Keep this file clean enough to remain useful after years of development.

## Changelog and Release Notes

`CHANGELOG.md` summarizes what each version introduces. Detailed release notes live in `docs/releases/`.

Do not update changelog or release note files automatically after normal implementation work. Update them only when the user explicitly asks for changelog or release note maintenance.

Routine development progress belongs in `.ai/`, not in release documentation.
