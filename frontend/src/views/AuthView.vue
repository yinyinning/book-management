<template>
    <div class="auth-container">
      <div class="auth-card">
        <el-radio-group v-model="mode">
            <el-radio-button value="login">登录</el-radio-button>
            <el-radio-button value="register">注册</el-radio-button>
        </el-radio-group>
        <el-form v-keyboard-nav="onSubmit">
            <el-form-item>
              <el-input v-model="username" v-if="mode==='login' && loginMode==='userLogin' || mode==='register'" placeholder="请输入用户名" />
              <el-input v-model="email" v-if="mode==='login' && loginMode==='emailLogin'" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="password" type="password" show-password placeholder="请输入密码" />
            </el-form-item>
            <el-form-item>
              <div class="captcha-row">
                <el-input v-model="captchaCode" placeholder="请输入验证码" />
                <img class="captcha-img" :src="captchaImage" @click="refreshCaptcha" alt="验证码" v-if="captchaImage" />
                <el-button @click="refreshCaptcha" v-else>重试</el-button>
              </div>
            </el-form-item>
            <el-form-item v-if="mode==='register'">
              <el-input v-model="email" placeholder="请输入邮箱（选填，用于找回密码）" />
            </el-form-item>
            <el-form-item v-if="mode==='register' && email">
              <div class="code-row">
                <el-input v-model="emailCode" placeholder="邮箱验证码"></el-input>
                <el-button @click="sendCode" :disabled="countdown>0">{{ countdown>0 ? countdown+'s' : '获取验证码' }}</el-button>
              </div>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="onLogin" v-if="mode === 'login'">登录</el-button>
                <el-button type="primary" @click="onRegister" v-if="mode==='register'">注册</el-button>
            </el-form-item>
            <el-form-item>
              <div class="link-container">
                <el-button link type="primary" v-if="mode==='login'" @click="router.push('/forgot-password')">忘记密码？</el-button>
                <el-button link type="primary" v-if="mode==='login' && loginMode==='userLogin'" @click="loginMode='emailLogin'">邮箱登录？</el-button>
                <el-button link type="primary" v-if="mode==='login' && loginMode==='emailLogin'" @click="loginMode='userLogin'">用户名登录？</el-button>
              </div>
            </el-form-item>
        </el-form>
      </div>
    </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { register, getCaptcha, sendBindingCode } from '@/services/user'

const username = ref('')
const password = ref('')
const router = useRouter()
const authStore = useAuthStore()
const mode = ref<'login' | 'register'>('login')
const loginMode = ref<'userLogin' | 'emailLogin'>('userLogin')
const captchaId=ref('')
const captchaCode=ref('')
const captchaImage=ref('')
const email=ref<string | null>(null)
const emailCode=ref('')
const countdown=ref(0)
let timer: number | null = null

watch(mode, () => {
    username.value = ''
    password.value = ''
    captchaCode.value = ''
    email.value=null
    emailCode.value=''
    if(timer){
      window.clearInterval(timer)
      timer=null
    }
    countdown.value=0
    refreshCaptcha()
})

async function onLogin() {
  try {
    await authStore.doLogin(username.value, password.value, captchaId.value, captchaCode.value, loginMode.value, email.value)
    ElMessage.success('登录成功')
    router.push('/')
  } catch {
    refreshCaptcha()
  }
}

async function onRegister() {
  try {
    await register(username.value, password.value, captchaId.value, captchaCode.value, email.value, emailCode.value)
    ElMessage.success('注册成功，请登录')
    mode.value = 'login'
  } catch {
    refreshCaptcha()
  }
}

async function refreshCaptcha() {
  const response = await getCaptcha()
  captchaId.value = response.captchaId
  captchaImage.value = response.image
  captchaCode.value = ''
}

async function sendCode() {
  try{
    await sendBindingCode(captchaId.value, captchaCode.value, email.value)
    ElMessage.success('验证码发送成功')
    startCount()
    refreshCaptcha()
  }catch{
    refreshCaptcha()
  }
}

function onSubmit(){
  if(mode.value==="login"){
    onLogin()
  }else{
    onRegister()
  }
}

function startCount(){
  countdown.value=60
  timer=window.setInterval(()=>{
    countdown.value--
    if(countdown.value<=0){
      window.clearInterval(timer!)
      timer=null
    }
  },1000)
}

onMounted(() => {
  refreshCaptcha()
})

onUnmounted(()=>{
  if(timer){
    window.clearInterval(timer)
  }
})
</script>

<style scoped>
.auth-container {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 100vh;
}
.auth-card {
  background: #fff;
  width: 360px;
  padding: 32px;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0,0,0,.08);
}
.auth-card .el-radio-group {
  width: 100%;
  margin-bottom: 24px;
}
.auth-card .el-radio-button {
  flex: 1;
}
.auth-card :deep(.el-radio-button__inner) {
  width: 100%;
  padding: 12px 0;
}
.auth-card .el-button {
  width: 100%;
}
.captcha-row {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}
.captcha-img {
  height: 40px;
  cursor: pointer;
  border-radius: 4px;
}
.captcha-row .el-input {
  flex: 1;
}
.link-container {
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-items: center;
  align-items: center;
  gap: 10px;
}
.link-container :hover {
  text-decoration: underline;
}
.code-row {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 10px;
}
.code-row .el-button {
  flex: 1;
}
</style>