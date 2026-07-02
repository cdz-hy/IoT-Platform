# IoT Platform API 接口文档

**Base URL:** `http://localhost:8080/api`

**认证方式:** Bearer Token（JWT）

请求头: `Authorization: Bearer <token>`

---

## 设备影子

### 更新设备desired区

| 项目 | 值 |
|------|-----|
| 方法 | `PUT` |
| 路径 | `/shadows/desired` |
| 说明 | 更新设备desired区 |

**请求体:** `ShadowUpdateRequest`

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 获取设备影子

| 项目 | 值 |
|------|-----|
| 方法 | `GET` |
| 路径 | `/shadows/{deviceId}` |
| 说明 | 获取设备影子 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `deviceId` | path | 是 | string |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 计算设备delta

| 项目 | 值 |
|------|-----|
| 方法 | `GET` |
| 路径 | `/shadows/{deviceId}/delta` |
| 说明 | 计算设备delta |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `deviceId` | path | 是 | string |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

## 产品管理

### 查询产品详情

| 项目 | 值 |
|------|-----|
| 方法 | `GET` |
| 路径 | `/products/{id}` |
| 说明 | 查询产品详情 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `id` | path | 是 | string |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 更新产品

| 项目 | 值 |
|------|-----|
| 方法 | `PUT` |
| 路径 | `/products/{id}` |
| 说明 | 更新产品 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `id` | path | 是 | string |

**请求体:** `Product`

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 删除产品

| 项目 | 值 |
|------|-----|
| 方法 | `DELETE` |
| 路径 | `/products/{id}` |
| 说明 | 删除产品 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `id` | path | 是 | string |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 更新产品服务

| 项目 | 值 |
|------|-----|
| 方法 | `PUT` |
| 路径 | `/products/services/{serviceId}` |
| 说明 | 更新产品服务 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `serviceId` | path | 是 | string |

**请求体:** `ThingService`

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 删除产品服务

| 项目 | 值 |
|------|-----|
| 方法 | `DELETE` |
| 路径 | `/products/services/{serviceId}` |
| 说明 | 删除产品服务 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `serviceId` | path | 是 | string |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 更新产品属性

| 项目 | 值 |
|------|-----|
| 方法 | `PUT` |
| 路径 | `/products/properties/{propertyId}` |
| 说明 | 更新产品属性 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `propertyId` | path | 是 | string |

**请求体:** `ThingProperty`

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 删除产品属性

| 项目 | 值 |
|------|-----|
| 方法 | `DELETE` |
| 路径 | `/products/properties/{propertyId}` |
| 说明 | 删除产品属性 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `propertyId` | path | 是 | string |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 更新产品事件

| 项目 | 值 |
|------|-----|
| 方法 | `PUT` |
| 路径 | `/products/events/{eventId}` |
| 说明 | 更新产品事件 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `eventId` | path | 是 | string |

**请求体:** `ThingEvent`

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 删除产品事件

| 项目 | 值 |
|------|-----|
| 方法 | `DELETE` |
| 路径 | `/products/events/{eventId}` |
| 说明 | 删除产品事件 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `eventId` | path | 是 | string |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 查询所有产品

| 项目 | 值 |
|------|-----|
| 方法 | `GET` |
| 路径 | `/products` |
| 说明 | 查询所有产品 |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 创建产品

| 项目 | 值 |
|------|-----|
| 方法 | `POST` |
| 路径 | `/products` |
| 说明 | 创建产品 |

**请求体:** `Product`

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 查询产品所有服务

| 项目 | 值 |
|------|-----|
| 方法 | `GET` |
| 路径 | `/products/{productId}/services` |
| 说明 | 查询产品所有服务 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `productId` | path | 是 | string |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 添加产品服务

| 项目 | 值 |
|------|-----|
| 方法 | `POST` |
| 路径 | `/products/{productId}/services` |
| 说明 | 添加产品服务 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `productId` | path | 是 | string |

**请求体:** `ThingService`

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 查询产品所有属性

| 项目 | 值 |
|------|-----|
| 方法 | `GET` |
| 路径 | `/products/{productId}/properties` |
| 说明 | 查询产品所有属性 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `productId` | path | 是 | string |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 添加产品属性

| 项目 | 值 |
|------|-----|
| 方法 | `POST` |
| 路径 | `/products/{productId}/properties` |
| 说明 | 添加产品属性 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `productId` | path | 是 | string |

**请求体:** `ThingProperty`

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 查询产品所有事件

| 项目 | 值 |
|------|-----|
| 方法 | `GET` |
| 路径 | `/products/{productId}/events` |
| 说明 | 查询产品所有事件 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `productId` | path | 是 | string |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 添加产品事件

| 项目 | 值 |
|------|-----|
| 方法 | `POST` |
| 路径 | `/products/{productId}/events` |
| 说明 | 添加产品事件 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `productId` | path | 是 | string |

**请求体:** `ThingEvent`

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 统计各类型产品数量

| 项目 | 值 |
|------|-----|
| 方法 | `GET` |
| 路径 | `/products/statistics/type` |
| 说明 | 统计各类型产品数量 |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

## 设备管理

### 查询设备详情

| 项目 | 值 |
|------|-----|
| 方法 | `GET` |
| 路径 | `/devices/{deviceId}` |
| 说明 | 查询设备详情 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `deviceId` | path | 是 | string |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 更新设备

| 项目 | 值 |
|------|-----|
| 方法 | `PUT` |
| 路径 | `/devices/{deviceId}` |
| 说明 | 更新设备 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `deviceId` | path | 是 | string |

**请求体:** `Device`

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 删除设备

| 项目 | 值 |
|------|-----|
| 方法 | `DELETE` |
| 路径 | `/devices/{deviceId}` |
| 说明 | 删除设备 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `deviceId` | path | 是 | string |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 注册设备

| 项目 | 值 |
|------|-----|
| 方法 | `POST` |
| 路径 | `/devices/register` |
| 说明 | 注册设备 |

**请求体:** `DeviceRegisterRequest`

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 下发命令

| 项目 | 值 |
|------|-----|
| 方法 | `POST` |
| 路径 | `/devices/command` |
| 说明 | 下发命令 |

**请求体:** `CommandRequest`

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 查询所有设备

| 项目 | 值 |
|------|-----|
| 方法 | `GET` |
| 路径 | `/devices` |
| 说明 | 查询所有设备 |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 查询设备完整详情（含影子/遥测/日志）

| 项目 | 值 |
|------|-----|
| 方法 | `GET` |
| 路径 | `/devices/{deviceId}/detail` |
| 说明 | 查询设备完整详情（含影子/遥测/日志） |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `deviceId` | path | 是 | string |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 查询设备上下线记录

| 项目 | 值 |
|------|-----|
| 方法 | `GET` |
| 路径 | `/devices/{deviceId}/connect-logs` |
| 说明 | 查询设备上下线记录 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `deviceId` | path | 是 | string |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 查询设备命令记录

| 项目 | 值 |
|------|-----|
| 方法 | `GET` |
| 路径 | `/devices/{deviceId}/commands` |
| 说明 | 查询设备命令记录 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `deviceId` | path | 是 | string |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 统计总设备数量

| 项目 | 值 |
|------|-----|
| 方法 | `GET` |
| 路径 | `/devices/statistics/total` |
| 说明 | 统计总设备数量 |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 统计在线设备数量

| 项目 | 值 |
|------|-----|
| 方法 | `GET` |
| 路径 | `/devices/statistics/online` |
| 说明 | 统计在线设备数量 |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 根据产品ID查询设备

| 项目 | 值 |
|------|-----|
| 方法 | `GET` |
| 路径 | `/devices/product/{productId}` |
| 说明 | 根据产品ID查询设备 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `productId` | path | 是 | string |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 查询最近上下线记录

| 项目 | 值 |
|------|-----|
| 方法 | `GET` |
| 路径 | `/devices/connect-logs/recent` |
| 说明 | 查询最近上下线记录 |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

## 告警管理

### 更新告警规则

| 项目 | 值 |
|------|-----|
| 方法 | `PUT` |
| 路径 | `/alerts/rules/{ruleId}` |
| 说明 | 更新告警规则 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `ruleId` | path | 是 | string |

**请求体:** `AlertRule`

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 删除告警规则

| 项目 | 值 |
|------|-----|
| 方法 | `DELETE` |
| 路径 | `/alerts/rules/{ruleId}` |
| 说明 | 删除告警规则 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `ruleId` | path | 是 | string |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 确认告警

| 项目 | 值 |
|------|-----|
| 方法 | `PUT` |
| 路径 | `/alerts/records/{alertId}/confirm` |
| 说明 | 确认告警 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `alertId` | path | 是 | integer |

**请求体:** JSON对象

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 查询所有告警规则

| 项目 | 值 |
|------|-----|
| 方法 | `GET` |
| 路径 | `/alerts/rules` |
| 说明 | 查询所有告警规则 |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 创建告警规则

| 项目 | 值 |
|------|-----|
| 方法 | `POST` |
| 路径 | `/alerts/rules` |
| 说明 | 创建告警规则 |

**请求体:** `AlertRule`

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 统计今日告警数量

| 项目 | 值 |
|------|-----|
| 方法 | `GET` |
| 路径 | `/alerts/statistics/today` |
| 说明 | 统计今日告警数量 |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 查询产品下的告警规则

| 项目 | 值 |
|------|-----|
| 方法 | `GET` |
| 路径 | `/alerts/rules/product/{productId}` |
| 说明 | 查询产品下的告警规则 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `productId` | path | 是 | string |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 查询告警记录

| 项目 | 值 |
|------|-----|
| 方法 | `GET` |
| 路径 | `/alerts/records` |
| 说明 | 查询告警记录 |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 查询设备告警记录

| 项目 | 值 |
|------|-----|
| 方法 | `GET` |
| 路径 | `/alerts/records/device/{deviceId}` |
| 说明 | 查询设备告警记录 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `deviceId` | path | 是 | string |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

## MQTT接入

### 模拟遥测数据上报

| 项目 | 值 |
|------|-----|
| 方法 | `POST` |
| 路径 | `/mqtt/simulate/{deviceId}/telemetry` |
| 说明 | 模拟遥测数据上报 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `deviceId` | path | 是 | string |

**请求体:** JSON对象

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 模拟设备上线

| 项目 | 值 |
|------|-----|
| 方法 | `POST` |
| 路径 | `/mqtt/simulate/{deviceId}/online` |
| 说明 | 模拟设备上线 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `deviceId` | path | 是 | string |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 模拟设备离线

| 项目 | 值 |
|------|-----|
| 方法 | `POST` |
| 路径 | `/mqtt/simulate/{deviceId}/offline` |
| 说明 | 模拟设备离线 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `deviceId` | path | 是 | string |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 模拟设备事件

| 项目 | 值 |
|------|-----|
| 方法 | `POST` |
| 路径 | `/mqtt/simulate/{deviceId}/event` |
| 说明 | 模拟设备事件 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `deviceId` | path | 是 | string |

**请求体:** JSON对象

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 获取设备MQTT连接信息

| 项目 | 值 |
|------|-----|
| 方法 | `GET` |
| 路径 | `/mqtt/access/{deviceId}` |
| 说明 | 获取设备MQTT连接信息 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `deviceId` | path | 是 | string |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

## 认证管理

### 用户注册

| 项目 | 值 |
|------|-----|
| 方法 | `POST` |
| 路径 | `/auth/register` |
| 说明 | 用户注册 |

**请求体:** `RegisterRequest`

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 管理员登录

| 项目 | 值 |
|------|-----|
| 方法 | `POST` |
| 路径 | `/auth/login` |
| 说明 | 管理员登录 |

**请求体:** `LoginRequest`

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 验证token

| 项目 | 值 |
|------|-----|
| 方法 | `GET` |
| 路径 | `/auth/validate` |
| 说明 | 验证token |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `Authorization` | header | 是 | string |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

## 遥测数据

### 获取设备最新遥测数据

| 项目 | 值 |
|------|-----|
| 方法 | `GET` |
| 路径 | `/telemetry/latest/{deviceId}` |
| 说明 | 获取设备最新遥测数据 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `deviceId` | path | 是 | string |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 获取设备遥测历史

| 项目 | 值 |
|------|-----|
| 方法 | `GET` |
| 路径 | `/telemetry/history/{deviceId}` |
| 说明 | 获取设备遥测历史 |

**路径参数:**

| 参数名 | 位置 | 必填 | 类型 |
|--------|------|------|------|
| `deviceId` | path | 是 | string |
| `limit` | query | 否 | integer |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

## 统计看板

### 获取完整仪表盘数据

| 项目 | 值 |
|------|-----|
| 方法 | `GET` |
| 路径 | `/dashboard/summary` |
| 说明 | 获取完整仪表盘数据 |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 获取统计数据

| 项目 | 值 |
|------|-----|
| 方法 | `GET` |
| 路径 | `/dashboard/statistics` |
| 说明 | 获取统计数据 |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

### 获取产品类型分布

| 项目 | 值 |
|------|-----|
| 方法 | `GET` |
| 路径 | `/dashboard/products/distribution` |
| 说明 | 获取产品类型分布 |

**响应:**

| 状态码 | 说明 |
|--------|------|
| `200` | OK |
| `400` | Bad Request |
| `500` | Internal Server Error |

---

## 数据模型

### ApiResponseVoid

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | integer(int32) |  |
| `message` | string |  |
| `data` | object |  |
| `timestamp` | integer(int64) |  |

### ShadowUpdateRequest

| 字段 | 类型 | 说明 |
|------|------|------|
| `deviceId` | string |  |
| `desired` | object |  |

### Device

| 字段 | 类型 | 说明 |
|------|------|------|
| `deviceId` | string |  |
| `nodeId` | string |  |
| `deviceName` | string |  |
| `location` | string |  |
| `status` | string |  |
| `lastOnlineTime` | string(date-time) |  |
| `activatedTime` | string(date-time) |  |
| `createdTime` | string(date-time) |  |
| `updatedTime` | string(date-time) |  |
| `productName` | string |  |

### Product

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | string |  |
| `name` | string |  |
| `type` | string |  |
| `description` | string |  |
| `createdTime` | string(date-time) |  |
| `updatedTime` | string(date-time) |  |
| `properties` | array |  |
| `services` | array |  |
| `events` | array |  |
| `devices` | array |  |

### ThingEvent

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | string |  |
| `name` | string |  |
| `identifier` | string |  |
| `eventType` | string |  |
| `description` | string |  |
| `createdTime` | string(date-time) |  |
| `updatedTime` | string(date-time) |  |

### ThingProperty

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | string |  |
| `name` | string |  |
| `identifier` | string |  |
| `dataType` | string |  |
| `minValue` | string |  |
| `maxValue` | string |  |
| `unit` | string |  |
| `accessMode` | string |  |
| `createdTime` | string(date-time) |  |
| `updatedTime` | string(date-time) |  |

### ThingService

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | string |  |
| `name` | string |  |
| `identifier` | string |  |
| `inputParams` | string |  |
| `outputParams` | string |  |
| `description` | string |  |
| `createdTime` | string(date-time) |  |
| `updatedTime` | string(date-time) |  |

### ApiResponseProduct

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | integer(int32) |  |
| `message` | string |  |
| `data` | object |  |
| `timestamp` | integer(int64) |  |

### ApiResponseThingService

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | integer(int32) |  |
| `message` | string |  |
| `data` | object |  |
| `timestamp` | integer(int64) |  |

### ApiResponseThingProperty

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | integer(int32) |  |
| `message` | string |  |
| `data` | object |  |
| `timestamp` | integer(int64) |  |

### ApiResponseThingEvent

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | integer(int32) |  |
| `message` | string |  |
| `data` | object |  |
| `timestamp` | integer(int64) |  |

### ApiResponseDevice

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | integer(int32) |  |
| `message` | string |  |
| `data` | object |  |
| `timestamp` | integer(int64) |  |

### AlertRule

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | string |  |
| `productId` | string |  |
| `propertyIdentifier` | string |  |
| `operator` | string |  |
| `threshold` | string |  |
| `alertContent` | string |  |
| `enabled` | boolean |  |
| `createdTime` | string(date-time) |  |
| `updatedTime` | string(date-time) |  |

### ApiResponseAlertRule

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | integer(int32) |  |
| `message` | string |  |
| `data` | object |  |
| `timestamp` | integer(int64) |  |

### DeviceRegisterRequest

| 字段 | 类型 | 说明 |
|------|------|------|
| `productId` | string |  |
| `nodeId` | string |  |
| `deviceName` | string |  |

### CommandRequest

| 字段 | 类型 | 说明 |
|------|------|------|
| `deviceId` | string |  |
| `commandName` | string |  |
| `commandParams` | string |  |

### ApiResponseCommandLog

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | integer(int32) |  |
| `message` | string |  |
| `data` | object |  |
| `timestamp` | integer(int64) |  |

### CommandLog

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | integer(int64) |  |
| `commandId` | string |  |
| `deviceId` | string |  |
| `serviceIdentifier` | string |  |
| `commandName` | string |  |
| `commandParams` | string |  |
| `status` | string |  |
| `result` | string |  |
| `requestTime` | string(date-time) |  |
| `responseTime` | string(date-time) |  |

### RegisterRequest

| 字段 | 类型 | 说明 |
|------|------|------|
| `username` | string |  |
| `password` | string |  |
| `nickname` | string |  |

### LoginRequest

| 字段 | 类型 | 说明 |
|------|------|------|
| `username` | string |  |
| `password` | string |  |

### ApiResponseMapStringString

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | integer(int32) |  |
| `message` | string |  |
| `data` | object |  |
| `timestamp` | integer(int64) |  |

### ApiResponseTelemetryLatest

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | integer(int32) |  |
| `message` | string |  |
| `data` | object |  |
| `timestamp` | integer(int64) |  |

### TelemetryLatest

| 字段 | 类型 | 说明 |
|------|------|------|
| `deviceId` | string |  |
| `productId` | string |  |
| `payload` | string |  |
| `reportedTime` | string(date-time) |  |

### ApiResponseListTelemetryRecord

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | integer(int32) |  |
| `message` | string |  |
| `data` | array |  |
| `timestamp` | integer(int64) |  |

### TelemetryRecord

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | integer(int64) |  |
| `deviceId` | string |  |
| `productId` | string |  |
| `payload` | string |  |
| `reportedTime` | string(date-time) |  |

### ApiResponseDeviceShadow

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | integer(int32) |  |
| `message` | string |  |
| `data` | object |  |
| `timestamp` | integer(int64) |  |

### DeviceShadow

| 字段 | 类型 | 说明 |
|------|------|------|
| `deviceId` | string |  |
| `shadowData` | string |  |
| `version` | integer(int64) |  |
| `updatedTime` | string(date-time) |  |

### ApiResponseMapStringObject

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | integer(int32) |  |
| `message` | string |  |
| `data` | object |  |
| `timestamp` | integer(int64) |  |

### ApiResponseListProduct

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | integer(int32) |  |
| `message` | string |  |
| `data` | array |  |
| `timestamp` | integer(int64) |  |

### ApiResponseListThingService

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | integer(int32) |  |
| `message` | string |  |
| `data` | array |  |
| `timestamp` | integer(int64) |  |

### ApiResponseListThingProperty

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | integer(int32) |  |
| `message` | string |  |
| `data` | array |  |
| `timestamp` | integer(int64) |  |

### ApiResponseListThingEvent

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | integer(int32) |  |
| `message` | string |  |
| `data` | array |  |
| `timestamp` | integer(int64) |  |

### ApiResponseMapStringLong

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | integer(int32) |  |
| `message` | string |  |
| `data` | object |  |
| `timestamp` | integer(int64) |  |

### ApiResponseListDevice

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | integer(int32) |  |
| `message` | string |  |
| `data` | array |  |
| `timestamp` | integer(int64) |  |

### ApiResponseListDeviceConnectLog

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | integer(int32) |  |
| `message` | string |  |
| `data` | array |  |
| `timestamp` | integer(int64) |  |

### DeviceConnectLog

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | integer(int64) |  |
| `deviceId` | string |  |
| `eventType` | string |  |
| `eventTime` | string(date-time) |  |
| `clientId` | string |  |
| `reason` | string |  |

### ApiResponseListCommandLog

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | integer(int32) |  |
| `message` | string |  |
| `data` | array |  |
| `timestamp` | integer(int64) |  |

### ApiResponseLong

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | integer(int32) |  |
| `message` | string |  |
| `data` | integer(int64) |  |
| `timestamp` | integer(int64) |  |

### ApiResponseBoolean

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | integer(int32) |  |
| `message` | string |  |
| `data` | boolean |  |
| `timestamp` | integer(int64) |  |

### ApiResponseListAlertRule

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | integer(int32) |  |
| `message` | string |  |
| `data` | array |  |
| `timestamp` | integer(int64) |  |

### AlertRecord

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | integer(int64) |  |
| `deviceId` | string |  |
| `productId` | string |  |
| `ruleId` | string |  |
| `propertyIdentifier` | string |  |
| `actualValue` | string |  |
| `alertContent` | string |  |
| `status` | string |  |
| `alertTime` | string(date-time) |  |
| `confirmedTime` | string(date-time) |  |
| `remark` | string |  |
| `deviceName` | string |  |

### ApiResponseListAlertRecord

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | integer(int32) |  |
| `message` | string |  |
| `data` | array |  |
| `timestamp` | integer(int64) |  |

