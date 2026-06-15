<template>
  <el-container class="layout-container">
    <el-header class="header">
      <div class="header-content">
        <h2>图书管理系统 - 读者端</h2>
        <div class="user-info">
          <span>欢迎，{{ readerInfo.name }}</span>
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
          <el-menu-item index="/reader/books">
            <el-icon><Reading /></el-icon>
            <span>图书列表</span>
          </el-menu-item>
          <el-menu-item index="/reader/requests">
            <el-icon><Edit /></el-icon>
            <span>我的申请</span>
          </el-menu-item>
          <el-menu-item index="/reader/borrows">
            <el-icon><Document /></el-icon>
            <span>我的借阅</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
    <el-footer class="footer">
      <span>图书管理系统 · 读者端 · 示例项目</span>
    </el-footer>
  </el-container>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Reading, Document, Edit } from '@element-plus/icons-vue'

export default {
  name: 'ReaderLayout',
  components: {
    Reading,
    Document,
    Edit
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const readerInfo = ref({})

    const activeMenu = computed(() => {
      if (route.path.startsWith('/reader/books')) return '/reader/books'
      return route.path
    })

    onMounted(() => {
      const userInfo = localStorage.getItem('userInfo')
      if (userInfo) {
        readerInfo.value = JSON.parse(userInfo)
      }
    })

    const logout = () => {
      localStorage.clear()
      router.push('/login')
    }

    return {
      readerInfo,
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

