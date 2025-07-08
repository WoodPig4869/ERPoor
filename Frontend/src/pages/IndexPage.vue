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
                <q-icon name="schedule" size="md" class="text-yellow-6" />
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
          </div>
        </q-card-section>
        <q-separator />
        <q-table
          flat
          dense
          :rows="expiringBatches"
          :columns="expiringColumns"
          row-key="batch_id"
          class="expiring-table"
          :rows-per-page-options="[5, 10, 20]"
          :pagination="{ rowsPerPage: 5 }"
        >
          <template v-slot:body-cell-expiration_date="props">
            <q-td :props="props">
              <q-chip
                :color="getExpirationColor(props.value)"
                text-color="white"
                dense
                :label="props.value"
              />
            </q-td>
          </template>
          <template v-slot:body-cell-quantity="props">
            <q-td :props="props">
              <q-badge
                :color="props.value < 10 ? 'red' : 'orange'"
                :label="props.value"
                class="q-px-sm"
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
          </div>
        </q-card-section>
        <q-separator />
        <q-table
          flat
          dense
          :rows="lowStockItems"
          :columns="lowStockColumns"
          row-key="product_id"
          class="low-stock-table"
          :rows-per-page-options="[5, 10, 20]"
          :pagination="{ rowsPerPage: 5 }"
        >
          <template v-slot:body-cell-current_stock="props">
            <q-td :props="props">
              <q-badge color="red" :label="props.value" class="q-px-sm" />
            </q-td>
          </template>
          <template v-slot:body-cell-actions="props">
            <q-td :props="props">
              <q-btn
                size="sm"
                color="primary"
                icon="add_shopping_cart"
                dense
                round
                @click="quickRestock(props.row)"
              >
                <q-tooltip>快速補貨</q-tooltip>
              </q-btn>
            </q-td>
          </template>
        </q-table>
      </q-card>

      <!-- 熱銷商品表格 -->
      <q-card class="q-mt-md bg-green-1" flat bordered>
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
      </q-card>
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue';
import type { QTableColumn } from 'quasar';
import { api } from 'boot/axios';
import { useQuasar } from 'quasar';

const $q = useQuasar();

// 即將過期商品數據
const expiringBatches = ref<ExpiringBatch[]>([]);
interface ExpiringBatch {
  batchId: number;
  product: string;
  expirationDate: string;
  quantity: number;
}

// 低庫存商品數據
const lowStockItems = ref<LowStockItem[]>([]);
interface LowStockItem {
  productId: number;
  productName: string;
  currentStock: number;
  minStock: number;
  unit: string;
}

interface HotItem {
  product_id: number;
  product_name: string;
  total_quantity: number;
  rank: number;
}

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

async function fetchExpiringData() {
  try {
    const response = await api.get<ExpiringBatch[]>('/expiringBatch');
    expiringBatches.value = response.data.map((item) => ({
      ...item,
    }));
  } catch (error) {
    $q.notify({
      color: 'negative',
      message: '獲取即將過期批次資料失敗',
      icon: 'report_problem',
      position: 'top',
    });
    console.error('Error fetching inventory data:', error);
  }
}

async function fetchLowStockItems() {
  try {
    const response = await api.get<LowStockItem[]>('/lowStockItems');
    lowStockItems.value = response.data.map((item) => ({
      ...item,
    }));
  } catch (error) {
    $q.notify({
      color: 'negative',
      message: '獲取低庫存商品資料失敗',
      icon: 'report_problem',
      position: 'top',
    });
    console.error('Error fetching low stock items:', error);
  }
}

// 頁面載入時開始動畫
onMounted(async () => {
  await fetchExpiringData();
  await fetchLowStockItems();

  targetValues.sales = 120000;
  targetValues.lowStock = lowStockItems.value.length;
  targetValues.expiring = expiringBatches.value.length;
  targetValues.orders = 42;

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
  { name: 'product', label: '商品名稱', field: 'product', align: 'left' },
  { name: 'expiration_date', label: '效期', field: 'expirationDate', align: 'center' },
  { name: 'quantity', label: '剩餘數量', field: 'quantity', align: 'center' },
]);

const lowStockColumns = ref<QTableColumn<LowStockItem>[]>([
  { name: 'product_name', label: '商品名稱', field: 'productName', align: 'left' },
  { name: 'current_stock', label: '目前庫存', field: 'currentStock', align: 'center' },
  { name: 'min_stock', label: '最低庫存', field: 'minStock', align: 'right' },
  { name: 'unit', label: '單位', field: 'unit', align: 'left' },
  { name: 'actions', label: '操作', field: () => '', align: 'left' },
]);

// 熱銷商品數據
const hotItems = ref<HotItem[]>([
  { product_id: 1, product_name: '可樂', total_quantity: 120, rank: 1 },
  { product_id: 2, product_name: '泡麵', total_quantity: 98, rank: 2 },
  { product_id: 3, product_name: '洋芋片', total_quantity: 85, rank: 3 },
  { product_id: 4, product_name: '礦泉水', total_quantity: 76, rank: 4 },
  { product_id: 5, product_name: '餅乾', total_quantity: 65, rank: 5 },
]);

const hotItemColumns = ref<QTableColumn<HotItem>[]>([
  { name: 'rank', label: '排名', field: 'rank', align: 'left' },
  { name: 'product_name', label: '商品名稱', field: 'product_name', align: 'left' },
  { name: 'total_quantity', label: '銷售數量', field: 'total_quantity', align: 'center' },
]);

// 工具函數
const getExpirationColor = (date: string): string => {
  const today = new Date();
  const expirationDate = new Date(date);
  const diffTime = expirationDate.getTime() - today.getTime();
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

  if (diffDays <= 1) return 'red';
  if (diffDays <= 3) return 'orange';
  return 'yellow-8';
};

const getRankColor = (rank: number): string => {
  if (rank === 1) return 'yellow-8';
  if (rank === 2) return 'grey-6';
  if (rank === 3) return 'orange-8';
  return 'blue-grey-5';
};

const quickRestock = (item: LowStockItem) => {
  console.log('快速補貨:', item.productName);
  // 這裡可以加入補貨邏輯
};
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
</style>
