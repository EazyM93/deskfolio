package io.deskfolio.domain.transaction;

import java.time.LocalDate;
import java.util.Objects;

public record PortfolioTransaction(
        long id,
        long portfolioId,
        LocalDate transactionDate,
        long assetId,
        long movementTypeId,
        long categoryId,
        long valueMinorUnits,
        long currencyId
) {

    public PortfolioTransaction {
        if (id < 1) {
            throw new IllegalArgumentException("Transaction id must be positive");
        }
        Objects.requireNonNull(transactionDate, "transactionDate");
    }
}
