package io.deskfolio.service.dashboard;

import io.deskfolio.domain.dashboard.DashboardSnapshot;
import io.deskfolio.dto.transaction.CreateAssetRequest;
import io.deskfolio.dto.transaction.CreateTransactionRequest;
import io.deskfolio.persistence.sqlite.DatabaseConfig;
import io.deskfolio.persistence.sqlite.FlywayMigrationRunner;
import io.deskfolio.persistence.sqlite.SqliteConnectionFactory;
import io.deskfolio.persistence.sqlite.asset.SqliteAssetRepository;
import io.deskfolio.persistence.sqlite.dashboard.SqliteDashboardRepository;
import io.deskfolio.persistence.sqlite.transaction.SqliteTransactionRepository;
import io.deskfolio.service.transaction.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class DashboardServiceIntegrationTest {

    @TempDir
    private Path temporaryDirectory;

    private DashboardService dashboardService;
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        DatabaseConfig databaseConfig = DatabaseConfig.forFile(temporaryDirectory.resolve("deskfolio-test.sqlite"));
        new FlywayMigrationRunner(databaseConfig).migrate();
        SqliteConnectionFactory connectionFactory = new SqliteConnectionFactory(databaseConfig);
        SqliteAssetRepository assetRepository = new SqliteAssetRepository(connectionFactory);
        transactionService = new TransactionService(
                connectionFactory,
                assetRepository,
                new SqliteTransactionRepository()
        );
        dashboardService = new DashboardService(new SqliteDashboardRepository(connectionFactory));
    }

    @Test
    void loadsEmptyDashboardWithoutInvestments() {
        DashboardSnapshot snapshot = dashboardService.snapshot();

        assertThat(snapshot.hasAssets()).isFalse();
        assertThat(snapshot.hasInvestments()).isFalse();
        assertThat(snapshot.totalInvestedMinorUnits()).isZero();
        assertThat(snapshot.monthlyAssetInvestments()).isEmpty();
    }

    @Test
    void calculatesInvestedValuePerAssetAndCumulativeMonthlySeries() {
        long firstAssetId = createNewAssetTransaction(
                "Vanguard FTSE All-World",
                "VWCE",
                1,
                LocalDate.of(2026, 1, 15),
                "100.00",
                1
        );
        createExistingAssetTransaction(firstAssetId, LocalDate.of(2026, 2, 10), "50.00", 3);

        long secondAssetId = createNewAssetTransaction(
                "Apple Inc.",
                "AAPL",
                2,
                LocalDate.of(2026, 2, 20),
                "200.00",
                1
        );
        createExistingAssetTransaction(secondAssetId, LocalDate.of(2026, 3, 5), "25.00", 2);

        createNewAssetTransaction(
                "Microsoft Corp.",
                "MSFT",
                2,
                LocalDate.of(2026, 1, 25),
                "75.00",
                1
        );

        DashboardSnapshot snapshot = dashboardService.snapshot();

        assertThat(snapshot.totalInvestedMinorUnits()).isEqualTo(40_000L);
        assertThat(snapshot.assetSummaries())
                .extracting(summary -> summary.assetName() + ":" + summary.totalInvestedMinorUnits())
                .containsExactly(
                        "Apple Inc.:17500",
                        "Microsoft Corp.:7500",
                        "Vanguard FTSE All-World:15000"
                );
        assertThat(snapshot.monthlyAssetInvestments())
                .extracting(point -> point.assetName()
                        + ":" + point.month()
                        + ":" + point.cumulativeInvestedMinorUnits())
                .containsExactly(
                        "Microsoft Corp.:2025-12-01:0",
                        "Vanguard FTSE All-World:2025-12-01:0",
                        "Apple Inc.:2026-01-01:0",
                        "Microsoft Corp.:2026-01-01:7500",
                        "Vanguard FTSE All-World:2026-01-01:10000",
                        "Apple Inc.:2026-02-01:20000",
                        "Vanguard FTSE All-World:2026-02-01:15000",
                        "Apple Inc.:2026-03-01:17500"
                );
    }

    private long createNewAssetTransaction(
            String assetName,
            String ticker,
            long assetTypeId,
            LocalDate date,
            String value,
            long movementTypeId
    ) {
        return transactionService.createTransaction(new CreateTransactionRequest(
                date,
                Optional.empty(),
                Optional.of(new CreateAssetRequest(
                        assetName,
                        Optional.of(ticker),
                        Optional.empty(),
                        assetTypeId,
                        Optional.empty()
                )),
                movementTypeId,
                1,
                new BigDecimal(value),
                1
        )).assetId();
    }

    private void createExistingAssetTransaction(long assetId, LocalDate date, String value, long movementTypeId) {
        transactionService.createTransaction(new CreateTransactionRequest(
                date,
                Optional.of(assetId),
                Optional.empty(),
                movementTypeId,
                1,
                new BigDecimal(value),
                1
        ));
    }
}
