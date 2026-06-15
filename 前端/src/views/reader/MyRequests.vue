<template>
  <div class="my-requests">
    <el-card>
      <template #header>
        <h3>我的借阅申请</h3>
      </template>

      <el-table :data="requests" v-loading="loading" stripe>
        <el-table-column prop="book.isbn" label="ISBN" width="150"></el-table-column>
        <el-table-column prop="book.title" label="书名" width="200"></el-table-column>
        <el-table-column prop="book.author" label="作者" width="150"></el-table-column>
        <el-table-column prop="expectedBorrowDate" label="预计借阅日期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.expectedBorrowDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="expectedReturnDate" label="预计归还日期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.expectedReturnDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="borrowDays" label="借阅天数" width="100" align="center"></el-table-column>
        <el-table-column prop="requestTime" label="申请时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.requestTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'PENDING'" type="warning">待审批</el-tag>
            <el-tag v-else-if="row.status === 'APPROVED'" type="success">已批准</el-tag>
            <el-tag v-else-if="row.status === 'REJECTED'" type="danger">已拒绝</el-tag>
            <el-tag v-else type="info">已取消</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'PENDING'"
              type="danger"
              size="small"
              @click="handleCancel(row)"
            >
              取消申请
            </el-button>
            <span v-else-if="row.status === 'REJECTED' && row.rejectReason" :title="row.rejectReason">
              {{ row.rejectReason }}
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRequestsByReader, cancelRequest } from '../../api/borrowRequest'

export default {
  name: 'MyRequests',
  setup() {
    const requests = ref([])
    const loading = ref(false)

    const formatDate = (dateStr) => {
      if (!dateStr) return '-'
      return dateStr
    }

    const formatDateTime = (dateStr) => {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      return date.toLocaleString('zh-CN')
    }

    const loadRequests = async () => {
      loading.value = true
      try {
        const userInfo = JSON.parse(localStorage.getItem('userInfo'))
        const res = await getRequestsByReader(userInfo.id)
        if (res.code === 200) {
          requests.value = res.data
        }
      } catch (error) {
        ElMessage.error('加载申请列表失败')
      } finally {
        loading.value = false
      }
    }

    const handleCancel = async (request) => {
      try {
        await ElMessageBox.confirm(
          `确定要取消《${request.book.title}》的借阅申请吗？`,
          '确认取消',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )

        const userInfo = JSON.parse(localStorage.getItem('userInfo'))
        const res = await cancelRequest(request.id, userInfo.id)
        if (res.code === 200) {
          ElMessage.success('申请已取消')
          loadRequests()
        } else {
          ElMessage.error(res.message || '取消失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error(error.response?.data?.message || '取消失败')
        }
      }
    }

    onMounted(() => {
      loadRequests()
    })

    return {
      requests,
      loading,
      formatDate,
      formatDateTime,
      handleCancel
    }
  }
}
</script>

<style scoped>
.my-requests {
  width: 100%;
}
</style>


