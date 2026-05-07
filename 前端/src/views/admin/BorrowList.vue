<template>
  <div class="borrow-list">
    <el-card>
      <template #header>
        <h3>借阅管理</h3>
      </template>

      <el-table :data="borrowRecords" v-loading="loading" stripe>
        <el-table-column prop="reader.readerId" label="读者ID" width="120"></el-table-column>
        <el-table-column prop="reader.name" label="读者姓名" width="120"></el-table-column>
        <el-table-column prop="book.isbn" label="ISBN" width="150"></el-table-column>
        <el-table-column prop="book.title" label="书名" width="200"></el-table-column>
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
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAllBorrowRecords } from '../../api/borrow'

export default {
  name: 'AdminBorrowList',
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
        const res = await getAllBorrowRecords()
        if (res.code === 200) {
          borrowRecords.value = res.data
        }
      } catch (error) {
        ElMessage.error('加载借阅记录失败')
      } finally {
        loading.value = false
      }
    }

    onMounted(() => {
      loadBorrowRecords()
    })

    return {
      borrowRecords,
      loading,
      formatDate
    }
  }
}
</script>

<style scoped>
.borrow-list {
  width: 100%;
}
</style>

