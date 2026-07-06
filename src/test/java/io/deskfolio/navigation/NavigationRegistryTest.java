package io.deskfolio.navigation;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NavigationRegistryTest {

    @Test
    void defaultRegistryContainsApplicationShellRoutes() {
        NavigationRegistry registry = NavigationRegistry.defaultRegistry();

        assertThat(registry.items())
                .extracting(NavigationItem::id)
                .containsExactly("dashboard", "transactions", "assets", "settings");
        assertThat(registry.defaultItem().id()).isEqualTo("dashboard");
    }

    @Test
    void registryRequiresAtLeastOneNavigationItem() {
        assertThatThrownBy(() -> new NavigationRegistry(List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least one item");
    }
}
