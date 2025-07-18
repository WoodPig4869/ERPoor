package org.allen.erpoor.dashboard;

import lombok.Data;
import org.allen.erpoor.dashboard.entity.ExpiryAlertView;
import org.allen.erpoor.dashboard.entity.LowStockAlertView;
import org.allen.erpoor.saleOrder.SaleOrderRepository;
import org.allen.erpoor.util.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    private final DashboardService dashboardService;
    private final SaleOrderRepository saleOrderRepository;

    public DashboardController(DashboardService dashboardService, SaleOrderRepository saleOrderRepository) {
        this.dashboardService = dashboardService;
        this.saleOrderRepository = saleOrderRepository;
    }

    @GetMapping("/expiringBatch")
    public ResponseEntity<CommonResponse<List<ExpiryAlertView>>> getExpiringBatches() {
        logger.debug("收到即將過期商品批次查詢請求");

        try {
            List<ExpiryAlertView> expiringBatches = dashboardService.getExpiringBatches();

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
    public ResponseEntity<CommonResponse<List<LowStockAlertView>>> getLowStockItems() {
        logger.debug("收到低庫存商品查詢請求");

        try {
            List<LowStockAlertView> lowStockItems = dashboardService.getLowStockItems();

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

    @GetMapping("/overview")
    public ResponseEntity<CommonResponse<Overview>> getOverview() {
        logger.debug("收到儀表板概覽資料查詢請求");

        try {
            List<LowStockAlertView> lowStockItems = dashboardService.getLowStockItems();
            List<ExpiryAlertView> expiringBatches = dashboardService.getExpiringBatches();

            LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
            LocalDate startOfNextMonth = startOfMonth.plusMonths(1);

            BigDecimal monthlySales = saleOrderRepository.getMonthlySalesTotalShippedByOrderDate(
                    startOfMonth,
                    startOfNextMonth
            );

            LocalDate startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY);
            LocalDate startOfNextWeek = startOfWeek.plusWeeks(1);
            int count = saleOrderRepository.getWeeklyOrderCount(startOfWeek, startOfNextWeek);


            Overview overview = new Overview();
            overview.setLowStockItems(lowStockItems);
            overview.setExpiringBatches(expiringBatches);
            overview.setMonthlySales(monthlySales);
            overview.setWeeklyOrderCount(count);

            logger.info("儀表板資料查詢成功：低庫存 {} 筆、即將過期 {} 筆",
                    lowStockItems.size(), expiringBatches.size());

            return ResponseEntity.ok(new CommonResponse<>(200, "查詢成功", overview));

        } catch (Exception e) {
            logger.error("查詢儀表板資料時發生錯誤: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponse.error(500, "查詢儀表板資料失敗"));
        }
    }


}
@Data
class Overview {
    private List<LowStockAlertView> lowStockItems;
    private List<ExpiryAlertView> expiringBatches;
    private BigDecimal monthlySales;
    private int weeklyOrderCount;
}
