<template>
  <q-dialog v-model="dialogModel">
    <q-card class="q-pa-md shadow-4" style="min-width: 320px; max-width: 95vw; width: 100%">
      <q-card-section>
        <div class="text-h6">新出貨單</div>
      </q-card-section>

      <q-separator />

      <q-card-section>
        <q-form @submit.prevent="submitForm" class="q-gutter-sm" ref="formRef" :loading="loading">
          <!-- 客戶資訊 -->
          <div class="text-subtitle2 text-grey-7 q-mb-sm">客戶資訊</div>
          <div class="row q-gutter-sm">
            <div class="col-12 col-sm-6 col-md-4">
              <q-input
                v-model="formData.customerName"
                label="客戶名稱"
                dense
                filled
                :rules="[(val) => !!val || '必填']"
              />
            </div>
            <div class="col-12 col-sm-6 col-md-4">
              <q-input v-model="formData.customerPhone" label="客戶電話" dense filled />
            </div>
            <div class="col-12 col-md-4">
              <q-input
                v-model="formData.customerEmail"
                label="客戶Email"
                type="email"
                dense
                filled
              />
            </div>
          </div>

          <div class="row">
            <div class="col-12">
              <q-input
                v-model="formData.customerAddress"
                label="客戶地址"
                dense
                filled
                type="text"
                rows="2"
              />
            </div>
          </div>

          <!-- 訂單資訊 -->
          <div class="text-subtitle2 text-grey-7 q-mb-sm q-mt-md">訂單資訊</div>
          <div class="row q-gutter-sm">
            <div class="col-12 col-sm-6">
              <q-input
                v-model="formData.orderDate"
                label="訂單日期"
                type="date"
                dense
                filled
                :rules="[(val) => !!val || '必填']"
              />
            </div>
            <div class="col-12 col-sm-6">
              <q-input v-model="formData.notes" label="備註" dense filled />
            </div>
          </div>

          <!-- 商品項目 -->
          <div class="text-subtitle2 text-grey-7 q-mb-sm q-mt-md">商品項目</div>
          <div v-for="(item, index) in formData.orderItems" :key="index" class="q-mb-md">
            <q-card flat bordered class="q-pa-md">
              <!-- 手機版：垂直布局 -->
              <div class="lt-sm">
                <div class="q-mb-sm">
                  <q-select
                    v-model="item.productId"
                    label="商品名稱"
                    :options="productOptions"
                    option-value="productId"
                    option-label="name"
                    dense
                    filled
                    emit-value
                    map-options
                    :rules="[(val) => !!val || '必填']"
                    @update:model-value="(val) => updateProductInfo(index, val)"
                  />
                </div>
                <div class="row q-gutter-sm q-mb-sm">
                  <div class="col-6">
                    <q-input
                      v-model.number="item.quantity"
                      label="數量"
                      type="number"
                      dense
                      filled
                      :rules="[(val) => val > 0 || '必須大於 0']"
                      @update:model-value="calculateItemTotal(index)"
                    />
                  </div>
                  <div class="col-6">
                    <q-input
                      v-model.number="item.salePrice"
                      label="售價"
                      type="number"
                      step="0.01"
                      dense
                      filled
                      :rules="[(val) => val >= 0 || '必須大於等於 0']"
                      @update:model-value="calculateItemTotal(index)"
                    />
                  </div>
                </div>
                <div class="row q-gutter-sm items-center">
                  <div class="col-8">
                    <q-input
                      v-model="item.totalPrice"
                      label="小計"
                      dense
                      filled
                      readonly
                      prefix="$"
                    />
                  </div>
                  <div class="col-4 flex justify-center">
                    <q-btn
                      icon="delete"
                      color="negative"
                      flat
                      dense
                      @click="removeOrderItem(index)"
                      :disable="formData.orderItems.length <= 1"
                    />
                  </div>
                </div>
              </div>

              <!-- 平板和桌機版：水平布局 -->
              <div class="gt-xs">
                <div class="row q-gutter-sm items-end">
                  <div class="col-12 col-sm-5 col-md-4">
                    <q-select
                      v-model="item.productId"
                      label="商品名稱"
                      :options="productOptions"
                      option-value="productId"
                      option-label="name"
                      dense
                      filled
                      emit-value
                      map-options
                      :rules="[(val) => !!val || '必填']"
                      @update:model-value="(val) => updateProductInfo(index, val)"
                    />
                  </div>
                  <div class="col-4 col-sm-2">
                    <q-input
                      v-model.number="item.quantity"
                      label="數量"
                      type="number"
                      dense
                      filled
                      :rules="[(val) => val > 0 || '必須大於 0']"
                      @update:model-value="calculateItemTotal(index)"
                    />
                  </div>
                  <div class="col-4 col-sm-2">
                    <q-input
                      v-model.number="item.salePrice"
                      label="售價"
                      type="number"
                      step="0.01"
                      dense
                      filled
                      :rules="[(val) => val >= 0 || '必須大於等於 0']"
                      @update:model-value="calculateItemTotal(index)"
                    />
                  </div>
                  <div class="col-4 col-sm-2">
                    <q-input
                      v-model="item.totalPrice"
                      label="小計"
                      dense
                      filled
                      readonly
                      prefix="$"
                      hint=" "
                    />
                  </div>
                  <div
                    class="col-12 col-sm-1 flex justify-center items-end"
                    style="padding-bottom: 18px"
                  >
                    <q-btn
                      icon="delete"
                      color="negative"
                      flat
                      dense
                      @click="removeOrderItem(index)"
                      :disable="formData.orderItems.length <= 1"
                    />
                  </div>
                </div>
              </div>
            </q-card>
          </div>

          <!-- 新增商品項目按鈕 -->
          <q-btn
            label="新增商品項目"
            icon="add"
            color="secondary"
            outline
            @click="addOrderItem"
            class="q-mb-md full-width-mobile"
          />

          <!-- 總計 -->
          <q-card flat bordered class="q-pa-md bg-grey-1">
            <div class="row justify-end">
              <div class="text-h6 text-primary">總計：${{ totalAmount.toFixed(2) }}</div>
            </div>
          </q-card>
        </q-form>
      </q-card-section>

      <q-card-actions align="right" class="q-gutter-sm">
        <q-btn
          flat
          label="清空"
          color="secondary"
          type="button"
          @click="resetForm"
          class="mobile-full-width"
        />
        <q-btn
          label="送出"
          color="primary"
          :loading="loading"
          @click="submitForm"
          class="mobile-full-width"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted, computed } from 'vue';
import { api } from 'boot/axios';
import { useQuasar } from 'quasar';
import { eventBus } from 'src/utils/eventBus';
import { useMainLayoutStore } from 'src/stores/MainLayout-store';

const layoutStore = useMainLayoutStore();

const $q = useQuasar();

interface OrderItem {
  productId: number | null;
  productName: string;
  productSku: string;
  quantity: number;
  salePrice: number;
  totalPrice: number;
}

interface SaleOrderForm {
  orderDate: string;
  customerName: string;
  customerPhone: string;
  customerEmail: string;
  customerAddress: string;
  orderStatus: string;
  notes: string;
  orderItems: OrderItem[];
}

const formData = ref<SaleOrderForm>({
  orderDate: new Date().toISOString().split('T')[0] as string,
  customerName: '',
  customerPhone: '',
  customerEmail: '',
  customerAddress: '',
  orderStatus: 'pending',
  notes: '',
  orderItems: [
    {
      productId: null,
      productName: '',
      productSku: '',
      quantity: 1,
      salePrice: 0,
      totalPrice: 0,
    },
  ],
});

interface ProductOption {
  productId: number;
  name: string;
  sku: string;
  price: number;
}

const productOptions = ref<ProductOption[]>([]);
const formRef = ref();
const loading = ref(false);
const dialogModel = defineModel<boolean | null>({ default: false });

// 計算總金額
const totalAmount = computed(() => {
  return formData.value.orderItems.reduce((sum, item) => sum + (item.totalPrice || 0), 0);
});

onMounted(async () => {
  await getProductOptions();
  resetForm();
  eventBus.on('product-added', () => {
    void getProductOptions();
  });
});

async function getProductOptions() {
  try {
    const { data } = await api.get('/saleOrder/products');
    productOptions.value = data;
  } catch (error) {
    console.log('獲取商品列表失敗', error);
  }
}

function updateProductInfo(index: number, productId: number) {
  const product = productOptions.value.find((p) => p.productId === productId);
  if (product) {
    const orderItem = formData.value.orderItems[index];
    if (orderItem) {
      orderItem.productName = product.name;
      orderItem.salePrice = product.price;
      calculateItemTotal(index);
    }
  }
}

function calculateItemTotal(index: number) {
  const item = formData.value.orderItems[index];
  if (item) {
    item.totalPrice = (item.quantity || 0) * (item.salePrice || 0);
  }
}

function addOrderItem() {
  formData.value.orderItems.push({
    productId: null,
    productName: '',
    productSku: '',
    quantity: 1,
    salePrice: 0,
    totalPrice: 0,
  });
}

function removeOrderItem(index: number) {
  if (formData.value.orderItems.length > 1) {
    formData.value.orderItems.splice(index, 1);
  }
}

async function submitForm() {
  const valid = await formRef.value?.validate?.();
  if (!valid) return;

  // 驗證是否有選擇商品
  const hasEmptyProduct = formData.value.orderItems.some((item) => !item.productId);
  if (hasEmptyProduct) {
    $q.notify({
      color: 'negative',
      message: '請選擇所有商品項目',
      icon: 'warning',
      position: 'top',
    });
    return;
  }

  loading.value = true;

  // 準備送出的資料
  const submitData = {
    ...formData.value,
    totalAmount: totalAmount.value,
    orderItems: formData.value.orderItems.map((item) => ({
      productId: item.productId,
      productName: item.productName,
      productSku: item.productSku,
      quantity: item.quantity,
      salePrice: item.salePrice,
    })),
  };

  console.log('送出資料:', submitData);

  try {
    await api.post('/saleOrder', submitData);
    $q.notify({
      color: 'positive',
      message: '已新增出貨單',
      icon: 'done',
      position: 'top',
    });
    dialogModel.value = false;
    layoutStore.incrementOrdersBadge();
    resetForm();
    eventBus.emit('sale-order-added');
  } catch (error) {
    console.error('送出失敗', error);
    $q.notify({
      color: 'negative',
      message: '送出失敗，請稍後再試',
      icon: 'error',
      position: 'top',
    });
  } finally {
    loading.value = false;
  }
}

function resetForm() {
  formData.value = {
    orderDate: new Date().toLocaleDateString('en-CA', {
      timeZone: 'Asia/Taipei',
    }),
    customerName: '',
    customerPhone: '',
    customerEmail: '',
    customerAddress: '',
    orderStatus: 'pending',
    notes: '',
    orderItems: [
      {
        productId: null,
        productName: '',
        productSku: '',
        quantity: 1,
        salePrice: 0,
        totalPrice: 0,
      },
    ],
  };
  void nextTick(() => {
    formRef.value?.resetValidation();
  });
}
</script>

<style scoped>
.full-width-mobile {
  width: 100%;
}

@media (min-width: 600px) {
  .full-width-mobile {
    width: auto;
  }
}

.mobile-full-width {
  min-width: 100px;
}

@media (max-width: 599px) {
  .mobile-full-width {
    width: 100%;
  }
}
</style>
