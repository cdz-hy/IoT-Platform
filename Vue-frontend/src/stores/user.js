import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi } from '@/api/index.js'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const username = ref(localStorage.getItem('username') || '')

  async function login(user, pass) {
    const res = await loginApi(user, pass)
    token.value = res.token
    username.value = res.username
    localStorage.setItem('token', res.token)
    localStorage.setItem('username', res.username)
    return res
  }

  function logout() {
    token.value = ''
    username.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('username')
  }

  return { token, username, login, logout }
})
