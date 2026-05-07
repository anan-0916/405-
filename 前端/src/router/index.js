import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import ReaderLayout from '../views/reader/Layout.vue'
import AdminLayout from '../views/admin/Layout.vue'
import BookList from '../views/reader/BookList.vue'
import BookDetail from '../views/reader/BookDetail.vue'
import MyRequests from '../views/reader/MyRequests.vue'
import MyBorrows from '../views/reader/MyBorrows.vue'
import AdminBookList from '../views/admin/BookList.vue'
import AdminBookDetail from '../views/admin/BookDetail.vue'
import AdminReaderList from '../views/admin/ReaderList.vue'
import RequestApproval from '../views/admin/RequestApproval.vue'
import AdminBorrowList from '../views/admin/BorrowList.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/reader',
    component: ReaderLayout,
    children: [
      {
        path: 'books',
        name: 'ReaderBooks',
        component: BookList
      },
      {
        path: 'books/:id',
        name: 'ReaderBookDetail',
        component: BookDetail
      },
      {
        path: 'requests',
        name: 'MyRequests',
        component: MyRequests
      },
      {
        path: 'borrows',
        name: 'MyBorrows',
        component: MyBorrows
      }
    ]
  },
  {
    path: '/admin',
    component: AdminLayout,
    children: [
      {
        path: 'books',
        name: 'AdminBooks',
        component: AdminBookList
      },
      {
        path: 'books/:id',
        name: 'AdminBookDetail',
        component: AdminBookDetail
      },
      {
        path: 'readers',
        name: 'AdminReaders',
        component: AdminReaderList
      },
      {
        path: 'requests',
        name: 'RequestApproval',
        component: RequestApproval
      },
      {
        path: 'borrows',
        name: 'AdminBorrows',
        component: AdminBorrowList
      }
    ]
  },
  {
    path: '/',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userType = localStorage.getItem('userType')
  
  if (to.path === '/login') {
    next()
  } else if (!token) {
    next('/login')
  } else if (to.path.startsWith('/admin') && userType !== 'admin') {
    next('/reader/books')
  } else if (to.path.startsWith('/reader') && userType === 'admin') {
    next('/admin/books')
  } else {
    next()
  }
})

export default router

