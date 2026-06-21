<template>
  <div class="app-container" :data-theme="theme">
    <!-- 全局流体渐变背景 -->
    <div class="liquid-gradient-bg"></div>

    <!-- 登录页面 -->
    <router-view v-if="isLoginPage" />

    <!-- 主布局 -->
    <el-container v-else class="main-layout">
      <!-- 侧边栏 -->
      <el-aside :width="isCollapsed ? '64px' : '240px'" class="sidebar" :class="{ collapsed: isCollapsed }">
        <!-- Logo区域 -->
        <div class="logo-section">
          <IoTIcon :size="isCollapsed ? 32 : 36" />
          <transition name="fade">
            <div class="logo-text" v-show="!isCollapsed">
              <h1>IoT Platform</h1>
              <p>物联网管理平台</p>
            </div>
          </transition>
        </div>

        <!-- 导航菜单 -->
        <nav class="nav-menu">
          <router-link
            v-for="item in menuItems"
            :key="item.path"
            :to="item.path"
            class="nav-item"
            :class="{ active: currentRoute === item.path }"
          >
            <div class="nav-icon">
              <el-icon :size="18"><component :is="item.icon" /></el-icon>
            </div>
            <transition name="fade">
              <span class="nav-label" v-show="!isCollapsed">{{ item.label }}</span>
            </transition>
            <transition name="fade">
              <span v-if="item.badge && !isCollapsed" class="nav-badge">{{ item.badge }}</span>
            </transition>
          </router-link>
        </nav>

        <!-- 底部折叠按钮 -->
        <div class="sidebar-footer">
          <button class="collapse-btn" @click="isCollapsed = !isCollapsed">
            <el-icon :size="16">
              <Fold v-if="!isCollapsed" />
              <Expand v-else />
            </el-icon>
          </button>
        </div>
      </el-aside>

      <!-- 主内容区 -->
      <el-container class="content-wrapper">
        <!-- 顶部导航 -->
        <el-header class="header">
          <div class="header-left">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/' }">
                <el-icon><HomeFilled /></el-icon>
              </el-breadcrumb-item>
              <el-breadcrumb-item>{{ currentPageTitle }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>

          <div class="header-right">
            <!-- 深色模式切换 -->
            <button class="header-action" @click="toggleTheme" :title="theme === 'dark' ? '切换到浅色模式' : '切换到深色模式'">
              <el-icon :size="16">
                <Moon v-if="theme === 'light'" />
                <Sunny v-else />
              </el-icon>
            </button>

            <!-- 全屏按钮 -->
            <button class="header-action" @click="toggleFullscreen">
              <el-icon :size="16">
                <FullScreen v-if="!isFullscreen" />
                <Aim v-else />
              </el-icon>
            </button>

            <!-- 通知按钮 -->
            <button class="header-action notification-btn" @click="showNotifications = !showNotifications">
              <el-badge :value="notificationCount" :max="99" :hidden="notificationCount === 0">
                <el-icon :size="16"><Bell /></el-icon>
              </el-badge>
            </button>

            <!-- 用户下拉菜单 -->
            <el-dropdown @command="handleCommand" trigger="click">
              <div class="user-dropdown">
                <div class="user-avatar">
                  <el-icon :size="16"><User /></el-icon>
                </div>
                <span class="user-name" v-show="!isCollapsed">管理员</span>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="apiDocs">
                    <el-icon><Document /></el-icon>
                    API 接口文档
                  </el-dropdown-item>
                  <el-dropdown-item command="logout" divided>
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 页面内容 -->
        <el-main class="main-content">
          <router-view v-slot="{ Component, route }">
            <transition name="page-fade" mode="out-in">
              <component :is="Component" :key="route.path" />
            </transition>
          </router-view>
        </el-main>
      </el-container>
    </el-container>

    <!-- 通知面板 -->
    <transition name="slide-right">
      <div v-if="showNotifications" class="notification-panel">
        <div class="notification-header">
          <h3>通知中心</h3>
          <button @click="showNotifications = false" class="close-btn">
            <el-icon><Close /></el-icon>
          </button>
        </div>
        <div class="notification-list">
          <div v-for="n in notifications" :key="n.id" class="notification-item" :class="n.type">
            <div class="notification-dot"></div>
            <div class="notification-content">
              <p class="notification-text">{{ n.content }}</p>
              <span class="notification-time">{{ n.time }}</span>
            </div>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import IoTIcon from './components/IoTIcon.vue'

const route = useRoute()
const router = useRouter()

const isCollapsed = ref(false)
const isFullscreen = ref(false)
const showNotifications = ref(false)
const notificationCount = ref(3)
const theme = ref('light')

const notifications = ref([
  { id: 1, content: '温度传感器TEMP_001温度过高: 38.5℃', type: 'warning', time: '2分钟前' },
  { id: 2, content: '烟雾报警器SMOKE_002检测到烟雾', type: 'danger', time: '5分钟前' },
  { id: 3, content: '设备DEV_003离线超过1小时', type: 'info', time: '10分钟前' }
])

const menuItems = [
  { path: '/dashboard', label: '统计看板', icon: 'DataBoard' },
  { path: '/products', label: '产品管理', icon: 'Box' },
  { path: '/devices', label: '设备管理', icon: 'Cpu', badge: 3 },
  { path: '/alerts', label: '告警管理', icon: 'Bell', badge: 2 },
  { path: '/connect-logs', label: '上下线记录', icon: 'Connection' },
  { path: '/simulator', label: '设备模拟器', icon: 'VideoCamera' }
]

const isLoginPage = computed(() => route.path === '/login')
const currentRoute = computed(() => route.path)

const currentPageTitle = computed(() => {
  const item = menuItems.find(m => m.path === route.path)
  return item?.label || '首页'
})

// 切换主题
const toggleTheme = () => {
  theme.value = theme.value === 'light' ? 'dark' : 'light'
  document.documentElement.setAttribute('data-theme', theme.value)
  localStorage.setItem('theme', theme.value)
}

// 监听系统主题变化
const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
mediaQuery.addEventListener('change', (e) => {
  if (!localStorage.getItem('theme')) {
    theme.value = e.matches ? 'dark' : 'light'
    document.documentElement.setAttribute('data-theme', theme.value)
  }
})

const handleCommand = (command) => {
  if (command === 'logout') {
    localStorage.removeItem('token')
    router.push('/login')
  } else if (command === 'apiDocs') {
    window.open('http://localhost:8080/api/swagger-ui.html', '_blank')
  }
}

const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
    isFullscreen.value = true
  } else {
    document.exitFullscreen()
    isFullscreen.value = false
  }
}

onMounted(() => {
  // 从localStorage读取主题设置
  const savedTheme = localStorage.getItem('theme')
  if (savedTheme) {
    theme.value = savedTheme
  } else {
    // 跟随系统主题
    theme.value = mediaQuery.matches ? 'dark' : 'light'
  }
  document.documentElement.setAttribute('data-theme', theme.value)
})
</script>

<style>
@import './assets/styles/design-system.css';
</style>

<style scoped>
.app-container {
  height: 100vh;
  overflow: hidden;
}

.main-layout {
  height: 100vh;
}

/* ==================== 侧边栏 ==================== */
.sidebar {
  background: var(--glass-bg);
  backdrop-filter: var(--glass-blur) var(--glass-saturate);
  -webkit-backdrop-filter: var(--glass-blur) var(--glass-saturate);
  border-right: var(--glass-border);
  transition: width var(--transition-base);
  display: flex;
  flex-direction: column;
  position: relative;
  z-index: 10;
  overflow: hidden;
}

/* Logo区域 */
.logo-section {
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  border-bottom: var(--glass-border);
  min-height: 60px;
}

.logo-text h1 {
  font-size: 16px;
  font-weight: 600;
  color: var(--fd-text);
  letter-spacing: 0.5px;
}

.logo-text p {
  font-size: 11px;
  color: var(--fd-text-tertiary);
  margin-top: 2px;
}

/* 导航菜单 */
.nav-menu {
  flex: 1;
  padding: 8px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: var(--radius-md);
  color: var(--fd-text-secondary);
  text-decoration: none;
  transition: all var(--transition-fast);
  font-weight: 500;
  font-size: 14px;
  position: relative;
}

.nav-item:hover {
  background: var(--fd-bg-hover);
  color: var(--fd-text);
}

.nav-item.active {
  background: var(--fd-primary-alpha);
  color: var(--fd-primary);
}

.nav-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 20px;
  background: var(--fd-primary);
  border-radius: 0 2px 2px 0;
}

.nav-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-sm);
  background: var(--fd-bg-hover);
  flex-shrink: 0;
  transition: all var(--transition-fast);
}

.nav-item.active .nav-icon {
  background: var(--fd-primary);
  color: white;
}

.nav-label {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.nav-badge {
  padding: 2px 8px;
  background: var(--fd-error);
  color: white;
  border-radius: 10px;
  font-size: 11px;
  font-weight: 600;
}

/* 侧边栏底部 */
.sidebar-footer {
  padding: 8px;
  border-top: var(--glass-border);
}

.collapse-btn {
  width: 100%;
  height: 32px;
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

.collapse-btn:hover {
  background: var(--fd-bg-hover);
  color: var(--fd-text);
}

/* ==================== 顶部导航 ==================== */
.header {
  height: 48px;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--glass-bg);
  backdrop-filter: var(--glass-blur) var(--glass-saturate);
  -webkit-backdrop-filter: var(--glass-blur) var(--glass-saturate);
  border-bottom: var(--glass-border);
  z-index: 5;
}

.header-left :deep(.el-breadcrumb) {
  font-size: 14px;
  font-weight: 400;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 4px;
}

.header-action {
  width: 32px;
  height: 32px;
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

.header-action:hover {
  background: var(--fd-bg-hover);
  color: var(--fd-text);
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 8px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: all var(--transition-fast);
  margin-left: 8px;
}

.user-dropdown:hover {
  background: var(--fd-bg-hover);
}

.user-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: var(--fd-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.user-name {
  font-size: 14px;
  font-weight: 400;
  color: var(--fd-text);
}

/* ==================== 主内容区 ==================== */
.content-wrapper {
  flex-direction: column;
  overflow: hidden;
}

.main-content {
  padding: 24px;
  overflow-y: auto;
  background: transparent; /* Changed from var(--fd-bg) so background shows through */
  scrollbar-gutter: stable;
}

/* ==================== 通知面板 ==================== */
.notification-panel {
  position: fixed;
  right: 0;
  top: 0;
  bottom: 0;
  width: 320px;
  background: var(--glass-bg);
  backdrop-filter: var(--glass-blur) var(--glass-saturate);
  -webkit-backdrop-filter: var(--glass-blur) var(--glass-saturate);
  border-left: var(--glass-border);
  z-index: 100;
  display: flex;
  flex-direction: column;
  box-shadow: var(--shadow-64), var(--glass-inner-glow);
}

.notification-header {
  padding: 16px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: var(--glass-border);
}

.notification-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--fd-text);
}

.close-btn {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  border-radius: var(--radius-sm);
  cursor: pointer;
  color: var(--fd-text-secondary);
  transition: all var(--transition-fast);
}

.close-btn:hover {
  background: var(--fd-bg-hover);
  color: var(--fd-text);
}

.notification-list {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
}

.notification-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  border-radius: var(--radius-md);
  margin-bottom: 4px;
  transition: all var(--transition-fast);
  cursor: pointer;
}

.notification-item:hover {
  background: var(--fd-bg-hover);
}

.notification-item.warning .notification-dot { background: var(--fd-warning); }
.notification-item.danger .notification-dot { background: var(--fd-error); }
.notification-item.info .notification-dot { background: var(--fd-primary); }

.notification-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-top: 5px;
  flex-shrink: 0;
}

.notification-content {
  flex: 1;
}

.notification-text {
  font-size: 13px;
  color: var(--fd-text);
  line-height: 1.5;
}

.notification-time {
  font-size: 12px;
  color: var(--fd-text-tertiary);
  margin-top: 4px;
  display: block;
}

/* ==================== 动画过渡 ==================== */
.fade-enter-active,
.fade-leave-active {
  transition: opacity var(--transition-fast);
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.15s ease-in-out;
}

.page-fade-enter-from,
.page-fade-leave-to {
  opacity: 0;
}

.slide-right-enter-active {
  animation: slideInRight 0.3s ease-out;
}

.slide-right-leave-active {
  animation: slideInRight 0.2s ease-in reverse;
}

/* 响应式 */
@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: 100;
    box-shadow: var(--shadow-16);
  }
}
</style>
