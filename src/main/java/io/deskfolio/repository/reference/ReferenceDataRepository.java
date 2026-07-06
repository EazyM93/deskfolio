package io.deskfolio.repository.reference;

import java.util.List;

public interface ReferenceDataRepository {

    List<ReferenceOption> assetTypes();

    List<ReferenceOption> movementTypes();

    List<ReferenceOption> transactionCategories();

    List<ReferenceOption> currencies();
}
