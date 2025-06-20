<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated>
      <q-toolbar>
        <q-btn flat dense round icon="menu" aria-label="Menu" @click="toggleLeftDrawer" />

        <q-toolbar-title> Quasar App </q-toolbar-title>

        <q-btn flat round dense icon="inventory_2" size="lg" @click="toggleTheme" />
        <q-btn flat round dense icon="local_shipping" size="lg" @click="toggleTheme" />
        <q-btn flat round dense icon="checklist" size="lg" @click="toggleTheme" />
        <q-btn flat round dense :icon="theme" size="mg" @click="toggleTheme" />
      </q-toolbar>
    </q-header>

    <q-drawer v-model="leftDrawerOpen" show-if-above bordered>
      <q-list>
        <q-item-label header> Essential Links </q-item-label>

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
import { useQuasar } from 'quasar';

import { ref } from 'vue';

const $q = useQuasar();

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

const leftDrawerOpen = ref(false);

function toggleLeftDrawer() {
  leftDrawerOpen.value = !leftDrawerOpen.value;
}

const theme = ref('light_mode');

const toggleTheme = () => {
  $q.dark.toggle();
  theme.value = $q.dark.isActive ? 'light_mode' : 'dark_mode';
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
