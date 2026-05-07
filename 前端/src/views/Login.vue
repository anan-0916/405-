<template>
  <div class="login-page">
    <div class="left-pane">
      <div class="hero">
        <img class="hero-img" src="https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?w=1600&q=80&auto=format&fit=crop" alt="books">
        <div class="hero-overlay">
          <h1>图书管理系统</h1>
          <p>智能管理 · 便捷借阅 · 数据驱动</p>
        </div>
      </div>
    </div>
    <div class="right-pane">
      <el-card class="login-card" shadow="hover">
        <div class="logo-row">
          <img class="logo" src="https://cdn-icons-png.flaticon.com/512/29/29302.png" alt="logo">
          <div class="title">图书管理</div>
        </div>

        <el-tabs v-model="activeTab" @tab-change="handleTabChange" class="login-tabs">
          <el-tab-pane label="管理员" name="admin">
            <el-form :model="adminForm" :rules="adminRules" ref="adminFormRef" label-width="0px" class="form">
              <el-form-item prop="username">
                <el-input v-model="adminForm.username" placeholder="用户名" prefix-icon="User"></el-input>
              </el-form-item>
              <el-form-item prop="password">
                <el-input v-model="adminForm.password" type="password" placeholder="密码" @keyup.enter="adminLogin"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="adminLogin" :loading="loading" class="full-btn">登录</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <el-tab-pane label="读者" name="reader">
            <el-form :model="readerForm" :rules="readerRules" ref="readerFormRef" label-width="0px" class="form">
              <el-form-item prop="readerId">
                <el-input v-model="readerForm.readerId" placeholder="读者ID"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="readerLogin" :loading="loading" class="full-btn">登录</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>

        <div class="footnote">使用测试账号登录或通过读者ID直接登录。</div>
      </el-card>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { adminLogin as apiAdminLogin } from '../api/admin'
import { getReaderByReaderId } from '../api/reader'

export default {
  name: 'Login',
  setup() {
    const router = useRouter()
    const activeTab = ref('admin')
    const loading = ref(false)
    const adminFormRef = ref(null)
    const readerFormRef = ref(null)

    const adminForm = reactive({
      username: '',
      password: ''
    })

    const readerForm = reactive({
      readerId: ''
    })

    const adminRules = {
      username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
      password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
    }

    const readerRules = {
      readerId: [{ required: true, message: '请输入读者ID', trigger: 'blur' }]
    }

    const handleTabChange = () => {
      adminFormRef.value?.resetFields()
      readerFormRef.value?.resetFields()
    }

    const adminLogin = async () => {
      await adminFormRef.value.validate(async (valid) => {
        if (valid) {
          loading.value = true
          try {
            const res = await apiAdminLogin(adminForm)
            if (res.code === 200) {
              localStorage.setItem('token', 'admin_token')
              localStorage.setItem('userType', 'admin')
              localStorage.setItem('userInfo', JSON.stringify(res.data))
              ElMessage.success('登录成功')
              router.push('/admin/books')
            } else {
              ElMessage.error(res.message || '登录失败')
            }
          } catch (error) {
            ElMessage.error('登录失败：' + (error.response?.data?.message || error.message))
          } finally {
            loading.value = false
          }
        }
      })
    }

    const readerLogin = async () => {
      await readerFormRef.value.validate(async (valid) => {
        if (valid) {
          loading.value = true
          try {
            const res = await getReaderByReaderId(readerForm.readerId)
            if (res.code === 200) {
              localStorage.setItem('token', 'reader_token')
              localStorage.setItem('userType', 'reader')
              localStorage.setItem('userInfo', JSON.stringify(res.data))
              ElMessage.success('登录成功')
              router.push('/reader/books')
            } else {
              ElMessage.error(res.message || '读者ID不存在')
            }
          } catch (error) {
            ElMessage.error('登录失败：' + (error.response?.data?.message || error.message))
          } finally {
            loading.value = false
          }
        }
      })
    }

    return {
      activeTab,
      loading,
      adminForm,
      readerForm,
      adminRules,
      readerRules,
      adminFormRef,
      readerFormRef,
      handleTabChange,
      adminLogin,
      readerLogin
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 420px;
}

.card-header {
  text-align: center;
}

.card-header h2 {
  color: #333;
  margin: 0;
}

.login-page {
  display: flex;
  min-height: 100vh;
}

.left-pane {
  flex: 1.2;
  position: relative;
  overflow: hidden;
}

.right-pane {
  flex: 0.9;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: #f5f7fa;
}

.hero {
  height: 100vh;
  position: relative;
}

.hero-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: brightness(0.65);
}

.hero-overlay {
  position: absolute;
  left: 60px;
  bottom: 60px;
  color: #fff;
}

.hero-overlay h1 {
  font-size: 48px;
  margin: 0 0 8px 0;
}

.hero-overlay p {
  margin: 0;
  font-size: 18px;
}

.login-card {
  width: 420px;
  border-radius: 12px;
}

.logo-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 18px;
}

.logo {
  width: 44px;
  height: 44px;
}

.title {
  font-size: 20px;
  font-weight: 600;
}

.form .el-input {
  margin-bottom: 12px;
}

.full-btn {
  width: 100%;
}

.footnote {
  margin-top: 14px;
  color: #909399;
  font-size: 12px;
  text-align: center;
}
</style>

