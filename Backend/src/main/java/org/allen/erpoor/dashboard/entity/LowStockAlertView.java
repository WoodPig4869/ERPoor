package org.allen.erpoor.dashboard.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Getter
@ToString
@NoArgsConstructor
@Table(name = "low_stock_alert_view")
public class LowStockAlertView {

    @Id
    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "name")
    private String name;

    @Column(name = "min_stock")
    private Integer minStock;

    @Column(name = "available_stock")
    private Integer availableStock;

    @Column(name = "shortage")
    private Integer shortage;
}
