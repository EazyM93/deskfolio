# 🧠 AI-Assisted Development

DeskFolio is designed for long-term development with Codex while keeping Git history clean.

## Permanent vs Local Context

Permanent context:

- `CODEX.md`
- `CLAUDE.md` for developers who use Claude
- `README.md`
- `docs/`
- `pom.xml`

Local AI context:

- `.ai/`
- `.codex/`
- `.claude/`

Local AI context is ignored by Git and should not be committed.

## Release Documentation Rule

`CHANGELOG.md` and `docs/releases/` are committed project documentation, but they are not AI memory and not routine task logs.

Codex must update them only when the user explicitly requests changelog or release note maintenance.

## Codex Startup Routine

At the start of every implementation session, Codex should read:

1. `CODEX.md`
2. `.ai/project-status.md`
3. `.ai/current-task.md`
4. `.ai/backlog.md`
5. Relevant committed specs under `docs/`

If the user says "Continue working" or "Riprendi il lavoro", Codex should use these files to resume without asking for manual context unless the task is blocked or contradictory.

## Codex Completion Routine

After meaningful work, Codex must update:

- `.ai/project-status.md`
- `.ai/current-task.md`
- `.ai/backlog.md`
- `.ai/session-summary.md`
- `.ai/known-issues.md` when bugs or debt are discovered
- `CODEX.md` only when long-term conventions change
- `CLAUDE.md` only when Claude compatibility guidance changes
- `docs/` only when architecture, specs or workflow change

Do not update `CHANGELOG.md` or `docs/releases/` during this routine unless the user explicitly requested release documentation changes.

## CAVEMAN Workflow

Use CAVEMAN as a lightweight implementation loop:

- Context: read project memory and relevant specs.
- Analyze: identify the smallest safe vertical slice.
- Validate: confirm dependencies, acceptance criteria and risks.
- Execute: implement only the scoped task.
- Measure: run relevant tests and checks.
- Archive: update `.ai/` memory and committed docs if needed.
- Next: set the next recommended task.

## Memory Hygiene

Do not duplicate entire specs inside `.ai/`. Link to committed docs instead.

Do not store secrets, credentials or personal financial data in AI memory.

Do not let `.ai/` become a second README. It should describe state, progress, blockers and next actions.
