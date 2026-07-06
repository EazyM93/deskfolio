# 🚀 Feature Specification: Settings

Settings are planned after the MVP shell and core portfolio flows.

## Planned Sections

- Theme
- Backup
- Export
- Import
- Database location
- Uninstall data
- Application preferences

## Design Requirements

- Settings must be reachable from the main navigation.
- Settings must be modular so new panels can be added without rewriting the whole settings screen.
- Database export must preserve the complete SQLite database as one file.
- Import must include validation and clear overwrite/merge behavior before implementation.
- Settings must include a destructive uninstall-data tool that deletes the DeskFolio application data directory, including the SQLite database and generated export directory.
- The uninstall-data tool must require explicit confirmation before deletion. The confirmation must clearly show the directory that will be removed.
- The uninstall-data action must not run while database work is in progress. The implementation must close active database resources before deleting files.
- After successful deletion, the app should either exit automatically with a clear message or require restart before further use.
- The uninstall-data tool must not delete files outside the DeskFolio application data directory.

## Uninstall Data Tool

Purpose: let a user remove local DeskFolio data before uninstalling the app.

Target directory:

`~/Library/Application Support/DeskFolio`

This includes:

- `deskfolio.sqlite`
- Flyway metadata inside the SQLite database
- default export directory `Exports`
- any future app-owned local files under the same directory

Required safeguards:

- Show the absolute path to be deleted.
- Require a deliberate confirmation step, not a single accidental click.
- Explain that this removes portfolio data from the local machine.
- Recommend exporting or backing up the database first.
- Log the operation without logging private portfolio contents.

## Initial Scope

Do not implement all settings in the first milestone. Prepare navigation and architecture so the module can be added cleanly.
