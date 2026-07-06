# 🎨 UI Specification

DeskFolio should feel like a modern native macOS productivity application.

## Inspiration

- IntelliJ IDEA: structured navigation, dense but readable tooling layout
- Apple design: native feeling, restraint, high-quality spacing
- Notion: clean content hierarchy and calm surfaces
- Raycast: focused command-like interactions and dark mode polish

## JavaFX Approach

Use FXML for stable screens because it:

- Separates layout from controller logic.
- Works with Scene Builder.
- Keeps UI structure readable for designers and future developers.
- Supports long-term maintainability for desktop screens.

Use programmatic JavaFX for:

- Reusable custom controls
- Dynamic chart/card generation
- Navigation composition
- Small UI factories where FXML becomes repetitive

## Visual System

Plan for:

- Dark mode from the beginning
- Light mode later through the same token system
- Consistent spacing scale
- Consistent typography scale
- CSS variables/design tokens where JavaFX supports them cleanly
- Icon set through Ikonli

## Navigation

Primary modules:

- Dashboard
- Transactions
- Assets
- Settings

Navigation should be registry-based so a future module can provide:

- id
- label
- icon
- view factory or FXML location
- default route

## Layout

Preferred shell:

- Left sidebar navigation
- Main content region
- Icon-only sidebar collapse/restore control to the left of the current page title
- Sidebar collapse and restore should use a short horizontal slide transition
- Optional top toolbar for page-specific actions
- Status/error area when needed

## Accessibility

- Labels must be associated with form controls.
- Keyboard navigation should work for forms.
- Important actions need clear focus states.
- Color must not be the only way to communicate validation status.
