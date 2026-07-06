package io.deskfolio.service.transaction;

import io.deskfolio.dto.transaction.CreateAssetRequest;
import io.deskfolio.dto.transaction.CreateTransactionRequest;
import io.deskfolio.dto.transaction.CreateTransactionResult;
import io.deskfolio.persistence.sqlite.DatabaseConfig;
import io.deskfolio.persistence.sqlite.FlywayMigrationRunner;
import io.deskfolio.persistence.sqlite.SqliteConnectionFactory;
import io.deskfolio.persistence.sqlite.asset.SqliteAssetRepository;
import io.deskfolio.persistence.sqlite.transaction.SqliteTransactionRepository;
import io.deskfolio.validation.transaction.TransactionValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransactionServiceIntegrationTest {

    @TempDir
    private Path temporaryDirectory;

    private SqliteConnectionFactory connectionFactory;
    private SqliteAssetRepository assetRepository;
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        DatabaseConfig databaseConfig = DatabaseConfig.forFile(temporaryDirectory.resolve("deskfolio-test.sqlite"));
        new FlywayMigrationRunner(databaseConfig).migrate();
        connectionFactory = new SqliteConnectionFactory(databaseConfig);
        assetRepository = new SqliteAssetRepository(connectionFactory);
        transactionService = new TransactionService(
                connectionFactory,
                assetRepository,
                new SqliteTransactionRepository()
        );
    }

    @Test
    void createsTransactionForExistingAsset() throws Exception {
        long assetId = createAsset(new CreateAssetRequest(
                "Vanguard FTSE All-World",
                Optional.of("VWCE"),
                Optional.of("IE00BK5BQT80"),
                1,
                Optional.of(new BigDecimal("0.22"))
        ));

        CreateTransactionResult result = transactionService.createTransaction(new CreateTransactionRequest(
                LocalDate.of(2026, 7, 6),
                Optional.of(assetId),
                Optional.empty(),
                1,
                1,
                new BigDecimal("1250.50"),
                1
        ));

        assertThat(result.assetId()).isEqualTo(assetId);
        assertThat(result.transactionId()).isPositive();
        assertThat(singleLong("SELECT value_minor_units FROM portfolio_transaction WHERE id = " + result.transactionId()))
                .isEqualTo(125_050L);
    }

    @Test
    void createsNewAssetAndTransactionAtomically() throws Exception {
        CreateTransactionResult result = transactionService.createTransaction(new CreateTransactionRequest(
                LocalDate.of(2026, 7, 6),
                Optional.empty(),
                Optional.of(new CreateAssetRequest(
                        "Apple Inc.",
                        Optional.of("aapl"),
                        Optional.of("us0378331005"),
                        2,
                        Optional.empty()
                )),
                1,
                1,
                new BigDecimal("500.00"),
                2
        ));

        assertThat(result.assetId()).isPositive();
        assertThat(singleText("SELECT ticker FROM asset WHERE id = " + result.assetId())).isEqualTo("AAPL");
        assertThat(singleText("SELECT isin FROM asset WHERE id = " + result.assetId())).isEqualTo("US0378331005");
        assertThat(singleLong("SELECT asset_id FROM portfolio_transaction WHERE id = " + result.transactionId()))
                .isEqualTo(result.assetId());
    }

    @Test
    void rollsBackNewAssetWhenTransactionCreationFails() throws Exception {
        assertThatThrownBy(() -> transactionService.createTransaction(new CreateTransactionRequest(
                LocalDate.of(2026, 7, 6),
                Optional.empty(),
                Optional.of(new CreateAssetRequest(
                        "Rollback Asset",
                        Optional.of("ROLL"),
                        Optional.empty(),
                        2,
                        Optional.empty()
                )),
                999,
                1,
                new BigDecimal("10.00"),
                1
        ))).isInstanceOf(RuntimeException.class);

        assertThat(singleLong("SELECT COUNT(*) FROM asset WHERE name = 'Rollback Asset'")).isZero();
        assertThat(singleLong("SELECT COUNT(*) FROM portfolio_transaction")).isZero();
    }

    @Test
    void rejectsInvalidTransactionWithSpecificErrors() {
        assertThatThrownBy(() -> transactionService.createTransaction(new CreateTransactionRequest(
                null,
                Optional.empty(),
                Optional.empty(),
                0,
                0,
                new BigDecimal("-1.00"),
                0
        )))
                .isInstanceOf(TransactionValidationException.class)
                .hasMessageContaining("Date is required.")
                .hasMessageContaining("Value must be greater than zero.")
                .hasMessageContaining("Choose an existing asset or create a new asset.");
    }

    @Test
    void rejectsTerForNonEtfAssets() {
        assertThatThrownBy(() -> transactionService.createTransaction(new CreateTransactionRequest(
                LocalDate.of(2026, 7, 6),
                Optional.empty(),
                Optional.of(new CreateAssetRequest(
                        "Stock With TER",
                        Optional.of("SWT"),
                        Optional.empty(),
                        2,
                        Optional.of(new BigDecimal("0.10"))
                )),
                1,
                1,
                new BigDecimal("10.00"),
                1
        )))
                .isInstanceOf(TransactionValidationException.class)
                .hasMessageContaining("TER is only applicable to ETF assets.");
    }

    private long createAsset(CreateAssetRequest request) throws Exception {
        try (Connection connection = connectionFactory.openConnection()) {
            return assetRepository.create(connection, request, LocalDate.of(2026, 7, 6));
        }
    }

    private long singleLong(String sql) throws Exception {
        try (Connection connection = connectionFactory.openConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            assertThat(resultSet.next()).isTrue();
            return resultSet.getLong(1);
        }
    }

    private String singleText(String sql) throws Exception {
        try (Connection connection = connectionFactory.openConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            assertThat(resultSet.next()).isTrue();
            return resultSet.getString(1);
        }
    }
}
