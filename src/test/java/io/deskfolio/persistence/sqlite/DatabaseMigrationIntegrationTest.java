package io.deskfolio.persistence.sqlite;

import io.deskfolio.testsupport.TestDatabaseSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseMigrationIntegrationTest {

    @TempDir
    private Path temporaryDirectory;

    @Test
    void migrationCreatesReferenceTablesAndDefaultPortfolio() throws Exception {
        SqliteConnectionFactory connectionFactory = TestDatabaseSupport.migratedConnectionFactory(temporaryDirectory);

        try (Connection connection = connectionFactory.openConnection();
             Statement statement = connection.createStatement()) {
            assertThat(countRows(statement, "currency")).isEqualTo(4);
            assertThat(countRows(statement, "asset_type")).isEqualTo(5);
            assertThat(countRows(statement, "movement_type")).isEqualTo(3);
            assertThat(csv(statement, "SELECT code || ':' || name FROM movement_type ORDER BY id"))
                    .isEqualTo("BUY:Buy,SELL:Sell,ACCUMULATE:Accumulate");
            assertThat(countRows(statement, "transaction_category")).isEqualTo(3);
            assertThat(singleText(statement, "SELECT name FROM portfolio WHERE id = 1"))
                    .isEqualTo("Default Portfolio");
            assertThat(tableExists(statement, "asset")).isTrue();
            assertThat(tableExists(statement, "portfolio_transaction")).isTrue();
        }
    }

    @Test
    void sqliteConnectionsEnableForeignKeys() throws Exception {
        SqliteConnectionFactory connectionFactory = TestDatabaseSupport.migratedConnectionFactory(temporaryDirectory);

        try (Connection connection = connectionFactory.openConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("PRAGMA foreign_keys")) {
            assertThat(resultSet.next()).isTrue();
            assertThat(resultSet.getInt(1)).isEqualTo(1);
        }
    }

    @Test
    void cleanAndMigrateDropsExistingLocalData() throws Exception {
        DatabaseConfig databaseConfig = DatabaseConfig.forFile(temporaryDirectory.resolve("deskfolio-reset-test.sqlite"));
        FlywayMigrationRunner migrationRunner = new FlywayMigrationRunner(databaseConfig);
        migrationRunner.migrate();

        SqliteConnectionFactory connectionFactory = new SqliteConnectionFactory(databaseConfig);
        try (Connection connection = connectionFactory.openConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    INSERT INTO asset (name, asset_type_id, creation_date)
                    VALUES ('Temporary Asset', 2, '2026-07-06')
                    """);
            assertThat(countRows(statement, "asset")).isEqualTo(1);
        }

        migrationRunner.cleanAndMigrate();

        try (Connection connection = connectionFactory.openConnection();
             Statement statement = connection.createStatement()) {
            assertThat(countRows(statement, "asset")).isZero();
            assertThat(countRows(statement, "currency")).isEqualTo(4);
        }
    }

    private int countRows(Statement statement, String tableName) throws Exception {
        try (ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
            assertThat(resultSet.next()).isTrue();
            return resultSet.getInt(1);
        }
    }

    private String singleText(Statement statement, String query) throws Exception {
        try (ResultSet resultSet = statement.executeQuery(query)) {
            assertThat(resultSet.next()).isTrue();
            return resultSet.getString(1);
        }
    }

    private String csv(Statement statement, String query) throws Exception {
        StringBuilder result = new StringBuilder();
        try (ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                if (!result.isEmpty()) {
                    result.append(",");
                }
                result.append(resultSet.getString(1));
            }
        }
        return result.toString();
    }

    private boolean tableExists(Statement statement, String tableName) throws Exception {
        try (ResultSet resultSet = statement.executeQuery("""
                SELECT 1
                FROM sqlite_master
                WHERE type = 'table'
                  AND name = '%s'
                """.formatted(tableName))) {
            return resultSet.next();
        }
    }
}
