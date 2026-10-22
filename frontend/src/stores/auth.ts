import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, emailLogin } from '@/services/user'

export const useAuthStore = defineStore('auth', () => {
  const token = ref('')
  const id = ref<number>(0)
  const username = ref('')
  const role = ref(0)
  const email= ref<string | null>(null)

  async function doLogin(u: string, p: string, cId: string, cCode: string, loginMode: string, e:string | null) {
    try {
      const result = loginMode==='userLogin' ? await login(u, p, cId, cCode): await emailLogin(e, p, cId, cCode)
      token.value = result.token
      id.value = result.id
      username.value = result.username
      role.value = result.role
      email.value = result.email
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
    email.value = null
  }

  return {
    token,
    id,
    username,
    role,
    email,
    doLogin,
    logout,
  }
},{
  persist: true,
})