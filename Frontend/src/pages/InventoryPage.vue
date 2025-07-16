<template>
  <div class="q-pa-md">
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

      <q-btn
        color="primary"
        icon="add"
        label="加入新商品"
        class="self-center"
        @click="addNewProduct"
      />
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
  <addNewProductForm v-model="showAddNewProductForm" @product-added="fetchInventoryData" />
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useQuasar } from 'quasar';
import { api } from 'boot/axios';
import { eventBus } from 'src/utils/eventBus';
import addNewProductForm from '../components/addNewProductForm.vue';

const $q = useQuasar();

const search = ref('');

const rows = ref<InventoryItem[]>([]);
const loading = ref(false);
const showAddNewProductForm = ref(false);

function addNewProduct() {
  showAddNewProductForm.value = true;
}
function onRowClick(productId: number) {
  console.log('Row clicked with productId:', productId);
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
}
</style>
