<template>
  <div class="device-standard">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>仪器设备标准管理</span>
        </div>
      </template>
      <div class="search-box">
        <el-form :model="searchForm" class="search-form" inline>
          <el-form-item label="标准内容">
            <el-input
              v-model="searchForm.standard"
              placeholder="请输入标准内容"
              class="search-input"
              @keyup.enter="handleSearch"
            >
              <template #append>
                <el-button @click="handleSearch">搜索</el-button>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="openAddDialog">新增</el-button>
            <el-button type="danger" @click="handleBatchDelete" :disabled="selectedIds.length === 0">批量删除</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
        border
        stripe
        align="center"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="name" label="名称" min-width="150" show-overflow-tooltip align="center" />
        <el-table-column prop="standard" label="标准内容" min-width="400" show-overflow-tooltip align="center" />
        <el-table-column prop="createTime" label="创建时间" min-width="180" align="center" />
        <el-table-column prop="updateTime" label="更新时间" min-width="180" align="center" />
        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="openEditDialog(row)">编辑</el-button>
            <el-button type="text" size="small" style="color: #f56c6c;" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="showDialog" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input
            v-model="form.name"
            placeholder="请输入名称"
          />
        </el-form-item>
        <el-form-item label="标准内容" prop="standard">
          <el-input
            v-model="form.standard"
            type="textarea"
            :rows="4"
            placeholder="请输入标准内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="closeDialog">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, nextTick } from 'vue'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const selectedIds = ref([])
const showDialog = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const searchForm = reactive({
  standard: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const form = reactive({
  id: null,
  name: '',
  standard: ''
})

const rules = {
  name: [
    { required: true, message: '请输入名称', trigger: 'blur' }
  ],
  standard: [
    { required: true, message: '请输入标准内容', trigger: 'blur' }
  ]
}

const loadData = () => {
  loading.value = true
  request.get('/standard/list', {
    params: {
      current: pagination.current,
      size: pagination.size,
      standard: searchForm.standard
    }
  }).then(res => {
    tableData.value = res.data.records
    pagination.total = res.data.total
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.standard = ''
  pagination.current = 1
  loadData()
}

const openAddDialog = () => {
  form.id = null
  form.name = ''
  form.standard = ''
  dialogTitle.value = '新增仪器设备标准'
  nextTick(() => {
    showDialog.value = true
  })
}

const openEditDialog = (row) => {
  form.id = row.id
  form.name = row.name
  form.standard = row.standard
  dialogTitle.value = '编辑仪器设备标准'
  nextTick(() => {
    showDialog.value = true
  })
}

const closeDialog = () => {
  showDialog.value = false
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    request.delete(`/standard/delete/${id}`).then(() => {
      ElMessage.success('删除成功')
      loadData()
    }).catch(() => {
      ElMessage.error('删除失败')
    })
  })
}

const handleBatchDelete = () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要删除的记录')
    return
  }
  ElMessageBox.confirm('确定要删除选中的记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    request.post('/standard/batchDelete', { ids: selectedIds.value }).then(() => {
      ElMessage.success('删除成功')
      selectedIds.value = []
      loadData()
    }).catch(() => {
      ElMessage.error('删除失败')
    })
  })
}

const handleSubmit = () => {
  if (!formRef.value) return
  formRef.value.validate((valid) => {
    if (valid) {
      if (form.id) {
        request.put('/standard/update', form).then(() => {
          ElMessage.success('更新成功')
          showDialog.value = false
          loadData()
        }).catch(() => {
          ElMessage.error('更新失败')
        })
      } else {
        request.post('/standard/add', form).then(() => {
          ElMessage.success('新增成功')
          showDialog.value = false
          loadData()
        }).catch(() => {
          ElMessage.error('新增失败')
        })
      }
    }
  })
}

const handleSelectionChange = (val) => {
  selectedIds.value = val.map(item => item.id)
}

const handleSizeChange = (val) => {
  pagination.size = val
  loadData()
}

const handleCurrentChange = (val) => {
  pagination.current = val
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.device-standard {
  width: 100%;
}

.box-card {
  border-radius: 4px;
}

:deep(.el-card > .el-card__body) {
  padding: 0px 0px 15px 0px;
}

.search-box {
  margin-bottom: 15px;
  padding: 15px;
  background-color: #fafafa;
  border-radius: 4px;
}

.search-box .el-input {
  width: 200px;
}

.search-form {
  margin-bottom: 0;
  width: 100%;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>