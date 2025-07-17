<template>
  <div class="q-pa-md">
    <!-- 頁面標題和搜索區域 -->
    <div class="page-header">
      <div class="row items-center justify-between q-mb-md">
        <div class="col-auto">
          <div class="text-h4 text-weight-bold text-primary">
            <q-icon name="inventory" class="q-mr-sm" />
            庫存管理
          </div>
          <div class="text-subtitle1 text-grey-6 q-mt-xs">管理和追蹤所有庫存商品</div>
        </div>
        <div class="col-auto">
          <q-btn
            color="primary"
            icon="add"
            label="加入新商品"
            unelevated
            @click="addNewProduct"
            class="q-px-lg"
          />
        </div>
      </div>
    </div>

    <!-- 統計卡片區 -->
    <div class="row q-col-gutter-md q-mb-md">
      <div class="col-xs-12 col-sm-6 col-md-3">
        <q-card class="stat-card bg-blue-1" flat bordered>
          <q-card-section>
            <div class="row items-center q-gutter-md">
              <q-icon name="inventory" size="md" class="text-blue-7" />
              <div>
                <div class="text-subtitle2 text-grey-7">商品總數</div>
                <div class="stat-value text-blue-8 q-mt-xs">
                  {{ Math.floor(animatedValues.totalProducts) }}
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
              <q-icon name="check_circle" size="md" class="text-green-7" />
              <div>
                <div class="text-subtitle2 text-grey-7">有效庫存</div>
                <div class="stat-value text-green-8 q-mt-xs">
                  {{ Math.floor(animatedValues.availableStock) }}
                </div>
              </div>
            </div>
          </q-card-section>
        </q-card>
      </div>

      <div class="col-xs-12 col-sm-6 col-md-3">
        <q-card class="stat-card bg-red-1" flat bordered>
          <q-card-section>
            <div class="row items-center q-gutter-md">
              <q-icon name="warning" size="md" class="text-red-7" />
              <div>
                <div class="text-subtitle2 text-grey-7">過期庫存</div>
                <div class="stat-value text-red-8 q-mt-xs">
                  {{ Math.floor(animatedValues.expiredStock) }}
                </div>
              </div>
            </div>
          </q-card-section>
        </q-card>
      </div>

      <div class="col-xs-12 col-sm-6 col-md-3">
        <q-card class="stat-card bg-purple-1" flat bordered>
          <q-card-section>
            <div class="row items-center q-gutter-md">
              <q-icon name="attach_money" size="md" class="text-purple-7" />
              <div>
                <div class="text-subtitle2 text-grey-7">庫存價值</div>
                <div class="stat-value text-purple-8 q-mt-xs">
                  ${{ formatNumber(animatedValues.totalValue) }}
                </div>
              </div>
            </div>
          </q-card-section>
        </q-card>
      </div>
    </div>

    <!-- 搜索區域 -->
    <div class="row justify-between items-center q-mb-md">
      <q-input
        filled
        v-model="search"
        label="搜尋庫存"
        class="col-grow q-mr-md"
        debounce="300"
        placeholder="輸入品名或庫別..."
        clearable
        dense
      >
        <template #append>
          <q-icon name="search" />
        </template>
      </q-input>
    </div>

    <q-table
      title="📦 庫存清單"
      :rows="rows"
      :columns="columns"
      row-key="productId"
      :loading="loading"
      :filter="search"
      :rows-per-page-options="[20, 35, 50, 0]"
      table-header-class="bg-light-blue-2 text-weight-bold"
      table-header-style="color: #5a6c7d"
      flat
      bordered
      separator="cell"
      class="rounded-borders"
    >
      <template #body="props">
        <q-tr :props="props" @click="onRowClick(props.row.productId)" class="cursor-pointer">
          <q-td v-for="col in props.cols" :key="col.name" :props="props">
            <template v-if="col.name === 'productName'">
              <q-chip
                flat
                dense
                padding="xs"
                :label="props.row.productName"
                class="text-subtitle1"
              />
            </template>
            <template v-else-if="col.name === 'stock'">
              <q-chip
                :color="props.row.stock < 10 ? 'red-4' : 'green-4'"
                text-color="white"
                class="text-weight-bold"
                dense
              >
                {{ props.row.stock }}
              </q-chip>
            </template>
            <template v-else-if="col.name === 'nearestExpiryDate'">
              {{ props.row.nearestExpiryDate }}
              <q-chip
                v-if="isNearingExpiry(props.row.nearestExpiryDate)"
                color="orange-5"
                text-color="white"
                icon="warning"
                label="即將過期"
                size="sm"
                class="q-mr-sm"
              />
            </template>
            <template v-else>
              {{ col.value }}
            </template>
          </q-td>
        </q-tr>
      </template>

      <template #no-data="{ icon, message, filter }">
        <div class="full-width row flex-center text-accent q-pa-lg">
          <q-icon size="2em" :name="icon" />
          <span>
            {{ message }}
          </span>
          <span v-if="filter"> 過濾條件: "{{ filter }}" </span>
        </div>
      </template>
    </q-table>
  </div>

  <!-- 產品批次詳情對話框 -->
  <q-dialog v-model="showProductBatchDetails" :maximized="$q.screen.lt.md">
    <q-card
      class="product-batch-details-card"
      style="min-width: 800px; max-width: 90vw; max-height: 90vh"
    >
      <q-card-section class="row items-center q-pb-none bg-primary text-white">
        <q-icon name="inventory" size="md" class="q-mr-sm" />
        <div class="text-h6">產品批次詳情</div>
        <q-space />
        <q-btn icon="close" flat round dense v-close-popup color="white" />
      </q-card-section>

      <q-separator />

      <q-card-section v-if="selectedProduct" class="scroll" style="max-height: 70vh">
        <div class="row q-col-gutter-md">
          <!-- 產品基本資訊 -->
          <div class="col-12 col-md-6">
            <q-card flat bordered class="info-card">
              <q-card-section>
                <div class="text-subtitle1 text-weight-bold text-primary q-mb-md">
                  <q-icon name="inventory" class="q-mr-sm" />
                  產品資訊
                </div>
                <div class="info-grid">
                  <div class="info-item">
                    <div class="info-label">產品名稱</div>
                    <div class="info-value text-weight-medium">
                      {{ selectedProduct.name }}
                    </div>
                  </div>
                  <div class="info-item">
                    <div class="info-label">類別</div>
                    <div class="info-value">{{ selectedProduct.category }}</div>
                  </div>
                  <div class="info-item">
                    <div class="info-label">單位</div>
                    <div class="info-value">{{ selectedProduct.unit }}</div>
                  </div>
                  <div class="info-item">
                    <div class="info-label">售價</div>
                    <div class="info-value text-green-8 text-weight-bold">
                      ${{ selectedProduct.price }}
                    </div>
                  </div>
                </div>
              </q-card-section>
            </q-card>
          </div>

          <!-- 庫存統計 -->
          <div class="col-12 col-md-6">
            <q-card flat bordered class="info-card">
              <q-card-section>
                <div class="text-subtitle1 text-weight-bold text-primary q-mb-md">
                  <q-icon name="assessment" class="q-mr-sm" />
                  庫存統計
                </div>
                <div class="info-grid">
                  <div class="info-item">
                    <div class="info-label">總庫存</div>
                    <div class="info-value text-weight-bold">
                      {{ selectedProduct.totalStock }}
                    </div>
                  </div>
                  <div class="info-item">
                    <div class="info-label">有效庫存</div>
                    <div class="info-value text-green-8 text-weight-bold">
                      {{ selectedProduct.availableStock }}
                    </div>
                  </div>
                  <div class="info-item">
                    <div class="info-label">過期庫存</div>
                    <div class="info-value text-red-8 text-weight-bold">
                      {{ selectedProduct.expiredStock }}
                    </div>
                  </div>
                  <div class="info-item">
                    <div class="info-label">最近到期日</div>
                    <div class="info-value">
                      {{ selectedProduct.nearestExpiryDate }}
                      <q-chip
                        v-if="isNearingExpiry(selectedProduct.nearestExpiryDate)"
                        color="orange-5"
                        text-color="white"
                        icon="warning"
                        label="即將過期"
                        size="sm"
                        class="q-ml-sm"
                      />
                    </div>
                  </div>
                </div>
              </q-card-section>
            </q-card>
          </div>

          <!-- 批次清單 -->
          <div class="col-12">
            <q-card flat bordered class="batches-card">
              <q-card-section>
                <div class="text-subtitle1 text-weight-bold text-primary q-mb-md">
                  <q-icon name="layers" class="q-mr-sm" />
                  批次清單
                  <q-chip
                    color="grey-4"
                    text-color="grey-8"
                    dense
                    :label="`共 ${productBatches.length} 批次`"
                    class="q-ml-sm"
                  />
                </div>
                <q-table
                  :rows="productBatches"
                  :columns="batchColumns"
                  row-key="batchId"
                  :loading="batchLoading"
                  flat
                  dense
                  hide-pagination
                  :pagination="{ rowsPerPage: 0 }"
                  class="detail-batches-table"
                >
                  <template #body-cell-batchCode="props">
                    <q-td :props="props">
                      <q-chip color="blue-2" text-color="blue-8" dense>
                        {{ props.value }}
                      </q-chip>
                    </q-td>
                  </template>
                  <template #body-cell-quantity="props">
                    <q-td :props="props">
                      <q-badge
                        :color="props.value > 0 ? 'green-4' : 'red-4'"
                        text-color="white"
                        :label="props.value"
                        class="q-px-sm"
                      />
                    </q-td>
                  </template>
                  <template #body-cell-purchasePrice="props">
                    <q-td :props="props">
                      <div class="text-weight-medium">${{ props.value }}</div>
                    </q-td>
                  </template>
                  <template #body-cell-salePrice="props">
                    <q-td :props="props">
                      <div class="text-weight-medium text-green-8">${{ props.value }}</div>
                    </q-td>
                  </template>
                  <template #body-cell-expirationDate="props">
                    <q-td :props="props">
                      <div class="text-weight-medium">
                        {{ formatDate(props.value) }}
                        <q-chip
                          v-if="isNearingExpiry(props.value)"
                          color="orange-5"
                          text-color="white"
                          icon="warning"
                          label="即將過期"
                          size="sm"
                          class="q-ml-sm"
                        />
                      </div>
                    </q-td>
                  </template>
                </q-table>
              </q-card-section>
            </q-card>
          </div>
        </div>
      </q-card-section>

      <q-separator />

      <q-card-actions align="right" class="bg-grey-1">
        <q-btn color="primary" icon="close" label="關閉" v-close-popup flat />
      </q-card-actions>
    </q-card>
  </q-dialog>

  <addNewProductForm v-model="showAddNewProductForm" @product-added="fetchInventoryData" />
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue';
import { useQuasar } from 'quasar';
import { api } from 'boot/axios';
import { eventBus } from 'src/utils/eventBus';
import addNewProductForm from '../components/addNewProductForm.vue';

const $q = useQuasar();

const search = ref('');

const rows = ref<InventoryItem[]>([]);
const loading = ref(false);
const showAddNewProductForm = ref(false);

// 統計動畫數值
interface AnimatedValues {
  totalProducts: number;
  availableStock: number;
  expiredStock: number;
  totalValue: number;
}

const animatedValues = reactive<AnimatedValues>({
  totalProducts: 0,
  availableStock: 0,
  expiredStock: 0,
  totalValue: 0,
});

function addNewProduct() {
  showAddNewProductForm.value = true;
}
const showProductBatchDetails = ref(false);
const selectedProduct = ref<InventoryItem | null>(null);
const productBatches = ref<ProductBatch[]>([]);
const batchLoading = ref(false);

interface ProductBatch {
  batchId: number;
  batchCode: string;
  quantity: number;
  purchasePrice: number;
  salePrice: number;
  expirationDate: string;
  createdAt: string;
  updatedAt: string;
}

async function onRowClick(productId: number) {
  const product = rows.value.find((item) => item.productId === productId);
  if (!product) return;

  selectedProduct.value = product;
  showProductBatchDetails.value = true;

  // 請求產品批次資料
  await fetchProductBatches(productId);
}

async function fetchProductBatches(productId: number) {
  try {
    batchLoading.value = true;
    const response = await api.get<ProductBatch[]>(`/inventory/${productId}`);

    // 從產品資料中獲取價格
    const product = rows.value.find((item) => item.productId === productId);
    const productPrice = product?.price || 0;

    // 為每個批次添加產品價格
    productBatches.value = response.data.map((batch) => ({
      ...batch,
      salePrice: productPrice, // 使用產品的售價
    }));
  } catch (error) {
    $q.notify({
      color: 'negative',
      message: '獲取批次資料失敗',
      icon: 'report_problem',
      position: 'top',
    });
    console.error('Error fetching product batches:', error);
  } finally {
    batchLoading.value = false;
  }
}
export interface InventoryItem {
  productId: number;
  name: string;
  category: string;
  unit: string;
  price: number;

  totalStock: number;
  availableStock: number;
  expiredStock: number;
  nearestExpiryDate: string;

  // 前端自行補充邏輯判斷欄位（非後端返回）
  nearExpiry?: boolean;
}

async function fetchInventoryData() {
  try {
    loading.value = true;
    const response = await api.get<InventoryItem[]>('/inventory/productInventoryView');
    rows.value = response.data.map((item) => ({
      ...item,
      nearExpiry: isNearingExpiry(item.nearestExpiryDate),
    }));
    
    // 更新統計資料
    updateInventoryStats(rows.value);
  } catch (error) {
    $q.notify({
      color: 'negative',
      message: '獲取庫存資料失敗',
      icon: 'report_problem',
      position: 'top',
    });
    console.error('Error fetching inventory data:', error);
  } finally {
    loading.value = false;
  }
}

// 在組件掛載時獲取資料
onMounted(() => {
  eventBus.on('productBatch-added', () => {
    void fetchInventoryData();
  });
  eventBus.on('product-added', () => {
    void fetchInventoryData();
  });
  void fetchInventoryData();
});

// Function to check if expiry date is within 7 days
function isNearingExpiry(expiryDate: string): boolean {
  const today = new Date();
  const expiry = new Date(expiryDate);
  const diffTime = expiry.getTime() - today.getTime();
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  return diffDays <= 7 && diffDays >= 0;
}

const columns = [
  {
    name: 'name',
    label: '品名',
    field: 'name',
    align: 'left' as const,
    sortable: true,
  },
  {
    name: 'availableStock',
    label: '有效庫存',
    field: 'availableStock',
    align: 'right' as const,
    sortable: true,
  },
  {
    name: 'unit',
    label: '單位',
    field: 'unit',
    align: 'left' as const,
  },
  {
    name: 'category',
    label: '類別',
    field: 'category',
    align: 'left' as const,
  },
  {
    name: 'price',
    label: '售價',
    field: 'price',
    align: 'left' as const,
    sortable: true,
    format: (val: number) => `$${val}`,
  },
  {
    name: 'nearestExpiryDate',
    label: '最近到期日',
    field: 'nearestExpiryDate',
    align: 'left' as const,
    sortable: true,
  },
  // {
  //   name: 'expiredStock',
  //   label: '過期數量',
  //   field: 'expiredStock',
  //   align: 'right' as const,
  //   sortable: true,
  // },
  // {
  //   name: 'totalStock',
  //   label: '總庫存',
  //   field: 'totalStock',
  //   align: 'right' as const,
  //   sortable: true,
  // },
];

// 批次表格欄位
const batchColumns = [
  {
    name: 'batchCode',
    label: '批次編號',
    field: 'batchCode',
    align: 'left' as const,
    sortable: true,
  },
  {
    name: 'quantity',
    label: '數量',
    field: 'quantity',
    align: 'center' as const,
    sortable: true,
  },
  {
    name: 'purchasePrice',
    label: '進價',
    field: 'purchasePrice',
    align: 'left' as const,
    sortable: true,
  },
  // {
  //   name: 'salePrice',
  //   label: '售價',
  //   field: 'salePrice',
  //   align: 'right' as const,
  //   sortable: true,
  // },
  {
    name: 'expirationDate',
    label: '到期日',
    field: 'expirationDate',
    align: 'left' as const,
    sortable: true,
  },
];

// 格式化日期函數
function formatDate(dateString: string): string {
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-TW', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  });
}

// 格式化數字函數
function formatNumber(num: number): string {
  return Math.floor(num).toLocaleString();
}

// 數字動畫函數
function animateNumber(
  start: number,
  end: number,
  duration: number,
  callback: (value: number) => void,
) {
  const startTime = performance.now();
  const difference = end - start;

  const animate = (currentTime: number) => {
    const elapsed = currentTime - startTime;
    const progress = Math.min(elapsed / duration, 1);
    const easeProgress = 1 - Math.pow(1 - progress, 4);
    const currentValue = start + difference * easeProgress;

    callback(currentValue);

    if (progress < 1) {
      requestAnimationFrame(animate);
    }
  };

  requestAnimationFrame(animate);
}

// 更新庫存統計
function updateInventoryStats(inventory: InventoryItem[]) {
  const totalProducts = inventory.length;
  const availableStock = inventory.reduce((sum, item) => sum + item.availableStock, 0);
  const expiredStock = inventory.reduce((sum, item) => sum + item.expiredStock, 0);
  const totalValue = inventory.reduce((sum, item) => sum + (item.availableStock * item.price), 0);

  setTimeout(() => {
    animateNumber(0, totalProducts, 1000, (value) => {
      animatedValues.totalProducts = value;
    });
    animateNumber(0, availableStock, 1200, (value) => {
      animatedValues.availableStock = value;
    });
    animateNumber(0, expiredStock, 1400, (value) => {
      animatedValues.expiredStock = value;
    });
    animateNumber(0, totalValue, 1600, (value) => {
      animatedValues.totalValue = value;
    });
  }, 300);
}
</script>

<style lang="scss" scoped>
.q-table {
  /* Elevate the table slightly for a modern look */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
}

.q-table__title {
  font-size: 1.8em;
  font-weight: 600;
  color: $primary; /* Use Quasar's primary color */
  padding-bottom: 10px; /* Add some space below the title */
}

.q-table th {
  font-weight: bold;
  /* You can further customize header background or text color if desired */
}

/* Enhancements for table rows on hover */
.q-table tbody tr:hover {
  background-color: lighten($primary, 30%); /* Light highlight on hover */
  cursor: pointer;
}

/* Optional: Add a subtle border radius to the table for a softer look */
.rounded-borders {
  border-radius: 8px;
}

.product-batch-details-card {
  .info-card {
    transition: all 0.3s ease;
    border-radius: 12px;

    &:hover {
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    }
  }

  .batches-card {
    .detail-batches-table {
      .q-table thead th {
        background-color: #f5f5f5;
        font-weight: 600;
        color: #424242;
      }

      .q-table tbody tr:hover {
        background-color: rgba(25, 118, 210, 0.04);
      }
    }
  }

  .info-grid {
    display: grid;
    grid-template-columns: 1fr;
    gap: 16px;

    .info-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px 0;
      border-bottom: 1px solid rgba(0, 0, 0, 0.05);

      .info-label {
        font-weight: 500;
        color: #616161;
        min-width: 90px;
      }

      .info-value {
        font-weight: 600;
        color: #424242;
        flex: 1;
        text-align: right;
      }
    }
  }
}

.q-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

  &:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  }
}

.q-chip {
  border-radius: 6px;
}

.page-header {
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 12px;
  transition: all 0.3s ease;
  min-height: 120px;

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
  font-variant-numeric: tabular-nums;

  @media (max-width: 768px) {
    font-size: 1.5rem;
  }
}

@media (max-width: 600px) {
  .row.justify-between {
    flex-direction: column;
    align-items: stretch;
  }

  .q-input {
    margin-right: 0 !important;
    margin-bottom: 12px;
  }

  .q-btn {
    align-self: flex-end;
  }

  .page-header {
    .row {
      flex-direction: column;
      gap: 16px;
    }
  }

  .stat-value {
    font-size: 1.4rem;
  }
}
</style>
