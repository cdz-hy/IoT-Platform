<template>
  <div class="mqtt-access-page">
    <div class="fd-page-header animate-fade-in-up">
      <div class="header-content">
        <h1 class="fd-page-title">MQTT接入信息</h1>
        <p class="fd-page-subtitle">查看设备的MQTT连接配置，通过HTTP模拟设备行为</p>
      </div>
    </div>

    <div class="bento-card animate-fade-in-up delay-100" style="margin-bottom: 20px;">
      <div class="card-header">
        <h3>选择设备</h3>
      </div>
      <el-select v-model="selectedDevice" placeholder="请选择设备" @change="loadAccessInfo" style="width: 100%">
        <el-option v-for="d in devices" :key="d.deviceId" :label="`${d.deviceName} (${d.deviceId})`" :value="d.deviceId" />
      </el-select>
    </div>

    <div v-if="accessInfo" class="info-grid animate-fade-in-up delay-200">
      <div class="bento-card">
        <div class="card-header"><h3>连接参数</h3></div>
        <div class="info-list">
          <div class="info-item"><span class="label">Broker地址</span><code>{{ accessInfo.host }}:{{ accessInfo.port }}</code></div>
          <div class="info-item"><span class="label">协议</span><code>{{ accessInfo.protocol }}</code></div>
          <div class="info-item"><span class="label">Client ID</span><code>{{ accessInfo.clientId }}</code></div>
          <div class="info-item"><span class="label">用户名</span><code>{{ accessInfo.username }}</code></div>
          <div class="info-item"><span class="label">密码</span><code>{{ accessInfo.password }}</code></div>
        </div>
      </div>
      <div class="bento-card">
        <div class="card-header"><h3>主题模板</h3></div>
        <div class="info-list">
          <div v-for="(topic, key) in accessInfo.topics" :key="key" class="info-item">
            <span class="label">{{ key }}</span><code>{{ topic }}</code>
          </div>
        </div>
      </div>
    </div>

    <div v-if="selectedDevice" class="bento-card animate-fade-in-up delay-300" style="margin-top: 20px;">
      <div class="card-header"><h3>设备模拟</h3></div>
      <div class="simulate-actions">
        <button class="fd-btn fd-btn-primary" @click="doSimulate('online')" :disabled="simulating">
          <el-icon><Top /></el-icon> 模拟上线
        </button>
        <button class="fd-btn fd-btn-secondary" @click="doSimulate('offline')" :disabled="simulating">
          <el-icon><Bottom /></el-icon> 模拟离线
        </button>
        <button class="fd-btn fd-btn-secondary" @click="doSimulate('telemetry')" :disabled="simulating">
          <el-icon><Upload /></el-icon> 模拟遥测上报
        </button>
      </div>
      <div v-if="simulateResult" class="simulate-result" :class="simulateResult.success ? 'success' : 'error'">
        {{ simulateResult.message }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getDevices, getMqttAccessInfo, simulateOnline, simulateOffline, simulateTelemetry } from '@/api/index.js'
import { ElMessage } from 'element-plus'

const devices = ref([])
const selectedDevice = ref('')
const accessInfo = ref(null)
const simulating = ref(false)
const simulateResult = ref(null)

onMounted(async () => {
  try {
    const res = await getDevices()
    devices.value = res.data || res
  } catch (e) { /* ignore */ }
})

async function loadAccessInfo() {
  if (!selectedDevice.value) return
  try {
    const res = await getMqttAccessInfo(selectedDevice.value)
    accessInfo.value = res.data || res
  } catch (e) {
    ElMessage.error('获取MQTT接入信息失败')
  }
}

async function doSimulate(type) {
  simulating.value = true
  simulateResult.value = null
  try {
    if (type === 'online') {
      await simulateOnline(selectedDevice.value)
      simulateResult.value = { success: true, message: '模拟上线成功' }
    } else if (type === 'offline') {
      await simulateOffline(selectedDevice.value)
      simulateResult.value = { success: true, message: '模拟离线成功' }
    } else {
      await simulateTelemetry(selectedDevice.value, { temperature: 25.5, humidity: 60 })
      simulateResult.value = { success: true, message: '模拟遥测上报成功' }
    }
  } catch (e) {
    simulateResult.value = { success: false, message: e.message || '操作失败' }
  } finally {
    simulating.value = false
  }
}
</script>

<style scoped>
.mqtt-access-page { max-width: 1200px; margin: 0 auto; }
.fd-page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.fd-page-title { font-size: 24px; font-weight: 700; color: var(--fd-text); }
.fd-page-subtitle { font-size: 14px; color: var(--fd-text-tertiary); margin-top: 4px; }
.bento-card { background: var(--fd-bg-card); backdrop-filter: var(--glass-blur) var(--glass-saturate); border: var(--glass-border); border-radius: var(--radius-xl); padding: 24px; box-shadow: var(--shadow-4); }
.card-header { margin-bottom: 16px; }
.card-header h3 { font-size: 16px; font-weight: 600; color: var(--fd-text); }
.info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.info-list { display: flex; flex-direction: column; gap: 12px; }
.info-item { display: flex; justify-content: space-between; align-items: center; padding: 10px 14px; background: var(--fd-bg-hover); border-radius: var(--radius-md); }
.info-item .label { color: var(--fd-text-secondary); font-size: 13px; }
.info-item code { font-family: 'SF Mono', 'Fira Code', monospace; font-size: 13px; color: var(--fd-primary); background: var(--fd-primary-alpha); padding: 2px 8px; border-radius: var(--radius-sm); }
.simulate-actions { display: flex; gap: 12px; flex-wrap: wrap; }
.simulate-result { margin-top: 16px; padding: 12px 16px; border-radius: var(--radius-md); font-size: 14px; }
.simulate-result.success { background: var(--fd-success-light); color: var(--fd-success); }
.simulate-result.error { background: var(--fd-error-light); color: var(--fd-error); }
[data-theme="dark"] .simulate-result.success { background: rgba(5, 150, 105, 0.15); }
[data-theme="dark"] .simulate-result.error { background: rgba(225, 29, 72, 0.15); }
.fd-btn { display: inline-flex; align-items: center; gap: 6px; padding: 8px 18px; border: none; border-radius: var(--radius-md); font-size: 14px; font-weight: 500; cursor: pointer; transition: all var(--transition-fast); }
.fd-btn-primary { background: var(--fd-primary); color: #fff; }
.fd-btn-primary:hover { background: var(--fd-primary-hover); }
.fd-btn-secondary { background: var(--fd-bg-hover); color: var(--fd-text); border: var(--glass-border); }
.fd-btn-secondary:hover { background: var(--fd-bg-pressed); }
.fd-btn:disabled { opacity: 0.5; cursor: not-allowed; }
@media (max-width: 768px) { .info-grid { grid-template-columns: 1fr; } }
</style>
