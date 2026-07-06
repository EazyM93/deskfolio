package io.deskfolio;

import io.deskfolio.configuration.AppPaths;
import io.deskfolio.configuration.DatabaseMode;
import io.deskfolio.navigation.NavigationRegistry;
import io.deskfolio.persistence.sqlite.DatabaseConfig;
import io.deskfolio.persistence.sqlite.FlywayMigrationRunner;
import io.deskfolio.persistence.sqlite.SqliteConnectionFactory;
import io.deskfolio.persistence.sqlite.asset.SqliteAssetRepository;
import io.deskfolio.persistence.sqlite.dashboard.SqliteDashboardRepository;
import io.deskfolio.persistence.sqlite.reference.SqliteReferenceDataRepository;
import io.deskfolio.persistence.sqlite.transaction.SqliteTransactionRepository;
import io.deskfolio.service.dashboard.DashboardService;
import io.deskfolio.service.transaction.TransactionService;
import io.deskfolio.ui.dashboard.DashboardViewFactory;
import io.deskfolio.ui.shell.ShellController;
import io.deskfolio.ui.transaction.TransactionViewFactory;
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
        DatabaseConfig databaseConfig = initializeDatabase();
        SqliteConnectionFactory connectionFactory = new SqliteConnectionFactory(databaseConfig);
        SqliteAssetRepository assetRepository = new SqliteAssetRepository(connectionFactory);
        SqliteReferenceDataRepository referenceDataRepository = new SqliteReferenceDataRepository(connectionFactory);
        DashboardService dashboardService = new DashboardService(new SqliteDashboardRepository(connectionFactory));
        TransactionService transactionService = new TransactionService(
                connectionFactory,
                assetRepository,
                new SqliteTransactionRepository()
        );
        NavigationRegistry navigationRegistry = NavigationRegistry.defaultRegistry(
                () -> DashboardViewFactory.create(dashboardService),
                () -> TransactionViewFactory.create(transactionService, assetRepository, referenceDataRepository)
        );

        FXMLLoader loader = new FXMLLoader(ShellController.class.getResource("shell.fxml"));
        loader.setControllerFactory(controllerClass -> {
            if (controllerClass == ShellController.class) {
                return new ShellController(navigationRegistry);
            }
            throw new IllegalStateException("Unsupported controller: " + controllerClass.getName());
        });
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

    private DatabaseConfig initializeDatabase() {
        AppPaths appPaths = AppPaths.defaultPaths();
        DatabaseConfig databaseConfig = DatabaseConfig.forFile(appPaths.databaseFile());
        FlywayMigrationRunner migrationRunner = new FlywayMigrationRunner(databaseConfig);
        DatabaseMode databaseMode = DatabaseMode.fromSystemProperty();

        if (databaseMode == DatabaseMode.LOCAL_RESET) {
            LOGGER.warn("Starting with local reset database mode. Existing local data will be dropped.");
            migrationRunner.cleanAndMigrate();
        } else {
            migrationRunner.migrate();
        }

        return databaseConfig;
    }
}
