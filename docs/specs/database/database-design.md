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

Implemented migrations:

- `V1__create_reference_tables.sql`
- `V2__create_assets_and_transactions.sql`

Foreign keys must be enabled on each SQLite connection with `PRAGMA foreign_keys = ON`.

## Local Database Path and Export

The default runtime database file is:

`~/Library/Application Support/DeskFolio/deskfolio.sqlite`

The application creates the parent directory before running migrations. The database is intentionally a single SQLite file so Settings can later export it by copying that file to a user-selected destination. The default export directory is:

`~/Library/Application Support/DeskFolio/Exports`

Import behavior is intentionally not implemented in the foundation milestone and must be designed before code is added.

Settings must later provide an uninstall-data action for users who want to remove DeskFolio completely. That action should delete the whole DeskFolio application data directory:

`~/Library/Application Support/DeskFolio`

This is a destructive user-triggered operation, separate from normal database migrations and separate from the development-only `local-reset` run mode. It must require explicit confirmation, close active database resources before deletion, and never delete files outside the DeskFolio application data directory.

## Runtime Database Modes

The default runtime mode is `managed`: Flyway migrates the existing database and preserves user data.

For local development only, the Maven profile `local-reset` starts the app with `-Ddeskfolio.database.mode=local-reset`, which runs Flyway `clean` before `migrate`. This is intended for disposable testing data and must not be used for packaged builds.

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

Implemented in migration `V2__create_assets_and_transactions.sql`.

### movement_type

Reference table for the supported MVP movement types: Buy, Sell and Accumulate.

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
| asset_id | required for asset trades in MVP |
| movement_type_id | references movement_type |
| category_id | references transaction_category |
| value_minor_units | integer minor units using the selected currency |
| currency_id | references currency |
| created_at | technical timestamp |
| updated_at | technical timestamp |

Implemented in migration `V2__create_assets_and_transactions.sql`.

The current implementation stores monetary values as `value_minor_units` using the selected currency. UI decimal input is converted to integer minor units before persistence.

## Future Tables

Do not implement these until needed, but preserve room for them:

- `asset_valuation` if future product design needs market or user-provided valuations
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
