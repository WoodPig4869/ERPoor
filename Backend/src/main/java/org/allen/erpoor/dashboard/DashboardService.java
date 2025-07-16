package org.allen.erpoor.dashboard;


import org.allen.erpoor.dashboard.entity.ExpiryAlertView;
import org.allen.erpoor.dashboard.entity.LowStockAlertView;

import java.util.List;

/**
 * 提供儀表板相關的服務操作，例如取得即將過期商品、低庫存商品、熱銷商品與統計指標。
 */
public interface DashboardService {

    /**
     * 查詢即將過期的商品批次清單。
     *
     * @return 回傳 {@link ExpiryAlertView} 清單，若無資料則為空列表。
     */
    List<ExpiryAlertView> getExpiringBatches();

    /**
     * 查詢低庫存商品清單。
     *
     * @return 回傳 {@link LowStockAlertView} 清單，若無資料則為空列表。
     */
    List<LowStockAlertView> getLowStockItems();

//    /**
//     * 查詢熱銷商品排行。
//     *
//     * @return 回傳 {@link HotItem} 清單，通常依照銷售數量遞減排序。
//     */
//    List<HotItem> getHotItems();
//
//    /**
//     * 查詢儀表板統計目標值，例如銷售總額、訂單數量、低庫存數、即將過期批次數等。
//     *
//     * @return 回傳 {@link TargetValues} 物件。
//     */
//    TargetValues getTargetValues();
}