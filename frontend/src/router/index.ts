import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router= createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/',
            component: () => import('@/components/Navi.vue'),
            meta: { requiresAuth: true }, // 添加需要登录的标记
            redirect: '/home',
            children: [
                {
                    path: 'home',
                    name: 'Home',
                    component: () => import('@/views/HomeView.vue'),
                },
                {
                    path: 'stats',
                    name: 'Stats',
                    component: () => import('@/views/StatsView.vue'),
                },
                {
                    path: 'books',
                    name: 'Books',
                    component: () => import('@/views/BookView.vue'),
                },
                {
                    path: 'borrow',
                    name: 'Borrow',
                    component: () => import('@/views/BorrowView.vue'),
                },
                {
                    path: 'profile',
                    name: 'Profile',
                    component: () => import('@/views/ProfileView.vue'),
                },
                {
                    path: 'profile/name',
                    name: 'Name',
                    component: () => import('@/views/ProfileNameView.vue'),
                },
                {
                    path: 'profile/password',
                    name: 'Password',
                    component: () => import('@/views/ProfilePasswordView.vue'),
                },
                {
                    path: 'profile/email',
                    name: 'Email',
                    component: () => import('@/views/ProfileEmailView.vue'),
                },
                {
                    path: 'users',
                    name: 'Users',
                    component: () => import('@/views/UserManageView.vue'),
                }
            ]
        },
        {
      path: '/book',
      name: 'BookManage',
      component: () => import('@/views/BookManage.vue'),
        },
        {
      path: '/book-copy',
      name: 'BookCopyManage',
      component: () => import('@/views/BookCopyManage.vue'),
        },
        {
            path: '/login',
            name: 'Login',
            component: () => import('@/views/AuthView.vue'),
            meta: { requiresAuth: false }, // 登录页面不需要登录
        },
        {
            path: '/forgot-password',
            name: 'ForgotPassword',
            component: () => import('@/views/ForgotPasswordView.vue'),
            meta: { requiresAuth: false },
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