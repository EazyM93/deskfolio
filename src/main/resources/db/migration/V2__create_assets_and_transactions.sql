CREATE TABLE asset (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    ticker TEXT,
    isin TEXT UNIQUE,
    asset_type_id INTEGER NOT NULL,
    ter TEXT,
    creation_date TEXT NOT NULL,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (asset_type_id) REFERENCES asset_type (id)
);

CREATE INDEX idx_asset_ticker ON asset (ticker);

CREATE TABLE portfolio_transaction (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    portfolio_id INTEGER NOT NULL,
    transaction_date TEXT NOT NULL,
    asset_id INTEGER NOT NULL,
    movement_type_id INTEGER NOT NULL,
    category_id INTEGER NOT NULL,
    value_minor_units INTEGER NOT NULL,
    currency_id INTEGER NOT NULL,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (portfolio_id) REFERENCES portfolio (id),
    FOREIGN KEY (asset_id) REFERENCES asset (id),
    FOREIGN KEY (movement_type_id) REFERENCES movement_type (id),
    FOREIGN KEY (category_id) REFERENCES transaction_category (id),
    FOREIGN KEY (currency_id) REFERENCES currency (id)
);

CREATE INDEX idx_portfolio_transaction_asset_id ON portfolio_transaction (asset_id);
CREATE INDEX idx_portfolio_transaction_date ON portfolio_transaction (transaction_date);
