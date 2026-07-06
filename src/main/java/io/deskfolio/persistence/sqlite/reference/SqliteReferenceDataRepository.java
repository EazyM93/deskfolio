package io.deskfolio.persistence.sqlite.reference;

import io.deskfolio.persistence.sqlite.SqliteConnectionFactory;
import io.deskfolio.persistence.sqlite.SqliteDataAccessException;
import io.deskfolio.repository.reference.ReferenceDataRepository;
import io.deskfolio.repository.reference.ReferenceOption;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class SqliteReferenceDataRepository implements ReferenceDataRepository {

    private final SqliteConnectionFactory connectionFactory;

    public SqliteReferenceDataRepository(SqliteConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public List<ReferenceOption> assetTypes() {
        return loadOptions("asset_type");
    }

    @Override
    public List<ReferenceOption> movementTypes() {
        return loadOptions("movement_type");
    }

    @Override
    public List<ReferenceOption> transactionCategories() {
        return loadOptions("transaction_category");
    }

    @Override
    public List<ReferenceOption> currencies() {
        return loadOptions("currency");
    }

    private List<ReferenceOption> loadOptions(String tableName) {
        String sql = "SELECT id, code, name FROM " + tableName + " ORDER BY id";

        try (Connection connection = connectionFactory.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<ReferenceOption> options = new ArrayList<>();
            while (resultSet.next()) {
                options.add(new ReferenceOption(
                        resultSet.getLong("id"),
                        resultSet.getString("code"),
                        resultSet.getString("name")
                ));
            }
            return options;
        } catch (SQLException exception) {
            throw new SqliteDataAccessException("Could not load reference data from " + tableName, exception);
        }
    }
}
