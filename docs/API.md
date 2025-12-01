# AutoMePolit API 文档

## 目录
- [基本信息](#基本信息)
- [认证机制](#认证机制)
- [通用响应格式](#通用响应格式)
- [错误码说明](#错误码说明)
- [API接口](#api接口)
  - [身份管理](#身份管理)
  - [账户关联](#账户关联)
  - [共享策略](#共享策略)
  - [系统管理](#系统管理)

## 基本信息

### 基础信息
- **Base URL**: `http://localhost:8080/api/v1`
- **协议**: HTTPS (生产环境)
- **数据格式**: JSON
- **字符编码**: UTF-8

### 版本控制
API使用URL路径版本控制，当前版本为 `v1`。

### 内容类型
- **请求**: `Content-Type: application/json`
- **响应**: `Content-Type: application/json`

## 认证机制

### JWT Token认证
本系统使用JWT (JSON Web Token) 进行用户认证。

#### 获取Token
```http
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

#### 使用Token
在请求头中添加Authorization字段：
```http
Authorization: Bearer <your-jwt-token>
```

#### Token刷新
```http
POST /auth/refresh
Content-Type: application/json

{
  "refreshToken": "your-refresh-token"
}
```

## 通用响应格式

### 成功响应
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "content": "actual data"
  },
  "timestamp": 1640995200000
}
```

### 失败响应
```json
{
  "code": 400,
  "message": "参数错误",
  "errors": [
    {
      "field": "username",
      "message": "用户名不能为空"
    }
  ],
  "timestamp": 1640995200000
}
```

### 分页响应
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "items": [...],
    "total": 100,
    "page": 1,
    "size": 10,
    "pages": 10
  },
  "timestamp": 1640995200000
}
```

## 错误码说明

### 成功码 (2xx)
| 错误码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 201 | 创建成功 |
| 204 | 删除成功 |

### 客户端错误 (4xx)
| 错误码 | 说明 | HTTP状态码 |
|--------|------|------------|
| 400 | 请求参数错误 | 400 |
| 401 | 未认证或Token无效 | 401 |
| 403 | 权限不足 | 403 |
| 404 | 资源不存在 | 404 |
| 409 | 资源冲突 | 409 |
| 422 | 数据验证失败 | 422 |
| 429 | 请求频率限制 | 429 |

### 业务错误码 (10000+)
| 错误码 | 说明 |
|--------|------|
| 10001 | 用户不存在 |
| 10002 | 密码错误 |
| 10003 | 账户已停用 |
| 10004 | 身份不存在 |
| 10005 | 账户关联不存在 |
| 10006 | 策略不存在 |
| 10007 | 策略已启用 |
| 10008 | 策略配置错误 |
| 10009 | 第三方服务连接失败 |
| 10010 | 数据同步失败 |

### 服务端错误 (5xx)
| 错误码 | 说明 | HTTP状态码 |
|--------|------|------------|
| 500 | 系统内部错误 | 500 |
| 502 | 网关错误 | 502 |
| 503 | 服务不可用 | 503 |
| 504 | 请求超时 | 504 |

## API接口

## 身份管理

### 获取身份列表
```http
GET /identities
```

#### 查询参数
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认1 |
| size | int | 否 | 每页数量，默认10 |
| search | string | 否 | 搜索关键词 |
| status | string | 否 | 身份状态 (active/inactive) |
| platform | string | 否 | 平台类型 |

#### 响应示例
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "items": [
      {
        "id": "1",
        "name": "我的微信",
        "description": "主要使用的微信账户",
        "platform": "wechat",
        "status": "active",
        "avatar": "https://example.com/avatar.jpg",
        "tags": ["主账号", "工作"],
        "deviceCount": 2,
        "createdAt": "2024-01-01T10:00:00Z",
        "updatedAt": "2024-01-01T10:00:00Z"
      }
    ],
    "total": 25,
    "page": 1,
    "size": 10,
    "pages": 3
  },
  "timestamp": 1640995200000
}
```

### 创建身份
```http
POST /identities
Content-Type: application/json

{
  "name": "我的微信",
  "description": "主要使用的微信账户",
  "platform": "wechat",
  "avatar": "https://example.com/avatar.jpg",
  "tags": ["主账号", "工作"],
  "devices": [
    {
      "deviceId": "device123",
      "deviceName": "iPhone 12",
      "deviceType": "mobile"
    }
  ],
  "biometricData": {
    "fingerprint": true,
    "faceId": false,
    "voiceId": true
  }
}
```

#### 响应示例
```json
{
  "code": 201,
  "message": "创建成功",
  "data": {
    "id": "1",
    "name": "我的微信",
    "description": "主要使用的微信账户",
    "platform": "wechat",
    "status": "active",
    "avatar": "https://example.com/avatar.jpg",
    "tags": ["主账号", "工作"],
    "deviceCount": 1,
    "createdAt": "2024-01-01T10:00:00Z",
    "updatedAt": "2024-01-01T10:00:00Z"
  },
  "timestamp": 1640995200000
}
```

### 获取身份详情
```http
GET /identities/{id}
```

#### 响应示例
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "id": "1",
    "name": "我的微信",
    "description": "主要使用的微信账户",
    "platform": "wechat",
    "status": "active",
    "avatar": "https://example.com/avatar.jpg",
    "tags": ["主账号", "工作"],
    "devices": [
      {
        "deviceId": "device123",
        "deviceName": "iPhone 12",
        "deviceType": "mobile",
        "lastLoginAt": "2024-01-01T10:00:00Z"
      }
    ],
    "biometricData": {
      "fingerprint": true,
      "faceId": false,
      "voiceId": true
    },
    "stats": {
      "accountCount": 3,
      "policyCount": 5,
      "lastSyncAt": "2024-01-01T09:00:00Z"
    },
    "createdAt": "2024-01-01T10:00:00Z",
    "updatedAt": "2024-01-01T10:00:00Z"
  },
  "timestamp": 1640995200000
}
```

### 更新身份
```http
PUT /identities/{id}
Content-Type: application/json

{
  "name": "我的微信-更新",
  "description": "更新的描述",
  "tags": ["主账号", "工作", "个人"]
}
```

### 删除身份
```http
DELETE /identities/{id}
```

#### 响应示例
```json
{
  "code": 204,
  "message": "删除成功",
  "data": null,
  "timestamp": 1640995200000
}
```

## 账户关联

### 获取关联账户列表
```http
GET /linked-accounts
```

#### 查询参数
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认1 |
| size | int | 否 | 每页数量，默认10 |
| search | string | 否 | 搜索关键词 |
| platform | string | 否 | 平台类型 |
| status | string | 否 | 关联状态 |

#### 响应示例
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "items": [
      {
        "id": "1",
        "identityId": "identity-1",
        "platform": "wechat",
        "platformUserId": "wx123456",
        "name": "微信用户",
        "nickname": "用户昵称",
        "avatar": "https://example.com/avatar.jpg",
        "status": "active",
        "isVerified": true,
        "lastSyncAt": "2024-01-01T10:00:00Z",
        "syncConfig": {
          "autoSync": true,
          "syncInterval": 3600,
          "syncDataTypes": ["profile", "contacts"]
        },
        "createdAt": "2024-01-01T10:00:00Z",
        "updatedAt": "2024-01-01T10:00:00Z"
      }
    ],
    "total": 15,
    "page": 1,
    "size": 10,
    "pages": 2
  },
  "timestamp": 1640995200000
}
```

### 关联第三方账户
```http
POST /linked-accounts/link
Content-Type: application/json

{
  "identityId": "identity-1",
  "platform": "wechat",
  "authCode": "auth-code-from-platform",
  "syncConfig": {
    "autoSync": true,
    "syncInterval": 3600,
    "syncDataTypes": ["profile", "contacts"]
  }
}
```

#### 响应示例
```json
{
  "code": 201,
  "message": "关联成功",
  "data": {
    "id": "1",
    "identityId": "identity-1",
    "platform": "wechat",
    "platformUserId": "wx123456",
    "name": "微信用户",
    "nickname": "用户昵称",
    "avatar": "https://example.com/avatar.jpg",
    "status": "active",
    "isVerified": true,
    "syncConfig": {
      "autoSync": true,
      "syncInterval": 3600,
      "syncDataTypes": ["profile", "contacts"]
    },
    "createdAt": "2024-01-01T10:00:00Z",
    "updatedAt": "2024-01-01T10:00:00Z"
  },
  "timestamp": 1640995200000
}
```

### 获取账户详情
```http
GET /linked-accounts/{id}
```

### 解除账户关联
```http
DELETE /linked-accounts/{id}
```

### 同步账户数据
```http
POST /linked-accounts/{id}/sync
Content-Type: application/json

{
  "syncTypes": ["profile", "contacts"],
  "forceFullSync": false
}
```

#### 响应示例
```json
{
  "code": 200,
  "message": "同步请求已提交",
  "data": {
    "syncTaskId": "task-123",
    "estimatedDuration": 30,
    "status": "pending"
  },
  "timestamp": 1640995200000
}
```

## 共享策略

### 获取策略列表
```http
GET /share-policies
```

#### 查询参数
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认1 |
| size | int | 否 | 每页数量，默认10 |
| search | string | 否 | 搜索关键词 |
| type | string | 否 | 策略类型 (sync/share/backup/migration) |
| status | string | 否 | 策略状态 |

#### 响应示例
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "items": [
      {
        "id": "1",
        "name": "微信支付宝同步",
        "description": "定期同步微信和支付宝的联系人信息",
        "type": "sync",
        "status": "active",
        "sourceAccount": {
          "id": "1",
          "name": "微信账户",
          "platform": "wechat"
        },
        "targetAccount": {
          "id": "2",
          "name": "支付宝账户",
          "platform": "alipay"
        },
        "config": {
          "executionFrequency": "daily",
          "retentionPolicy": "90days",
          "syncRules": ["realTime", "bidirectional"],
          "dataTypes": ["contacts", "profile"],
          "notifications": {
            "success": true,
            "failure": true,
            "conflict": true
          }
        },
        "stats": {
          "executionCount": 25,
          "successCount": 23,
          "errorCount": 2,
          "lastExecutionAt": "2024-01-01T08:00:00Z",
          "averageDuration": 45
        },
        "createdAt": "2024-01-01T10:00:00Z",
        "updatedAt": "2024-01-01T10:00:00Z"
      }
    ],
    "total": 8,
    "page": 1,
    "size": 10,
    "pages": 1
  },
  "timestamp": 1640995200000
}
```

### 创建共享策略
```http
POST /share-policies
Content-Type: application/json

{
  "name": "微信支付宝同步",
  "description": "定期同步微信和支付宝的联系人信息",
  "type": "sync",
  "sourceAccountId": "1",
  "targetAccountId": "2",
  "config": {
    "executionFrequency": "daily",
    "retentionPolicy": "90days",
    "syncRules": ["realTime", "bidirectional"],
    "dataTypes": ["contacts", "profile"],
    "notifications": {
      "success": true,
      "failure": true,
      "conflict": true
    },
    "conflictResolution": "manual",
    "compressionEnabled": true,
    "encryptionEnabled": false
  }
}
```

#### 响应示例
```json
{
  "code": 201,
  "message": "创建成功",
  "data": {
    "id": "1",
    "name": "微信支付宝同步",
    "description": "定期同步微信和支付宝的联系人信息",
    "type": "sync",
    "status": "active",
    "sourceAccount": {
      "id": "1",
      "name": "微信账户",
      "platform": "wechat"
    },
    "targetAccount": {
      "id": "2",
      "name": "支付宝账户",
      "platform": "alipay"
    },
    "config": {
      "executionFrequency": "daily",
      "retentionPolicy": "90days",
      "syncRules": ["realTime", "bidirectional"],
      "dataTypes": ["contacts", "profile"],
      "notifications": {
        "success": true,
        "failure": true,
        "conflict": true
      }
    },
    "stats": {
      "executionCount": 0,
      "successCount": 0,
      "errorCount": 0,
      "lastExecutionAt": null,
      "averageDuration": 0
    },
    "createdAt": "2024-01-01T10:00:00Z",
    "updatedAt": "2024-01-01T10:00:00Z"
  },
  "timestamp": 1640995200000
}
```

### 获取策略详情
```http
GET /share-policies/{id}
```

#### 响应示例
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "id": "1",
    "name": "微信支付宝同步",
    "description": "定期同步微信和支付宝的联系人信息",
    "type": "sync",
    "status": "active",
    "sourceAccount": {
      "id": "1",
      "name": "微信账户",
      "platform": "wechat"
    },
    "targetAccount": {
      "id": "2",
      "name": "支付宝账户",
      "platform": "alipay"
    },
    "config": {
      "executionFrequency": "daily",
      "retentionPolicy": "90days",
      "syncRules": ["realTime", "bidirectional"],
      "dataTypes": ["contacts", "profile"],
      "notifications": {
        "success": true,
        "failure": true,
        "conflict": true
      },
      "conflictResolution": "manual",
      "compressionEnabled": true,
      "encryptionEnabled": false
    },
    "stats": {
      "executionCount": 25,
      "successCount": 23,
      "errorCount": 2,
      "lastExecutionAt": "2024-01-01T08:00:00Z",
      "averageDuration": 45
    },
    "executions": [
      {
        "id": "exec-1",
        "type": "scheduled",
        "status": "success",
        "startedAt": "2024-01-01T08:00:00Z",
        "duration": 45,
        "dataCount": 150,
        "triggerType": "scheduled"
      }
    ],
    "createdAt": "2024-01-01T10:00:00Z",
    "updatedAt": "2024-01-01T10:00:00Z"
  },
  "timestamp": 1640995200000
}
```

### 更新共享策略
```http
PUT /share-policies/{id}
Content-Type: application/json

{
  "name": "微信支付宝同步-更新",
  "description": "更新的描述",
  "config": {
    "executionFrequency": "hourly",
    "syncRules": ["realTime"]
  }
}
```

### 启用/禁用策略
```http
PATCH /share-policies/{id}/status
Content-Type: application/json

{
  "status": "inactive"
}
```

### 删除策略
```http
DELETE /share-policies/{id}
```

### 立即执行策略
```http
POST /share-policies/{id}/execute
Content-Type: application/json

{
  "syncTypes": ["contacts", "profile"],
  "forceFullSync": false
}
```

#### 响应示例
```json
{
  "code": 200,
  "message": "执行请求已提交",
  "data": {
    "executionId": "exec-123",
    "estimatedDuration": 30,
    "status": "pending"
  },
  "timestamp": 1640995200000
}
```

### 获取策略执行历史
```http
GET /share-policies/{id}/executions
```

#### 查询参数
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认1 |
| size | int | 否 | 每页数量，默认10 |
| status | string | 否 | 执行状态 |
| startDate | string | 否 | 开始日期 (ISO 8601) |
| endDate | string | 否 | 结束日期 (ISO 8601) |

#### 响应示例
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "items": [
      {
        "id": "exec-1",
        "policyId": "1",
        "type": "scheduled",
        "status": "success",
        "startedAt": "2024-01-01T08:00:00Z",
        "endedAt": "2024-01-01T08:00:45Z",
        "duration": 45,
        "dataCount": 150,
        "dataProcessed": {
          "contacts": 120,
          "profile": 30
        },
        "triggerType": "scheduled",
        "triggerData": {
          "scheduledTime": "2024-01-01T08:00:00Z"
        },
        "result": {
          "successCount": 150,
          "errorCount": 0,
          "conflicts": []
        }
      }
    ],
    "total": 25,
    "page": 1,
    "size": 10,
    "pages": 3
  },
  "timestamp": 1640995200000
}
```

## 系统管理

### 健康检查
```http
GET /health
```

#### 响应示例
```json
{
  "code": 200,
  "message": "健康检查通过",
  "data": {
    "status": "UP",
    "components": {
      "db": {
        "status": "UP",
        "details": {
          "database": "MySQL",
          "validationQuery": "SELECT 1"
        }
      },
      "redis": {
        "status": "UP",
        "details": {
          "version": "7.0.0"
        }
      },
      "diskSpace": {
        "status": "UP",
        "details": {
          "total": 107374182400,
          "free": 85899345920,
          "threshold": 10485760
        }
      }
    }
  },
  "timestamp": 1640995200000
}
```

### 获取系统信息
```http
GET /system/info
```

#### 响应示例
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "version": "1.0.0",
    "buildTime": "2024-01-01T10:00:00Z",
    "environment": "development",
    "javaVersion": "17.0.1",
    "springVersion": "3.0.0",
    "uptime": 86400000,
    "features": {
      "wechat": true,
      "alipay": true,
      "weibo": true,
      "qq": false,
      "douyin": false
    },
    "statistics": {
      "totalUsers": 100,
      "totalIdentities": 150,
      "totalAccounts": 300,
      "totalPolicies": 25,
      "activePolicies": 20
    }
  },
  "timestamp": 1640995200000
}
```

### 获取API文档
```http
GET /swagger-ui.html
```

### 获取系统指标
```http
GET /actuator/metrics
```

#### 响应示例
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "names": [
      "jvm.threads.states",
      "jvm.memory.used",
      "http.server.requests",
      "process.files.open",
      "system.cpu.usage"
    ]
  },
  "timestamp": 1640995200000
}
```

### 获取特定指标
```http
GET /actuator/metrics/{metricName}
```

### 获取环境信息
```http
GET /actuator/env
```

## WebSocket接口

### 连接认证
WebSocket连接需要携带JWT Token进行认证：
```javascript
const socket = new WebSocket('ws://localhost:8080/ws?token=your-jwt-token');
```

### 消息格式
```json
{
  "type": "SYNC_STATUS",
  "data": {
    "policyId": "1",
    "status": "running",
    "progress": 50,
    "message": "正在同步联系人..."
  },
  "timestamp": 1640995200000
}
```

### 消息类型
| 类型 | 说明 |
|------|------|
| SYNC_STATUS | 同步状态更新 |
| POLICY_STATUS | 策略状态更新 |
| SYSTEM_ALERT | 系统告警 |
| NOTIFICATION | 通知消息 |

## SDK使用示例

### JavaScript/TypeScript
```typescript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
  timeout: 10000
});

// 请求拦截器
api.interceptors.request.use(config => {
  const token = localStorage.getItem('jwt_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 响应拦截器
api.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response?.status === 401) {
      // 处理认证失败
      localStorage.removeItem('jwt_token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// 使用示例
const identityService = {
  async getIdentities(params = {}) {
    return api.get('/identities', { params });
  },
  
  async createIdentity(data) {
    return api.post('/identities', data);
  },
  
  async updateIdentity(id, data) {
    return api.put(`/identities/${id}`, data);
  },
  
  async deleteIdentity(id) {
    return api.delete(`/identities/${id}`);
  }
};
```

### Python
```python
import requests
import json

class AutoMePolitAPI:
    def __init__(self, base_url, token=None):
        self.base_url = base_url
        self.session = requests.Session()
        if token:
            self.session.headers.update({'Authorization': f'Bearer {token}'})
    
    def get_identities(self, **params):
        response = self.session.get(f'{self.base_url}/identities', params=params)
        response.raise_for_status()
        return response.json()
    
    def create_identity(self, data):
        response = self.session.post(f'{self.base_url}/identities', json=data)
        response.raise_for_status()
        return response.json()

# 使用示例
api = AutoMePolitAPI('http://localhost:8080/api/v1', token='your-jwt-token')
identities = api.get_identities(page=1, size=10)
```

### cURL
```bash
# 获取身份列表
curl -X GET "http://localhost:8080/api/v1/identities" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json"

# 创建身份
curl -X POST "http://localhost:8080/api/v1/identities" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "我的微信",
    "description": "主要使用的微信账户",
    "platform": "wechat"
  }'
```

## 注意事项

1. **频率限制**: API对每个IP的请求频率有限制，默认为60次/分钟
2. **数据验证**: 所有请求参数都会进行严格验证
3. **日志记录**: 所有API调用都会被记录到访问日志中
4. **缓存策略**: GET请求会进行适当的缓存，缓存时间根据具体接口而定
5. **幂等性**: PUT、DELETE请求保证幂等性，多次调用不会产生副作用

## 更新日志

### v1.0.0 (2024-01-01)
- 初始版本发布
- 实现身份管理功能
- 实现账户关联功能
- 实现共享策略功能
- 实现基础系统管理功能

---

如有问题或建议，请联系技术支持团队或查阅项目README.md。