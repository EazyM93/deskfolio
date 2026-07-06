# 🏗 Project Structure

This repository separates committed product documentation from local-only AI operational memory.

## Committed Structure

```text
deskfolio/
  pom.xml
  README.md
  CODEX.md
  CLAUDE.md
  docs/
    architecture/
      overview.md
      dependency-strategy.md
      project-structure.md
      adr/
    development/
      workflow.md
      coding-conventions.md
      ai-assisted-development.md
      emoji-conventions.md
    roadmap/
      implementation-roadmap.md
    releases/
      README.md
      0.1.0.md
    specs/
      database/
      features/
      ui/
  src/
    main/
      java/
      resources/
        db/
          migration/
    test/
      java/
      resources/
```

## Local-Only Structure

```text
deskfolio/
  .ai/
    project-status.md
    current-task.md
    backlog.md
    roadmap.md
    decisions.md
    known-issues.md
    session-summary.md
  .claude/
    prompts/
    templates/
    rules.md
  .codex/
    prompts/
    templates/
    rules.md
```

`.ai/`, `.codex/` and `.claude/` are ignored by Git. They exist to help AI agents resume work across sessions without polluting the permanent repository history. Codex is the primary agent workspace; `.claude/` is kept only for developers who use Claude.

## Source Directory Intent

- `src/main/java`: Java application source organized by layer and feature.
- `src/main/resources`: JavaFX FXML, CSS, icons, configuration defaults and Flyway migrations.
- `src/main/resources/db/migration`: versioned Flyway SQL migrations.
- `src/test/java`: unit and integration tests.
- `src/test/resources`: test fixtures and test-only configuration.

## Documentation Directory Intent

- `docs/architecture`: long-lived architectural guidance and ADRs.
- `docs/specs`: project, database, UI and feature specifications.
- `docs/development`: workflow, coding conventions, review standards and AI collaboration rules.
- `docs/roadmap`: committed product roadmap at milestone level.
- `docs/releases`: detailed release notes, updated only on explicit user request.
- `CHANGELOG.md`: concise version-by-version summary, updated only on explicit user request.

## Rule

If information is useful to all future developers, commit it under `docs/` or `CODEX.md`. Keep `CLAUDE.md` aligned only when compatibility guidance for Claude changes.

If information is temporary, session-specific, task-progress-related or AI-operational, keep it in `.ai/` or `.codex/`. Use `.claude/` only for Claude-specific local files.
