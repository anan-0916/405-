<template>
  <div class="reader-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>读者管理</h3>
          <el-button type="primary" @click="handleAdd">新增读者</el-button>
        </div>
      </template>

      <el-table :data="readers" v-loading="loading" stripe>
        <el-table-column prop="readerId" label="读者ID" width="150"></el-table-column>
        <el-table-column prop="name" label="姓名" width="150"></el-table-column>
        <el-table-column prop="phone" label="电话" width="150"></el-table-column>
        <el-table-column prop="email" label="邮箱" width="200"></el-table-column>
        <el-table-column prop="maxBorrowCount" label="最大借阅数" width="120" align="center"></el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="500px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="读者ID" prop="readerId">
          <el-input v-model="form.readerId" :disabled="isEdit"></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email"></el-input>
        </el-form-item>
        <el-form-item label="最大借阅数" prop="maxBorrowCount">
          <el-input-number v-model="form.maxBorrowCount" :min="1"></el-input-number>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getReaders, createReader, updateReader, deleteReader } from '../../api/reader'

export default {
  name: 'AdminReaderList',
  setup() {
    const readers = ref([])
    const loading = ref(false)
    const dialogVisible = ref(false)
    const isEdit = ref(false)
    const formRef = ref(null)

    const form = reactive({
      id: null,
      readerId: '',
      name: '',
      phone: '',
      email: '',
      maxBorrowCount: 5
    })

    const rules = {
      readerId: [{ required: true, message: '请输入读者ID', trigger: 'blur' }],
      name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
      phone: [{ required: true, message: '请输入电话', trigger: 'blur' }],
      email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
      ]
    }

    const dialogTitle = computed(() => isEdit.value ? '编辑读者' : '新增读者')

    const loadReaders = async () => {
      loading.value = true
      try {
        const res = await getReaders()
        if (res.code === 200) {
          readers.value = res.data
        }
      } catch (error) {
        ElMessage.error('加载读者列表失败')
      } finally {
        loading.value = false
      }
    }

    const handleAdd = () => {
      isEdit.value = false
      resetForm()
      dialogVisible.value = true
    }

    const handleEdit = (row) => {
      isEdit.value = true
      Object.assign(form, row)
      dialogVisible.value = true
    }

    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm(
          `确定要删除读者 ${row.name} 吗？`,
          '确认删除',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )

        const res = await deleteReader(row.id)
        if (res.code === 200) {
          ElMessage.success('删除成功')
          loadReaders()
        } else {
          ElMessage.error(res.message || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error(error.response?.data?.message || '删除失败')
        }
      }
    }

    const handleSubmit = async () => {
      await formRef.value.validate(async (valid) => {
        if (valid) {
          try {
            let res
            if (isEdit.value) {
              res = await updateReader(form.id, form)
            } else {
              res = await createReader(form)
            }
            if (res.code === 200) {
              ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
              dialogVisible.value = false
              loadReaders()
            } else {
              ElMessage.error(res.message || '操作失败')
            }
          } catch (error) {
            ElMessage.error(error.response?.data?.message || '操作失败')
          }
        }
      })
    }

    const resetForm = () => {
      Object.assign(form, {
        id: null,
        readerId: '',
        name: '',
        phone: '',
        email: '',
        maxBorrowCount: 5
      })
      formRef.value?.resetFields()
    }

    onMounted(() => {
      loadReaders()
    })

    return {
      readers,
      loading,
      dialogVisible,
      isEdit,
      form,
      rules,
      formRef,
      dialogTitle,
      handleAdd,
      handleEdit,
      handleDelete,
      handleSubmit,
      resetForm
    }
  }
}
</script>

<style scoped>
.reader-list {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
}
</style>

