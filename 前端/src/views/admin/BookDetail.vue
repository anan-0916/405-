<template>
  <div class="book-detail" v-loading="loading">
    <el-card v-if="book">
      <div class="detail-header">
        <div class="cover">
          <img :src="getImage(book)" :alt="book.title" />
        </div>
        <div class="meta">
          <h2>{{ book.title }}</h2>
          <p class="sub">{{ book.author }} · {{ book.publisher }}</p>
          <p class="price">¥{{ book.price }}</p>
          <p class="copies">可借：{{ book.availableCopies }} / 总数：{{ book.totalCopies }}</p>
          <div class="actions">
            <el-button @click="goBack">返回</el-button>
          </div>
        </div>
      </div>
      <el-divider />
      <div class="section">
        <h4>图书简介</h4>
        <p class="desc">{{ book.description || '暂无简介' }}</p>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getBookById } from '../../api/book'

export default {
  name: 'AdminBookDetail',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const book = ref(null)
    const loading = ref(false)

    const getImage = (b) => {
      const seed = b.isbn || b.id || Math.random()
      return `https://picsum.photos/seed/${encodeURIComponent(seed)}/360/500`
    }

    const loadBook = async () => {
      loading.value = true
      try {
        const res = await getBookById(route.params.id)
        if (res.code === 200) {
          book.value = res.data
        } else {
          ElMessage.error(res.message || '加载图书详情失败')
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '加载图书详情失败')
      } finally {
        loading.value = false
      }
    }

    const goBack = () => router.back()

    onMounted(() => {
      loadBook()
    })

    return {
      book,
      loading,
      getImage,
      goBack
    }
  }
}
</script>

<style scoped>
.book-detail {
  max-width: 960px;
  margin: 0 auto;
}
.detail-header {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}
.cover img {
  width: 240px;
  height: 340px;
  object-fit: cover;
  border-radius: 6px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.12);
}
.meta {
  flex: 1;
}
.meta h2 {
  margin: 0;
}
.sub {
  color: #909399;
  margin: 8px 0;
}
.price {
  color: #f56c6c;
  font-weight: 700;
  margin: 6px 0;
}
.copies {
  color: #606266;
  margin-bottom: 12px;
}
.actions {
  display: flex;
  gap: 10px;
}
.section h4 {
  margin: 0 0 8px;
}
.desc {
  white-space: pre-wrap;
  line-height: 1.6;
  color: #606266;
}
</style>

