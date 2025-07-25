<template>
  <q-layout view="lHh Lpr lFf">
    <!-- 現代化的漸層背景 -->
    <div class="gradient-bg"></div>

    <!-- 頂部導航欄 -->
    <q-header elevated class="modern-header">
      <q-toolbar class="toolbar-container">
        <q-btn
          flat
          dense
          round
          icon="menu"
          aria-label="Menu"
          @click="toggleLeftDrawer"
          class="menu-btn"
        />

        <q-toolbar-title class="modern-title">
          <div class="title-container">
            <q-icon name="business" size="md" class="title-icon" />
            <span class="title-text">凱亞 ERP</span>
            <q-chip outline size="sm" class="beta-chip">BETA</q-chip>
          </div>
        </q-toolbar-title>

        <!-- 操作按鈕組 -->
        <div class="action-buttons">
          <q-btn
            flat
            round
            icon="add_shopping_cart"
            size="md"
            @click="showPurchaseForm = true"
            class="action-btn purchase-btn"
          >
            <q-tooltip
              class="modern-tooltip purchase-tooltip"
              :offset="[10, 10]"
              transition-show="scale"
              transition-hide="scale"
            >
              新增進貨單
            </q-tooltip>
          </q-btn>

          <q-btn
            flat
            round
            icon="local_shipping"
            size="md"
            @click="showShippingFormMethod"
            class="action-btn shipping-btn"
          >
            <q-tooltip
              class="modern-tooltip shipping-tooltip"
              :offset="[10, 10]"
              transition-show="scale"
              transition-hide="scale"
            >
              新增出貨單
            </q-tooltip>
          </q-btn>

          <q-btn
            flat
            round
            icon="checklist"
            size="md"
            @click="showInventoryFormMethod"
            class="action-btn inventory-btn"
          >
            <q-tooltip
              class="modern-tooltip inventory-tooltip"
              :offset="[10, 10]"
              transition-show="scale"
              transition-hide="scale"
            >
              庫存盤點
            </q-tooltip>
          </q-btn>

          <q-separator vertical inset class="btn-separator" />

          <q-btn flat @click="toggleTheme">
            <q-icon :name="theme === 'dark_mode' ? 'light_mode' : 'dark_mode'" />
            <q-tooltip>
              {{ theme === 'dark_mode' ? '深色模式' : '淺色模式' }}
            </q-tooltip>
          </q-btn>
        </div>
      </q-toolbar>
    </q-header>

    <!-- 側邊欄 -->
    <q-drawer v-model="leftDrawerOpen" show-if-above bordered class="modern-drawer" :width="280">
      <!-- 用戶資訊區域 -->
      <div class="user-section">
        <div class="user-avatar">
          <q-avatar size="48px" class="avatar-gradient">
            <q-icon name="person" size="24px" />
          </q-avatar>
        </div>
        <div class="user-info">
          <div class="user-nickname">{{ nickname }}</div>
          <div class="user-username">{{ username }}</div>
        </div>
      </div>

      <q-separator class="section-separator" />

      <!-- 導航菜單 -->
      <q-list class="navigation-menu">
        <q-item
          v-for="(item, index) in menuItems"
          :key="item.to"
          :to="item.to"
          clickable
          exact
          v-ripple="{ color: 'primary' }"
          active-class="modern-active"
          class="menu-item"
          :class="`menu-item-${index}`"
        >
          <q-item-section avatar>
            <div class="menu-icon-container">
              <q-icon :name="item.icon" size="sm" />
            </div>
          </q-item-section>

          <q-item-section>
            <q-item-label class="menu-title">{{ item.title }}</q-item-label>
            <q-item-label caption class="menu-caption">{{ item.caption }}</q-item-label>
          </q-item-section>

          <q-item-section side v-if="item.badge">
            <q-badge :color="item.badgeColor" rounded>{{ item.badge }}</q-badge>
          </q-item-section>
        </q-item>
      </q-list>

      <q-separator class="section-separator" />

      <!-- 快速操作區域 -->
      <div class="quick-actions">
        <q-item-label header class="menu-header">
          <q-icon name="flash_on" size="sm" class="header-icon" />
          快速操作
        </q-item-label>

        <div class="quick-action-grid">
          <q-btn flat class="quick-action-btn" @click="showPurchaseFormMethod">
            <div class="quick-action-content">
              <q-icon name="add_shopping_cart" size="md" />
              <span>進貨</span>
            </div>
          </q-btn>

          <q-btn flat class="quick-action-btn" @click="showShippingFormMethod">
            <div class="quick-action-content">
              <q-icon name="local_shipping" size="md" />
              <span>出貨</span>
            </div>
          </q-btn>
        </div>
      </div>

      <!-- 底部資訊 -->
      <div class="drawer-footer">
        <EssentialLink
          v-for="link in linksList"
          :key="link.title"
          v-bind="link"
          class="footer-link"
        />
        <div class="version-info">
          <q-chip outline size="xs" color="grey">v2.0.1</q-chip>
        </div>
      </div>
    </q-drawer>

    <!-- 表單組件 -->
    <PurchaseForm v-model="showPurchaseForm" />
    <ShippingForm v-model="showShippingForm" />
    <InventoryForm v-model="showInventoryForm" />

    <!-- 主要內容區域 -->
    <q-page-container class="modern-page-container">
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
import EssentialLink, { type EssentialLinkProps } from 'components/EssentialLink.vue';
import PurchaseForm from 'src/components/PurchaseForm.vue';
import ShippingForm from 'src/components/ShippingForm.vue';
import InventoryForm from 'src/components/InventoryForm.vue';
import { useQuasar } from 'quasar';
import { ref, watch, onMounted, onUnmounted, computed } from 'vue';
import { useMainLayoutStore } from 'src/stores/MainLayout-store';

const layoutStore = useMainLayoutStore();

const $q = useQuasar();
const showPurchaseForm = ref(false);
const showShippingForm = ref(false);
const showInventoryForm = ref(false);
const leftDrawerOpen = ref(false);
const theme = ref('light_mode');

// 分離 nickname 和 username
const nickname = ref('訪客');
const username = ref('未登入');

// 開啟表單方法
function showPurchaseFormMethod() {
  showPurchaseForm.value = true;
}
function showShippingFormMethod() {
  showShippingForm.value = true;
}
function showInventoryFormMethod() {
  showInventoryForm.value = true;
}

interface MenuItem {
  to: string;
  icon: string;
  title: string;
  caption: string;
  badge?: number;
  badgeColor?: string;
}

const menuItems = computed<MenuItem[]>(() => [
  {
    to: '/',
    icon: 'dashboard',
    title: '控制台',
    caption: '系統總覽與統計',
    badgeColor: 'primary',
  },
  {
    to: '/inventory',
    icon: 'inventory',
    title: '庫存管理',
    caption: '商品庫存與進出貨',
    badge: layoutStore.inventoryBadge,
    badgeColor: 'secondary',
  },
  {
    to: '/orders',
    icon: 'receipt_long',
    title: '訂單管理',
    caption: '訂單處理與追蹤',
    badge: layoutStore.ordersBadge,
    badgeColor: 'warning',
  },
  {
    to: '/analytics',
    icon: 'analytics',
    title: '數據分析',
    caption: '銷售報表與分析',
  },
  {
    to: '/settings',
    icon: 'settings',
    title: '系統設定',
    caption: '偏好設定與配置',
  },
]);

const linksList: EssentialLinkProps[] = [
  {
    title: '說明文件',
    caption: '使用手冊',
    icon: 'help_outline',
    link: 'https://google.com',
  },
  {
    title: '技術支援',
    caption: '聯繫客服',
    icon: 'support_agent',
    link: 'https://google.com',
  },
];

const initLocalStorage = () => {
  const storedUsername = localStorage.getItem('username');
  const storedNickname = localStorage.getItem('nickname');

  if (storedUsername) {
    username.value = storedUsername;
  } else {
    localStorage.setItem('username', '未登入');
    username.value = '未登入';
  }

  if (storedNickname) {
    nickname.value = storedNickname;
  } else {
    localStorage.setItem('nickname', '訪客');
    nickname.value = '訪客';
  }
};

const onStorageChange = (event: StorageEvent) => {
  if (event.key === 'username' && event.newValue === null) {
    localStorage.setItem('username', '未登入');
    username.value = '未登入';
  }

  if (event.key === 'nickname' && event.newValue === null) {
    localStorage.setItem('nickname', '訪客');
    nickname.value = '訪客';
  }
};

onUnmounted(() => {
  window.removeEventListener('storage', onStorageChange);
  window.removeEventListener('logout', initLocalStorage);
});

// 初始化
onMounted(() => {
  // 讀取主題
  const savedTheme = localStorage.getItem('theme');

  if (savedTheme) {
    theme.value = savedTheme;
    $q.dark.set(savedTheme === 'dark_mode');
  } else {
    localStorage.setItem('theme', 'light_mode');
    $q.dark.set(false);
  }

  initLocalStorage(); // 初始設定
  // 新增監聽 logout 事件
  window.addEventListener('logout', initLocalStorage);
});

// 監聽主題變化
watch(theme, (newVal) => {
  localStorage.setItem('theme', newVal);
});

function toggleLeftDrawer() {
  leftDrawerOpen.value = !leftDrawerOpen.value;
}

const toggleTheme = () => {
  $q.dark.toggle();

  const isDark = $q.dark.isActive;
  theme.value = isDark ? 'dark_mode' : 'light_mode';
  localStorage.setItem('theme', theme.value);
};
</script>

<style lang="scss" scoped>
// 全局漸層背景
.gradient-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  opacity: 0.05;
  z-index: -1;
}

// 現代化頂部導航欄
.modern-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);

  .toolbar-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 16px;
  }
}

.modern-title {
  .title-container {
    display: flex;
    align-items: center;
    gap: 12px;

    .title-icon {
      color: rgba(255, 255, 255, 0.9);
    }

    .title-text {
      font-size: 1.25rem;
      font-weight: 600;
      color: white;
      letter-spacing: 0.5px;
    }

    .beta-chip {
      border-color: rgba(255, 255, 255, 0.3);
      color: rgba(255, 255, 255, 0.8);
      font-size: 0.7rem;
    }
  }
}

// 操作按鈕組
.action-buttons {
  display: flex;
  align-items: center;
  gap: 8px;

  .action-btn {
    position: relative;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }

    &.purchase-btn:hover {
      background-color: rgba(63, 81, 181, 0.1);
      color: #ffc107;
    }

    &.shipping-btn:hover {
      background-color: rgba(156, 39, 176, 0.1);
      color: #ffc107;
    }

    &.inventory-btn:hover {
      background-color: rgba(255, 193, 7, 0.1);
      color: #ffc107;
    }
  }

  .btn-separator {
    height: 24px;
    margin: 0 8px;
    opacity: 0.3;
  }

  .theme-btn {
    transition: all 0.3s ease;

    &:hover {
      transform: rotate(180deg);
    }
  }
}

// 現代化工具提示
.modern-tooltip {
  background: rgba(0, 0, 0, 0.8) !important;
  color: white !important;
  border-radius: 8px !important;
  padding: 8px 12px !important;
  font-size: 0.85rem !important;
  backdrop-filter: blur(10px);

  &.purchase-tooltip {
    background: linear-gradient(135deg, #3f51b5, #303f9f) !important;
  }

  &.shipping-tooltip {
    background: linear-gradient(135deg, #9c27b0, #7b1fa2) !important;
  }

  &.inventory-tooltip {
    background: linear-gradient(135deg, #ffc107, #f57c00) !important;
  }
}

// 現代化側邊欄
.modern-drawer {
  background: linear-gradient(180deg, #fafafa 0%, #f5f5f5 100%);
  border-right: 1px solid rgba(0, 0, 0, 0.05);
}

.user-section {
  padding: 24px 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.8), rgba(255, 255, 255, 0.4));
  border-radius: 0 0 16px 16px;

  .user-avatar {
    .avatar-gradient {
      background: linear-gradient(135deg, #667eea, #764ba2);
      color: white;
    }
  }

  .user-info {
    flex: 1;

    .user-nickname {
      font-size: 1.1rem;
      font-weight: 600;
      color: #2c3e50;
      margin-bottom: 4px;
    }

    .user-username {
      font-size: 0.9rem;
      color: #7f8c8d;
      margin-bottom: 8px;
    }

    .status-chip {
      font-size: 0.75rem;
    }
  }
}

.section-separator {
  margin: 16px 0;
  opacity: 0.1;
}

// 導航菜單
.navigation-menu {
  padding: 0 12px;

  .menu-header {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 0.85rem;
    font-weight: 600;
    color: #5a6c7d;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    margin: 16px 0 12px 0;

    .header-icon {
      color: #667eea;
    }
  }

  .menu-item {
    border-radius: 12px;
    margin: 4px 0;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    overflow: hidden;

    &:hover {
      background-color: rgba(102, 126, 234, 0.08);
      transform: translateX(4px);
    }

    .menu-icon-container {
      width: 36px;
      height: 36px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
      transition: all 0.3s ease;
    }

    .menu-title {
      font-weight: 500;
      font-size: 0.95rem;
      color: #2c3e50;
    }

    .menu-caption {
      font-size: 0.8rem;
      color: #7f8c8d;
      margin-top: 2px;
    }

    &.modern-active {
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
      border-left: 3px solid #667eea;

      .menu-icon-container {
        background: linear-gradient(135deg, #667eea, #764ba2);
        color: white;
        box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
      }

      .menu-title {
        color: #667eea;
        font-weight: 600;
      }
    }
  }
}

// 快速操作區域
.quick-actions {
  padding: 16px 12px;

  .quick-action-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 12px;
    margin-top: 12px;

    .quick-action-btn {
      padding: 16px 8px;
      border-radius: 12px;
      background: linear-gradient(135deg, rgba(255, 255, 255, 0.8), rgba(255, 255, 255, 0.4));
      border: 1px solid rgba(102, 126, 234, 0.1);
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 24px rgba(102, 126, 234, 0.15);
        background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
      }

      .quick-action-content {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 8px;

        span {
          font-size: 0.8rem;
          font-weight: 500;
          color: #5a6c7d;
        }
      }
    }
  }
}

// 底部資訊
.drawer-footer {
  margin-top: auto;
  padding: 16px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);

  .footer-link {
    margin: 4px 0;
    opacity: 0.7;
    transition: opacity 0.3s ease;

    &:hover {
      opacity: 1;
    }
  }

  .version-info {
    margin-top: 12px;
    text-align: center;
  }
}

// 主要內容區域
.modern-page-container {
  background: linear-gradient(180deg, #fafafa 0%, #ffffff 100%);
  min-height: calc(100vh - 50px);
}

// 深色模式適配
.body--dark {
  .gradient-bg {
    background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
    opacity: 0.1;
  }

  .modern-drawer {
    background: linear-gradient(180deg, #2c3e50 0%, #34495e 100%);
    border-right: 1px solid rgba(255, 255, 255, 0.05);
  }

  .user-section {
    background: linear-gradient(135deg, rgba(52, 73, 94, 0.8), rgba(44, 62, 80, 0.4));

    .user-nickname {
      color: #ecf0f1;
    }

    .user-username {
      color: #95a5a6;
    }
  }

  .menu-item {
    .menu-title {
      color: #ecf0f1;
    }

    .menu-caption {
      color: #95a5a6;
    }

    &:hover {
      background-color: rgba(102, 126, 234, 0.1);
    }

    &.modern-active {
      .menu-title {
        color: #667eea;
      }
    }
  }

  .quick-action-btn {
    background: linear-gradient(135deg, rgba(52, 73, 94, 0.8), rgba(44, 62, 80, 0.4));
    border: 1px solid rgba(255, 255, 255, 0.05);

    .quick-action-content span {
      color: #95a5a6;
    }
  }

  .drawer-footer {
    border-top: 1px solid rgba(255, 255, 255, 0.05);
  }

  .modern-page-container {
    background: linear-gradient(180deg, #29435c 0%, #202831 100%);
  }
}

// 響應式設計
@media (max-width: 1024px) {
  .action-buttons {
    gap: 4px;

    .action-btn {
      padding: 8px;
    }
  }
}

@media (max-width: 768px) {
  .modern-title .title-text {
    font-size: 1.1rem;
  }

  .user-section {
    padding: 16px;
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }

  .quick-action-grid {
    grid-template-columns: 1fr;
  }
}

// 動畫效果
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.menu-item {
  animation: fadeInUp 0.5s ease-out;
  animation-fill-mode: both;

  &.menu-item-0 {
    animation-delay: 0.1s;
  }
  &.menu-item-1 {
    animation-delay: 0.2s;
  }
  &.menu-item-2 {
    animation-delay: 0.3s;
  }
  &.menu-item-3 {
    animation-delay: 0.4s;
  }
  &.menu-item-4 {
    animation-delay: 0.5s;
  }
}
// 現代化頂部導航欄
.modern-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);

  .toolbar-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 16px;

    // 確保 toolbar 有足夠的 flex 空間分配
    display: flex;
    align-items: center;
    min-height: 50px;
  }
}

.modern-title {
  // 給標題區域適當的 flex 屬性
  flex: 1;
  min-width: 0; // 允許縮小

  .title-container {
    display: flex;
    align-items: center;
    gap: 12px;

    // 防止標題被完全擠壓
    min-width: 120px;

    .title-icon {
      color: rgba(255, 255, 255, 0.9);
      flex-shrink: 0; // icon 不縮小
    }

    .title-text {
      font-size: 1.25rem;
      font-weight: 600;
      color: white;
      letter-spacing: 0.5px;
      white-space: nowrap; // 防止換行
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .beta-chip {
      border-color: rgba(255, 255, 255, 0.3);
      color: rgba(255, 255, 255, 0.8);
      font-size: 0.7rem;
      flex-shrink: 0; // chip 不縮小
    }
  }
}

// 操作按鈕組
.action-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0; // 按鈕區域不縮小

  .action-btn {
    position: relative;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }

    &.purchase-btn:hover {
      background-color: rgba(63, 81, 181, 0.1);
      color: #ffc107;
    }

    &.shipping-btn:hover {
      background-color: rgba(156, 39, 176, 0.1);
      color: #ffc107;
    }

    &.inventory-btn:hover {
      background-color: rgba(255, 193, 7, 0.1);
      color: #ffc107;
    }
  }

  .btn-separator {
    height: 24px;
    margin: 0 8px;
    opacity: 0.3;
  }

  .theme-btn {
    transition: all 0.3s ease;

    &:hover {
      transform: rotate(180deg);
    }
  }
}

// Menu 按鈕樣式
.menu-btn {
  margin-right: 8px;
  flex-shrink: 0; // menu 按鈕不縮小
}

// 響應式設計優化
@media (max-width: 1024px) {
  .modern-header .toolbar-container {
    padding: 0 12px;
  }

  .action-buttons {
    gap: 4px;

    .action-btn {
      padding: 8px;
    }

    .btn-separator {
      margin: 0 4px;
    }
  }
}

@media (max-width: 768px) {
  .modern-header .toolbar-container {
    padding: 0 8px;
  }

  .modern-title {
    .title-container {
      gap: 8px;
      min-width: 100px; // 窄螢幕下縮小最小寬度

      .title-text {
        font-size: 1.1rem;
      }

      .beta-chip {
        font-size: 0.65rem;
        padding: 2px 6px;
      }
    }
  }

  .action-buttons {
    gap: 2px;

    .action-btn {
      padding: 6px;
      min-width: 40px;
    }

    .btn-separator {
      margin: 0 2px;
    }
  }
}

@media (max-width: 600px) {
  .modern-title {
    .title-container {
      gap: 6px;
      min-width: 80px;

      .title-text {
        font-size: 1rem;
      }
    }
  }

  .action-buttons {
    // 在極窄螢幕下，隱藏部分按鈕或使用下拉選單
    .btn-separator {
      display: none;
    }

    // 可以考慮隱藏 tooltip 文字較長的按鈕
    .inventory-btn {
      display: none;
    }
  }
}

@media (max-width: 480px) {
  .modern-header .toolbar-container {
    padding: 0 4px;
    min-height: 48px;
  }

  .modern-title {
    .title-container {
      min-width: 70px;

      .title-text {
        font-size: 0.9rem;
      }

      // 極窄螢幕下隱藏 BETA 標籤
      .beta-chip {
        display: none;
      }
    }
  }

  .action-buttons {
    // 只保留最重要的按鈕
    .shipping-btn {
      display: none;
    }
  }
}

// 針對特別窄的螢幕（如小手機橫屏）
@media (max-width: 360px) {
  .modern-title {
    .title-container {
      min-width: 60px;

      .title-text {
        font-size: 0.85rem;
      }
    }
  }
}
</style>
