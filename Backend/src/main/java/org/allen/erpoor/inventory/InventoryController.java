package org.allen.erpoor.inventory;

import org.allen.erpoor.inventory.entity.Product;
import org.allen.erpoor.inventory.entity.ProductBatch;
import org.allen.erpoor.inventory.entity.ProductInventoryView;
import org.allen.erpoor.inventory.entity.ProductOption;
import org.allen.erpoor.util.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);
    private final ProductInventoryViewRepository productInventoryViewRepository;
    private final ProductRepository productRepository;
    private final ProductBatchRepository productBatchRepository;

    public InventoryController(ProductInventoryViewRepository productInventoryViewRepository, ProductRepository productRepository, ProductBatchRepository productBatchRepository) {
        this.productInventoryViewRepository = productInventoryViewRepository;
        this.productRepository = productRepository;
        this.productBatchRepository = productBatchRepository;
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
        newBatch.setBatchCode(batch.getBatchCode());
        newBatch.setQuantity(batch.getQuantity());
        newBatch.setExpirationDate(batch.getExpirationDate());
        newBatch.setPurchasePrice(batch.getPurchasePrice());
        newBatch.setReceivedDate(batch.getReceivedDate());
        newBatch.setSupplierId(batch.getSupplierId());
        newBatch.setSupplierName(batch.getSupplierName());
        return newBatch;
    }
}