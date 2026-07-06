package io.deskfolio.persistence.sqlite.asset;

import io.deskfolio.domain.asset.Asset;
import io.deskfolio.dto.transaction.CreateAssetRequest;
import io.deskfolio.persistence.sqlite.SqliteConnectionFactory;
import io.deskfolio.persistence.sqlite.SqliteDataAccessException;
import io.deskfolio.repository.asset.AssetRepository;
import io.deskfolio.repository.reference.ReferenceOption;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class SqliteAssetRepository implements AssetRepository {

    private final SqliteConnectionFactory connectionFactory;

    public SqliteAssetRepository(SqliteConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public long create(Connection connection, CreateAssetRequest request, LocalDate creationDate) {
        String sql = """
                INSERT INTO asset (name, ticker, isin, asset_type_id, ter, creation_date)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, request.name().trim());
            statement.setString(2, request.ticker().orElse(null));
            statement.setString(3, request.isin().orElse(null));
            statement.setLong(4, request.assetTypeId());
            statement.setString(5, request.ter().map(BigDecimal::toPlainString).orElse(null));
            statement.setString(6, creationDate.toString());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
            throw new SqliteDataAccessException("Could not read generated asset id", null);
        } catch (SQLException exception) {
            throw new SqliteDataAccessException("Could not create asset", exception);
        }
    }

    @Override
    public List<Asset> findAll() {
        String sql = """
                SELECT id, name, ticker, isin, asset_type_id, ter, creation_date
                FROM asset
                ORDER BY name
                """;

        try (Connection connection = connectionFactory.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<Asset> assets = new ArrayList<>();
            while (resultSet.next()) {
                assets.add(mapAsset(resultSet));
            }
            return assets;
        } catch (SQLException exception) {
            throw new SqliteDataAccessException("Could not load assets", exception);
        }
    }

    @Override
    public List<ReferenceOption> findAssetOptions() {
        String sql = """
                SELECT id, COALESCE(ticker, isin, CAST(id AS TEXT)) AS code, name
                FROM asset
                ORDER BY name
                """;

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
            throw new SqliteDataAccessException("Could not load asset options", exception);
        }
    }

    @Override
    public Optional<Asset> findById(Connection connection, long id) {
        String sql = """
                SELECT id, name, ticker, isin, asset_type_id, ter, creation_date
                FROM asset
                WHERE id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapAsset(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new SqliteDataAccessException("Could not load asset", exception);
        }
    }

    private Asset mapAsset(ResultSet resultSet) throws SQLException {
        return new Asset(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                optionalString(resultSet.getString("ticker")),
                optionalString(resultSet.getString("isin")),
                resultSet.getLong("asset_type_id"),
                optionalString(resultSet.getString("ter")).map(BigDecimal::new),
                LocalDate.parse(resultSet.getString("creation_date"))
        );
    }

    private Optional<String> optionalString(String value) {
        return value == null || value.isBlank() ? Optional.empty() : Optional.of(value);
    }
}
