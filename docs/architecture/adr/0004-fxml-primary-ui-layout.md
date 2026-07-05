# ADR 0004: Use FXML as Primary UI Layout Format

Status: Accepted

## Context

DeskFolio needs maintainable desktop screens and may benefit from Scene Builder.

## Decision

Use FXML for stable screens and programmatic JavaFX for dynamic reusable components.

## Rationale

FXML separates layout from behavior and keeps screen structure readable. Programmatic JavaFX remains better for dynamic chart/card composition and reusable controls.

## Consequences

- Controllers must stay thin.
- FXML files should follow the same module organization as controllers.
- UI state and business workflows belong in services, not FXML controllers.
