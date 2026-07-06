package io.deskfolio.persistence.sqlite;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public final class FlywayMigrationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlywayMigrationRunner.class);
    private static final String MIGRATION_LOCATION = "classpath:db/migration";

    private final DatabaseConfig databaseConfig;

    public FlywayMigrationRunner(DatabaseConfig databaseConfig) {
        this.databaseConfig = Objects.requireNonNull(databaseConfig, "databaseConfig");
    }

    public MigrateResult migrate() {
        createDatabaseDirectory();

        Flyway flyway = flyway(true);

        MigrateResult result = flyway.migrate();
        LOGGER.info("Database migrated to version {} using {}", result.targetSchemaVersion, databaseConfig.jdbcUrl());
        return result;
    }

    public MigrateResult cleanAndMigrate() {
        createDatabaseDirectory();

        Flyway flyway = flyway(false);
        LOGGER.warn("Cleaning local development database before migration: {}", databaseConfig.jdbcUrl());
        flyway.clean();
        return migrate();
    }

    private Flyway flyway(boolean cleanDisabled) {
        return Flyway.configure()
                .dataSource(databaseConfig.jdbcUrl(), null, null)
                .locations(MIGRATION_LOCATION)
                .cleanDisabled(cleanDisabled)
                .load();
    }

    private void createDatabaseDirectory() {
        try {
            Files.createDirectories(databaseConfig.databaseFile().toAbsolutePath().getParent());
        } catch (IOException exception) {
            throw new IllegalStateException("Could not create database directory", exception);
        }
    }
}
