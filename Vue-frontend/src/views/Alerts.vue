<template>
  <div class="alerts-page">
    <div class="fd-page-header animate-fade-in-up">
      <div class="header-content">
        <h1 class="fd-page-title">告警管理</h1>
        <p class="fd-page-subtitle">配置告警规则和查看告警记录</p>
      </div>
    </div>

    <div class="content-grid animate-fade-in-up delay-100">
      <!-- 告警规则 -->
      <div class="bento-card">
        <div class="card-header" style="padding-bottom: 20px;">
          <h3>告警规则</h3>
          <button class="fd-btn fd-btn-primary" @click="showRuleDialog">
            <el-icon><Plus /></el-icon>
            添加规则
          </button>
        </div>
        <div class="fd-card-body">
          <div class="rules-list">
            <div v-for="rule in rules" :key="rule.id" class="rule-item">
              <div class="rule-icon">
                <el-icon :size="16"><Warning /></el-icon>
              </div>
              <div class="rule-content">
                <div class="rule-name">{{ rule.propertyName }} {{ rule.conditionExpr }}</div>
                <div class="rule-desc">{{ rule.alertContent }}</div>
              </div>
              <div class="rule-status" :class="{ enabled: rule.enabled }">
                {{ rule.enabled ? '启用' : '禁用' }}
              </div>
              <button class="delete-btn" @click="handleDeleteRule(rule)">
                <el-icon><Delete /></el-icon>
              </button>
            </div>
            <div v-if="rules.length === 0" class="empty-list">
              <p>暂无告警规则</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 告警记录 -->
      <div class="bento-card">
        <div class="card-header" style="padding-bottom: 20px;">
          <h3>告警记录</h3>
          <span class="record-count">共 {{ records.length }} 条</span>
        </div>
        <div class="fd-card-body">
          <div class="records-list">
            <div v-for="record in records" :key="record.id" class="record-item" :class="record.status">
              <div class="record-indicator"></div>
              <div class="record-content">
                <p class="record-text">{{ record.alertContent }}</p>
                <div class="record-meta">
                  <span class="record-device">
                    <el-icon><Cpu /></el-icon>
                    {{ record.deviceId }}
                  </span>
                  <span class="record-time">
                    <el-icon><Clock /></el-icon>
                    {{ record.alertTime }}
                  </span>
                </div>
              </div>
              <button
                v-if="record.status === 'pending'"
                class="fd-btn fd-btn-subtle confirm-btn"
                @click="handleConfirm(record)"
              >
                确认
              </button>
              <span v-else class="fd-tag fd-tag-success">已确认</span>
            </div>
            <div v-if="records.length === 0" class="empty-list">
              <el-icon :size="48" color="var(--fd-border)"><CircleCheck /></el-icon>
              <p>暂无告警记录</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加规则对话框 -->
    <el-dialog v-model="ruleDialogVisible" title="添加告警规则" width="460px">
      <el-form :model="ruleForm" label-position="top">
        <el-form-item label="产品">
          <el-select v-model="ruleForm.productId" placeholder="请选择产品" style="width: 100%">
            <el-option v-for="p in products" :key="p.id" :label="p.name" :value="p.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="属性名称">
          <input v-model="ruleForm.propertyName" placeholder="如 temperature" class="fd-input" />
        </el-form-item>
        <el-form-item label="条件表达式">
          <input v-model="ruleForm.conditionExpr" placeholder="如 > 35, < 10" class="fd-input" />
          <div class="fd-form-tip">支持运算符: >, <, >=, <=, ==</div>
        </el-form-item>
        <el-form-item label="告警内容">
          <textarea v-model="ruleForm.alertContent" placeholder="告警提示信息" class="fd-input" rows="3" style="resize: vertical;"></textarea>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <button class="fd-btn fd-btn-secondary" @click="ruleDialogVisible = false">取消</button>
          <button class="fd-btn fd-btn-primary" @click="handleCreateRule">确定</button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAlertRules, createAlertRule, deleteAlertRule, getAlertRecords, confirmAlert, getProducts } from '../api'

const rules = ref([])
const records = ref([])
const products = ref([])
const ruleDialogVisible = ref(false)

const ruleForm = reactive({
  productId: '',
  propertyName: '',
  conditionExpr: '',
  alertContent: ''
})

const loadRules = async () => {
  try {
    const res = await getAlertRules()
    rules.value = res.data
  } catch (error) {
    console.error('加载规则失败', error)
  }
}

const loadRecords = async () => {
  try {
    const res = await getAlertRecords()
    records.value = res.data
  } catch (error) {
    console.error('加载记录失败', error)
  }
}

const loadProducts = async () => {
  try {
    const res = await getProducts()
    products.value = res.data
  } catch (error) {
    console.error('加载产品失败', error)
  }
}

const showRuleDialog = () => {
  ruleForm.productId = ''
  ruleForm.propertyName = ''
  ruleForm.conditionExpr = ''
  ruleForm.alertContent = ''
  ruleDialogVisible.value = true
}

const handleCreateRule = async () => {
  try {
    await createAlertRule(ruleForm)
    ElMessage.success('创建成功')
    ruleDialogVisible.value = false
    loadRules()
  } catch (error) {
    console.error('创建失败', error)
  }
}

const handleDeleteRule = async (row) => {
  try {
    await deleteAlertRule(row.id)
    ElMessage.success('删除成功')
    loadRules()
  } catch (error) {
    console.error('删除失败', error)
  }
}

const handleConfirm = async (row) => {
  try {
    await confirmAlert(row.id, '已确认处理')
    ElMessage.success('确认成功')
    loadRecords()
  } catch (error) {
    console.error('确认失败', error)
  }
}

onMounted(() => {
  loadRules()
  loadRecords()
  loadProducts()
})
</script>

<style scoped>
.alerts-page {
  max-width: 1200px;
  margin: 0 auto;
}

.header-content {
  flex: 1;
}

.content-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.rules-list,
.records-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 500px;
  overflow-y: auto;
}

.rule-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.2);
  border: var(--glass-border);
  border-radius: var(--radius-xl);
  transition: all var(--transition-fast);
}

[data-theme="dark"] .rule-item {
  background: rgba(15, 23, 42, 0.3);
}

.rule-item:hover {
  background: rgba(255, 255, 255, 0.4);
}

[data-theme="dark"] .rule-item:hover {
  background: rgba(15, 23, 42, 0.5);
}

.rule-icon {
  width: 32px;
  height: 32px;
  border-radius: var(--radius-sm);
  background: var(--fd-warning-light);
  color: var(--fd-warning);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.rule-content {
  flex: 1;
  min-width: 0;
}

.rule-name {
  font-size: 13px;
  font-weight: 500;
  color: var(--fd-text);
  margin-bottom: 2px;
}

.rule-desc {
  font-size: 12px;
  color: var(--fd-text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.rule-status {
  padding: 3px 10px;
  border-radius: var(--radius-sm);
  font-size: 11px;
  background: var(--fd-bg-hover);
  color: var(--fd-text-secondary);
}

.rule-status.enabled {
  background: var(--fd-success-light);
  color: var(--fd-success);
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
}

.delete-btn:hover {
  background: var(--fd-error-light);
  color: var(--fd-error);
}

.record-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.2);
  border: var(--glass-border);
  border-radius: var(--radius-xl);
  margin-bottom: 8px;
  transition: all var(--transition-fast);
}

[data-theme="dark"] .record-item {
  background: rgba(15, 23, 42, 0.3);
}

.record-item:hover {
  background: rgba(255, 255, 255, 0.4);
}

[data-theme="dark"] .record-item:hover {
  background: rgba(15, 23, 42, 0.5);
}

.record-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-top: 6px;
  flex-shrink: 0;
}

.record-item.pending .record-indicator {
  background: var(--fd-warning);
  animation: pulse 2s infinite;
}

.record-item.confirmed .record-indicator {
  background: var(--fd-success);
}

.record-content {
  flex: 1;
  min-width: 0;
}

.record-text {
  font-size: 13px;
  color: var(--fd-text);
  margin-bottom: 6px;
  line-height: 1.5;
}

.record-meta {
  display: flex;
  gap: 14px;
}

.record-device,
.record-time {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  color: var(--fd-text-tertiary);
}

.confirm-btn {
  font-size: 12px;
  padding: 4px 10px;
  flex-shrink: 0;
}

.record-count {
  font-size: 12px;
  color: var(--fd-text-secondary);
}

.empty-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: var(--fd-text-tertiary);
}

.empty-list p {
  margin-top: 12px;
  font-size: 13px;
}

/* 对话框 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 768px) {
  .content-grid {
    grid-template-columns: 1fr;
  }
}
</style>
