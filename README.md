# DeskFolio

DeskFolio is a local-first native macOS desktop application for managing a personal investment portfolio.

The application will be built with Java 26, JavaFX, Maven and SQLite. It is designed as a maintainable desktop product, not as a web application. The final distribution target is a normal macOS `.app` bundle that can be started with a double click.

## 📌 Project Status

This repository is currently prepared for documentation-first, AI-assisted development.

It intentionally contains no application implementation code yet. The current repository defines:

- Architecture and engineering rules
- Maven project structure
- Database design
- UI specifications
- Feature specifications
- ADRs
- Development workflow
- Claude Code operating context
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

- [CLAUDE.md](CLAUDE.md): permanent engineering handbook for Claude Code and developers
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

DeskFolio is optimized for long-term Claude Code collaboration.

Permanent project guidance lives in `CLAUDE.md` and committed documentation under `docs/`.

Local AI memory lives in ignored directories:

- `.ai/`
- `.claude/`

These directories are intentionally excluded from Git so task state, session summaries, prompts and planning notes remain local-only.

## 🚀 First Implementation Milestone

The first implementation milestone is not to build the whole app. It is to create a thin vertical slice:

1. Start a JavaFX desktop shell
2. Initialize SQLite database with Flyway
3. Add asset and transaction persistence
4. Implement transaction entry flow
5. Show dashboard with manually entered monthly portfolio values
6. Package a macOS `.app`

See [Implementation Roadmap](docs/roadmap/implementation-roadmap.md).
