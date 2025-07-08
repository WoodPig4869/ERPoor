<template>
  <q-dialog v-model="dialogModel">
    <q-card class="q-pa-md shadow-4" style="min-width: 400px; max-width: 90vw">
      <q-card-section>
        <div class="text-h6">新進貨單</div>
      </q-card-section>

      <q-separator />

      <q-card-section>
        <q-form @submit.prevent="submitForm" class="q-gutter-sm" ref="formRef" :loading="loading">
          <q-select
            v-model="formData.productId"
            label="商品名稱"
            :options="productOptions"
            option-value="productId"
            option-label="name"
            dense
            filled
            emit-value
            map-options
            :rules="[(val) => !!val || '必填']"
          />
          <q-input
            v-model="formData.quantity"
            label="進貨數量"
            type="number"
            dense
            filled
            :rules="[(val) => val >= 0 || '必須大於等於 0']"
          />
          <q-input
            v-model="formData.price"
            label="進貨單價"
            type="number"
            dense
            filled
            :rules="[(val) => val >= 0 || '必須大於等於 0']"
          />
          <q-input
            v-model="formData.supplierName"
            label="供應商名稱"
            dense
            filled
            :rules="[(val) => !!val || '必填']"
          />
          <q-input
            v-model="formData.receivedDate"
            label="進貨日期"
            type="date"
            dense
            filled
            :rules="[(val) => !!val || '必填']"
          />
          <q-input
            v-model="formData.expirationDate"
            label="有效期限"
            type="date"
            dense
            filled
            :rules="[(val) => !!val || '必填']"
          />
        </q-form>
      </q-card-section>

      <q-card-actions align="right">
        <q-btn flat label="清空" color="secondary" type="button" @click="resetForm" />
        <q-btn label="送出" color="primary" :loading="loading" @click="submitForm" />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted } from 'vue';
import { api } from 'boot/axios';
import { useQuasar } from 'quasar';
import { eventBus } from 'src/utils/eventBus';

const $q = useQuasar();

const formData = ref({
  productId: '',
  quantity: '',
  price: '',
  receivedDate: new Date().toISOString().split('T')[0],
  expirationDate: '',
  supplierName: '凱亞良品',
  supplierId: 1,
});

interface ProductOption {
  productId: number;
  name: string;
}

const productOptions = ref<ProductOption[]>([]);
const formRef = ref();
const loading = ref(false);
const dialogModel = defineModel<boolean | null>({ default: false });

onMounted(async () => {
  await getProductNameOptions(); // 使用 await 確保 Promise 被處理
  resetForm();
  eventBus.on('product-added', () => {
    void getProductNameOptions();
  });
});

async function getProductNameOptions() {
  try {
    const { data } = await api.get('/inventory/getProductNameOptions');
    productOptions.value = data;
  } catch (error) {
    console.log('獲取商品列表失敗', error);
  }
}

async function submitForm() {
  const valid = await formRef.value?.validate?.();
  if (!valid) return;

  loading.value = true;
  console.log(formData.value);
  try {
    await api.post('/inventory/purchase', formData.value);
    $q.notify({
      color: 'positive',
      message: '已新增進貨單',
      icon: 'done',
      position: 'top',
    });
    dialogModel.value = false;
    resetForm();
    eventBus.emit('productBatch-added');
  } catch (error) {
    console.error('送出失敗', error);
  } finally {
    loading.value = false;
  }
}

function resetForm() {
  formData.value = {
    productId: '',
    quantity: '',
    price: '',
    receivedDate: new Date().toLocaleDateString('en-CA', {
      timeZone: 'Asia/Taipei',
    }),
    expirationDate: '',
    supplierName: '凱亞良品',
    supplierId: 1,
  };
  void nextTick(() => {
    formRef.value?.resetValidation();
  });
}
</script>
