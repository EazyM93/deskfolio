package io.deskfolio.ui.shell;

import io.deskfolio.navigation.NavigationItem;
import io.deskfolio.navigation.NavigationRegistry;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ShellController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShellController.class);
    private static final double SIDEBAR_WIDTH = 240;
    private static final Duration SIDEBAR_ANIMATION_DURATION = Duration.millis(180);

    private final NavigationRegistry navigationRegistry;
    private final Map<String, Button> navigationButtons = new LinkedHashMap<>();
    private boolean sidebarVisible = true;
    private Timeline sidebarAnimation;

    @FXML
    private VBox sidebar;

    @FXML
    private VBox navigationContainer;

    @FXML
    private StackPane contentRegion;

    @FXML
    private Label pageTitle;

    @FXML
    private Button sidebarToggleButton;

    public ShellController() {
        this(NavigationRegistry.defaultRegistry());
    }

    public ShellController(NavigationRegistry navigationRegistry) {
        this.navigationRegistry = navigationRegistry;
    }

    @FXML
    private void initialize() {
        sidebar.setMinWidth(SIDEBAR_WIDTH);
        sidebar.setPrefWidth(SIDEBAR_WIDTH);
        sidebar.setMaxWidth(SIDEBAR_WIDTH);
        navigationRegistry.items().forEach(this::addNavigationButton);
        navigateTo(navigationRegistry.defaultItem());
    }

    private void addNavigationButton(NavigationItem item) {
        Button button = new Button(item.label());
        button.getStyleClass().add("navigation-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setMnemonicParsing(false);
        button.setOnAction(event -> navigateTo(item));

        navigationButtons.put(item.id(), button);
        navigationContainer.getChildren().add(button);
    }

    private void navigateTo(NavigationItem item) {
        LOGGER.debug("Navigating to {}", item.id());

        pageTitle.setText(item.label());
        navigationButtons.forEach((id, button) -> button.getStyleClass().remove("selected"));
        Button selectedButton = navigationButtons.get(item.id());
        if (selectedButton != null) {
            selectedButton.getStyleClass().add("selected");
        }

        contentRegion.getChildren().setAll(item.viewFactory().get());
    }

    @FXML
    private void toggleSidebar() {
        if (sidebarAnimation != null && sidebarAnimation.getStatus() == Animation.Status.RUNNING) {
            return;
        }

        sidebarVisible = !sidebarVisible;
        sidebarToggleButton.setText(sidebarVisible ? "‹" : "☰");
        sidebarToggleButton.setAccessibleText(sidebarVisible ? "Hide sidebar" : "Show sidebar");

        double targetWidth = sidebarVisible ? SIDEBAR_WIDTH : 0;
        double targetTranslation = sidebarVisible ? 0 : -SIDEBAR_WIDTH;
        double targetOpacity = sidebarVisible ? 1 : 0;

        sidebar.setManaged(true);
        sidebarToggleButton.setDisable(true);

        sidebarAnimation = new Timeline(new KeyFrame(
                SIDEBAR_ANIMATION_DURATION,
                new KeyValue(sidebar.minWidthProperty(), targetWidth),
                new KeyValue(sidebar.prefWidthProperty(), targetWidth),
                new KeyValue(sidebar.maxWidthProperty(), targetWidth),
                new KeyValue(sidebar.translateXProperty(), targetTranslation),
                new KeyValue(sidebar.opacityProperty(), targetOpacity)
        ));
        sidebarAnimation.setOnFinished(event -> {
            sidebar.setManaged(sidebarVisible);
            sidebarToggleButton.setDisable(false);
        });
        sidebarAnimation.play();

        LOGGER.debug("Sidebar visibility changed to {}", sidebarVisible);
    }
}
