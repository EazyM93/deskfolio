package io.deskfolio.domain.dashboard;

import java.time.LocalDate;

public record MonthlyAssetInvestment(
        long assetId,
        String assetName,
        LocalDate month,
        long cumulativeInvestedMinorUnits
) {
}
