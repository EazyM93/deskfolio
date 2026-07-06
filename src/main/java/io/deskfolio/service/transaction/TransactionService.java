package io.deskfolio.service.transaction;

import io.deskfolio.dto.transaction.CreateAssetRequest;
import io.deskfolio.dto.transaction.CreateTransactionRequest;
import io.deskfolio.dto.transaction.CreateTransactionResult;
import io.deskfolio.persistence.sqlite.SqliteConnectionFactory;
import io.deskfolio.persistence.sqlite.SqliteDataAccessException;
import io.deskfolio.repository.asset.AssetRepository;
import io.deskfolio.repository.transaction.TransactionRepository;
import io.deskfolio.validation.transaction.TransactionValidationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class TransactionService {

    private static final long DEFAULT_PORTFOLIO_ID = 1;
    private static final long ETF_ASSET_TYPE_ID = 1;
    private static final int MONEY_SCALE = 2;

    private final SqliteConnectionFactory connectionFactory;
    private final AssetRepository assetRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(
            SqliteConnectionFactory connectionFactory,
            AssetRepository assetRepository,
            TransactionRepository transactionRepository
    ) {
        this.connectionFactory = Objects.requireNonNull(connectionFactory, "connectionFactory");
        this.assetRepository = Objects.requireNonNull(assetRepository, "assetRepository");
        this.transactionRepository = Objects.requireNonNull(transactionRepository, "transactionRepository");
    }

    public CreateTransactionResult createTransaction(CreateTransactionRequest request) {
        CreateTransactionRequest normalizedRequest = normalize(request);
        validate(normalizedRequest);

        try (Connection connection = connectionFactory.openConnection()) {
            boolean previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                long assetId = resolveAssetId(connection, normalizedRequest);
                long transactionId = transactionRepository.create(
                        connection,
                        DEFAULT_PORTFOLIO_ID,
                        normalizedRequest.transactionDate(),
                        assetId,
                        normalizedRequest.movementTypeId(),
                        normalizedRequest.categoryId(),
                        toMinorUnits(normalizedRequest.valueAmount()),
                        normalizedRequest.currencyId()
                );
                connection.commit();
                return new CreateTransactionResult(transactionId, assetId);
            } catch (RuntimeException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(previousAutoCommit);
            }
        } catch (SQLException exception) {
            throw new SqliteDataAccessException("Could not create transaction atomically", exception);
        }
    }

    private long resolveAssetId(Connection connection, CreateTransactionRequest request) {
        if (request.newAsset().isPresent()) {
            return assetRepository.create(connection, request.newAsset().orElseThrow(), LocalDate.now());
        }

        long assetId = request.existingAssetId().orElseThrow();
        if (assetRepository.findById(connection, assetId).isEmpty()) {
            throw new TransactionValidationException(List.of("Selected asset does not exist."));
        }
        return assetId;
    }

    private CreateTransactionRequest normalize(CreateTransactionRequest request) {
        Optional<CreateAssetRequest> normalizedAsset = request.newAsset().map(asset -> new CreateAssetRequest(
                asset.name() == null ? "" : asset.name().trim(),
                normalizedOptionalUppercase(asset.ticker()),
                normalizedOptionalUppercase(asset.isin()),
                asset.assetTypeId(),
                asset.ter()
        ));

        return new CreateTransactionRequest(
                request.transactionDate(),
                request.existingAssetId(),
                normalizedAsset,
                request.movementTypeId(),
                request.categoryId(),
                request.valueAmount(),
                request.currencyId()
        );
    }

    private Optional<String> normalizedOptionalUppercase(Optional<String> value) {
        return value
                .map(String::trim)
                .filter(text -> !text.isBlank())
                .map(text -> text.toUpperCase(java.util.Locale.ROOT));
    }

    private void validate(CreateTransactionRequest request) {
        List<String> errors = new ArrayList<>();

        if (request.transactionDate() == null) {
            errors.add("Date is required.");
        }
        if (request.valueAmount() == null) {
            errors.add("Value is required.");
        } else if (request.valueAmount().signum() <= 0) {
            errors.add("Value must be greater than zero.");
        } else if (request.valueAmount().scale() > MONEY_SCALE) {
            errors.add("Value can have at most two decimal places.");
        }
        if (request.movementTypeId() < 1) {
            errors.add("Movement type is required.");
        }
        if (request.categoryId() < 1) {
            errors.add("Category is required.");
        }
        if (request.currencyId() < 1) {
            errors.add("Currency is required.");
        }

        boolean hasExistingAsset = request.existingAssetId().isPresent();
        boolean hasNewAsset = request.newAsset().isPresent();
        if (hasExistingAsset == hasNewAsset) {
            errors.add("Choose an existing asset or create a new asset.");
        }

        request.newAsset().ifPresent(asset -> validateNewAsset(asset, errors));

        if (!errors.isEmpty()) {
            throw new TransactionValidationException(errors);
        }
    }

    private void validateNewAsset(CreateAssetRequest asset, List<String> errors) {
        if (asset.name() == null || asset.name().isBlank()) {
            errors.add("Asset name is required.");
        }
        if (asset.assetTypeId() < 1) {
            errors.add("Asset type is required.");
        }
        asset.ter().ifPresent(ter -> {
            if (asset.assetTypeId() != ETF_ASSET_TYPE_ID) {
                errors.add("TER is only applicable to ETF assets.");
            }
            if (ter.signum() < 0) {
                errors.add("TER cannot be negative.");
            }
        });
    }

    private long toMinorUnits(BigDecimal value) {
        return value
                .setScale(MONEY_SCALE, RoundingMode.UNNECESSARY)
                .movePointRight(MONEY_SCALE)
                .longValueExact();
    }
}
