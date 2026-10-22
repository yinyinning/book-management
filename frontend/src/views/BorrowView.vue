<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { listMyRecord, borrow,returnBook,listAllRecord,listUserRecord,helpReturn } from '@/services/borrow'
import type { BorrowRecord } from '@/models/borrow'
import { ElMessage } from 'element-plus'
import {useAuthStore} from '@/stores/auth'

const records = ref<BorrowRecord[]>([])
const bookCopyId = ref('')
const userId = ref('')
const helpBookCopyId = ref('')
const authStore = useAuthStore()
async function load() {
      records.value = await listMyRecord()
    }
async function  doBorrow(){
    await borrow(Number(bookCopyId.value))
        ElMessage.success('借书成功')
        bookCopyId.value =''
        load()
    }
async function  doReturn(){
    await returnBook(Number(bookCopyId.value))
        ElMessage.success('还书成功')
        bookCopyId.value =''
        load()
    }
async function loadAllRecords() {
      records.value = await listAllRecord()
    }
async function loadUserRecords() {
      records.value = await listUserRecord(Number(userId.value))
    }
async function  doHelpReturn(){
    await helpReturn(Number(helpBookCopyId.value))
        ElMessage.success('帮忙还书成功')
        helpBookCopyId.value =''
        loadAllRecords()
    }    


    onMounted(load)

</script>
<template>
    <div style="margin-bottom: 16px">
        <el-input v-model="bookCopyId" placeholder="请输入副本ID" />
        <el-button type="primary" @click="doBorrow">借书</el-button>
        <el-button type="success" @click="doReturn">还书</el-button>
    </div>
    <div style="margin-bottom: 16px " v-if="authStore.role >= 1">
        <el-button type="primary" @click="loadAllRecords">查所有记录</el-button>
        <el-input v-model="userId" placeholder="请输入用户ID" style="width: 180px"/>
        <el-button type="primary" @click="loadUserRecords">查该用户记录</el-button>
        <el-input v-model="helpBookCopyId" placeholder="请输入副本ID" style="width: 180px"/>
        <el-button type="primary" @click="doHelpReturn">帮忙还书</el-button>
    </div>
    <el-table :data="records">
      <el-table-column prop="id" label="ID" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="barcode" label="副本条码" />
      <el-table-column prop="title" label="书名" />
      <el-table-column prop="borrowTime" label="借书时间" />
      <el-table-column prop="dueTime" label="应还时间" />
      <el-table-column prop="returnTime" label="归还时间" />
      <el-table-column prop="statusText" label="状态" />
    </el-table>
</template>