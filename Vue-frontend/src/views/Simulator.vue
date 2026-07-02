<template>
  <div class="simulator-page">
    <div class="fd-page-header animate-fade-in-up">
      <div class="header-content">
        <h1 class="fd-page-title">设备模拟器</h1>
        <p class="fd-page-subtitle">模拟设备连接和数据上报</p>
      </div>
    </div>

    <div class="bento-grid bento-grid-4 animate-fade-in-up delay-100">
      <!-- 连接配置 -->
      <div class="fd-card bento-col-2">
        <div class="fd-card-header">
          <h3>连接配置</h3>
          <div class="connection-status" :class="{ connected }">
            <span class="status-dot"></span>
            {{ connected ? '已连接' : '未连接' }}
          </div>
        </div>
        <div class="fd-card-body">
          <div class="fd-form-group">
            <label class="fd-form-label">Broker地址</label>
            <input v-model="config.broker" placeholder="ws://localhost:8083/mqtt" class="fd-input" :disabled="connected" />
          </div>
          <div class="fd-form-group">
            <label class="fd-form-label">选择设备</label>
            <el-select v-model="config.deviceId" placeholder="请选择设备" style="width: 100%" @change="onDeviceChange" :disabled="connected">
              <el-option v-for="d in devices" :key="d.deviceId" :label="d.deviceName || d.deviceId" :value="d.deviceId"></el-option>
            </el-select>
          </div>
          <div class="fd-form-group">
            <label class="fd-form-label">产品ID</label>
            <input v-model="config.productId" class="fd-input" disabled />
          </div>
          <div class="button-group">
            <button class="fd-btn fd-btn-primary" @click="connect" :disabled="connected || !config.deviceId">
              <el-icon><Connection /></el-icon>
              连接
            </button>
            <button class="fd-btn fd-btn-secondary" @click="disconnect" :disabled="!connected">
              断开
            </button>
          </div>
        </div>
      </div>

      <!-- 数据上报 -->
      <div class="fd-card bento-col-2">
        <div class="fd-card-header">
          <h3>上报数据</h3>
          <button class="fd-btn fd-btn-secondary" @click="generateRandomData" size="small" :disabled="properties.length === 0">
            随机生成
          </button>
        </div>
        <div class="fd-card-body form-scroll-area">
          <div v-for="prop in properties" :key="prop.id" class="fd-form-group">
            <label class="fd-form-label">{{ prop.name }} <span class="type-tag">{{ prop.dataType }}</span></label>
            <input
              v-model="telemetryData[prop.identifier]"
              :placeholder="`类型: ${prop.dataType}`"
              class="fd-input"
              :disabled="!connected"
            />
          </div>
          <div v-if="properties.length === 0" class="empty-props">
            <p>请先选择设备并连接</p>
          </div>
          <button
            class="fd-btn fd-btn-primary submit-btn"
            @click="sendTelemetry"
            :disabled="!connected || properties.length === 0"
          >
            <el-icon><Upload /></el-icon>
            上报数据
          </button>
        </div>
      </div>

      <!-- 运行日志 -->
      <div class="fd-card bento-col-4">
        <div class="fd-card-header">
          <h3>运行日志</h3>
          <div class="log-actions">
            <el-radio-group v-model="logFilter" size="small">
              <el-radio-button label="all">全部</el-radio-button>
              <el-radio-button label="up">上行</el-radio-button>
              <el-radio-button label="down">下行</el-radio-button>
              <el-radio-button label="system">系统</el-radio-button>
            </el-radio-group>
            <button class="fd-btn fd-btn-subtle clear-btn" @click="clearLogs">清空</button>
          </div>
        </div>
        <div class="fd-card-body">
          <div class="log-container" ref="logContainer">
            <div v-for="(log, index) in filteredLogs" :key="index" class="log-item" :class="[log.type, log.direction]">
              <div class="log-icon-wrapper">
                <el-icon v-if="log.direction === 'up'"><Top /></el-icon>
                <el-icon v-else-if="log.direction === 'down'"><Bottom /></el-icon>
                <el-icon v-else><Setting /></el-icon>
              </div>
              <span class="log-time">{{ log.time }}</span>
              <span class="log-message">{{ log.message }}</span>
            </div>
            <div v-if="filteredLogs.length === 0" class="empty-logs">
              <p>暂无日志记录...</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Connection, Upload, Top, Bottom, Setting } from '@element-plus/icons-vue'
import { getDevices, getProperties, getMqttAccessInfo } from '../api'
import Paho from 'paho-mqtt'

const devices = ref([])
const properties = ref([])
const connected = ref(false)
const logs = ref([])
const logContainer = ref(null)
const logFilter = ref('all')

const config = reactive({
  broker: 'ws://localhost:8083/mqtt',
  deviceId: '',
  productId: ''
})

const telemetryData = reactive({})
let mqttClient = null

const loadDevices = async () => {
  try {
    const res = await getDevices()
    devices.value = res.data || res || []
    
    // 恢复记忆的设备
    const savedDeviceId = localStorage.getItem('simulator_device_id')
    if (savedDeviceId && devices.value.some(d => d.deviceId === savedDeviceId)) {
      config.deviceId = savedDeviceId
      await onDeviceChange()
    }
  } catch (error) {
    console.error('加载设备列表失败', error)
  }
}

let deviceSecret = ''

const onDeviceChange = async () => {
  const device = devices.value.find(d => d.deviceId === config.deviceId)
  if (device) {
    localStorage.setItem('simulator_device_id', config.deviceId)
    // 获取MQTT接入信息（含密钥和产品ID）
    try {
      const res = await getMqttAccessInfo(config.deviceId)
      const info = res.data || res
      deviceSecret = info.password || ''
      // 从topic中提取productId
      const telemetryTopic = info.topics?.telemetry || ''
      const parts = telemetryTopic.split('/')
      if (parts.length >= 3) {
        config.productId = parts[1]
      }
    } catch (e) {
      deviceSecret = ''
    }
    await loadProperties(config.productId)
    if (!connected.value) {
      generateRandomData()
    }
  }
}

const loadProperties = async (productId) => {
  if (!productId) return
  try {
    const res = await getProperties(productId)
    properties.value = res.data || res || []
  } catch (error) {
    console.error('加载属性失败', error)
  }
}

// 记录日志，增加 direction: 'up', 'down', 'system'
const addLog = (type, direction, message) => {
  const now = new Date()
  const time = now.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
  logs.value.push({ type, direction, message, time })
  if (logs.value.length > 200) logs.value.shift()
  
  nextTick(() => {
    if (logContainer.value) {
      logContainer.value.scrollTop = logContainer.value.scrollHeight
    }
  })
}

const filteredLogs = computed(() => {
  if (logFilter.value === 'all') return logs.value
  return logs.value.filter(log => log.direction === logFilter.value)
})

const connect = () => {
  if (!config.deviceId) {
    ElMessage.warning('请选择设备')
    return
  }

  const device = devices.value.find(d => d.deviceId === config.deviceId)
  if (!device) {
    ElMessage.error('找不到设备信息')
    return
  }

  try {
    const url = new URL(config.broker)
    const host = url.hostname
    const port = Number(url.port) || 8083
    const path = url.pathname || '/mqtt'
    const clientId = config.deviceId

    mqttClient = new Paho.Client(host, port, path, clientId)

    mqttClient.onConnectionLost = (responseObject) => {
      connected.value = false
      if (responseObject.errorCode !== 0) {
        addLog('error', 'system', `连接异常断开: ${responseObject.errorMessage}`)
      } else {
        addLog('info', 'system', `连接已断开`)
      }
    }

    mqttClient.onMessageArrived = (message) => {
      const topic = message.destinationName
      const payload = message.payloadString
      addLog('info', 'down', `收到消息: topic=${topic}, payload=${payload}`)

      if (topic.includes('/command/')) {
        const parts = topic.split('/')
        const commandId = parts[4]
        addLog('warning', 'system', `正在执行平台下发的命令: ${commandId}`)
        
        // 模拟 1 秒后返回执行结果
        setTimeout(() => {
          const responseTopic = `iot/${config.productId}/${config.deviceId}/command/response`
          const responsePayload = JSON.stringify({
            commandId: commandId,
            status: 'success',
            result: '命令执行成功 (模拟器返回)'
          })
          const msg = new Paho.Message(responsePayload)
          msg.destinationName = responseTopic
          if (mqttClient && mqttClient.isConnected()) {
            mqttClient.send(msg)
            addLog('success', 'up', `发送命令响应: ${responsePayload}`)
          }
        }, 1000)
      } else if (topic.includes('/shadow/delta')) {
         addLog('warning', 'down', `收到影子配置(Delta)下发: ${payload}`)
      }
    }

    addLog('info', 'system', `正在连接到 ${config.broker}...`)
    mqttClient.connect({
      userName: config.deviceId,
      password: deviceSecret || '',
      cleanSession: true,
      onSuccess: () => {
        connected.value = true
        addLog('success', 'system', `设备 ${config.deviceId} 已成功连接到 MQTT Broker`)
        ElMessage.success('MQTT 连接成功')
        
        // 订阅命令主题
        const cmdTopic = `iot/${config.productId}/${config.deviceId}/command/+`
        mqttClient.subscribe(cmdTopic)
        addLog('info', 'system', `已订阅命令主题: ${cmdTopic}`)

        // 订阅影子同步主题
        const shadowTopic = `iot/${config.productId}/${config.deviceId}/shadow/delta`
        mqttClient.subscribe(shadowTopic)
        addLog('info', 'system', `已订阅影子主题: ${shadowTopic}`)
      },
      onFailure: (err) => {
        addLog('error', 'system', `连接失败: ${err.errorMessage}`)
        ElMessage.error('连接失败: ' + err.errorMessage)
      }
    })
  } catch (err) {
    addLog('error', 'system', `连接异常: ${err.message}`)
    ElMessage.error('连接异常，请检查Broker地址')
  }
}

const disconnect = () => {
  if (mqttClient && mqttClient.isConnected()) {
    mqttClient.disconnect()
  }
  connected.value = false
  addLog('info', 'system', `设备 ${config.deviceId} 已主动断开连接`)
  ElMessage.success('已断开')
}

const sendTelemetry = () => {
  if (!connected.value || !mqttClient || !mqttClient.isConnected()) {
    ElMessage.warning('请先连接设备')
    return
  }

  const data = {}
  Object.entries(telemetryData).forEach(([key, value]) => {
    if (value !== '') {
      const prop = properties.value.find(p => p.identifier === key)
      if (prop?.dataType === 'int') data[key] = parseInt(value)
      else if (prop?.dataType === 'double') data[key] = parseFloat(value)
      else if (prop?.dataType === 'boolean') data[key] = value === 'true'
      else data[key] = value
    }
  })

  const topic = `iot/${config.productId}/${config.deviceId}/telemetry`
  const payload = JSON.stringify(data)
  
  try {
    const message = new Paho.Message(payload)
    message.destinationName = topic
    mqttClient.send(message)
    addLog('success', 'up', `成功上报属性数据 [${topic}]: ${payload}`)
    ElMessage.success('数据上报成功')
  } catch (error) {
    addLog('error', 'system', `数据上报失败: ${error.message}`)
    ElMessage.error('数据上报失败')
  }
}

const generateRandomData = () => {
  properties.value.forEach(prop => {
    const parseBound = (val, defaultVal) => {
      if (val === null || val === undefined || val === '') return defaultVal
      const num = Number(val)
      return isNaN(num) ? defaultVal : num
    }

    if (prop.dataType === 'int') {
      let min = parseBound(prop.minValue, 0)
      let max = parseBound(prop.maxValue, 100)
      if (min > max) { const temp = min; min = max; max = temp; }
      telemetryData[prop.identifier] = Math.floor(Math.random() * (max - min + 1)) + min
    } else if (prop.dataType === 'double') {
      let min = parseBound(prop.minValue, 0)
      let max = parseBound(prop.maxValue, 100)
      if (min > max) { const temp = min; min = max; max = temp; }
      telemetryData[prop.identifier] = (Math.random() * (max - min) + min).toFixed(1)
    } else if (prop.dataType === 'boolean') {
      telemetryData[prop.identifier] = Math.random() > 0.5 ? 'true' : 'false'
    } else if (prop.dataType === 'string') {
      telemetryData[prop.identifier] = 'data_' + Math.floor(Math.random() * 10000)
    }
  })
}

const clearLogs = () => {
  logs.value = []
}

onMounted(() => {
  loadDevices()
})
</script>

<style scoped>
.simulator-page {
  max-width: 1400px;
  margin: 0 auto;
}

.header-content {
  flex: 1;
}

.fd-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.fd-card-header h3 {
  font-size: 14px;
  font-weight: 600;
  color: var(--fd-text);
  margin: 0;
}

.bento-col-4 {
  grid-column: span 4;
}

.form-scroll-area {
  max-height: 280px;
  overflow-y: auto;
  padding-right: 10px;
}

.form-scroll-area::-webkit-scrollbar {
  width: 6px;
}
.form-scroll-area::-webkit-scrollbar-thumb {
  background: var(--fd-border);
  border-radius: 3px;
}

.connection-status {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 12px;
  border-radius: var(--radius-md);
  font-size: 12px;
  background: var(--fd-bg-hover);
  color: var(--fd-text-secondary);
}

.connection-status.connected {
  background: var(--fd-success-light);
  color: var(--fd-success);
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
}

.connection-status.connected .status-dot {
  animation: pulse 2s infinite;
}

.button-group {
  display: flex;
  gap: 10px;
  margin-top: 16px;
}

.button-group .fd-btn {
  flex: 1;
}

.submit-btn {
  width: 100%;
  margin-top: 16px;
}

.empty-props {
  padding: 30px;
  text-align: center;
  color: var(--fd-text-secondary);
  font-size: 13px;
}

.log-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.clear-btn {
  font-size: 12px;
  padding: 4px 10px;
}

.log-container {
  height: 350px;
  overflow-y: auto;
  background: rgba(15, 23, 42, 0.85); /* Deep dark background for logs to look like terminal */
  border: var(--glass-border);
  border-radius: var(--radius-xl);
  padding: 16px;
  color: #f8fafc;
}

[data-theme="dark"] .log-container {
  background: rgba(15, 23, 42, 0.6);
}

.log-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 6px 8px;
  margin-bottom: 4px;
  border-radius: var(--radius-sm);
  font-family: var(--font-mono);
  font-size: 13px;
  animation: fadeIn 0.3s ease-out;
}

.log-item:hover {
  background: rgba(255, 255, 255, 0.05);
}

.log-icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  border-radius: 4px;
  flex-shrink: 0;
  margin-top: 2px;
}

.log-item.up .log-icon-wrapper { color: #38bdf8; background: rgba(56, 189, 248, 0.1); }
.log-item.down .log-icon-wrapper { color: #f43f5e; background: rgba(244, 63, 94, 0.1); }
.log-item.system .log-icon-wrapper { color: #94a3b8; background: rgba(148, 163, 184, 0.1); }

.log-item.info .log-message { color: #e2e8f0; }
.log-item.success .log-message { color: #34d399; }
.log-item.error .log-message { color: #f87171; }
.log-item.warning .log-message { color: #fbbf24; }

.log-time {
  color: #64748b;
  flex-shrink: 0;
}

.log-message {
  word-break: break-all;
}

.empty-logs {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #64748b;
  font-size: 13px;
}

.type-tag {
  padding: 1px 6px;
  background: var(--fd-bg-hover);
  border-radius: var(--radius-sm);
  font-size: 10px;
  color: var(--fd-text-secondary);
}

@media (max-width: 1200px) {
  .bento-grid-4 {
    grid-template-columns: 1fr;
  }
  .bento-col-2, .bento-col-4 {
    grid-column: span 1;
  }
}
</style>
