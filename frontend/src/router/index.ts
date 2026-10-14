import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router= createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/',
            name: 'Home',
            component: () => import('@/views/HomeView.vue'),
            meta: { requiresAuth: true }, // 添加需要登录的标记
        },
        {
            path: '/login',
            name: 'Login',
            component: () => import('@/views/AuthView.vue'),
            meta: { requiresAuth: false }, // 登录页面不需要登录
        },
    ]
})

router.beforeEach((to) => {
    const authStore = useAuthStore()
    if (to.meta.requiresAuth && !authStore.token) {
        return '/login'
    }
})

export default router