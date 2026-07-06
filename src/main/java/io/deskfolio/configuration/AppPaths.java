package io.deskfolio.configuration;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;

public record AppPaths(Path applicationDataDirectory, Path databaseFile, Path defaultExportDirectory) {

    private static final String APPLICATION_DIRECTORY_NAME = "DeskFolio";
    private static final String DATABASE_FILE_NAME = "deskfolio.sqlite";
    private static final String EXPORT_DIRECTORY_NAME = "Exports";

    public AppPaths {
        Objects.requireNonNull(applicationDataDirectory, "applicationDataDirectory");
        Objects.requireNonNull(databaseFile, "databaseFile");
        Objects.requireNonNull(defaultExportDirectory, "defaultExportDirectory");
    }

    public static AppPaths defaultPaths() {
        Path applicationDataDirectory = defaultApplicationDataDirectory();
        return new AppPaths(
                applicationDataDirectory,
                applicationDataDirectory.resolve(DATABASE_FILE_NAME),
                applicationDataDirectory.resolve(EXPORT_DIRECTORY_NAME)
        );
    }

    private static Path defaultApplicationDataDirectory() {
        String userHome = System.getProperty("user.home");
        String operatingSystem = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);

        if (operatingSystem.contains("mac")) {
            return Path.of(userHome, "Library", "Application Support", APPLICATION_DIRECTORY_NAME);
        }

        return Path.of(userHome, "." + APPLICATION_DIRECTORY_NAME.toLowerCase(Locale.ROOT));
    }
}
