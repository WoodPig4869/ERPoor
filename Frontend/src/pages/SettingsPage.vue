<template>
  <div>Setting</div>
  <!-- logout button -->
  <q-btn color="primary" label="登出" @click="handleLogout" />
</template>

<script setup lang="ts">
import { api } from 'boot/axios';
import { useRouter } from 'vue-router';
import { Notify } from 'quasar';

const router = useRouter();

// 定義 refresh token 回傳格式
interface RefreshTokenResponse {
  refreshToken: string;
}

const handleLogout = async () => {
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
