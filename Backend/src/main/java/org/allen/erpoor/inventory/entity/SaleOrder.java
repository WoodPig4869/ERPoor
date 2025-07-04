package org.allen.erpoor.inventory.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * SaleOrder 類別，映射到資料庫中的 'sale_order' 資料表。
 * 用於紀錄客戶訂單資訊。
 */
@Entity
@Table(name = "sale_order", indexes = {
        @Index(name = "idx_order_order_date", columnList = "order_date")
})
public class SaleOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId; // 訂單唯一識別碼

    @Column(name = "order_date", nullable = false)
    private Date orderDate; // 訂單日期

    @Column(name = "customer_name", length = 100)
    private String customerName; // 客戶名稱

    @Column(name = "total_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO; // 訂單總金額

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt; // 建立時間

    /**
     * 預設建構子。
     */
    public SaleOrder() {
    }

    /**
     * 在實體持久化之前設定建立時間。
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    // --- Getter 和 Setter 方法 ---

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "SaleOrder{" +
                "orderId=" + orderId +
                ", orderDate=" + orderDate +
                ", customerName='" + customerName + '\'' +
                ", totalAmount=" + totalAmount +
                ", createdAt=" + createdAt +
                '}';
    }
}
