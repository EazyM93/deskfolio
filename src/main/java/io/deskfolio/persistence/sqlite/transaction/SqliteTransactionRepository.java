package io.deskfolio.persistence.sqlite.transaction;

import io.deskfolio.persistence.sqlite.SqliteDataAccessException;
import io.deskfolio.repository.transaction.TransactionRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public final class SqliteTransactionRepository implements TransactionRepository {

    @Override
    public long create(
            Connection connection,
            long portfolioId,
            LocalDate transactionDate,
            long assetId,
            long movementTypeId,
            long categoryId,
            long valueMinorUnits,
            long currencyId
    ) {
        String sql = """
                INSERT INTO portfolio_transaction (
                    portfolio_id,
                    transaction_date,
                    asset_id,
                    movement_type_id,
                    category_id,
                    value_minor_units,
                    currency_id
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, portfolioId);
            statement.setString(2, transactionDate.toString());
            statement.setLong(3, assetId);
            statement.setLong(4, movementTypeId);
            statement.setLong(5, categoryId);
            statement.setLong(6, valueMinorUnits);
            statement.setLong(7, currencyId);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
            throw new SqliteDataAccessException("Could not read generated transaction id", null);
        } catch (SQLException exception) {
            throw new SqliteDataAccessException("Could not create transaction", exception);
        }
    }
}
