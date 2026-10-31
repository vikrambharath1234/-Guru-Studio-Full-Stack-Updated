import { useState } from 'react'
import { getCurrentUserProfile, loginAdmin } from '../services/adminService.js'
import { ADMIN_TOKEN_KEY, getErrorMessage } from '../services/api.js'
import { getAdminErrorMessage } from './adminHelpers.js'

// Field limits. They match the checks on the backend (LoginRequest.java).
const MAX_USERNAME_LENGTH = 150
const MAX_PASSWORD_LENGTH = 72

// The admin login page at /admin. There is no sign-up, only the one admin account.
function AdminLogin() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  // Messages next to each field, and one message for the whole form
  const [fieldErrors, setFieldErrors] = useState({})
  const [formError, setFormError] = useState('')
  const [loggingIn, setLoggingIn] = useState(false)

  // Checks the fields before anything is sent to the server
  function validate() {
    const errors = {}
    if (!username.trim()) {
      errors.username = 'Please enter your username.'
    } else if (username.trim().length > MAX_USERNAME_LENGTH) {
      errors.username = 'Username is too long.'
    }
    if (!password) {
      errors.password = 'Please enter your password.'
    } else if (password.length > MAX_PASSWORD_LENGTH) {
      errors.password = 'Password is too long.'
    }
    return errors
  }

  async function handleSubmit(event) {
    event.preventDefault()
    setFormError('')

    const errors = validate()
    setFieldErrors(errors)
    if (Object.keys(errors).length > 0) {
      return
    }

    setLoggingIn(true)
    try {
      // 1. Log in with the existing login API. It returns a token.
      const data = await loginAdmin(username.trim(), password)
      localStorage.setItem(ADMIN_TOKEN_KEY, data.accessToken)

      // 2. Check that this account really is an admin. Customer accounts can log in too,
      //    but they must not reach the dashboard.
      const profile = await getCurrentUserProfile()
      if (profile.role !== 'ADMIN') {
        localStorage.removeItem(ADMIN_TOKEN_KEY)
        setFormError('This account does not have admin access.')
        setLoggingIn(false)
        return
      }

      window.location.href = '/admin/dashboard'
    } catch (error) {
      localStorage.removeItem(ADMIN_TOKEN_KEY)
      // A wrong username or password gives 401, so show the server message for that case
      const message =
        error.response?.status === 401
          ? getErrorMessage(error, 'Invalid username or password.')
          : getAdminErrorMessage(error, 'Could not log in. Please try again.')
      setFormError(message)
      setLoggingIn(false)
    }
  }

  return (
    <div className="admin-login-page">
      <form className="admin-card admin-login-card" onSubmit={handleSubmit} noValidate>
        <div className="admin-login-brand">
          <img className="admin-login-logo" src="/images/logo.png" alt="Guru Studio logo" />
          <p className="eyebrow">Guru Studio</p>
          <h1 className="admin-title">Admin Login</h1>
          <p className="admin-muted">Sign in to manage bookings, messages, photos and reviews.</p>
        </div>

        <div className="admin-field">
          <label htmlFor="adminUsername">Username</label>
          <input
            id="adminUsername"
            type="text"
            autoComplete="username"
            value={username}
            aria-invalid={Boolean(fieldErrors.username)}
            aria-describedby={fieldErrors.username ? 'adminUsername-error' : undefined}
            onChange={(event) => setUsername(event.target.value)}
          />
          {fieldErrors.username && (
            <p className="admin-field-error" id="adminUsername-error">{fieldErrors.username}</p>
          )}
        </div>

        <div className="admin-field">
          <label htmlFor="adminPassword">Password</label>
          <div className="admin-password-wrap">
            <input
              id="adminPassword"
              type={showPassword ? 'text' : 'password'}
              autoComplete="current-password"
              value={password}
              aria-invalid={Boolean(fieldErrors.password)}
              aria-describedby={fieldErrors.password ? 'adminPassword-error' : undefined}
              onChange={(event) => setPassword(event.target.value)}
            />
            <button
              type="button"
              className="admin-password-toggle"
              onClick={() => setShowPassword(!showPassword)}
              aria-label={showPassword ? 'Hide password' : 'Show password'}
              aria-pressed={showPassword}
            >
              {showPassword ? 'Hide' : 'Show'}
            </button>
          </div>
          {fieldErrors.password && (
            <p className="admin-field-error" id="adminPassword-error">{fieldErrors.password}</p>
          )}
        </div>

        {formError && <p className="admin-error" role="alert">{formError}</p>}

        <button className="admin-button admin-button-full" type="submit" disabled={loggingIn}>
          {loggingIn ? 'Logging in…' : 'Log In'}
        </button>
      </form>
    </div>
  )
}

export default AdminLogin
