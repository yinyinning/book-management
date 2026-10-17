<template>
    <div>
        <el-form v-keyboard-nav="onSubmit">
            <el-form-item>
                <el-button :icon="ArrowLeft" circle @click="router.push('/profile')" />
                <p>更改用户名</p>
            </el-form-item>
            <el-form-item>
                <el-input v-model="username" placeholder="请输入用户名" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" :disabled="username===oldUsername || username===''" @click="onSubmit">保存</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { updateUsername } from '@/services/user'
import { ArrowLeft } from '@element-plus/icons-vue'

const auth = useAuthStore()
const oldUsername=auth.username
const username = ref(auth.username)
const router = useRouter()

async function onSubmit() {
    if(username.value===oldUsername){
        return
    }
    if (!username.value) {
        ElMessage.error('新用户名不能为空')
        return
    }
    await updateUsername(username.value)
    ElMessage.success('用户名更新成功')
    auth.username = username.value
    router.push('/profile')
}
</script>