package org.allen.erpoor.inventory;

import org.allen.erpoor.inventory.entity.ProductInventoryView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductInventoryViewRepository extends JpaRepository<ProductInventoryView, Integer> {
}
