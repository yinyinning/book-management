<template>
    <div class="forgot-container">
        <div class="forgot-card">
            <el-form v-keyboard-nav="onSubmit">
                <el-form-item>
                    <el-button :icon="ArrowLeft" circle @click="router.push('/login')" />
                    <p>重置密码</p>
                </el-form-item>
                <el-form-item>
                    <el-input v-model="email" :disabled="step===2" placeholder="请输入邮箱（请确认账号已绑定邮箱）"/>
                </el-form-item>
                <el-form-item>
                    <div class="captcha-row">
                        <el-input v-model="captchaCode" placeholder="请输入验证码"/>
                        <img class="captcha-img" :src="captchaImage" @click="refreshCaptcha" alt="验证码" v-if="captchaImage"/>
                        <el-button @click="refreshCaptcha" v-else>重试</el-button>
                    </div>
                </el-form-item>
                <el-form-item>
                    <div class="code-row">
                        <el-input v-model="emailCode" placeholder="邮箱验证码"/>
                        <el-button @click="sendCode" :disabled="countdown>0">{{ countdown>0 ? countdown+'s' : '获取验证码' }}</el-button>
                    </div>
                </el-form-item>
                <el-form-item v-if="step===2">
                    <el-input v-model="newPassword" placeholder="请输入新密码"/>
                </el-form-item>
                <el-form-item v-if="step===2">
                    <el-button @click="onSubmit">重置密码</el-button>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script setup lang="ts">
import { getCaptcha, resetPasswordByEmail, sendResetCode } from '@/services/user';
import { ArrowLeft } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { onMounted, onUnmounted, ref } from 'vue';
import { useRouter } from 'vue-router';

const router=useRouter()
const newPassword=ref('')
const email=ref('')
const emailCode=ref('')
const countdown=ref(0)
const captchaId=ref('')
const captchaCode=ref('')
const captchaImage=ref('')
const step=ref(1)
let timer: number | null = null

async function sendCode() {
    try{
        await sendResetCode(captchaId.value, captchaCode.value, email.value)
        ElMessage.success("验证码发送成功")
        startCount()
        step.value=2
    }catch{
        refreshCaptcha()
    }
}

async function refreshCaptcha() {
    const response=await getCaptcha()
    captchaId.value=response.captchaId
    captchaImage.value=response.image
    captchaCode.value=''
}

async function doReset() {
    try{
        await resetPasswordByEmail(email.value, emailCode.value, newPassword.value)
        ElMessage.success('密码重置成功')
        router.push('/login')
    }catch{
        refreshCaptcha()
    }
}

function onSubmit(){
    if(step.value===1){
        sendCode()
    }else{
        doReset()
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
.forgot-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100vh;
}
.forgot-card {
    background: #fff;
    width: 360px;
    padding: 32px;
    border-radius: 12px;
    box-shadow: 0 4px 16px rgba(0,0,0,.08);
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
.code-row {
    display: flex;
    align-items: center;
    gap: 8px;
    width: 100%;
}
.code-row .el-input {
    flex: 1;
}
</style>