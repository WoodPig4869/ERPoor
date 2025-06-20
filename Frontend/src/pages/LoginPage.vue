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
import { defineComponent } from 'vue';
import { ref } from 'vue';
import { api } from 'boot/axios';
</script>

<style>
.bg-image {
  background-image: linear-gradient(135deg, #7028e4 0%, #e5b2ca 100%);
}
</style>
