<template>
  <div class="request-approval">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>借阅申请审批</h3>
          <el-button type="primary" @click="loadPendingRequests">刷新</el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="待审批" name="pending">
          <el-table :data="pendingRequests" v-loading="loading" stripe>
            <el-table-column prop="reader.readerId" label="读者ID" width="120"></el-table-column>
            <el-table-column prop="reader.name" label="读者姓名" width="120"></el-table-column>
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
            <el-table-column label="操作" width="200" align="center">
              <template #default="{ row }">
                <el-button
                  type="success"
                  size="small"
                  @click="handleApprove(row)"
                >
                  批准
                </el-button>
                <el-button
                  type="danger"
                  size="small"
                  @click="handleReject(row)"
                >
                  拒绝
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="全部申请" name="all">
          <el-table :data="allRequests" v-loading="loading" stripe>
            <el-table-column prop="reader.readerId" label="读者ID" width="120"></el-table-column>
            <el-table-column prop="reader.name" label="读者姓名" width="120"></el-table-column>
            <el-table-column prop="book.title" label="书名" width="200"></el-table-column>
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
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag v-if="row.status === 'PENDING'" type="warning">待审批</el-tag>
                <el-tag v-else-if="row.status === 'APPROVED'" type="success">已批准</el-tag>
                <el-tag v-else-if="row.status === 'REJECTED'" type="danger">已拒绝</el-tag>
                <el-tag v-else type="info">已取消</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="processTime" label="处理时间" width="180">
              <template #default="{ row }">
                {{ row.processTime ? formatDateTime(row.processTime) : '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="rejectReason" label="拒绝原因" width="200">
              <template #default="{ row }">
                {{ row.rejectReason || '-' }}
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 拒绝原因对话框 -->
    <el-dialog
      v-model="rejectDialogVisible"
      title="拒绝申请"
      width="500px"
    >
      <el-form>
        <el-form-item label="拒绝原因">
          <el-input
            v-model="rejectReason"
            type="textarea"
            :rows="4"
            placeholder="请输入拒绝原因"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmReject">确定拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPendingRequests, getAllRequests, approveRequest, rejectRequest } from '../../api/borrowRequest'

export default {
  name: 'RequestApproval',
  setup() {
    const activeTab = ref('pending')
    const pendingRequests = ref([])
    const allRequests = ref([])
    const loading = ref(false)
    const rejectDialogVisible = ref(false)
    const rejectReason = ref('')
    const currentRejectRequest = ref(null)

    const formatDate = (dateStr) => {
      if (!dateStr) return '-'
      return dateStr
    }

    const formatDateTime = (dateStr) => {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      return date.toLocaleString('zh-CN')
    }

    const loadPendingRequests = async () => {
      loading.value = true
      try {
        const res = await getPendingRequests()
        if (res.code === 200) {
          pendingRequests.value = res.data
        }
      } catch (error) {
        ElMessage.error('加载待审批申请失败')
      } finally {
        loading.value = false
      }
    }

    const loadAllRequests = async () => {
      loading.value = true
      try {
        const res = await getAllRequests()
        if (res.code === 200) {
          allRequests.value = res.data
        }
      } catch (error) {
        ElMessage.error('加载申请列表失败')
      } finally {
        loading.value = false
      }
    }

    const handleApprove = async (request) => {
      try {
        await ElMessageBox.confirm(
          `确定要批准读者 ${request.reader.name} 借阅《${request.book.title}》的申请吗？`,
          '确认批准',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'success'
          }
        )

        const adminInfo = JSON.parse(localStorage.getItem('userInfo'))
        const res = await approveRequest(request.id, adminInfo.id)
        if (res.code === 200) {
          ElMessage.success('申请已批准')
          loadPendingRequests()
          loadAllRequests()
        } else {
          ElMessage.error(res.message || '批准失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error(error.response?.data?.message || '批准失败')
        }
      }
    }

    const handleReject = (request) => {
      currentRejectRequest.value = request
      rejectReason.value = ''
      rejectDialogVisible.value = true
    }

    const confirmReject = async () => {
      if (!rejectReason.value.trim()) {
        ElMessage.warning('请输入拒绝原因')
        return
      }

      try {
        const adminInfo = JSON.parse(localStorage.getItem('userInfo'))
        const res = await rejectRequest(
          currentRejectRequest.value.id,
          adminInfo.id,
          rejectReason.value
        )
        if (res.code === 200) {
          ElMessage.success('申请已拒绝')
          rejectDialogVisible.value = false
          loadPendingRequests()
          loadAllRequests()
        } else {
          ElMessage.error(res.message || '拒绝失败')
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '拒绝失败')
      }
    }

    onMounted(() => {
      loadPendingRequests()
      loadAllRequests()
    })

    return {
      activeTab,
      pendingRequests,
      allRequests,
      loading,
      rejectDialogVisible,
      rejectReason,
      formatDate,
      formatDateTime,
      loadPendingRequests,
      handleApprove,
      handleReject,
      confirmReject
    }
  }
}
</script>

<style scoped>
.request-approval {
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


