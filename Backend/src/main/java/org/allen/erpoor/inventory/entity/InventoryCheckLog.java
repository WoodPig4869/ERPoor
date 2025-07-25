package org.allen.erpoor.inventory.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_check_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryCheckLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(name = "expected_quantity", nullable = false)
    private Integer expectedQuantity;

    @Column(name = "actual_quantity", nullable = false)
    private Integer actualQuantity;

    // 這個欄位是 GENERATED ALWAYS AS，在 Java 中不用寫入，只需標為讀取用
    @Column(name = "difference", insertable = false, updatable = false)
    private Integer difference;

    @Column(name = "created_by", length = 50, updatable = false)
    private String createdBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}