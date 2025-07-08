package org.allen.erpoor.dashboard;

import org.allen.erpoor.dashboard.entity.ExpiryAlertView;
import org.allen.erpoor.dashboard.entity.LowStockAlertView;
import org.allen.erpoor.inventory.ProductBatchRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService{

    private final ExpiryAlertViewRepository expiryAlertViewRepository;
    private final LowStockAlertViewRepository lowStockAlertViewRepository;

    public DashboardServiceImpl(ExpiryAlertViewRepository expiryAlertViewRepository, LowStockAlertViewRepository lowStockAlertViewRepository) {
        this.expiryAlertViewRepository = expiryAlertViewRepository;
        this.lowStockAlertViewRepository = lowStockAlertViewRepository;
    }

    @Override
    public List<ExpiryAlertView> getExpiringBatches() {
        return expiryAlertViewRepository.findAll();
    }

    @Override
    public List<LowStockAlertView> getLowStockItems() {
        return lowStockAlertViewRepository.findAll();
    }


}
