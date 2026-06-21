# IoT Platform - 物联网平台

基于 Spring Boot + Vue 3 的物联网平台，模拟企业级IoT平台核心功能。

## 技术栈

### 后端
- Spring Boot 2.7.18
- Spring Data JPA
- Moquette MQTT Broker (嵌入式)
- Eclipse Paho MQTT Client
- JWT认证
- Swagger API文档

### 前端
- Vue 3 + Vite
- Element Plus
- ECharts
- Axios

## 快速开始

### 环境要求
- JDK 1.8+
- Maven 3.6+
- Node.js 16+ (前端开发)

### 后端启动

1. 进入后端目录
```bash
cd Java-backend
```

2. 使用Maven打包
```bash
mvn clean package -DskipTests
```

3. 运行项目
```bash
java -jar target/iot-platform-1.0.0.jar
```

或者直接在IDE中运行 `IotPlatformApplication.java`

后端启动后：
- API服务: http://localhost:8080/api
- API文档: http://localhost:8080/api/swagger-ui/
- MQTT Broker: tcp://localhost:1883
- MQTT WebSocket: ws://localhost:8083/mqtt
- H2控制台: http://localhost:8080/api/h2-console

### 前端启动

1. 进入前端目录
```bash
cd Vue-frontend
```

2. 安装依赖
```bash
npm install
```

3. 启动开发服务器
```bash
npm run dev
```

前端访问地址: http://localhost:5173

### 默认账号
- 用户名: admin
- 密码: admin123

## 功能模块

### 1. 用户认证
- JWT Token认证
- 登录/退出

### 2. 产品管理
- 产品CRUD
- 物模型管理（属性/服务/事件）

### 3. 设备管理
- 设备注册/删除
- 设备详情查看
- 在线状态监控

### 4. MQTT设备接入
- 嵌入式Moquette Broker
- 设备认证
- 主题规范

### 5. 设备影子
- reported/desired/delta
- 版本号并发控制

### 6. 命令下发
- 通过MQTT下发命令
- 命令响应处理

### 7. 告警管理
- 告警规则配置
- 告警记录查询
- 告警确认

### 8. 统计看板
- 设备统计
- 产品分布图表
- 上下线记录

## MQTT主题规范

| 主题 | 用途 |
|------|------|
| `iot/{productId}/{deviceId}/telemetry` | 设备上报属性 |
| `iot/{productId}/{deviceId}/event` | 设备上报事件 |
| `iot/{productId}/{deviceId}/command/{commandId}` | 平台下发命令 |
| `iot/{productId}/{deviceId}/command/response` | 设备返回命令结果 |

## 项目结构

```
IoT Platform/
├── Java-backend/          # 后端Spring Boot项目
│   ├── src/
│   │   └── main/
│   │       ├── java/com/iot/platform/
│   │       │   ├── config/        # 配置类
│   │       │   ├── controller/    # 控制器
│   │       │   ├── dto/           # 数据传输对象
│   │       │   ├── entity/        # 实体类
│   │       │   ├── exception/     # 异常处理
│   │       │   ├── mqtt/          # MQTT相关
│   │       │   ├── repository/    # 数据访问层
│   │       │   ├── security/      # 安全认证
│   │       │   ├── service/       # 业务逻辑层
│   │       │   └── websocket/     # WebSocket
│   │       └── resources/
│   │           ├── application.yml
│   │           ├── data.sql       # 初始数据
│   │           └── schema.sql     # 建表脚本
│   └── pom.xml
│
├── Vue-frontend/          # 前端Vue项目
│   ├── src/
│   │   ├── api/           # API接口
│   │   ├── components/    # 组件
│   │   ├── router/        # 路由
│   │   ├── utils/         # 工具类
│   │   └── views/         # 页面
│   ├── package.json
│   └── vite.config.js
│
└── README.md
```

## 使用MySQL数据库

1. 执行 `schema.sql` 创建数据库和表

2. 修改 `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/iot_platform?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8mb4
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: your_password
  jpa:
    database-platform: org.hibernate.dialect.MySQL8Dialect
    hibernate:
      ddl-auto: none
```

3. 执行 `data.sql` 插入初始数据

## 许可证

本项目仅供学习使用
