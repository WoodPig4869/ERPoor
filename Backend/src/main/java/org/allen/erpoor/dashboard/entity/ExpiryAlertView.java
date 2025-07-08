package org.allen.erpoor.dashboard.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;

@Entity
@Immutable
@Getter
@ToString
@NoArgsConstructor
@Table(name = "expiry_alert_view")
public class ExpiryAlertView {

    @Id
    @Column(name = "batch_id")
    private Integer batchId;

    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "name")
    private String name;

    @Column(name = "batch_code")
    private String batchCode;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "supplier_name")
    private String supplierName;

    @Column(name = "days_to_expiry")
    private Integer daysToExpiry;
}
