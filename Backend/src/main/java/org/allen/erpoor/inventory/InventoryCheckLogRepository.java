package org.allen.erpoor.inventory;

import org.allen.erpoor.inventory.entity.InventoryCheckLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryCheckLogRepository extends JpaRepository<InventoryCheckLog, Long> {
}
