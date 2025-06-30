// src/stores/purchase-store.ts
import { defineStore } from 'pinia';
import { ref } from 'vue';

export const usePurchaseStore = defineStore('purchase', () => {
  const formData = ref({
    supplier: '',
    item: '',
    quantity: 1,
    price: 0,
  });

  const resetForm = () => {
    formData.value = {
      supplier: '',
      item: '',
      quantity: 1,
      price: 0,
    };
  };

  return { formData, resetForm };
});
