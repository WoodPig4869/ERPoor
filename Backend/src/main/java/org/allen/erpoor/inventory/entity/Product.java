package org.allen.erpoor.inventory.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Product 類別，映射到資料庫中的 'product' 資料表。
 * 用於表示商品基本資訊。
 */
@Data
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

    @Column(name = "expiry_alert_days", nullable = false)
    private Integer expiry_alert_days; //到期的預警天數

    @Column(name="min_stock",nullable = false)
    private Integer min_stock; //最小庫存/

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // 商品描述

    @Column(name = "enabled")
    private Boolean enabled; // 是否啟用

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price; // 價格

    @Column(name = "created_at")
    private Timestamp createdAt; // 建立時間
    /**
     * 在實體持久化之前設定建立時間。
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

}
