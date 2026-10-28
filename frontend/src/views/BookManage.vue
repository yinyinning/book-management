<script setup lang="ts">
    import { ref, onMounted } from 'vue'
    import { ElMessage,ElMessageBox } from 'element-plus'
    import { listBooks, addBook,updateBook,deleteBook } from '@/services/book'
    import type { Book } from '@/models/book'

    // 图书列表（响应式数据，初始为空数组）
    const books = ref<Book[]>([])

    // 新增对话框是否显示
    const dialogVisible = ref(false)
    //编辑
    const editId = ref<number | null>(null)
    // 新增表单的四个字段
    const form = ref({
      isbn: '',
      title: '',
      author: '',
      publisher: '',
    })

    // 加载数据：调用后端接口，把结果存进 books
    async function load() {
      books.value = await listBooks()
    }

    // 打开新增对话框：先清空表单，再显示
    function openAdd() {
      form.value = { isbn: '', title: '', author: '', publisher: '' }
      dialogVisible.value = true
      editId.value = null
    }

    function openEdit(book:Book) {
      form.value = { isbn: book.isbn, title: book.title, author: book.author, publisher: book.publisher }
      dialogVisible.value = true
      editId.value = book.id
    }
    // 提交新增：调 addBook，成功后提示、关对话框、刷新列表
    async function submitAdd() {
      if(editId.value === null){
      await addBook(form.value)
      ElMessage.success('新增成功')
      dialogVisible.value = false
      await load()}
      else{await updateBook({ id: editId.value,...form.value})
        ElMessage.success('更新成功')
        dialogVisible.value = false
        await load()
    }
    }

    async function remove(book: Book) {
        try {
          await ElMessageBox.confirm(`确认删除图书 ${book.title} 吗？`, '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          })
          await deleteBook(book.id)
          ElMessage.success('删除成功')
          await load()
        } catch (error) {
          // 用户取消删除，不做任何处理
        }
      }

    // 页面一加载完就执行 load
    onMounted(load)
  </script>

  <template>
    <!-- 顶部：新增按钮 -->
    <div style="margin-bottom: 16px">
      <el-button type="primary" @click="openAdd">新增图书</el-button>
    </div>

    <!-- 图书列表 -->
    <el-table :data="books" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="isbn" label="ISBN" />
      <el-table-column prop="title" label="书名" />
      <el-table-column prop="author" label="作者" />
      <el-table-column prop="publisher" label="出版社" />
      <el-table-column label="操作" >  
        <template #default="scope">
          <el-button type="primary" @click="openEdit(scope.row)">编辑</el-button>
          <el-button type="danger" @click="remove(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增对话框 -->
    <el-dialog v-model="dialogVisible" :title="editId === null ? '新增图书' : '编辑图书'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="ISBN">
          <el-input v-model="form.isbn" />
        </el-form-item>
        <el-form-item label="书名">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="form.author" />
        </el-form-item>
        <el-form-item label="出版社">
          <el-input v-model="form.publisher" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">确定</el-button>
      </template>
    </el-dialog>
  </template>