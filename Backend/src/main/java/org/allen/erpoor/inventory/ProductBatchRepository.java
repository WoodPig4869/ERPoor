package org.allen.erpoor.inventory;

import org.allen.erpoor.inventory.entity.ProductBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductBatchRepository extends JpaRepository<ProductBatch, Long> {

    public List<ProductBatch> findByProductId(Integer productId);

}
