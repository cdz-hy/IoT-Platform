<template>
  <div class="products-page">
    <!-- 页面头部 -->
    <div class="fd-page-header animate-fade-in-up">
      <div class="header-content">
        <h1 class="fd-page-title">产品管理</h1>
        <p class="fd-page-subtitle">管理和配置物联网产品及其物模型</p>
      </div>
      <button class="fd-btn fd-btn-primary" @click="showAddDialog">
        <el-icon><Plus /></el-icon>
        创建产品
      </button>
    </div>

    <!-- 产品网格 -->
    <div class="product-grid animate-fade-in-up delay-200">
      <div
        v-for="product in products"
        :key="product.id"
        class="bento-card product-card"
        @click="viewDetail(product)"
      >
        <div class="card-header">
          <div class="product-icon" :style="{ background: getIconBg(product.type) }">
            <el-icon :size="22"><Box /></el-icon>
          </div>
          <div class="card-actions" @click.stop>
            <el-dropdown trigger="click" @command="(cmd) => handleCommand(cmd, product)">
              <button class="action-trigger">
                <el-icon><MoreFilled /></el-icon>
              </button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="edit">
                    <el-icon><Edit /></el-icon>
                    编辑
                  </el-dropdown-item>
                  <el-dropdown-item command="delete" divided>
                    <el-icon><Delete /></el-icon>
                    删除
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>

        <div class="card-body">
          <h3 class="product-name">{{ product.name }}</h3>
          <p class="product-id">{{ product.id }}</p>
          <div class="product-meta">
            <span class="meta-item">
              <el-icon><Collection /></el-icon>
              {{ product.type || '未分类' }}
            </span>
            <span class="meta-item">
              <el-icon><Cpu /></el-icon>
              {{ product.devices?.length || 0 }} 台设备
            </span>
          </div>
        </div>

        <div class="card-footer">
          <span class="time-text">
            <el-icon><Clock /></el-icon>
            {{ formatTime(product.createdTime) }}
          </span>
          <button class="detail-btn" @click.stop="viewDetail(product)">
            查看详情
            <el-icon><ArrowRight /></el-icon>
          </button>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="products.length === 0" class="liquid-glass-panel empty-state">
        <el-icon :size="64" color="var(--fd-border)"><Box /></el-icon>
        <h3>暂无产品</h3>
        <p>点击"创建产品"开始添加您的第一个产品</p>
        <button class="fd-btn fd-btn-primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          创建产品
        </button>
      </div>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑产品' : '创建产品'"
      width="460px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="产品名称" prop="name">
          <input v-model="form.name" placeholder="请输入产品名称" class="fd-input" />
        </el-form-item>
        <el-form-item label="产品类型" prop="type">
          <div class="type-selector">
            <div
              v-for="type in productTypes"
              :key="type.value"
              class="type-option"
              :class="{ active: form.type === type.value }"
              @click="form.type = type.value"
            >
              <div class="type-icon" :style="{ background: type.gradient }">
                <el-icon :size="18"><component :is="type.icon" /></el-icon>
              </div>
              <span class="type-label">{{ type.label }}</span>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <button class="fd-btn fd-btn-secondary" @click="dialogVisible = false">取消</button>
          <button class="fd-btn fd-btn-primary" @click="handleSubmit" :disabled="submitting">
            {{ submitting ? '提交中...' : '确定' }}
          </button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProducts, createProduct, updateProduct, deleteProduct } from '../api'

const router = useRouter()
const products = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const editId = ref('')

const form = reactive({ name: '', type: '' })
const rules = {
  name: [{ required: true, message: '请输入产品名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择产品类型', trigger: 'change' }]
}

const productTypes = [
  { label: '传感器', value: '传感器', icon: 'Odometer', gradient: 'linear-gradient(135deg, #0078d4, #008272)' },
  { label: '照明设备', value: '照明设备', icon: 'Sunny', gradient: 'linear-gradient(135deg, #d83b01, #c239b3)' },
  { label: '安防设备', value: '安防设备', icon: 'Lock', gradient: 'linear-gradient(135deg, #a4262c, #8764b8)' },
  { label: '环境监测', value: '环境监测', icon: 'Cloudy', gradient: 'linear-gradient(135deg, #107c10, #008272)' },
  { label: '智能家居', value: '智能家居', icon: 'House', gradient: 'linear-gradient(135deg, #8764b8, #0078d4)' },
  { label: '其他', value: '其他', icon: 'More', gradient: 'linear-gradient(135deg, #605e5c, #323130)' }
]

const getIconBg = (type) => {
  const typeObj = productTypes.find(t => t.value === type)
  return typeObj?.gradient || productTypes[5].gradient
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

const loadProducts = async () => {
  loading.value = true
  try {
    const res = await getProducts()
    products.value = res.data
  } catch (error) {
    console.error('加载产品列表失败', error)
  } finally {
    loading.value = false
  }
}

const showAddDialog = () => {
  isEdit.value = false
  form.name = ''
  form.type = ''
  dialogVisible.value = true
}

const showEditDialog = (row) => {
  isEdit.value = true
  editId.value = row.id
  form.name = row.name
  form.type = row.type
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateProduct(editId.value, form)
      ElMessage.success('更新成功')
    } else {
      await createProduct(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadProducts()
  } catch (error) {
    console.error('提交失败', error)
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('删除产品将同时删除其下的所有设备和数据，确定要继续吗？', '确认删除', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'warning'
  })
  try {
    await deleteProduct(row.id)
    ElMessage.success('删除成功')
    loadProducts()
  } catch (error) {
    console.error('删除失败', error)
  }
}

const handleCommand = (command, product) => {
  if (command === 'edit') showEditDialog(product)
  else if (command === 'delete') handleDelete(product)
}

const viewDetail = (row) => router.push(`/products/${row.id}`)

onMounted(loadProducts)
</script>

<style scoped>
.products-page {
  max-width: 1400px;
  margin: 0 auto;
}

.header-content {
  flex: 1;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  grid-auto-rows: minmax(200px, auto);
  gap: 24px;
}

/* product-card base styles handled by bento-card */
.product-card {
  cursor: pointer;
  padding: 0; /* Override bento-card padding to distribute it among header/body/footer */
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px 24px 0;
}

.product-icon {
  width: 44px;
  height: 44px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.action-trigger {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  border-radius: var(--radius-sm);
  color: var(--fd-text-secondary);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.action-trigger:hover {
  background: var(--fd-bg-hover);
  color: var(--fd-text);
}

.card-body {
  padding: 16px 24px;
  flex: 1;
}

.product-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--fd-text);
  margin-bottom: 4px;
}

.product-id {
  font-family: var(--font-mono);
  font-size: 11px;
  color: var(--fd-text-tertiary);
  margin-bottom: 12px;
}

.product-meta {
  display: flex;
  gap: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--fd-text-secondary);
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  border-top: var(--glass-border);
}

.time-text {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  color: var(--fd-text-tertiary);
}

.detail-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  background: transparent;
  border: none;
  color: var(--fd-primary);
  font-size: 12px;
  cursor: pointer;
  transition: all var(--transition-fast);
  border-radius: var(--radius-sm);
}

.detail-btn:hover {
  background: var(--fd-primary-alpha);
}

.detail-btn .el-icon {
  transition: transform var(--transition-fast);
}

.detail-btn:hover .el-icon {
  transform: translateX(2px);
}

.empty-state {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
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
  margin-bottom: 20px;
}

/* 对话框 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.type-selector {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}

.type-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 12px 8px;
  background: var(--fd-bg);
  border: 1px solid var(--fd-border);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.type-option:hover {
  border-color: var(--fd-border-hover);
}

.type-option.active {
  border-color: var(--fd-primary);
  background: var(--fd-primary-alpha);
}

.type-icon {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.type-label {
  font-size: 12px;
  color: var(--fd-text);
  font-weight: 500;
}

@media (max-width: 768px) {
  .fd-page-header {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  .product-grid {
    grid-template-columns: 1fr;
  }
}
</style>
