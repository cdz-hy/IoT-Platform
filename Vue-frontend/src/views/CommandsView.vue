<template>
  <div class="commands-page">
    <div class="fd-page-header animate-fade-in-up">
      <div class="header-content">
        <h1 class="fd-page-title">命令管理</h1>
        <p class="fd-page-subtitle">向设备下发命令并查看执行记录</p>
      </div>
    </div>

    <div class="bento-card animate-fade-in-up delay-100" style="margin-bottom: 20px;">
      <div class="card-header"><h3>下发命令</h3></div>
      <div class="command-form">
        <el-select v-model="form.deviceId" placeholder="选择设备" style="width: 220px">
          <el-option v-for="d in devices" :key="d.deviceId" :label="`${d.deviceName} (${d.deviceId})`" :value="d.deviceId" />
        </el-select>
        <input v-model="form.commandName" placeholder="命令名称" class="fd-input" style="width: 180px" />
        <input v-model="form.commandParams" placeholder='参数JSON, 如 {"brightness":80}' class="fd-input" style="flex:1; min-width: 200px" />
        <button class="fd-btn fd-btn-primary" @click="doSend" :disabled="sending">
          <el-icon><Promotion /></el-icon> 下发
        </button>
      </div>
    </div>

    <div class="bento-card animate-fade-in-up delay-200">
      <div class="card-header">
        <h3>命令记录</h3>
        <el-select v-model="logDeviceId" placeholder="选择设备查看记录" @change="loadLogs" style="width: 250px" size="small">
          <el-option v-for="d in devices" :key="d.deviceId" :label="`${d.deviceName} (${d.deviceId})`" :value="d.deviceId" />
        </el-select>
      </div>

      <div v-if="logs.length" class="history-table">
        <div class="table-row table-header">
          <span class="col-id">命令ID</span>
          <span class="col-name">命令名称</span>
          <span class="col-params">参数</span>
          <span class="col-status">状态</span>
          <span class="col-result">结果</span>
          <span class="col-time">下发时间</span>
        </div>
        <div v-for="row in logs" :key="row.commandId" class="table-row">
          <span class="col-id"><code>{{ row.commandId }}</code></span>
          <span class="col-name">{{ row.commandName }}</span>
          <span class="col-params"><code>{{ row.commandParams || '-' }}</code></span>
          <span class="col-status">
            <span class="status-badge" :class="row.status">{{ statusLabel(row.status) }}</span>
          </span>
          <span class="col-result">{{ row.result || '-' }}</span>
          <span class="col-time">{{ row.requestTime }}</span>
        </div>
      </div>
      <div v-else class="empty-state">
        <el-icon :size="48"><Promotion /></el-icon>
        <p>暂无命令记录</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getDevices, sendCommand, getCommandLogs } from '@/api/index.js'
import { ElMessage } from 'element-plus'

const devices = ref([])
const form = ref({ deviceId: '', commandName: '', commandParams: '' })
const sending = ref(false)
const logDeviceId = ref('')
const logs = ref([])

onMounted(async () => {
  try {
    const res = await getDevices()
    devices.value = res.data || res
  } catch (e) { /* ignore */ }
})

function statusLabel(s) {
  return { pending: '执行中', success: '成功', failed: '失败' }[s] || s
}

async function doSend() {
  if (!form.value.deviceId || !form.value.commandName) {
    ElMessage.warning('请选择设备并填写命令名称')
    return
  }
  sending.value = true
  try {
    await sendCommand(form.value)
    ElMessage.success('命令下发成功')
    if (logDeviceId.value === form.value.deviceId) loadLogs()
  } catch (e) {
    ElMessage.error('命令下发失败')
  } finally {
    sending.value = false
  }
}

async function loadLogs() {
  if (!logDeviceId.value) return
  try {
    const res = await getCommandLogs(logDeviceId.value)
    logs.value = res.data || res || []
  } catch (e) { logs.value = [] }
}
</script>

<style scoped>
.commands-page { max-width: 1400px; margin: 0 auto; }
.fd-page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.fd-page-title { font-size: 24px; font-weight: 700; color: var(--fd-text); }
.fd-page-subtitle { font-size: 14px; color: var(--fd-text-tertiary); margin-top: 4px; }
.bento-card { background: var(--fd-bg-card); backdrop-filter: var(--glass-blur) var(--glass-saturate); border: var(--glass-border); border-radius: var(--radius-xl); padding: 24px; box-shadow: var(--shadow-4); }
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.card-header h3 { font-size: 16px; font-weight: 600; color: var(--fd-text); }
.command-form { display: flex; gap: 12px; align-items: center; flex-wrap: wrap; }
.fd-input { padding: 8px 14px; border: var(--glass-border); border-radius: var(--radius-md); background: var(--fd-bg-hover); color: var(--fd-text); font-size: 14px; outline: none; transition: all var(--transition-fast); }
.fd-input:focus { border-color: var(--fd-border-focus); box-shadow: 0 0 0 2px var(--fd-primary-alpha); }
.fd-input::placeholder { color: var(--fd-text-disabled); }
.fd-btn { display: inline-flex; align-items: center; gap: 6px; padding: 8px 18px; border: none; border-radius: var(--radius-md); font-size: 14px; font-weight: 500; cursor: pointer; transition: all var(--transition-fast); }
.fd-btn-primary { background: var(--fd-primary); color: #fff; }
.fd-btn-primary:hover { background: var(--fd-primary-hover); }
.fd-btn:disabled { opacity: 0.5; cursor: not-allowed; }
.history-table { width: 100%; }
.table-row { display: flex; padding: 10px 0; border-bottom: 1px solid var(--fd-border); align-items: center; }
.table-header { font-weight: 600; font-size: 13px; color: var(--fd-text-secondary); }
.col-id { width: 140px; font-size: 12px; }
.col-id code { font-family: 'SF Mono', 'Fira Code', monospace; color: var(--fd-primary); background: var(--fd-primary-alpha); padding: 1px 6px; border-radius: var(--radius-sm); }
.col-name { width: 120px; font-size: 13px; }
.col-params { flex: 1; font-size: 12px; min-width: 0; }
.col-params code { font-family: 'SF Mono', 'Fira Code', monospace; font-size: 11px; color: var(--fd-text-tertiary); word-break: break-all; }
.col-status { width: 80px; }
.col-result { width: 120px; font-size: 12px; color: var(--fd-text-tertiary); }
.col-time { width: 160px; font-size: 12px; color: var(--fd-text-tertiary); }
.status-badge { display: inline-block; padding: 2px 10px; border-radius: 20px; font-size: 12px; font-weight: 500; }
.status-badge.pending { background: var(--fd-warning-light); color: var(--fd-warning); }
.status-badge.success { background: var(--fd-success-light); color: var(--fd-success); }
.status-badge.failed { background: var(--fd-error-light); color: var(--fd-error); }
[data-theme="dark"] .status-badge.pending { background: rgba(234, 88, 12, 0.15); }
[data-theme="dark"] .status-badge.success { background: rgba(5, 150, 105, 0.15); }
[data-theme="dark"] .status-badge.failed { background: rgba(225, 29, 72, 0.15); }
.empty-state { text-align: center; padding: 60px 20px; color: var(--fd-text-tertiary); }
.empty-state p { margin-top: 12px; font-size: 14px; }
</style>
