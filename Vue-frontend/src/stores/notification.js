import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

const STORAGE_KEY = 'iot_notifications'

export const useNotificationStore = defineStore('notification', () => {
  // 从 localStorage 加载通知
  const notifications = ref(loadNotifications())

  // 未读数量
  const unreadCount = computed(() => notifications.value.filter(n => !n.read).length)

  // 按时间倒序排列
  const sortedNotifications = computed(() =>
    [...notifications.value].sort((a, b) => b.timestamp - a.timestamp)
  )

  // 只显示未读的
  const unreadNotifications = computed(() =>
    sortedNotifications.value.filter(n => !n.read)
  )

  function loadNotifications() {
    try {
      const saved = localStorage.getItem(STORAGE_KEY)
      if (saved) {
        return JSON.parse(saved)
      }
    } catch (e) {
      // ignore
    }
    return []
  }

  function saveNotifications() {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(notifications.value))
  }

  // 添加通知
  function addNotification({ content, type = 'info', alertId = null, deviceId = null }) {
    const notification = {
      id: Date.now() + Math.random(),
      content,
      type, // info, warning, danger
      alertId,
      deviceId,
      read: false,
      timestamp: Date.now(),
      time: formatTime(Date.now())
    }
    notifications.value.unshift(notification)

    // 最多保留 100 条
    if (notifications.value.length > 100) {
      notifications.value = notifications.value.slice(0, 100)
    }

    saveNotifications()
    return notification
  }

  // 标记单个为已读
  function markAsRead(id) {
    const notification = notifications.value.find(n => n.id === id)
    if (notification) {
      notification.read = true
      saveNotifications()
    }
  }

  // 标记所有为已读
  function markAllAsRead() {
    notifications.value.forEach(n => {
      n.read = true
    })
    saveNotifications()
  }

  // 删除通知
  function removeNotification(id) {
    notifications.value = notifications.value.filter(n => n.id !== id)
    saveNotifications()
  }

  // 清空所有通知
  function clearAll() {
    notifications.value = []
    saveNotifications()
  }

  // 处理 WebSocket 告警消息
  function handleAlertMessage(alertData) {
    const typeMap = {
      'error': 'danger',
      'warn': 'warning',
      'info': 'info'
    }

    addNotification({
      content: alertData.content || `设备 ${alertData.deviceId} 触发告警`,
      type: typeMap[alertData.alertType] || 'warning',
      alertId: alertData.alertId,
      deviceId: alertData.deviceId
    })
  }

  // 处理设备状态变更
  function handleDeviceStatusChange(deviceData) {
    if (deviceData.status === 'offline') {
      addNotification({
        content: `设备 ${deviceData.deviceName || deviceData.deviceId} 已离线`,
        type: 'warning',
        deviceId: deviceData.deviceId
      })
    }
  }

  function formatTime(timestamp) {
    const now = Date.now()
    const diff = now - timestamp

    if (diff < 60000) return '刚刚'
    if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
    if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`

    const date = new Date(timestamp)
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    return `${month}-${day} ${hours}:${minutes}`
  }

  return {
    notifications: sortedNotifications,
    unreadNotifications,
    unreadCount,
    addNotification,
    markAsRead,
    markAllAsRead,
    removeNotification,
    clearAll,
    handleAlertMessage,
    handleDeviceStatusChange
  }
})
