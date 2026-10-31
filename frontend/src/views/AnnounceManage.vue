<script setup lang="ts">
  import { ref, onMounted } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { listAnnouncements, addAnnouncement, deleteAnnouncement } from '@/services/announcement'
  import type { Announcement } from '@/models/announcement'
  import { useAuthStore } from '@/stores/auth'

  const authStore = useAuthStore()

  // 当前激活的 tab：'0'=公告，'1'=申诉（el-tabs 的 name 用字符串）
  const activeType = ref('0')
  const list = ref<Announcement[]>([])
  const dialogVisible = ref(false)
  const form = ref({ title: '', content: '' })

  async function load() {
    list.value = await listAnnouncements(Number(activeType.value))
  }

  function openAdd() {
    form.value = { title: '', content: '' }
    dialogVisible.value = true
  }

  async function submitAdd() {
    await addAnnouncement({ title: form.value.title, content: form.value.content, type: Number(activeType.value) })
    ElMessage.success('发布成功')
    dialogVisible.value = false
    await load()
  }

  async function remove(item: Announcement) {
    try {
      await ElMessageBox.confirm('确认删除这条通告吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
      await deleteAnnouncement(item.id)
      ElMessage.success('删除成功')
      await load()
    } catch (error) {
      // 用户点了取消，什么都不做
    }
  }

  onMounted(load)
  </script>



  <template>
    <el-tabs v-model="activeType" @tab-change="load">
      <el-tab-pane label="公告" name="0">
        <el-button type="primary" @click="openAdd" v-if="authStore.role >= 1">发布公告</el-button>
      </el-tab-pane>
      <el-tab-pane label="申诉" name="1">
        <el-button type="primary" @click="openAdd" v-if="authStore.role === 0">发布申诉</el-button>
      </el-tab-pane>
    </el-tabs>
    <el-table :data="list">
      <el-table-column prop="title" label="标题"/>
      <el-table-column prop="content" label="内容" show-overflow-tooltip/>
      <el-table-column prop="publisherName" label="发布人"/>
        <el-table-column prop="publisherId" label="发布人ID"/>

      <el-table-column prop="publishTime" label="发布时间"/>
      <el-table-column label="操作" #default="scope">
        <el-button type="danger" @click="remove(scope.row)" v-if="authStore.role >= 1">删除</el-button>
      </el-table-column>
    </el-table>
    <el-dialog v-model="dialogVisible" title="发布公告">
      <el-form :model="form" v-keyboard-nav="submitAdd">
        <el-form-item label="标题">
          <el-input v-model="form.title"/>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea"/>
        </el-form-item>
      </el-form>
        <template #footer>
          <el-button @click="dialogVisible=false">取消</el-button>
          <el-button type="primary" @click="submitAdd" >发布</el-button>
        </template>
    </el-dialog>
  </template>