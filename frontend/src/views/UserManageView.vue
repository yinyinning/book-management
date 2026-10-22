<template>
    <div class="new-button">
        <el-button @click="newUser">新建用户</el-button>
        <el-button @click="newAdmin" v-if="authStore.role===2">新建管理员</el-button>
    </div>
    <el-table :data="users">
        <el-table-column prop="id" label="用户ID"/>
        <el-table-column prop="username" label="用户名"/>
        <el-table-column prop="email" label="邮箱"/>
        <el-table-column label="角色">
            <template #default="scope">{{ getRoleText(scope.row.role) }}</template>
        </el-table-column>
        <el-table-column label="状态">
            <template #default="scope">{{ getStatusText(scope.row.status) }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间"/>
        <el-table-column prop="updateTime" label="更新时间"/>
        <el-table-column label="操作">
            <template #default="scope">
                <el-button @click="toggleStatus(scope.row)">{{ scope.row.status===1? '禁用' : '启用' }}</el-button>
                <el-button @click="removeUser(scope.row)" type="danger">删除</el-button>
            </template>
        </el-table-column>
    </el-table>
    <el-dialog v-model="dialogVisible" :title="createRole==='user'?'新增用户':'新增管理员'">
        <el-form v-keyboard-nav="onSubmit">
            <el-form-item>
                <el-input v-model="newUsername" placeholder="用户名"/>
            </el-form-item>
            <el-form-item>
                <el-input v-model="newPassword" type="password" show-password placeholder="密码"/>
            </el-form-item>
            <el-form-item>
                <div class="captcha-row">
                    <el-input v-model="captchaCode" placeholder="验证码"/>
                    <img :src="captchaImg" @click="refreshCaptcha" alt="验证码" v-if="captchaImg"/>
                </div>
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button type="primary" @click="onSubmit">确认</el-button>
            <el-button @click="dialogVisible=false">取消</el-button>
        </template>
    </el-dialog>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { listUsers, deleteUserById, updateUserStatus, type UserResult, getCaptcha, register, registerAdmin } from '@/services/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const users=ref<UserResult[]>([])
const dialogVisible=ref(false)
const newUsername=ref('')
const newPassword=ref('')
const captchaId=ref('')
const captchaCode=ref('')
const captchaImg=ref('')
const authStore=useAuthStore()
const createRole=ref<'user' | 'admin'>('user')

function getRoleText(role: number){
    return role===0?"普通用户":role===1?"普通管理员":"超级管理员"
}
function getStatusText(status: number){
    return status===0?"禁用":"正常"
}
function newUser(){
    dialogVisible.value=true;
    createRole.value='user'
}
function newAdmin(){
    dialogVisible.value=true;
    createRole.value='admin'
}
async function fetch() {
    users.value=await listUsers()
}
async function toggleStatus(userResult: UserResult){
    await updateUserStatus(userResult.id, userResult.status===0?1:0)
    ElMessage.success('操作成功')
    await fetch()
}
async function removeUser(userResult: UserResult){
    try{
        await ElMessageBox.confirm(`确定删除用户⌈${userResult.username}⌋吗？`,"提示",{type: 'warning'})
    }catch{
        return
    }
    await deleteUserById(userResult.id)
    ElMessage.success('删除成功')
    await fetch()
}
async function refreshCaptcha(){
    const response=await getCaptcha()
    captchaId.value=response.captchaId
    captchaCode.value=''
    captchaImg.value=response.image
}
async function onSubmit() {
    try{
        if(createRole.value==='user'){
            await register(newUsername.value, newPassword.value, captchaId.value, captchaCode.value)
        }else{
            await registerAdmin(newUsername.value, newPassword.value, captchaId.value, captchaCode.value)
        }
        ElMessage.success('注册成功')
        newUsername.value=''
        newPassword.value=''
        refreshCaptcha()
        fetch()
    }catch{
        refreshCaptcha()
    }
}

onMounted(()=>{fetch(),refreshCaptcha()})
</script>

<style scope>
.new-button {
    margin-bottom: 10px;
}
.captcha-row {
    display: flex;
    align-items: center;
    gap: 8px;
    width: 100%;
}

.captcha-row img{
    cursor: pointer;
    border-radius: 4px;
}
</style>

