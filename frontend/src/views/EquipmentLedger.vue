<template>
  <div class="equipment-ledger">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>设备台账管理</span>
        </div>
      </template>
      <div class="search-box">
        <el-form :model="searchForm" class="search-form" inline>
          <el-form-item label="设备名称">
            <el-input
              v-model="searchForm.equipmentName"
              placeholder="请输入设备名称"
              class="search-input"
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item label="设备编号">
            <el-input
              v-model="searchForm.deviceNo"
              placeholder="请输入设备编号"
              class="search-input"
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item label="科室">
            <el-select v-model="searchForm.department" placeholder="请选择科室" clearable>
              <el-option
                v-for="dept in departmentOptions"
                :key="dept"
                :label="dept"
                :value="dept"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="使用状态">
            <el-select v-model="searchForm.useStatus" placeholder="请选择使用状态">
              <el-option label="全部" value="" />
              <el-option
                v-for="status in useStatusOptions"
                :key="status"
                :label="status"
                :value="status"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
            <el-button type="primary" @click="openAddDialog">新增</el-button>
            <!-- <el-button type="danger" @click="handleBatchDelete" :disabled="selectedRows.length === 0">批量删除</el-button> -->
            <el-button type="success" @click="triggerFileInput">导入Excel</el-button>
            <el-button type="warning" @click="handleExport">导出Excel</el-button>
            <input
              ref="fileInputRef"
              type="file"
              accept=".xlsx, .xls"
              @change="handleImport"
              style="display: none;"
            />
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
        <el-table-column prop="equipmentName" label="计量器具名称" min-width="150" show-overflow-tooltip align="center" />
        <el-table-column prop="specModel" label="规格型号" min-width="120" show-overflow-tooltip align="center" />
        <el-table-column prop="serialNumber" label="序列号" min-width="120" show-overflow-tooltip align="center" />
        <el-table-column prop="deviceNo" label="设备编号" min-width="120" show-overflow-tooltip align="center" />
        <el-table-column prop="manufacturer" label="制造厂家" min-width="120" show-overflow-tooltip align="center" />
        <el-table-column prop="enableDate" label="启用日期" min-width="100" align="center" />
        <el-table-column prop="expectedScrapDate" label="预计报废日期" min-width="120" align="center" />
        <el-table-column prop="installationLocation" label="安装位置" min-width="120" show-overflow-tooltip align="center" />
        <el-table-column prop="keeper" label="保管人" min-width="100" align="center" />
        <el-table-column prop="maintenanceStatus" label="设备维修/维保" min-width="120" show-overflow-tooltip align="center" />
        <el-table-column prop="useStatus" label="使用状态" min-width="100" align="center" />
        <el-table-column prop="department" label="科室" min-width="100" align="center" />
        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="handleEdit(row)">编辑</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="1200px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="110px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="计量器具名称" prop="equipmentName">
              <el-input v-model="form.equipmentName" placeholder="请输入计量器具名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规格型号" prop="specModel">
              <el-input v-model="form.specModel" placeholder="请输入规格型号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="序列号" prop="serialNumber">
              <el-input v-model="form.serialNumber" placeholder="请输入序列号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备编号" prop="deviceNo">
              <el-input v-model="form.deviceNo" placeholder="请输入设备编号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="制造厂家" prop="manufacturer">
              <el-input v-model="form.manufacturer" placeholder="请输入制造厂家" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="启用日期" prop="enableDate">
              <el-input v-model="form.enableDate" placeholder="请输入启用日期" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="预计报废日期" prop="expectedScrapDate">
              <el-input v-model="form.expectedScrapDate" placeholder="请输入预计报废日期" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="安装位置" prop="installationLocation">
              <el-input v-model="form.installationLocation" placeholder="请输入安装位置" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="保管人" prop="keeper">
              <el-input v-model="form.keeper" placeholder="请输入保管人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备维修/维保" prop="maintenanceStatus">
              <el-input v-model="form.maintenanceStatus" placeholder="请输入设备维修/维保" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="科室" prop="department">
              <el-input v-model="form.department" placeholder="请输入科室" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="使用状态" prop="useStatus">
              <el-select v-model="form.useStatus" placeholder="请选择使用状态">
                <el-option
                  v-for="status in useStatusOptions"
                  :key="status"
                  :label="status"
                  :value="status"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </div>
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
const selectedRows = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const fileInputRef = ref(null)

const searchForm = reactive({
  equipmentName: '',
  deviceNo: '',
  department: '',
  useStatus: ''
})

const departmentOptions = ref([])
const useStatusOptions = ref([])

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const form = reactive({
  id: null,
  equipmentName: '',
  specModel: '',
  serialNumber: '',
  deviceNo: '',
  manufacturer: '',
  enableDate: '',
  expectedScrapDate: '',
  installationLocation: '',
  keeper: '',
  maintenanceStatus: '',
  department: '',
  useStatus: ''
})

const rules = {
  equipmentName: [
    { required: true, message: '请输入计量器具名称', trigger: 'blur' }
  ],
  deviceNo: [
    { required: true, message: '请输入设备编号', trigger: 'blur' }
  ]
}

const loadData = async () => {
  loading.value = true
  try {
    const response = await request.post('/equipment/page', {
      current: pagination.current,
      size: pagination.size,
      deviceName: searchForm.equipmentName,
      deviceNo: searchForm.deviceNo,
      department: searchForm.department,
      nextInspectionDate: searchForm.useStatus
    })
    tableData.value = response.data.records
    pagination.total = response.data.total
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const loadDepartmentOptions = async () => {
  try {
    const response = await request.get('/equipment/departments')
    if (response && response.data) {
      departmentOptions.value = response.data
    }
  } catch (error) {
    console.error('加载科室选项失败:', error)
  }
}

const loadUseStatusOptions = async () => {
  try {
    const response = await request.get('/equipment/status-list')
    if (response && response.data) {
      useStatusOptions.value = response.data
    }
  } catch (error) {
    console.error('加载使用状态选项失败:', error)
    useStatusOptions.value = ['在用', '停用', '报废', '维修中']
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.equipmentName = ''
  searchForm.deviceNo = ''
  searchForm.department = ''
  searchForm.useStatus = ''
  pagination.current = 1
  loadData()
}

const openAddDialog = () => {
  form.id = null
  form.equipmentName = ''
  form.specModel = ''
  form.serialNumber = ''
  form.deviceNo = ''
  form.manufacturer = ''
  form.enableDate = ''
  form.expectedScrapDate = ''
  form.installationLocation = ''
  form.keeper = ''
  form.maintenanceStatus = ''
  form.department = ''
  form.useStatus = ''
  dialogTitle.value = '新增设备台账'
  nextTick(() => {
    dialogVisible.value = true
  })
}

const handleEdit = async (row) => {
  dialogTitle.value = '编辑设备台账'
  try {
    const response = await request.get(`/equipment/get/${row.id}`)
    Object.assign(form, response.data)
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取数据失败')
  }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/equipment/delete/${id}`)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

const handleBatchDelete = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的记录')
    return
  }
  ElMessageBox.confirm('确定要删除选中的记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const ids = selectedRows.value.map(row => row.id)
      await request.delete('/equipment/batchDelete', {
        data: { ids }
      })
      ElMessage.success('删除成功')
      selectedRows.value = []
      loadData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          await request.put('/equipment/update', form)
          ElMessage.success('更新成功')
        } else {
          await request.post('/equipment/add', form)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }
  })
}

const triggerFileInput = () => {
  fileInputRef.value.click()
}

const handleImport = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  const isExcel = file.name.endsWith('.xlsx') || file.name.endsWith('.xls')
  if (!isExcel) {
    ElMessage.error('请选择Excel文件')
    event.target.value = ''
    return
  }

  const formData = new FormData()
  formData.append('file', file)

  try {
    await request.post('/equipment/import', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    ElMessage.success('导入成功')
    loadData()
    loadDepartmentOptions()
  } catch (error) {
    ElMessage.error('导入失败')
  }

  event.target.value = ''
}

const handleExport = async () => {
  try {
    const params = {}
    if (searchForm.equipmentName) params.deviceName = searchForm.equipmentName
    if (searchForm.deviceNo) params.deviceNo = searchForm.deviceNo
    if (searchForm.department) params.department = [searchForm.department]
    if (searchForm.useStatus && searchForm.useStatus !== '') params.nextInspectionDate = searchForm.useStatus
    
    const response = await request.post('/equipment/export', params, {
      responseType: 'blob'
    })
    
    if (!response || response.size === 0) {
      ElMessage.error('导出数据为空')
      return
    }
    
    const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = '设备台账.xlsx'
    document.body.appendChild(a)
    a.click()
    window.URL.revokeObjectURL(url)
    document.body.removeChild(a)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

const handleSelectionChange = (val) => {
  selectedRows.value = val
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
  loadDepartmentOptions()
  loadUseStatusOptions()
  loadData()
})
</script>

<style scoped>
.equipment-ledger {
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

.search-box .el-select {
  width: 150px;
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

.dialog-footer {
  text-align: right;
}
</style>