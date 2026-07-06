package io.deskfolio.ui.dashboard;

import io.deskfolio.service.dashboard.DashboardService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;

public final class DashboardViewFactory {

    private DashboardViewFactory() {
    }

    public static Node create(DashboardService dashboardService) {
        FXMLLoader loader = new FXMLLoader(DashboardViewController.class.getResource("dashboard.fxml"));
        loader.setControllerFactory(controllerClass -> {
            if (controllerClass == DashboardViewController.class) {
                return new DashboardViewController(dashboardService);
            }
            throw new IllegalStateException("Unsupported controller: " + controllerClass.getName());
        });

        try {
            return loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load dashboard view", exception);
        }
    }
}
