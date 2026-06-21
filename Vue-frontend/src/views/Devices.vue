<template>
  <div class="devices-page">
    <!-- 页面头部 -->
    <div class="fd-page-header animate-fade-in-up">
      <div class="header-content">
        <h1 class="fd-page-title">设备管理</h1>
        <p class="fd-page-subtitle">管理和监控所有物联网设备</p>
      </div>
      <div class="header-actions">
        <div class="search-box">
          <el-icon><Search /></el-icon>
          <input v-model="searchText" placeholder="搜索设备..." class="fd-input search-input" />
        </div>
        <button class="fd-btn fd-btn-primary" @click="showRegisterDialog">
          <el-icon><Plus /></el-icon>
          注册设备
        </button>
      </div>
    </div>

    <!-- 筛选标签 -->
    <div class="filter-tags animate-fade-in-up delay-100">
      <button
        v-for="filter in filters"
        :key="filter.value"
        class="filter-tag"
        :class="{ active: currentFilter === filter.value }"
        @click="currentFilter = filter.value"
      >
        <span class="filter-dot" :class="filter.value"></span>
        {{ filter.label }}
        <span class="filter-count">{{ filter.count }}</span>
      </button>
    </div>

    <!-- 设备表格 -->
    <div class="liquid-glass-panel table-card animate-fade-in-up delay-200">
      <el-table :data="filteredDevices" v-loading="loading">
        <el-table-column prop="deviceId" label="设备ID" width="160">
          <template #default="{ row }">
            <div class="device-id-cell">
              <div class="device-dot" :class="row.status"></div>
              <span class="device-id">{{ row.deviceId }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="deviceName" label="设备名称" min-width="140">
          <template #default="{ row }">
            <span class="device-name">{{ row.deviceName || '未命名设备' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="所属产品" width="140">
          <template #default="{ row }">
            <span class="fd-tag">{{ row.product?.name || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="在线状态" width="110">
          <template #default="{ row }">
            <div class="fd-status" :class="row.status === 'online' ? 'fd-status-online' : 'fd-status-offline'">
              <span class="fd-status-dot"></span>
              <span>{{ row.status === 'online' ? '在线' : '离线' }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="lastOnlineTime" label="最后上线" width="150">
          <template #default="{ row }">
            <span class="time-text">{{ formatTime(row.lastOnlineTime) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <button class="action-btn view" @click="viewDetail(row)" title="查看详情">
                <el-icon><View /></el-icon>
              </button>
              <button class="action-btn shadow" @click="viewShadow(row)" title="设备影子">
                <el-icon><Document /></el-icon>
              </button>
              <button class="action-btn delete" @click="handleDelete(row)" title="删除">
                <el-icon><Delete /></el-icon>
              </button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="filteredDevices.length === 0 && !loading" class="empty-state">
        <el-icon :size="64" color="var(--fd-border)"><Cpu /></el-icon>
        <h3>暂无设备</h3>
        <p>点击"注册设备"添加您的第一个设备</p>
      </div>
    </div>

    <!-- 注册设备对话框 -->
    <el-dialog v-model="dialogVisible" title="注册设备" width="460px">
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="所属产品" prop="productId">
          <el-select v-model="form.productId" placeholder="请选择产品" style="width: 100%">
            <el-option v-for="p in products" :key="p.id" :label="p.name" :value="p.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="设备标识码" prop="nodeId">
          <input v-model="form.nodeId" placeholder="如 IMEI/MAC/序列号" class="fd-input" />
          <div class="fd-form-tip">设备的唯一标识符，用于MQTT连接认证</div>
        </el-form-item>
        <el-form-item label="设备名称">
          <input v-model="form.deviceName" placeholder="可选，便于识别的设备名称" class="fd-input" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <button class="fd-btn fd-btn-secondary" @click="dialogVisible = false">取消</button>
          <button class="fd-btn fd-btn-primary" @click="handleRegister" :disabled="submitting">
            {{ submitting ? '注册中...' : '注册设备' }}
          </button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDevices, registerDevice, deleteDevice, getProducts } from '../api'

const router = useRouter()
const devices = ref([])
const products = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const searchText = ref('')
const currentFilter = ref('all')

const form = reactive({ productId: '', nodeId: '', deviceName: '' })
const rules = {
  productId: [{ required: true, message: '请选择产品', trigger: 'change' }],
  nodeId: [{ required: true, message: '请输入设备标识码', trigger: 'blur' }]
}

const filters = computed(() => [
  { label: '全部', value: 'all', count: devices.value.length },
  { label: '在线', value: 'online', count: devices.value.filter(d => d.status === 'online').length },
  { label: '离线', value: 'offline', count: devices.value.filter(d => d.status !== 'online').length }
])

const filteredDevices = computed(() => {
  let result = devices.value
  if (currentFilter.value !== 'all') {
    result = result.filter(d => d.status === currentFilter.value)
  }
  if (searchText.value) {
    const search = searchText.value.toLowerCase()
    result = result.filter(d =>
      d.deviceId.toLowerCase().includes(search) ||
      (d.deviceName && d.deviceName.toLowerCase().includes(search))
    )
  }
  return result
})

const formatTime = (time) => {
  if (!time) return '从未上线'
  return new Date(time).toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

const loadDevices = async () => {
  loading.value = true
  try {
    const res = await getDevices()
    devices.value = res.data
  } catch (error) {
    console.error('加载设备列表失败', error)
  } finally {
    loading.value = false
  }
}

const loadProducts = async () => {
  try {
    const res = await getProducts()
    products.value = res.data
  } catch (error) {
    console.error('加载产品列表失败', error)
  }
}

const showRegisterDialog = () => {
  form.productId = ''
  form.nodeId = ''
  form.deviceName = ''
  dialogVisible.value = true
}

const handleRegister = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    await registerDevice(form)
    ElMessage.success('设备注册成功')
    dialogVisible.value = false
    loadDevices()
  } catch (error) {
    console.error('注册失败', error)
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('删除设备将清除所有相关数据，确定要继续吗？', '确认删除', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'warning'
  })
  try {
    await deleteDevice(row.deviceId)
    ElMessage.success('删除成功')
    loadDevices()
  } catch (error) {
    console.error('删除失败', error)
  }
}

const viewDetail = (row) => router.push(`/devices/${row.deviceId}`)
const viewShadow = (row) => router.push(`/devices/${row.deviceId}?tab=shadow`)

onMounted(() => {
  loadDevices()
  loadProducts()
})
</script>

<style scoped>
.devices-page {
  max-width: 1400px;
  margin: 0 auto;
}

.header-content {
  flex: 1;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 12px;
  background: var(--fd-bg);
  border: 1px solid var(--fd-border);
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
}

.search-box:focus-within {
  border-color: var(--fd-border-focus);
  box-shadow: 0 0 0 1px var(--fd-border-focus);
}

.search-box .el-icon {
  color: var(--fd-text-tertiary);
}

.search-input {
  border: none;
  background: transparent;
  padding: 8px 0;
  width: 180px;
}

.search-input:focus {
  box-shadow: none;
}

.filter-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.filter-tag {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 14px;
  background: var(--fd-bg-card);
  border: 1px solid var(--fd-border);
  border-radius: var(--radius-md);
  font-size: 13px;
  color: var(--fd-text-secondary);
  cursor: pointer;
  transition: all var(--transition-fast);
  font-weight: 500;
}

.filter-tag:hover {
  border-color: var(--fd-border-hover);
  color: var(--fd-text);
}

.filter-tag.active {
  background: var(--fd-primary-alpha);
  border-color: var(--fd-primary);
  color: var(--fd-primary);
}

.filter-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
}

.filter-dot.all { background: var(--fd-gray-500); }
.filter-dot.online { background: var(--fd-success); }
.filter-dot.offline { background: var(--fd-gray-400); }

.filter-count {
  padding: 1px 6px;
  background: var(--fd-bg-hover);
  border-radius: var(--radius-sm);
  font-size: 11px;
}

.filter-tag.active .filter-count {
  background: var(--fd-primary-alpha);
}

/* table-card base styling handled by liquid-glass-panel */
.table-card {
  overflow: hidden;
}

.device-id-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.device-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.device-dot.online { background: var(--fd-success); box-shadow: 0 0 6px rgba(16, 124, 16, 0.4); }
.device-dot.offline { background: var(--fd-gray-400); }

.device-id {
  font-family: var(--font-mono);
  font-size: 12px;
  color: var(--fd-primary);
}

.device-name {
  font-size: 13px;
  color: var(--fd-text);
}

.time-text {
  font-size: 12px;
  color: var(--fd-text-tertiary);
}

.action-buttons {
  display: flex;
  gap: 6px;
}

.action-btn {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.action-btn.view {
  background: var(--fd-primary-alpha);
  color: var(--fd-primary);
}

.action-btn.view:hover { background: rgba(0, 120, 212, 0.15); }

.action-btn.shadow {
  background: rgba(135, 100, 184, 0.1);
  color: #8764b8;
}

.action-btn.shadow:hover { background: rgba(135, 100, 184, 0.15); }

.action-btn.delete {
  background: var(--fd-error-light);
  color: var(--fd-error);
}

.action-btn.delete:hover { background: rgba(164, 38, 44, 0.15); }

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

/* 对话框 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 768px) {
  .fd-page-header {
    flex-direction: column;
    gap: 16px;
  }
  .header-actions {
    width: 100%;
    flex-direction: column;
  }
  .search-box {
    width: 100%;
  }
  .search-input {
    width: 100%;
  }
}
</style>
