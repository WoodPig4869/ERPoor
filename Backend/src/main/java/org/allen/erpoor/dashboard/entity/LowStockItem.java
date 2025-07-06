package org.allen.erpoor.dashboard.entity;

public interface LowStockItem {
    Integer getProductId();
    String getProductName();
    Integer getCurrentStock();
    Integer getMinStock();
    String getUnit();
}
