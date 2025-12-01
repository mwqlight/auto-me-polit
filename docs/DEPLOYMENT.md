# AutoMePolit 部署文档

## 目录
- [系统要求](#系统要求)
- [开发环境搭建](#开发环境搭建)
- [生产环境部署](#生产环境部署)
- [Docker部署](#docker部署)
- [数据库初始化](#数据库初始化)
- [常见问题](#常见问题)

## 系统要求

### 硬件要求
- **CPU**: 2核心以上
- **内存**: 4GB以上（推荐8GB）
- **硬盘**: 20GB以上可用空间
- **网络**: 稳定的互联网连接

### 软件要求
- **操作系统**: Linux/macOS/Windows
- **Java**: OpenJDK 17+
- **Node.js**: 18.0+
- **Maven**: 3.8+
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **Docker**: 20.0+ (可选)

## 开发环境搭建

### 1. 环境准备

#### 安装Java 17
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk

# CentOS/RHEL
sudo yum install java-17-openjdk-devel

# macOS
brew install openjdk@17

# 验证安装
java -version
```

#### 安装Node.js 18
```bash
# 使用nvm管理Node.js版本
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash
source ~/.bashrc
nvm install 18
nvm use 18
nvm alias default 18

# 验证安装
node -v
npm -v
```

#### 安装Maven
```bash
# Ubuntu/Debian
sudo apt install maven

# CentOS/RHEL
sudo yum install maven

# macOS
brew install maven

# 验证安装
mvn -version
```

### 2. 数据库准备

#### MySQL 8.0 安装和配置
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install mysql-server-8.0
sudo mysql_secure_installation

# CentOS/RHEL
sudo yum install mysql-server
sudo systemctl start mysqld
sudo mysql_secure_installation

# macOS
brew install mysql
brew services start mysql
```

#### 创建数据库
```sql
-- 登录MySQL
mysql -u root -p

-- 创建数据库和用户
CREATE DATABASE auto_me_polit CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'auto_me_polit'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON auto_me_polit.* TO 'auto_me_polit'@'localhost';
FLUSH PRIVILEGES;
```

#### Redis 安装和配置
```bash
# Ubuntu/Debian
sudo apt install redis-server

# CentOS/RHEL
sudo yum install redis

# macOS
brew install redis

# 启动Redis
sudo systemctl start redis-server
# 或
brew services start redis

# 验证Redis
redis-cli ping
# 应该返回 PONG
```

### 3. 项目部署

#### 克隆项目
```bash
git clone <your-repository-url>
cd auto-me-polit
```

#### 后端部署
```bash
# 进入后端目录
cd auto-me-polit-backend

# 复制环境配置文件
cp ../.env.development .env

# 修改配置文件，修改数据库连接信息
vim .env

# 编译和打包
mvn clean compile
mvn test
mvn package

# 运行测试
mvn spring-boot:run

# 后台运行
nohup mvn spring-boot:run > backend.log 2>&1 &
```

#### 前端部署
```bash
# 进入前端目录
cd auto-me-polit-frontend

# 复制环境配置文件
cp .env.development .env

# 修改API配置
vim .env

# 安装依赖
npm install

# 开发模式运行
npm run dev

# 生产构建
npm run build

# 预览生产构建
npm run preview
```

### 4. 验证部署

#### 检查后端服务
```bash
# 检查端口占用
netstat -tlnp | grep 8080

# 测试API接口
curl http://localhost:8080/api/v1/health
# 应该返回: {"status":"UP"}
```

#### 检查前端服务
```bash
# 检查端口占用
netstat -tlnp | grep 5173

# 访问前端页面
# 浏览器打开: http://localhost:5173
```

## 生产环境部署

### 1. 服务器准备

#### 防火墙配置
```bash
# 开放必要端口
sudo ufw allow 22   # SSH
sudo ufw allow 80   # HTTP
sudo ufw allow 443  # HTTPS
sudo ufw allow 8080 # 后端API
sudo ufw enable
```

#### 系统优化
```bash
# 增加文件描述符限制
echo "* soft nofile 65535" >> /etc/security/limits.conf
echo "* hard nofile 65535" >> /etc/security/limits.conf

# 优化网络参数
echo "net.core.somaxconn = 65535" >> /etc/sysctl.conf
echo "net.ipv4.tcp_max_syn_backlog = 65535" >> /etc/sysctl.conf
sysctl -p
```

### 2. 生产环境配置

#### 后端配置
```bash
# 复制生产环境配置
cp .env.production .env

# 修改关键配置
# - 数据库连接信息
# - Redis连接信息
# - JWT密钥
# - 第三方服务配置

# 设置环境变量
export SPRING_PROFILES_ACTIVE=production
```

#### 前端配置
```bash
# 复制生产环境配置
cp .env.production .env

# 修改API域名
VITE_API_BASE_URL=https://your-api-domain.com/api/v1

# 构建生产版本
npm run build
```

### 3. Nginx 配置

#### 安装Nginx
```bash
# Ubuntu/Debian
sudo apt install nginx

# CentOS/RHEL
sudo yum install nginx
```

#### 创建Nginx配置
```nginx
# /etc/nginx/sites-available/auto-me-polit
server {
    listen 80;
    server_name your-domain.com;
    
    # 重定向到HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name your-domain.com;
    
    # SSL配置
    ssl_certificate /path/to/your/certificate.pem;
    ssl_certificate_key /path/to/your/private.key;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512;
    ssl_prefer_server_ciphers off;
    
    # 前端静态文件
    location / {
        root /var/www/auto-me-polit/frontend/dist;
        try_files $uri $uri/ /index.html;
        
        # 缓存配置
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
            expires 1y;
            add_header Cache-Control "public, immutable";
        }
    }
    
    # 后端API代理
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # 超时配置
        proxy_connect_timeout 30s;
        proxy_send_timeout 30s;
        proxy_read_timeout 30s;
        
        # WebSocket支持
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
    
    # 健康检查
    location /health {
        proxy_pass http://localhost:8080/api/v1/health;
        access_log off;
    }
}
```

#### 启用Nginx配置
```bash
# 创建软链接
sudo ln -s /etc/nginx/sites-available/auto-me-polit /etc/nginx/sites-enabled/

# 测试配置
sudo nginx -t

# 重启Nginx
sudo systemctl restart nginx
sudo systemctl enable nginx
```

### 4. 进程管理

#### 使用Systemd管理后端服务
```ini
# /etc/systemd/system/auto-me-polit-backend.service
[Unit]
Description=AutoMePolit Backend Service
After=network.target mysql.service redis.service

[Service]
Type=simple
User=auto-me-polit
Group=auto-me-polit
WorkingDirectory=/opt/auto-me-polit/backend
ExecStart=/usr/bin/java -jar -Dspring.profiles.active=production auto-me-polit-backend.jar
Restart=always
RestartSec=10

# 环境变量
Environment=SPRING_PROFILES_ACTIVE=production
Environment=JAVA_HOME=/usr/lib/jvm/java-17-openjdk

# 资源限制
LimitNOFILE=65535
LimitNPROC=32768

# 日志
StandardOutput=journal
StandardError=journal
SyslogIdentifier=auto-me-polit-backend

[Install]
WantedBy=multi-user.target
```

#### 启动服务
```bash
# 创建服务用户
sudo useradd -r -s /bin/false auto-me-polit

# 复制应用文件
sudo mkdir -p /opt/auto-me-polit/backend
sudo cp target/auto-me-polit-backend.jar /opt/auto-me-polit/backend/
sudo chown -R auto-me-polit:auto-me-polit /opt/auto-me-polit/backend

# 重新加载systemd
sudo systemctl daemon-reload

# 启动服务
sudo systemctl start auto-me-polit-backend
sudo systemctl enable auto-me-polit-backend

# 检查服务状态
sudo systemctl status auto-me-polit-backend
```

## Docker部署

### 1. 创建Docker Compose配置
```yaml
# docker-compose.yml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: auto-me-polit-mysql
    restart: unless-stopped
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: auto_me_polit
      MYSQL_USER: auto_me_polit
      MYSQL_PASSWORD: password
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    command: --default-authentication-plugin=mysql_native_password

  redis:
    image: redis:7-alpine
    container_name: auto-me-polit-redis
    restart: unless-stopped
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    command: redis-server --appendonly yes

  backend:
    build:
      context: ./auto-me-polit-backend
      dockerfile: Dockerfile
    container_name: auto-me-polit-backend
    restart: unless-stopped
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - DB_HOST=mysql
      - REDIS_HOST=redis
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - redis
    volumes:
      - ./logs:/app/logs

  frontend:
    build:
      context: ./auto-me-polit-frontend
      dockerfile: Dockerfile
    container_name: auto-me-polit-frontend
    restart: unless-stopped
    ports:
      - "80:80"
    depends_on:
      - backend

  nginx:
    image: nginx:alpine
    container_name: auto-me-polit-nginx
    restart: unless-stopped
    ports:
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
      - ./ssl:/etc/nginx/ssl
      - ./logs:/var/log/nginx
    depends_on:
      - frontend
      - backend

volumes:
  mysql_data:
  redis_data:

networks:
  default:
    name: auto-me-polit-network
```

### 2. 构建Docker镜像

#### 后端Dockerfile
```dockerfile
# auto-me-polit-backend/Dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

# 安装必要工具
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# 复制应用
COPY target/auto-me-polit-backend.jar app.jar

# 创建日志目录
RUN mkdir -p logs

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/v1/health || exit 1

# 暴露端口
EXPOSE 8080

# 启动应用
CMD ["java", "-jar", "-Dspring.profiles.active=docker", "app.jar"]
```

#### 前端Dockerfile
```dockerfile
# auto-me-polit-frontend/Dockerfile
FROM node:18-alpine as builder

WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production

COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### 3. 启动Docker服务
```bash
# 构建并启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f backend
docker-compose logs -f frontend

# 停止服务
docker-compose down

# 停止并清理数据
docker-compose down -v
```

## 数据库初始化

### 1. 手动初始化
```sql
-- 创建数据表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);

-- 插入初始数据
INSERT INTO users (username, email, password) VALUES 
('admin', 'admin@example.com', '$2a$10$...'), -- 密码为admin123
('user', 'user@example.com', '$2a$10$...');   -- 密码为user123
```

### 2. Flyway迁移（推荐）
```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

## 常见问题

### 1. 端口被占用
```bash
# 查看端口占用
netstat -tlnp | grep 8080

# 杀死占用进程
sudo kill -9 <PID>
```

### 2. 数据库连接失败
- 检查数据库服务是否启动
- 验证连接信息是否正确
- 检查防火墙设置
- 确认数据库用户权限

### 3. Redis连接失败
```bash
# 测试Redis连接
redis-cli ping

# 查看Redis日志
sudo journalctl -u redis-server -f
```

### 4. 内存不足
```bash
# 查看系统资源使用
free -h
df -h

# 调整JVM参数
java -Xmx2g -Xms2g -jar auto-me-polit-backend.jar
```

### 5. SSL证书问题
```bash
# 使用Let's Encrypt获取免费证书
sudo apt install certbot
sudo certbot --nginx -d your-domain.com

# 手动安装证书
sudo mkdir -p /etc/nginx/ssl
sudo cp your-cert.pem /etc/nginx/ssl/
sudo cp your-private.key /etc/nginx/ssl/
```

### 6. 性能优化建议

#### JVM调优
```bash
# 生产环境JVM参数
java -server \
    -Xms2g -Xmx2g \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=200 \
    -XX:+UnlockExperimentalVMOptions \
    -XX:+UseCGroupMemoryLimitForHeap \
    -jar auto-me-polit-backend.jar
```

#### 数据库优化
```sql
-- 调整MySQL配置
SET GLOBAL innodb_buffer_pool_size = 1073741824; -- 1GB
SET GLOBAL innodb_log_file_size = 268435456;     -- 256MB
```

#### Nginx优化
```nginx
# 启用gzip压缩
gzip on;
gzip_types text/plain text/css application/json application/javascript;

# 启用HTTP/2
listen 443 ssl http2;

# 调整worker连接数
worker_connections 4096;
```

### 7. 监控和日志

#### 日志配置
```yaml
# logback-spring.xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/application.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <maxHistory>30</maxHistory>
            <totalSizeCap>3GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="STDOUT" />
        <appender-ref ref="FILE" />
    </root>
</configuration>
```

#### 健康检查
```bash
# 创建健康检查脚本
#!/bin/bash
# health-check.sh

API_URL="http://localhost:8080/api/v1/health"
RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" $API_URL)

if [ $RESPONSE -eq 200 ]; then
    echo "Application is healthy"
    exit 0
else
    echo "Application is unhealthy (HTTP $RESPONSE)"
    exit 1
fi

# 添加到crontab
# */5 * * * * /path/to/health-check.sh | logger -t health-check
```

## 故障排除

### 检查服务状态
```bash
# 检查后端服务
curl http://localhost:8080/api/v1/health

# 检查前端服务
curl -I http://localhost:5173

# 检查数据库连接
mysql -u auto_me_polit -p -e "SELECT 1;"

# 检查Redis连接
redis-cli ping
```

### 查看日志
```bash
# 后端日志
tail -f /opt/auto-me-polit/backend/logs/application.log

# Nginx日志
tail -f /var/log/nginx/access.log
tail -f /var/log/nginx/error.log

# 系统日志
sudo journalctl -u auto-me-polit-backend -f
```

### 重启服务
```bash
# 重启后端服务
sudo systemctl restart auto-me-polit-backend

# 重启Nginx
sudo systemctl restart nginx

# 重启所有Docker服务
docker-compose restart
```

---

如有问题，请参考项目README.md或联系技术支持团队。