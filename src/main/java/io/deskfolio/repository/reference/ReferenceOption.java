package io.deskfolio.repository.reference;

public record ReferenceOption(long id, String code, String name) {

    @Override
    public String toString() {
        return name;
    }
}
