# 🚀 Feature Specification: Dashboard

## Purpose

The dashboard is the home screen. It shows the user's invested situation derived from persisted transactions.

## Current Source of Truth

Dashboard values are derived from `portfolio_transaction`.

- `BUY` increases invested value.
- `ACCUMULATE` increases invested value.
- `SELL` decreases invested value.

Manual monthly valuation entry is not part of the current implementation. Do not reintroduce:

- monthly valuation input forms
- `asset_monthly_valuation` persistence
- dashboard totals derived from manually typed monthly values
- charts based on manually entered monthly rows

## MVP Content

- Total invested value across tracked assets.
- Number of tracked assets.
- Main line chart with one cumulative invested-value series per asset.
- Asset cards showing each asset's total invested value.
- Empty state when there is no invested value yet.
- Chart tooltips are not part of the current implementation.

## Chart Specification

X axis:

- One point per transaction month for each asset.
- Each asset series starts at `0` in the month before the asset's first transaction.
- Month ordering uses month and year, so the same month name in different years remains a separate point in chronological order.
- Month labels are generated from persisted transaction dates.

Y axis:

- Cumulative invested value.
- Values are derived from transaction amounts, with sells subtracting from the cumulative total.

## Asset Cards

Each card represents one asset and displays:

- Asset name
- Ticker when available
- Asset type
- Total invested value

## Acceptance Criteria

- Dashboard can load with no data.
- Dashboard can load with one asset.
- Dashboard can load with multiple assets.
- Chart month labels are generated from transaction data.
- Asset cards show total invested value per asset.
- No dashboard calculations depend on hardcoded asset type names.
