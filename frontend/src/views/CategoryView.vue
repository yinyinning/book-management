<template>
    <div class="category-container">
        <div class="categories">
            <p v-if="typeList.length===0">暂无分类</p>
            <el-tag v-for="t in typeList" :key="t.id" :class="{ active: t.id===activeType }" @click="selectType(t.id)">{{ t.name }}</el-tag>
        </div>
        <div class="content">
            <p v-if="activeType===0">请选择左侧分类</p>
            <p v-else-if="bookList.length===0">该分类暂无图书</p>
            <BookCoverCard v-for="b in bookList" :key="b.id" :book="b" />
        </div>
    </div>
</template>

<script lang="ts" setup>
import type { Book, BookType } from '@/models/book';
import { getAllTypes, getBooksByType } from '@/services/book';
import { onMounted, ref } from 'vue';

const typeList=ref<BookType[]>([])
const bookList=ref<Book[]>([])
const activeType=ref(0)

async function fetch() {
    typeList.value=await getAllTypes()
}

async function selectType(id: number) {
    activeType.value=id
    bookList.value=await getBooksByType(id)
}

onMounted(()=>{
    fetch()
})
</script>

<style scoped>
.category-container {
    display: flex;
}
.categories {
    width: 180px;
    display: flex;
    flex-direction: column;
}
.content {
    flex: 1;
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
}
.category-container p {
    width: 100%;
    text-align: center;
}
.active {
    background-color: #409eff;
    color: #fff;
}
</style>