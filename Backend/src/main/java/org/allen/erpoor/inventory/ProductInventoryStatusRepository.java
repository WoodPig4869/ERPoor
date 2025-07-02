package org.allen.erpoor.inventory;

import org.allen.erpoor.inventory.entity.ProductInventoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductInventoryStatusRepository extends JpaRepository<ProductInventoryStatus, Integer> {
}
