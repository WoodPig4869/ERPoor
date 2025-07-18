import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useMainLayoutStore = defineStore('mainLayout', () => {
  const inventoryBadge = ref(0);
  const ordersBadge = ref(0);

  // +1
  const incrementInventoryBadge = () => {
    inventoryBadge.value++;
  };

  const incrementOrdersBadge = () => {
    ordersBadge.value++;
  };

  // 歸零
  const resetInventoryBadge = () => {
    inventoryBadge.value = 0;
  };

  const resetOrdersBadge = () => {
    ordersBadge.value = 0;
  };

  return {
    inventoryBadge,
    ordersBadge,
    incrementInventoryBadge,
    incrementOrdersBadge,
    resetInventoryBadge,
    resetOrdersBadge,
  };
});
