<template>
  <div class="book-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>图书管理</h3>
          <el-button type="primary" @click="handleAdd">新增图书</el-button>
        </div>
      </template>

      <div class="books-grid" v-loading="loading">
        <div class="book-card" v-for="book in pagedBooks" :key="book.id" @click="goDetail(book)">
          <div class="card-media">
            <img :src="getImage(book)" :alt="book.title" />
            <div class="media-overlay">
              <div class="overlay-actions">
                <el-button size="small" @click.stop="goDetail(book)">详情</el-button>
                <el-button size="small" type="primary" @click.stop="handleEdit(book)">编辑</el-button>
                <el-button size="small" type="danger" @click.stop="handleDelete(book)">删除</el-button>
              </div>
            </div>
          </div>
          <div class="card-body">
            <div class="title">{{ book.title }}</div>
            <div class="meta">{{ book.author }} · {{ book.publisher }}</div>
            <div class="price">¥{{ book.price }}</div>
            <div class="copies">可借：{{ book.availableCopies }} / 总数：{{ book.totalCopies }}</div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="600px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="ISBN" prop="isbn">
          <el-input v-model="form.isbn" :disabled="isEdit"></el-input>
        </el-form-item>
        <el-form-item label="书名" prop="title">
          <el-input v-model="form.title"></el-input>
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="form.author"></el-input>
        </el-form-item>
        <el-form-item label="出版社" prop="publisher">
          <el-input v-model="form.publisher"></el-input>
        </el-form-item>
        <el-form-item label="总数量" prop="totalCopies">
          <el-input-number v-model="form.totalCopies" :min="1"></el-input-number>
        </el-form-item>
        <el-form-item label="可借数量" prop="availableCopies">
          <el-input-number v-model="form.availableCopies" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2"></el-input-number>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
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
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { computed as vueComputed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBooks, createBook, updateBook, deleteBook } from '../../api/book'

export default {
  name: 'AdminBookList',
  setup() {
    const router = useRouter()
    const books = ref([])
    const pageSize = ref(12)
    const currentPage = ref(1)
    const loading = ref(false)
    const dialogVisible = ref(false)
    const isEdit = ref(false)
    const formRef = ref(null)

    const form = reactive({
      id: null,
      isbn: '',
      title: '',
      author: '',
      publisher: '',
      totalCopies: 1,
      availableCopies: 1,
      price: 0,
      description: ''
    })

    const rules = {
      isbn: [{ required: true, message: '请输入ISBN', trigger: 'blur' }],
      title: [{ required: true, message: '请输入书名', trigger: 'blur' }],
      author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
      publisher: [{ required: true, message: '请输入出版社', trigger: 'blur' }],
      totalCopies: [{ required: true, message: '请输入总数量', trigger: 'blur' }],
      availableCopies: [{ required: true, message: '请输入可借数量', trigger: 'blur' }]
    }

    const dialogTitle = computed(() => isEdit.value ? '编辑图书' : '新增图书')
    const pagedBooks = vueComputed(() => {
      const start = (currentPage.value - 1) * pageSize.value
      return books.value.slice(start, start + pageSize.value)
    })

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
          `确定要删除《${row.title}》吗？`,
          '确认删除',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )

        const res = await deleteBook(row.id)
        if (res.code === 200) {
          ElMessage.success('删除成功')
          loadBooks()
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
              res = await updateBook(form.id, form)
            } else {
              res = await createBook(form)
            }
            if (res.code === 200) {
              ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
              dialogVisible.value = false
              loadBooks()
            } else {
              ElMessage.error(res.message || '操作失败')
            }
          } catch (error) {
            ElMessage.error(error.response?.data?.message || '操作失败')
          }
        }
      })
    }

    const goDetail = (book) => {
      router.push(`/admin/books/${book.id}`)
    }

    const resetForm = () => {
      Object.assign(form, {
        id: null,
        isbn: '',
        title: '',
        author: '',
        publisher: '',
        totalCopies: 1,
        availableCopies: 1,
        price: 0,
        description: ''
      })
      formRef.value?.resetFields()
    }

    onMounted(() => {
      loadBooks()
    })

    return {
      books,
      loading,
      dialogVisible,
      isEdit,
      form,
      rules,
      formRef,
      dialogTitle,
      pagedBooks,
      pageSize,
      currentPage,
      goDetail,
      loadBooks,
      handleAdd,
      handleEdit,
      handleDelete,
      handleSubmit,
      resetForm,
      getImage
    }
  }
}
</script>

<style scoped>
.book-list {
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

.pager {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}

.books-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 18px;
  align-items: start;
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

.card-media {
  position: relative;
  height: 300px;
  overflow: hidden;
}

.card-media img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: transform 0.4s ease;
}

.book-card:hover .card-media img {
  transform: scale(1.06);
}

.media-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  background: linear-gradient(transparent, rgba(0,0,0,0.35));
  opacity: 0;
  transition: opacity 0.2s ease;
}

.book-card:hover .media-overlay {
  opacity: 1;
}

.overlay-actions {
  padding: 12px;
  display: flex;
  gap: 8px;
}

.card-body {
  padding: 12px 14px 16px 14px;
}

.card-body .title {
  font-weight: 600;
  margin-bottom: 6px;
}

.card-body .meta {
  color: #909399;
  font-size: 12px;
  margin-bottom: 8px;
}

.card-body .price {
  color: #f56c6c;
  font-weight: 700;
  margin-bottom: 6px;
}

.card-body .copies {
  color: #909399;
  font-size: 12px;
}
</style>

