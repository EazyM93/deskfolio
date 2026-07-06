package io.deskfolio.repository.transaction;

import java.sql.Connection;
import java.time.LocalDate;

public interface TransactionRepository {

    long create(
            Connection connection,
            long portfolioId,
            LocalDate transactionDate,
            long assetId,
            long movementTypeId,
            long categoryId,
            long valueMinorUnits,
            long currencyId
    );
}
