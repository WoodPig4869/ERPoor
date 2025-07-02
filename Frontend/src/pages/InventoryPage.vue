<template>
  <div class="q-pa-md">
    <q-input
      filled
      v-model="search"
      label="搜尋庫存"
      class="q-mb-md"
      debounce="300"
      placeholder="輸入品名或庫別..."
      clearable
      dense
    >
      <template #append>
        <q-icon name="search" />
      </template>
    </q-input>

    <q-table
      title="📦 庫存清單"
      :rows="rows"
      :columns="columns"
      row-key="id"
      :filter="search"
      :rows-per-page-options="[10, 30, 50, 0]"
      table-header-class="bg-light-blue-2 text-weight-bold"
      table-header-style="color: #5a6c7d"
      flat
      bordered
      separator="cell"
      class="rounded-borders"
    >
      <template #body="props">
        <q-tr :props="props" @click="onRowClick(props.row.id)" class="cursor-pointer">
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
            <template v-else-if="col.name === 'expiryDate'">
              {{ props.row.expiryDate }}
              <q-chip
                v-if="isNearingExpiry(props.row.expiryDate)"
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
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useQuasar } from 'quasar';

const $q = useQuasar();

const search = ref('');

// Function to handle product click, can be expanded to show product details
function onRowClick(productId: number) {
  $q.notify({
    color: 'primary',
    message: '點擊品項ID:' + productId.toString(),
    icon: 'info',
    position: 'top',
    timeout: 1000,
  });
  // In a real application, you might open a dialog or navigate to a detail page
  console.log(`Product with ID ${productId} clicked.`);
}

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
    name: 'productName',
    label: '品名',
    field: 'productName',
    align: 'left' as const,
    sortable: true,
  },
  {
    name: 'stock',
    label: '現有庫存',
    field: 'stock',
    align: 'right' as const,
    sortable: true,
  },
  { name: 'unit', label: '單位', field: 'unit', align: 'left' as const }, // 例如公斤、箱、包
  { name: 'location', label: '庫別', field: 'location', align: 'left' as const }, // 例如：冷藏庫、冷凍庫、常溫區
  {
    name: 'expiryDate',
    label: '有效期限',
    field: 'expiryDate',
    align: 'left' as const,
    sortable: true,
  },
  {
    name: 'updatedAt',
    label: '最後進貨',
    field: 'updatedAt',
    align: 'left' as const,
    sortable: true,
  },
];

// Mock data for the table rows
const rows = [
  {
    id: 1,
    productName: '活龍蝦',
    stock: 15,
    unit: '隻',
    location: '冷藏庫',
    expiryDate: '2025-07-05', // Nearing expiry for demonstration
    updatedAt: '2025-07-01 10:00',
  },
  {
    id: 2,
    productName: '鮮活花蟹',
    stock: 30,
    unit: '隻',
    location: '冷藏庫',
    expiryDate: '2025-07-20',
    updatedAt: '2025-07-01 09:30',
  },
  {
    id: 3,
    productName: '美國牛小排',
    stock: 5, // Low stock for demonstration
    unit: '公斤',
    location: '冷凍庫',
    expiryDate: '2025-12-01',
    updatedAt: '2025-06-30 14:00',
  },
  {
    id: 4,
    productName: '國產豬五花',
    stock: 22,
    unit: '公斤',
    location: '冷凍庫',
    expiryDate: '2025-11-15',
    updatedAt: '2025-06-30 13:00',
  },
  {
    id: 5,
    productName: '新鮮鮭魚片',
    stock: 8, // Low stock for demonstration
    unit: '片',
    location: '冷藏庫',
    expiryDate: '2025-07-03', // Nearing expiry
    updatedAt: '2025-07-02 08:00',
  },
  {
    id: 6,
    productName: '有機雞蛋',
    stock: 120,
    unit: '顆',
    location: '常溫區',
    expiryDate: '2025-07-25',
    updatedAt: '2025-07-01 11:00',
  },
  {
    id: 7,
    productName: '進口橄欖油',
    stock: 50,
    unit: '瓶',
    location: '常溫區',
    expiryDate: '2026-06-30',
    updatedAt: '2025-06-29 16:00',
  },
  {
    id: 8,
    productName: '台灣鯛魚',
    stock: 18,
    unit: '條',
    location: '冷凍庫',
    expiryDate: '2025-10-10',
    updatedAt: '2025-06-28 10:30',
  },
  {
    id: 9,
    productName: '北海道生食級干貝',
    stock: 7, // Low stock
    unit: '顆',
    location: '冷凍庫',
    expiryDate: '2025-07-06', // Nearing expiry
    updatedAt: '2025-07-02 09:00',
  },
  {
    id: 10,
    productName: '阿拉斯加鱈魚',
    stock: 25,
    unit: '片',
    location: '冷凍庫',
    expiryDate: '2025-11-20',
    updatedAt: '2025-06-27 15:00',
  },
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
  background-color: lighten($primary, 40%); /* Light highlight on hover */
  cursor: pointer;
}

/* Optional: Add a subtle border radius to the table for a softer look */
.rounded-borders {
  border-radius: 8px;
}
</style>
