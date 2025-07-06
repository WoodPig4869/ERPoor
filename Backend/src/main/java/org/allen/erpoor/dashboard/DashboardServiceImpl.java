package org.allen.erpoor.dashboard;

import org.allen.erpoor.dashboard.entity.ExpiringBatch;
import org.allen.erpoor.inventory.ProductBatchRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService{

    private final ProductBatchRepository productBatchRepository;

    public DashboardServiceImpl(ProductBatchRepository productBatchRepository) {
        this.productBatchRepository = productBatchRepository;
    }

    @Override
    public List<ExpiringBatch> getExpiringBatche() {
        return productBatchRepository.getExpiringBatch();
    }
}
