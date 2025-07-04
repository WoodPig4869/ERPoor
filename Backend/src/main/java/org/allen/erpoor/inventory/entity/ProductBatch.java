package org.allen.erpoor.inventory.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * ProductBatch 類別，映射到資料庫中的 'product_batch' 資料表。
 * 用於紀錄每批商品的進貨資訊與效期。
 */
@Entity
@Table(name = "product_batch", indexes = {
        @Index(name = "idx_product_batch_product_id_expiration_date", columnList = "product_id, expiration_date"),
        @Index(name = "idx_product_batch_product_id_expiry", columnList = "product_id, expiration_date"),
        @Index(name = "idx_product_batch_product_id_received", columnList = "product_id, received_date")
})
public class ProductBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id")
    private Long batchId; // 批次唯一識別碼

    @Column(name = "product_id", nullable = false)
    private Integer productId; // 對應商品 ID（ Product 表 ）

    @Column(name = "batch_code", length = 50)
    private String batchCode; // 批號

    @Column(name = "quantity", nullable = false)
    private Integer quantity; // 進貨數量

    @Column(name = "expiration_date", nullable = false)
    private Date expirationDate; // 有效日期

    @Column(name = "purchase_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal purchasePrice = BigDecimal.ZERO; // 進貨單價

    @Column(name = "received_date", nullable = false)
    private Date receivedDate; // 進貨日期

    @Column(name = "supplier_id")
    private Integer supplierId; // 供應商 ID

    @Column(name = "supplier_name", nullable = false, length = 100)
    private String supplierName; // 供應商名稱

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt; // 建立時間

    /**
     * 預設建構子。
     */
    public ProductBatch() {
    }

    /**
     * 在實體持久化之前設定建立時間。
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    // --- Getter 和 Setter 方法 ---

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "ProductBatch{" +
                "batchId=" + batchId +
                ", productId=" + productId +
                ", batchCode='" + batchCode + '\'' +
                ", quantity=" + quantity +
                ", expirationDate=" + expirationDate +
                ", purchasePrice=" + purchasePrice +
                ", receivedDate=" + receivedDate +
                ", supplierId=" + supplierId +
                ", supplierName='" + supplierName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
