import axios from "axios"
import { ElMessage } from "element-plus"
import { useAuthStore } from "@/stores/auth"

const request = axios.create({
  baseURL: '/api',
  timeout: 5000,
})

request.interceptors.request.use(
  (config) => {
    const token = useAuthStore().token
    if (token) {
      config.headers.satoken = token
    }
    return config
  }
)

request.interceptors.response.use(
  (response) => {
    return response.data.data
  },
  (error) => {
    const data=error.response?.data
    if(data){
      ElMessage.error(data.message || '请求失败，请稍后再试')
      if(data.code===401){
        useAuthStore().logout()
        window.location.href='/login'
      }
    }
    else{
      ElMessage.error('网络异常或服务未启动，请稍后再试')
    }
    return Promise.reject(error)
}
)

export default request