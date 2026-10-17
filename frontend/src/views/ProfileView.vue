<template>
    <div class="profile-view">
        <el-descriptions :column="1" border>
            <el-descriptions-item label="用户名">{{ auth.username }}</el-descriptions-item>
            <el-descriptions-item label="用户ID">{{ auth.id }}</el-descriptions-item>
            <el-descriptions-item label="角色">{{ auth.role===0 ? '普通用户' : auth.role===1 ? '管理员' : '超级管理员' }}</el-descriptions-item>
        </el-descriptions>
    </div>
    <div class="profile-actions">
        <el-button type="primary" @click="router.push('/profile/name')">更改用户名</el-button>
        <el-button type="primary" @click="router.push('/profile/password')">更改密码</el-button>
    </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const auth = useAuthStore()
const router = useRouter()

</script>

<style scoped>
.profile-actions {
    margin-top: 20px;
}
</style>