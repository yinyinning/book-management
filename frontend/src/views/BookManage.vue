<script setup lang="ts">
  import { ref, onMounted } from 'vue'
  import { listBooks } from '@/services/book'
  import type { Book } from '@/models/book'

  // 图书列表（响应式数据，初始为空数组）
  const books = ref<Book[]>([])

  // 加载数据：调用后端接口，把结果存进 books
  async function load() {
    books.value = await listBooks()
  }

  // 页面一加载完就执行 load
  onMounted(load)
  </script>

  <template>
    <el-table :data="books" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="isbn" label="ISBN" />
      <el-table-column prop="title" label="书名" />
      <el-table-column prop="author" label="作者" />
      <el-table-column prop="publisher" label="出版社" />
    </el-table>
  </template>