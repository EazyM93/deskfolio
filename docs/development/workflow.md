# 📋 Development Workflow

DeskFolio uses documentation-first, spec-driven development.

## Feature Lifecycle

1. Define or update the feature specification.
2. Confirm architectural impact.
3. Add or update ADRs when decisions affect long-term structure.
4. Break work into vertical slices.
5. Implement one slice at a time.
6. Add tests at the correct level.
7. Update committed documentation if conventions changed.
8. Update local AI memory in `.ai/`.

## Definition of Ready

A task is `READY` when:

- Scope is clear.
- Dependencies are listed.
- Acceptance criteria are testable.
- Required specs or ADRs exist.
- The task can be implemented without guessing core architecture.

## Definition of Done

A task is `DONE` when:

- Implementation matches the relevant spec.
- Relevant tests pass.
- Documentation is updated.
- No unrelated refactors were introduced.
- `.ai/` project memory is updated.
- Task status is updated.

## Git Strategy

Use a simple solo-friendly trunk strategy:

- `main` remains stable.
- Use short-lived feature branches when work is non-trivial.
- Commit small coherent changes.
- Keep generated local AI files out of Git.

## Commit Convention

Use Conventional Commits:

- `docs: prepare architecture documentation`
- `feat: add transaction entry vertical slice`
- `fix: correct portfolio valuation calculation`
- `test: add repository migration tests`
- `refactor: simplify navigation registry`
- `chore: update build configuration`

## Code Review Checklist

- Does the change follow the layer boundaries?
- Are controllers thin?
- Are financial values represented safely?
- Are database migrations versioned?
- Are errors user-safe and logs developer-useful?
- Are tests appropriate to the risk?
- Is documentation updated when behavior or conventions changed?
