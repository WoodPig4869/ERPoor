package org.allen.erpoor.dashboard;

import org.allen.erpoor.dashboard.entity.LowStockAlertView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LowStockAlertViewRepository extends JpaRepository<LowStockAlertView, Long> {
}
