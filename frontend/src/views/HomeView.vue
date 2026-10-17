<template>
    <div>
        <h1>欢迎，{{ auth.username }}</h1>
        <p>路由和Element Plus已挂上。下面按钮如果有样式，就说明Element Plus生效了：</p>
        <el-button type="primary">点我试试</el-button>
    </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
</script>