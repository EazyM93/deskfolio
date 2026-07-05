# 📦 Dependency Strategy

Dependencies must be stable, widely adopted and clearly useful. Avoid adding libraries for convenience when Java or JavaFX already provide a maintainable solution.

## Selected Dependencies

| Dependency | Purpose | Why Selected | Alternatives |
|---|---|---|---|
| JavaFX | Native desktop UI | Mature Java desktop UI toolkit with FXML, CSS and native packaging support | Swing, SWT, Compose Desktop |
| SQLite JDBC | SQLite access from Java | Standard JDBC driver for embedded SQLite | JNA/native SQLite wrappers |
| Flyway | Database migrations | Versioned migrations, widely adopted, supports SQLite foundational migrations | Liquibase, custom schema_version table |
| SLF4J | Logging API | Stable facade that avoids binding code to one backend | java.util.logging |
| Logback | Logging backend | Mature SLF4J backend with file logging support | Log4j2 |
| JUnit 5 | Testing | Modern Java test framework | TestNG |
| AssertJ | Assertions | Readable assertions improve maintainability | JUnit assertions |
| Mockito | Test doubles | Useful for service boundary tests | Manual fakes |
| ControlsFX | JavaFX controls | Mature set of controls for forms and UX polish | Custom controls |
| Ikonli | Icons | Consistent icon integration in JavaFX | Image assets, custom SVG loaders |

## Flyway and SQLite Decision

Flyway is appropriate for DeskFolio because SQLite 3.7+ is supported for Flyway foundational migration capabilities. DeskFolio needs a reliable local migration history as the schema evolves over years.

Use Flyway for:

- Schema creation
- Seed reference data such as asset types and movement types
- Backward-compatible schema migrations
- Validation that the database schema matches the application version

Avoid Flyway for:

- User data import/export workflows
- Long-running data repair jobs that need UI progress
- Business-level transformations that require domain validation

If Flyway becomes too limiting for a future SQLite-specific migration, add a documented application-level migration service for that exceptional case instead of replacing Flyway globally.

## Dependency Addition Checklist

Before adding a dependency:

- Confirm it solves a real problem in the current roadmap.
- Check license compatibility.
- Prefer libraries with stable releases, documentation and active maintenance.
- Add it to `pom.xml`.
- Document it in this file.
- Add an ADR if it affects architecture.
