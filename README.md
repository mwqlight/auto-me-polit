# AutoMePolit

[![GitHub stars](https://img.shields.io/github/stars/your-username/auto-me-polit.svg?style=flat-square)](https://github.com/your-username/auto-me-polit/stargazers)
[![GitHub issues](https://img.shields.io/github/issues/your-username/auto-me-polit.svg?style=flat-square)](https://github.com/your-username/auto-me-polit/issues)
[![GitHub license](https://img.shields.io/github/license/your-username/auto-me-polit.svg?style=flat-square)](https://github.com/your-username/auto-me-polit/blob/main/LICENSE)

## 项目简介

AutoMePolit是一个现代化的多身份管理与数据同步平台，旨在帮助用户管理不同平台的身份信息，并实现数据在各个平台间的安全、同步与共享。该系统采用SpringBoot + Vue3技术栈，提供完整的身份管理、账户关联和数据共享策略功能。

### 核心特性

- **多身份管理**: 为每个平台或服务创建独立身份，保护隐私和安全
- **智能账户关联**: 轻松关联和管理多个第三方平台账户
- **灵活数据策略**: 配置不同类型的数据共享策略（同步、共享、备份、迁移）
- **可视化操作界面**: 直观的Web界面，支持响应式设计
- **实时状态监控**: 实时查看数据同步状态和错误信息
- **安全可靠**: 采用加密存储和JWT认证机制，确保数据安全
- **开放扩展**: 支持添加新的平台和数据类型

## 项目架构

### 技术栈

#### 后端技术
- **框架**: Spring Boot 3.0+
- **语言**: Java 17
- **数据库**: MySQL 8.0
- **缓存**: Redis 7.0
- **安全**: Spring Security + JWT
- **ORM**: Spring Data JPA
- **API文档**: SpringDoc OpenAPI 3.0
- **任务调度**: Spring Scheduler
- **消息队列**: RabbitMQ / Apache Kafka (可选)

#### 前端技术
- **框架**: Vue 3.0+
- **语言**: TypeScript 4.9+
- **构建工具**: Vite 4.0+
- **UI库**: Ant Design Vue 4.0+
- **状态管理**: Pinia
- **路由**: Vue Router 4.0
- **HTTP客户端**: Axios
- **样式**: Tailwind CSS 3.0+ / Less

### 目录结构

```
auto-me-polit
├── auto-me-polit-backend          # SpringBoot后端项目
│   ├── src/main/java/com/autompolit
│   │   ├── config/                 # 配置类
│   │   ├── controller/             # 控制器
│   │   ├── dto/                    # 数据传输对象
│   │   ├── service/                # 业务逻辑
│   │   ├── repository/             # 数据访问层
│   │   ├── entity/                 # 实体类
│   │   ├── exception/              # 异常处理
│   │   ├── util/                   # 工具类
│   │   └── Application.java        # 启动类
│   ├── src/main/resources
│   │   ├── application.yml         # 应用配置
│   │   └── db/migration/           # 数据库迁移文件
│   └── pom.xml                     # Maven配置
├── auto-me-polit-frontend          # Vue3前端项目
│   ├── public/                     # 静态资源
│   ├── src
│   │   ├── assets/                 # 静态资源
│   │   ├── components/             # 公共组件
│   │   ├── composables/            # 组合式API
│   │   ├── router/                 # 路由配置
│   │   ├── store/                  # 状态管理
│   │   ├── styles/                 # 全局样式
│   │   ├── types/                  # TypeScript类型定义
│   │   ├── utils/                  # 工具函数
│   │   ├── views/                  # 页面视图
│   │   ├── App.vue                 # 根组件
│   │   └── main.ts                 # 入口文件
│   ├── index.html                  # HTML模板
│   ├── package.json                # 前端依赖
│   └── vite.config.ts              # Vite配置
├── docs/                           # 项目文档
│   ├── API.md                      # API文档
│   ├── DATABASE.md                 # 数据库文档
│   └── DEPLOYMENT.md               # 部署文档
├── .env.development                # 开发环境变量
├── .env.production                 # 生产环境变量
├── .gitignore                      # Git忽略规则
├── LICENSE                         # 许可证
├── README.md                       # 项目说明
└── feature-main.md                 # 功能规划
```

## 功能模块

### 身份管理
- 创建和管理多个身份
- 为每个身份绑定设备和生物特征信息
- 支持身份标签分类
- 查看身份使用统计

### 账户关联
- 关联第三方平台账户（微信、支付宝、微博等）
- 支持OAuth 2.0授权流程
- 查看关联账户详情
- 配置同步设置

### 共享策略
- 创建数据共享策略（同步、共享、备份、迁移）
- 配置策略执行频率和规则
- 查看策略执行历史和统计
- 实时监控策略状态

### 系统管理
- 用户账户管理
- 系统配置管理
- 审计日志记录
- 性能监控

## 快速开始

### 环境要求
- Java 17+
- Node.js 16+ / Node.js 18+ (推荐)
- MySQL 8.0+
- Redis 7.0+

### 安装部署

#### 1. 克隆仓库

```bash
git clone https://github.com/your-username/auto-me-polit.git
cd auto-me-polit
```

#### 2. 数据库配置

启动MySQL和Redis服务，然后创建数据库：

```bash
mysql -u root -p
```

```sql
CREATE DATABASE auto_me_polit CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 3. 配置环境变量

根据`.env.development`和`.env.production`配置示例，创建本地环境配置文件。

#### 4. 启动后端服务

```bash
cd auto-me-polit-backend
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=development
```

#### 5. 启动前端服务

```bash
cd auto-me-polit-frontend
npm install
npm run dev
```

访问前端应用：http://localhost:5173

更详细的安装说明请参考[部署文档](docs/DEPLOYMENT.md)。

## API文档

详细的API文档请参考[API文档](docs/API.md)。

## 开发指南

### 开发规范

本项目严格遵循团队制定的5A6S开发规范：

- **5A**: Architecture(清晰架构) + API(统一接口规范) + Automation(自动化工程) + Assurance(质量保障) + Agility(敏捷协作)
- **6S**: Structure(目录结构标准) + Standards(编码标准) + Security(安全标准) + Stability(稳定性标准) + Scalability(可扩展标准) + Sustainability(可持续维护)

更多详情请参考项目[开发规范文档](docs/DEVELOPMENT.md)。

### 贡献指南

1. Fork 项目
2. 创建功能分支: `git checkout -b feature/AmazingFeature`
3. 提交更改: `git commit -m 'Add some AmazingFeature'`
4. 推送分支: `git push origin feature/AmazingFeature`
5. 提交 Pull Request

## 许可证

本项目基于 MIT 许可证开源 - 详情请查看 [LICENSE](LICENSE) 文件。

## 联系方式

- 项目作者: Your Name (your.email@example.com)
- 项目链接: [https://github.com/your-username/auto-me-polit](https://github.com/your-username/auto-me-polit)
- 项目主页: [https://your-website.com](https://your-website.com)

## 更新日志

### v1.0.0 (2024-01-01)
- 初始版本发布
- 实现基础身份管理功能
- 实现账户关联功能
- 实现共享策略功能

## 致谢

感谢以下开源项目和技术：

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Vue.js](https://vuejs.org/)
- [Ant Design Vue](https://antdv.com/)
- [MySQL](https://www.mysql.com/)
- [Redis](https://redis.io/) 
