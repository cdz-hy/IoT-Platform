<template>
  <div class="connect-logs-page">
    <!-- 页面头部 -->
    <div class="fd-page-header animate-fade-in-up">
      <div class="header-content">
        <h1 class="fd-page-title">上下线记录</h1>
        <p class="fd-page-subtitle">查看所有设备的连接和断开事件</p>
      </div>
      <button class="fd-btn fd-btn-secondary" @click="loadLogs">
        <el-icon><Refresh /></el-icon>
        刷新
      </button>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row animate-fade-in-up delay-100">
      <div class="stat-card">
        <div class="stat-icon success">
          <el-icon :size="18"><Top /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ onlineCount }}</span>
          <span class="stat-label">上线事件</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon error">
          <el-icon :size="18"><Bottom /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ offlineCount }}</span>
          <span class="stat-label">下线事件</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon primary">
          <el-icon :size="18"><Connection /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ logs.length }}</span>
          <span class="stat-label">总记录数</span>
        </div>
      </div>
    </div>

    <!-- 记录列表 -->
    <div class="bento-card animate-fade-in-up delay-200">
      <div class="card-header" style="padding-bottom: 20px;">
        <h3>最近事件</h3>
        <div class="filter-group">
          <button
            v-for="filter in filters"
            :key="filter.value"
            class="filter-btn"
            :class="{ active: currentFilter === filter.value }"
            @click="currentFilter = filter.value"
          >
            {{ filter.label }}
          </button>
        </div>
      </div>

      <div class="fd-card-body">
        <div class="logs-list">
          <div v-for="(log, index) in filteredLogs" :key="log.id" class="log-item" :style="{ animationDelay: `${index * 0.05}s` }">
            <div class="log-indicator" :class="log.eventType">
              <el-icon v-if="log.eventType === 'online'"><Top /></el-icon>
              <el-icon v-else><Bottom /></el-icon>
            </div>
            <div class="log-content">
              <div class="log-header">
                <span class="device-id">{{ log.deviceId }}</span>
                <span class="event-type" :class="log.eventType">
                  {{ log.eventType === 'online' ? '上线' : '下线' }}
                </span>
              </div>
              <div class="log-time">
                <el-icon><Clock /></el-icon>
                {{ formatTime(log.eventTime) }}
              </div>
            </div>
            <button class="fd-btn fd-btn-subtle view-btn" @click="$router.push(`/devices/${log.deviceId}`)">
              查看设备
              <el-icon><ArrowRight /></el-icon>
            </button>
          </div>

          <div v-if="filteredLogs.length === 0" class="empty-state">
            <el-icon :size="64" color="var(--fd-border)"><Connection /></el-icon>
            <h3>暂无记录</h3>
            <p>当设备上线或下线时，事件将显示在这里</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getRecentConnectLogs } from '../api'

const logs = ref([])
const loading = ref(false)
const currentFilter = ref('all')

const filters = [
  { label: '全部', value: 'all' },
  { label: '上线', value: 'online' },
  { label: '下线', value: 'offline' }
]

const filteredLogs = computed(() => {
  if (currentFilter.value === 'all') return logs.value
  return logs.value.filter(log => log.eventType === currentFilter.value)
})

const onlineCount = computed(() => logs.value.filter(l => l.eventType === 'online').length)
const offlineCount = computed(() => logs.value.filter(l => l.eventType === 'offline').length)

const formatTime = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)} 分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)} 小时前`
  return date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

const loadLogs = async () => {
  loading.value = true
  try {
    const res = await getRecentConnectLogs()
    logs.value = res.data
  } catch (error) {
    console.error('加载记录失败', error)
  } finally {
    loading.value = false
  }
}

onMounted(loadLogs)
</script>

<style scoped>
.connect-logs-page {
  max-width: 1200px;
  margin: 0 auto;
}

.header-content {
  flex: 1;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 20px;
  background: var(--glass-bg);
  backdrop-filter: blur(10px);
  border: var(--glass-border);
  border-radius: var(--radius-2xl);
  transition: all var(--transition-base);
  box-shadow: var(--shadow-8), var(--glass-inner-glow);
}

[data-theme="dark"] .stat-card {
  background: rgba(15, 23, 42, 0.4);
}

.stat-card:hover {
  box-shadow: var(--shadow-16), var(--glass-inner-glow);
  transform: translateY(-4px);
}

.stat-icon {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-icon.success { background: var(--fd-success); }
.stat-icon.error { background: var(--fd-error); }
.stat-icon.primary { background: var(--fd-primary); }

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 22px;
  font-weight: 600;
  color: var(--fd-text);
}

.stat-label {
  font-size: 12px;
  color: var(--fd-text-secondary);
}

.fd-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.fd-card-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--fd-text);
}

.filter-group {
  display: flex;
  gap: 3px;
  background: var(--fd-bg);
  padding: 3px;
  border-radius: var(--radius-md);
}

.filter-btn {
  padding: 6px 14px;
  background: transparent;
  border: none;
  border-radius: var(--radius-sm);
  font-size: 12px;
  color: var(--fd-text-secondary);
  cursor: pointer;
  transition: all var(--transition-fast);
  font-weight: 500;
}

.filter-btn:hover {
  color: var(--fd-text);
}

.filter-btn.active {
  background: var(--fd-bg-card);
  color: var(--fd-text);
  box-shadow: var(--shadow-2);
}

.logs-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 600px;
  overflow-y: auto;
}

.log-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px 20px;
  background: rgba(255, 255, 255, 0.2);
  border: var(--glass-border);
  border-radius: var(--radius-xl);
  transition: all var(--transition-base);
  animation: fadeInUp 0.3s ease-out forwards;
  opacity: 0;
  box-shadow: var(--shadow-4);
}

[data-theme="dark"] .log-item {
  background: rgba(15, 23, 42, 0.3);
}

.log-item:hover {
  background: rgba(255, 255, 255, 0.4);
  box-shadow: var(--shadow-8);
  transform: translateX(4px);
}

[data-theme="dark"] .log-item:hover {
  background: rgba(15, 23, 42, 0.5);
}

.log-indicator {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-md);
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
  min-width: 0;
}

.log-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 4px;
}

.device-id {
  font-family: var(--font-mono);
  font-size: 13px;
  color: var(--fd-text);
}

.event-type {
  padding: 2px 8px;
  border-radius: var(--radius-sm);
  font-size: 11px;
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
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: var(--fd-text-secondary);
}

.view-btn {
  font-size: 12px;
  padding: 6px 12px;
  flex-shrink: 0;
}

.view-btn .el-icon {
  transition: transform var(--transition-fast);
}

.view-btn:hover .el-icon {
  transform: translateX(3px);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.empty-state h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--fd-text);
  margin: 16px 0 8px;
}

.empty-state p {
  font-size: 14px;
  color: var(--fd-text-secondary);
}

@media (max-width: 768px) {
  .stats-row {
    grid-template-columns: 1fr;
  }
  .log-item {
    flex-direction: column;
    align-items: flex-start;
  }
  .view-btn {
    width: 100%;
    justify-content: center;
  }
}
</style>
