package io.deskfolio.navigation;

import io.deskfolio.ui.shell.PlaceholderViewFactory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class NavigationRegistry {

    private final List<NavigationItem> items;

    public NavigationRegistry(List<NavigationItem> items) {
        Objects.requireNonNull(items, "items");
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Navigation registry requires at least one item");
        }
        this.items = List.copyOf(items);
    }

    public static NavigationRegistry defaultRegistry() {
        return new NavigationRegistry(List.of(
                new NavigationItem("dashboard", "Dashboard", "dashboard",
                        () -> PlaceholderViewFactory.create("Dashboard", "Portfolio overview will land in Milestone 4.")),
                new NavigationItem("transactions", "Transactions", "transactions",
                        () -> PlaceholderViewFactory.create("Transactions", "Transaction entry starts in Milestone 3.")),
                new NavigationItem("assets", "Assets", "assets",
                        () -> PlaceholderViewFactory.create("Assets", "Asset management starts in Milestone 3.")),
                new NavigationItem("settings", "Settings", "settings",
                        () -> PlaceholderViewFactory.create("Settings", "Settings foundation starts in Milestone 6."))
        ));
    }

    public List<NavigationItem> items() {
        return items;
    }

    public NavigationItem defaultItem() {
        return items.getFirst();
    }

    public Optional<NavigationItem> findById(String id) {
        return items.stream()
                .filter(item -> item.id().equals(id))
                .findFirst();
    }
}
