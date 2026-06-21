# 综合项目实训大作业：物联网平台开发（Spring Boot + Web前端）

## 一、项目背景

物联网（IoT）技术正深刻改变着人与物理世界的交互方式，主流物联网平台如华为IoTDA、AWS IoT Core、阿里云IoT等，不仅提供海量设备接入能力，更具备产品模型管理、设备生命周期管理、双向消息通信、远程配置下发等企业级能力。这些平台以**产品（Product）** 为模型抽象，以**设备（Device）** 为接入实体，通过**MQTT协议**实现设备与云端的高效、可靠通信。

本实训项目要求同学们模拟一个完整的企业级物联网平台，实现产品管理、设备注册与鉴权、MQTT消息处理、设备上下线监控、设备影子与远程配置下发等核心功能。通过本项目的开发，深入理解主流IoT平台的技术架构与业务逻辑，掌握全栈开发技能。

## 二、项目目标

1. **理解主流IoT平台的业务模型**：产品与设备的层级关系、物模型定义、设备生命周期管理。
2. **掌握MQTT协议在IoT场景中的应用**：客户端认证、主题订阅与发布、消息处理、设备上下线事件监听。
3. **掌握Spring Boot集成MQTT**：实现嵌入式MQTT Broker（Moquette）或集成外部Broker。
4. **掌握设备影子（Device Shadow）机制**：实现设备状态的云端存储与同步、异步配置下发。
5. **培养系统架构设计能力**：分层架构、事件驱动、消息队列等设计模式。
6. **掌握前后端分离开发与集成**：RESTful API设计、WebSocket实时推送。

## 三、需求描述

### 3.1 业务角色

- **平台管理员（唯一角色）** ：登录后可进行产品管理、设备管理、查看设备数据、下发控制命令、配置设备影子、查看上下线记录与报警信息。

### 3.2 功能需求

#### 3.2.1 用户认证（基础）

- 管理员登录页面：用户名/密码验证（可硬编码或从数据库读取）。
- 使用JWT进行身份认证与授权，拦截非认证请求。未登录不可访问其他页面。

#### 3.2.2 产品管理

产品（Product）是同一类设备的抽象模型，定义了该类设备的功能集合。产品管理具体包含以下功能：

- **产品列表展示**：以表格形式展示所有产品，字段包括：
  - 产品ID（自动生成）
  - 产品名称
  - 产品类型（如温湿度传感器、智能灯、烟雾报警器等）
  - 创建时间
  - 设备数量（统计该产品下注册的设备总数）
- **创建产品**：填写产品名称、选择产品类型，系统自动生成产品ID。
- **编辑产品**：修改产品名称。
- **删除产品**：物理删除产品及其关联的所有设备和数据（注意：删除前需确认）。
- **物模型管理（核心功能）** ：
  - 物模型以属性（Property）、服务（Service）、事件（Event）三个维度描述产品-。
  - **属性**：定义设备的可读/写数据项，如温度（temperature，double类型，范围-20~50）、湿度（humidity，int类型，范围0~100）、开关状态（switch，boolean类型）等，可配置读取权限（只读/读写）。
  - **服务**：定义设备可被远程调用的指令，如“重启设备”、“调整亮度”、“校准传感器”等。
  - **事件**：定义设备主动上报的异常或通知事件，如“高温告警”、“低电量告警”等。
  - 提供物模型的可视化配置界面（前端表单），支持增删改查操作。
  - 物模型数据持久化存储（可设计为多表关联：product、thing_property、thing_service、thing_event）。

#### 3.2.3 设备管理

设备归属于某个产品，并继承该产品的所有物模型定义。设备管理具体包含以下功能：

- **设备列表展示**：以表格形式展示所有设备，字段包括：
  - 设备ID（deviceId，全局唯一，平台分配或自定义生成）
  - 设备名称（设备标识符或用户自定义名称）
  - 所属产品名称
  - 在线状态（在线/离线）
  - 最后上线时间
  - 注册时间
- **注册设备**：选择所属产品，填写设备标识码（nodeId，如IMEI/MAC/序列号），系统生成设备ID。
  - 自动为该设备生成MQTT连接凭证（用户名/密码或ClientId+密钥）。
- **编辑设备**：修改设备名称。
- **删除设备**：物理删除设备及其所有历史数据。
- **查看设备详情**：展示设备的完整信息，包括：
  - 基本信息：设备ID、设备标识码、所属产品、在线状态、最后上线时间、激活时间。
  - **物模型数据**：展示设备最近一次上报的各属性值（即设备影子中的reported区）。
  - **操作日志**：展示设备的历史上下线记录（每次连接/断开的时间）。
  - **命令记录**：展示平台向设备下发的历史命令及执行结果。

#### 3.2.4 MQTT协议与设备接入

基于MQTT协议实现设备与平台的双向通信，具体包含：

- **MQTT Broker选择**：推荐使用嵌入式Moquette Broker直接集成到Spring Boot项目中-。Moquette是用Java基于Netty实现的轻量级MQTT Broker，可无缝集成、支持拦截器扩展消息持久化和认证授权逻辑，单机最低可支持2万+并发连接-。也可以选择部署独立的EMQX Broker。
- **设备鉴权机制**：平台需实现自定义MQTT认证逻辑，设备使用“设备ID+密钥”在连接时进行身份验证。
- **主题设计**：定义标准化的MQTT主题规范：
  - `iot/{productId}/{deviceId}/telemetry`：设备上报属性数据。
  - `iot/{productId}/{deviceId}/event`：设备上报事件。
  - `iot/{productId}/{deviceId}/command/{commandId}`：平台下发命令。
  - `iot/{productId}/{deviceId}/command/response`：设备返回命令执行结果。
  - `$SYS/brokers/+/clients/+/connected/disconnected`：设备上下线系统主题（用于监控）。
- **设备模拟器**：开发一个简易的MQTT设备模拟器（用Java/Python脚本或前端输入框模拟），用于发送MQTT消息进行功能测试。
  - 支持配置目标Broker地址、设备凭证。
  - 支持按照物模型定义，模拟上报属性数据（自动生成符合产品模型中属性数据类型和范围的值）。
  - 支持接收平台下发的命令并模拟返回执行结果。

#### 3.2.5 实时监控与数据展示

- **设备列表实时状态**：在设备列表页展示各设备的在线状态（实时更新，可通过轮询或WebSocket推送）。
- **设备最新数据看板**：在一个汇总看板上展示所有在线设备的最新上报数据（按照物模型属性展示）。
- **设备上下线记录**：
  - 记录每次设备连接（上线）和断开（下线）的时间戳。
  - 在设备详情页展示历史上下线记录。
  - 提供一个全局的上下线记录列表（展示所有设备最近的上/下线事件，按时间倒序排列）。

#### 3.2.6 设备影子

设备影子（Device Shadow）是一个用于存储设备当前状态信息的JSON文档，用于解决设备离线时的控制同步问题。请实现设备影子机制：

- **影子数据结构**：为每个设备维护一个影子JSON文档，包含以下区域：
  - `reported`：设备实际上报的最新属性值。
  - `desired`：平台期望设备达到的属性值（管理员通过前端设置）。
  - `delta`：desired与reported的差异项（系统自动计算，用于下发）。
- **影子功能**：
  - 设备上报属性时，自动更新reported区。
  - 管理员可在前端“设备影子”页面查看和修改desired值。
  - 设备上线时，平台自动比较desired与reported的差异，将差异部分（delta）下发给设备。
  - 设备收到delta后更新自身状态，并重新上报属性，使reported与desired对齐。
- **并发控制**：影子文档携带版本号，用于防止分布式环境下的状态回退。

#### 3.2.7 远程命令下发

- 管理员可在设备详情页面向指定设备下发命令（命令定义来自产品的服务模型）。
- 命令下发方式：平台通过MQTT将命令发布到设备的命令主题。
- 设备接收到命令后执行，并通过响应主题返回执行结果（成功/失败及结果参数）。
- 前端展示命令下发记录及执行状态。

#### 3.2.8 规则引擎与告警（可选加分项）

- **规则配置**：管理员可为设备的属性值配置告警规则（如温度 > 30℃ 触发告警）。
- **规则引擎**：当设备上报数据时，实时触发规则匹配。推荐使用Drools规则引擎实现。
- **告警记录**：展示所有触发告警的记录，包含告警时间、设备名称、告警内容、是否已处理。
- **告警确认**：管理员可标记告警为已处理。

#### 3.2.9 统计看板（可选加分项）

- 仪表盘统计：总产品数、总设备数、在线设备数、今日告警数。
- 按产品类型统计设备数量的饼图。
- 近7天设备上下线趋势折线图。

### 3.3 非功能需求

1. **数据持久化**：使用MySQL存储产品信息、设备信息、上下线记录、命令记录、告警记录、设备影子数据；设备历史属性数据可用MySQL存储，也可扩展使用InfluxDB等时序数据库。
2. **代码规范**：后端遵循Restful风格，分层清晰（Controller/Service/Repository）；前端代码结构合理，命名语义化。
3. **异常处理**：前后端均需有完善的异常提示与错误码。
4. **时间完整性**：所有时间字段采用`LocalDateTime`。
5. **API文档**：推荐集成Swagger/OpenAPI，便于接口调试。

### 3.4 数据库设计参考

```
-- 产品表
product (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    type VARCHAR(30),
    created_time DATETIME
)

-- 物模型属性表
thing_property (
    id VARCHAR(64) PRIMARY KEY,
    product_id VARCHAR(64) NOT NULL,
    property_name VARCHAR(50),
    data_type VARCHAR(20),     -- int, double, boolean, string
    min_value VARCHAR(50),
    max_value VARCHAR(50),
    unit VARCHAR(20),
    access_mode VARCHAR(10),   -- read, write, read_write
    FOREIGN KEY (product_id) REFERENCES product(id)
)

-- 物模型服务表
thing_service (
    id VARCHAR(64) PRIMARY KEY,
    product_id VARCHAR(64) NOT NULL,
    service_name VARCHAR(50),
    input_params TEXT,         -- JSON格式
    output_params TEXT,        -- JSON格式
    FOREIGN KEY (product_id) REFERENCES product(id)
)

-- 物模型事件表
thing_event (
    id VARCHAR(64) PRIMARY KEY,
    product_id VARCHAR(64) NOT NULL,
    event_name VARCHAR(50),
    event_type VARCHAR(30),
    description TEXT,
    FOREIGN KEY (product_id) REFERENCES product(id)
)

-- 设备表
device (
    device_id VARCHAR(64) PRIMARY KEY,
    product_id VARCHAR(64) NOT NULL,
    node_id VARCHAR(64) UNIQUE NOT NULL,      -- 设备唯一标识码
    device_name VARCHAR(50),
    secret VARCHAR(128),                       -- 连接密钥
    status VARCHAR(10),                        -- online, offline
    last_online_time DATETIME,
    created_time DATETIME,
    FOREIGN KEY (product_id) REFERENCES product(id)
)

-- 设备影子表（JSON存储）
device_shadow (
    device_id VARCHAR(64) PRIMARY KEY,
    shadow_data JSON,                          -- 包含reported/desired/delta/version
    updated_time DATETIME,
    FOREIGN KEY (device_id) REFERENCES device(device_id)
)

-- 设备上下线记录表
device_connect_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id VARCHAR(64),
    event_type VARCHAR(10),                    -- online / offline
    event_time DATETIME,
    FOREIGN KEY (device_id) REFERENCES device(device_id)
)

-- 命令记录表
command_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id VARCHAR(64),
    command_name VARCHAR(50),
    command_params TEXT,
    status VARCHAR(20),                        -- pending, success, failed
    result TEXT,
    request_time DATETIME,
    response_time DATETIME
)

-- 告警规则表
alert_rule (
    id VARCHAR(64) PRIMARY KEY,
    product_id VARCHAR(64),
    property_name VARCHAR(50),
    condition_expr VARCHAR(255),               -- 如 "> 35"
    alert_content TEXT,
    enabled BOOLEAN DEFAULT TRUE
)

-- 告警记录表
alert_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id VARCHAR(64),
    rule_id VARCHAR(64),
    alert_content TEXT,
    alert_time DATETIME,
    status VARCHAR(20),                        -- pending, confirmed
    confirmed_time DATETIME,
    remark VARCHAR(255)
)
```

**说明**：以上表结构为推荐方案，可根据实现简化调整。关键约束包括：`device.node_id` 全局唯一，`product` 与 `device` 之间为一对多关系，`device_shadow` 与 `device` 为一对一关系。

## 四、技术栈要求

| 层次        | 技术选型（推荐）                       | 备选                                    |
| :---------- | :------------------------------------- | :-------------------------------------- |
| 后端框架    | Spring Boot 2.x / 3.x                  | -                                       |
| MQTT Broker | Moquette（嵌入式） + 认证拦截器        | EMQX（独立部署）+ Java Client           |
| MQTT客户端  | Eclipse Paho MQTT Client（用于模拟器） | -                                       |
| 持久层      | Spring Data JPA 或 MyBatis             | JDBC + 原生SQL                          |
| 数据库      | MySQL 5.7+                             | H2嵌入式测试                            |
| 规则引擎    | Drools                                 | 简单if条件判断                          |
| 实时通信    | WebSocket（消息推送）+ STOMP           | 前端轮询                                |
| 前端框架    | Vue 3 + Element Plus                   | React + Ant Design / 原生JS + Bootstrap |
| 图表库      | ECharts                                | Chart.js                                |
| 构建工具    | Maven                                  | Gradle                                  |
| 接口文档    | SpringDoc OpenAPI (Swagger UI)         | 手动编写                                |

## 五、任务分解与开发计划（10人·日）

| 天数    | 任务内容                                                     | 输出物                                           |
| :------ | :----------------------------------------------------------- | :----------------------------------------------- |
| 第1天   | 需求分析、系统架构设计、数据库设计、搭建Spring Boot项目骨架、配置数据源、创建实体类及Repository层，完成MQTT Broker集成（Moquette嵌入式）。 | 数据库ER图、项目骨架可启动、MQTT服务可监听       |
| 第2-3天 | 实现产品管理模块（增删改查）、物模型管理（属性/服务/事件的CRUD）、设备管理模块（注册、列表、删除）。 | 产品API + 设备API完成，通过Postman测试           |
| 第4-5天 | 实现MQTT设备接入与认证逻辑（自定义认证拦截器）、设备上下线监听与记录、设备影子核心逻辑（影子创建、更新、delta计算）。 | 设备可成功连接Broker并认证、影子机制初步可用     |
| 第6-7天 | 实现设备上报数据的接收与处理（按主题解析消息）、数据持久化、规则引擎与告警逻辑、命令下发与响应处理API。 | 设备上报数据可正确入库，告警可触发，命令下发可用 |
| 第8天   | 前端框架搭建，完成登录页、产品管理页、设备管理页、设备详情页（含影子配置）、实时看板页，与后端API对接。 | 前端主要页面可完成核心业务流程                   |
| 第9天   | 设备模拟器开发（MQTT客户端模拟发送数据）、WebSocket实时推送集成（设备状态、最新数据推前端）、异常处理、功能完善与优化。 | 端到端流程完整演示                               |
| 第10天  | 系统联调、修复Bug、撰写项目文档、准备演示PPT或演示视频，完成项目提交。 | 可运行系统 + 完整文档                            |

## 六、评分标准（总分100分）

| 评分项                   | 分值 | 说明                                                         |
| :----------------------- | :--- | :----------------------------------------------------------- |
| **核心功能完成度**       | 45分 |                                                              |
| 产品管理与物模型         | 10分 | 产品的增删改查、物模型（属性/服务/事件）的完整CRUD及可视化管理 |
| 设备管理                 | 10分 | 设备注册、删除、详情查看、上下线状态实时展示                 |
| MQTT设备接入与认证       | 10分 | 设备成功连接Broker、认证机制有效、上下线记录完整             |
| 设备影子                 | 8分  | reported/desired/delta概念实现正确，离线配置同步机制可用     |
| 命令下发与响应           | 7分  | 命令下发、接收、响应、记录完整                               |
| **代码质量与规范**       | 10分 | 后端分层结构清晰、前端代码规范、命名语义化、注释适当         |
| **数据库设计**           | 8分  | 表关系合理、字段规范、满足查询需求、影子JSON设计合理         |
| **前端交互体验**         | 8分  | 页面布局专业、数据实时刷新、操作流畅、友好的提示反馈         |
| **系统稳定性与异常处理** | 7分  | 无致命Bug、输入校验完整、网络异常/后端错误有友好提示         |
| **创新与拓展（加分项）** | 12分 | 实现规则引擎、时序数据库存储、WebSocket实时推送、统计看板、设备分组等额外功能 |
| **文档与提交物**         | 10分 | 项目说明书（含部署步骤）、API文档、演示视频/PPT、数据库ER图完整 |

## 七、提交物要求

1. **源代码**：后端工程（含SQL初始化脚本）和前端工程，压缩包命名：`学号_姓名_IoTPlatform`。
2. **项目运行说明书**（PDF或Markdown）：
   - 开发环境说明（JDK版本、MySQL版本、IDE等）
   - 数据库初始化步骤（含建表SQL）
   - 后端启动命令
   - MQTT Broker的启动说明（若使用独立Broker）
   - 前端访问地址及测试账号
   - 设备模拟器使用说明（含示例命令）
3. **演示视频**（3~5分钟）：展示完整操作流程（产品创建 → 注册设备 → 设备模拟器连接 → 数据上报 → 影子配置 → 命令下发 → 告警触发）。
4. **数据库ER图**（截图或PDF）。
5. **API接口文档**（可使用Swagger自动生成或手写Markdown）。

## 八、关键技术实现提示

### 8.1 MQTT Broker集成（以Moquette嵌入式为例）

```
<!-- pom.xml 依赖 -->
<dependency>
    <groupId>io.moquette</groupId>
    <artifactId>moquette-broker</artifactId>
    <version>0.17</version>
</dependency>
```

```
@Configuration
public class MoquetteConfig {
    @Bean(destroyMethod = "stopServer")
    public Server mqttBroker() throws IOException {
        Properties props = new Properties();
        props.put(BrokerConstants.HOST_PROPERTY_NAME, "0.0.0.0");
        props.put(BrokerConstants.PORT_PROPERTY_NAME, "1883");
        // 配置WebSocket支持
        props.put("websocket_port", "8083");
        props.put("websocket_path", "/mqtt");
        
        IConfig config = new MemoryConfig(props);
        Server server = new Server();
        server.startServer(config);
        return server;
    }
}
```



集成后需实现自定义认证拦截器，在设备MQTT连接时验证设备ID与密钥的合法性。Moquette支持通过SpringAwareAuthenticator实现业务认证逻辑的自定义扩展-。

### 8.2 设备影子JSON结构示例

```
{
  "deviceId": "device001",
  "version": 5,
  "state": {
    "reported": {
      "temperature": 23.5,
      "humidity": 65,
      "switch": true
    },
    "desired": {
      "temperature": 25.0,
      "switch": true
    }
  },
  "delta": {
    "temperature": 25.0
  }
}
```

### 8.3 设备上下线监听

利用MQTT Broker的拦截器机制，在客户端连接（CONNECT）和断开（DISCONNECT）时记录事件并更新设备状态。Moquette提供了InterceptHandler接口，可实现`onConnect`和`onDisconnect`方法。

### 8.4 规则引擎（可选简化方案）

在设备上报数据的处理链路中，根据设备所属产品的告警规则进行条件判断，符合条件则插入告警记录。如需更专业的实现，可集成Drools规则引擎，实现复杂事件处理。

## 九、注意事项

1. **MQTT Broker选型**：推荐直接使用嵌入式Moquette，无需单独部署外部服务，项目启动即可用，便于调试和部署。如选用EMQX，需额外提供启动和配置说明。
2. **前后端跨域**：Spring Boot后端需要配置CORS过滤器允许前端跨域访问。
3. **WebSocket实时推送**：设备上下线、最新数据变化可通过WebSocket推送到前端，避免频繁轮询。前端可使用MQTT over WebSocket直接订阅主题进行实时数据展示。
4. **物模型处理**：设备上报的属性数据需与产品物模型中定义的属性进行校验（数据类型、范围等），不符合则丢弃并记录异常日志。
5. **安全性**：敏感信息（如设备密钥）需加密存储，MQTT连接建议启用TLS（选做）。
6. **不要过度复杂**：核心目标是理解主流IoT平台的工作机制，实现完整流程即可。高并发、分布式等高阶特性非本实训考察重点。

## 十、核心业务流程图

```
┌─────────────┐    1.创建产品+定义物模型    ┌─────────────┐
│             │ ─────────────────────────▶ │             │
│  平台管理员  │                           │   产品模型   │
│  (Web前端)   │ ◀───────────────────────── │  (数据库)    │
│             │    2.注册设备+获取凭证      └─────────────┘
└─────────────┘                                   │
       │                                           │ 继承
       │ 8.查看设备影子/下发命令                    ▼
       │                                   ┌─────────────┐
       │                                   │    设备     │
       │                                   │  (数据库)    │
       │                                   └─────────────┘
       │                                           │
       │                                           │ 3.MQTT连接
       │                                           │ (设备ID+密钥认证)
       │                                           ▼
       │                                   ┌─────────────┐
       │                                   │   MQTT      │
       │                                   │   Broker    │
       │                                   │  (Moquette) │
       │                                   └─────────────┘
       │                                           │
       │ 7.影子同步/命令下发  ┌─────────────┐      │ 4.上报属性/事件
       └───────────────────▶│   IoT平台   │◀─────┘
                            │  (SpringBoot)│
                            └─────────────┘
                                  │     │
                                  │     └──▶ 5.规则引擎 → 告警记录
                                  ▼
                            6.实时推送到前端
```



## 十一、参考资源

1. 华为云IoT设备接入服务（IoTDA）产品文档（功能模型参考）
2. MQTT协议规范（MQTT 3.1.1/5.0，了解核心概念即可）
3. Moquette GitHub仓库：io.moquette/moquette-broker
4. Eclipse Paho MQTT Client使用指南
5. Vue 3 + Element Plus官方文档
6. ECharts官方文档（图表展示）

## 十二、结语

本大作业对标华为云IoT等主流物联网平台的核心能力，涵盖产品模型、设备管理、MQTT协议接入、设备影子、命令下发、规则引擎等关键模块。通过一周的开发，同学们将深入理解企业级物联网平台的技术架构与业务逻辑，为未来从事IoT方向的开发工作打下坚实基础。

工作量适中但有挑战——建议合理分配时间，优先完成产品管理→设备注册→MQTT连接与认证→数据上报与存储这条核心链路，再逐步丰富影子、命令、告警等进阶功能。前后端可并行开发，提前约定好API接口规范。