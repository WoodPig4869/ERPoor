package org.allen.erpoor.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Immutable
@Table(name = "product_inventory_status")
public class ProductInventoryStatus {

    @Id
    @Column(name = "product_id")
    private final Integer productId;

    @Column(name = "product_name", nullable = false)
    private final String productName;

    @Column(name = "unit", nullable = false)
    private final String unit;

    @Column(name = "category", nullable = false)
    private final String category;

    @Column(name = "description")
    private final String description;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private final BigDecimal price;

    @Column(name = "enabled", nullable = false)
    private final Boolean enabled;

    @Column(name = "total_available_quantity", nullable = false)
    private final Integer totalAvailableQuantity;

    @Column(name = "latest_received_date")
    private final LocalDate latestReceivedDate;

    @Column(name = "nearest_expiry_date")
    private final LocalDate nearestExpiryDate;

    @Column(name = "supplier_for_nearest_expiry")
    private final String supplierForNearestExpiry;

    // 全參數建構子 (由JPA或查詢使用)
    public ProductInventoryStatus(
            Integer productId,
            String productName,
            String unit,
            String category,
            String description,
            BigDecimal price,
            Boolean enabled,
            Integer totalAvailableQuantity,
            LocalDate latestReceivedDate,
            LocalDate nearestExpiryDate,
            String supplierForNearestExpiry) {

        this.productId = productId;
        this.productName = productName;
        this.unit = unit;
        this.category = category;
        this.description = description;
        this.price = price;
        this.enabled = enabled;
        this.totalAvailableQuantity = totalAvailableQuantity;
        this.latestReceivedDate = latestReceivedDate;
        this.nearestExpiryDate = nearestExpiryDate;
        this.supplierForNearestExpiry = supplierForNearestExpiry;
    }

    // 無參建構子 (JPA要求)
    protected ProductInventoryStatus() {
        this.productId = null;
        this.productName = null;
        this.unit = null;
        this.category = null;
        this.description = null;
        this.price = null;
        this.enabled = null;
        this.totalAvailableQuantity = null;
        this.latestReceivedDate = null;
        this.nearestExpiryDate = null;
        this.supplierForNearestExpiry = null;
    }

    // getter
    public Integer getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getUnit() {
        return unit;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Boolean isEnabled() {  // 對於boolean類型更推薦用isXXX命名
        return enabled;
    }

    public Integer getTotalAvailableQuantity() {
        return totalAvailableQuantity;
    }

    public LocalDate getLatestReceivedDate() {
        return latestReceivedDate;
    }

    public LocalDate getNearestExpiryDate() {
        return nearestExpiryDate;
    }

    public String getSupplierForNearestExpiry() {
        return supplierForNearestExpiry;
    }

    // 可選：添加業務方法
    public boolean isLowStock() {
        return totalAvailableQuantity != null && totalAvailableQuantity < 10;
    }

    public boolean isNearExpiry() {
        return nearestExpiryDate != null
                && nearestExpiryDate.isBefore(LocalDate.now().plusWeeks(2));
    }

    @Override
    public String toString() {
        return "ProductInventoryStatus{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", totalAvailableQuantity=" + totalAvailableQuantity +
                '}';
    }
}