<template>
  <q-dialog v-model="dialogModel">
    <q-card class="q-pa-md shadow-4" style="min-width: 400px; max-width: 90vw">
      <q-card-section>
        <div class="text-h6">盤點庫存</div>
      </q-card-section>

      <q-separator />

      <q-card-section>
        <q-form @submit.prevent="submitForm" class="q-gutter-sm" ref="formRef" :loading="loading">
          <q-select
            v-model="selectedProduct"
            label="商品名稱"
            :options="productOptions"
            option-value="productId"
            option-label="name"
            dense
            filled
            emit-value
            map-options
            @update:model-value="onProductChange"
            :rules="[(val) => !!val || '必填']"
          />

          <q-input
            v-model="expectedQuantity"
            label="系統預期庫存"
            type="number"
            dense
            filled
            readonly
            :rules="[(val) => !!val || '請先選擇商品']"
          />

          <q-input
            v-model="formData.actualQuantity"
            label="實際盤點數量"
            type="number"
            dense
            filled
            :rules="[(val) => val >= 0 || '必須大於等於 0']"
          />

          <!-- 差異顯示 -->
          <div v-if="showDifference" class="q-mt-md">
            <q-banner :class="differenceClass" rounded>
              <template v-slot:avatar>
                <q-icon :name="differenceIcon" />
              </template>
              差異：{{ difference }} ({{
                difference > 0 ? '盤盈' : difference < 0 ? '盤虧' : '一致'
              }})
            </q-banner>
          </div>
        </q-form>
      </q-card-section>

      <q-card-actions align="right">
        <q-btn flat label="清空" color="secondary" type="button" @click="resetForm" />
        <q-btn label="送出" color="primary" :loading="loading" @click="handleSubmit" />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, onMounted } from 'vue';
import { api } from 'boot/axios';
import { useQuasar } from 'quasar';
import { eventBus } from 'src/utils/eventBus';
import { useMainLayoutStore } from 'src/stores/MainLayout-store';

const layoutStore = useMainLayoutStore();
const $q = useQuasar();

interface ProductOption {
  productId: number;
  name: string;
  totalStock: number;
}

interface InventoryCheckData {
  productName: string;
  expectedQuantity: number;
  actualQuantity: number;
}

const formData = ref<InventoryCheckData>({
  productName: '',
  expectedQuantity: 0,
  actualQuantity: 0,
});

const productOptions = ref<ProductOption[]>([]);
const selectedProduct = ref<number | null>(null);
const expectedQuantity = ref<number>(0);
const formRef = ref();
const loading = ref(false);
const dialogModel = defineModel<boolean | null>({ default: false });

// 計算差異
const difference = computed(() => {
  return formData.value.actualQuantity - formData.value.expectedQuantity;
});

const showDifference = computed(() => {
  return formData.value.actualQuantity !== 0 && formData.value.expectedQuantity !== 0;
});

const differenceClass = computed(() => {
  if (difference.value > 0) return 'bg-positive text-white';
  if (difference.value < 0) return 'bg-negative text-white';
  return 'bg-info text-white';
});

const differenceIcon = computed(() => {
  if (difference.value > 0) return 'trending_up';
  if (difference.value < 0) return 'trending_down';
  return 'check_circle';
});

onMounted(async () => {
  await getInventoryCheckOptions();
  resetForm();
});

async function getInventoryCheckOptions() {
  try {
    const { data } = await api.get('/inventory/productInventoryView');
    productOptions.value = data;
  } catch (error) {
    console.log('獲取庫存盤點選項失敗', error);
    $q.notify({
      color: 'negative',
      message: '獲取商品庫存資料失敗',
      icon: 'error',
      position: 'top',
    });
  }
}

function onProductChange(productId: number) {
  const selectedProductData = productOptions.value.find((p) => p.productId === productId);
  if (selectedProductData) {
    formData.value.productName = selectedProductData.name;
    formData.value.expectedQuantity = selectedProductData.totalStock;
    expectedQuantity.value = selectedProductData.totalStock;
    formData.value.actualQuantity = selectedProductData.totalStock;
  }
}

async function submitForm() {
  const valid = await formRef.value?.validate?.();
  if (!valid) return;

  loading.value = true;

  const submitData = {
    productName: formData.value.productName,
    expectedQuantity: formData.value.expectedQuantity,
    actualQuantity: Number(formData.value.actualQuantity),
  };

  console.log('盤點資料:', submitData);

  try {
    await api.post('/inventory/inventory-check', submitData);

    const message =
      difference.value === 0 ? '盤點完成，庫存一致' : `盤點完成，差異：${difference.value}`;

    $q.notify({
      color: difference.value === 0 ? 'positive' : 'warning',
      message,
      icon: difference.value === 0 ? 'done' : 'info',
      position: 'top',
    });

    dialogModel.value = false;
    layoutStore.incrementInventoryBadge();
    resetForm();
    eventBus.emit('inventory-checked');
  } catch (error) {
    console.error('盤點送出失敗', error);
    $q.notify({
      color: 'negative',
      message: '盤點送出失敗，請稍後再試',
      icon: 'error',
      position: 'top',
    });
  } finally {
    loading.value = false;
  }
}

function handleSubmit() {
  const actual = formData.value.actualQuantity;
  const expected = formData.value.expectedQuantity;
  const product = formData.value.productName;

  if (actual === expected) {
    // ✅ 庫存一致的確認對話框
    $q.dialog({
      title: '✅ 盤點確認',
      message: '庫存一致，將記錄盤點時間',
      html: true,
      ok: {
        label: '確認盤點',
        color: 'positive',
        flat: false,
      },
      cancel: {
        label: '取消',
        color: 'grey',
        flat: true,
      },
      persistent: true,
    }).onOk(() => {
      void submitForm();
    });
  } else {
    // ⚠️ 有差異時的紅色提示框
    $q.dialog({
      title: '<div class="text-h6 text-weight-bold text-red">⚠️ 盤點確認</div>',
      message: `
        <div class="q-mt-sm">
          <p class="text-weight-bold">您即將執行<b class="text-red">庫存修正</b>：</p>
          <ul>
            <li>產品: <b>${product}</b></li>
            <li>原數量: <b>${expected}</b></li>
            <li>修正為: <b class="text-red">${actual}</b></li>
          </ul>
        </div>
      `,
      html: true,
      ok: {
        label: '確認修正',
        color: 'negative',
        flat: false,
      },
      cancel: {
        label: '取消',
        color: 'grey',
        flat: true,
      },
      persistent: true,
    }).onOk(() => {
      void submitForm();
    });
  }
}

function resetForm() {
  formData.value = {
    productName: '',
    expectedQuantity: 0,
    actualQuantity: 0,
  };
  selectedProduct.value = null;
  expectedQuantity.value = 0;

  void nextTick(() => {
    formRef.value?.resetValidation();
  });
}
</script>
