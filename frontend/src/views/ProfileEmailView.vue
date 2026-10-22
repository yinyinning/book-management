<template>
    <div class="email-container">
        <div class="email-card">
            <el-form v-keyboard-nav="onSubmit">
                <el-form-item class="email-top">
                    <el-button :icon="ArrowLeft" circle @click="router.push('/profile')" />
                    <p>绑定邮箱</p>
                </el-form-item>
                <el-form-item>
                    <el-input v-model="email" placeholder="请输入邮箱" />
                </el-form-item>
                <el-form-item>
                    <div class="captcha-row">
                        <el-input v-model="captchaCode" placeholder="请输入验证码" />
                        <img class="captcha-image" :src="captchaImage" alt="验证码" @click="refreshCaptcha" v-if="captchaImage" />
                        <el-button @click="refreshCaptcha" v-else>重试</el-button>
                    </div>
                </el-form-item>
                <el-form-item>
                    <div class="code-row">
                        <el-input v-model="emailCode" placeholder="邮箱验证码" />
                        <el-button @click="sendCode" :disabled="countdown>0">{{ countdown>0 ? countdown+'s' : '获取验证码' }}</el-button>
                    </div>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="onSubmit">确认</el-button>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ArrowLeft } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import { onMounted, onUnmounted, ref } from 'vue';
import { bindingEmailByEmail, getCaptcha, sendBindingCode } from '@/services/user';
import { ElMessage } from 'element-plus';

const router=useRouter()
const email=ref<string | null>(null)
const captchaId=ref('')
const captchaCode=ref('')
const captchaImage=ref('')
const emailCode=ref('')
const countdown=ref(0)
let timer: number | null = null

async function refreshCaptcha() {
    const response=await getCaptcha()
    captchaId.value=response.captchaId
    captchaImage.value=response.image
    captchaCode.value=''
}

async function sendCode() {
    try{
        await sendBindingCode(captchaId.value, captchaCode.value, email.value)
        ElMessage.success("验证码发送成功")
        startCount()
        refreshCaptcha()
    }catch{
        refreshCaptcha()
    }
}

async function onSubmit() {
    try{
        await bindingEmailByEmail(email.value, emailCode.value)
        ElMessage.success('绑定成功')
        router.push('/profile')
    }catch{
        refreshCaptcha()
    }
}

function startCount(){
    countdown.value=60
    timer=window.setInterval(()=>{
        countdown.value--
        if(countdown.value<=0){
            window.clearInterval(timer!)
        }
    },1000)
}

onMounted(()=>{
    refreshCaptcha()
})

onUnmounted(()=>{
    if(timer){
        window.clearInterval(timer)
    }
})
</script>

<style scoped>
.email-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100vh;
}
.email-card {
    background-color: #fff;
    width: 360px;
    padding: 32px;
    border-radius: 12px;
    box-shadow: 0px 4px 16px rgba(0,0,0,.08);
}
.captcha-row {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}
.captcha-image {
  height: 40px;
  cursor: pointer;
  border-radius: 4px;
}
.code-row {
    display: flex;
    align-items: center;
    gap: 8px;
    width: 100%;
}
</style>