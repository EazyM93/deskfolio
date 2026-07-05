# ADR 0001: Use JavaFX for Native Desktop UI

Status: Accepted

## Context

DeskFolio must be a native macOS desktop application, not a web application. It must be distributable as a normal `.app`.

## Decision

Use JavaFX as the desktop UI framework.

## Rationale

JavaFX is a mature Java desktop framework with FXML, CSS styling, controls, charts and compatibility with Java packaging workflows. It keeps the application in the mandatory Java ecosystem and avoids embedding a web runtime.

## Consequences

- UI code must stay separated from domain and service logic.
- Modern styling requires deliberate CSS and design tokens.
- Some native macOS polish may require careful packaging and platform testing.
