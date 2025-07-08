<template>
  <q-layout>
    <q-page-container>
      <q-page class="login-page">
        <!-- 背景裝飾元素 -->
        <div class="background-decorations">
          <div class="decoration decoration-1"></div>
          <div class="decoration decoration-2"></div>
          <div class="decoration decoration-3"></div>
          <div class="decoration decoration-4"></div>
        </div>

        <!-- 主要登入卡片 -->
        <div class="login-container">
          <q-card class="login-card glass-card">
            <!-- Logo 區域 -->
            <q-card-section class="logo-section">
              <div class="logo-container">
                <q-avatar size="80px" class="logo-avatar">
                  <img src="../assets/logo.jpg" alt="ERP Logo" />
                </q-avatar>
              </div>
            </q-card-section>

            <!-- 標題區域 -->
            <q-card-section class="title-section">
              <h1 class="app-title">凱亞 ERP 系統</h1>
              <p class="app-subtitle">進銷存管理</p>
            </q-card-section>

            <!-- 表單區域 -->
            <q-card-section class="form-section">
              <q-form @submit="handleLogin" class="login-form">
                <div class="input-group">
                  <q-input
                    v-model="loginForm.username"
                    label="使用者名稱"
                    filled
                    dense
                    :rules="usernameRules"
                    class="modern-input"
                    color="primary"
                  >
                    <template v-slot:prepend>
                      <q-icon name="person" color="primary" />
                    </template>
                  </q-input>
                </div>

                <div class="input-group">
                  <q-input
                    v-model="loginForm.password"
                    :type="showPassword ? 'text' : 'password'"
                    label="密碼"
                    filled
                    dense
                    :rules="passwordRules"
                    class="modern-input"
                    color="primary"
                  >
                    <template v-slot:prepend>
                      <q-icon name="lock" color="primary" />
                    </template>
                    <template v-slot:append>
                      <q-icon
                        :name="showPassword ? 'visibility_off' : 'visibility'"
                        class="cursor-pointer"
                        @click="togglePasswordVisibility"
                        color="primary"
                      />
                    </template>
                  </q-input>
                </div>

                <!-- 記住我選項 -->
                <div class="remember-section">
                  <q-checkbox
                    v-model="rememberMe"
                    label="記住我"
                    color="primary"
                    class="modern-checkbox"
                  />
                </div>
                <!-- 登入按鈕 -->
                <div class="button-section">
                  <q-btn
                    type="submit"
                    label="登入"
                    color="primary"
                    size="lg"
                    class="login-btn modern-sleek-btn"
                    :loading="isLoading"
                    :disable="!isFormValid"
                    no-caps
                    rounded
                    unelevated
                  >
                    <template v-slot:loading>
                      <q-spinner-dots color="white" size="18px" />
                    </template>

                    <!-- 現代幾何裝飾 -->
                    <div class="btn-accent"></div>
                  </q-btn>
                </div>

                <!-- 額外選項 -->
                <div class="extra-options">
                  <q-btn
                    flat
                    no-caps
                    label="忘記密碼？"
                    color="primary"
                    class="forgot-password-btn"
                    @click="handleForgotPassword"
                  />
                </div>
              </q-form>
            </q-card-section>
          </q-card>
        </div>

        <!-- 載入提示 -->
        <q-dialog v-model="showLoadingDialog" persistent>
          <q-card class="loading-card">
            <q-card-section class="row items-center">
              <q-spinner-cube color="primary" size="2em" />
              <span class="q-ml-sm" black>正在登入中...</span>
            </q-card-section>
          </q-card>
        </q-dialog>
      </q-page>
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useQuasar } from 'quasar';
import { api } from 'boot/axios';

// 型別定義
interface LoginForm {
  username: string;
  password: string;
}

interface LoginResponse {
  username: string;
  nickname: string;
  role: string;
  accessToken: string;
  refreshToken: string;
}

// 組合式函數
const router = useRouter();
const $q = useQuasar();

// 響應式狀態
const loginForm = ref<LoginForm>({
  username: '',
  password: '',
});

const isLoading = ref(false);
const showPassword = ref(false);
const rememberMe = ref(false);
const showLoadingDialog = ref(false);

// 表單驗證規則
const usernameRules = [
  (val: string) => (val && val.length > 0) || '請輸入使用者名稱',
  (val: string) => val.length >= 3 || '使用者名稱至少需要3個字符',
];

const passwordRules = [
  (val: string) => (val && val.length > 0) || '請輸入密碼',
  (val: string) => val.length >= 6 || '密碼至少需要6個字符',
];

// 計算屬性
const isFormValid = computed(() => {
  return loginForm.value.username.length >= 3 && loginForm.value.password.length >= 6;
});

// 方法
const togglePasswordVisibility = () => {
  showPassword.value = !showPassword.value;
};

const handleLogin = async () => {
  if (!isFormValid.value) return;

  try {
    isLoading.value = true;
    showLoadingDialog.value = true;

    const { data } = await api.post<LoginResponse>('/login', {
      username: loginForm.value.username,
      password: loginForm.value.password,
    });

    // 儲存登入資訊
    localStorage.setItem('username', data.username);
    localStorage.setItem('nickname', data.nickname);
    localStorage.setItem('role', data.role);
    localStorage.setItem('accessToken', data.accessToken);
    localStorage.setItem('refreshToken', data.refreshToken);

    // 如果選擇記住我，儲存使用者名稱
    localStorage.removeItem('rememberedUsername');
    if (rememberMe.value) {
      localStorage.setItem('rememberedUsername', data.username);
    }

    // 設定 API 預設標頭
    api.defaults.headers.common.Authorization = `Bearer ${data.accessToken}`;

    // 顯示成功訊息
    $q.notify({
      type: 'positive',
      message: `歡迎回來，${data.nickname}！`,
      position: 'top',
      timeout: 2000,
    });

    await router.push('/');
  } catch (error: unknown) {
    loginForm.value.password = '';
    localStorage.removeItem('rememberedUsername');
    rememberMe.value = false;
    console.error('登入失敗', error);
  } finally {
    isLoading.value = false;
    showLoadingDialog.value = false;
  }
};

const handleForgotPassword = () => {
  $q.dialog({
    title: '忘記密碼',
    message: '此功能尚未實裝，請聯繫系統管理員重設密碼',
    ok: {
      label: '確定',
      color: 'primary',
    },
  });
};

onMounted(() => {
  // 匯入測試帳號
  loginForm.value.username = 'admin';
  loginForm.value.password = '123456';
  // 檢查是否有記住的使用者名稱
  const rememberedUsername = localStorage.getItem('rememberedUsername');
  if (rememberedUsername) {
    loginForm.value.username = rememberedUsername;
    loginForm.value.password = '';
    rememberMe.value = true;
  }
});
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.background-decorations {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.decoration {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;

  &.decoration-1 {
    width: 80px;
    height: 80px;
    top: 20%;
    left: 10%;
    animation-delay: 0s;
  }

  &.decoration-2 {
    width: 120px;
    height: 120px;
    top: 60%;
    right: 15%;
    animation-delay: 2s;
  }

  &.decoration-3 {
    width: 60px;
    height: 60px;
    bottom: 30%;
    left: 20%;
    animation-delay: 4s;
  }

  &.decoration-4 {
    width: 100px;
    height: 100px;
    top: 40%;
    right: 30%;
    animation-delay: 1s;
  }
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

.login-container {
  position: relative;
  z-index: 2;
  width: 100%;
  max-width: 400px;
  padding: 20px;
}

.glass-card {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 48px rgba(0, 0, 0, 0.15);
  }
}

.logo-section {
  text-align: center;
  padding: 30px 20px 20px;
}

.logo-container {
  position: relative;
  display: inline-block;
}

.logo-avatar {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  border: 3px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;

  &:hover {
    transform: scale(1.05);
    box-shadow: 0 6px 30px rgba(0, 0, 0, 0.15);
  }
}

.title-section {
  text-align: center;
  padding: 0 20px 20px;
}

.app-title {
  font-size: 2rem;
  font-weight: 700;
  color: white;
  margin: 0 0 8px 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.app-subtitle {
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
  font-weight: 400;
}

.form-section {
  padding: 20px 30px 30px;
}

.input-group {
  margin-bottom: 20px;
}

.modern-input {
  :deep(.q-field__control) {
    background: rgba(255, 255, 255, 0.1);
    border-radius: 12px;
    border: 1px solid rgba(255, 255, 255, 0.2);
    transition: all 0.3s ease;

    &:hover {
      background: rgba(255, 255, 255, 0.15);
      border-color: rgba(255, 255, 255, 0.3);
    }
  }

  :deep(.q-field__native) {
    color: white;
  }

  :deep(.q-field__label) {
    color: rgba(255, 255, 255, 0.7);
  }

  :deep(.q-field--focused .q-field__control) {
    background: rgba(255, 255, 255, 0.2);
    border-color: #1976d2;
    box-shadow: 0 0 0 2px rgba(25, 118, 210, 0.3);
  }
}

.remember-section {
  margin: 20px 0;
  display: flex;
  align-items: center;
}

.modern-checkbox {
  :deep(.q-checkbox__label) {
    color: rgba(255, 255, 255, 0.9);
    font-size: 0.9rem;
  }
}

.button-section {
  margin: 30px 0 20px;
}

.login-btn {
  width: 100%;
  height: 50px;
  font-size: 1.1rem;
  font-weight: 600;
  background: linear-gradient(45deg, #1976d2, #42a5f5);
  box-shadow: 0 4px 15px rgba(25, 118, 210, 0.3);
  transition: all 0.3s ease;

  &:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 6px 25px rgba(25, 118, 210, 0.4);
  }

  &:active {
    transform: translateY(0);
  }
}

.extra-options {
  text-align: center;
  margin-top: 20px;
}

.forgot-password-btn {
  color: rgba(255, 255, 255, 0.8);
  font-size: 0.9rem;
  text-decoration: none;
  transition: all 0.3s ease;

  &:hover {
    color: white;
    text-decoration: underline;
  }
}

.loading-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  min-width: 200px;
}

// 響應式設計
@media (max-width: 480px) {
  .login-container {
    max-width: 90%;
    padding: 10px;
  }

  .form-section {
    padding: 20px;
  }

  .app-title {
    font-size: 1.5rem;
  }

  .login-btn {
    height: 45px;
    font-size: 1rem;
  }
}

// 深色模式支援
.body--dark {
  .glass-card {
    background: rgba(30, 30, 30, 0.8);
    border-color: rgba(255, 255, 255, 0.1);
  }

  .modern-input {
    :deep(.q-field__control) {
      background: rgba(255, 255, 255, 0.05);
    }
  }
}
// 登入按鈕強化
.button-section {
  position: relative;
  display: flex;
  justify-content: center;
  margin: 2rem 0;
}

.modern-sleek-btn {
  position: relative;
  min-width: 200px;
  height: 56px;
  background: linear-gradient(135deg, #1a1a1a 0%, #2d2d2d 100%);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow:
    0 4px 20px rgba(0, 0, 0, 0.15),
    0 1px 3px rgba(0, 0, 0, 0.2);
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  font-weight: 500;
  font-size: 16px;
  letter-spacing: 0.3px;
  color: white;
  backdrop-filter: blur(20px);
}

.modern-sleek-btn:hover {
  transform: translateY(-1px);
  background: linear-gradient(135deg, #2d2d2d 0%, #404040 100%);
  border-color: rgba(255, 255, 255, 0.2);
  box-shadow:
    0 8px 30px rgba(0, 0, 0, 0.2),
    0 2px 8px rgba(0, 0, 0, 0.15);
}

.modern-sleek-btn:active {
  transform: translateY(0);
  box-shadow:
    0 2px 12px rgba(0, 0, 0, 0.15),
    0 1px 4px rgba(0, 0, 0, 0.2);
}

.modern-sleek-btn:disabled {
  opacity: 0.5;
  transform: none;
  background: linear-gradient(135deg, #1a1a1a 0%, #2d2d2d 100%);
  border-color: rgba(255, 255, 255, 0.05);
}

/* 現代幾何裝飾 */
.btn-accent {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.btn-accent::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
  transition: left 0.5s ease;
}

.modern-sleek-btn:hover .btn-accent {
  opacity: 1;
}

.modern-sleek-btn:hover .btn-accent::before {
  left: 100%;
}

/* 載入狀態美化 */
.modern-sleek-btn .q-spinner-dots {
  opacity: 0.9;
}

/* 光滑表面效果 */
.modern-sleek-btn::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 50%;
  background: linear-gradient(to bottom, rgba(255, 255, 255, 0.05), transparent);
  pointer-events: none;
}

/* 響應式設計 */
@media (max-width: 768px) {
  .modern-sleek-btn {
    min-width: 180px;
    height: 52px;
    font-size: 15px;
  }
}

/* 深色主題變體 */
@media (prefers-color-scheme: dark) {
  .modern-sleek-btn {
    background: linear-gradient(135deg, #0a0a0a 0%, #1a1a1a 100%);
    border-color: rgba(255, 255, 255, 0.08);
  }

  .modern-sleek-btn:hover {
    background: linear-gradient(135deg, #1a1a1a 0%, #2a2a2a 100%);
    border-color: rgba(255, 255, 255, 0.15);
  }
}

/* 淺色主題變體 */
@media (prefers-color-scheme: light) {
  .modern-sleek-btn {
    background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
    border: 1px solid rgba(0, 0, 0, 0.08);
    color: #1a1a1a;
    box-shadow:
      0 4px 20px rgba(0, 0, 0, 0.08),
      0 1px 3px rgba(0, 0, 0, 0.12);
  }

  .modern-sleek-btn:hover {
    background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
    border-color: rgba(0, 0, 0, 0.12);
    box-shadow:
      0 8px 30px rgba(0, 0, 0, 0.12),
      0 2px 8px rgba(0, 0, 0, 0.08);
  }

  .modern-sleek-btn::after {
    background: linear-gradient(to bottom, rgba(255, 255, 255, 0.8), transparent);
  }

  .btn-accent::before {
    background: linear-gradient(90deg, transparent, rgba(0, 0, 0, 0.05), transparent);
  }
}
</style>
