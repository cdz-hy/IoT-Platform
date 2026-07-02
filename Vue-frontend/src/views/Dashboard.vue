<template>
  <div class="dashboard">
    <!-- 欢迎区域 -->
    <div class="welcome-section animate-fade-in-up">
      <div class="welcome-content">
        <div class="welcome-text">
          <h1>欢迎回来，管理员</h1>
          <p>{{ currentDate }}，系统运行正常</p>
        </div>
        <button class="fd-btn fd-btn-secondary" @click="$router.push('/devices')">
          <el-icon><Cpu /></el-icon>
          设备管理
        </button>
      </div>
    </div>

    <!-- Bento Grid 统计卡片 -->
    <div class="bento-grid">
      <!-- 产品总数 -->
      <div class="bento-card stat-card animate-fade-in-up delay-100">
        <div class="stat-icon primary">
          <el-icon :size="22"><Box /></el-icon>
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ statistics.totalProducts }}</span>
          <span class="stat-label">产品总数</span>
        </div>
      </div>

      <!-- 设备总数 -->
      <div class="bento-card stat-card animate-fade-in-up delay-200">
        <div class="stat-icon info">
          <el-icon :size="22"><Cpu /></el-icon>
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ statistics.totalDevices }}</span>
          <span class="stat-label">设备总数</span>
        </div>
      </div>

      <!-- 在线设备 -->
      <div class="bento-card stat-card animate-fade-in-up delay-300">
        <div class="stat-icon success">
          <el-icon :size="22"><Connection /></el-icon>
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ statistics.onlineDevices }}</span>
          <span class="stat-label">在线设备</span>
        </div>
        <div class="stat-badge online">
          <span class="badge-dot"></span>
          {{ onlineRate }}%
        </div>
      </div>

      <!-- 今日告警 -->
      <div class="bento-card stat-card animate-fade-in-up delay-400">
        <div class="stat-icon warning">
          <el-icon :size="22"><Bell /></el-icon>
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ statistics.todayAlerts }}</span>
          <span class="stat-label">今日告警</span>
        </div>
      </div>

      <!-- 产品分布图 -->
      <div class="bento-card chart-card bento-col-2 animate-fade-in-up delay-200">
        <div class="card-header">
          <h3>产品类型分布</h3>
          <span class="fd-tag">实时</span>
        </div>
        <div ref="pieChartRef" class="chart-container"></div>
      </div>

      <!-- 告警列表 - 跨越2列2行 -->
      <div class="bento-card alert-card bento-col-2 bento-row-2 animate-fade-in-up delay-300">
        <div class="card-header">
          <h3>实时告警</h3>
          <el-badge :value="alerts.length" :max="99" type="danger">
            <button class="fd-btn fd-btn-subtle view-all-btn">查看全部</button>
          </el-badge>
        </div>
        <div class="alert-list">
          <div v-for="alert in alerts" :key="alert.id" class="alert-item" :class="alert.level">
            <div class="alert-indicator"></div>
            <div class="alert-content">
              <p class="alert-text">{{ alert.content }}</p>
              <span class="alert-time">{{ alert.time }}</span>
            </div>
          </div>
          <div v-if="alerts.length === 0" class="empty-alerts">
            <el-icon :size="48" color="var(--fd-border)"><CircleCheck /></el-icon>
            <p>暂无告警</p>
          </div>
        </div>
      </div>

      <!-- 设备状态趋势 -->
      <div class="bento-card chart-card bento-col-2 animate-fade-in-up delay-400">
        <div class="card-header">
          <h3>设备状态趋势</h3>
          <span class="fd-tag">近7天</span>
        </div>
        <div ref="lineChartRef" class="chart-container"></div>
      </div>

      <!-- 最近上下线记录 -->
      <div class="bento-card log-card bento-col-2 animate-fade-in-up delay-500">
        <div class="card-header">
          <h3>最近上下线记录</h3>
          <button class="fd-btn fd-btn-subtle view-all-btn" @click="$router.push('/connect-logs')">查看更多</button>
        </div>
        <div class="log-list">
          <div v-for="log in connectLogs" :key="log.id" class="log-item">
            <div class="log-indicator" :class="log.eventType">
              <el-icon v-if="log.eventType === 'online'"><Top /></el-icon>
              <el-icon v-else><Bottom /></el-icon>
            </div>
            <div class="log-content">
              <span class="log-device">{{ log.deviceId }}</span>
              <span class="log-event" :class="log.eventType">
                {{ log.eventType === 'online' ? '上线' : '下线' }}
              </span>
            </div>
            <span class="log-time">{{ formatTime(log.eventTime) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, watch } from 'vue'
import * as echarts from 'echarts'
import { getDashboardSummary, getRecentConnectLogs, getProductDistribution } from '../api'
import { useRealtimeStore } from '@/stores/realtime.js'
import { storeToRefs } from 'pinia'

const realtimeStore = useRealtimeStore()
const { lastTelemetry, lastAlert, lastDevice } = storeToRefs(realtimeStore)

const currentDate = computed(() => {
  const now = new Date()
  return now.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })
})

const statistics = reactive({
  totalProducts: 0,
  totalDevices: 0,
  onlineDevices: 0,
  todayAlerts: 0
})

const onlineRate = computed(() => {
  if (statistics.totalDevices === 0) return 0
  return Math.round((statistics.onlineDevices / statistics.totalDevices) * 100)
})

const connectLogs = ref([])
const alerts = ref([])

const pieChartRef = ref(null)
const lineChartRef = ref(null)
let pieChart = null
let lineChart = null

const formatTime = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  return date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

const connectTrends = ref(null)

const loadStatistics = async () => {
  try {
    const res = await getDashboardSummary()
    const data = res.data || res
    statistics.totalProducts = data.totalProducts || 0
    statistics.totalDevices = data.totalDevices || 0
    statistics.onlineDevices = data.onlineDevices || 0
    statistics.todayAlerts = data.todayAlerts || 0
    if (data.latestAlerts) {
      alerts.value = data.latestAlerts.map(a => ({
        id: a.id,
        content: a.alertContent,
        level: a.status === 'pending' ? 'high' : 'medium',
        time: formatTime(a.alertTime)
      }))
    }
    // 更新趋势图数据
    if (data.connectTrends) {
      connectTrends.value = data.connectTrends
      trendData = data.connectTrends
      renderLineChart()
    }
  } catch (error) {
    // 降级到基础统计
    try {
      const res = await getDashboardStatistics()
      Object.assign(statistics, res.data || res)
    } catch (e) { /* ignore */ }
  }
}

const loadConnectLogs = async () => {
  try {
    const res = await getRecentConnectLogs()
    connectLogs.value = res.data
  } catch (error) {
    console.error('加载上下线记录失败', error)
  }
}

const initPieChart = async () => {
  try {
    const res = await getProductDistribution()
    const data = Object.entries(res.data || {}).map(([name, value]) => ({ name, value }))
    if (pieChartRef.value) {
      pieChart = echarts.init(pieChartRef.value)
      pieChart.setOption({
        tooltip: {
          trigger: 'item',
          backgroundColor: 'var(--fd-bg-card)',
          borderColor: 'var(--fd-border)',
          textStyle: { color: 'var(--fd-text)' }
        },
        legend: {
          orient: 'vertical',
          right: 20,
          top: 'center',
          textStyle: { color: 'var(--fd-text-secondary)', fontSize: 12 }
        },
        series: [{
          name: '产品类型',
          type: 'pie',
          radius: ['42%', '72%'],
          center: ['38%', '50%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 6,
            borderColor: 'var(--fd-bg-card)',
            borderWidth: 2
          },
          label: { show: false },
          emphasis: {
            label: { show: true, fontSize: 13, fontWeight: 'normal' },
            itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0, 120, 212, 0.2)' }
          },
          data: data.length > 0 ? data : [{ name: '暂无数据', value: 0 }],
          color: ['#0078d4', '#107c10', '#d83b01', '#8764b8', '#008272', '#c239b3']
        }]
      })
    }
  } catch (error) {
    console.error('加载产品分布数据失败', error)
  }
}

let trendData = { dates: [], online: [], offline: [] }

const initLineChart = async () => {
  if (!lineChartRef.value) return
  lineChart = echarts.init(lineChartRef.value)

  // 从已加载的统计数据中获取趋势数据
  if (connectTrends.value) {
    trendData = connectTrends.value
  }

  renderLineChart()
}

const renderLineChart = () => {
  if (!lineChart) return
  lineChart.setOption({
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'var(--fd-bg-card)',
      borderColor: 'var(--fd-border)',
      textStyle: { color: 'var(--fd-text)' }
    },
    legend: {
      data: ['上线', '下线'],
      textStyle: { color: 'var(--fd-text-secondary)', fontSize: 12 }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: trendData.dates || [],
      axisLine: { lineStyle: { color: 'var(--fd-border)' } },
      axisLabel: { color: 'var(--fd-text-secondary)' }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      splitLine: { lineStyle: { color: 'var(--fd-border)' } },
      axisLabel: { color: 'var(--fd-text-secondary)' }
    },
    series: [
      {
        name: '上线',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: { width: 2.5, color: '#107c10' },
        itemStyle: { color: '#107c10' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(16, 124, 16, 0.2)' },
            { offset: 1, color: 'rgba(16, 124, 16, 0.02)' }
          ])
        },
        data: trendData.online || [0, 0, 0, 0, 0, 0, 0]
      },
      {
        name: '下线',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: { width: 2.5, color: '#a4262c' },
        itemStyle: { color: '#a4262c' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(164, 38, 44, 0.2)' },
            { offset: 1, color: 'rgba(164, 38, 44, 0.02)' }
          ])
        },
        data: trendData.offline || [0, 0, 0, 0, 0, 0, 0]
      }
    ]
  })
}

let refreshTimer = null

// WebSocket实时更新
watch(lastAlert, (newAlert) => {
  if (newAlert) {
    alerts.value.unshift({
      id: newAlert.alertId,
      content: newAlert.content,
      level: 'high',
      time: '刚刚'
    })
    statistics.todayAlerts++
  }
})

watch(lastDevice, () => { loadStatistics() })
watch(lastTelemetry, () => { loadStatistics() })

onMounted(() => {
  loadStatistics()
  loadConnectLogs()
  initPieChart()
  initLineChart()
  refreshTimer = setInterval(() => {
    loadStatistics()
    loadConnectLogs()
  }, 30000)
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (refreshTimer) clearInterval(refreshTimer)
  if (pieChart) pieChart.dispose()
  if (lineChart) lineChart.dispose()
  window.removeEventListener('resize', handleResize)
})

const handleResize = () => {
  pieChart?.resize()
  lineChart?.resize()
}
</script>

<style scoped>
.dashboard {
  max-width: 1400px;
  margin: 0 auto;
}

/* 欢迎区域 */
.welcome-section {
  margin-bottom: 24px;
  padding: 28px 32px;
  background: var(--glass-bg);
  backdrop-filter: var(--glass-blur) var(--glass-saturate);
  -webkit-backdrop-filter: var(--glass-blur) var(--glass-saturate);
  border: var(--glass-border);
  border-radius: var(--radius-2xl);
  box-shadow: var(--shadow-8), var(--glass-inner-glow);
}

.welcome-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.welcome-text h1 {
  font-size: 24px;
  font-weight: 600;
  color: var(--fd-text);
  margin-bottom: 4px;
}

.welcome-text p {
  font-size: 14px;
  color: var(--fd-text-secondary);
}

.welcome-section .fd-btn-secondary {
  background: rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(10px);
  border: var(--glass-border);
  color: var(--fd-text);
}

.welcome-section .fd-btn-secondary:hover {
  background: rgba(255, 255, 255, 0.6);
}

[data-theme="dark"] .welcome-section .fd-btn-secondary {
  background: rgba(15, 23, 42, 0.4);
  color: var(--fd-text);
}

/* Bento Grid */
.bento-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  grid-auto-rows: minmax(180px, auto);
  gap: 24px;
}

.bento-col-2 {
  grid-column: span 2;
}

.bento-row-2 {
  grid-row: span 2;
}

/* 统计卡片 (bento-card已包含背景等样式) */
.stat-card {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 24px;
}

.stat-icon {
  width: 44px;
  height: 44px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-icon.primary { background: var(--fd-primary); }
.stat-icon.success { background: var(--fd-success); }
.stat-icon.warning { background: var(--fd-warning); }
.stat-icon.info { background: #008272; }

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: var(--fd-text);
  line-height: 1;
}

.stat-label {
  font-size: 13px;
  color: var(--fd-text-secondary);
  margin-top: 4px;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 500;
}

.stat-trend.up { color: var(--fd-success); }
.stat-trend.down { color: var(--fd-error); }

.stat-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  font-weight: 500;
}

.stat-badge.online { color: var(--fd-success); }

.badge-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: var(--fd-success);
  animation: pulse 2s infinite;
}

/* 图表卡片 */
.chart-card,
.alert-card,
.log-card {
  padding: 24px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.card-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--fd-text);
}

.chart-container {
  height: 260px;
}

.view-all-btn {
  font-size: 13px;
  padding: 4px 10px;
}

/* 告警列表 */
.alert-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 260px;
  overflow-y: auto;
}

.alert-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: var(--fd-bg-hover);
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
  cursor: pointer;
}

.alert-item:hover {
  background: var(--fd-bg-pressed);
}

.alert-item.critical .alert-indicator { background: var(--fd-error); }
.alert-item.high .alert-indicator { background: var(--fd-warning); }
.alert-item.medium .alert-indicator { background: var(--fd-primary); }

.alert-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.alert-item.critical .alert-indicator {
  animation: glow 1.5s infinite;
}

@keyframes glow {
  0%, 100% { box-shadow: 0 0 4px rgba(164, 38, 44, 0.4); }
  50% { box-shadow: 0 0 10px rgba(164, 38, 44, 0.6); }
}

.alert-content {
  flex: 1;
  min-width: 0;
}

.alert-text {
  font-size: 13px;
  color: var(--fd-text);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.alert-time {
  font-size: 12px;
  color: var(--fd-text-tertiary);
  margin-top: 2px;
}

.empty-alerts {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: var(--fd-text-tertiary);
}

.empty-alerts p {
  margin-top: 12px;
  font-size: 13px;
}

/* 日志列表 */
.log-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
  max-height: 260px;
  overflow-y: auto;
}

.log-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
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

.log-content {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
}

.log-device {
  font-family: var(--font-mono);
  font-size: 13px;
  color: var(--fd-text);
}

.log-event {
  padding: 2px 8px;
  border-radius: var(--radius-sm);
  font-size: 11px;
  font-weight: 500;
}

.log-event.online {
  background: var(--fd-success-light);
  color: var(--fd-success);
}

.log-event.offline {
  background: var(--fd-error-light);
  color: var(--fd-error);
}

.log-time {
  font-size: 12px;
  color: var(--fd-text-tertiary);
  flex-shrink: 0;
}

/* 响应式 */
@media (max-width: 1200px) {
  .bento-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .bento-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  .bento-col-2,
  .bento-row-2 {
    grid-column: span 1;
    grid-row: span 1;
  }
  .welcome-content {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
}
</style>
