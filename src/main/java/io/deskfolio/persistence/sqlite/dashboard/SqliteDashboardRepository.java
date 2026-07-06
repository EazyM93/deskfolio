package io.deskfolio.persistence.sqlite.dashboard;

import io.deskfolio.domain.dashboard.AssetInvestmentSummary;
import io.deskfolio.domain.dashboard.MonthlyAssetInvestment;
import io.deskfolio.persistence.sqlite.SqliteConnectionFactory;
import io.deskfolio.persistence.sqlite.SqliteDataAccessException;
import io.deskfolio.repository.dashboard.DashboardRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class SqliteDashboardRepository implements DashboardRepository {

    private final SqliteConnectionFactory connectionFactory;

    public SqliteDashboardRepository(SqliteConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public List<AssetInvestmentSummary> assetInvestmentSummaries(long portfolioId) {
        String sql = """
                SELECT
                    a.id AS asset_id,
                    a.name AS asset_name,
                    a.ticker AS ticker,
                    at.name AS asset_type_name,
                    COALESCE(SUM(
                        CASE mt.code
                            WHEN 'SELL' THEN -pt.value_minor_units
                            ELSE pt.value_minor_units
                        END
                    ), 0) AS total_invested_minor_units,
                    COALESCE(c.code, 'EUR') AS currency_code
                FROM asset a
                JOIN asset_type at ON at.id = a.asset_type_id
                LEFT JOIN portfolio_transaction pt
                    ON pt.asset_id = a.id
                   AND pt.portfolio_id = ?
                LEFT JOIN movement_type mt ON mt.id = pt.movement_type_id
                LEFT JOIN currency c ON c.id = pt.currency_id
                GROUP BY a.id, a.name, a.ticker, at.name
                ORDER BY a.name
                """;

        try (Connection connection = connectionFactory.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, portfolioId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<AssetInvestmentSummary> summaries = new ArrayList<>();
                while (resultSet.next()) {
                    summaries.add(new AssetInvestmentSummary(
                            resultSet.getLong("asset_id"),
                            resultSet.getString("asset_name"),
                            optionalString(resultSet.getString("ticker")),
                            resultSet.getString("asset_type_name"),
                            resultSet.getLong("total_invested_minor_units"),
                            resultSet.getString("currency_code")
                    ));
                }
                return summaries;
            }
        } catch (SQLException exception) {
            throw new SqliteDataAccessException("Could not load dashboard investment summaries", exception);
        }
    }

    @Override
    public List<MonthlyAssetInvestment> monthlyAssetInvestments(long portfolioId) {
        String sql = """
                WITH monthly_movements AS (
                    SELECT
                        a.id AS asset_id,
                        a.name AS asset_name,
                        date(pt.transaction_date, 'start of month') AS month,
                        SUM(
                            CASE mt.code
                                WHEN 'SELL' THEN -pt.value_minor_units
                                ELSE pt.value_minor_units
                            END
                        ) AS monthly_delta_minor_units
                    FROM portfolio_transaction pt
                    JOIN asset a ON a.id = pt.asset_id
                    JOIN movement_type mt ON mt.id = pt.movement_type_id
                    WHERE pt.portfolio_id = ?
                    GROUP BY a.id, a.name, date(pt.transaction_date, 'start of month')
                ),
                asset_first_months AS (
                    SELECT
                        asset_id,
                        asset_name,
                        MIN(month) AS first_month
                    FROM monthly_movements
                    GROUP BY asset_id, asset_name
                ),
                baseline_movements AS (
                    SELECT
                        asset_id,
                        asset_name,
                        date(first_month, '-1 month') AS month,
                        0 AS monthly_delta_minor_units
                    FROM asset_first_months
                ),
                chart_movements AS (
                    SELECT asset_id, asset_name, month, monthly_delta_minor_units
                    FROM baseline_movements
                    UNION ALL
                    SELECT asset_id, asset_name, month, monthly_delta_minor_units
                    FROM monthly_movements
                )
                SELECT
                    asset_id,
                    asset_name,
                    month,
                    SUM(monthly_delta_minor_units) OVER (
                        PARTITION BY asset_id
                        ORDER BY month
                        ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
                    ) AS cumulative_invested_minor_units
                FROM chart_movements
                ORDER BY month, asset_name
                """;

        try (Connection connection = connectionFactory.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, portfolioId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<MonthlyAssetInvestment> investments = new ArrayList<>();
                while (resultSet.next()) {
                    investments.add(new MonthlyAssetInvestment(
                            resultSet.getLong("asset_id"),
                            resultSet.getString("asset_name"),
                            LocalDate.parse(resultSet.getString("month")),
                            resultSet.getLong("cumulative_invested_minor_units")
                    ));
                }
                return investments;
            }
        } catch (SQLException exception) {
            throw new SqliteDataAccessException("Could not load monthly asset investments", exception);
        }
    }

    private Optional<String> optionalString(String value) {
        return value == null || value.isBlank() ? Optional.empty() : Optional.of(value);
    }
}
