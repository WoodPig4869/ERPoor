package org.allen.erpoor.inventory;

import org.allen.erpoor.inventory.entity.ProductBatch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductBatchRepository extends JpaRepository<ProductBatch, Long> {
}
