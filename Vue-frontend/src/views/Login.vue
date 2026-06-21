<template>
  <div class="login-page">
    <div class="login-container">
      <div class="liquid-glass-panel login-bento">
        <!-- 左侧宣传区域 (Hero Section) -->
        <div class="bento-hero">
          <div class="hero-content">
            <div class="hero-header">
              <IoTIcon :size="56" />
              <div class="logo-text">
                <h1>IoT Platform</h1>
                <p>物联网管理平台</p>
              </div>
            </div>
            
            <div class="hero-description">
              <p>构建安全、可靠、高效的物理世界连接方案，实现海量设备的端到端一站式生命周期管理。</p>
            </div>

            <div class="hero-features">
              <div class="feature-item">
                <el-icon><Connection /></el-icon>
                <span>MQTT 协议设备接入</span>
              </div>
              <div class="feature-item">
                <el-icon><Odometer /></el-icon>
                <span>标准物模型属性定义</span>
              </div>
              <div class="feature-item">
                <el-icon><Document /></el-icon>
                <span>设备影子与离线同步</span>
              </div>
              <div class="feature-item">
                <el-icon><Warning /></el-icon>
                <span>规则引擎与实时告警</span>
              </div>
            </div>
            
            <div class="hero-footer">
              <p>物联网平台开发实训大作业项目 · 基于 Spring Boot + Vue 3 构建</p>
            </div>
          </div>
        </div>

        <!-- 右侧登录表单 -->
        <div class="bento-form">
          <div class="form-header">
            <h2>欢迎回来</h2>
            <p>请登录您的平台管理员账号以继续</p>
          </div>

          <el-form :model="form" :rules="rules" ref="formRef" class="login-form">
            <el-form-item prop="username">
              <div class="input-group">
                <label class="input-label">用户名</label>
                <div class="input-wrapper">
                  <el-icon class="input-icon"><User /></el-icon>
                  <input
                    v-model="form.username"
                    placeholder="请输入管理员用户名"
                    class="fd-input"
                  />
                </div>
              </div>
            </el-form-item>

            <el-form-item prop="password">
              <div class="input-group">
                <label class="input-label">密码</label>
                <div class="input-wrapper">
                  <el-icon class="input-icon"><Lock /></el-icon>
                  <input
                    v-model="form.password"
                    type="password"
                    placeholder="请输入密码"
                    class="fd-input"
                    @keyup.enter="handleLogin"
                  />
                </div>
              </div>
            </el-form-item>

            <div class="form-options">
              <label class="checkbox-label">
                <input type="checkbox" v-model="rememberMe" />
                <span>保持登录状态</span>
              </label>
              <a href="#" class="forgot-link">忘记密码？</a>
            </div>

            <button class="fd-btn fd-btn-primary login-btn" @click.prevent="handleLogin" :disabled="loading">
              <span v-if="!loading">登 录 系 统</span>
              <span v-else class="loading-text">
                <span class="loading-spinner"></span>
                身份验证中...
              </span>
            </button>
          </el-form>

          <!-- 底部提示 -->
          <div class="card-footer">
            <div class="divider">
              <span>开发测试账号</span>
            </div>
            <div class="demo-account" @click="fillDemo('admin', 'admin123')">
              <span class="account-role">
                <el-icon><Avatar /></el-icon> 系统管理员
              </span>
              <span class="account-creds">admin / admin123</span>
            </div>
          </div>
        </div>
      </div>

      
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api'
import IoTIcon from '../components/IoTIcon.vue'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const rememberMe = ref(false)

const form = reactive({
  username: 'admin',
  password: 'admin123'
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const fillDemo = (username, password) => {
  form.username = username
  form.password = password
}

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true

  try {
    const res = await login(form.username, form.password)
    localStorage.setItem('token', res.data.token)
    ElMessage.success('登录成功，欢迎回来！')
    router.push('/dashboard')
  } catch (error) {
    // 错误已在拦截器中统一处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  background: transparent;
}

/* 登录容器 */
.login-container {
  position: relative;
  z-index: 10;
  width: 100%;
  max-width: 900px;
  padding: 20px;
  animation: fadeInScale 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
}

/* 登录核心卡片 - Bento Grid Layout */
.login-bento {
  display: flex;
  flex-direction: row;
  padding: 0;
  overflow: hidden;
  border-radius: var(--radius-2xl);
  min-height: 520px;
}

/* 左侧宣传区域 */
.bento-hero {
  flex: 1.1;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.15) 0%, rgba(255, 255, 255, 0.05) 100%);
  border-right: var(--glass-border);
  padding: 48px;
  display: flex;
  flex-direction: column;
}

[data-theme="dark"] .bento-hero {
  background: linear-gradient(135deg, rgba(15, 23, 42, 0.4) 0%, rgba(15, 23, 42, 0.1) 100%);
}

.hero-content {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.hero-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.logo-text h1 {
  font-size: 26px;
  font-weight: 700;
  color: var(--fd-text);
  letter-spacing: -0.5px;
}

.logo-text p {
  font-size: 14px;
  color: var(--fd-primary);
  font-weight: 500;
  margin-top: 4px;
}

.hero-description {
  margin-bottom: 40px;
  font-size: 15px;
  color: var(--fd-text-secondary);
  line-height: 1.6;
}

.hero-features {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: auto;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 16px;
  background: rgba(255, 255, 255, 0.2);
  border: var(--glass-border);
  border-radius: var(--radius-lg);
  font-size: 13px;
  font-weight: 500;
  color: var(--fd-text);
  transition: all var(--transition-fast);
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.35);
  transform: translateY(-2px);
  box-shadow: var(--shadow-4);
}

[data-theme="dark"] .feature-item {
  background: rgba(15, 23, 42, 0.4);
}
[data-theme="dark"] .feature-item:hover {
  background: rgba(15, 23, 42, 0.6);
}

.feature-item .el-icon {
  color: var(--fd-primary);
  font-size: 18px;
}

.hero-footer {
  margin-top: 40px;
  padding-top: 20px;
  border-top: var(--glass-border);
  font-size: 12px;
  color: var(--fd-text-tertiary);
}

/* 右侧登录表单 */
.bento-form {
  flex: 1;
  padding: 48px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  background: rgba(255, 255, 255, 0.05);
}

[data-theme="dark"] .bento-form {
  background: rgba(0, 0, 0, 0.2);
}

.form-header {
  margin-bottom: 32px;
}

.form-header h2 {
  font-size: 24px;
  font-weight: 600;
  color: var(--fd-text);
  margin-bottom: 8px;
}

.form-header p {
  font-size: 14px;
  color: var(--fd-text-secondary);
}

.login-form {
  margin-bottom: 20px;
}

.input-group {
  width: 100%;
}

.input-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: var(--fd-text);
  margin-bottom: 6px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 12px;
  color: var(--fd-text-tertiary);
  z-index: 1;
}

.input-wrapper .fd-input {
  padding-left: 36px;
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  height: 42px;
}

.input-wrapper .fd-input:focus {
  background: rgba(255, 255, 255, 0.5);
  border-color: var(--fd-primary);
}

[data-theme="dark"] .input-wrapper .fd-input {
  background: rgba(15, 23, 42, 0.3);
  border-color: rgba(255, 255, 255, 0.1);
}

[data-theme="dark"] .input-wrapper .fd-input:focus {
  background: rgba(15, 23, 42, 0.5);
}

.form-options {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--fd-text-secondary);
  cursor: pointer;
}

.checkbox-label input[type="checkbox"] {
  width: 16px;
  height: 16px;
  accent-color: var(--fd-primary);
}

.forgot-link {
  font-size: 13px;
  color: var(--fd-primary);
  text-decoration: none;
  transition: color var(--transition-fast);
}

.forgot-link:hover {
  color: var(--fd-primary-hover);
  text-decoration: underline;
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  border-radius: var(--radius-md);
  box-shadow: 0 4px 12px rgba(0, 120, 212, 0.3);
}

.login-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 120, 212, 0.4);
}

.login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

.loading-text {
  display: flex;
  align-items: center;
  gap: 8px;
}

.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.card-footer {
  margin-top: 16px;
}

.divider {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: var(--glass-border);
}

.divider span {
  font-size: 12px;
  color: var(--fd-text-tertiary);
  white-space: nowrap;
}

.demo-account {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.2);
  border: var(--glass-border);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.demo-account:hover {
  background: rgba(255, 255, 255, 0.4);
  border-color: var(--fd-primary);
}

[data-theme="dark"] .demo-account {
  background: rgba(15, 23, 42, 0.3);
}

[data-theme="dark"] .demo-account:hover {
  background: rgba(15, 23, 42, 0.5);
  border-color: var(--fd-primary);
}

.account-role {
  font-size: 13px;
  font-weight: 500;
  color: var(--fd-text);
  display: flex;
  align-items: center;
  gap: 6px;
}

.account-creds {
  font-size: 13px;
  font-family: var(--font-mono);
  color: var(--fd-primary);
  font-weight: 500;
}

.copyright {
  text-align: center;
  margin-top: 24px;
}

.copyright p {
  font-size: 13px;
  color: var(--fd-text-tertiary);
  font-weight: 500;
}

/* 响应式 */
@media (max-width: 768px) {
  .login-bento {
    flex-direction: column;
  }
  .bento-hero {
    padding: 32px;
    border-right: none;
    border-bottom: var(--glass-border);
  }
  .bento-form {
    padding: 32px;
  }
  .hero-features {
    grid-template-columns: 1fr;
  }
}
</style>
