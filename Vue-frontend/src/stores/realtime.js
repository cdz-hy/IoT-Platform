import { defineStore } from 'pinia'
import { ref } from 'vue'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client/dist/sockjs.min.js'
import { useNotificationStore } from './notification.js'

export const useRealtimeStore = defineStore('realtime', () => {
  const connected = ref(false)
  const lastTelemetry = ref(null)
  const lastShadow = ref(null)
  const lastAlert = ref(null)
  const lastCommand = ref(null)
  const lastDevice = ref(null)
  let client = null
  let connecting = false

  function connect() {
    if (connecting || (client && connected.value)) return
    connecting = true

    try {
      const wsUrl = `${window.location.origin}/ws`
      client = new Client({
        webSocketFactory: () => new SockJS(wsUrl),
        reconnectDelay: 15000,
        heartbeatIncoming: 10000,
        heartbeatOutgoing: 10000,
        connectionTimeout: 3000,
        onConnect: () => {
          connected.value = true
          connecting = false

          const notificationStore = useNotificationStore()

          client.subscribe('/topic/device/telemetry', (msg) => {
            try { lastTelemetry.value = JSON.parse(msg.body) } catch (e) { /* ignore */ }
          })
          client.subscribe('/topic/shadow', (msg) => {
            try { lastShadow.value = JSON.parse(msg.body) } catch (e) { /* ignore */ }
          })
          client.subscribe('/topic/alert', (msg) => {
            try {
              const data = JSON.parse(msg.body)
              lastAlert.value = data
              notificationStore.handleAlertMessage(data)
            } catch (e) { /* ignore */ }
          })
          client.subscribe('/topic/command/response', (msg) => {
            try { lastCommand.value = JSON.parse(msg.body) } catch (e) { /* ignore */ }
          })
          client.subscribe('/topic/device/status', (msg) => {
            try {
              const data = JSON.parse(msg.body)
              lastDevice.value = data
              notificationStore.handleDeviceStatusChange(data)
            } catch (e) { /* ignore */ }
          })
        },
        onDisconnect: () => { connected.value = false; connecting = false },
        onStompError: () => { connected.value = false; connecting = false },
        onWebSocketClose: () => { connected.value = false; connecting = false }
      })
      client.activate()
    } catch (e) {
      connecting = false
    }
  }

  function disconnect() {
    connecting = false
    if (client) {
      try { client.deactivate() } catch (e) { /* ignore */ }
      client = null
    }
    connected.value = false
  }

  return { connected, lastTelemetry, lastShadow, lastAlert, lastCommand, lastDevice, connect, disconnect }
})
