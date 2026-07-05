# 🏗 Architecture Overview

DeskFolio uses a layered architecture adapted for a long-lived JavaFX desktop application.

## Goals

- Keep business rules independent from JavaFX and SQLite.
- Make features implementable as small vertical slices.
- Keep UI controllers thin and testable.
- Allow database schema evolution without corrupting local user data.
- Keep native macOS packaging as a first-class requirement.

## Layers

| Layer | Responsibility | Depends On |
|---|---|---|
| UI | FXML, CSS, reusable controls, visual layout | Controllers, theme |
| Controllers | UI event handling and view state coordination | Services, DTOs, navigation |
| Services | Use cases, workflows, business orchestration | Domain, repositories, validation |
| Domain | Core entities, value objects, invariants | None |
| DTOs | Boundary objects between UI and services | Domain primitives |
| Validation | Reusable input and business validation | Domain |
| Repositories | Persistence contracts | Domain |
| Persistence | SQLite, Flyway, JDBC implementations | Repositories, domain mapping |
| Configuration | App paths, preferences, runtime config | Java platform APIs |
| Navigation | Module registry, screen routing | UI/controller abstractions |
| Theme | Design tokens, CSS, dark/light mode | JavaFX |
| Utilities | Small technical helpers | Narrow dependencies only |

## Dependency Direction

Dependencies must flow from outer layers to inner layers:

`UI -> Controllers -> Services -> Repositories -> Persistence`

`Domain` stays independent and should not import JavaFX, JDBC, Flyway or logging implementation classes.

## Vertical Slice Rule

Implement features as complete thin slices through the layers. Example: "Add transaction" includes UI form, validation, service method, repository contract, SQLite implementation, tests and documentation updates.

Avoid building a large generic framework before the first feature proves the need.

## Package Convention

Future Java packages should use:

`io.deskfolio.<layer>[.<feature>]`

Examples:

- `io.deskfolio.domain.asset`
- `io.deskfolio.service.transaction`
- `io.deskfolio.persistence.sqlite.transaction`
- `io.deskfolio.ui.dashboard`

## Error Handling

- Domain validation errors should be explicit and user-presentable.
- Unexpected technical errors should be logged and converted into safe UI messages.
- Persistence errors must not leak raw SQL details into the UI.

## Configuration

Application data should live in the macOS user application support directory, not beside the executable. The exact path strategy must be implemented in the configuration layer and documented when added.
