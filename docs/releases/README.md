# 📦 Release Notes

This directory contains detailed release notes for each DeskFolio version.

Use `CHANGELOG.md` for a short cross-version summary. Use one file in this directory for the detailed notes of a specific version.

## Policy

Release notes are permanent project documentation, but they are not task memory.

Codex must update release notes only when the user explicitly asks for changelog or release note updates. They must not be modified automatically after normal feature work.

## Naming Convention

Use semantic version filenames:

```text
docs/releases/0.1.0.md
docs/releases/0.2.0.md
docs/releases/1.0.0.md
```

Pre-release notes may use:

```text
docs/releases/0.2.0-beta.1.md
```

## Recommended Structure

Each release note should include:

- Version
- Release date or planned status
- Summary
- ✨ New Features
- 🐛 Bug Fixes
- 🛠 Improvements
- 🔒 Security
- ⚡ Performance
- 🗄 Database
- 🎨 UI
- 🧪 Testing
- 📚 Documentation
- 📦 Packaging
- ⚙ Configuration
- ❌ Removed
- 🔁 Migration Notes
- ⚠ Known Issues

Use only the categories that are relevant for a specific release.

The canonical emoji list lives in `docs/development/emoji-conventions.md`.
