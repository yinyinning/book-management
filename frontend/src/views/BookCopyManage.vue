<script setup lang="ts">
    import { ref, onMounted } from 'vue'
    import { ElMessage,ElMessageBox } from 'element-plus'
    import { listBookCopies, addBookCopy, updateBookCopy, deleteBookCopy } from '@/services/bookCopy'
    import type { BookCopy } from '@/models/bookCopy'

    // 图书副本列表（响应式数据，初始为空数组）
    const bookCopies = ref<BookCopy[]>([])

    // 新增对话框是否显示
    const dialogVisible = ref(false)
    //编辑
    const editId = ref<number | null>(null)
    // 新增表单的两个字段
    const form = ref({
      bookId:0,
      barcode: ''
    })

    // 加载数据：调用后端接口，把结果存进 bookCopies
    async function load() {
      bookCopies.value = await listBookCopies()
    }

    // 打开新增对话框：先清空表单，再显示
    function openAdd() {
      form.value = { bookId:0,barcode: '' }
      dialogVisible.value = true
      editId.value = null
    }

    function openEdit(book:BookCopy) {
      form.value = { bookId: book.bookId, barcode: book.barcode }
      dialogVisible.value = true
      editId.value = book.id
    }
    
    // 提交新增：调 addBookCopy，成功后提示、关对话框、刷新列表
    async function submitAdd() {
      if(editId.value === null){
      await addBookCopy(form.value)
      ElMessage.success('新增成功')
      dialogVisible.value = false
      await load()}
      else{await updateBookCopy({ id: editId.value,...form.value})
        ElMessage.success('更新成功')
        dialogVisible.value = false
        await load()
    }
    }

    async function remove(book: BookCopy) {
        try {
          await ElMessageBox.confirm(`确认删除图书副本 ${book.barcode} 吗？`, '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          })
          await deleteBookCopy(book.id)
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
      <el-button type="primary" @click="openAdd">新增图书副本</el-button>
    </div>

    <!-- 图书副本列表 -->
    <el-table :data="bookCopies" border>
        <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="bookId" label="BOOKID" width="80" />
      <el-table-column prop="barcode" label="条码" />
      <el-table-column prop="status" label="状态" />
      <el-table-column label="操作" >  
      <template #default="scope">
        <el-button type="primary" @click="openEdit(scope.row)">编辑</el-button>
        <el-button type="danger" @click="remove(scope.row)">删除</el-button>
      </template>
    </el-table-column>
    </el-table>

    <!-- 新增对话框 -->
    <el-dialog v-model="dialogVisible" :title="editId === null ? '新增图书副本' : '编辑图书副本'" width="500px">
      <el-form :model="form" label-width="80px" v-keyboard-nav="submitAdd">
        <el-form-item label="BOOKID">
          <el-input-number v-model="form.bookId" :min="1" />
        </el-form-item>
        <el-form-item label="条码">
          <el-input v-model="form.barcode" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">确定</el-button>
      </template>
    </el-dialog>
  </template>