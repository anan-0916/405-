<template>
  <div class="book-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>图书列表</h3>
          <el-input
            v-model="searchKeyword"
            placeholder="搜索图书（书名、作者、ISBN）"
            style="width: 300px"
            clearable
            @input="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
      </template>

      <div class="books-grid" v-loading="loading">
        <div class="book-card" v-for="book in pagedBooks" :key="book.id" @click="goDetail(book)">
          <div class="card-media">
            <img :src="getImage(book)" :alt="book.title" />
            <div class="media-overlay">
              <el-button
                size="small"
                type="primary"
                :disabled="book.availableCopies === 0"
                @click.stop="handleBorrow(book)"
              >借阅</el-button>
            </div>
          </div>
          <div class="card-body">
            <div class="title">{{ book.title }}</div>
            <div class="meta">{{ book.author }} · {{ book.publisher }}</div>
            <div class="price">¥{{ book.price }}</div>
            <div class="copies">可借：{{ book.availableCopies }}</div>
          </div>
        </div>
      </div>
    </el-card>
    <div class="pager">
      <el-pagination
        background
        layout="total, prev, pager, next"
        :page-size="pageSize"
        :current-page="currentPage"
        :total="books.length"
        @current-change="(p) => currentPage = p"
      />
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getBooks, searchBooks } from '../../api/book'
import { submitRequest } from '../../api/borrowRequest'

export default {
  name: 'BookList',
  components: {
    Search
  },
  setup() {
    const router = useRouter()
    const books = ref([])
    const pageSize = ref(12)
    const currentPage = ref(1)
    const loading = ref(false)
    const searchKeyword = ref('')

    const loadBooks = async () => {
      loading.value = true
      try {
        const res = await getBooks()
        if (res.code === 200) {
          books.value = res.data
          currentPage.value = 1
        }
      } catch (error) {
        ElMessage.error('加载图书列表失败')
      } finally {
        loading.value = false
      }
    }

    const getImage = (book) => {
      const seed = book.isbn || book.id || Math.random()
      return `https://picsum.photos/seed/${encodeURIComponent(seed)}/300/420`
    }

    const handleSearch = async () => {
      if (!searchKeyword.value.trim()) {
        loadBooks()
        return
      }
      loading.value = true
      try {
        const res = await searchBooks(searchKeyword.value)
        if (res.code === 200) {
          books.value = res.data
          currentPage.value = 1
        }
      } catch (error) {
        ElMessage.error('搜索失败')
      } finally {
        loading.value = false
      }
    }

    const handleBorrow = async (book) => {
      try {
        const { value: formData } = await ElMessageBox.prompt(
          '请填写借阅信息',
          '提交借阅申请',
          {
            confirmButtonText: '提交',
            cancelButtonText: '取消',
            inputType: 'text',
            inputPlaceholder: '请输入借阅天数（1-90天）',
            inputValidator: (value) => {
              if (!value || isNaN(value) || parseInt(value) < 1 || parseInt(value) > 90) {
                return '请输入1-90之间的数字'
              }
              return true
            }
          }
        )

        const borrowDays = parseInt(formData)
        const userInfo = JSON.parse(localStorage.getItem('userInfo'))
        const today = new Date()
        const expectedBorrowDate = today.toISOString().split('T')[0]

        const res = await submitRequest({
          bookId: book.id,
          readerId: userInfo.id,
          expectedBorrowDate: expectedBorrowDate,
          borrowDays: borrowDays
        })

        if (res.code === 200) {
          ElMessage.success('借阅申请已提交，请等待管理员审批')
          loadBooks()
        } else {
          ElMessage.error(res.message || '提交申请失败')
        }
      } catch (error) {
        if (error !== 'cancel' && error !== 'close') {
          ElMessage.error(error.response?.data?.message || error.message || '提交申请失败')
        }
      }
    }

    const goDetail = (book) => {
      router.push(`/reader/books/${book.id}`)
    }

    const pagedBooks = computed(() => {
      const start = (currentPage.value - 1) * pageSize.value
      return books.value.slice(start, start + pageSize.value)
    })

    onMounted(() => {
      loadBooks()
    })

    return {
      books,
      pagedBooks,
      pageSize,
      currentPage,
      loading,
      searchKeyword,
      handleSearch,
      handleBorrow,
      getImage,
      goDetail
    }
  }
}
</script>

<style scoped>
.book-list {
  width: 100%;
}
.pager {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
}

.books-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.book-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  transition: transform 0.18s ease, box-shadow 0.18s ease;
  cursor: pointer;
}

.book-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.12);
}

.card-media { height: 260px; position: relative; overflow: hidden; }
.card-media img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.4s ease; }
.book-card:hover .card-media img { transform: scale(1.06); }
.media-overlay { position: absolute; inset: 0; display:flex; align-items:flex-end; justify-content:center; background: linear-gradient(transparent, rgba(0,0,0,0.3)); opacity:0; transition:opacity .2s; }
.book-card:hover .media-overlay { opacity:1; }
.card-body { padding: 10px 12px; }
.card-body .title { font-weight:600; margin-bottom:6px; }
.card-body .meta { color:#909399; font-size:12px; margin-bottom:8px; }
.card-body .price { color:#f56c6c; font-weight:700; margin-bottom:6px; }
.card-body .copies { color:#909399; font-size:12px; }
</style>


