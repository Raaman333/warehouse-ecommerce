import axios from 'axios'

/**
 * Central Axios instance — all API calls use this client.
 * Base URL reads from Vite env var (defaults to /api which
 * the Vite dev proxy forwards to localhost:8080, and Nginx
 * forwards in production).
 */
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
})

// Response interceptor: surface error messages from Spring's ResponseStatusException
api.interceptors.response.use(
  response => response,
  error => {
    const message = error.response?.data || error.message || 'Network error'
    console.error('[API Error]', message)
    return Promise.reject(error)
  }
)

export default api
