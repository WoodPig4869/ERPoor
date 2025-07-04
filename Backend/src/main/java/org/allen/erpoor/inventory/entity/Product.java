package org.allen.erpoor.inventory.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Product 類別，映射到資料庫中的 'product' 資料表。
 * 用於表示商品基本資訊。
 */
@Entity // 標記此類別為 JPA 實體
@Table(name = "product", indexes = {
        @Index(name = "idx_product_name", columnList = "name")
}) // 映射到 'product' 資料表
public class Product {

    @Id // 主鍵
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id") // 對應資料表欄位
    private Long productId; // 商品編號

    @Column(name = "name", nullable = false, length = 100)
    private String name; // 商品名稱

    @Column(name = "category", length = 50)
    private String category; // 分類

    @Column(name = "unit", length = 20)
    private String unit; // 單位

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // 商品描述

    @Column(name = "enabled")
    private Boolean enabled; // 是否啟用

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price; // 價格

    @Column(name = "created_at")
    private Timestamp createdAt; // 建立時間

    /**
     * 預設建構子。
     * 設定 enabled 預設為 true。
     */
    public Product() {
        this.enabled = true; // 預設為啟用狀態
    }

    /**
     * 在實體持久化之前設定建立時間。
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    // --- Getter 和 Setter 方法 ---

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", unit='" + unit + '\'' +
                ", description='" + description + '\'' +
                ", enabled=" + enabled +
                ", price=" + price +
                ", createdAt=" + createdAt +
                '}';
    }
}
