<template>
    <div class="profile-container">
        <div class="profile-avatar">
            <el-image class="avatar" v-if="auth.avatar" :src="auth.avatar" :preview-src-list="[auth.avatar]" fit="cover" />
            <el-avatar v-else :size="100">{{ auth.username.charAt(0) }}</el-avatar>
            <el-button @click="router.push('/profile/avatar')">更换头像</el-button> 
        </div>
        <div class="profile-view">
            <el-descriptions :column="1" border>
                <el-descriptions-item label="用户名">{{ auth.username }}</el-descriptions-item>
                <el-descriptions-item label="用户ID">{{ auth.id }}</el-descriptions-item>
                <el-descriptions-item label="角色">{{ auth.role===0 ? '普通用户' : auth.role===1 ? '管理员' : '超级管理员' }}</el-descriptions-item>
                <el-descriptions-item label="邮箱">{{ auth.email || '无'}}</el-descriptions-item>
            </el-descriptions>
        </div>
        <div class="profile-actions">
            <el-button type="primary" @click="router.push('/profile/email')">{{ auth.email===null ? '绑定邮箱' : '更改邮箱' }}</el-button>
            <el-button type="primary" @click="router.push('/profile/name')">更改用户名</el-button>
            <el-button type="primary" @click="router.push('/profile/password')">更改密码</el-button>
        </div>
        <div class="loginout-button">
            <el-button type="warning" @click="logoutCheck">退出登录</el-button>
        </div>
    </div>
</template>

<script setup lang="ts">
import { logout } from '@/services/user'
import { useAuthStore } from '@/stores/auth'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'

const auth = useAuthStore()
const router = useRouter()

async function logoutCheck() {
    try{
        await ElMessageBox.confirm('确定退出吗？',"提示",{type: 'warning'})
    }catch{
        return
    }
    await logout()
    auth.logout()
    router.push('/login')
    ElMessage.success('退出登录成功')
}
</script>

<style scoped>
.avatar {
    width: 100px;
    height: 100px;
    border-radius: 50%;
}
.profile-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 20px;
}
.profile-avatar {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 15px;
}
.profile-view {
    width: 100%;
}
.profile-actions {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
}
</style>