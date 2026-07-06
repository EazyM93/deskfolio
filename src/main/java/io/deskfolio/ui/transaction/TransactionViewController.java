package io.deskfolio.ui.transaction;

import io.deskfolio.dto.transaction.CreateAssetRequest;
import io.deskfolio.dto.transaction.CreateTransactionRequest;
import io.deskfolio.repository.asset.AssetRepository;
import io.deskfolio.repository.reference.ReferenceDataRepository;
import io.deskfolio.repository.reference.ReferenceOption;
import io.deskfolio.service.transaction.TransactionService;
import io.deskfolio.validation.transaction.TransactionValidationException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public final class TransactionViewController {

    private static final String ETF_CODE = "ETF";

    private final TransactionService transactionService;
    private final AssetRepository assetRepository;
    private final ReferenceDataRepository referenceDataRepository;
    private List<ReferenceOption> assetTypes;
    private Optional<ReferenceOption> etfAssetType = Optional.empty();

    @FXML
    private DatePicker transactionDatePicker;

    @FXML
    private CheckBox createNewAssetCheckBox;

    @FXML
    private ComboBox<ReferenceOption> existingAssetComboBox;

    @FXML
    private VBox newAssetFields;

    @FXML
    private TextField assetNameField;

    @FXML
    private TextField tickerField;

    @FXML
    private TextField isinField;

    @FXML
    private ComboBox<ReferenceOption> assetTypeComboBox;

    @FXML
    private CheckBox etfCheckBox;

    @FXML
    private TextField terField;

    @FXML
    private ComboBox<ReferenceOption> movementTypeComboBox;

    @FXML
    private ComboBox<ReferenceOption> categoryComboBox;

    @FXML
    private TextField valueField;

    @FXML
    private ComboBox<ReferenceOption> currencyComboBox;

    @FXML
    private Label messageLabel;

    public TransactionViewController(
            TransactionService transactionService,
            AssetRepository assetRepository,
            ReferenceDataRepository referenceDataRepository
    ) {
        this.transactionService = transactionService;
        this.assetRepository = assetRepository;
        this.referenceDataRepository = referenceDataRepository;
    }

    @FXML
    private void initialize() {
        transactionDatePicker.setValue(LocalDate.now());
        reloadAssets();
        loadAssetTypes();
        movementTypeComboBox.setItems(FXCollections.observableArrayList(referenceDataRepository.movementTypes()));
        categoryComboBox.setItems(FXCollections.observableArrayList(referenceDataRepository.transactionCategories()));
        currencyComboBox.setItems(FXCollections.observableArrayList(referenceDataRepository.currencies()));

        selectFirst(movementTypeComboBox);
        selectFirst(categoryComboBox);
        selectFirst(currencyComboBox);

        createNewAssetCheckBox.selectedProperty().addListener((observable, oldValue, selected) -> updateAssetMode());
        etfCheckBox.selectedProperty().addListener((observable, oldValue, selected) -> updateEtfMode());
        updateAssetMode();
        updateEtfMode();
    }

    @FXML
    private void saveTransaction() {
        clearMessage();

        try {
            CreateTransactionRequest request = buildRequest();
            transactionService.createTransaction(request);
            messageLabel.getStyleClass().setAll("message-label", "success-message");
            messageLabel.setText("Transaction saved.");
            clearFormAfterSave();
            reloadAssets();
        } catch (TransactionValidationException exception) {
            messageLabel.getStyleClass().setAll("message-label", "error-message");
            messageLabel.setText(String.join("\n", exception.errors()));
        } catch (RuntimeException exception) {
            messageLabel.getStyleClass().setAll("message-label", "error-message");
            messageLabel.setText("Could not save transaction. Check the entered data and try again.");
        }
    }

    private CreateTransactionRequest buildRequest() {
        Optional<CreateAssetRequest> newAsset = createNewAssetCheckBox.isSelected()
                ? Optional.of(new CreateAssetRequest(
                assetNameField.getText(),
                optionalText(tickerField.getText()),
                optionalText(isinField.getText()),
                selectedNewAssetTypeId(),
                etfCheckBox.isSelected() ? optionalDecimal(terField.getText()) : Optional.empty()
        ))
                : Optional.empty();

        Optional<Long> existingAssetId = createNewAssetCheckBox.isSelected()
                ? Optional.empty()
                : Optional.ofNullable(existingAssetComboBox.getValue()).map(ReferenceOption::id);

        return new CreateTransactionRequest(
                transactionDatePicker.getValue(),
                existingAssetId,
                newAsset,
                selectedId(movementTypeComboBox),
                selectedId(categoryComboBox),
                parseAmount(valueField.getText()),
                selectedId(currencyComboBox)
        );
    }

    private void updateAssetMode() {
        boolean createNewAsset = createNewAssetCheckBox.isSelected();
        existingAssetComboBox.setDisable(createNewAsset);
        newAssetFields.setVisible(createNewAsset);
        newAssetFields.setManaged(createNewAsset);
    }

    private void updateEtfMode() {
        if (etfCheckBox.isSelected()) {
            assetTypeComboBox.setDisable(true);
            assetTypeComboBox.getSelectionModel().clearSelection();
        } else {
            assetTypeComboBox.setDisable(false);
            selectFirst(assetTypeComboBox);
        }
        terField.setDisable(!etfCheckBox.isSelected());
    }

    private void loadAssetTypes() {
        assetTypes = referenceDataRepository.assetTypes();
        etfAssetType = assetTypes.stream()
                .filter(option -> ETF_CODE.equals(option.code()))
                .findFirst();
        assetTypeComboBox.setItems(FXCollections.observableArrayList(assetTypes.stream()
                .filter(option -> !ETF_CODE.equals(option.code()))
                .toList()));
        selectFirst(assetTypeComboBox);
    }

    private void reloadAssets() {
        existingAssetComboBox.setItems(FXCollections.observableArrayList(assetRepository.findAssetOptions()));
    }

    private void clearFormAfterSave() {
        transactionDatePicker.setValue(LocalDate.now());
        valueField.clear();
        assetNameField.clear();
        tickerField.clear();
        isinField.clear();
        terField.clear();
        createNewAssetCheckBox.setSelected(false);
        etfCheckBox.setSelected(false);
    }

    private void clearMessage() {
        messageLabel.getStyleClass().setAll("message-label");
        messageLabel.setText("");
    }

    private Optional<String> optionalText(String value) {
        return value == null || value.isBlank() ? Optional.empty() : Optional.of(value.trim());
    }

    private Optional<BigDecimal> optionalDecimal(String value) {
        return optionalText(value).map(this::parseAmount);
    }

    BigDecimal parseAmount(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalizedValue = value.strip()
                .replace(" ", "")
                .replace(",", ".");
        try {
            return new BigDecimal(normalizedValue);
        } catch (NumberFormatException exception) {
            throw new TransactionValidationException(java.util.List.of("Value must be a valid financial amount."));
        }
    }

    private long selectedId(ComboBox<ReferenceOption> comboBox) {
        return comboBox.getValue() == null ? 0 : comboBox.getValue().id();
    }

    private long selectedNewAssetTypeId() {
        if (etfCheckBox.isSelected()) {
            return etfAssetType.map(ReferenceOption::id).orElse(0L);
        }
        return selectedId(assetTypeComboBox);
    }

    private void selectFirst(ComboBox<ReferenceOption> comboBox) {
        if (!comboBox.getItems().isEmpty()) {
            comboBox.getSelectionModel().selectFirst();
        }
    }
}
