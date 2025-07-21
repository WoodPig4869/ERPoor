<template>
  <div class="settings-page">
    <!-- 頁面標題 -->
    <div class="page-header">
      <h1 class="page-title">
        <q-icon name="settings" class="title-icon" />
        系統設定
      </h1>
      <p class="page-subtitle">管理您的帳戶設定與系統偏好</p>
    </div>

    <!-- 施工中提示 -->
    <div class="construction-notice">
      <q-icon name="construction" class="construction-icon" />
      <div class="construction-text">
        <h3>頁面施工中</h3>
        <p>我們正在努力為您打造更好的設定體驗，敬請期待！</p>
      </div>
    </div>

    <!-- 設定區塊 -->
    <div class="settings-container">
      <!-- 帳戶設定卡片 -->
      <div class="settings-card">
        <div class="card-header">
          <q-icon name="account_circle" class="card-icon" />
          <h3>帳戶設定</h3>
        </div>
        <div class="card-content">
          <p class="coming-soon">個人資料編輯功能即將推出</p>
          <div class="placeholder-items">
            <div class="placeholder-item">
              <q-skeleton type="text" width="120px" />
              <q-skeleton type="rect" height="40px" />
            </div>
            <div class="placeholder-item">
              <q-skeleton type="text" width="120px" />
              <q-skeleton type="rect" height="40px" />
            </div>
          </div>
        </div>
      </div>

      <!-- 通知設定卡片 -->
      <div class="settings-card">
        <div class="card-header">
          <q-icon name="notifications" class="card-icon" />
          <h3>通知設定</h3>
        </div>
        <div class="card-content">
          <p class="coming-soon">推送通知設定即將推出</p>
          <div class="placeholder-items">
            <div class="placeholder-item">
              <q-skeleton type="text" width="120px" />
              <q-skeleton type="rect" height="40px" />
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 登出按鈕區域 -->
    <div class="logout-section">
      <div class="logout-card">
        <div class="logout-content">
          <q-icon name="logout" class="logout-icon" />
          <div class="logout-text">
            <h3>安全登出</h3>
            <p>將清除您的憑證訊息</p>
          </div>
        </div>
        <q-btn
          color="negative"
          label="登出系統"
          icon="logout"
          size="lg"
          class="logout-btn"
          @click="handleLogout"
          :loading="isLoggingOut"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { api } from 'boot/axios';
import { useRouter } from 'vue-router';
import { Notify } from 'quasar';

const router = useRouter();
const isLoggingOut = ref(false);

// 定義 refresh token 回傳格式
interface RefreshTokenResponse {
  refreshToken: string;
}

const handleLogout = async () => {
  isLoggingOut.value = true;

  try {
    const token = localStorage.getItem('refreshToken');

    if (token) {
      await api.post<RefreshTokenResponse>('/revokeRefreshToken', {
        refreshToken: token,
      });
    }
  } catch (error: unknown) {
    console.error('登出失敗', error);
  } finally {
    // 清除登入資訊
    localStorage.removeItem('username');
    localStorage.removeItem('nickname');
    localStorage.removeItem('role');
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');

    // 設定 API 預設標頭
    api.defaults.headers.common.Authorization = '';

    isLoggingOut.value = false;
  }

  try {
    // 重新導向到首頁
    window.dispatchEvent(new Event('logout'));
    await router.push('/login');
    Notify.create({
      message: '登出成功',
      color: 'positive',
    });
  } catch (error: unknown) {
    console.error('重新導向失敗', error);
  }
};
</script>

<style scoped>
.settings-page {
  min-height: 100vh;
  padding: 2rem;
}

.page-header {
  text-align: center;
  margin-bottom: 2rem;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  margin: 0 0 0.5rem 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
}

.title-icon {
  font-size: 2.5rem;
}

.page-subtitle {
  font-size: 1.1rem;
  margin: 0;
  font-weight: 300;
}

.construction-notice {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 1rem;
  padding: 2rem;
  margin-bottom: 2rem;
  display: flex;
  align-items: center;
  gap: 1.5rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.construction-icon {
  font-size: 3rem;
  color: #f39c12;
}

.construction-text h3 {
  margin: 0 0 0.5rem 0;
  font-size: 1.5rem;
  color: #2c3e50;
}

.construction-text p {
  margin: 0;
  color: #7f8c8d;
  font-size: 1rem;
}

.settings-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.settings-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 1rem;
  padding: 1.5rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition:
    transform 0.3s ease,
    box-shadow 0.3s ease;
}

.settings-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1rem;
}

.card-icon {
  font-size: 1.5rem;
  color: #667eea;
}

.card-header h3 {
  margin: 0;
  font-size: 1.25rem;
  color: #2c3e50;
  font-weight: 600;
}

.card-content {
  color: #7f8c8d;
}

.coming-soon {
  margin: 0 0 1rem 0;
  font-style: italic;
  color: #95a5a6;
}

.placeholder-items {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.placeholder-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.logout-section {
  display: flex;
  justify-content: center;
  margin-top: 2rem;
}

.logout-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 1rem;
  padding: 2rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  gap: 2rem;
  max-width: 600px;
  width: 100%;
}

.logout-content {
  display: flex;
  align-items: center;
  gap: 1rem;
  flex: 1;
}

.logout-icon {
  font-size: 2rem;
  color: #e74c3c;
}

.logout-text h3 {
  margin: 0 0 0.25rem 0;
  color: #2c3e50;
  font-size: 1.25rem;
}

.logout-text p {
  margin: 0;
  color: #7f8c8d;
  font-size: 0.9rem;
}

.logout-btn {
  min-width: 140px;
  border-radius: 0.5rem;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(231, 76, 60, 0.3);
  transition: all 0.3s ease;
}

.logout-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(231, 76, 60, 0.4);
}

/* 響應式設計 */
@media (max-width: 768px) {
  .settings-page {
    padding: 1rem;
  }

  .page-title {
    font-size: 2rem;
  }

  .construction-notice {
    flex-direction: column;
    text-align: center;
    padding: 1.5rem;
  }

  .logout-card {
    flex-direction: column;
    text-align: center;
    gap: 1.5rem;
  }

  .logout-content {
    flex-direction: column;
    text-align: center;
  }

  .settings-container {
    grid-template-columns: 1fr;
  }
}

/* 動畫效果 */
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

.settings-card,
.construction-notice,
.logout-card {
  animation: fadeInUp 0.6s ease-out;
}

.settings-card:nth-child(1) {
  animation-delay: 0.1s;
}
.settings-card:nth-child(2) {
  animation-delay: 0.2s;
}
</style>
