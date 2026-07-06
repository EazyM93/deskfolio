package io.deskfolio.ui.transaction;

import io.deskfolio.repository.asset.AssetRepository;
import io.deskfolio.repository.reference.ReferenceDataRepository;
import io.deskfolio.service.transaction.TransactionService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;

public final class TransactionViewFactory {

    private TransactionViewFactory() {
    }

    public static Node create(
            TransactionService transactionService,
            AssetRepository assetRepository,
            ReferenceDataRepository referenceDataRepository
    ) {
        FXMLLoader loader = new FXMLLoader(TransactionViewController.class.getResource("transactions.fxml"));
        loader.setControllerFactory(controllerClass -> {
            if (controllerClass == TransactionViewController.class) {
                return new TransactionViewController(transactionService, assetRepository, referenceDataRepository);
            }
            throw new IllegalStateException("Unsupported controller: " + controllerClass.getName());
        });

        try {
            return loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load transactions view", exception);
        }
    }
}
