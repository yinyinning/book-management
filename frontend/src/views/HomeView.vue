<template>
    <div class="home-container">
        <div class="top-row">
            <el-input v-model="search" placeholder="搜索" @keyup.enter="onSearch" />
            <el-button :disabled="!search" @click="onSearch">确认</el-button>
            <div class="category-button" @click="goCategories">
                <div></div>
                <div></div>
                <div></div>
                <div></div>
            </div>
        </div>
        <div class="newest-announcement">

        </div>
        <div class="body-container">
            <div class="left-newest">
                <p>最新图书</p>
                <div class="book-row">
                    <p v-if="newestBookList.length===0">暂无图书</p>
                    <book-cover-card v-for="b in newestBookList" :key="b.id" :book="b" />
                </div>
            </div>
            <div class="right-hotest">
                <p>最热图书</p>
                <div class="book-row">
                    <p v-if="hotestBookList.length===0">暂无图书</p>
                    <book-cover-card v-for="b in hotestBookList" :key="b.id" :book="b" />
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import type { Book } from '@/models/book';
import { listBooks } from '@/services/book';
import BookCoverCard from '@/components/BookCoverCard.vue';
import { useRouter } from 'vue-router';

const router=useRouter()
const search=ref('')
const bookList=ref<Book[]>([])
const newestBookList=computed(()=>bookList.value.slice(-10).reverse())
const hotestBookList=computed(()=>{
    //占位，现在后端图书还没有借阅次数字段，有了之后改成真的最热图书
    return bookList.value.slice(0,10)
})

function onSearch(){
    router.push({ path: '/books', query: { keyword: search.value } })
}
function goCategories(){
    router.push('/categories')
}

async function fetch(){
    bookList.value=await listBooks()
}

onMounted(()=>{
    fetch()
})
</script>

<style scoped>
.book-row {
    display: flex;
    gap: 10px;
    margin-top: 20px;
    flex-wrap: wrap;
}
.category-button {
    display: grid;
    grid-template-columns: repeat(2,1fr);
    gap: 2px;
    cursor: pointer;
}
.category-button div {
    width: 10px;
    height: 10px;
    background: #fff;
    border: 1px solid #333;
}
.home-container {
    display: flex;
    flex-direction: column;
    gap: 18px;
    padding: 20px;
}
.top-row {
    display: flex;
    align-items: center;
    gap:10px;
}
.body-container {
    display: flex;
    gap: 20px;
}
.left-newest,.right-hotest {
    flex: 1;
}
.left-newest p,.right-hotest p {
    width: 100%;
    text-align: center;
}
</style>