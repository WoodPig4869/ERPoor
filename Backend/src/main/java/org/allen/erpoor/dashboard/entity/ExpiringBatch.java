package org.allen.erpoor.dashboard.entity;


import java.time.LocalDate;

public interface ExpiringBatch {
    Integer getBatchId();
    String getProduct();
    LocalDate getExpirationDate();
    Integer getQuantity();
}
