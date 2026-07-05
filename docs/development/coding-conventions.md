# 📝 Coding Conventions

No application code exists yet. These conventions apply when implementation begins.

## Java

- Prefer explicit names over abbreviations.
- Keep classes focused on one responsibility.
- Prefer constructor injection.
- Avoid static mutable state.
- Use immutable value objects where practical.
- Use `BigDecimal` or integer minor units for financial values, never floating point.
- Keep JavaFX types out of domain and service layers.

## Naming

- Services: `AssetService`, `TransactionService`
- Repository contracts: `AssetRepository`
- SQLite implementations: `SqliteAssetRepository`
- Controllers: `DashboardController`
- DTOs: `CreateTransactionRequest`, `AssetSummaryDto`
- Validators: `TransactionValidator`

## Tests

- Unit test class name: `<ClassName>Test`
- Integration test class name: `<ClassName>IntegrationTest`
- Test method names should describe behavior.

## Documentation

Every non-obvious architectural decision belongs in an ADR.

Feature behavior belongs in `docs/specs/features/`.

Temporary progress belongs only in `.ai/`.
