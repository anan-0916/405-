<template>
  <el-container class="layout-container">
    <el-header class="header">
      <div class="header-content">
        <h2>图书管理系统 - 管理员端</h2>
        <div class="user-info">
          <span>欢迎，{{ adminInfo.name }}</span>
          <el-button type="danger" size="small" @click="logout">退出</el-button>
        </div>
      </div>
    </el-header>
    <el-container>
      <el-aside width="200px" class="aside">
        <el-menu
          :default-active="activeMenu"
          router
          class="menu"
        >
          <el-menu-item index="/admin/books">
            <el-icon><Reading /></el-icon>
            <span>图书管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/readers">
            <el-icon><User /></el-icon>
            <span>读者管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/requests">
            <el-icon><Edit /></el-icon>
            <span>申请审批</span>
          </el-menu-item>
          <el-menu-item index="/admin/borrows">
            <el-icon><Document /></el-icon>
            <span>借阅管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
    <el-footer class="footer">
      <span>西西弗图书管理系统 · 管理员端</span>
    </el-footer>
  </el-container>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Reading, User, Document, Edit } from '@element-plus/icons-vue'

export default {
  name: 'AdminLayout',
  components: {
    Reading,
    User,
    Document,
    Edit
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const adminInfo = ref({})

    const activeMenu = computed(() => {
      if (route.path.startsWith('/admin/books')) return '/admin/books'
      return route.path
    })

    onMounted(() => {
      const userInfo = localStorage.getItem('userInfo')
      if (userInfo) {
        adminInfo.value = JSON.parse(userInfo)
      }
    })

    const logout = () => {
      localStorage.clear()
      router.push('/login')
    }

    return {
      adminInfo,
      activeMenu,
      logout
    }
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.footer {
  text-align: center;
  color: #909399;
  padding: 10px 0;
  font-size: 12px;
}

.header {
  background-color: #409eff;
  color: white;
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.aside {
  background-color: #fff;
  border-right: 1px solid #e4e7ed;
}

.menu {
  border-right: none;
}

.main {
  background-color: #f5f5f5;
  padding: 20px;
}
</style>

