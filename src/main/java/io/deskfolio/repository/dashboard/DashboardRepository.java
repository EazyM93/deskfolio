package io.deskfolio.repository.dashboard;

import io.deskfolio.domain.dashboard.AssetInvestmentSummary;
import io.deskfolio.domain.dashboard.MonthlyAssetInvestment;

import java.util.List;

public interface DashboardRepository {

    List<AssetInvestmentSummary> assetInvestmentSummaries(long portfolioId);

    List<MonthlyAssetInvestment> monthlyAssetInvestments(long portfolioId);
}
