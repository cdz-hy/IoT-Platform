<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 256 256"
    xmlns="http://www.w3.org/2000/svg"
    class="iot-icon"
  >
    <!-- 外层波纹扩散 -->
    <circle
      cx="128" cy="128" r="76"
      fill="none"
      stroke="#1677FF"
      stroke-width="2"
      opacity="0"
      class="ripple-expand ripple-1"
    />
    <circle
      cx="128" cy="128" r="76"
      fill="none"
      stroke="#1677FF"
      stroke-width="2"
      opacity="0"
      class="ripple-expand ripple-2"
    />
    <circle
      cx="128" cy="128" r="76"
      fill="none"
      stroke="#1677FF"
      stroke-width="2"
      opacity="0"
      class="ripple-expand ripple-3"
    />

    <!-- 轨道圆环 -->
    <circle
      cx="128" cy="128" r="80"
      fill="none"
      stroke="#1677FF"
      stroke-width="1.5"
      opacity="0.15"
      stroke-dasharray="6 6"
      class="orbit-track"
    />

    <!-- 旋转的设备节点组 -->
    <g class="rotating-devices">
      <!-- 连接线（从设备到中心） -->
      <line x1="128" y1="72" x2="128" y2="94"
            stroke="#1677FF" stroke-width="1.5" opacity="0.4"
            class="device-line"/>
      <line x1="76" y1="168" x2="97" y2="150"
            stroke="#1677FF" stroke-width="1.5" opacity="0.4"
            class="device-line"/>
      <line x1="180" y1="168" x2="159" y2="150"
            stroke="#1677FF" stroke-width="1.5" opacity="0.4"
            class="device-line"/>

      <!-- 设备节点1（顶部） -->
      <g class="device-node">
        <circle cx="128" cy="60" r="16" fill="#1677FF" opacity="0" class="device-glow"/>
        <circle cx="128" cy="60" r="12" fill="#1677FF" class="device-dot"/>
        <circle cx="128" cy="60" r="5" fill="white" opacity="0.9"/>
      </g>

      <!-- 设备节点2（左下） -->
      <g class="device-node">
        <circle cx="68" cy="172" r="16" fill="#1677FF" opacity="0" class="device-glow"/>
        <circle cx="68" cy="172" r="12" fill="#1677FF" class="device-dot"/>
        <circle cx="68" cy="172" r="5" fill="white" opacity="0.9"/>
      </g>

      <!-- 设备节点3（右下） -->
      <g class="device-node">
        <circle cx="188" cy="172" r="16" fill="#1677FF" opacity="0" class="device-glow"/>
        <circle cx="188" cy="172" r="12" fill="#1677FF" class="device-dot"/>
        <circle cx="188" cy="172" r="5" fill="white" opacity="0.9"/>
      </g>
    </g>

    <!-- 静态MQTT波纹 -->
    <circle
      cx="128" cy="128" r="58"
      fill="none"
      stroke="#1677FF"
      stroke-width="3"
      opacity="0.12"
    />

    <!-- 中心圆环 -->
    <circle
      cx="128" cy="128"
      r="34"
      fill="none"
      stroke="#1677FF"
      stroke-width="6"
      class="center-ring"
    />

    <!-- 中心圆环光晕 -->
    <circle
      cx="128" cy="128"
      r="34"
      fill="none"
      stroke="#1677FF"
      stroke-width="12"
      opacity="0"
      class="center-glow"
    />

    <!-- 中心圆点 -->
    <circle
      cx="128" cy="128"
      r="16"
      fill="#36CFC9"
      class="center-dot"
    />

    <!-- 中心圆点光晕 -->
    <circle
      cx="128" cy="128"
      r="16"
      fill="#36CFC9"
      opacity="0"
      class="center-dot-glow"
    />
  </svg>
</template>

<script setup>
defineProps({
  size: {
    type: [Number, String],
    default: 48
  }
})
</script>

<style scoped>
.iot-icon {
  filter: drop-shadow(0 2px 8px rgba(22, 119, 255, 0.25));
  transition: all 300ms ease-out;
}

.iot-icon:hover {
  filter: drop-shadow(0 4px 16px rgba(22, 119, 255, 0.35));
  transform: scale(1.08);
}

/* ==================== 波纹扩散动画 ==================== */
.ripple-expand {
  transform-origin: 128px 128px;
}

.ripple-1 {
  animation: rippleExpand 3s ease-out infinite;
}

.ripple-2 {
  animation: rippleExpand 3s ease-out infinite;
  animation-delay: 1s;
}

.ripple-3 {
  animation: rippleExpand 3s ease-out infinite;
  animation-delay: 2s;
}

@keyframes rippleExpand {
  0% {
    r: 40;
    opacity: 0.4;
    stroke-width: 3;
  }
  100% {
    r: 100;
    opacity: 0;
    stroke-width: 1;
  }
}

/* ==================== 轨道 ==================== */
.orbit-track {
  transform-origin: 128px 128px;
  animation: orbitTrackRotate 30s linear infinite;
}

@keyframes orbitTrackRotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* ==================== 旋转设备组 ==================== */
.rotating-devices {
  transform-origin: 128px 128px;
  animation: devicesRotate 15s linear infinite;
}

@keyframes devicesRotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 设备连线 */
.device-line {
  stroke-dasharray: 4 4;
  animation: lineFlow 1.5s linear infinite;
}

@keyframes lineFlow {
  from {
    stroke-dashoffset: 0;
  }
  to {
    stroke-dashoffset: -16;
  }
}

/* 设备节点呼吸 */
.device-node {
  animation: deviceBreathe 2s ease-in-out infinite;
}

@keyframes deviceBreathe {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.85;
  }
}

/* 设备光晕 */
.device-glow {
  animation: deviceGlow 2s ease-in-out infinite;
}

@keyframes deviceGlow {
  0%, 100% {
    opacity: 0;
    r: 16;
  }
  50% {
    opacity: 0.3;
    r: 22;
  }
}

/* 设备圆点脉冲 */
.device-dot {
  animation: deviceDotPulse 2s ease-in-out infinite;
}

@keyframes deviceDotPulse {
  0%, 100% {
    r: 12;
  }
  50% {
    r: 13;
  }
}

/* ==================== 中心圆环 ==================== */
.center-ring {
  transform-origin: 128px 128px;
  animation: ringPulse 3s ease-in-out infinite;
}

@keyframes ringPulse {
  0%, 100% {
    stroke-width: 6;
    opacity: 1;
  }
  50% {
    stroke-width: 5;
    opacity: 0.9;
  }
}

/* 中心圆环光晕 */
.center-glow {
  transform-origin: 128px 128px;
  animation: ringGlow 3s ease-in-out infinite;
}

@keyframes ringGlow {
  0%, 100% {
    opacity: 0;
    stroke-width: 12;
  }
  50% {
    opacity: 0.15;
    stroke-width: 16;
  }
}

/* ==================== 中心圆点 ==================== */
.center-dot {
  transform-origin: 128px 128px;
  animation: dotPulse 2s ease-in-out infinite;
}

@keyframes dotPulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.08);
    opacity: 0.9;
  }
}

/* 中心圆点光晕 */
.center-dot-glow {
  transform-origin: 128px 128px;
  animation: dotGlow 2s ease-in-out infinite;
}

@keyframes dotGlow {
  0%, 100% {
    opacity: 0;
    transform: scale(1);
  }
  50% {
    opacity: 0.3;
    transform: scale(1.5);
  }
}
</style>
