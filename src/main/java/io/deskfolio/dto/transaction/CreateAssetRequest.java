package io.deskfolio.dto.transaction;

import java.math.BigDecimal;
import java.util.Optional;

public record CreateAssetRequest(
        String name,
        Optional<String> ticker,
        Optional<String> isin,
        long assetTypeId,
        Optional<BigDecimal> ter
) {
}
