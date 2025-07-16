package org.allen.erpoor.saleOrder;

import org.allen.erpoor.saleOrder.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
}
