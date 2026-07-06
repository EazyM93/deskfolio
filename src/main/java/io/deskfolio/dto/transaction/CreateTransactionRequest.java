package io.deskfolio.dto.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public record CreateTransactionRequest(
        LocalDate transactionDate,
        Optional<Long> existingAssetId,
        Optional<CreateAssetRequest> newAsset,
        long movementTypeId,
        long categoryId,
        BigDecimal valueAmount,
        long currencyId
) {
}
