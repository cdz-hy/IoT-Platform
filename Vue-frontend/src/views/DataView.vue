<template>
  <div class="data-page">
    <div class="fd-page-header animate-fade-in-up">
      <div class="header-content">
        <h1 class="fd-page-title">遥测数据</h1>
        <p class="fd-page-subtitle">查看设备上报的遥测数据历史记录</p>
      </div>
    </div>

    <div class="bento-card animate-fade-in-up delay-100" style="margin-bottom: 20px;">
      <div class="card-header"><h3>选择设备</h3></div>
      <el-select v-model="selectedDevice" placeholder="请选择设备" @change="loadData" style="width: 100%">
        <el-option v-for="d in devices" :key="d.deviceId" :label="`${d.deviceName} (${d.deviceId})`" :value="d.deviceId" />
      </el-select>
    </div>

    <div v-if="latest" class="bento-card animate-fade-in-up delay-200" style="margin-bottom: 20px;">
      <div class="card-header"><h3>最新数据</h3></div>
      <div class="payload-grid">
        <div v-for="(val, key) in parseJson(latest.payload)" :key="key" class="payload-card">
          <span class="payload-key">{{ key }}</span>
          <span class="payload-value">{{ val }}</span>
        </div>
      </div>
      <div class="update-time">上报时间: {{ latest.reportedTime }}</div>
    </div>

    <div v-if="history.length" class="bento-card animate-fade-in-up delay-300">
      <div class="card-header"><h3>历史记录 (最近{{ history.length }}条)</h3></div>
      <div class="history-table">
        <div class="table-row table-header">
          <span class="col-time">上报时间</span>
          <span class="col-data">数据内容</span>
        </div>
        <div v-for="(row, i) in history" :key="i" class="table-row">
          <span class="col-time">{{ row.reportedTime }}</span>
          <span class="col-data"><code>{{ row.payload }}</code></span>
        </div>
      </div>
    </div>

    <div v-if="selectedDevice && !latest && !history.length" class="empty-state animate-fade-in-up delay-200">
      <el-icon :size="48"><Document /></el-icon>
      <p>暂无遥测数据</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getDevices, getLatestTelemetry, getTelemetryHistory } from '@/api/index.js'

const devices = ref([])
const selectedDevice = ref('')
const latest = ref(null)
const history = ref([])

onMounted(async () => {
  try {
    const res = await getDevices()
    devices.value = res.data || res
  } catch (e) { /* ignore */ }
})

async function loadData() {
  if (!selectedDevice.value) return
  try {
    const res = await getLatestTelemetry(selectedDevice.value)
    latest.value = res.data || res
  } catch (e) { latest.value = null }
  try {
    const res = await getTelemetryHistory(selectedDevice.value, 50)
    history.value = res.data || res || []
  } catch (e) { history.value = [] }
}

function parseJson(str) {
  try { return JSON.parse(str) } catch (e) { return {} }
}
</script>

<style scoped>
.data-page { max-width: 1200px; margin: 0 auto; }
.fd-page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.fd-page-title { font-size: 24px; font-weight: 700; color: var(--fd-text); }
.fd-page-subtitle { font-size: 14px; color: var(--fd-text-tertiary); margin-top: 4px; }
.bento-card { background: var(--fd-bg-card); backdrop-filter: var(--glass-blur) var(--glass-saturate); border: var(--glass-border); border-radius: var(--radius-xl); padding: 24px; box-shadow: var(--shadow-4); }
.card-header { margin-bottom: 16px; }
.card-header h3 { font-size: 16px; font-weight: 600; color: var(--fd-text); }
.payload-grid { display: flex; gap: 16px; flex-wrap: wrap; }
.payload-card { background: var(--fd-bg-hover); border: var(--glass-border); border-radius: var(--radius-lg); padding: 16px 24px; min-width: 140px; text-align: center; }
.payload-key { display: block; font-size: 12px; color: var(--fd-text-tertiary); margin-bottom: 6px; text-transform: uppercase; letter-spacing: 0.5px; }
.payload-value { font-size: 24px; font-weight: 700; color: var(--fd-primary); }
.update-time { margin-top: 16px; font-size: 12px; color: var(--fd-text-tertiary); }
.history-table { width: 100%; }
.table-row { display: flex; padding: 10px 0; border-bottom: 1px solid var(--fd-border); }
.table-header { font-weight: 600; font-size: 13px; color: var(--fd-text-secondary); }
.col-time { width: 200px; font-size: 13px; color: var(--fd-text-secondary); }
.col-data { flex: 1; font-size: 13px; }
.col-data code { font-family: 'SF Mono', 'Fira Code', monospace; font-size: 12px; color: var(--fd-primary); background: var(--fd-primary-alpha); padding: 2px 6px; border-radius: var(--radius-sm); word-break: break-all; }
.empty-state { text-align: center; padding: 60px 20px; color: var(--fd-text-tertiary); }
.empty-state p { margin-top: 12px; font-size: 14px; }
</style>
