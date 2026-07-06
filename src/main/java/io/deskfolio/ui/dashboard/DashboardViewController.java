package io.deskfolio.ui.dashboard;

import io.deskfolio.domain.dashboard.AssetInvestmentSummary;
import io.deskfolio.domain.dashboard.DashboardSnapshot;
import io.deskfolio.domain.dashboard.MonthlyAssetInvestment;
import io.deskfolio.service.dashboard.DashboardService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class DashboardViewController {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH);

    private final DashboardService dashboardService;
    private final NumberFormat moneyFormat = NumberFormat.getNumberInstance(Locale.ITALY);

    @FXML
    private Label totalInvestedLabel;

    @FXML
    private Label assetCountLabel;

    @FXML
    private LineChart<String, Number> assetInvestmentChart;

    @FXML
    private VBox emptyState;

    @FXML
    private FlowPane assetCards;

    public DashboardViewController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
        moneyFormat.setMinimumFractionDigits(2);
        moneyFormat.setMaximumFractionDigits(2);
    }

    @FXML
    private void initialize() {
        refresh();
    }

    private void refresh() {
        DashboardSnapshot snapshot = dashboardService.snapshot();

        totalInvestedLabel.setText(formatMoney(snapshot.totalInvestedMinorUnits(), snapshot.displayCurrencyCode()));
        assetCountLabel.setText(Integer.toString(snapshot.assetSummaries().size()));
        updateEmptyState(snapshot);
        updateChart(snapshot);
        updateAssetCards(snapshot);
    }

    private void updateEmptyState(DashboardSnapshot snapshot) {
        boolean showEmptyState = !snapshot.hasInvestments();
        emptyState.setVisible(showEmptyState);
        emptyState.setManaged(showEmptyState);
    }

    private void updateChart(DashboardSnapshot snapshot) {
        assetInvestmentChart.getData().clear();
        Map<Long, XYChart.Series<String, Number>> seriesByAssetId = new LinkedHashMap<>();
        Set<String> monthLabels = new LinkedHashSet<>();

        for (MonthlyAssetInvestment investment : snapshot.monthlyAssetInvestments()) {
            String monthLabel = investment.month().format(MONTH_FORMATTER);
            monthLabels.add(monthLabel);

            XYChart.Series<String, Number> series = seriesByAssetId.computeIfAbsent(investment.assetId(), assetId -> {
                XYChart.Series<String, Number> newSeries = new XYChart.Series<>();
                newSeries.setName(investment.assetName());
                return newSeries;
            });
            XYChart.Data<String, Number> chartPoint = new XYChart.Data<>(
                    monthLabel,
                    investment.cumulativeInvestedMinorUnits() / 100.0
            );
            series.getData().add(chartPoint);
        }

        if (assetInvestmentChart.getXAxis() instanceof CategoryAxis categoryAxis) {
            List<String> categories = new ArrayList<>(monthLabels);
            categoryAxis.setCategories(FXCollections.observableArrayList(categories));
        }
        assetInvestmentChart.getData().addAll(seriesByAssetId.values());
    }

    private void updateAssetCards(DashboardSnapshot snapshot) {
        assetCards.getChildren().clear();
        snapshot.assetSummaries().forEach(this::addAssetCard);
    }

    private void addAssetCard(AssetInvestmentSummary asset) {
        VBox card = new VBox(6);
        card.getStyleClass().add("asset-card");

        Label name = new Label(asset.assetName());
        name.getStyleClass().add("asset-card-title");

        Label metadata = new Label(asset.ticker().map(ticker -> ticker + " · ").orElse("") + asset.assetTypeName());
        metadata.getStyleClass().add("asset-card-meta");

        Label investedValue = new Label(formatMoney(asset.totalInvestedMinorUnits(), asset.currencyCode()));
        investedValue.getStyleClass().add("asset-card-value");

        Label detail = new Label("Total invested");
        detail.getStyleClass().add("asset-card-meta");

        card.getChildren().addAll(name, metadata, investedValue, detail);
        assetCards.getChildren().add(card);
    }

    private String formatMoney(long minorUnits, String currencyCode) {
        String sign = minorUnits < 0 ? "-" : "";
        return sign + moneyFormat.format(Math.abs(minorUnits) / 100.0) + " " + currencyCode;
    }

}
