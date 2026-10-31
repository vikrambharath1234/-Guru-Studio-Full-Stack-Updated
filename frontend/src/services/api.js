// The one place where we talk to the Spring Boot backend.
// Every other service file uses this "api" object instead of writing full URLs.
import axios from 'axios'

// The backend address. Vite reads it from frontend/.env (see .env.example).
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

// The key used to save the admin's login token in the browser
export const ADMIN_TOKEN_KEY = 'guruStudioAdminToken'

export const api = axios.create({
  baseURL: API_BASE_URL,
})

// Before every request, add the admin token (if we have one) to the headers.
// The backend uses it to know the request comes from a logged-in admin.
api.interceptors.request.use((config) => {
  const token = localStorage.getItem(ADMIN_TOKEN_KEY)
  if (token) {
    config.headers.Authorization = 'Bearer ' + token
  }
  return config
})

// If the backend says "not logged in" (401), the token has expired.
// Remove it and send the admin back to the login page.
// The login call itself is skipped, because a wrong password also gives 401.
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const isLoginRequest = error.config?.url?.includes('/api/auth/login')
    if (error.response?.status === 401 && !isLoginRequest) {
      localStorage.removeItem(ADMIN_TOKEN_KEY)
      window.location.href = '/admin'
    }
    return Promise.reject(error)
  },
)

// Turns a backend error into a short message we can show the visitor.
// The backend sends { message, fieldErrors }. If the connection failed, we use the fallback text.
export function getErrorMessage(error, fallback) {
  const data = error.response?.data
  if (data?.fieldErrors && Object.keys(data.fieldErrors).length > 0) {
    return Object.values(data.fieldErrors)[0]
  }
  if (data?.message) {
    return data.message
  }
  return fallback
}
