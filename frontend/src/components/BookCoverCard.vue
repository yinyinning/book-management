<template>
    <div class="book-cover" :style="{ background: coverBgColor(book.id) }">
        <div class="title">{{ book.title }}</div>
        <div class="author">{{ book.author }}</div>
    </div>
</template>

<script lang="ts" setup>
import type { Book } from '@/models/book';

defineProps<{book: Book}>()
function coverBgColor(id: number): string {
    const colors = ['#5B8DEF', '#E85D75', '#3FB68B', '#F2A65A', '#8E6DD8', '#4ECDC4']
    return colors[(id)%colors.length]
}
</script>

<style scoped>
.book-cover {
    width: 120px;
    height: 194px;
    padding: 10px;
    border-radius: 5px;
    text-align: center;
}
.book-cover .title {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    font-size: 18px;
    color: #fff;
    font-weight: bold;
}
.book-cover .author {
    margin-top: 6px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    font-size: 12px;
    color: rgba(255, 255, 255, 0.8);
    font-weight: bold;
}
</style>