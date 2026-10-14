import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login } from '@/services/user'

export const useAuthStore = defineStore('auth', () => {
  const token = ref('')
  const id = ref<number>(0)
  const username = ref('')
  const role = ref(0)

  async function doLogin(u: string, p: string, cId: string, cCode: string) {
    try {
      const result = await login(u, p, cId, cCode)
      token.value = result.token
      id.value = result.id
      username.value = result.username
      role.value = result.role
    } catch (error) {
      console.error('Login failed:', error)
      throw error
    }
  }

  function logout() {
    token.value = ''
    id.value = 0
    username.value = ''
    role.value = 0
  }

  return {
    token,
    id,
    username,
    role,
    doLogin,
    logout,
  }
},{
  persist: true,
})