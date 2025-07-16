import { defineBoot } from '#q-app/wrappers';
import axios, { type AxiosInstance } from 'axios';
import { Notify } from 'quasar';
import type { Router } from 'vue-router';

declare module 'vue' {
  interface ComponentCustomProperties {
    $axios: AxiosInstance;
    $api: AxiosInstance;
  }
}

interface RefreshTokenResponse {
  accessToken: string;
  refreshToken: string;
}

// Be careful when using SSR for cross-request state pollution
// due to creating a Singleton instance here;
// If any client changes this (global) instance, it might be a
// good idea to move this instance creation inside of the
// "export default () => {}" function below (which runs individually
// for each client)
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 5000,
});

// 👉 Vue Router 實例，在 boot 函數中設定
let vueRouter: Router;

// 👉 是否正在刷新中，避免多次呼叫 /renewRefreshToken
let isRefreshing = false;
let refreshSubscribers: ((token: string) => void)[] = [];

// 👉 呼叫所有等待中的請求，注入新 token
function onRefreshed(token: string) {
  refreshSubscribers.forEach((cb) => cb(token));
  refreshSubscribers = [];
}

// 👉 加入等待刷新完成的請求
function subscribeTokenRefresh(cb: (token: string) => void) {
  refreshSubscribers.push(cb);
}

// ✅ 設定 Request 預設加上 token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(new Error(error)),
);

// ✅ 回應攔截處理成功回應
api.interceptors.response.use(
  (response) => {
    const res = response.data;
    const { code, message } = res;

    if (typeof code === 'number') {
      if (code >= 200 && code < 300) {
        return res;
      } else {
        Notify.create({
          type: 'negative',
          message: `[${code}] ${message || '操作失敗'}`,
        });
      }
    }

    return res;
  },

  async (error) => {
    const originalRequest = error.config;
    // 👉 400，失敗請求
    if (error.response?.status === 400) {
      Notify.create({
        type: 'negative',
        message: error.response.data.message,
      });
      return Promise.reject(new Error(error.response.data.message));
    }

    // 👉 401，且尚未 retry，嘗試刷新 token
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      if (isRefreshing) {
        // 如果正在刷新，等待完成後再發送原請求
        return new Promise((resolve) => {
          subscribeTokenRefresh((token: string) => {
            originalRequest.headers['Authorization'] = `Bearer ${token}`;
            resolve(api(originalRequest));
          });
        });
      }

      isRefreshing = true;

      try {
        const { data } = await api.post<RefreshTokenResponse>('/renewRefreshToken', {
          refreshToken: localStorage.getItem('refreshToken'),
        });

        const newAccessToken = data.accessToken;
        localStorage.setItem('accessToken', newAccessToken);
        localStorage.setItem('refreshToken', data.refreshToken);
        api.defaults.headers.common['Authorization'] = `Bearer ${newAccessToken}`;
        onRefreshed(newAccessToken);

        // 重新送出原請求
        return api(originalRequest);
      } catch (refreshError) {
        Notify.create({
          type: 'negative',
          message: '登入過期，請重新登入',
        });
        // 清除 token 並導向登入或首頁
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');

        // 檢查 vueRouter 是否已初始化
        if (vueRouter) {
          await vueRouter.push('/login');
        }

        return Promise.reject(new Error(refreshError as string));
      } finally {
        isRefreshing = false;
      }
    }

    // 其他錯誤提示
    Notify.create({
      type: 'negative',
      message: error.message || '網路錯誤',
    });
    return Promise.reject(new Error(error.message || '網路錯誤'));
  },
);

export default defineBoot(({ app, router }) => {
  // 設定 vueRouter 實例
  vueRouter = router;

  // for use inside Vue files (Options API) through this.$axios and this.$api
  app.config.globalProperties.$axios = axios;
  // ^ ^ ^ this will allow you to use this.$axios (for Vue Options API form)
  //       so you won't necessarily have to import axios in each vue file

  app.config.globalProperties.$api = api;
  // ^ ^ ^ this will allow you to use this.$api (for Vue Options API form)
  //       so you can easily perform requests against your app's API
});

export { api };
