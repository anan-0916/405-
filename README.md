# 图书管理系统

一个基于 SpringBoot + Vue 3 的图书管理系统，支持读者借书和管理员管理图书等功能。

## 技术栈

### 后端
- Spring Boot 2.7.14
- Spring Data JPA
- MySQL 8.0
- Maven

### 前端
- Vue 3
- Vue Router 4
- Element Plus
- Axios
- Vite

## 项目结构

```
图书管理商场/
├── src/                    # SpringBoot后端代码
│   └── main/
│       ├── java/com/bookstore/
│       │   ├── entity/     # 实体类
│       │   ├── repository/ # 数据访问层
│       │   ├── service/    # 服务层
│       │   ├── controller/ # 控制器层
│       │   └── config/      # 配置类
│       └── resources/
│           └── application.yml
├── frontend/               # Vue前端代码
│   ├── src/
│   │   ├── api/           # API接口
│   │   ├── views/         # 页面组件
│   │   ├── router/        # 路由配置
│   │   └── App.vue
│   ├── package.json
│   └── vite.config.js
├── database/              # 数据库脚本
│   └── init.sql
├── pom.xml                # Maven配置
└── README.md
```

## 功能特性

### 读者功能
- 图书浏览和搜索
- 借阅图书
- 查看我的借阅记录
- 归还图书

### 管理员功能
- 图书管理（增删改查）
- 读者管理（增删改查）
- 借阅记录查看

## 快速开始

### 1. 数据库配置

1. 创建MySQL数据库（默认名称：bookstore）
2. 修改 `src/main/resources/application.yml` 中的数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/bookstore?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root  # 修改为你的数据库用户名
    password: root  # 修改为你的数据库密码
```

3. 执行 `database/init.sql` 初始化数据库和示例数据

### 2. 启动后端

```bash
# 进入项目根目录
cd 图书管理商场

# 使用Maven启动（需要先安装Maven）
mvn spring-boot:run

# 或者使用IDE直接运行 BookManagementApplication.java
```

后端服务将在 `http://localhost:8080` 启动

### 3. 启动前端

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务将在 `http://localhost:3000` 启动

### 4. 访问系统

打开浏览器访问：`http://localhost:3000`

## 默认账号

### 管理员
- 用户名：`admin`
- 密码：`admin123`

### 读者
- 读者ID：`R001` 或 `R002`

## API接口说明

### 图书管理
- `GET /api/books` - 获取所有图书
- `GET /api/books/{id}` - 获取图书详情
- `GET /api/books/search?keyword=xxx` - 搜索图书
- `POST /api/books` - 新增图书
- `PUT /api/books/{id}` - 更新图书
- `DELETE /api/books/{id}` - 删除图书

### 读者管理
- `GET /api/readers` - 获取所有读者
- `GET /api/readers/{id}` - 获取读者详情
- `POST /api/readers` - 新增读者
- `PUT /api/readers/{id}` - 更新读者
- `DELETE /api/readers/{id}` - 删除读者

### 借阅管理
- `POST /api/borrow` - 借阅图书
- `POST /api/borrow/return/{id}` - 归还图书
- `GET /api/borrow/reader/{readerId}` - 获取读者借阅记录
- `GET /api/borrow/all` - 获取所有借阅记录

### 管理员
- `POST /api/admin/login` - 管理员登录

## 开发说明

### 后端开发
- 使用Spring Data JPA进行数据持久化
- 使用RESTful API设计
- 统一使用 `ApiResponse` 封装响应数据

### 前端开发
- 使用Vue 3 Composition API
- 使用Element Plus组件库
- 使用Vue Router进行路由管理
- 使用Axios进行HTTP请求

## 注意事项

1. 确保MySQL服务已启动
2. 确保端口8080和3000未被占用
3. 首次运行需要执行数据库初始化脚本
4. 前端开发服务器已配置代理，API请求会自动转发到后端

## 许可证

MIT License

