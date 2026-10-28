<template>
    <div class="avatar-container">
        <div class="arrowleft-row">
            <el-button :icon="ArrowLeft" circle @click="router.push('/profile')" />
        </div>
        <div class="preset-list">
            <el-avatar v-for="n in 16" :key="n" :size="48" :src="`/preset-avatar/${n}.svg`" @click="selectPreset(n)" />
        </div>
        <el-upload :show-file-list="false" accept="image/png,image/webp,image/jpeg" :http-request="handleUpload">
            <el-button>上传本地图片</el-button>
        </el-upload>
    </div>
</template>

<script lang="ts" setup>
import { updateAvatar, uploadAvatar } from '@/services/user';
import { useAuthStore } from '@/stores/auth';
import { ArrowLeft } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';

const auth=useAuthStore()
const router=useRouter()

async function handleUpload(options: any) {
    const avatar=await uploadAvatar(options.file)
    auth.avatar=avatar
    ElMessage.success('上传照片成功')
    router.push('/profile')
}
async function selectPreset(n: number) {
    const path=`/preset-avatar/${n}.svg`
    await updateAvatar(path)
    auth.avatar=path
    ElMessage.success('头像更换成功')
    router.push('/profile')
}
</script>

<style scoped>
.avatar-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap:25px;
}
.arrowleft-row {
    width: 100%;
    text-align: left;
}
.preset-list {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
}
.preset-list .el-avatar {
    cursor: pointer;
}
</style>