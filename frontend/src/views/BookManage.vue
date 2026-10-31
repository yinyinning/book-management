<script setup lang="ts">
    import { ref, onMounted } from 'vue'
    import { ElMessage,ElMessageBox } from 'element-plus'
    import { listBooks, addBook,updateBook,deleteBook, getAllTypes, addBookTypes, resetBookTypes } from '@/services/book'
    import { type BookType, type Book } from '@/models/book'
    import { Plus } from '@element-plus/icons-vue'
    import { addBookCopy } from '@/services/bookCopy'

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
      allTypes.value=await getAllTypes()
    }

    const typeDialogVisible=ref(false)
    const resetTypeDialogVisible=ref(false)
    const addCopyDialogVisible=ref(false)
    const allTypes=ref<BookType[]>([])
    const selectedTypeIds=ref<number[]>([])
    const currentBook=ref<Book | null>(null)
    const barcode=ref('')

    // 打开新增对话框：先清空表单，再显示
    function openAdd() {
      form.value = { isbn: '', title: '', author: '', publisher: '' }
      dialogVisible.value = true
      editId.value = null
      currentBook.value=null
    }

    function openEdit(book:Book) {
      form.value = { isbn: book.isbn, title: book.title, author: book.author, publisher: book.publisher }
      dialogVisible.value = true
      editId.value = book.id
      currentBook.value=book
    }

    async function openTypeDialog(book: Book) {
      currentBook.value=book
      selectedTypeIds.value=[]
      const all=await getAllTypes()
      const existingIds=book.types.map(t=>t.id)
      allTypes.value=all.filter(t=>!existingIds.includes(t.id))
      typeDialogVisible.value=true
    }

    async function openResetTypeDialog() {
      selectedTypeIds.value=[]
      allTypes.value=await getAllTypes()
      resetTypeDialogVisible.value=true
    }

    function openAddCopy(book: Book) {
      barcode.value=''
      currentBook.value=book
      addCopyDialogVisible.value=true
    }

    async function submitAddTypes() {
      if(!currentBook.value || selectedTypeIds.value.length===0){
        return
      }
      await addBookTypes(currentBook.value.id,selectedTypeIds.value)
      ElMessage.success('增加分类成功')
      typeDialogVisible.value=false
    }

    async function submitResetTypes() {
      if(!currentBook.value || selectedTypeIds.value.length===0){
        return
      }
      await resetBookTypes(currentBook.value.id,selectedTypeIds.value)
      ElMessage.success('重置分类成功')
      resetTypeDialogVisible.value=false
    }

    async function submitAddCopy() {
      if(!barcode.value.trim) return
      await addBookCopy({ bookId: currentBook.value!.id, barcode: barcode.value})
      ElMessage.success('新增副本成功')
      barcode.value=''
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
      <el-table-column label="操作" width="280">  
        <template #default="scope">
          <el-space>
            <el-button @click="openTypeDialog(scope.row)">增加分类</el-button>
            <el-button type="primary" @click="openEdit(scope.row)">编辑</el-button>
            <el-button type="danger" @click="remove(scope.row)">删除</el-button>
            <el-button :icon="Plus" circle title="新增副本" @click="openAddCopy(scope.row)" />
          </el-space>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增对话框 -->
    <el-dialog v-model="dialogVisible" :title="editId === null ? '新增图书' : '编辑图书'" width="500px">
      <el-form :model="form" label-width="80px" v-keyboard-nav="submitAdd">
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
        <el-form-item v-if="editId!==null">
          <el-button type="warning" @click="openResetTypeDialog">重置分类</el-button>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">确定</el-button>
      </template>
    </el-dialog>
    <el-dialog v-model="typeDialogVisible" title="新增分类">
      <div class="type-options">
        <el-select v-model="selectedTypeIds" multiple :placeholder='allTypes.length===0? "已拥有全部分类" : "请选择分类"' >
          <el-option v-for="t in allTypes" :key="t.id" :label="t.name" :value="t.id" />
        </el-select>
      </div>
      <template #footer>
        <el-button @click="typeDialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submitAddTypes">确定</el-button>
      </template>
    </el-dialog>
    <el-dialog v-model="resetTypeDialogVisible" title="重置分类">
      <div class="type-options">
        <el-select v-model="selectedTypeIds" multiple placeholder="请选择分类">
          <el-option v-for="t in allTypes" :key="t.id" :label="t.name" :value="t.id" />
        </el-select>
      </div>
      <template #footer>
        <el-button @click="resetTypeDialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submitResetTypes">确定</el-button>
      </template>
    </el-dialog>
    <el-dialog v-model="addCopyDialogVisible" title="新增副本">
      <el-form v-keyboard-nav="submitAddCopy">
        <el-form-item label="图书">
          <el-input :model-value="currentBook?.title" disabled />
        </el-form-item>
        <el-form-item label="馆藏编码">
          <el-input v-model="barcode" placeholder="请输入馆藏编码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addCopyDialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submitAddCopy">确认</el-button>
      </template>
    </el-dialog>
  </template>