package org.allen.erpoor.saleOrder;

import org.allen.erpoor.saleOrder.entity.SaleOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleOrderRepository extends JpaRepository<SaleOrder, Integer> {

    public Optional<SaleOrder> findByOrderId(Integer orderId);

    @Query("""
    SELECT s
    FROM SaleOrder s
    WHERE s.cancelledAt IS NULL
       OR s.cancelledAt >= :cutoffTime
    ORDER BY s.orderDate DESC
""")
    List<SaleOrder> findValidOrdersAfterCutoff(@Param("cutoffTime") LocalDateTime cutoffTime);

    @Query("""
SELECT COALESCE(SUM(s.totalAmount), 0)
FROM SaleOrder s
WHERE s.orderDate >= :startOfMonth
  AND s.orderDate < :startOfNextMonth
  AND s.orderStatus = 'shipped'
""")
    BigDecimal getMonthlySalesTotalShippedByOrderDate(
            @Param("startOfMonth") LocalDate startOfMonth,
            @Param("startOfNextMonth") LocalDate startOfNextMonth
    );


    @Query("""
    SELECT COUNT(s)
    FROM SaleOrder s
    WHERE s.orderDate >= :startOfWeek
      AND s.orderDate < :startOfNextWeek
""")
    Integer getWeeklyOrderCount(
            @Param("startOfWeek") LocalDate startOfWeek,
            @Param("startOfNextWeek") LocalDate startOfNextWeek
    );


}
