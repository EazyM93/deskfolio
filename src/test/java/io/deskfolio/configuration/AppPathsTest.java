package io.deskfolio.configuration;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class AppPathsTest {

    @Test
    void defaultPathsUseSingleSqliteFileInApplicationDataDirectory() {
        AppPaths appPaths = AppPaths.defaultPaths();

        assertThat(appPaths.applicationDataDirectory()).isAbsolute();
        assertThat(appPaths.databaseFile().getFileName()).isEqualTo(Path.of("deskfolio.sqlite"));
        assertThat(appPaths.databaseFile().getParent()).isEqualTo(appPaths.applicationDataDirectory());
        assertThat(appPaths.defaultExportDirectory().getParent()).isEqualTo(appPaths.applicationDataDirectory());
    }
}
