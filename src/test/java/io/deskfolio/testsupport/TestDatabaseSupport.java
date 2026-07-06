package io.deskfolio.testsupport;

import io.deskfolio.persistence.sqlite.DatabaseConfig;
import io.deskfolio.persistence.sqlite.FlywayMigrationRunner;
import io.deskfolio.persistence.sqlite.SqliteConnectionFactory;

import java.nio.file.Path;

public final class TestDatabaseSupport {

    private TestDatabaseSupport() {
    }

    public static SqliteConnectionFactory migratedConnectionFactory(Path temporaryDirectory) {
        DatabaseConfig databaseConfig = DatabaseConfig.forFile(temporaryDirectory.resolve("deskfolio-test.sqlite"));
        new FlywayMigrationRunner(databaseConfig).migrate();
        return new SqliteConnectionFactory(databaseConfig);
    }
}
