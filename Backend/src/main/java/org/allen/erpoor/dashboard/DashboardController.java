package org.allen.erpoor.dashboard;

import org.allen.erpoor.dashboard.entity.ExpiringBatch;
import org.allen.erpoor.dashboard.entity.LowStockItem;
import org.allen.erpoor.inventory.InventoryController;
import org.allen.erpoor.util.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
public class DashboardController {
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/expiringBatch")
    public ResponseEntity<CommonResponse<List<ExpiringBatch>>> getExpiringBatches() {
        logger.debug("收到即將過期商品批次查詢請求");

        try {
            List<ExpiringBatch> expiringBatches = dashboardService.getExpiringBatche();

            if (expiringBatches == null || expiringBatches.isEmpty()) {
                logger.info("查無即將過期商品批次");
                return ResponseEntity.ok(new CommonResponse<>(200, "未找到即將過期商品", expiringBatches));
            }

            logger.info("成功查詢即將過期商品，共 {} 筆記錄", expiringBatches.size());
            return ResponseEntity.ok(new CommonResponse<>(200, "查詢成功", expiringBatches));

        } catch (Exception e) {
            logger.error("查詢即將過期商品批次時發生錯誤: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponse.error(500, "查詢即將過期商品失敗"));
        }
    }

    @GetMapping("/lowStockItems")
    public ResponseEntity<CommonResponse<List<LowStockItem>>> getLowStockItems() {
        logger.debug("收到低庫存商品查詢請求");

        try {
            List<LowStockItem> lowStockItems = dashboardService.getLowStockItems();

            if (lowStockItems == null || lowStockItems.isEmpty()) {
                logger.info("查無低庫存商品");
                return ResponseEntity.ok(new CommonResponse<>(200, "目前無低庫存商品", lowStockItems));
            }

            logger.info("成功查詢低庫存商品，共 {} 筆記錄", lowStockItems.size());
            return ResponseEntity.ok(new CommonResponse<>(200, "查詢成功", lowStockItems));

        } catch (Exception e) {
            logger.error("查詢低庫存商品時發生錯誤: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponse.error(500, "查詢低庫存商品失敗"));
        }
    }

}
