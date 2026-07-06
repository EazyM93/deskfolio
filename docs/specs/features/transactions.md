# 🚀 Feature Specification: Transactions

## Purpose

The transactions page allows the user to add portfolio movements and optionally create a new asset during entry.

## Form Fields

Base fields:

- Date
- Asset select
- Asset Type select
- Category select
- Movement Type select
- Value

Switch:

- `Create new asset`

When enabled, show:

- Asset name
- Ticker
- ISIN
- Asset Type select
- ETF checkbox

When ETF is enabled, show:

- TER percentage field

When ETF is enabled, the Asset Type select must be disabled and ETF is used as the asset type. ETF must not appear as a selectable option inside the Asset Type list.

## Business Rules

- Date is required.
- Value is required and must be a valid financial amount.
- Value accepts plain decimal input such as `100`, `100.50` or `100,50`.
- Existing asset mode requires an asset.
- New asset mode requires asset name and asset type.
- New asset mode uses the ETF checkbox for ETF assets; otherwise the user selects a non-ETF asset type.
- ISIN should be normalized consistently before persistence.
- TER is only applicable when the selected or created asset type is ETF.
- New asset creation must automatically set `creation_date`.

## Vertical Slice Boundary

The first implementation should persist both:

- The new asset when `Create new asset` is enabled.
- The transaction referencing that asset.

This must be atomic. If transaction creation fails, the asset should not be partially created unless the design explicitly supports draft assets.

## Acceptance Criteria

- User can add a transaction for an existing asset.
- User can create an ETF asset while adding a transaction.
- User can create a stock asset while adding a transaction.
- Validation errors are visible and specific.
- Persistence is transactional.

## Implementation Notes

Milestone 3 implements the first vertical slice with:

- `asset` and `portfolio_transaction` tables in Flyway migration `V2__create_assets_and_transactions.sql`.
- SQLite repositories for assets, reference data and transactions.
- `TransactionService` as the transactional boundary for create-asset-and-transaction workflows.
- JavaFX Transactions view loaded from the registry-based navigation shell.
- Monetary values persisted as integer minor units in `portfolio_transaction.value_minor_units`.
- The new-asset Asset Type list excludes ETF; ETF is controlled only by the ETF checkbox.
- Supported MVP movement types are `Buy`, `Sell` and `Accumulate`.
