package io.deskfolio.ui.shell;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public final class PlaceholderViewFactory {

    private PlaceholderViewFactory() {
    }

    public static Node create(String title, String detail) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("placeholder-title");

        Label detailLabel = new Label(detail);
        detailLabel.getStyleClass().add("placeholder-detail");
        detailLabel.setWrapText(true);

        VBox container = new VBox(12, titleLabel, detailLabel);
        container.getStyleClass().add("placeholder-view");
        container.setAlignment(Pos.CENTER_LEFT);
        return container;
    }
}
