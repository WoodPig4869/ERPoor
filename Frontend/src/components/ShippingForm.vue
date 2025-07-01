<template>
  <q-dialog v-model="dialogOpen">
    <q-card class="q-pa-md shadow-4" style="min-width: 400px; max-width: 90vw">
      <q-card-section>
        <div class="text-h6">新增進貨單</div>
      </q-card-section>

      <q-separator />

      <q-card-section>
        <q-form @submit.prevent="submitForm" class="q-gutter-sm" ref="formRef">
          <q-input
            v-model="formData.supplier"
            label="供應商名稱"
            dense
            filled
            :rules="[(val) => !!val || '必填']"
          />
          <q-input
            v-model="formData.item"
            label="品項名稱"
            dense
            filled
            :rules="[(val) => !!val || '必填']"
          />
          <q-input
            v-model="formData.quantity"
            label="數量"
            type="number"
            dense
            filled
            :rules="[(val) => val > 0 || '必須大於 0']"
          />
          <q-input
            v-model="formData.price"
            label="單價"
            type="number"
            dense
            filled
            :rules="[(val) => val >= 0 || '不得為負數']"
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
import { ref, toRef, nextTick } from 'vue';
import { api } from 'boot/axios';
import { usePurchaseStore } from '../stores/purchase-store';

const store = usePurchaseStore();
const formData = toRef(store, 'formData'); // 保持響應式連接

const formRef = ref();
const loading = ref(false);
const dialogOpen = ref(false);

async function submitForm() {
  const valid = await formRef.value?.validate?.();
  if (!valid) return;

  loading.value = true;
  try {
    const payload = {
      supplierName: formData.value.supplier,
      itemName: formData.value.item,
      quantity: Number(formData.value.quantity),
      unitPrice: Number(formData.value.price),
    };

    const { data } = await api.post('/api/purchase', payload);
    console.log('送出成功', data);
    dialogOpen.value = false;
    store.resetForm();
  } catch (error) {
    console.error('送出失敗', error);
  } finally {
    loading.value = false;
  }
}

function resetForm() {
  void store.resetForm?.(); // 明確標記忽略 Promise
  void nextTick(() => {
    formRef.value?.resetValidation();
  });
}
</script>
