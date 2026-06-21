import request from '../utils/request'

// ==================== 认证接口 ====================

export function login(username, password) {
  return request.post('/auth/login', { username, password })
}

// ==================== 产品接口 ====================

export function getProducts() {
  return request.get('/products')
}

export function getProduct(id) {
  return request.get(`/products/${id}`)
}

export function createProduct(data) {
  return request.post('/products', data)
}

export function updateProduct(id, data) {
  return request.put(`/products/${id}`, data)
}

export function deleteProduct(id) {
  return request.delete(`/products/${id}`)
}

export function getProductCountByType() {
  return request.get('/products/statistics/type')
}

// 物模型属性
export function getProperties(productId) {
  return request.get(`/products/${productId}/properties`)
}

export function addProperty(productId, data) {
  return request.post(`/products/${productId}/properties`, data)
}

export function updateProperty(propertyId, data) {
  return request.put(`/products/properties/${propertyId}`, data)
}

export function deleteProperty(propertyId) {
  return request.delete(`/products/properties/${propertyId}`)
}

// 物模型服务
export function getServices(productId) {
  return request.get(`/products/${productId}/services`)
}

export function addService(productId, data) {
  return request.post(`/products/${productId}/services`, data)
}

export function deleteService(serviceId) {
  return request.delete(`/products/services/${serviceId}`)
}

// 物模型事件
export function getEvents(productId) {
  return request.get(`/products/${productId}/events`)
}

export function addEvent(productId, data) {
  return request.post(`/products/${productId}/events`, data)
}

export function deleteEvent(eventId) {
  return request.delete(`/products/events/${eventId}`)
}

// ==================== 设备接口 ====================

export function getDevices() {
  return request.get('/devices')
}

export function getDevice(deviceId) {
  return request.get(`/devices/${deviceId}`)
}

export function registerDevice(data) {
  return request.post('/devices/register', data)
}

export function updateDevice(deviceId, data) {
  return request.put(`/devices/${deviceId}`, data)
}

export function deleteDevice(deviceId) {
  return request.delete(`/devices/${deviceId}`)
}

export function getDevicesByProduct(productId) {
  return request.get(`/devices/product/${productId}`)
}

export function getConnectLogs(deviceId) {
  return request.get(`/devices/${deviceId}/connect-logs`)
}

export function getRecentConnectLogs() {
  return request.get('/devices/connect-logs/recent')
}

export function sendCommand(data) {
  return request.post('/devices/command', data)
}

export function getCommandLogs(deviceId) {
  return request.get(`/devices/${deviceId}/commands`)
}

export function getOnlineDeviceCount() {
  return request.get('/devices/statistics/online')
}

export function getTotalDeviceCount() {
  return request.get('/devices/statistics/total')
}

// ==================== 设备影子接口 ====================

export function getShadow(deviceId) {
  return request.get(`/shadows/${deviceId}`)
}

export function updateDesired(data) {
  return request.put('/shadows/desired', data)
}

export function getDelta(deviceId) {
  return request.get(`/shadows/${deviceId}/delta`)
}

// ==================== 告警接口 ====================

export function getAlertRules() {
  return request.get('/alerts/rules')
}

export function getAlertRulesByProduct(productId) {
  return request.get(`/alerts/rules/product/${productId}`)
}

export function createAlertRule(data) {
  return request.post('/alerts/rules', data)
}

export function updateAlertRule(ruleId, data) {
  return request.put(`/alerts/rules/${ruleId}`, data)
}

export function deleteAlertRule(ruleId) {
  return request.delete(`/alerts/rules/${ruleId}`)
}

export function getAlertRecords() {
  return request.get('/alerts/records')
}

export function getAlertRecordsByDevice(deviceId) {
  return request.get(`/alerts/records/device/${deviceId}`)
}

export function confirmAlert(alertId, remark) {
  return request.put(`/alerts/records/${alertId}/confirm`, { remark })
}

export function getTodayAlertCount() {
  return request.get('/alerts/statistics/today')
}

// ==================== 看板接口 ====================

export function getDashboardStatistics() {
  return request.get('/dashboard/statistics')
}

export function getProductDistribution() {
  return request.get('/dashboard/products/distribution')
}
