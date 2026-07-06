CREATE TABLE currency (
    id INTEGER PRIMARY KEY,
    code TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL,
    symbol TEXT
);

CREATE TABLE asset_type (
    id INTEGER PRIMARY KEY,
    code TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL
);

CREATE TABLE movement_type (
    id INTEGER PRIMARY KEY,
    code TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL
);

CREATE TABLE transaction_category (
    id INTEGER PRIMARY KEY,
    code TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL
);

CREATE TABLE portfolio (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    base_currency_id INTEGER NOT NULL,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (base_currency_id) REFERENCES currency (id)
);

INSERT INTO currency (id, code, name, symbol) VALUES
    (1, 'EUR', 'Euro', '€'),
    (2, 'USD', 'US Dollar', '$'),
    (3, 'GBP', 'British Pound', '£'),
    (4, 'CHF', 'Swiss Franc', 'CHF');

INSERT INTO asset_type (id, code, name) VALUES
    (1, 'ETF', 'ETF'),
    (2, 'STOCK', 'Stock'),
    (3, 'BOND', 'Bond'),
    (4, 'CRYPTO', 'Crypto'),
    (5, 'CASH', 'Cash');

INSERT INTO movement_type (id, code, name) VALUES
    (1, 'BUY', 'Buy'),
    (2, 'SELL', 'Sell'),
    (3, 'ACCUMULATE', 'Accumulate');

INSERT INTO transaction_category (id, code, name) VALUES
    (1, 'INVESTMENT', 'Investment'),
    (2, 'CASH_MOVEMENT', 'Cash movement'),
    (3, 'ADJUSTMENT', 'Adjustment');

INSERT INTO portfolio (id, name, base_currency_id) VALUES
    (1, 'Default Portfolio', 1);
