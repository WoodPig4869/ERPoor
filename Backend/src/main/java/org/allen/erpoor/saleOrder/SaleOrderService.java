package org.allen.erpoor.saleOrder;

import org.allen.erpoor.saleOrder.entity.OrderStatus;
import org.allen.erpoor.saleOrder.entity.SaleOrder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SaleOrderService {

    private final SaleOrderRepository saleOrderRepository;

    public SaleOrderService(SaleOrderRepository saleOrderRepository) {
        this.saleOrderRepository = saleOrderRepository;
    }

    public SaleOrder save(SaleOrder saleOrder) {
        return saleOrderRepository.save(saleOrder);
    }

    public Optional<SaleOrder> findByOrderId(Integer orderId) {
        return saleOrderRepository.findByOrderId(orderId);
    }

    public List<SaleOrder> findAllOrders() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        return saleOrderRepository.findValidOrdersAfterCutoff(cutoff);
    }

    public boolean updateOrderStatus(Integer orderId, OrderStatus status) {
        Optional<SaleOrder> optionalOrder = saleOrderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            return false;
        }

        SaleOrder order = optionalOrder.get();
        order.setOrderStatus(status);
        saleOrderRepository.save(order);
        return true;
    }

    public boolean cancelOrder(Integer orderId, String reason) {
        Optional<SaleOrder> optionalOrder = saleOrderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            return false;
        }

        SaleOrder order = optionalOrder.get();

        // 假設你有 enum 狀態欄位 OrderStatus 並限制只能取消 pending 狀態
        if (order.getOrderStatus() == OrderStatus.cancelled) {
            return false; // 已取消不能再取消
        }

        order.setOrderStatus(OrderStatus.cancelled);
        order.setCancelledReason(reason);
        saleOrderRepository.save(order);
        return true;
    }

}
