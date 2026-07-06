package io.deskfolio.persistence.sqlite;

import java.nio.file.Path;
import java.util.Objects;

public record DatabaseConfig(Path databaseFile) {

    public DatabaseConfig {
        Objects.requireNonNull(databaseFile, "databaseFile");
    }

    public static DatabaseConfig forFile(Path databaseFile) {
        return new DatabaseConfig(databaseFile);
    }

    public String jdbcUrl() {
        return "jdbc:sqlite:" + databaseFile.toAbsolutePath();
    }
}
