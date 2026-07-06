package io.deskfolio.service.dashboard;

import io.deskfolio.domain.dashboard.AssetInvestmentSummary;
import io.deskfolio.domain.dashboard.DashboardSnapshot;
import io.deskfolio.repository.dashboard.DashboardRepository;

import java.util.List;
import java.util.Objects;

public final class DashboardService {

    private static final long DEFAULT_PORTFOLIO_ID = 1;
    private static final String DEFAULT_DISPLAY_CURRENCY = "EUR";

    private final DashboardRepository dashboardRepository;

    public DashboardService(DashboardRepository dashboardRepository) {
        this.dashboardRepository = Objects.requireNonNull(dashboardRepository, "dashboardRepository");
    }

    public DashboardSnapshot snapshot() {
        List<AssetInvestmentSummary> assetSummaries = dashboardRepository.assetInvestmentSummaries(DEFAULT_PORTFOLIO_ID);
        long totalInvested = assetSummaries.stream()
                .mapToLong(AssetInvestmentSummary::totalInvestedMinorUnits)
                .sum();
        String displayCurrency = assetSummaries.stream()
                .filter(summary -> summary.totalInvestedMinorUnits() != 0)
                .map(AssetInvestmentSummary::currencyCode)
                .findFirst()
                .orElse(DEFAULT_DISPLAY_CURRENCY);

        return new DashboardSnapshot(
                totalInvested,
                displayCurrency,
                assetSummaries,
                dashboardRepository.monthlyAssetInvestments(DEFAULT_PORTFOLIO_ID)
        );
    }
}
