package io.deskfolio.configuration;

import java.util.Locale;

public enum DatabaseMode {
    MANAGED,
    LOCAL_RESET;

    private static final String PROPERTY_NAME = "deskfolio.database.mode";

    public static DatabaseMode fromSystemProperty() {
        String configuredValue = System.getProperty(PROPERTY_NAME, MANAGED.name());
        return from(configuredValue);
    }

    static DatabaseMode from(String value) {
        String normalizedValue = value == null ? "" : value.trim()
                .replace('-', '_')
                .toUpperCase(Locale.ROOT);

        return switch (normalizedValue) {
            case "LOCAL_RESET" -> LOCAL_RESET;
            case "MANAGED", "" -> MANAGED;
            default -> throw new IllegalArgumentException(
                    "Unsupported database mode '%s'. Use managed or local-reset.".formatted(value)
            );
        };
    }
}
