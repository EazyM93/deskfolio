package io.deskfolio.domain.dashboard;

import java.util.List;

public record DashboardSnapshot(
        long totalInvestedMinorUnits,
        String displayCurrencyCode,
        List<AssetInvestmentSummary> assetSummaries,
        List<MonthlyAssetInvestment> monthlyAssetInvestments
) {

    public boolean hasAssets() {
        return !assetSummaries.isEmpty();
    }

    public boolean hasInvestments() {
        return assetSummaries.stream().anyMatch(summary -> summary.totalInvestedMinorUnits() != 0);
    }
}
