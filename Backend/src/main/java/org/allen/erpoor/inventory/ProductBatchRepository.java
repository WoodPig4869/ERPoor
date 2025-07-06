package org.allen.erpoor.inventory;

import org.allen.erpoor.dashboard.entity.ExpiringBatch;
import org.allen.erpoor.inventory.entity.ProductBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductBatchRepository extends JpaRepository<ProductBatch, Long> {

    @Query(value = """
    SELECT 
        pb.batch_id AS batchId,
        p.name AS product,
        pb.expiration_date AS expirationDate,
        pb.quantity AS quantity
    FROM product_batch pb
    JOIN product p ON pb.product_id = p.product_id
    WHERE pb.expiration_date <= CURRENT_DATE + p.expiry_alert_days * INTERVAL '1 day'
      AND pb.quantity > 0
      AND p.enabled = TRUE
    ORDER BY pb.expiration_date ASC
    """, nativeQuery = true)
    List<ExpiringBatch> getExpiringBatch();


}
