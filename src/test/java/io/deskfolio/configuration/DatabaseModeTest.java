package io.deskfolio.configuration;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DatabaseModeTest {

    @Test
    void parsesManagedMode() {
        assertThat(DatabaseMode.from("managed")).isEqualTo(DatabaseMode.MANAGED);
        assertThat(DatabaseMode.from("")).isEqualTo(DatabaseMode.MANAGED);
    }

    @Test
    void parsesLocalResetMode() {
        assertThat(DatabaseMode.from("local-reset")).isEqualTo(DatabaseMode.LOCAL_RESET);
        assertThat(DatabaseMode.from("LOCAL_RESET")).isEqualTo(DatabaseMode.LOCAL_RESET);
    }

    @Test
    void rejectsUnsupportedMode() {
        assertThatThrownBy(() -> DatabaseMode.from("drop-everything"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported database mode");
    }
}
