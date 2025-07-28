<template>
  <div class="q-pa-md">
    <!-- 頁面標題和搜索區域 -->
    <div class="page-header">
      <div class="row items-center justify-between q-mb-md">
        <div class="col-auto">
          <div class="text-h4 text-weight-bold text-primary">
            <q-icon name="fact_check" class="q-mr-sm" />
            盤點記錄
          </div>
          <div class="text-subtitle1 text-grey-6 q-mt-xs">檢視所有庫存盤點歷史記錄</div>
        </div>
        <div class="col-auto">
          <q-btn
            color="primary"
            icon="refresh"
            label="重新整理"
            unelevated
            @click="fetchInventoryRecords"
            :loading="loading"
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
              <q-icon name="receipt_long" size="md" class="text-blue-7" />
              <div>
                <div class="text-subtitle2 text-grey-7">總盤點次數</div>
                <div class="stat-value text-blue-8 q-mt-xs">
                  {{ Math.floor(animatedValues.totalRecords) }}
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
              <q-icon name="trending_up" size="md" class="text-green-7" />
              <div>
                <div class="text-subtitle2 text-grey-7">盤盈次數</div>
                <div class="stat-value text-green-8 q-mt-xs">
                  {{ Math.floor(animatedValues.profitRecords) }}
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
              <q-icon name="trending_down" size="md" class="text-red-7" />
              <div>
                <div class="text-subtitle2 text-grey-7">盤虧次數</div>
                <div class="stat-value text-red-8 q-mt-xs">
                  {{ Math.floor(animatedValues.lossRecords) }}
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
              <q-icon name="balance" size="md" class="text-purple-7" />
              <div>
                <div class="text-subtitle2 text-grey-7">無差異次數</div>
                <div class="stat-value text-purple-8 q-mt-xs">
                  {{ Math.floor(animatedValues.matchRecords) }}
                </div>
              </div>
            </div>
          </q-card-section>
        </q-card>
      </div>
    </div>

    <!-- 搜索和篩選區域 -->
    <div class="row q-col-gutter-md q-mb-md">
      <div class="col-12 col-md-4">
        <q-input
          filled
          v-model="search"
          label="搜尋商品名稱"
          debounce="300"
          placeholder="輸入商品名稱..."
          clearable
          dense
        >
          <template #append>
            <q-icon name="search" />
          </template>
        </q-input>
      </div>

      <div class="col-12 col-md-3">
        <q-select
          filled
          v-model="differenceFilter"
          :options="differenceOptions"
          label="差異類型篩選"
          emit-value
          map-options
          clearable
          dense
        />
      </div>

      <div class="col-12 col-md-3">
        <q-select
          filled
          v-model="createdByFilter"
          :options="createdByOptions"
          label="盤點人員篩選"
          clearable
          dense
        />
      </div>

      <div class="col-12 col-md-2">
        <q-btn
          color="grey-6"
          icon="clear_all"
          label="清除篩選"
          @click="clearFilters"
          flat
          class="full-width"
          dense
        />
      </div>
    </div>

    <!-- 盤點記錄表格 -->
    <q-table
      title="📋 盤點記錄"
      :rows="filteredRows"
      :columns="columns"
      row-key="id"
      :loading="loading"
      :rows-per-page-options="[20, 35, 50, 0]"
      table-header-class="bg-light-blue-2 text-weight-bold"
      table-header-style="color: #5a6c7d"
      flat
      bordered
      separator="cell"
      class="rounded-borders"
      :pagination="pagination"
    >
      <template #body="props">
        <q-tr :props="props" @click="onRowClick(props.row)" class="cursor-pointer">
          <q-td v-for="col in props.cols" :key="col.name" :props="props">
            <template v-if="col.name === 'productName'">
              <q-chip
                flat
                dense
                :label="props.row.productName"
                class="text-subtitle1"
                color="blue-2"
                text-color="blue-8"
              />
            </template>
            <template v-else-if="col.name === 'difference'">
              <q-chip
                :color="getDifferenceColor(props.row.difference)"
                :text-color="getDifferenceTextColor(props.row.difference)"
                :icon="getDifferenceIcon(props.row.difference)"
                class="text-weight-bold"
                dense
              >
                {{ formatDifference(props.row.difference) }}
              </q-chip>
            </template>
            <template v-else-if="col.name === 'createdAt'">
              {{ formatDateTime(props.row.createdAt) }}
            </template>
            <template v-else-if="col.name === 'createdBy'">
              <q-chip
                outline
                dense
                :label="props.row.createdBy || '系統'"
                color="grey-6"
                class="text-caption"
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
          <span class="q-ml-sm">
            {{ message }}
          </span>
          <span v-if="filter" class="q-ml-sm"> 篩選條件: "{{ filter }}" </span>
        </div>
      </template>
    </q-table>
  </div>

  <!-- 盤點記錄詳情對話框 -->
  <q-dialog v-model="showRecordDetails" :maximized="$q.screen.lt.md">
    <q-card
      class="record-details-card"
      :style="$q.screen.lt.md ? '' : 'min-width: 600px; max-width: 80vw; max-height: 80vh'"
    >
      <q-card-section class="row items-center q-pb-none bg-primary text-white">
        <q-icon name="fact_check" size="md" class="q-mr-sm" />
        <div class="text-h6">盤點記錄詳情</div>
        <q-space />
        <q-btn icon="close" flat round dense v-close-popup color="white" />
      </q-card-section>

      <q-separator />

      <q-card-section v-if="selectedRecord" class="scroll">
        <div class="row q-col-gutter-md">
          <!-- 基本資訊 -->
          <div class="col-12">
            <q-card flat bordered class="info-card">
              <q-card-section>
                <div class="text-subtitle1 text-weight-bold text-primary q-mb-md">
                  <q-icon name="info" class="q-mr-sm" />
                  盤點基本資訊
                </div>
                <div class="mobile-info-grid">
                  <div class="mobile-info-row">
                    <div class="mobile-info-item">
                      <div class="mobile-info-label">記錄編號</div>
                      <div class="mobile-info-value text-weight-medium">
                        #{{ selectedRecord.id }}
                      </div>
                    </div>
                    <div class="mobile-info-item">
                      <div class="mobile-info-label">商品名稱</div>
                      <div class="mobile-info-value text-primary text-weight-bold">
                        {{ selectedRecord.productName }}
                      </div>
                    </div>
                  </div>
                  <div class="mobile-info-row">
                    <div class="mobile-info-item">
                      <div class="mobile-info-label">盤點人員</div>
                      <div class="mobile-info-value">
                        {{ selectedRecord.createdBy || '系統' }}
                      </div>
                    </div>
                    <div class="mobile-info-item">
                      <div class="mobile-info-label">盤點時間</div>
                      <div class="mobile-info-value">
                        {{ formatDateTime(selectedRecord.createdAt) }}
                      </div>
                    </div>
                  </div>
                </div>
              </q-card-section>
            </q-card>
          </div>

          <!-- 數量資訊 -->
          <div class="col-12">
            <q-card flat bordered class="info-card">
              <q-card-section>
                <div class="text-subtitle1 text-weight-bold text-primary q-mb-md">
                  <q-icon name="assessment" class="q-mr-sm" />
                  數量統計
                </div>
                <div class="mobile-stats-grid">
                  <div class="mobile-stat-item expected">
                    <div class="mobile-stat-value text-weight-bold">
                      {{ selectedRecord.expectedQuantity }}
                    </div>
                    <div class="mobile-stat-label">預期數量</div>
                  </div>
                  <div class="mobile-stat-item actual">
                    <div class="mobile-stat-value text-weight-bold">
                      {{ selectedRecord.actualQuantity }}
                    </div>
                    <div class="mobile-stat-label">實際數量</div>
                  </div>
                  <div
                    class="mobile-stat-item difference"
                    :class="getDifferenceClass(selectedRecord.difference)"
                  >
                    <div class="mobile-stat-value text-weight-bold">
                      {{ formatDifference(selectedRecord.difference) }}
                    </div>
                    <div class="mobile-stat-label">差異數量</div>
                    <div class="mobile-stat-description">
                      {{ getDifferenceDescription(selectedRecord.difference) }}
                    </div>
                  </div>
                </div>
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
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, computed } from 'vue';
import { useQuasar } from 'quasar';
import { api } from 'boot/axios';

const $q = useQuasar();

// 介面定義
interface InventoryCheckRecord {
  id: number;
  productName: string;
  expectedQuantity: number;
  actualQuantity: number;
  difference: number;
  createdBy: string | null;
  createdAt: string;
}

// 響應式資料
const rows = ref<InventoryCheckRecord[]>([]);
const loading = ref(false);
const search = ref('');
const differenceFilter = ref<string | null>(null);
const createdByFilter = ref<string | null>(null);
const showRecordDetails = ref(false);
const selectedRecord = ref<InventoryCheckRecord | null>(null);

// 統計動畫數值
interface AnimatedValues {
  totalRecords: number;
  profitRecords: number;
  lossRecords: number;
  matchRecords: number;
}

const animatedValues = reactive<AnimatedValues>({
  totalRecords: 0,
  profitRecords: 0,
  lossRecords: 0,
  matchRecords: 0,
});

// 分頁設定
const pagination = ref({
  sortBy: 'createdAt',
  descending: true,
  page: 1,
  rowsPerPage: 20,
});

// 篩選選項
const differenceOptions = [
  { label: '全部', value: null },
  { label: '盤盈 (差異 > 0)', value: 'profit' },
  { label: '盤虧 (差異 < 0)', value: 'loss' },
  { label: '無差異 (差異 = 0)', value: 'match' },
];

// 計算屬性：創建人員選項
const createdByOptions = computed(() => {
  const uniqueCreatedBy = [...new Set(rows.value.map((record) => record.createdBy || '系統'))]
    .filter(Boolean)
    .sort();
  return uniqueCreatedBy;
});

// 計算屬性：篩選後的資料
const filteredRows = computed(() => {
  let filtered = rows.value;

  // 搜尋篩選
  if (search.value) {
    const searchLower = search.value.toLowerCase();
    filtered = filtered.filter((record) => record.productName.toLowerCase().includes(searchLower));
  }

  // 差異類型篩選
  if (differenceFilter.value) {
    filtered = filtered.filter((record) => {
      switch (differenceFilter.value) {
        case 'profit':
          return record.difference > 0;
        case 'loss':
          return record.difference < 0;
        case 'match':
          return record.difference === 0;
        default:
          return true;
      }
    });
  }

  // 創建人員篩選
  if (createdByFilter.value) {
    filtered = filtered.filter((record) => (record.createdBy || '系統') === createdByFilter.value);
  }

  return filtered;
});

// 表格欄位定義
const columns = [
  {
    name: 'id',
    label: '記錄編號',
    field: 'id',
    align: 'left' as const,
    sortable: true,
  },
  {
    name: 'productName',
    label: '商品名稱',
    field: 'productName',
    align: 'left' as const,
    sortable: true,
  },
  {
    name: 'expectedQuantity',
    label: '預期數量',
    field: 'expectedQuantity',
    align: 'center' as const,
    sortable: true,
  },
  {
    name: 'actualQuantity',
    label: '實際數量',
    field: 'actualQuantity',
    align: 'center' as const,
    sortable: true,
  },
  {
    name: 'difference',
    label: '差異',
    field: 'difference',
    align: 'center' as const,
    sortable: true,
  },
  {
    name: 'createdBy',
    label: '盤點人員',
    field: 'createdBy',
    align: 'left' as const,
    sortable: true,
  },
  {
    name: 'createdAt',
    label: '盤點時間',
    field: 'createdAt',
    align: 'left' as const,
    sortable: true,
  },
];

// 方法：獲取盤點記錄資料
async function fetchInventoryRecords() {
  try {
    loading.value = true;
    const response = await api.get<InventoryCheckRecord[]>('/inventory/check-log');
    rows.value = response.data;

    // 更新統計資料
    updateRecordStats(rows.value);
  } catch (error) {
    $q.notify({
      color: 'negative',
      message: '獲取盤點記錄失敗',
      icon: 'report_problem',
      position: 'top',
    });
    console.error('Error fetching inventory records:', error);
  } finally {
    loading.value = false;
  }
}

// 方法：點擊行事件
function onRowClick(record: InventoryCheckRecord) {
  selectedRecord.value = record;
  showRecordDetails.value = true;
}

// 方法：清除篩選
function clearFilters() {
  search.value = '';
  differenceFilter.value = null;
  createdByFilter.value = null;
}

// 方法：格式化差異顯示
function formatDifference(difference: number): string {
  if (difference > 0) {
    return `+${difference}`;
  }
  return difference.toString();
}

// 方法：獲取差異顏色
function getDifferenceColor(difference: number): string {
  if (difference > 0) return 'green-4';
  if (difference < 0) return 'red-4';
  return 'grey-4';
}

// 方法：獲取差異文字顏色
function getDifferenceTextColor(difference: number): string {
  if (difference === 0) return 'grey-8';
  return 'white';
}

// 方法：獲取差異圖示
function getDifferenceIcon(difference: number): string {
  if (difference > 0) return 'trending_up';
  if (difference < 0) return 'trending_down';
  return 'remove';
}

// 方法：獲取差異類別
function getDifferenceClass(difference: number): string {
  if (difference > 0) return 'profit';
  if (difference < 0) return 'loss';
  return 'match';
}

// 方法：獲取差異描述
function getDifferenceDescription(difference: number): string {
  if (difference > 0) return '盤盈';
  if (difference < 0) return '盤虧';
  return '無差異';
}

// 方法：格式化日期時間
function formatDateTime(dateString: string): string {
  const date = new Date(dateString);
  return date.toLocaleString('zh-TW', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  });
}

// 方法：數字動畫
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

// 方法：更新記錄統計
function updateRecordStats(records: InventoryCheckRecord[]) {
  const totalRecords = records.length;
  const profitRecords = records.filter((record) => record.difference > 0).length;
  const lossRecords = records.filter((record) => record.difference < 0).length;
  const matchRecords = records.filter((record) => record.difference === 0).length;

  setTimeout(() => {
    animateNumber(0, totalRecords, 1000, (value) => {
      animatedValues.totalRecords = value;
    });
    animateNumber(0, profitRecords, 1200, (value) => {
      animatedValues.profitRecords = value;
    });
    animateNumber(0, lossRecords, 1400, (value) => {
      animatedValues.lossRecords = value;
    });
    animateNumber(0, matchRecords, 1600, (value) => {
      animatedValues.matchRecords = value;
    });
  }, 300);
}

// 組件掛載時獲取資料
onMounted(() => {
  void fetchInventoryRecords();
});
</script>

<style lang="scss" scoped>
.q-table {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
}

.q-table__title {
  font-size: 1.8em;
  font-weight: 600;
  color: $primary;
  padding-bottom: 10px;
}

.q-table th {
  font-weight: bold;
}

.q-table tbody tr:hover {
  background-color: lighten($primary, 30%);
  cursor: pointer;
}

.rounded-borders {
  border-radius: 8px;
}

.record-details-card {
  .info-card {
    transition: all 0.3s ease;
    border-radius: 12px;

    &:hover {
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    }
  }

  // 手機版資訊網格
  .mobile-info-grid {
    display: flex;
    flex-direction: column;
    gap: 16px;

    .mobile-info-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 16px;

      @media (max-width: 480px) {
        grid-template-columns: 1fr;
      }
    }

    .mobile-info-item {
      padding: 12px;
      background: rgba(0, 0, 0, 0.02);
      border-radius: 8px;
      border-left: 3px solid $primary;

      .mobile-info-label {
        font-size: 12px;
        font-weight: 500;
        color: #616161;
        text-transform: uppercase;
        letter-spacing: 0.5px;
        margin-bottom: 4px;
      }

      .mobile-info-value {
        font-size: 14px;
        font-weight: 600;
        color: #424242;
      }
    }
  }

  // 手機版統計網格
  .mobile-stats-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 12px;

    @media (max-width: 600px) {
      grid-template-columns: 1fr;
    }

    .mobile-stat-item {
      text-align: center;
      padding: 16px 8px;
      background: rgba(0, 0, 0, 0.02);
      border-radius: 8px;
      border: 1px solid rgba(0, 0, 0, 0.08);
      position: relative;

      &.expected {
        border-left: 4px solid #2196f3;
        background: rgba(33, 150, 243, 0.05);
      }

      &.actual {
        border-left: 4px solid #ff9800;
        background: rgba(255, 152, 0, 0.05);
      }

      &.difference {
        &.profit {
          border-left: 4px solid #4caf50;
          background: rgba(76, 175, 80, 0.05);
        }

        &.loss {
          border-left: 4px solid #f44336;
          background: rgba(244, 67, 54, 0.05);
        }

        &.match {
          border-left: 4px solid #9e9e9e;
          background: rgba(158, 158, 158, 0.05);
        }
      }

      .mobile-stat-value {
        font-size: 20px;
        font-weight: 700;
        line-height: 1.2;
        margin-bottom: 4px;

        @media (max-width: 480px) {
          font-size: 18px;
        }
      }

      .mobile-stat-label {
        font-size: 12px;
        font-weight: 500;
        color: #616161;
        text-transform: uppercase;
        letter-spacing: 0.5px;
        margin-bottom: 2px;
      }

      .mobile-stat-description {
        font-size: 11px;
        font-weight: 600;
        color: #757575;
        text-transform: uppercase;
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
