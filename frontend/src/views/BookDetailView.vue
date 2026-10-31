<template>
    <div class="book-detail-container">
        <div class="back-button">
            <el-button :icon="ArrowLeft" circle @click="router.push('/home')"></el-button>
        </div>
        <div class="detail" v-if="book">
            <h2>{{ book.title }}</h2>
            <p>作者：{{ book.author }}</p>
            <p>出版社：{{ book.publisher }}</p>
            <p>ISBN：{{ book.isbn }}</p>
            <p class="available-copy-count">空闲副本数：{{ availableCount }}</p>
            <div class="types-container">
                <span>分类</span>
                <div class="types-tags">
                    <el-tag v-for="t in book.types" :key="t.id">{{ t.name }}</el-tag>
                    <span v-if="book.types.length===0">无</span>
                </div>
            </div>
        </div>
        <p v-else>无内容</p>
        <div class="borrow-button">
            <el-button @click="borrowDialogVisible=true" :disabled="borrowLimit===0">借阅</el-button>
        </div>
        <el-dialog v-model="borrowDialogVisible">
            <div class="borrow-dialog">
                <h3>请选择借阅数量</h3>
                <el-input-number v-model="borrowCount" :min="1" :max="borrowLimit" />
                <el-button @click="onBorrow">确认</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script lang="ts" setup>
import type { Book } from '@/models/book';
import type { BookCopy } from '@/models/bookCopy';
import { getById } from '@/services/book';
import { listByBookId } from '@/services/bookCopy';
import { borrow, countMyBorrowing, listMyBookId } from '@/services/borrow';
import { ArrowLeft } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { computed, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const router=useRouter()
const route=useRoute()
const bookId=Number(route.params.id)
const book=ref<Book | null>(null)
const bookCopies=ref<BookCopy[]>([])
const availableBookCopies=computed(()=>bookCopies.value.filter(b=>b.status===0))
const availableCount=computed(()=>availableBookCopies.value.length)
const borrowCount=ref(1)
const borrowingCount=ref(0)
const borrowedBookIds=ref<number[]>([])
const borrowLimit=computed(()=>Math.min(availableCount.value, 8-borrowingCount.value))
const isBorrowing=computed(()=>borrowedBookIds.value.includes(bookId))
const borrowDialogVisible=ref(false)

async function fetch(){
    book.value=await getById(bookId)
    bookCopies.value=await listByBookId(bookId)
    borrowingCount.value=await countMyBorrowing()
    borrowedBookIds.value=await listMyBookId()
}
async function onBorrow() {
    if(isBorrowing.value){
        try{
            await ElMessageBox.confirm('您已经借过这本书了，确认再借吗？','提示',{ type: 'warning'})
        }catch{
            return
        }
    }
    const barcodeList: string[] = []
    let successCount=0
    let failCount=0
    for(let i=0;i<borrowCount.value;i++){
        try{
            const barcode=await borrow(availableBookCopies.value[i].id)
            barcodeList.push(barcode)
            successCount++
        }catch{
            failCount++
            ElMessage.error('借阅失败一本')
        }
    }
    ElMessageBox.alert(`成功借阅 ${successCount} 本（失败 ${failCount} 本），副本条码：${barcodeList.join('、')}`,`${successCount>0 ? '借阅成功' : '借阅失败'}`,{ confirmButtonText: '知道了', type: `${successCount>0 ? 'success' : 'warning'}`})
    borrowDialogVisible.value=false
    borrowCount.value=1
    fetch()
}

onMounted(fetch)
</script>

<style scoped>
.book-detail-container {
    display: flex;
    flex-direction: column;
    justify-content: center;
    gap: 60px;
}
.available-copy-count {
    font-size: 18px;
    font-weight: bold;
    color: #409eff;
}
.back-button {
    width: 50%;
}
.detail {
    display: flex;
    flex-direction: column;
    justify-content: center;
    gap: 20px;
}
.borrow-dialog {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    gap: 20px;
}
.types-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 10px;
}
.types-tags {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    align-items: center;
    gap: 10px;
}
</style>