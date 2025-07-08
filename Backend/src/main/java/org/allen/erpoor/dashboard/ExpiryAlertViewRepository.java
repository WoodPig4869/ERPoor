package org.allen.erpoor.dashboard;

import org.allen.erpoor.dashboard.entity.ExpiryAlertView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpiryAlertViewRepository extends JpaRepository<ExpiryAlertView, Long> {
}
