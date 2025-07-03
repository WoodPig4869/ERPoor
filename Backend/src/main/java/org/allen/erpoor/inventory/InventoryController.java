package org.allen.erpoor.inventory;

import org.allen.erpoor.inventory.entity.ProductInventoryStatus;
import org.allen.erpoor.util.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);
    private final ProductInventoryStatusRepository productInventoryStatusRepository;

    public InventoryController(ProductInventoryStatusRepository productInventoryStatusRepository) {
        this.productInventoryStatusRepository = productInventoryStatusRepository;
    }

    @GetMapping("/status")
    public ResponseEntity<CommonResponse<Iterable<ProductInventoryStatus>>> findAll() {
        logger.debug("收到商品庫存狀態查詢請求");
        try {
            Iterable<ProductInventoryStatus> inventoryStatus = productInventoryStatusRepository.findAll();

            if (!inventoryStatus.iterator().hasNext()) {
                logger.info("未找到任何商品庫存記錄");
                return ResponseEntity.ok(new CommonResponse<>(200, "未找到庫存記錄", inventoryStatus));
            }

            logger.info("成功查詢商品庫存狀態，共 {} 筆記錄", ((Collection<?>) inventoryStatus).size());
            return ResponseEntity.ok(new CommonResponse<>(200, "查詢成功", inventoryStatus));

        } catch (Exception e) {
            logger.error("查詢商品庫存狀態時發生錯誤: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponse.error(500, "查詢庫存狀態失敗"));
        }
    }
}
