<template>
  <div class="product-detail-page">
    <!-- 页面头部 -->
    <div class="fd-page-header animate-fade-in-up">
      <div class="header-left">
        <button class="fd-btn fd-btn-subtle back-btn" @click="$router.back()">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </button>
        <div>
          <h1 class="fd-page-title">{{ product.name }}</h1>
          <p class="fd-page-subtitle">产品ID: {{ product.id }}</p>
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
          <span class="info-label">产品ID</span>
          <span class="info-value mono">{{ product.id }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">产品名称</span>
          <span class="info-value">{{ product.name }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">产品类型</span>
          <span class="info-value">
            <span class="fd-tag">{{ product.type || '未分类' }}</span>
          </span>
        </div>
        <div class="info-item">
          <span class="info-label">创建时间</span>
          <span class="info-value">{{ product.createdTime }}</span>
        </div>
      </div>
    </div>

    <!-- 物模型管理 -->
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
          <span class="tab-count">{{ tab.count }}</span>
        </button>
      </div>

      <!-- 属性列表 -->
      <div v-show="activeTab === 'properties'" class="model-content">
        <div class="content-header">
          <h3>属性定义</h3>
          <button class="fd-btn fd-btn-primary" @click="showAddProperty">
            <el-icon><Plus /></el-icon>
            添加属性
          </button>
        </div>
        <div class="model-list">
          <div v-for="prop in properties" :key="prop.id" class="model-item">
            <div class="item-icon property">
              <el-icon :size="16"><Odometer /></el-icon>
            </div>
            <div class="item-content">
              <div class="item-name">{{ prop.propertyName }}</div>
              <div class="item-meta">
                <span class="meta-tag">{{ prop.dataType }}</span>
                <span v-if="prop.unit" class="meta-tag">{{ prop.unit }}</span>
                <span v-if="prop.minValue || prop.maxValue" class="meta-tag">
                  {{ prop.minValue }} ~ {{ prop.maxValue }}
                </span>
                <span class="meta-tag access">{{ prop.accessMode }}</span>
              </div>
            </div>
            <button class="delete-btn" @click="handleDeleteProperty(prop)">
              <el-icon><Delete /></el-icon>
            </button>
          </div>
          <div v-if="properties.length === 0" class="empty-list">
            <p>暂无属性定义</p>
          </div>
        </div>
      </div>

      <!-- 服务列表 -->
      <div v-show="activeTab === 'services'" class="model-content">
        <div class="content-header">
          <h3>服务定义</h3>
          <button class="fd-btn fd-btn-primary" @click="showAddService">
            <el-icon><Plus /></el-icon>
            添加服务
          </button>
        </div>
        <div class="model-list">
          <div v-for="service in services" :key="service.id" class="model-item">
            <div class="item-icon service">
              <el-icon :size="16"><Setting /></el-icon>
            </div>
            <div class="item-content">
              <div class="item-name">{{ service.serviceName }}</div>
              <div class="item-meta">
                <span class="meta-desc">输入: {{ service.inputParams || '无' }}</span>
                <span class="meta-desc">输出: {{ service.outputParams || '无' }}</span>
              </div>
            </div>
            <button class="delete-btn" @click="handleDeleteService(service)">
              <el-icon><Delete /></el-icon>
            </button>
          </div>
          <div v-if="services.length === 0" class="empty-list">
            <p>暂无服务定义</p>
          </div>
        </div>
      </div>

      <!-- 事件列表 -->
      <div v-show="activeTab === 'events'" class="model-content">
        <div class="content-header">
          <h3>事件定义</h3>
          <button class="fd-btn fd-btn-primary" @click="showAddEvent">
            <el-icon><Plus /></el-icon>
            添加事件
          </button>
        </div>
        <div class="model-list">
          <div v-for="event in events" :key="event.id" class="model-item">
            <div class="item-icon event">
              <el-icon :size="16"><Bell /></el-icon>
            </div>
            <div class="item-content">
              <div class="item-name">{{ event.eventName }}</div>
              <div class="item-meta">
                <span class="meta-tag">{{ event.eventType }}</span>
                <span class="meta-desc">{{ event.description }}</span>
              </div>
            </div>
            <button class="delete-btn" @click="handleDeleteEvent(event)">
              <el-icon><Delete /></el-icon>
            </button>
          </div>
          <div v-if="events.length === 0" class="empty-list">
            <p>暂无事件定义</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加属性对话框 -->
    <el-dialog v-model="propertyDialogVisible" title="添加属性" width="460px">
      <el-form :model="propertyForm" label-position="top">
        <el-form-item label="属性名称">
          <input v-model="propertyForm.propertyName" placeholder="如 temperature" class="fd-input" />
        </el-form-item>
        <div class="form-row">
          <el-form-item label="数据类型" class="flex-1">
            <el-select v-model="propertyForm.dataType" style="width: 100%">
              <el-option value="int" label="int"></el-option>
              <el-option value="double" label="double"></el-option>
              <el-option value="boolean" label="boolean"></el-option>
              <el-option value="string" label="string"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="访问权限" class="flex-1">
            <el-select v-model="propertyForm.accessMode" style="width: 100%">
              <el-option value="read" label="只读(read)"></el-option>
              <el-option value="read_write" label="读写(read_write)"></el-option>
            </el-select>
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="最小值" class="flex-1">
            <input v-model="propertyForm.minValue" class="fd-input" />
          </el-form-item>
          <el-form-item label="最大值" class="flex-1">
            <input v-model="propertyForm.maxValue" class="fd-input" />
          </el-form-item>
          <el-form-item label="单位" class="flex-1">
            <input v-model="propertyForm.unit" placeholder="℃, %" class="fd-input" />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <button class="fd-btn fd-btn-secondary" @click="propertyDialogVisible = false">取消</button>
          <button class="fd-btn fd-btn-primary" @click="handleAddProperty">确定</button>
        </div>
      </template>
    </el-dialog>

    <!-- 添加服务对话框 -->
    <el-dialog v-model="serviceDialogVisible" title="添加服务" width="460px">
      <el-form :model="serviceForm" label-position="top">
        <el-form-item label="服务名称">
          <input v-model="serviceForm.serviceName" placeholder="如 重启设备" class="fd-input" />
        </el-form-item>
        <el-form-item label="输入参数">
          <textarea v-model="serviceForm.inputParams" placeholder='JSON格式' class="fd-input" rows="3" style="resize: vertical;"></textarea>
        </el-form-item>
        <el-form-item label="输出参数">
          <textarea v-model="serviceForm.outputParams" placeholder='JSON格式' class="fd-input" rows="3" style="resize: vertical;"></textarea>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <button class="fd-btn fd-btn-secondary" @click="serviceDialogVisible = false">取消</button>
          <button class="fd-btn fd-btn-primary" @click="handleAddService">确定</button>
        </div>
      </template>
    </el-dialog>

    <!-- 添加事件对话框 -->
    <el-dialog v-model="eventDialogVisible" title="添加事件" width="460px">
      <el-form :model="eventForm" label-position="top">
        <el-form-item label="事件名称">
          <input v-model="eventForm.eventName" placeholder="如 高温告警" class="fd-input" />
        </el-form-item>
        <el-form-item label="事件类型">
          <el-select v-model="eventForm.eventType" style="width: 100%">
            <el-option value="alert" label="告警(alert)"></el-option>
            <el-option value="info" label="信息(info)"></el-option>
            <el-option value="fault" label="故障(fault)"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <textarea v-model="eventForm.description" class="fd-input" rows="3" style="resize: vertical;"></textarea>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <button class="fd-btn fd-btn-secondary" @click="eventDialogVisible = false">取消</button>
          <button class="fd-btn fd-btn-primary" @click="handleAddEvent">确定</button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  getProduct, getProperties, addProperty, deleteProperty,
  getServices, addService, deleteService,
  getEvents, addEvent, deleteEvent
} from '../api'

const route = useRoute()
const productId = route.params.id

const product = ref({})
const properties = ref([])
const services = ref([])
const events = ref([])
const activeTab = ref('properties')

const tabs = computed(() => [
  { key: 'properties', label: '属性', icon: 'Odometer', count: properties.value.length },
  { key: 'services', label: '服务', icon: 'Setting', count: services.value.length },
  { key: 'events', label: '事件', icon: 'Bell', count: events.value.length }
])

const propertyDialogVisible = ref(false)
const serviceDialogVisible = ref(false)
const eventDialogVisible = ref(false)

const propertyForm = reactive({
  propertyName: '', dataType: 'double', minValue: '', maxValue: '', unit: '', accessMode: 'read'
})
const serviceForm = reactive({ serviceName: '', inputParams: '[]', outputParams: '[]' })
const eventForm = reactive({ eventName: '', eventType: 'alert', description: '' })

const loadProduct = async () => {
  try {
    const res = await getProduct(productId)
    product.value = res.data
  } catch (error) {
    console.error('加载产品详情失败', error)
  }
}

const loadProperties = async () => {
  try {
    const res = await getProperties(productId)
    properties.value = res.data
  } catch (error) {
    console.error('加载属性列表失败', error)
  }
}

const loadServices = async () => {
  try {
    const res = await getServices(productId)
    services.value = res.data
  } catch (error) {
    console.error('加载服务列表失败', error)
  }
}

const loadEvents = async () => {
  try {
    const res = await getEvents(productId)
    events.value = res.data
  } catch (error) {
    console.error('加载事件列表失败', error)
  }
}

const showAddProperty = () => {
  propertyForm.propertyName = ''
  propertyForm.dataType = 'double'
  propertyForm.minValue = ''
  propertyForm.maxValue = ''
  propertyForm.unit = ''
  propertyForm.accessMode = 'read'
  propertyDialogVisible.value = true
}

const handleAddProperty = async () => {
  try {
    await addProperty(productId, propertyForm)
    ElMessage.success('添加成功')
    propertyDialogVisible.value = false
    loadProperties()
  } catch (error) {
    console.error('添加属性失败', error)
  }
}

const handleDeleteProperty = async (row) => {
  try {
    await deleteProperty(row.id)
    ElMessage.success('删除成功')
    loadProperties()
  } catch (error) {
    console.error('删除属性失败', error)
  }
}

const showAddService = () => {
  serviceForm.serviceName = ''
  serviceForm.inputParams = '[]'
  serviceForm.outputParams = '[]'
  serviceDialogVisible.value = true
}

const handleAddService = async () => {
  try {
    await addService(productId, serviceForm)
    ElMessage.success('添加成功')
    serviceDialogVisible.value = false
    loadServices()
  } catch (error) {
    console.error('添加服务失败', error)
  }
}

const handleDeleteService = async (row) => {
  try {
    await deleteService(row.id)
    ElMessage.success('删除成功')
    loadServices()
  } catch (error) {
    console.error('删除服务失败', error)
  }
}

const showAddEvent = () => {
  eventForm.eventName = ''
  eventForm.eventType = 'alert'
  eventForm.description = ''
  eventDialogVisible.value = true
}

const handleAddEvent = async () => {
  try {
    await addEvent(productId, eventForm)
    ElMessage.success('添加成功')
    eventDialogVisible.value = false
    loadEvents()
  } catch (error) {
    console.error('添加事件失败', error)
  }
}

const handleDeleteEvent = async (row) => {
  try {
    await deleteEvent(row.id)
    ElMessage.success('删除成功')
    loadEvents()
  } catch (error) {
    console.error('删除事件失败', error)
  }
}

onMounted(() => {
  loadProduct()
  loadProperties()
  loadServices()
  loadEvents()
})
</script>

<style scoped>
.product-detail-page {
  max-width: 1000px;
  margin: 0 auto;
}

.fd-page-header {
  display: flex;
  align-items: center;
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
  grid-template-columns: repeat(2, 1fr);
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
  font-size: 14px;
  color: var(--fd-text);
  font-weight: 500;
}

.info-value.mono {
  font-family: var(--font-mono);
  font-size: 13px;
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
  font-size: 14px;
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

.tab-count {
  padding: 2px 8px;
  background: var(--fd-bg-hover);
  border-radius: var(--radius-sm);
  font-size: 11px;
}

.tab-btn.active .tab-count {
  background: var(--fd-primary-alpha);
}

.model-content {
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

.model-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.model-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.3);
  border: var(--glass-border);
  border-radius: var(--radius-xl);
  transition: all var(--transition-fast);
}

[data-theme="dark"] .model-item {
  background: rgba(15, 23, 42, 0.4);
}

.model-item:hover {
  background: var(--fd-bg-hover);
}

.item-icon {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.item-icon.property {
  background: var(--fd-primary-alpha);
  color: var(--fd-primary);
}

.item-icon.service {
  background: var(--fd-success-light);
  color: var(--fd-success);
}

.item-icon.event {
  background: var(--fd-warning-light);
  color: var(--fd-warning);
}

.item-content {
  flex: 1;
  min-width: 0;
}

.item-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--fd-text);
  margin-bottom: 4px;
}

.item-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.meta-tag {
  padding: 2px 8px;
  background: var(--fd-bg-hover);
  border-radius: var(--radius-sm);
  font-size: 11px;
  color: var(--fd-text-secondary);
}

.meta-tag.access {
  background: rgba(135, 100, 184, 0.1);
  color: #8764b8;
}

.meta-desc {
  font-size: 12px;
  color: var(--fd-text-tertiary);
}

.delete-btn {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  border-radius: var(--radius-sm);
  color: var(--fd-text-tertiary);
  cursor: pointer;
  transition: all var(--transition-fast);
  flex-shrink: 0;
}

.delete-btn:hover {
  background: var(--fd-error-light);
  color: var(--fd-error);
}

.empty-list {
  padding: 30px;
  text-align: center;
  color: var(--fd-text-secondary);
  font-size: 13px;
}

/* 对话框 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.form-row {
  display: flex;
  gap: 12px;
}

.flex-1 {
  flex: 1;
}

@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
  .form-row {
    flex-direction: column;
  }
}
</style>
