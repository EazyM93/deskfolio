package io.deskfolio.domain.dashboard;

import java.util.Optional;

public record AssetInvestmentSummary(
        long assetId,
        String assetName,
        Optional<String> ticker,
        String assetTypeName,
        long totalInvestedMinorUnits,
        String currencyCode
) {
}
