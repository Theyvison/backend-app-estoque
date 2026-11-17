package io.github.theyvison.estoque.sale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import io.github.theyvison.estoque.product.Product;
import io.github.theyvison.estoque.user.User;

class SaleTest {

    private final User owner = new User("Vendedor", "vendedor@example.com", "hashed-password");
    private final Product product = new Product(owner, "Produto", null, new BigDecimal("12.50"), 10, 5, null);

    @Test
    void calculatesTotalAndStartsAsCompleted() {
        Sale sale = new Sale(owner, product, "Cliente", LocalDate.of(2026, 11, 17), 3, new BigDecimal("12.50"));

        assertEquals(new BigDecimal("37.50"), sale.getTotalAmount());
        assertEquals(SaleStatus.COMPLETED, sale.getStatus());
        assertEquals("Produto", sale.getProductName());
    }

    @Test
    void cancellationSetsStatusAndTimestamp() {
        Sale sale = createSale();

        sale.cancel();

        assertEquals(SaleStatus.CANCELED, sale.getStatus());
        assertNotNull(sale.getCanceledAt());
    }

    @Test
    void cannotCancelSaleTwice() {
        Sale sale = createSale();
        sale.cancel();

        assertThrows(IllegalStateException.class, sale::cancel);
    }

    @Test
    void rejectsNonPositiveQuantity() {
        assertThrows(IllegalArgumentException.class,
                () -> new Sale(owner, product, "Cliente", LocalDate.now(), 0, new BigDecimal("12.50")));
    }

    private Sale createSale() {
        return new Sale(owner, product, "Cliente", LocalDate.of(2026, 11, 17), 1, new BigDecimal("12.50"));
    }
}
