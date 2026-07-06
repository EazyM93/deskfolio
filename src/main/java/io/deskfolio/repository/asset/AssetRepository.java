package io.deskfolio.repository.asset;

import io.deskfolio.domain.asset.Asset;
import io.deskfolio.dto.transaction.CreateAssetRequest;
import io.deskfolio.repository.reference.ReferenceOption;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AssetRepository {

    long create(Connection connection, CreateAssetRequest request, LocalDate creationDate);

    List<Asset> findAll();

    List<ReferenceOption> findAssetOptions();

    Optional<Asset> findById(Connection connection, long id);
}
