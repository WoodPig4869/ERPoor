package org.allen.erpoor.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Immutable
@Getter
@ToString
@NoArgsConstructor
@Table(name = "product_inventory_view")
public class ProductInventoryView {

    @Id
    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "name")
    private String name;

    @Column(name = "category")
    private String category;

    @Column(name = "unit")
    private String unit;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "total_stock")
    private Integer totalStock;

    @Column(name = "available_stock")
    private Integer availableStock;

    @Column(name = "expired_stock")
    private Integer expiredStock;

    @Column(name = "nearest_expiry_date")
    private LocalDate nearestExpiryDate;

}