<template>
  <el-container>
    <el-header>
      <el-menu mode="horizontal" router :default-active="activeMenu">
        <el-menu-item index="/home">首页</el-menu-item>
        <el-menu-item index="/stats" v-if="authStore.role>=1">统计图表</el-menu-item>
        <el-menu-item index="/books">图书浏览</el-menu-item>
        <el-menu-item index="/book-copy" v-if="authStore.role>=1">副本管理</el-menu-item>
         <el-menu-item index="/book" v-if="authStore.role>=1">图书管理</el-menu-item>
         <el-menu-item index="/borrow">我的借阅</el-menu-item>
        <el-menu-item index="/profile">个人中心</el-menu-item>
         <el-menu-item index="/announcement">通告管理</el-menu-item>
        <el-menu-item index="/users" v-if="authStore.role>=1">用户管理</el-menu-item>
      </el-menu>
      <el-dropdown @command="handleCommand">
        <el-avatar>{{ authStore.username.charAt(0) }}</el-avatar>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </el-header>

    <el-main>
      <router-view />
    </el-main>

    <el-footer style="text-align: center; padding: 20px 0;">
      <span>© 2026 图书管理系统</span>
    </el-footer>
  </el-container>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
import { useRouter, useRoute } from 'vue-router'
import { logout } from '@/services/user'
import { computed } from 'vue'

const authStore = useAuthStore()
const router = useRouter()
const route = useRoute()
const activeMenu = computed(() => '/' + route.path.split('/')[1] || '')

async function handleCommand(command: string) {
  if (command === 'logout') {
    await logout()
    authStore.logout()
    router.push('/login')
  }
}
</script>

<style scope>
.el-header {
    margin-bottom: 40px;
}
</style>