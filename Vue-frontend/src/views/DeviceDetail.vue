<template>
  <div class="device-detail-page">
    <!-- 页面头部 -->
    <div class="fd-page-header animate-fade-in-up">
      <div class="header-left">
        <button class="fd-btn fd-btn-subtle back-btn" @click="$router.back()">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </button>
        <div>
          <h1 class="fd-page-title">{{ device.deviceName || '设备详情' }}</h1>
          <p class="fd-page-subtitle">{{ device.deviceId }}</p>
        </div>
      </div>
      <div class="header-right">
        <div class="fd-status" :class="device.status === 'online' ? 'fd-status-online' : 'fd-status-offline'">
          <span class="fd-status-dot"></span>
          {{ device.status === 'online' ? '在线' : '离线' }}
        </div>
      </div>
    </div>

    <!-- 基本信息 -->
    <div class="bento-card animate-fade-in-up delay-100">
      <div class="card-header" style="padding-bottom: 20px;">
        <h3>基本信息</h3>
      </div>
      <div class="info-grid">
        <div class="info-item">
          <span class="info-label">设备ID</span>
          <span class="info-value mono">{{ device.deviceId }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">设备标识码</span>
          <span class="info-value mono">{{ device.nodeId }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">设备名称</span>
          <span class="info-value">{{ device.deviceName || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">所属产品</span>
          <span class="info-value">{{ device.productName || device.belongProductId || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">最后上线</span>
          <span class="info-value">{{ device.lastOnlineTime || '从未上线' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">注册时间</span>
          <span class="info-value">{{ device.createdTime }}</span>
        </div>
      </div>
    </div>

    <!-- 功能标签页 -->
    <div class="bento-card animate-fade-in-up delay-200">
      <div class="model-tabs">
        <button
          v-for="tab in tabs"
          :key="tab.key"
          class="tab-btn"
          :class="{ active: activeTab === tab.key }"
          @click="activeTab = tab.key"
        >
          <el-icon><component :is="tab.icon" /></el-icon>
          {{ tab.label }}
        </button>
      </div>

      <!-- 设备影子 -->
      <div v-show="activeTab === 'shadow'" class="tab-content">
        <div class="content-header">
          <h3>设备影子数据</h3>
          <button class="fd-btn fd-btn-secondary" @click="showShadowDialog" size="small">
            修改期望值
          </button>
        </div>
        <div class="shadow-grid">
          <div class="shadow-card">
            <h4>Reported (设备上报)</h4>
            <pre class="shadow-data">{{ formatJson(shadowData?.state?.reported) }}</pre>
          </div>
          <div class="shadow-card">
            <h4>Desired (平台期望)</h4>
            <pre class="shadow-data">{{ formatJson(shadowData?.state?.desired) }}</pre>
          </div>
          <div class="shadow-card">
            <h4>Delta (差异)</h4>
            <pre class="shadow-data">{{ formatJson(shadowData?.delta) }}</pre>
          </div>
        </div>
      </div>

      <!-- 上下线记录 -->
      <div v-show="activeTab === 'logs'" class="tab-content">
        <div class="log-list">
          <div v-for="log in connectLogs" :key="log.id" class="log-item">
            <div class="log-indicator" :class="log.eventType">
              <el-icon v-if="log.eventType === 'online'"><Top /></el-icon>
              <el-icon v-else><Bottom /></el-icon>
            </div>
            <span class="event-type" :class="log.eventType">
              {{ log.eventType === 'online' ? '上线' : '下线' }}
            </span>
            <span class="log-time">{{ log.eventTime }}</span>
          </div>
          <div v-if="connectLogs.length === 0" class="empty-list">
            <p>暂无上下线记录</p>
          </div>
        </div>
      </div>

      <!-- 命令记录 -->
      <div v-show="activeTab === 'commands'" class="tab-content">
        <div class="command-list">
          <div v-for="cmd in commandLogs" :key="cmd.id" class="command-item">
            <div class="command-icon" :class="cmd.status">
              <el-icon :size="16"><Promotion /></el-icon>
            </div>
            <div class="command-content">
              <div class="command-name">{{ cmd.commandName }}</div>
              <div class="command-meta">
                <span class="command-time">{{ cmd.requestTime }}</span>
                <span class="command-status" :class="cmd.status">{{ cmd.status }}</span>
              </div>
            </div>
          </div>
          <div v-if="commandLogs.length === 0" class="empty-list">
            <p>暂无命令记录</p>
          </div>
        </div>
      </div>

      <!-- 下发命令 -->
      <div v-show="activeTab === 'send'" class="tab-content">
        <div class="send-form">
          <div class="fd-form-group">
            <label class="fd-form-label">命令名称</label>
            <input v-model="commandForm.commandName" placeholder="如 重启设备" class="fd-input" />
          </div>
          <div class="fd-form-group">
            <label class="fd-form-label">命令参数</label>
            <textarea v-model="commandForm.commandParams" placeholder="JSON格式参数" class="fd-input" rows="5" style="resize: vertical;"></textarea>
          </div>
          <button class="fd-btn fd-btn-primary" @click="handleSendCommand">
            <el-icon><Promotion /></el-icon>
            下发命令
          </button>
        </div>
      </div>
    </div>

    <!-- 修改期望值对话框 -->
    <el-dialog v-model="shadowDialogVisible" title="修改期望值" width="460px">
      <div class="shadow-form">
        <div v-for="prop in properties" :key="prop.id" class="fd-form-group">
          <label class="fd-form-label">{{ prop.name || prop.propertyName }} <span class="type-tag">{{ prop.dataType }}</span></label>
          <input v-model="desiredValues[prop.identifier || prop.propertyName]" :placeholder="`请输入${prop.name || prop.propertyName}`" class="fd-input" />
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <button class="fd-btn fd-btn-secondary" @click="shadowDialogVisible = false">取消</button>
          <button class="fd-btn fd-btn-primary" @click="handleUpdateDesired">确定</button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  getDevice, getShadow, updateDesired,
  getConnectLogs, getCommandLogs, sendCommand, getProperties
} from '../api'

const route = useRoute()
const deviceId = route.params.id

const device = ref({})
const shadowData = ref({})
const connectLogs = ref([])
const commandLogs = ref([])
const properties = ref([])
const activeTab = ref('shadow')
const shadowDialogVisible = ref(false)
const desiredValues = reactive({})

const commandForm = reactive({ commandName: '', commandParams: '{}' })

const tabs = [
  { key: 'shadow', label: '设备影子', icon: 'Document' },
  { key: 'logs', label: '上下线记录', icon: 'Connection' },
  { key: 'commands', label: '命令记录', icon: 'Promotion' },
  { key: 'send', label: '下发命令', icon: 'Right' }
]

const loadDevice = async () => {
  try {
    const res = await getDevice(deviceId)
    device.value = res.data
  } catch (error) {
    console.error('加载设备详情失败', error)
  }
}

const loadShadow = async () => {
  try {
    const res = await getShadow(deviceId)
    shadowData.value = JSON.parse(res.data.shadowData || '{}')
  } catch (error) {
    console.error('加载设备影子失败', error)
  }
}

const loadConnectLogs = async () => {
  try {
    const res = await getConnectLogs(deviceId)
    connectLogs.value = res.data
  } catch (error) {
    console.error('加载上下线记录失败', error)
  }
}

const loadCommandLogs = async () => {
  try {
    const res = await getCommandLogs(deviceId)
    commandLogs.value = res.data
  } catch (error) {
    console.error('加载命令记录失败', error)
  }
}

const loadProperties = async () => {
  const productId = device.value.belongProductId
  if (!productId) return
  try {
    const res = await getProperties(productId)
    properties.value = res.data || res || []
  } catch (error) {
    console.error('加载属性列表失败', error)
  }
}

const showShadowDialog = () => {
  properties.value.forEach(prop => {
    const key = prop.identifier || prop.propertyName
    desiredValues[key] = shadowData.value?.state?.desired?.[key] || ''
  })
  shadowDialogVisible.value = true
}

const handleUpdateDesired = async () => {
  try {
    const desired = {}
    Object.entries(desiredValues).forEach(([key, value]) => {
      if (value !== '') desired[key] = value
    })
    await updateDesired({ deviceId, desired })
    ElMessage.success('更新成功')
    shadowDialogVisible.value = false
    loadShadow()
  } catch (error) {
    console.error('更新失败', error)
  }
}

const handleSendCommand = async () => {
  try {
    await sendCommand({
      deviceId,
      commandName: commandForm.commandName,
      commandParams: commandForm.commandParams
    })
    ElMessage.success('命令下发成功')
    loadCommandLogs()
  } catch (error) {
    console.error('命令下发失败', error)
  }
}

const formatJson = (obj) => {
  return obj ? JSON.stringify(obj, null, 2) : '{}'
}

onMounted(() => {
  loadDevice()
  loadShadow()
  loadConnectLogs()
  loadCommandLogs()
  setTimeout(loadProperties, 500)
})
</script>

<style scoped>
.device-detail-page {
  max-width: 1000px;
  margin: 0 auto;
}

.fd-page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
}

.fd-page-subtitle {
  font-family: var(--font-mono);
  font-size: 12px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1px;
  background: var(--fd-border);
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 16px 24px;
  background: var(--glass-bg);
  backdrop-filter: blur(10px);
}

[data-theme="dark"] .info-item {
  background: rgba(15, 23, 42, 0.4);
}

.info-label {
  font-size: 12px;
  color: var(--fd-text-secondary);
}

.info-value {
  font-size: 13px;
  color: var(--fd-text);
  font-weight: 500;
}

.info-value.mono {
  font-family: var(--font-mono);
  font-size: 12px;
}

.model-tabs {
  display: flex;
  border-bottom: 1px solid var(--fd-border);
}

.tab-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px;
  background: transparent;
  border: none;
  font-size: 13px;
  color: var(--fd-text-secondary);
  cursor: pointer;
  transition: all var(--transition-fast);
  border-bottom: 2px solid transparent;
  font-weight: 500;
}

.tab-btn:hover {
  background: var(--fd-bg-hover);
  color: var(--fd-text);
}

.tab-btn.active {
  color: var(--fd-primary);
  border-bottom-color: var(--fd-primary);
  background: var(--fd-primary-alpha);
}

.tab-content {
  padding: 16px 20px;
}

.content-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.content-header h3 {
  font-size: 14px;
  font-weight: 600;
  color: var(--fd-text);
}

.shadow-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.shadow-card {
  background: rgba(255, 255, 255, 0.2);
  border: var(--glass-border);
  border-radius: var(--radius-xl);
  padding: 16px;
}

[data-theme="dark"] .shadow-card {
  background: rgba(15, 23, 42, 0.3);
}

.shadow-card h4 {
  font-size: 12px;
  font-weight: 500;
  color: var(--fd-text-secondary);
  margin-bottom: 10px;
}

.shadow-data {
  background: rgba(255, 255, 255, 0.4);
  border: var(--glass-border);
  border-radius: var(--radius-lg);
  padding: 12px;
  font-family: var(--font-mono);
  font-size: 12px;
  color: var(--fd-text);
  min-height: 120px;
  overflow: auto;
  white-space: pre-wrap;
}

.log-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 400px;
  overflow-y: auto;
}

.log-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.2);
  border: var(--glass-border);
  border-radius: var(--radius-xl);
  transition: all var(--transition-fast);
}

[data-theme="dark"] .log-item {
  background: rgba(15, 23, 42, 0.3);
}

.log-item:hover {
  background: var(--fd-bg-hover);
}

.log-indicator {
  width: 32px;
  height: 32px;
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.log-indicator.online {
  background: var(--fd-success);
}

.log-indicator.offline {
  background: var(--fd-error);
}

.event-type {
  padding: 3px 10px;
  border-radius: var(--radius-sm);
  font-size: 12px;
  font-weight: 500;
}

.event-type.online {
  background: var(--fd-success-light);
  color: var(--fd-success);
}

.event-type.offline {
  background: var(--fd-error-light);
  color: var(--fd-error);
}

.log-time {
  font-size: 12px;
  color: var(--fd-text-secondary);
  margin-left: auto;
}

.command-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 400px;
  overflow-y: auto;
}

.command-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.2);
  border: var(--glass-border);
  border-radius: var(--radius-xl);
}

[data-theme="dark"] .command-item {
  background: rgba(15, 23, 42, 0.3);
}

.command-icon {
  width: 32px;
  height: 32px;
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.command-icon.pending {
  background: var(--fd-warning);
}

.command-icon.success {
  background: var(--fd-success);
}

.command-icon.failed {
  background: var(--fd-error);
}

.command-content {
  flex: 1;
}

.command-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--fd-text);
  margin-bottom: 4px;
}

.command-meta {
  display: flex;
  gap: 12px;
}

.command-time {
  font-size: 11px;
  color: var(--fd-text-secondary);
}

.command-status {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: var(--radius-sm);
}

.command-status.pending {
  background: var(--fd-warning-light);
  color: var(--fd-warning);
}

.command-status.success {
  background: var(--fd-success-light);
  color: var(--fd-success);
}

.command-status.failed {
  background: var(--fd-error-light);
  color: var(--fd-error);
}

.send-form {
  max-width: 500px;
}

.empty-list {
  padding: 40px;
  text-align: center;
  color: var(--fd-text-secondary);
  font-size: 13px;
}

.type-tag {
  padding: 1px 6px;
  background: var(--fd-bg-hover);
  border-radius: var(--radius-sm);
  font-size: 10px;
  color: var(--fd-text-secondary);
}

/* 对话框 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
  .shadow-grid {
    grid-template-columns: 1fr;
  }
  .model-tabs {
    flex-wrap: wrap;
  }
  .tab-btn {
    flex: none;
    width: 50%;
  }
}
</style>
