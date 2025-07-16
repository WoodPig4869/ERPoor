package org.allen.erpoor.saleOrder;

import org.allen.erpoor.inventory.ProductRepository;
import org.allen.erpoor.inventory.entity.Product;
import org.allen.erpoor.saleOrder.entity.OrderStatus;
import org.allen.erpoor.saleOrder.entity.SaleOrder;
import org.allen.erpoor.saleOrder.entity.OrderItem;
import org.allen.erpoor.util.CommonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/saleOrder")
public class SaleOrderController {

    private static final Logger logger = LoggerFactory.getLogger(SaleOrderController.class);
    private final SaleOrderService saleOrderService;
    private final ProductRepository productRepository;

    public SaleOrderController(SaleOrderService saleOrderService, ProductRepository productRepository) {
        this.saleOrderService = saleOrderService;
        this.productRepository = productRepository;
    }

    @PostMapping
    public ResponseEntity<CommonResponse<SaleOrder>> save(@RequestBody SaleOrder saleOrder) {
        logger.debug("收到儲存訂單請求: {}", saleOrder);
        try {
            // 反向綁定，確保每個 orderItem 都知道它隸屬哪張訂單
            for (OrderItem item : saleOrder.getOrderItems()) {
                item.setSaleOrder(saleOrder);
            }

            SaleOrder savedOrder = saleOrderService.save(saleOrder);
            logger.info("成功儲存訂單，訂單編號: {}", savedOrder.getOrderId());
            return ResponseEntity.ok(new CommonResponse<>(200, "訂單儲存成功", savedOrder));
        } catch (Exception e) {
            logger.error("儲存訂單時發生錯誤: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponse.error(500, "儲存訂單失敗"));
        }
    }


    // 透過 orderId 查詢 SaleOrder
    @GetMapping("/{orderId}")
    public ResponseEntity<CommonResponse<SaleOrder>> findByOrderId(@PathVariable Integer orderId) {
        logger.debug("收到查詢訂單請求，訂單編號: {}", orderId);
        try {
            Optional<SaleOrder> optionalOrder = saleOrderService.findByOrderId(orderId);

            if (optionalOrder.isEmpty()) {
                logger.info("未找到訂單，訂單編號: {}", orderId);
                return ResponseEntity.ok(new CommonResponse<>(200, "未找到訂單", null));
            }

            logger.info("成功查詢訂單，訂單編號: {}", orderId);
            return ResponseEntity.ok(new CommonResponse<>(200, "查詢成功", optionalOrder.get()));

        } catch (Exception e) {
            logger.error("查詢訂單時發生錯誤: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponse.error(500, "查詢訂單失敗"));
        }
    }

    @GetMapping("/products")
    public ResponseEntity<CommonResponse<List<Product>>> findByEnabled() {
        logger.debug("收到查詢產品請求");
        try {
            List<Product> products = productRepository.findByEnabled(true);

            if (products.isEmpty()) {
                logger.info("未找到符合條件的產品");
                return ResponseEntity.ok(new CommonResponse<>(200, "未找到產品", Collections.emptyList()));
            }

            logger.info("成功查詢產品，數量: {}", products.size());
            return ResponseEntity.ok(new CommonResponse<>(200, "查詢成功", products));

        } catch (Exception e) {
            logger.error("查詢產品時發生錯誤: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponse.error(500, "查詢產品失敗"));
        }
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<SaleOrder>>> findAllOrders() {
        logger.debug("收到查詢全部訂單請求");
        try {
            List<SaleOrder> orders = saleOrderService.findAllOrders();

            if (orders.isEmpty()) {
                logger.info("查無訂單資料");
                return ResponseEntity.ok(new CommonResponse<>(200, "沒有訂單資料", orders));
            }

            logger.info("成功查詢 {} 筆訂單", orders.size());
            return ResponseEntity.ok(new CommonResponse<>(200, "查詢成功", orders));

        } catch (Exception e) {
            logger.error("查詢全部訂單時發生錯誤: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponse.error(500, "查詢全部訂單失敗"));
        }
    }

    public static class StatusChangeRequest {
        public OrderStatus status;
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<CommonResponse<String>> updateOrderStatus(
            @PathVariable Integer orderId,
            @RequestBody StatusChangeRequest request) {

        try {
            boolean updated = saleOrderService.updateOrderStatus(orderId, request.status);

            if (!updated) {
                return ResponseEntity.ok(new CommonResponse<>(404, "訂單不存在", null));
            }

            return ResponseEntity.ok(new CommonResponse<>(200, "狀態更新成功", null));

        } catch (DataAccessException e) {
            String fullMessage = e.getRootCause() != null
                    ? e.getRootCause().getMessage()
                    : "資料庫錯誤";

            // 只保留第一行，去除 PL/pgSQL function 堆疊資訊
            String cleanMessage = fullMessage.split("\n")[0];

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CommonResponse.error(400, cleanMessage));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponse.error(500, "更新失敗"));
        }
    }


    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<CommonResponse<String>> cancelOrder(
            @PathVariable Integer orderId,
            @RequestBody CancelRequest request) {

        try {
            boolean cancelled = saleOrderService.cancelOrder(orderId, request.reason);

            if (!cancelled) {
                return ResponseEntity.ok(new CommonResponse<>(404, "取消失敗，訂單不存在或已取消", null));
            }

            return ResponseEntity.ok(new CommonResponse<>(200, "訂單已成功取消", null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponse.error(500, "取消訂單失敗"));
        }
    }

    public static class CancelRequest {
        public String reason;
    }
}