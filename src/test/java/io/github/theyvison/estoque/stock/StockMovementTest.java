package io.github.theyvison.estoque.stock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import io.github.theyvison.estoque.product.Product;
import io.github.theyvison.estoque.user.User;

class StockMovementTest {

    private final Product product = new Product(
            new User("Vendedor", "vendedor@example.com", "hashed-password"),
            "Produto", null, new BigDecimal("12.50"), 10, 5, null);

    @Test
    void createsInitialMovementWithoutSale() {
        StockMovement movement = new StockMovement(product, null, StockMovementType.INITIAL, 10, 10, null);

        assertEquals(StockMovementType.INITIAL, movement.getType());
        assertEquals(10, movement.getQuantityChange());
        assertEquals(10, movement.getQuantityAfter());
    }

    @Test
    void requiresReasonForAdjustment() {
        assertThrows(IllegalArgumentException.class,
                () -> new StockMovement(product, null, StockMovementType.ADJUSTMENT, 2, 12, " "));
    }

    @Test
    void rejectsZeroQuantityChange() {
        assertThrows(IllegalArgumentException.class,
                () -> new StockMovement(product, null, StockMovementType.INITIAL, 0, 10, null));
    }

    @Test
    void rejectsNegativeResultingStock() {
        assertThrows(IllegalArgumentException.class,
                () -> new StockMovement(product, null, StockMovementType.SALE, -3, -1, null));
    }
}
