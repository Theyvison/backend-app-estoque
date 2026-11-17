package io.github.theyvison.estoque.stock;

import java.time.Instant;
import java.util.Objects;

import io.github.theyvison.estoque.product.Product;
import io.github.theyvison.estoque.sale.Sale;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "stock_movements")
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id")
    private Sale sale;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private StockMovementType type;

    @Column(name = "quantity_change", nullable = false)
    private Integer quantityChange;

    @Column(name = "quantity_after", nullable = false)
    private Integer quantityAfter;

    @Column(length = 255)
    private String reason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected StockMovement() {
    }

    public StockMovement(Product product, Sale sale, StockMovementType type, int quantityChange,
            int quantityAfter, String reason) {
        this.product = Objects.requireNonNull(product, "product must not be null");
        this.type = Objects.requireNonNull(type, "type must not be null");
        if (quantityChange == 0) {
            throw new IllegalArgumentException("quantityChange must not be zero");
        }
        if (quantityAfter < 0) {
            throw new IllegalArgumentException("quantityAfter must not be negative");
        }
        if (type == StockMovementType.ADJUSTMENT && (reason == null || reason.isBlank())) {
            throw new IllegalArgumentException("reason is required for an adjustment");
        }
        this.sale = sale;
        this.quantityChange = quantityChange;
        this.quantityAfter = quantityAfter;
        this.reason = reason;
    }

    @PrePersist
    private void setCreatedAtIfMissing() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public Sale getSale() {
        return sale;
    }

    public StockMovementType getType() {
        return type;
    }

    public Integer getQuantityChange() {
        return quantityChange;
    }

    public Integer getQuantityAfter() {
        return quantityAfter;
    }

    public String getReason() {
        return reason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
