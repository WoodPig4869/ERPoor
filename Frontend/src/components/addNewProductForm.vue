<template>
  <q-dialog v-model="dialogModel">
    <q-card class="q-pa-md shadow-4" style="min-width: 500px; max-width: 90vw">
      <q-card-section>
        <div class="text-h6">新增產品</div>
      </q-card-section>

      <q-separator />

      <q-card-section>
        <q-form @submit.prevent="submitForm" class="q-gutter-sm" ref="formRef" :loading="loading">
          <q-input
            v-model="formData.name"
            label="商品名稱"
            dense
            filled
            :rules="[(v) => !!v || '必填']"
          />
          <q-input v-model="formData.category" label="分類" dense filled />
          <q-input
            v-model="formData.unit"
            label="單位（如:瓶、包）"
            dense
            filled
            :rules="[(v) => !!v || '必填']"
          />
          <q-input
            v-model.number="formData.expiry_alert_days"
            label="到期預警天數"
            type="number"
            dense
            filled
            :rules="[(v) => v >= 0 || '必須 >= 0']"
          />
          <q-input
            v-model.number="formData.min_stock"
            label="告警庫存量"
            type="number"
            dense
            filled
            :rules="[(v) => v >= 0 || '必須 >= 0']"
          />
          <q-input
            v-model="formData.price"
            label="價格"
            type="number"
            dense
            filled
            :rules="[(v) => v >= 0 || '必須 >= 0']"
          />
          <q-input
            v-model="formData.description"
            label="商品描述"
            type="textarea"
            dense
            filled
            autogrow
          />
          <q-toggle v-model="formData.enabled" label="是否啟用" />
        </q-form>
      </q-card-section>

      <q-card-actions align="right">
        <q-btn flat label="清空" color="secondary" @click="resetForm" />
        <q-btn label="送出" color="primary" :loading="loading" @click="submitForm" />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted } from 'vue';
import { useQuasar } from 'quasar';
import { api } from 'boot/axios';
import { eventBus } from 'src/utils/eventBus';

const $q = useQuasar();
const formRef = ref();
const loading = ref(false);
const dialogModel = defineModel<boolean | null>({ default: false });

const formData = ref({
  name: '',
  category: '',
  unit: '',
  expiry_alert_days: 30,
  min_stock: 10,
  description: '',
  enabled: true,
  price: 0,
});

function resetForm() {
  formData.value = {
    name: '',
    category: '',
    unit: '件',
    expiry_alert_days: 30,
    min_stock: 10,
    description: '',
    enabled: true,
    price: 0,
  };
  void nextTick(() => {
    formRef.value?.resetValidation();
  });
}

async function submitForm() {
  const valid = await formRef.value?.validate?.();
  if (!valid) return;

  loading.value = true;
  try {
    await api.post('/inventory/addProduct', formData.value);
    $q.notify({
      color: 'positive',
      message: '成功新增產品！',
      icon: 'check',
      position: 'top',
    });
    dialogModel.value = false;
    resetForm();
    eventBus.emit('product-added');
  } catch (err) {
    console.error('新增產品失敗', err);
    $q.notify({
      color: 'negative',
      message: '新增失敗，請檢查資料或稍後再試',
      icon: 'error',
    });
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  resetForm();
});
</script>
