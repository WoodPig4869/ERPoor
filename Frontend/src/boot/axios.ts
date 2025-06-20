import { defineBoot } from '#q-app/wrappers';
import axios, { type AxiosInstance } from 'axios';
import { Notify } from 'quasar';

declare module 'vue' {
  interface ComponentCustomProperties {
    $axios: AxiosInstance;
    $api: AxiosInstance;
  }
}

// Be careful when using SSR for cross-request state pollution
// due to creating a Singleton instance here;
// If any client changes this (global) instance, it might be a
// good idea to move this instance creation inside of the
// "export default () => {}" function below (which runs individually
// for each client)
const api = axios.create({ baseURL: 'http://localhost:8080' });

// ➤ 封裝 response interceptor 處理 RPC-style 回應格式
api.interceptors.response.use(
  (response) => {
    const res = response.data;
    const { code, message } = res;

    if (typeof code === 'number') {
      if (code >= 200 && code < 300) {
        Notify.create({
          type: 'positive',
          message: message || '操作成功',
        });
      } else {
        Notify.create({
          type: 'negative',
          message: `[${code}] ${message || '操作失敗'}`,
        });
      }
    }

    // ➤ 回傳純資料，讓呼叫端拿到 res.data
    return res.data;
  },
  (error) => {
    // ➤ 處理網路錯誤、500等非RPC格式錯誤
    Notify.create({
      type: 'negative',
      message: error.message || '網路錯誤',
    });
    return Promise.reject(new Error(error.message || '網路錯誤'));
  },
);

export default defineBoot(({ app }) => {
  // for use inside Vue files (Options API) through this.$axios and this.$api

  app.config.globalProperties.$axios = axios;
  // ^ ^ ^ this will allow you to use this.$axios (for Vue Options API form)
  //       so you won't necessarily have to import axios in each vue file

  app.config.globalProperties.$api = api;
  // ^ ^ ^ this will allow you to use this.$api (for Vue Options API form)
  //       so you can easily perform requests against your app's API
});

export { api };
