<template>
    <div>
        <el-form v-keyboard-nav="onSubmit">
            <el-form-item>
                <el-button :icon="ArrowLeft" circle @click="router.push('/profile')" />
                <p>更改密码</p>
            </el-form-item>
            <el-form-item label="旧密码">
                <el-input v-model="oldPassword" type="password" placeholder="请输入现密码" />
            </el-form-item>
            <el-form-item label="新密码">
                <el-input v-model="newPassword" type="password" placeholder="请输入新密码" />
            </el-form-item>
            <el-form-item label="确认新密码">
                <el-input v-model="confirmPassword" type="password" placeholder="请再次输入新密码" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="onSubmit">确认</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { updatePassword } from '@/services/user'
import { ArrowLeft } from '@element-plus/icons-vue'

const auth = useAuthStore()
const oldPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const router = useRouter()

async function onSubmit() {
    if (!oldPassword.value || !newPassword.value || !confirmPassword.value) {
        ElMessage.error('请填写所有密码字段')
        return
    }
    if (newPassword.value !== confirmPassword.value) {
        ElMessage.error('新密码和确认密码不匹配')
        return
    }
    await updatePassword(oldPassword.value, newPassword.value)
    ElMessage.success('密码更新成功，请重新登录')
    auth.logout()
    router.push('/login')
}
</script>

<style scoped>
.el-form-item :deep(.el-form-item__label) {
    width: 120px;
    font-weight: bold;
    color: #333;
}
</style>