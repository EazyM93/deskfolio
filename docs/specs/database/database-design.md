# 🗄 Database Design

SQLite is mandatory because DeskFolio must support exporting the complete application data as a single file.

## Design Goals

- Support the current MVP without overengineering.
- Keep room for dividends, taxes, fees, multiple portfolios, multiple currencies and historical prices.
- Preserve data integrity through foreign keys and constraints.
- Avoid floating point values for money.
- Keep reference data normalized.

## Migration Strategy

Use Flyway SQL migrations under:

`src/main/resources/db/migration/`

Naming convention:

`V<major>__<description>.sql`

Examples for future implementation:

- `V1__create_reference_tables.sql`
- `V2__create_assets.sql`
- `V3__create_transactions.sql`

Foreign keys must be enabled on each SQLite connection with `PRAGMA foreign_keys = ON`.

## Conceptual Schema

### portfolio

Prepares for multiple portfolios while allowing MVP to use a single default portfolio.

| Field | Notes |
|---|---|
| id | primary key |
| name | required |
| base_currency_id | references currency |
| created_at | technical timestamp |
| updated_at | technical timestamp |

### currency

Supports future multi-currency transactions and reporting.

| Field | Notes |
|---|---|
| id | primary key |
| code | ISO 4217 code, unique |
| name | display name |
| symbol | optional |

### asset_type

Reference table for ETF, stock and future asset types.

| Field | Notes |
|---|---|
| id | primary key |
| code | stable unique code |
| name | display name |

### asset

Represents a financial instrument.

| Field | Notes |
|---|---|
| id | primary key |
| name | required |
| ticker | optional but indexed |
| isin | optional, unique when present |
| asset_type_id | references asset_type |
| ter | optional fixed-scale decimal text, mainly for ETFs |
| creation_date | user/business date, set automatically when created from the app |
| created_at | technical timestamp |
| updated_at | technical timestamp |

### movement_type

Reference table for buy, sell, deposit, withdrawal and future movement types.

| Field | Notes |
|---|---|
| id | primary key |
| code | stable unique code |
| name | display name |

### transaction_category

The requested form includes Category. Keep it separate from movement type so categories can evolve.

| Field | Notes |
|---|---|
| id | primary key |
| code | stable unique code |
| name | display name |

### portfolio_transaction

Stores user-entered movements.

| Field | Notes |
|---|---|
| id | primary key |
| portfolio_id | references portfolio |
| transaction_date | local date |
| asset_id | optional for cash-only future movements, required for asset trades in MVP |
| movement_type_id | references movement_type |
| category_id | references transaction_category |
| value_amount | fixed-scale decimal text or integer minor units |
| currency_id | references currency |
| created_at | technical timestamp |
| updated_at | technical timestamp |

### asset_monthly_valuation

Stores manually entered historical values used by the dashboard chart and asset cards.

| Field | Notes |
|---|---|
| id | primary key |
| portfolio_id | references portfolio |
| asset_id | references asset |
| valuation_month | month identifier, e.g. first day of month |
| value_amount | fixed-scale decimal text or integer minor units |
| currency_id | references currency |
| created_at | technical timestamp |
| updated_at | technical timestamp |

Unique constraint: `(portfolio_id, asset_id, valuation_month)`.

## Future Tables

Do not implement these until needed, but preserve room for them:

- `dividend`
- `tax_event`
- `fee`
- `price_history`
- `exchange_rate`
- `import_batch`
- `backup_metadata`

## Money and Percentages

Use one explicit representation across the app:

- Preferred: integer minor units plus currency scale where the value is monetary.
- Acceptable for percentages: fixed-scale decimal text parsed into `BigDecimal`.

Never store financial values as floating point.
