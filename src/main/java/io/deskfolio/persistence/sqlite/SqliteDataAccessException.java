package io.deskfolio.persistence.sqlite;

public final class SqliteDataAccessException extends RuntimeException {

    public SqliteDataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
