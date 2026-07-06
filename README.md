# DeskFolio

DeskFolio is a local-first native macOS desktop application for managing a personal investment portfolio.

The application is built with Java 26, JavaFX, Maven and SQLite. It is designed as a maintainable desktop product, not as a web application. The final distribution target is a normal macOS `.app` bundle that can be started with a double click.

## 📌 Project Status

This repository is currently in active MVP implementation after documentation-first preparation.

The current repository defines:

- Architecture and engineering rules
- Maven project structure
- JavaFX application shell
- SQLite and Flyway database foundation
- Asset and transaction entry vertical slice
- Transaction-derived dashboard MVP
- Database design
- UI specifications
- Feature specifications
- ADRs
- Development workflow
- Codex operating context
- Local-only AI workspace structure

## 🏗 Architecture Summary

DeskFolio follows a layered desktop architecture:

- `UI`: JavaFX views and visual components
- `Controllers`: JavaFX event handling and view coordination
- `Services`: application use cases and business orchestration
- `Domain`: core business objects and rules
- `Repositories`: persistence-facing contracts
- `Persistence`: SQLite implementations and database access
- `Validation`: input and domain validation
- `Navigation`: scalable module navigation
- `Configuration`: application settings and environment paths
- `Theme`: dark/light mode and design tokens

See [Architecture Overview](docs/architecture/overview.md).

## ⚙ Technology Stack

- Java 26
- JavaFX
- Maven
- SQLite
- Flyway for database migrations
- SLF4J + Logback for logging
- JUnit 5 + AssertJ + Mockito for testing
- ControlsFX for mature JavaFX controls where useful
- Ikonli for icon support
- jpackage for native macOS packaging

Each dependency is explained in [Dependency Strategy](docs/architecture/dependency-strategy.md).

## 📚 Key Documents

- [CODEX.md](CODEX.md): permanent engineering handbook for Codex and developers
- [CLAUDE.md](CLAUDE.md): compatibility handbook for developers who use Claude
- [Architecture Overview](docs/architecture/overview.md)
- [Project Structure](docs/architecture/project-structure.md)
- [Database Design](docs/specs/database/database-design.md)
- [Project Specification](docs/specs/project-specification.md)
- [UI Specification](docs/specs/ui/ui-specification.md)
- [Feature Specifications](docs/specs/features)
- [Development Workflow](docs/development/workflow.md)
- [AI-Assisted Development](docs/development/ai-assisted-development.md)
- [Emoji Conventions](docs/development/emoji-conventions.md)
- [Changelog](CHANGELOG.md)
- [Release Notes](docs/releases)
- [Roadmap](docs/roadmap/implementation-roadmap.md)
- [ADRs](docs/architecture/adr)

## 🧠 AI-Assisted Development

DeskFolio is optimized for long-term Codex collaboration.

Permanent project guidance lives in `CODEX.md` and committed documentation under `docs/`. `CLAUDE.md` remains available for developers who use Claude.

Local AI memory lives in ignored directories:

- `.ai/`
- `.codex/`
- `.claude/`

These directories are intentionally excluded from Git so task state, session summaries, prompts and planning notes remain local-only.

## 🚀 MVP Implementation Scope

The current MVP path is a thin vertical slice:

1. Start a JavaFX desktop shell: completed
2. Initialize SQLite database with Flyway: completed
3. Add asset and transaction persistence: completed
4. Implement transaction entry flow: completed
5. Show transaction-derived invested value in the dashboard: completed
6. Package a macOS `.app`: next milestone

See [Implementation Roadmap](docs/roadmap/implementation-roadmap.md).

## Local Run Modes

Normal local run preserves and migrates the existing local database:

`mvn javafx:run`

Development reset run drops and recreates the local database before startup:

`mvn javafx:run -Plocal-reset`

Use `local-reset` only for disposable development data. Build/default runtime uses managed database migrations without dropping data.

## Local Data Removal

DeskFolio stores local data under:

`~/Library/Application Support/DeskFolio`

A future Settings section will provide an uninstall-data tool to delete that app-owned directory, including `deskfolio.sqlite`, after explicit user confirmation. This is intended for users who want to remove local data before uninstalling the app.
