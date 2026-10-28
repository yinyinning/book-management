<template>
    <div class="typemanage-container">
        <div class="button-container">
            <el-button @click="addType">增加分类</el-button>
        </div>
        <div class="table-container">
            <el-table :data="typeList" @row-contextmenu="onRowMenu">
                <el-table-column prop="id" label="类型id" />
                <el-table-column prop="name" label="类型名称" />
                <el-table-column label="操作">
                    <template #default="scope">
                        <el-button type="primary" @click="updateType(scope.row)">更改名称</el-button>
                        <el-button type="danger" @click="deleteType(scope.row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <el-dialog title="新增分类" v-model="addDialogVisible">
            <el-form v-keyboard-nav="onSubmitAdd">
                <el-form-item>
                    <el-input v-model="typeName" placeholder="类型名称" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button type="primary" @click="onSubmitAdd">确认</el-button>
                <el-button @click="addDialogVisible=false">取消</el-button>
            </template>
        </el-dialog>
        <el-dialog title="更新类型名称" v-model="updateDialogVisible">
            <el-form v-keyboard-nav="onSubmitUpdate">
                <el-form-item>
                    <p>id:</p>
                    <el-input v-model="updateId" :disabled="true"></el-input>
                </el-form-item>
                <el-form-item>
                    <p>名称:</p>
                    <el-input v-model="updateName" placeholder="请输入名称" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button type="primary" @click="onSubmitUpdate" :disabled="updateName===updateOldName || updateName===''">保存</el-button>
                <el-button @click="updateDialogVisible=false">取消</el-button>
            </template>
        </el-dialog>
        <div class="row-menu" v-show="menuVisible" :style="{ left: menuX+'px', top: menuY+'px' }">
            <div class="menu-item" @click="handleMenu('update')">更改名称</div>
            <div class="menu-item" @click="handleMenu('delete')">删除</div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import type { BookType } from '@/models/book';
import { addBookType, deleteBookType, getAllTypes, updateBookType } from '@/services/book';
import { ElMessage, ElMessageBox } from 'element-plus';
import { onMounted, onUnmounted, ref } from 'vue';

const addDialogVisible=ref(false)
const updateDialogVisible=ref(false)
const typeName=ref('')
const updateId=ref(0)
const updateOldName=ref('')
const updateName=ref('')
const menuVisible=ref(false)
const menuX=ref(0)
const menuY=ref(0)
const typeList=ref<BookType[]>([])
const currentRow=ref<BookType | null>(null)

function addType(){
    addDialogVisible.value=true
}
function updateType(type: BookType){
    updateId.value=type.id
    updateName.value=type.name
    updateOldName.value=type.name
    updateDialogVisible.value=true
}
function handleMenu(action: string) {
    menuVisible.value=false
    if(!currentRow.value) return
    if(action==='delete') deleteType(currentRow.value)
    else if(action==='update') updateType(currentRow.value)
}
function onRowMenu(row: BookType, _column: any, event: MouseEvent) {
    event.preventDefault()
    currentRow.value=row
    menuX.value=event.clientX
    menuY.value=event.clientY
    menuVisible.value=true
}
function closeMenu(){
    menuVisible.value=false
}

async function fetch() {
    typeList.value=await getAllTypes()
}
async function onSubmitAdd(){
    await addBookType(typeName.value)
    ElMessage.success('新增类型成功')
    typeName.value=''
    await fetch()
}
async function onSubmitUpdate() {
    await updateBookType(updateId.value, updateName.value)
    ElMessage.success('更改类型名称成功')
    updateId.value=0
    updateName.value=''
    updateOldName.value=''
    await fetch()
    updateDialogVisible.value=false
}
async function deleteType(type: BookType) {
    try{
        await ElMessageBox.confirm(`确定删除类型⌈${type.name}⌋吗？`,'提示',{type: 'warning'})
    }catch{
        return
    }
    await deleteBookType(type.id)
    ElMessage.success('类型删除成功')
    await fetch()
}

onMounted(()=>{
    fetch()
    document.addEventListener('click',closeMenu)
})
onUnmounted(()=>{
    document.removeEventListener('click',closeMenu)
})
</script>

<style scoped>
.row-menu {
    position: fixed;
    background-color: #fff;
    border-radius: 6px;
    padding: 4px 0;
    z-index: 1000;
}
.menu-item {
    padding: 8px 20px;
    cursor: pointer;
    font-size: 14px;
}
.menu-item:hover {
    background-color: #f5f7fa;
}
.button-container {
    margin-bottom: 10px;
}
</style>

