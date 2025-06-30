<template>
  <q-layout>
    <q-page-container>
      <q-page class="flex bg-image flex-center">
        <q-card v-bind:style="$q.screen.lt.sm ? { width: '80%' } : { width: '30%' }">
          <q-card-section>
            <q-avatar size="103px" class="absolute-center shadow-10">
              <img src="../assets/logo.jpg" />
            </q-avatar>
          </q-card-section>
          <q-card-section>
            <div class="text-center q-pt-lg">
              <div class="col text-h6 ellipsis">凱亞ERP</div>
            </div>
          </q-card-section>
          <q-card-section>
            <q-form id="login-form" class="q-gutter-md">
              <q-input filled v-model="username" label="Username" lazy-rules />

              <q-input type="password" filled v-model="password" label="Password" lazy-rules />

              <div>
                <q-btn label="登入" @click="login" type="button" color="primary" />
              </div>
            </q-form>
          </q-card-section>
        </q-card>
      </q-page>
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { api } from 'boot/axios';
import { useRouter } from 'vue-router';

const router = useRouter();

const username = ref('');
const password = ref('');

interface LoginResponse {
  username: string;
  nickname: string;
  role: string;
  accessToken: string;
  refreshToken: string;
}

const login = async () => {
  try {
    const { data } = await api.post<LoginResponse>('/login', {
      username: username.value,
      password: password.value,
    });

    localStorage.setItem('username', data.username);
    localStorage.setItem('nickname', data.nickname);
    localStorage.setItem('role', data.role);
    localStorage.setItem('accessToken', data.accessToken);
    localStorage.setItem('refreshToken', data.refreshToken);

    api.defaults.headers.common.Authorization = `Bearer ${data.accessToken}`;
    await router.push('/');
    console.log('登入成功', data);
  } catch (err) {
    console.error('登入失敗', err);
  }
};
</script>

<style>
.bg-image {
  background-image: linear-gradient(135deg, #7028e4 0%, #e5b2ca 100%);
}
</style>
