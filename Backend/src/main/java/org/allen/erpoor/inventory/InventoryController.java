package org.allen.erpoor.inventory;

import org.allen.erpoor.inventory.entity.*;
import org.allen.erpoor.util.CommonResponse;
import org.allen.erpoor.util.SetAppUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);
    private final ProductInventoryViewRepository productInventoryViewRepository;
    private final ProductRepository productRepository;
    private final ProductBatchRepository productBatchRepository;
    private final InventoryCheckLogRepository inventoryCheckLogRepository;

    public InventoryController(ProductInventoryViewRepository productInventoryViewRepository, ProductRepository productRepository, ProductBatchRepository productBatchRepository, InventoryCheckLogRepository inventoryCheckLogRepository) {
        this.productInventoryViewRepository = productInventoryViewRepository;
        this.productRepository = productRepository;
        this.productBatchRepository = productBatchRepository;
        this.inventoryCheckLogRepository = inventoryCheckLogRepository;
    }

    @GetMapping("/productInventoryView")
    public ResponseEntity<CommonResponse<Iterable<ProductInventoryView>>> findAll() {
        logger.debug("收到商品庫存狀態查詢請求");
        try {
            Iterable<ProductInventoryView> inventoryStatus = productInventoryViewRepository.findAll();

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

    @GetMapping("/{productId}")
    public ResponseEntity<CommonResponse<Iterable<ProductBatch>>> findByProductId(@PathVariable Integer productId) {
        logger.debug("收到查詢商品庫存請求,商品 ID: {}", productId);
        Iterable<ProductBatch> batches;
        try {
            batches = productBatchRepository.findByProductIdAndQuantityGreaterThan(productId,0);
        } catch (Exception e) {
            logger.error("查詢商品庫存時發生錯誤: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponse.error(500, "查詢庫存失敗"));
        }
        return ResponseEntity.ok(new CommonResponse<>(200, "查詢成功", batches));
    }

    @PostMapping("/addProduct")
    public ResponseEntity<CommonResponse<String>> addProduct(@RequestBody Product product) {
        logger.debug("收到新增商品請求{}", product);
        try {
            Product newProduct = new Product();
            newProduct.setName(product.getName());
            newProduct.setCategory(product.getCategory());
            newProduct.setUnit(product.getUnit());
            newProduct.setDescription(product.getDescription());
            newProduct.setPrice(product.getPrice());
            productRepository.save(product);
            logger.info("新增商品成功");
            return ResponseEntity.ok(new CommonResponse<>(200, "新增成功", newProduct.getName()));
        } catch (Exception e) {
            logger.error("新增商品庫存時發生錯誤: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponse.error(500, "新增商品失敗"));
        }
    }

    @GetMapping("/getProductNameOptions")
    public ResponseEntity<CommonResponse<Iterable<ProductOption>>> getProductNameOptions() {
        logger.debug("收到商品名稱選項查詢請求");
        try {
            Iterable<ProductOption> productNames = productRepository.findAllProductOptions();
            logger.info("成功查詢商品名稱選項");
            return ResponseEntity.ok(new CommonResponse<>(200, "查詢成功", productNames));
        } catch (Exception e) {
            logger.error("查詢商品名稱選項時發生錯誤: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponse.error(500, "查詢商品名稱選項失敗"));
        }
    }

    @SetAppUser
    @PostMapping("/purchase")
    public ResponseEntity<CommonResponse<String>> addPurchase(@RequestBody ProductBatch batch) {
        logger.debug("收到新增進貨請求: {}", batch);
        try {
            ProductBatch newBatch = getProductBatch(batch);

            productBatchRepository.save(newBatch);

            logger.info("新增進貨成功，商品 ID: {}", newBatch.getProductId());
            return ResponseEntity.ok(new CommonResponse<>(200, "新增進貨成功", "商品 ID: " + newBatch.getProductId()));
        } catch (Exception e) {
            logger.error("新增進貨時發生錯誤: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponse.error(500, "新增進貨失敗"));
        }
    }

    private static ProductBatch getProductBatch(ProductBatch batch) {
        ProductBatch newBatch = new ProductBatch();
        newBatch.setProductId(batch.getProductId());
        newBatch.setQuantity(batch.getQuantity());
        newBatch.setExpirationDate(batch.getExpirationDate());
        newBatch.setPurchasePrice(batch.getPurchasePrice());
        newBatch.setReceivedDate(batch.getReceivedDate());
        newBatch.setSupplierId(batch.getSupplierId());
        newBatch.setSupplierName(batch.getSupplierName());
        return newBatch;
    }

    @SetAppUser
    @PostMapping("/inventory-check")
    public ResponseEntity<CommonResponse<String>> addInventoryCheck(@RequestBody InventoryCheckLog checkLog) {
        logger.debug("收到新增盤點請求: {}", checkLog);
        try {
            // 儲存盤點記錄
            inventoryCheckLogRepository.save(checkLog);

            logger.info("新增盤點成功，商品: {}", checkLog.getProductName());
            return ResponseEntity.ok(
                    new CommonResponse<>(200, "新增盤點成功", "商品: " + checkLog.getProductName())
            );
        } catch (Exception e) {
            logger.error("新增盤點時發生錯誤: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponse.error(500, "新增盤點失敗"));
        }
    }

    @GetMapping("/check-log")
    public ResponseEntity<CommonResponse<List<InventoryCheckLog>>> getCheckLogs() {
        logger.debug("收到查詢盤點紀錄請求");

        try {
            List<InventoryCheckLog> logs = inventoryCheckLogRepository.findAll();

            if (logs.isEmpty()) {
                logger.info("未找到任何盤點紀錄");
                return ResponseEntity.ok(new CommonResponse<>(200, "目前尚無盤點紀錄", logs));
            }

            logger.info("查詢成功，共 {} 筆盤點紀錄", logs.size());
            return ResponseEntity.ok(new CommonResponse<>(200, "查詢成功", logs));

        } catch (Exception e) {
            logger.error("查詢盤點紀錄時發生錯誤: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponse.error(500, "查詢盤點紀錄失敗"));
        }
    }


}