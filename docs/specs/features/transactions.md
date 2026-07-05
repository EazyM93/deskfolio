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
- ETF checkbox

When ETF is enabled, show:

- TER percentage field

## Business Rules

- Date is required.
- Value is required and must be a valid financial amount.
- Existing asset mode requires an asset.
- New asset mode requires asset name and asset type.
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
