<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated>
      <q-toolbar>
        <q-btn flat dense round icon="menu" aria-label="Menu" @click="toggleLeftDrawer" />

        <q-toolbar-title> 凱亞 ERP 系統 </q-toolbar-title>

        <q-btn flat round dense icon="inventory_2" size="lg" @click="showPurchaseForm = true">
          <q-tooltip class="bg-indigo text-body2 shadow-4" :offset="[10, 10]"> 進貨 </q-tooltip>
        </q-btn>
        <PurchaseForm v-model="showPurchaseForm" />
        <q-btn flat round dense icon="local_shipping" size="lg" @click="showShippingForm = true">
          <q-tooltip class="bg-purple text-body2 shadow-4" :offset="[10, 10]"> 出貨 </q-tooltip>
        </q-btn>
        <ShippingForm v-model="showShippingForm" />
        <q-btn flat round dense icon="checklist" size="lg" @click="showInventoryForm = true">
          <q-tooltip class="bg-amber text-body2 shadow-4" :offset="[10, 10]"> 盤點 </q-tooltip>
        </q-btn>
        <InventoryForm v-model="showInventoryForm" />
        <q-btn flat round dense :icon="theme" size="mg" @click="toggleTheme" />
      </q-toolbar>
    </q-header>

    <q-drawer v-model="leftDrawerOpen" show-if-above bordered>
      <q-list>
        <q-item-label header class="text-grey-7 text-weight-bold text-h7 text-center">
          {{ nickNameAndUsername }}
        </q-item-label>

        <q-list>
          <q-item
            v-for="item in menuItems"
            :key="item.to"
            :to="item.to"
            clickable
            exact
            v-ripple
            active-class="active"
          >
            <q-item-section avatar>
              <q-icon :name="item.icon" />
            </q-item-section>

            <q-item-section>
              <q-item-label>{{ item.title }}</q-item-label>
              <q-item-label caption class="caption">{{ item.caption }}</q-item-label>
            </q-item-section>
          </q-item>
        </q-list>

        <EssentialLink v-for="link in linksList" :key="link.title" v-bind="link" />
      </q-list>
    </q-drawer>
    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
// import { useI18n } from 'vue-i18n';
import EssentialLink, { type EssentialLinkProps } from 'components/EssentialLink.vue';
import PurchaseForm from 'src/components/PurchaseForm.vue';
import { useQuasar } from 'quasar';

import { ref, watch, onMounted } from 'vue';

const $q = useQuasar();
const showPurchaseForm = ref(false);
const showShippingForm = ref(false);
const showInventoryForm = ref(false);
const leftDrawerOpen = ref(false);
const theme = ref('light_mode');

const nickNameAndUsername = ref(
  localStorage.getItem('nickname') + ' （ ' + localStorage.getItem('username') + ' ）',
);

interface MenuItem {
  to: string;
  icon: string;
  title: string;
  caption: string;
}

const menuItems: MenuItem[] = [
  {
    to: '/',
    icon: 'home',
    title: '首頁',
    caption: '回到主畫面',
  },
  {
    to: '/inventory',
    icon: 'inventory',
    title: '庫存',
    caption: '查看存貨清單',
  },
  {
    to: '/settings',
    icon: 'settings',
    title: '設定',
    caption: '變更偏好設定',
  },
];

const linksList: EssentialLinkProps[] = [
  {
    title: 'Docs',
    caption: 'quasar.dev',
    icon: 'school',
    link: 'https://quasar.dev',
  },
];

// 初始化時從 localStorage 讀取
onMounted(() => {
  const savedTheme = localStorage.getItem('theme');
  if (savedTheme) {
    theme.value = savedTheme;
    $q.dark.set(savedTheme === 'dark_mode');
  }

  if (!localStorage.getItem('username')) {
    localStorage.setItem('username', '未登入');
    localStorage.setItem('nickname', '訪客');
  }
});

// 監聽 theme 變化並存入 localStorage
watch(theme, (newVal) => {
  localStorage.setItem('theme', newVal);
});

function toggleLeftDrawer() {
  leftDrawerOpen.value = !leftDrawerOpen.value;
}

const toggleTheme = () => {
  $q.dark.toggle();
  theme.value = $q.dark.isActive ? 'dark_mode' : 'light_mode';
};
</script>

<style lang="scss">
.q-item.active {
  background-color: #d0f0ff;
  color: #00796b;
  font-weight: bold;
  .caption {
    color: gray;
  }
}
</style>
