// src/stores/purchase-store.ts
import { defineStore } from 'pinia';
import { ref } from 'vue';

export const usePurchaseStore = defineStore('newProduct', () => {
  const formData = ref({
    name: '',
    category: '',
    unit: '',
    price: 1,
    description: '',
  });

  const resetForm = () => {
    formData.value = {
      name: '',
      category: '',
      unit: '',
      price: 1,
      description: '',
    };
  };

  return { formData, resetForm };
});
