package io.deskfolio.validation.transaction;

import java.util.List;

public final class TransactionValidationException extends RuntimeException {

    private final List<String> errors;

    public TransactionValidationException(List<String> errors) {
        super(String.join("\n", errors));
        this.errors = List.copyOf(errors);
    }

    public List<String> errors() {
        return errors;
    }
}
