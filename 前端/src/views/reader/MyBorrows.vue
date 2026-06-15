<template>
  <div class="my-borrows">
    <el-card>
      <template #header>
        <h3>我的借阅记录</h3>
      </template>

      <el-table :data="borrowRecords" v-loading="loading" stripe>
        <el-table-column prop="book.isbn" label="ISBN" width="150"></el-table-column>
        <el-table-column prop="book.title" label="书名" width="200"></el-table-column>
        <el-table-column prop="book.author" label="作者" width="150"></el-table-column>
        <el-table-column prop="borrowTime" label="借阅时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.borrowTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="expectedReturnTime" label="应还时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.expectedReturnTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="returnTime" label="归还时间" width="180">
          <template #default="{ row }">
            {{ row.returnTime ? formatDate(row.returnTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'BORROWED'" type="success">借阅中</el-tag>
            <el-tag v-else-if="row.status === 'RETURNED'" type="info">已归还</el-tag>
            <el-tag v-else type="danger">逾期</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'BORROWED'"
              type="danger"
              size="small"
              @click="handleReturn(row)"
            >
              归还
            </el-button>
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
import { getBorrowRecordsByReader } from '../../api/borrow'
import { returnBook } from '../../api/borrow'

export default {
  name: 'MyBorrows',
  setup() {
    const borrowRecords = ref([])
    const loading = ref(false)

    const formatDate = (dateStr) => {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      return date.toLocaleString('zh-CN')
    }

    const loadBorrowRecords = async () => {
      loading.value = true
      try {
        const userInfo = JSON.parse(localStorage.getItem('userInfo'))
        const res = await getBorrowRecordsByReader(userInfo.id)
        if (res.code === 200) {
          borrowRecords.value = res.data
        }
      } catch (error) {
        ElMessage.error('加载借阅记录失败')
      } finally {
        loading.value = false
      }
    }

    const handleReturn = async (record) => {
      try {
        await ElMessageBox.confirm(
          `确定要归还《${record.book.title}》吗？`,
          '确认归还',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )

        const res = await returnBook(record.id)
        if (res.code === 200) {
          ElMessage.success('归还成功')
          loadBorrowRecords()
        } else {
          ElMessage.error(res.message || '归还失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error(error.response?.data?.message || '归还失败')
        }
      }
    }

    onMounted(() => {
      loadBorrowRecords()
    })

    return {
      borrowRecords,
      loading,
      formatDate,
      handleReturn
    }
  }
}
</script>

<style scoped>
.my-borrows {
  width: 100%;
}
</style>

