<template>
  <q-page class="dashboard-page q-pa-md">
    <div class="q-gutter-md">
      <!-- 統計卡片區 -->
      <div class="row q-col-gutter-md">
        <div class="col-xs-12 col-sm-6 col-md-3">
          <q-card class="stat-card bg-blue-2" flat bordered>
            <q-card-section>
              <div class="row items-center q-gutter-md">
                <q-icon name="trending_up" size="md" class="text-blue-7" />
                <div>
                  <div class="text-subtitle2 text-grey-7">本月銷售總額</div>
                  <div class="stat-value text-blue-8 q-mt-xs">
                    ${{ formatNumber(animatedValues.sales) }}
                  </div>
                </div>
              </div>
            </q-card-section>
          </q-card>
        </div>

        <div class="col-xs-12 col-sm-6 col-md-3">
          <q-card class="stat-card bg-red-2" flat bordered>
            <q-card-section>
              <div class="row items-center q-gutter-md">
                <q-icon name="inventory_2" size="md" class="text-red-7" />
                <div>
                  <div class="text-subtitle2 text-grey-7">低庫存商品</div>
                  <div class="stat-value text-red-8 q-mt-xs">
                    {{ Math.floor(animatedValues.lowStock) }} 項
                  </div>
                </div>
              </div>
            </q-card-section>
          </q-card>
        </div>

        <div class="col-xs-12 col-sm-6 col-md-3">
          <q-card class="stat-card bg-yellow-2" flat bordered>
            <q-card-section>
              <div class="row items-center q-gutter-md">
                <q-icon name="schedule" size="md" class="text-yellow-9" />
                <div>
                  <div class="text-subtitle2 text-grey-7">即將過期</div>
                  <div class="stat-value text-yellow-9 q-mt-xs">
                    {{ Math.floor(animatedValues.expiring) }} 批
                  </div>
                </div>
              </div>
            </q-card-section>
          </q-card>
        </div>

        <div class="col-xs-12 col-sm-6 col-md-3">
          <q-card class="stat-card bg-green-1" flat bordered>
            <q-card-section>
              <div class="row items-center q-gutter-md">
                <q-icon name="receipt" size="md" class="text-green-7" />
                <div>
                  <div class="text-subtitle2 text-grey-7">本週訂單數</div>
                  <div class="stat-value text-green-8 q-mt-xs">
                    {{ Math.floor(animatedValues.orders) }} 張
                  </div>
                </div>
              </div>
            </q-card-section>
          </q-card>
        </div>
      </div>

      <!-- 快速操作按鈕 -->
      <!-- <div class="row q-col-gutter-md q-mt-md">
        <div class="col-12">
          <q-card flat bordered class="bg-grey-1">
            <q-card-section>
              <div class="text-h6 text-grey-8 q-mb-md">
                <q-icon name="flash_on" class="q-mr-sm" />
                快速操作
              </div>
              <div class="row q-col-gutter-sm">
                <div class="col-auto">
                  <q-btn
                    color="primary"
                    icon="add_shopping_cart"
                    label="新增進貨"
                    unelevated
                    class="q-px-lg"
                  />
                </div>
                <div class="col-auto">
                  <q-btn
                    color="secondary"
                    icon="sell"
                    label="銷售出貨"
                    unelevated
                    class="q-px-lg"
                  />
                </div>
                <div class="col-auto">
                  <q-btn
                    color="accent"
                    icon="inventory"
                    label="庫存盤點"
                    unelevated
                    class="q-px-lg"
                  />
                </div>
                <div class="col-auto">
                  <q-btn
                    color="info"
                    icon="assessment"
                    label="生成報告"
                    unelevated
                    class="q-px-lg"
                  />
                </div>
              </div>
            </q-card-section>
          </q-card>
        </div>
      </div> -->

      <!-- 即將過期商品表格 -->
      <q-card class="q-mt-md bg-yellow-1" flat bordered>
        <q-card-section>
          <div class="text-h6 text-yellow-9 row items-center">
            <q-icon name="warning" class="q-mr-sm" />
            即將過期商品批次
            <q-space />
            <q-chip
              color="yellow-8"
              text-color="white"
              :label="`${expiringBatches.length} 批次`"
              dense
            />
          </div>
        </q-card-section>
        <q-separator />
        <q-table
          flat
          dense
          :rows="expiringBatches"
          :columns="expiringColumns"
          row-key="batchCode"
          class="expiring-table"
          :rows-per-page-options="[5, 10, 20]"
          :pagination="{ rowsPerPage: 5 }"
          no-data-label="暫無即將過期商品"
          :loading="loading"
        >
          <template v-slot:body-cell-expirationDate="props">
            <q-td :props="props">
              <q-chip
                :color="getExpirationColor(props.value)"
                text-color="white"
                dense
                :label="formatDate(props.value)"
                :icon="props.row.daysToExpiry <= 3 ? 'priority_high' : 'schedule'"
              />
            </q-td>
          </template>

          <template v-slot:body-cell-quantity="props">
            <q-td :props="props">
              <q-badge
                :color="props.value < 10 ? 'red' : props.value < 50 ? 'orange' : 'green'"
                :label="`${props.value} 件`"
                class="q-px-sm"
              />
            </q-td>
          </template>

          <template v-slot:body-cell-daysToExpiry="props">
            <q-td :props="props">
              <q-chip
                :color="props.value <= 3 ? 'red' : props.value <= 7 ? 'orange' : 'yellow'"
                text-color="white"
                dense
                :label="`${props.value} 天`"
                :icon="props.value <= 3 ? 'error' : 'schedule'"
              />
            </q-td>
          </template>

          <template v-slot:body-cell-name="props">
            <q-td :props="props">
              <div class="row items-center">
                <q-avatar size="sm" color="grey-3" text-color="grey-8" class="q-mr-sm">
                  <q-icon name="inventory" />
                </q-avatar>
                <div>
                  <div class="text-weight-medium">{{ props.value }}</div>
                  <div class="text-caption text-grey-6">批次: {{ props.row.batchCode }}</div>
                </div>
              </div>
            </q-td>
          </template>

          <template v-slot:body-cell-supplierName="props">
            <q-td :props="props">
              <q-chip
                color="grey-3"
                text-color="grey-8"
                dense
                :label="props.value"
                icon="business"
              />
            </q-td>
          </template>
        </q-table>
      </q-card>

      <!-- 低庫存商品表格 -->
      <q-card class="q-mt-md bg-red-1" flat bordered>
        <q-card-section>
          <div class="text-h6 text-red-9 row items-center">
            <q-icon name="inventory_2" class="q-mr-sm" />
            低庫存商品警示
            <q-space />
            <q-chip
              color="red-8"
              text-color="white"
              :label="`${lowStockItems.length} 項商品`"
              dense
            />
          </div>
        </q-card-section>
        <q-separator />
        <q-table
          flat
          dense
          :rows="lowStockItems"
          :columns="lowStockColumns"
          row-key="name"
          class="low-stock-table"
          :rows-per-page-options="[5, 10, 20]"
          :pagination="{ rowsPerPage: 5 }"
          no-data-label="暫無低庫存商品"
          :loading="loading"
        >
          <template v-slot:body-cell-name="props">
            <q-td :props="props">
              <div class="row items-center">
                <q-avatar size="sm" color="red-2" text-color="red-8" class="q-mr-sm">
                  <q-icon name="inventory" />
                </q-avatar>
                <div class="text-weight-medium">{{ props.value }}</div>
              </div>
            </q-td>
          </template>
        </q-table>
      </q-card>

      <!-- 熱銷商品表格 -->
      <!-- <q-card class="q-mt-md bg-green-1" flat bordered>
        <q-card-section>
          <div class="text-h6 text-green-9 row items-center">
            <q-icon name="local_fire_department" class="q-mr-sm" />
            熱銷商品排行榜
          </div>
        </q-card-section>
        <q-separator />
        <q-table
          flat
          dense
          :rows="hotItems"
          :columns="hotItemColumns"
          row-key="product_id"
          class="hot-items-table"
          :rows-per-page-options="[5, 10, 20]"
          :pagination="{ rowsPerPage: 5 }"
        >
          <template v-slot:body-cell-rank="props">
            <q-td :props="props">
              <q-chip
                :color="getRankColor(props.value)"
                text-color="white"
                dense
                :label="`#${props.value}`"
              />
            </q-td>
          </template>
          <template v-slot:body-cell-total_quantity="props">
            <q-td :props="props">
              <div class="row items-center">
                <q-linear-progress
                  :value="props.value / 150"
                  color="green"
                  class="q-mr-sm"
                  style="width: 80px"
                />
                <span class="text-weight-medium">{{ props.value }}</span>
              </div>
            </q-td>
          </template>
        </q-table>
      </q-card> -->
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue';
import type { QTableColumn } from 'quasar';
import { api } from 'boot/axios';
import { useQuasar } from 'quasar';

const $q = useQuasar();
const loading = ref(true);

// 即將過期商品數據
interface ExpiringBatch {
  productId: number;
  name: string; // 商品名稱
  batchId: number;
  batchCode: string;
  quantity: number;
  expirationDate: string; // 效期
  supplierName: string;
  daysToExpiry: number;
}

// 低庫存商品數據
interface LowStockItem {
  productId: number;
  name: string; // 商品名稱
  minStock: number; // 最低庫存
  availableStock: number; // 目前庫存
  shortage: number;
}

// 熱銷商品數據
// interface HotItem {
//   product_id: number;
//   product_name: string;
//   total_quantity: number;
//   rank: number;
// }

// 動畫數值
const animatedValues = reactive({
  sales: 0,
  lowStock: 0,
  expiring: 0,
  orders: 0,
});

// 目標數值(用於數字動畫，目前為模擬數據)
const targetValues = {
  sales: 120000,
  lowStock: 3,
  expiring: 5,
  orders: 42,
};

// 數字動畫函數
const animateNumber = (
  start: number,
  end: number,
  duration: number,
  callback: (value: number) => void,
) => {
  const startTime = performance.now();
  const difference = end - start;

  const animate = (currentTime: number) => {
    const elapsed = currentTime - startTime;
    const progress = Math.min(elapsed / duration, 1);

    // 使用 easeOutQuart 緩動函數讓動畫更自然
    const easeProgress = 1 - Math.pow(1 - progress, 4);
    const currentValue = start + difference * easeProgress;

    callback(currentValue);

    if (progress < 1) {
      requestAnimationFrame(animate);
    }
  };

  requestAnimationFrame(animate);
};

// 格式化數字（添加千位分隔符）
const formatNumber = (num: number): string => {
  return Math.floor(num).toLocaleString();
};

interface OverviewResponse {
  lowStockItems: LowStockItem[];
  expiringBatches: ExpiringBatch[];
  weeklyOrderCount: number;
  monthlySales: number;
}

const lowStockItems = ref<LowStockItem[]>([]);
const expiringBatches = ref<ExpiringBatch[]>([]);
const weeklyOrderCount = ref(0);
const monthlySales = ref(0);

async function fetchOverviewData() {
  try {
    loading.value = true;
    const response = await api.get<OverviewResponse>('/dashboard/overview');
    const data = response.data;

    if (data) {
      lowStockItems.value = data.lowStockItems;
      expiringBatches.value = data.expiringBatches;
      weeklyOrderCount.value = data.weeklyOrderCount;
      monthlySales.value = data.monthlySales;
    } else {
      $q.notify({
        color: 'warning',
        message: '回傳資料為空',
        icon: 'warning',
        position: 'top',
      });
    }
  } catch (error) {
    $q.notify({
      color: 'negative',
      message: '獲取儀表板總覽資料失敗',
      icon: 'report_problem',
      position: 'top',
    });
    console.error('Error fetching dashboard overview:', error);
  }

  loading.value = false;
}

// 頁面載入時開始動畫
onMounted(async () => {
  await fetchOverviewData();

  targetValues.sales = monthlySales.value;
  targetValues.lowStock = lowStockItems.value.length;
  targetValues.expiring = expiringBatches.value.length;
  targetValues.orders = weeklyOrderCount.value;

  // 延遲一點開始動畫，讓頁面先渲染
  setTimeout(() => {
    // 銷售總額動畫 - 較長時間因為數字大
    animateNumber(0, targetValues.sales, 1500, (value) => {
      animatedValues.sales = value;
    });

    // 低庫存商品動畫
    animateNumber(0, targetValues.lowStock, 1000, (value) => {
      animatedValues.lowStock = value;
    });

    // 即將過期動畫
    animateNumber(0, targetValues.expiring, 1200, (value) => {
      animatedValues.expiring = value;
    });

    // 訂單數動畫
    animateNumber(0, targetValues.orders, 1500, (value) => {
      animatedValues.orders = value;
    });
  }, 300);
});

const expiringColumns = ref<QTableColumn<ExpiringBatch>[]>([
  { name: 'name', label: '商品名稱', field: 'name', align: 'left' },
  { name: 'batchCode', label: '批次代碼', field: 'batchCode', align: 'center' },
  { name: 'expirationDate', label: '效期', field: 'expirationDate', align: 'center' },
  { name: 'quantity', label: '剩餘數量', field: 'quantity', align: 'center' },
  { name: 'daysToExpiry', label: '剩餘天數', field: 'daysToExpiry', align: 'center' },
  { name: 'supplierName', label: '供應商', field: 'supplierName', align: 'left' },
]);

const lowStockColumns = ref<QTableColumn<LowStockItem>[]>([
  { name: 'name', label: '商品名稱', field: 'name', align: 'left' },
  { name: 'availableStock', label: '目前庫存', field: 'availableStock', align: 'center' },
  { name: 'minStock', label: '最低庫存', field: 'minStock', align: 'left' },
  { name: 'shortage', label: '缺貨數量', field: 'shortage', align: 'left' },
]);

// 熱銷商品數據
// const hotItems = ref<HotItem[]>([
//   { product_id: 1, product_name: '可樂', total_quantity: 120, rank: 1 },
//   { product_id: 2, product_name: '泡麵', total_quantity: 98, rank: 2 },
//   { product_id: 3, product_name: '洋芋片', total_quantity: 85, rank: 3 },
//   { product_id: 4, product_name: '礦泉水', total_quantity: 76, rank: 4 },
//   { product_id: 5, product_name: '餅乾', total_quantity: 65, rank: 5 },
// ]);

// const hotItemColumns = ref<QTableColumn<HotItem>[]>([
//   { name: 'rank', label: '排名', field: 'rank', align: 'left' },
//   { name: 'product_name', label: '商品名稱', field: 'product_name', align: 'left' },
//   { name: 'total_quantity', label: '銷售數量', field: 'total_quantity', align: 'center' },
// ]);

// 輔助函數
const getExpirationColor = (date: string) => {
  const today = new Date();
  const expDate = new Date(date);
  const diffTime = expDate.getTime() - today.getTime();
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

  if (diffDays <= 3) return 'red';
  if (diffDays <= 7) return 'orange';
  return 'yellow';
};

const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-TW', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  });
};

// const getRankColor = (rank: number): string => {
//   if (rank === 1) return 'yellow-8';
//   if (rank === 2) return 'grey-6';
//   if (rank === 3) return 'orange-8';
//   return 'blue-grey-5';
// };
</script>

<style scoped lang="scss">
.stat-card {
  border-radius: 12px;
  transition: all 0.3s ease;
  min-height: 120px; /* 固定最小高度 */

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  .q-card-section {
    height: 100%;
    display: flex;
    align-items: center;
  }
}

.stat-value {
  font-size: 1.8rem;
  font-weight: 600;
  line-height: 1.2;
  transition: all 0.3s ease;
  font-variant-numeric: tabular-nums; /* 確保數字寬度一致 */

  @media (max-width: 768px) {
    font-size: 1.5rem;
  }

  @media (max-width: 480px) {
    font-size: 1.3rem;
  }
}

.expiring-table,
.low-stock-table,
.hot-items-table {
  .q-table__top {
    display: none;
  }

  .q-table thead th {
    font-weight: 600;
    color: #424242;
  }

  .q-table tbody tr:hover {
    background: rgba(255, 255, 255, 0.7);
  }
}

.q-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

  &:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  }
}

.q-btn {
  border-radius: 8px;
  text-transform: none;
  font-weight: 500;
}

.q-chip {
  border-radius: 6px;
}

.q-badge {
  border-radius: 4px;
}
.expiring-table {
  .q-table__top {
    padding: 12px;
  }

  .q-table tbody td {
    padding: 8px 12px;
  }
}

.low-stock-table {
  .q-table__top {
    padding: 12px;
  }

  .q-table tbody td {
    padding: 8px 12px;
  }
}

/* 響應式設計 */
@media (max-width: 768px) {
  .expiring-table,
  .low-stock-table {
    .q-table tbody td {
      padding: 6px 8px;
      font-size: 12px;
    }
  }

  .text-h6 {
    font-size: 1.1rem;
  }
}
</style>
