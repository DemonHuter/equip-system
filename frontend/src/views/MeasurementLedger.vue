<template>
  <div class="measurement-ledger">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>计量台账管理</span>
        </div>
      </template>

      <div class="search-box">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="设备名称">
            <el-input v-model="searchForm.deviceName" placeholder="请输入设备名称" clearable />
          </el-form-item>
          <el-form-item label="设备编号">
            <el-input v-model="searchForm.deviceNo" placeholder="请输入设备编号" clearable />
          </el-form-item>
          <el-form-item label="科室">
            <el-select v-model="searchForm.department" multiple placeholder="请选择科室" clearable>
              <el-option
                v-for="option in departmentOptions"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="下次检定日期">
            <el-date-picker
              v-model="searchForm.nextInspectionDate"
              type="month"
              placeholder="选择年月"
              format="YYYY-MM"
              value-format="YYYY-MM"
              clearable
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
            <el-button type="success" @click="handleImport">导入Excel</el-button>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleAdd">新增</el-button>
            <!-- <el-button type="danger" :disabled="selectedRows.length === 0" @click="handleBatchDelete">批量删除</el-button> -->
            <el-button type="warning" @click="handleExport">导出Excel</el-button>
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
        <!-- <el-table-column type="selection" width="55" align="center" /> -->
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="deviceName" label="设备名称" min-width="120" show-overflow-tooltip align="center" />
        <el-table-column prop="specModel" label="规格型号" align="center" />
        <el-table-column prop="deviceNo" label="设备编号" align="center" />
        <el-table-column prop="inspectionUnit" label="检测检定单位" min-width="120" show-overflow-tooltip align="center" />
        <el-table-column prop="inspectionDate" label="应检日期" min-width="100" align="center" />

        <el-table-column prop="testResult" label="检测结果" min-width="100" align="center" />
        <el-table-column prop="certNo" label="证书编号" min-width="120" show-overflow-tooltip align="center" />
        <el-table-column prop="cycle" label="周期" min-width="50" align="center" />
        <el-table-column prop="nextInspectionDate" label="下次检定日期" min-width="100" align="center" />
        <el-table-column prop="remark" label="备注" min-width="100" show-overflow-tooltip align="center" />
        <el-table-column prop="department" label="科室" min-width="100" align="center" />
        <el-table-column label="判断标准" min-width="120" align="center">
          <template #default="{ row }">
            <el-tooltip :content="getStandardDetail(row.judgmentStandard)" placement="top">
              <span>{{ getStandardName(row.judgmentStandard) }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
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

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="1200px"
      append-to-body
    >
      <el-form
        :model="form"
        :rules="rules"
        ref="formRef"
        label-width="110px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="设备名称" prop="deviceName">
              <el-input v-model="form.deviceName" placeholder="请输入设备名称" />
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
            <el-form-item label="设备编号" prop="deviceNo">
              <el-input v-model="form.deviceNo" placeholder="请输入设备编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="检测检定单位" prop="inspectionUnit">
              <el-input v-model="form.inspectionUnit" placeholder="请输入检测检定单位" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="应检日期" prop="inspectionDate">
              <el-input v-model="form.inspectionDate" placeholder="请输入应检日期或'新设备'" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="检测结果" prop="testResult">
              <el-input v-model="form.testResult" placeholder="请输入检测结果" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="证书编号" prop="certNo">
              <el-input v-model="form.certNo" placeholder="请输入证书编号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="周期" prop="cycle">
              <el-input v-model="form.cycle" placeholder="请输入周期" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="下次检定日期" prop="nextInspectionDate">
              <el-input v-model="form.nextInspectionDate" placeholder="请输入下次检定日期" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="科室" prop="department">
              <el-input v-model="form.department" placeholder="请输入科室" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="判断标准" prop="judgmentStandard">
              <el-select v-model="form.judgmentStandard" placeholder="请选择判断标准">
                <el-option
                  v-for="item in standardOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 导入Excel对话框 -->
    <el-dialog
      v-model="importDialogVisible"
      title="导入Excel数据"
      width="400px"
      append-to-body
    >
      <el-upload
        class="upload-demo"
        action=""
        :auto-upload="false"
        :on-change="handleFileChange"
        :show-file-list="false"
        accept=".xlsx, .xls"
      >
        <el-button type="primary">选择Excel文件</el-button>
        <template #tip>
          <div class="el-upload__tip">
            请选择Excel文件，格式参考计量台账.xlsx
          </div>
        </template>
      </el-upload>
      <div v-if="selectedFile" class="file-info">
        已选择文件：{{ selectedFile.name }}
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="importDialogVisible = false">取消</el-button>
          <el-button type="primary" :disabled="!selectedFile" @click="submitImport">确定导入</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const selectedRows = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增记录')
const formRef = ref(null)
const importDialogVisible = ref(false)
const selectedFile = ref(null)

const searchForm = reactive({
  deviceName: '',
  deviceNo: '',
  department: [],
  nextInspectionDate: ''
})

// 监听科室变化
watch(() => searchForm.department, (newVal) => {
  console.log('科室变化:', newVal)
}, { deep: true })

// 科室选项
const departmentOptions = ref([])

// 判断标准选项
const standardOptions = ref([])

// 判断标准映射（用于根据ID获取名称和标准内容）
const standardMap = ref({})

// 加载科室选项
const loadDepartmentOptions = async () => {
  try {
    console.log('开始加载科室选项...')
    const response = await request.get('/ledger/departments')
    console.log('科室选项响应:', response)
    if (response && response.data) {
      departmentOptions.value = response.data.map(dept => ({
        label: dept,
        value: dept
      }))
      console.log('科室选项加载成功:', departmentOptions.value)
    } else {
      console.error('科室选项响应格式错误:', response)
      ElMessage.error('科室选项响应格式错误')
    }
  } catch (error) {
    console.error('加载科室选项失败:', error)
    console.error('错误详情:', error.response)
    ElMessage.error('加载科室选项失败')
  }
}

// 加载判断标准选项
const loadStandardOptions = async () => {
  try {
    console.log('开始加载判断标准选项...')
    const response = await request.get('/standard/all')
    console.log('判断标准响应:', response)
    if (response && response.data) {
      standardOptions.value = response.data.map(standard => ({
        label: standard.name,
        value: standard.id
      }))
      // 构建映射表
      standardMap.value = {}
      response.data.forEach(standard => {
        standardMap.value[standard.id] = {
          name: standard.name,
          standard: standard.standard
        }
      })
      console.log('判断标准加载成功:', standardOptions.value)
    } else {
      console.error('判断标准响应格式错误:', response)
      ElMessage.error('判断标准响应格式错误')
    }
  } catch (error) {
    console.error('加载判断标准失败:', error)
    console.error('错误详情:', error.response)
    ElMessage.error('加载判断标准失败')
  }
}

// 根据ID获取标准名称
const getStandardName = (standardId) => {
  if (!standardId) return ''
  const standard = standardMap.value[standardId]
  return standard ? standard.name : '未配置'
}

// 根据ID获取标准详情（用于悬停提示）
const getStandardDetail = (standardId) => {
  if (!standardId) return '未配置判断标准'
  const standard = standardMap.value[standardId]
  return standard ? standard.standard : '标准不存在'
}

// 页面加载时加载选项
onMounted(() => {
  loadDepartmentOptions()
  loadStandardOptions()
  loadData()
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const form = reactive({
  id: null,
  deviceName: '',
  specModel: '',
  deviceNo: '',
  inspectionUnit: '',
  inspectionDate: '',
  testResult: '',
  certNo: '',
  cycle: '',
  nextInspectionDate: '',
  remark: '',
  department: '',
  judgmentStandard: ''
})

const rules = {
  deviceName: [
    { required: true, message: '请输入设备名称', trigger: 'blur' }
  ],
  deviceNo: [
    { required: true, message: '请输入设备编号', trigger: 'blur' }
  ]
}

const loadData = async () => {
  loading.value = true
  try {
    // 打印搜索条件，以便调试
    console.log('搜索条件:', searchForm)
    
    const response = await request.post('/ledger/page', {
      current: pagination.current,
      size: pagination.size,
      deviceName: searchForm.deviceName,
      deviceNo: searchForm.deviceNo,
      department: searchForm.department,
      nextInspectionDate: searchForm.nextInspectionDate
    })
    
    console.log('响应数据:', response.data)
    tableData.value = response.data.records
    pagination.total = response.data.total
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  Object.keys(searchForm).forEach(key => {
    if (Array.isArray(searchForm[key])) {
      searchForm[key] = []
    } else {
      searchForm[key] = ''
    }
  })
  pagination.current = 1
  loadData()
}

const handleSizeChange = (size) => {
  pagination.size = size
  loadData()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  loadData()
}

const handleSelectionChange = (val) => {
  selectedRows.value = val
}

const handleAdd = () => {
  dialogTitle.value = '新增记录'
  Object.keys(form).forEach(key => {
    form[key] = null
  })
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  dialogTitle.value = '编辑记录'
  try {
    const response = await request.get(`/ledger/get/${row.id}`)
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
      await request.delete(`/ledger/delete/${id}`)
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
      await request.delete('/ledger/batchDelete', {
        data: { ids }
      })
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

const handleExport = async () => {
  try {
    console.log('开始导出...')
    const response = await request.post('/ledger/export', {
      current: 1,
      size: 99999,
      deviceName: searchForm.deviceName,
      deviceNo: searchForm.deviceNo,
      department: searchForm.department,
      nextInspectionDate: searchForm.nextInspectionDate
    }, {
      responseType: 'blob'
    })
    
    console.log('导出响应:', response)
    console.log('响应数据类型:', typeof response)
    
    // 创建下载链接
    const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = '计量台账.xlsx'
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    if (error.response) {
      console.error('响应状态:', error.response.status)
      console.error('响应数据:', error.response.data)
    }
    ElMessage.error('导出失败')
  }
}

const handleImport = () => {
  importDialogVisible.value = true
  selectedFile.value = null
}

const handleFileChange = (file) => {
  selectedFile.value = file.raw
}

const submitImport = async () => {
  if (!selectedFile.value) {
    ElMessage.warning('请选择Excel文件')
    return
  }

  const formData = new FormData()
  formData.append('file', selectedFile.value)

  try {
    await request.post('/ledger/import', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    ElMessage.success('导入成功')
    importDialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('导入失败: ' + (error.response?.data?.message || error.message))
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          await request.put('/ledger/update', form)
          ElMessage.success('更新成功')
        } else {
          await request.post('/ledger/add', form)
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

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.measurement-ledger {
  width: 100%;
}

.box-card {
  border-radius: 4px;
}
:deep(.el-card > .el-card__body) {
  padding:0px 0px 15px 0px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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
  width: 200px;
}

.search-box .el-date-picker {
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

.dialog-footer {
  text-align: right;
}

:deep(.el-table) {
  border-radius: 4px;
}

:deep(.el-table th) {
  background-color: #fafafa !important;
}

:deep(.el-button--text) {
  padding: 0;
  margin: 0 5px;
}
</style>