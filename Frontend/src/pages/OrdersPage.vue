<template>
  <q-page class="orders-page q-pa-md">
    <div class="q-gutter-md">
      <!-- 頁面標題和搜索區域 -->
      <div class="page-header">
        <div class="row items-center justify-between">
          <div class="col-auto">
            <div class="text-h4 text-weight-bold text-primary">
              <q-icon name="receipt_long" class="q-mr-sm" />
              訂單管理
            </div>
            <div class="text-subtitle1 text-grey-6 q-mt-xs">管理和追蹤所有銷售訂單</div>
          </div>
          <div class="col-auto">
            <q-btn
              color="primary"
              icon="add"
              label="新增訂單"
              unelevated
              @click="showShippingForm = true"
              class="q-px-lg"
            />
          </div>
        </div>
      </div>

      <!-- 統計卡片區 -->
      <div class="row q-col-gutter-md">
        <div class="col-xs-12 col-sm-6 col-md-3">
          <q-card class="stat-card bg-blue-1" flat bordered>
            <q-card-section>
              <div class="row items-center q-gutter-md">
                <q-icon name="receipt" size="md" class="text-blue-7" />
                <div>
                  <div class="text-subtitle2 text-grey-7">總訂單數</div>
                  <div class="stat-value text-blue-8 q-mt-xs">
                    {{ Math.floor(animatedValues.totalOrders) }}
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
                <q-icon name="local_shipping" size="md" class="text-green-7" />
                <div>
                  <div class="text-subtitle2 text-grey-7">已出貨</div>
                  <div class="stat-value text-green-8 q-mt-xs">
                    {{ Math.floor(animatedValues.shippedOrders) }}
                  </div>
                </div>
              </div>
            </q-card-section>
          </q-card>
        </div>

        <div class="col-xs-12 col-sm-6 col-md-3">
          <q-card class="stat-card bg-orange-1" flat bordered>
            <q-card-section>
              <div class="row items-center q-gutter-md">
                <q-icon name="schedule" size="md" class="text-orange-7" />
                <div>
                  <div class="text-subtitle2 text-grey-7">待處理</div>
                  <div class="stat-value text-orange-8 q-mt-xs">
                    {{ Math.floor(animatedValues.pendingOrders) }}
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
                <q-icon name="trending_up" size="md" class="text-purple-7" />
                <div>
                  <div class="text-subtitle2 text-grey-7">本月營業額</div>
                  <div class="stat-value text-purple-8 q-mt-xs">
                    ${{ formatNumber(animatedValues.monthlyRevenue) }}
                  </div>
                </div>
              </div>
            </q-card-section>
          </q-card>
        </div>
      </div>

      <!-- 搜索和篩選區域 -->
      <q-card flat bordered class="search-card">
        <q-card-section>
          <div class="row q-col-gutter-md">
            <div class="col-12 col-md-4">
              <q-input
                v-model="searchQuery"
                filled
                dense
                placeholder="搜索訂單編號或客戶名稱..."
                debounce="300"
                clearable
              >
                <template #append>
                  <q-icon name="search" />
                </template>
              </q-input>
            </div>
            <div class="col-12 col-md-3">
              <q-select
                v-model="statusFilter"
                filled
                dense
                label="訂單狀態"
                :options="statusOptions"
                emit-value
                map-options
                clearable
              />
            </div>
            <div class="col-12 col-md-3">
              <q-input v-model="dateRange" filled dense label="訂單日期" type="date" clearable />
            </div>
            <div class="col-12 col-md-2">
              <q-btn
                color="primary"
                icon="refresh"
                label="重新整理"
                @click="fetchOrders"
                :loading="loading"
                unelevated
                class="full-width"
              />
            </div>
          </div>
        </q-card-section>
      </q-card>

      <!-- 訂單表格 -->
      <q-card flat bordered>
        <q-table
          :rows="filteredOrders"
          :columns="columns"
          :loading="loading"
          :pagination="pagination"
          :rows-per-page-options="[0, 10, 20, 50]"
          @request="onRequest"
          row-key="orderId"
          flat
          binary-state-sort
          class="orders-table"
        >
          <template #body-cell-orderNumber="props">
            <q-td :props="props">
              <div class="row items-center">
                <q-chip color="blue-2" text-color="blue-8" dense class="text-weight-medium">
                  {{ props.value }}
                </q-chip>
              </div>
            </q-td>
          </template>

          <template #body-cell-orderStatus="props">
            <q-td :props="props">
              <q-chip
                :color="getStatusColor(props.value)"
                text-color="white"
                :icon="getStatusIcon(props.value)"
                dense
                class="text-weight-medium"
              >
                {{ getStatusText(props.value) }}
              </q-chip>
            </q-td>
          </template>

          <template #body-cell-customerName="props">
            <q-td :props="props">
              <div class="row items-center">
                <q-avatar size="sm" color="grey-4" text-color="grey-8" class="q-mr-sm">
                  <q-icon name="person" />
                </q-avatar>
                <div>
                  <div class="text-weight-medium">{{ props.value }}</div>
                  <div class="text-caption text-grey-6">
                    {{ props.row.customerPhone || '無電話' }}
                  </div>
                </div>
              </div>
            </q-td>
          </template>

          <template #body-cell-totalAmount="props">
            <q-td :props="props">
              <div class="text-weight-bold text-green-8">${{ formatNumber(props.value) }}</div>
            </q-td>
          </template>

          <template #body-cell-orderDate="props">
            <q-td :props="props">
              <div class="text-weight-medium">
                {{ formatDate(props.value) }}
              </div>
            </q-td>
          </template>

          <template #body-cell-actions="props">
            <q-td :props="props">
              <div class="row q-gutter-xs">
                <q-btn
                  size="sm"
                  color="primary"
                  icon="visibility"
                  flat
                  round
                  @click="viewOrderDetails(props.row)"
                >
                  <q-tooltip>查看詳情</q-tooltip>
                </q-btn>
                <q-btn
                  v-if="props.row.orderStatus === 'pending'"
                  size="sm"
                  color="green"
                  icon="local_shipping"
                  flat
                  round
                  @click="updateOrderStatus(props.row, 'shipped')"
                >
                  <q-tooltip>標記已出貨</q-tooltip>
                </q-btn>
                <q-btn
                  v-if="props.row.orderStatus === 'pending'"
                  size="sm"
                  color="red"
                  icon="cancel"
                  flat
                  round
                  @click="showCancelDialog(props.row)"
                >
                  <q-tooltip>取消訂單</q-tooltip>
                </q-btn>
              </div>
            </q-td>
          </template>

          <template #no-data>
            <div class="full-width row flex-center text-grey-6 q-pa-xl">
              <div class="column items-center">
                <q-icon name="receipt_long" size="4rem" class="q-mb-md" />
                <div class="text-h6 q-mb-sm">暫無訂單資料</div>
                <div class="text-subtitle1">請新增您的第一筆訂單</div>
              </div>
            </div>
          </template>
        </q-table>
      </q-card>

      <!-- 訂單詳情對話框 -->
      <q-dialog v-model="showOrderDetails" :maximized="$q.screen.lt.md">
        <q-card
          class="order-details-card"
          style="min-width: 800px; max-width: 90vw; max-height: 90vh"
        >
          <q-card-section class="row items-center q-pb-none bg-primary text-white">
            <q-icon name="receipt_long" size="md" class="q-mr-sm" />
            <div class="text-h6">訂單詳情</div>
            <q-space />
            <q-btn icon="close" flat round dense v-close-popup color="white" />
          </q-card-section>

          <q-separator />

          <q-card-section v-if="selectedOrder" class="scroll" style="max-height: 70vh">
            <div class="row q-col-gutter-md">
              <!-- 訂單基本資訊 -->
              <div class="col-12 col-md-6">
                <q-card flat bordered class="info-card">
                  <q-card-section>
                    <div class="text-subtitle1 text-weight-bold text-primary q-mb-md">
                      <q-icon name="receipt" class="q-mr-sm" />
                      訂單資訊
                    </div>
                    <div class="info-grid">
                      <div class="info-item">
                        <div class="info-label">訂單編號</div>
                        <div class="info-value">
                          <q-chip color="blue-2" text-color="blue-8" dense>
                            {{ selectedOrder.orderNumber }}
                          </q-chip>
                        </div>
                      </div>
                      <div class="info-item">
                        <div class="info-label">訂單狀態</div>
                        <div class="info-value">
                          <q-chip
                            :color="getStatusColor(selectedOrder.orderStatus)"
                            text-color="white"
                            :icon="getStatusIcon(selectedOrder.orderStatus)"
                            dense
                          >
                            {{ getStatusText(selectedOrder.orderStatus) }}
                          </q-chip>
                        </div>
                      </div>
                      <div class="info-item">
                        <div class="info-label">訂單日期</div>
                        <div class="info-value">{{ formatDate(selectedOrder.orderDate) }}</div>
                      </div>
                      <div class="info-item">
                        <div class="info-label">總金額</div>
                        <div class="info-value text-green-8 text-weight-bold text-h6">
                          ${{ formatNumber(selectedOrder.totalAmount) }}
                        </div>
                      </div>
                      <div class="info-item" v-if="selectedOrder.notes">
                        <div class="info-label">備註</div>
                        <div class="info-value">{{ selectedOrder.notes }}</div>
                      </div>
                    </div>
                  </q-card-section>
                </q-card>
              </div>

              <!-- 客戶資訊 -->
              <div class="col-12 col-md-6">
                <q-card flat bordered class="info-card">
                  <q-card-section>
                    <div class="text-subtitle1 text-weight-bold text-primary q-mb-md">
                      <q-icon name="person" class="q-mr-sm" />
                      客戶資訊
                    </div>
                    <div class="info-grid">
                      <div class="info-item">
                        <div class="info-label">客戶名稱</div>
                        <div class="info-value text-weight-medium">
                          {{ selectedOrder.customerName }}
                        </div>
                      </div>
                      <div class="info-item">
                        <div class="info-label">聯絡電話</div>
                        <div class="info-value">
                          <span v-if="selectedOrder.customerPhone">
                            <q-icon name="phone" class="q-mr-xs" />
                            {{ selectedOrder.customerPhone }}
                          </span>
                          <span v-else class="text-grey-6">無</span>
                        </div>
                      </div>
                      <div class="info-item">
                        <div class="info-label">電子郵件</div>
                        <div class="info-value">
                          <span v-if="selectedOrder.customerEmail">
                            <q-icon name="email" class="q-mr-xs" />
                            {{ selectedOrder.customerEmail }}
                          </span>
                          <span v-else class="text-grey-6">無</span>
                        </div>
                      </div>
                      <div class="info-item">
                        <div class="info-label">客戶地址</div>
                        <div class="info-value">
                          <span v-if="selectedOrder.customerAddress">
                            <q-icon name="location_on" class="q-mr-xs" />
                            {{ selectedOrder.customerAddress }}
                          </span>
                          <span v-else class="text-grey-6">無</span>
                        </div>
                      </div>
                    </div>
                  </q-card-section>
                </q-card>
              </div>

              <!-- 訂單項目 -->
              <div class="col-12">
                <q-card flat bordered class="items-card">
                  <q-card-section>
                    <div class="text-subtitle1 text-weight-bold text-primary q-mb-md">
                      <q-icon name="shopping_cart" class="q-mr-sm" />
                      訂單項目
                      <q-chip
                        color="grey-4"
                        text-color="grey-8"
                        dense
                        :label="`共 ${selectedOrder.orderItems.length} 項`"
                        class="q-ml-sm"
                      />
                    </div>
                    <q-table
                      :rows="selectedOrder.orderItems"
                      :columns="orderItemColumns"
                      row-key="orderItemId"
                      flat
                      dense
                      hide-pagination
                      :pagination="{ rowsPerPage: 0 }"
                      class="detail-items-table"
                    >
                      <template #body-cell-productName="props">
                        <q-td :props="props">
                          <div class="row items-center">
                            <q-avatar size="sm" color="blue-1" text-color="blue-8" class="q-mr-sm">
                              <q-icon name="inventory" />
                            </q-avatar>
                            <div>
                              <div class="text-weight-medium">{{ props.value }}</div>
                              <div class="text-caption text-grey-6">{{ props.row.productSku }}</div>
                            </div>
                          </div>
                        </q-td>
                      </template>
                      <template #body-cell-quantity="props">
                        <q-td :props="props">
                          <q-badge
                            color="blue-2"
                            text-color="blue-8"
                            :label="props.value"
                            class="q-px-sm"
                          />
                        </q-td>
                      </template>
                      <template #body-cell-salePrice="props">
                        <q-td :props="props">
                          <div class="text-weight-medium">${{ formatNumber(props.value) }}</div>
                        </q-td>
                      </template>
                      <template #body-cell-totalPrice="props">
                        <q-td :props="props">
                          <div class="text-weight-bold text-green-8">
                            ${{ formatNumber(props.value) }}
                          </div>
                        </q-td>
                      </template>
                    </q-table>
                  </q-card-section>
                </q-card>
              </div>

              <!-- 時間軸資訊 -->
              <div class="col-12">
                <q-card flat bordered class="timeline-card">
                  <q-card-section>
                    <div class="text-subtitle1 text-weight-bold text-primary q-mb-md">
                      <q-icon name="schedule" class="q-mr-sm" />
                      訂單時間軸
                    </div>
                    <q-timeline>
                      <q-timeline-entry
                        color="primary"
                        icon="add_circle"
                        :title="formatDateTime(selectedOrder.createdAt)"
                        subtitle="訂單建立"
                      />
                      <q-timeline-entry
                        v-if="selectedOrder.shippedAt"
                        color="positive"
                        icon="local_shipping"
                        :title="formatDateTime(selectedOrder.shippedAt)"
                        subtitle="訂單出貨"
                      />
                      <q-timeline-entry
                        v-if="selectedOrder.cancelledAt"
                        color="negative"
                        icon="cancel"
                        :title="formatDateTime(selectedOrder.cancelledAt)"
                        :subtitle="`訂單取消 - ${selectedOrder.cancelledReason || '無原因'}`"
                      />
                    </q-timeline>
                  </q-card-section>
                </q-card>
              </div>
            </div>
          </q-card-section>

          <q-separator />

          <q-card-actions align="right" class="bg-grey-1">
            <q-btn
              v-if="selectedOrder?.orderStatus === 'pending'"
              color="negative"
              icon="cancel"
              label="取消訂單"
              @click="showCancelDialog(selectedOrder)"
              outline
            />
            <q-btn
              v-if="selectedOrder?.orderStatus === 'pending'"
              color="positive"
              icon="local_shipping"
              label="標記已出貨"
              @click="updateOrderStatus(selectedOrder, 'shipped')"
              unelevated
            />
            <q-btn color="primary" icon="close" label="關閉" v-close-popup flat />
          </q-card-actions>
        </q-card>
      </q-dialog>

      <!-- 取消訂單對話框 -->
      <q-dialog v-model="showCancelOrderDialog" persistent>
        <q-card style="min-width: 400px">
          <q-card-section>
            <div class="text-h6 text-negative">取消訂單</div>
          </q-card-section>
          <q-card-section>
            <div class="q-mb-md">
              將作廢訂單 <strong>{{ orderToCancel?.orderNumber }}</strong>
            </div>
            <q-input
              v-model="cancelReason"
              filled
              type="textarea"
              label="取消原因"
              rows="3"
              :rules="[(val) => !!val || '請輸入取消原因']"
            />
          </q-card-section>
          <q-card-actions align="right">
            <q-btn flat label="取消" v-close-popup />
            <q-btn
              color="negative"
              label="確定取消"
              @click="confirmCancelOrder"
              :loading="cancelLoading"
            />
          </q-card-actions>
        </q-card>
      </q-dialog>
    </div>

    <!-- 新增出貨單表單 -->
    <ShippingForm v-model="showShippingForm" @sale-order-added="handleOrderAdded" />
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, reactive } from 'vue';
import { useQuasar } from 'quasar';
import { eventBus } from 'src/utils/eventBus';
import ShippingForm from 'src/components/ShippingForm.vue';
import type { QTableColumn } from 'quasar';

const $q = useQuasar();

// 訂單資料結構
interface SaleOrder {
  orderId: number;
  orderNumber: string;
  orderDate: string;
  customerName: string;
  customerPhone: string;
  customerEmail: string;
  customerAddress: string;
  orderStatus: 'pending' | 'shipped' | 'cancelled';
  totalAmount: number;
  notes: string;
  createdAt: string;
  updatedAt: string;
  shippedAt?: string;
  cancelledAt?: string;
  cancelledReason?: string;
  orderItems: OrderItem[];
}

interface OrderItem {
  orderItemId: number;
  productId: number;
  productName: string;
  productSku: string;
  quantity: number;
  salePrice: number;
  totalPrice: number;
  createdAt: string;
}
interface Order {
  orderStatus: string;
  totalAmount: number;
}

interface AnimatedValues {
  totalOrders: number;
  shippedOrders: number;
  pendingOrders: number;
  monthlyRevenue: number;
}

// 響應式數據
const orders = ref<SaleOrder[]>([]);
const loading = ref(false);
const searchQuery = ref('');
const statusFilter = ref('');
const dateRange = ref('');
const showShippingForm = ref(false);
const showOrderDetails = ref(false);
const selectedOrder = ref<SaleOrder | null>(null);
const showCancelOrderDialog = ref(false);
const orderToCancel = ref<SaleOrder | null>(null);
const cancelReason = ref('');
const cancelLoading = ref(false);
import { api } from 'boot/axios';

// 動畫數值
const animatedValues = reactive({
  totalOrders: 0,
  shippedOrders: 0,
  pendingOrders: 0,
  monthlyRevenue: 0,
});

// 分頁設定
const pagination = ref({
  sortBy: 'createdAt',
  descending: true,
  page: 1,
  rowsPerPage: 10,
  rowsNumber: 0,
});

// 狀態選項
const statusOptions = [
  { label: '全部', value: '' },
  { label: '待處理', value: 'pending' },
  { label: '已出貨', value: 'shipped' },
  { label: '已取消', value: 'cancelled' },
];

// 表格欄位定義
const columns: QTableColumn<SaleOrder>[] = [
  {
    name: 'orderNumber',
    label: '訂單編號',
    field: 'orderNumber',
    align: 'left',
    sortable: true,
  },
  {
    name: 'orderDate',
    label: '訂單日期',
    field: 'orderDate',
    align: 'left',
    sortable: true,
  },
  {
    name: 'customerName',
    label: '客戶資訊',
    field: 'customerName',
    align: 'left',
    sortable: true,
  },
  {
    name: 'orderStatus',
    label: '訂單狀態',
    field: 'orderStatus',
    align: 'center',
    sortable: true,
  },
  {
    name: 'totalAmount',
    label: '總金額',
    field: 'totalAmount',
    align: 'right',
    sortable: true,
  },
  {
    name: 'actions',
    label: '操作',
    field: () => '',
    align: 'center',
    sortable: false,
  },
];

// 訂單項目表格欄位
const orderItemColumns: QTableColumn<OrderItem>[] = [
  {
    name: 'productName',
    label: '商品資訊',
    field: 'productName',
    align: 'left',
  },
  {
    name: 'quantity',
    label: '數量',
    field: 'quantity',
    align: 'center',
  },
  {
    name: 'salePrice',
    label: '單價',
    field: 'salePrice',
    align: 'right',
  },
  {
    name: 'totalPrice',
    label: '小計',
    field: 'totalPrice',
    align: 'right',
  },
];

// 過濾後的訂單
const filteredOrders = computed(() => {
  let filtered = orders.value;

  // 搜索過濾
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    filtered = filtered.filter(
      (order) =>
        order.orderNumber.toLowerCase().includes(query) ||
        order.customerName.toLowerCase().includes(query),
    );
  }

  // 狀態過濾
  if (statusFilter.value) {
    filtered = filtered.filter((order) => order.orderStatus === statusFilter.value);
  }

  // 日期過濾
  if (dateRange.value) {
    filtered = filtered.filter((order) => order.orderDate === dateRange.value);
  }

  return filtered;
});

// 工具函數
const formatNumber = (num: number): string => {
  return Math.floor(num).toLocaleString();
};

const formatDate = (dateString: string): string => {
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-TW', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  });
};

const formatDateTime = (dateString: string): string => {
  const date = new Date(dateString);
  return date.toLocaleString('zh-TW', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  });
};

const getStatusColor = (status: string): string => {
  switch (status) {
    case 'pending':
      return 'orange';
    case 'shipped':
      return 'green';
    case 'cancelled':
      return 'red';
    default:
      return 'grey';
  }
};

const getStatusIcon = (status: string): string => {
  switch (status) {
    case 'pending':
      return 'schedule';
    case 'shipped':
      return 'local_shipping';
    case 'cancelled':
      return 'cancel';
    default:
      return 'help';
  }
};

const getStatusText = (status: string): string => {
  switch (status) {
    case 'pending':
      return '待處理';
    case 'shipped':
      return '已出貨';
    case 'cancelled':
      return '已取消';
    default:
      return '未知';
  }
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
    const easeProgress = 1 - Math.pow(1 - progress, 4);
    const currentValue = start + difference * easeProgress;

    callback(currentValue);

    if (progress < 1) {
      requestAnimationFrame(animate);
    }
  };

  requestAnimationFrame(animate);
};

function updateDashboard(orders: Order[], animatedValues: AnimatedValues) {
  const totalOrders = orders.length;
  const shippedOrders = orders.filter((o) => o.orderStatus === 'shipped').length;
  const pendingOrders = orders.filter((o) => o.orderStatus === 'pending').length;
  const monthlyRevenue = orders.reduce((sum, order) => sum + order.totalAmount, 0);

  setTimeout(() => {
    animateNumber(0, totalOrders, 1000, (value) => {
      animatedValues.totalOrders = value;
    });
    animateNumber(0, shippedOrders, 1200, (value) => {
      animatedValues.shippedOrders = value;
    });
    animateNumber(0, pendingOrders, 1400, (value) => {
      animatedValues.pendingOrders = value;
    });
    animateNumber(0, monthlyRevenue, 1600, (value) => {
      animatedValues.monthlyRevenue = value;
    });
  }, 300);
}

// API 函數
const fetchOrders = async () => {
  loading.value = true;
  try {
    const response = await api.get('/saleOrder');
    orders.value = response.data;

    // 更新分頁總數
    pagination.value.rowsNumber = orders.value.length;

    updateDashboard(orders.value, animatedValues);
  } catch (error) {
    console.error('Error fetching orders:', error);
    $q.notify({
      color: 'negative',
      message: '獲取訂單資料失敗',
      icon: 'error',
      position: 'top',
    });
  } finally {
    loading.value = false;
  }
};

const viewOrderDetails = (order: SaleOrder) => {
  selectedOrder.value = order;
  showOrderDetails.value = true;
};

function confirmDialog(): Promise<boolean> {
  return new Promise((resolve) => {
    $q.dialog({
      title: '處理中→已出貨',
      message: '將扣除庫存數量',
      ok: { label: '確定', color: 'positive' },
      cancel: { label: '取消', color: 'negative' },
      persistent: true,
    })
      .onOk(() => {
        resolve(true);
      })
      .onCancel(() => {
        resolve(false);
      });
  });
}
const updateOrderStatus = async (order: SaleOrder, newStatus: string) => {
  if (loading.value) return;

  const confirmed = await confirmDialog();
  if (!confirmed) return;

  try {
    loading.value = true; // 開啟 loading 狀態

    await api.put(`/saleOrder/${order.orderId}/status`, { status: newStatus });

    // eslint-disable-next-line
    order.orderStatus = newStatus as any;
    order.updatedAt = new Date().toISOString();
    if (newStatus === 'shipped') {
      order.shippedAt = new Date().toISOString();
    }

    updateDashboard(orders.value, animatedValues);

    $q.notify({
      color: 'positive',
      message: '訂單狀態已更新',
      icon: 'done',
      position: 'top',
    });
  } catch (error) {
    console.error('Error updating order status:', error);
    $q.notify({
      color: 'negative',
      message: '更新訂單狀態失敗',
      icon: 'error',
      position: 'top',
    });
  } finally {
    loading.value = false; // 關閉 loading 狀態
  }
};

const showCancelDialog = (order: SaleOrder) => {
  orderToCancel.value = order;
  cancelReason.value = '';
  showCancelOrderDialog.value = true;
};

const confirmCancelOrder = async () => {
  if (!orderToCancel.value) {
    // 沒有選擇要取消的訂單，直接跳出或給提示
    return;
  }
  cancelLoading.value = true;
  try {
    // 調用後端取消 API
    await api.put(`/saleOrder/${orderToCancel.value.orderId}/cancel`, {
      reason: cancelReason.value || '',
    });

    // ✅ 更新儀表板統計動畫
    updateDashboard(orders.value, animatedValues);

    $q.notify({
      color: 'positive',
      message: '訂單已取消',
      icon: 'done',
      position: 'top',
    });

    showCancelOrderDialog.value = false;
    orderToCancel.value = null;
    cancelReason.value = '';
  } catch (error) {
    console.error('Error cancelling order:', error);
    $q.notify({
      color: 'negative',
      message: '取消訂單失敗',
      icon: 'error',
      position: 'top',
    });
  } finally {
    cancelLoading.value = false;
  }
};

const handleOrderAdded = () => {
  void fetchOrders();
};

// eslint-disable-next-line
const onRequest = (props: any) => {
  // 處理分頁和排序
  pagination.value = props.pagination;
};

// 生命週期
onMounted(() => {
  void fetchOrders();

  // 監聽新增訂單事件
  eventBus.on('sale-order-added', handleOrderAdded);
});
</script>

<style scoped lang="scss">
.orders-page {
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

  .search-card {
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }

  .orders-table {
    .q-table thead th {
      font-weight: 600;
      color: #424242;
    }

    .q-table tbody tr:hover {
      background: rgba(25, 118, 210, 0.04);
    }
  }

  .order-details-card {
    .info-card {
      transition: all 0.3s ease;

      &:hover {
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
      }
    }

    .items-card {
      .detail-items-table {
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

    .timeline-card {
      .q-timeline {
        .q-timeline-entry {
          .q-timeline-entry__title {
            font-weight: 600;
            color: #424242;
          }

          .q-timeline-entry__subtitle {
            color: #616161;
          }
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

// 響應式設計
@media (max-width: 768px) {
  .orders-page {
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
}
</style>
