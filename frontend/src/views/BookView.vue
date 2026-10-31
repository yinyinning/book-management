<script setup lang="ts">
    import { ref, onMounted } from 'vue'
    import { searchBooks } from '@/services/book'
    import type { Book } from '@/models/book'

    const keyword = ref('')
    const pageNum = ref(1)
    const pageSize = ref(10)

    // 图书列表（响应式数据，初始为空数组）
    const books = ref<Book[]>([])
    const total = ref(0)
    
    // 加载数据：调用后端接口，把结果存进 books
    async function load() {
        const result = await searchBooks(keyword.value, pageNum.value, pageSize.value)
        books.value = result.records
        total.value = result.total

    }

    // 回到第一页重新查询
    function doSearch() {
      pageNum.value = 1
        load()
    }

    

    // 页面一加载完就执行 load
    onMounted(load)
  </script>

  <template>
<!-- 搜索框 -->
 <div style="margin-bottom: 12px">
<el-input v-model="keyword" placeholder="请输入书名/作者/ISBN" style="width: 300px"clearable @keyup.enter="doSearch" />
<el-button type="primary" @click="doSearch">搜索</el-button>
 </div>
    <!-- 图书列表 -->
    <el-table :data="books" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="isbn" label="ISBN" />
      <el-table-column prop="title" label="书名" />
      <el-table-column prop="author" label="作者" />
      <el-table-column prop="publisher" label="出版社" />
    </el-table>
    <!-- 分页 -->
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[5, 10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="load"
        @size-change="doSearch"
      />
  </template>