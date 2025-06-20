<template>
  <div class="q-pa-md">
    <q-input
      filled
      v-model="search"
      label="搜尋庫存"
      class="q-mb-sm"
      debounce="300"
      placeholder="輸入品名或庫別..."
    >
      <template #append>
        <q-icon name="search" />
      </template>
    </q-input>

    <q-table
      title="庫存清單"
      :rows="rows"
      :columns="columns"
      row-key="id"
      :filter="search"
      :rows-per-page-options="[10, 30, 50, 0]"
      table-header-class="bg-light-blue-3 text-subtitle1"
      flat
      bordered
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const search = ref('');

const columns = [
  { name: 'id', label: 'ID', field: 'id', align: 'left' as const, sortable: true },
  {
    name: 'productName',
    label: '品名',
    field: 'productName',
    align: 'left' as const,
    sortable: true,
  },
  { name: 'stock', label: '現有庫存', field: 'stock', align: 'right' as const, sortable: true },
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
    label: '更新時間',
    field: 'updatedAt',
    align: 'left' as const,
    sortable: true,
  },
];

const rows = [
  {
    id: 1,
    productName: '活龍蝦',
    stock: 15,
    unit: '隻',
    location: '冷藏庫',
    expiryDate: '2025-06-10',
    updatedAt: '2025-06-05 10:00',
  },
  {
    id: 2,
    productName: '鮮活花蟹',
    stock: 30,
    unit: '隻',
    location: '冷藏庫',
    expiryDate: '2025-06-08',
    updatedAt: '2025-06-05 09:30',
  },
  {
    id: 3,
    productName: '鮭魚片',
    stock: 50,
    unit: '公斤',
    location: '冷凍庫',
    expiryDate: '2025-07-01',
    updatedAt: '2025-06-04 14:20',
  },
  {
    id: 4,
    productName: '鯛魚',
    stock: 40,
    unit: '公斤',
    location: '冷藏庫',
    expiryDate: '2025-06-12',
    updatedAt: '2025-06-05 11:00',
  },
  {
    id: 5,
    productName: '草蝦',
    stock: 25,
    unit: '公斤',
    location: '冷凍庫',
    expiryDate: '2025-07-05',
    updatedAt: '2025-06-04 15:00',
  },
  {
    id: 6,
    productName: '牡蠣',
    stock: 80,
    unit: '公斤',
    location: '冷藏庫',
    expiryDate: '2025-06-09',
    updatedAt: '2025-06-05 08:50',
  },
  {
    id: 7,
    productName: '吻仔魚',
    stock: 60,
    unit: '公斤',
    location: '冷凍庫',
    expiryDate: '2025-07-10',
    updatedAt: '2025-06-03 16:30',
  },
  {
    id: 8,
    productName: '章魚',
    stock: 20,
    unit: '公斤',
    location: '冷凍庫',
    expiryDate: '2025-06-25',
    updatedAt: '2025-06-04 17:00',
  },
  {
    id: 9,
    productName: '秋刀魚',
    stock: 100,
    unit: '公斤',
    location: '冷藏庫',
    expiryDate: '2025-06-20',
    updatedAt: '2025-06-05 12:15',
  },
  {
    id: 10,
    productName: '海螺',
    stock: 15,
    unit: '隻',
    location: '冷藏庫',
    expiryDate: '2025-06-15',
    updatedAt: '2025-06-05 13:30',
  },
  {
    id: 11,
    productName: '帶子',
    stock: 70,
    unit: '公斤',
    location: '冷凍庫',
    expiryDate: '2025-07-15',
    updatedAt: '2025-06-02 14:00',
  },
  {
    id: 12,
    productName: '青口貝',
    stock: 90,
    unit: '公斤',
    location: '冷藏庫',
    expiryDate: '2025-06-14',
    updatedAt: '2025-06-05 09:10',
  },
  {
    id: 13,
    productName: '魷魚',
    stock: 55,
    unit: '公斤',
    location: '冷凍庫',
    expiryDate: '2025-07-08',
    updatedAt: '2025-06-04 11:45',
  },
  {
    id: 14,
    productName: '鰻魚',
    stock: 35,
    unit: '公斤',
    location: '冷藏庫',
    expiryDate: '2025-06-18',
    updatedAt: '2025-06-05 10:30',
  },
  {
    id: 15,
    productName: '蝦仁',
    stock: 120,
    unit: '公斤',
    location: '冷凍庫',
    expiryDate: '2025-07-20',
    updatedAt: '2025-06-03 09:20',
  },
  {
    id: 16,
    productName: '鮮干貝',
    stock: 25,
    unit: '公斤',
    location: '冷藏庫',
    expiryDate: '2025-06-22',
    updatedAt: '2025-06-05 11:50',
  },
  {
    id: 17,
    productName: '沙丁魚',
    stock: 80,
    unit: '公斤',
    location: '冷凍庫',
    expiryDate: '2025-07-02',
    updatedAt: '2025-06-04 13:10',
  },
  {
    id: 18,
    productName: '海帶',
    stock: 150,
    unit: '公斤',
    location: '冷藏庫',
    expiryDate: '2025-08-01',
    updatedAt: '2025-06-05 14:00',
  },
  {
    id: 19,
    productName: '魚卵',
    stock: 10,
    unit: '公斤',
    location: '冷藏庫',
    expiryDate: '2025-06-12',
    updatedAt: '2025-06-05 15:20',
  },
  {
    id: 20,
    productName: '海苔',
    stock: 200,
    unit: '包',
    location: '常溫區',
    expiryDate: '2025-12-31',
    updatedAt: '2025-06-01 10:00',
  },
];
</script>
