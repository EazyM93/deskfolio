package io.deskfolio;

import io.deskfolio.ui.shell.ShellController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class DeskFolioApplication extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeskFolioApplication.class);

    @Override
    public void start(Stage stage) throws IOException {
        LOGGER.info("Starting DeskFolio application shell");

        FXMLLoader loader = new FXMLLoader(ShellController.class.getResource("shell.fxml"));
        Scene scene = new Scene(loader.load(), 1200, 760);
        scene.getStylesheets().add(Objects.requireNonNull(
                DeskFolioApplication.class.getResource("/io/deskfolio/theme/dark-theme.css")
        ).toExternalForm());

        stage.setTitle("DeskFolio");
        stage.setMinWidth(960);
        stage.setMinHeight(640);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
