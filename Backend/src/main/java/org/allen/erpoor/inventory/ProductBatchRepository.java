package org.allen.erpoor.inventory;

import org.allen.erpoor.inventory.entity.ProductBatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductBatchRepository extends JpaRepository<ProductBatch, Long> {

    public List<ProductBatch> findByProductIdAndQuantityGreaterThan(Integer productId, Integer quantity);

}
