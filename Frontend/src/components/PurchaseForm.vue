<template>
  <q-dialog v-model="dialogOpen">
    <q-card class="q-pa-md shadow-4" style="min-width: 400px; max-width: 90vw">
      <q-card-section>
        <div class="text-h6">加入新商品</div>
      </q-card-section>

      <q-separator />

      <q-card-section>
        <q-form @submit.prevent="submitForm" class="q-gutter-sm" ref="formRef" :loading="loading">
          <q-select
            v-model="formData.name"
            label="商品名稱"
            :options="productNameOptions"
            dense
            filled
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

const formData = ref({
  name: '',
  quantity: '',
  price: '',
  receivedDate: new Date().toISOString().split('T')[0],
  expirationDate: '',
  supplierName: '凱亞良品',
});

const productNameOptions = ref<string[]>([]);
const formRef = ref();
const loading = ref(false);
const dialogOpen = ref(false);

onMounted(async () => {
  await getProductNameOptions(); // 使用 await 確保 Promise 被處理
  resetForm();
});

async function getProductNameOptions() {
  try {
    const { data } = await api.get('/inventory/getProductNameOptions');
    productNameOptions.value = data;
  } catch (error) {
    console.log('獲取商品名稱失敗', error);
  }
}

async function submitForm() {
  const valid = await formRef.value?.validate?.();
  if (!valid) return;

  loading.value = true;
  console.log(formData.value);
  try {
    const { data } = await api.post('/inventory/purchase', formData.value);
    console.log('送出成功', data);
    dialogOpen.value = false;
    resetForm();
  } catch (error) {
    console.error('送出失敗', error);
  } finally {
    loading.value = false;
  }
}

function resetForm() {
  formData.value = {
    name: '',
    quantity: '',
    price: '',
    receivedDate: new Date().toISOString().split('T')[0],
    expirationDate: '',
    supplierName: '凱亞良品',
  };
  void nextTick(() => {
    formRef.value?.resetValidation();
  });
}
</script>
