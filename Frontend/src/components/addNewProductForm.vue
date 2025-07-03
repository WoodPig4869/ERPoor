<template>
  <q-dialog v-model="dialogOpen">
    <q-card class="q-pa-md shadow-4" style="min-width: 400px; max-width: 90vw">
      <q-card-section>
        <div class="text-h6">加入新商品</div>
      </q-card-section>

      <q-separator />

      <q-card-section>
        <q-form @submit.prevent="submitForm" class="q-gutter-sm" ref="formRef" :loading="loading">
          <q-input
            v-model="formData.name"
            label="商品名稱"
            type="text"
            dense
            filled
            :rules="[(val) => !!val || '必填']"
          />
          <q-input
            v-model="formData.category"
            label="分類（如：虱目魚系列）"
            type="text"
            dense
            filled
            :rules="[(val) => !!val || '必填']"
          />
          <q-input
            v-model="formData.price"
            label="單價"
            type="number"
            dense
            filled
            :rules="[(val) => val > 0 || '必須大於 0']"
          />
          <q-input
            v-model="formData.unit"
            label="計量單位(如：箱、包)"
            type="text"
            dense
            filled
            :rules="[(val) => !!val || '必填']"
          />
          <q-input
            v-model="formData.description"
            label="簡述"
            type="textarea"
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
import { ref, nextTick } from 'vue';
import { api } from 'boot/axios';
import { usePurchaseStore } from '../stores/newProduct-store';

const store = usePurchaseStore();
const formData = ref({
  name: '',
  category: '',
  price: 0,
  unit: '',
  description: '',
});

const formRef = ref();
const loading = ref(false);
const dialogOpen = ref(false);

async function submitForm() {
  const valid = await formRef.value?.validate?.();
  if (!valid) return;

  loading.value = true;
  console.log(formData.value);
  try {
    const { data } = await api.post('/inventory/addProduct', formData.value);
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
  void store.resetForm?.(); // 明確標記忽略 Promise
  void nextTick(() => {
    formRef.value?.resetValidation();
  });
}
</script>
