import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      { path: '', component: () => import('pages/IndexPage.vue') }, // 無需登入
      {
        path: 'inventory',
        component: () => import('pages/InventoryPage.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: 'orders',
        component: () => import('pages/OrdersPage.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: 'inventoryRecords',
        component: () => import('pages/InventoryRecords.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: 'settings',
        component: () => import('pages/SettingsPage.vue'),
        meta: { requiresAuth: true },
      },
    ],
  },
  {
    path: '/login',
    component: () => import('pages/LoginPage.vue'),
  },
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue'),
  },
];

export default routes;
