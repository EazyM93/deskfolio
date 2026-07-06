package io.deskfolio.persistence.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public final class SqliteConnectionFactory {

    private final DatabaseConfig databaseConfig;

    public SqliteConnectionFactory(DatabaseConfig databaseConfig) {
        this.databaseConfig = Objects.requireNonNull(databaseConfig, "databaseConfig");
    }

    public Connection openConnection() {
        try {
            Connection connection = DriverManager.getConnection(databaseConfig.jdbcUrl());
            enableForeignKeys(connection);
            return connection;
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not open SQLite connection", exception);
        }
    }

    private void enableForeignKeys(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
    }
}
