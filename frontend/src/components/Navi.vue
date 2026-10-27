<template>
  <el-container>
    <el-header class="header">
      <div class="logo">
        <el-icon><Reading /></el-icon>
        <span>图书管理系统</span>
      </div>
      <el-menu mode="horizontal" router :default-active="activeMenu">
        <el-menu-item index="/home">首页</el-menu-item>
        <el-menu-item index="/stats" v-if="authStore.role>=1">统计图表</el-menu-item>
        <el-menu-item index="/books">图书浏览</el-menu-item>
        <el-menu-item index="/borrow">我的借阅</el-menu-item>
        <el-sub-menu index="profile">
          <template #title>个人中心</template>
          <el-menu-item index="/profile">我的资料</el-menu-item>
          <el-menu-item index="/profile/name">修改用户名</el-menu-item>
          <el-menu-item index="/profile/password">修改密码</el-menu-item>
          <el-menu-item index="/profile/email">{{ authStore.email===null ? '绑定邮箱' : '更改邮箱' }}</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/users" v-if="authStore.role>=1">用户管理</el-menu-item>
        <el-menu-item index="/book-manage" v-if="authStore.role>=1">图书管理</el-menu-item>
        <el-menu-item index="/book-copy" v-if="authStore.role>=1">副本管理</el-menu-item>
      </el-menu>
      <el-dropdown @command="handleCommand">
        <el-avatar :src="authStore.avatar ?? undefined">{{ authStore.username.charAt(0) }}</el-avatar>
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
import { Reading } from '@element-plus/icons-vue'
 
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

<style scoped>
.el-header {
  display: flex;
  align-items: center;
  gap: 20px;
}
.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: bold;
  font-size: 18px;
}
.el-menu {
  flex: 1;
}
</style>