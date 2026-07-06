package io.deskfolio.navigation;

import javafx.scene.Node;

import java.util.Objects;
import java.util.function.Supplier;

public record NavigationItem(
        String id,
        String label,
        String iconKey,
        Supplier<Node> viewFactory
) {

    public NavigationItem {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(label, "label");
        Objects.requireNonNull(iconKey, "iconKey");
        Objects.requireNonNull(viewFactory, "viewFactory");

        if (id.isBlank()) {
            throw new IllegalArgumentException("Navigation item id must not be blank");
        }
        if (label.isBlank()) {
            throw new IllegalArgumentException("Navigation item label must not be blank");
        }
    }
}
