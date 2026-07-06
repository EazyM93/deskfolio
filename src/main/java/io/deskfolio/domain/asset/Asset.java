package io.deskfolio.domain.asset;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public record Asset(
        long id,
        String name,
        Optional<String> ticker,
        Optional<String> isin,
        long assetTypeId,
        Optional<BigDecimal> ter,
        LocalDate creationDate
) {

    public Asset {
        if (id < 1) {
            throw new IllegalArgumentException("Asset id must be positive");
        }
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(ticker, "ticker");
        Objects.requireNonNull(isin, "isin");
        Objects.requireNonNull(ter, "ter");
        Objects.requireNonNull(creationDate, "creationDate");
    }
}
