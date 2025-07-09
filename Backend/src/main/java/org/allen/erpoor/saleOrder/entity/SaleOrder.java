package org.allen.erpoor.saleOrder.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "sale_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    @Column(name = "order_number", nullable = false, unique = true, length = 20)
    private String orderNumber;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @Column(name = "customer_phone", length = 20)
    private String customerPhone;

    @Column(name = "customer_email", length = 100)
    private String customerEmail;

    @Column(name = "customer_address")
    private String customerAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false, columnDefinition = "order_status default 'pending'")
    private OrderStatus orderStatus = OrderStatus.pending;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "notes")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancelled_reason")
    private String cancelledReason;

    @OneToMany(mappedBy = "saleOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;
}

enum OrderStatus {
    pending,
    shipped,
    cancelled;
}