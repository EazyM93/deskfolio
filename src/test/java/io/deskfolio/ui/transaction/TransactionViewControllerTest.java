package io.deskfolio.ui.transaction;

import io.deskfolio.validation.transaction.TransactionValidationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransactionViewControllerTest {

    private final TransactionViewController controller = new TransactionViewController(null, null, null);

    @Test
    void parsesWholeNumberAmount() {
        assertThat(controller.parseAmount("100")).isEqualByComparingTo(new BigDecimal("100"));
    }

    @Test
    void parsesDecimalAmountsWithDotOrComma() {
        assertThat(controller.parseAmount("100.50")).isEqualByComparingTo(new BigDecimal("100.50"));
        assertThat(controller.parseAmount("100,50")).isEqualByComparingTo(new BigDecimal("100.50"));
    }

    @Test
    void blankAmountReturnsNullForRequiredFieldValidation() {
        assertThat(controller.parseAmount(" ")).isNull();
    }

    @Test
    void rejectsInvalidAmount() {
        assertThatThrownBy(() -> controller.parseAmount("abc"))
                .isInstanceOf(TransactionValidationException.class)
                .hasMessageContaining("Value must be a valid financial amount.");
    }
}
