# 🚀 Feature Specification: Dashboard

## Purpose

The dashboard is the home screen. It gives a quick overview of portfolio status and historical manually entered valuations.

## MVP Content

- Portfolio total value
- Portfolio allocation
- Performance summary
- Main performance chart
- Asset cards with latest manually entered value

## Chart Specification

X axis:

- One point per month.
- Months grow automatically as time passes and new valuation data is added.
- The axis should not require code changes as the application ages.

Y axis:

- Total portfolio value.
- Derived from manually entered monthly values.

## Asset Cards

Each card represents one asset and displays:

- Asset name
- Ticker when available
- Asset type
- Latest manually entered value
- Latest valuation month

## Empty State

When no data exists, show a useful empty dashboard with a clear action to add the first transaction or first valuation.

## Acceptance Criteria

- Dashboard can load with no data.
- Dashboard can load with one asset.
- Dashboard can load with multiple assets.
- Chart month labels are generated from available valuation data.
- No portfolio calculations depend on hardcoded asset types.
