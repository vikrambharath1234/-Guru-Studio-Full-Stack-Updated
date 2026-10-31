// Chooses which admin page to show, based on the web address.
// There is no router library. The admin has only two pages, so a simple check is enough.
import AdminLogin from './AdminLogin.jsx'
import AdminDashboard from './AdminDashboard.jsx'
import { ADMIN_TOKEN_KEY } from '../services/api.js'
import './Admin.css'

function AdminRouter() {
  // Remove a trailing slash so "/admin/dashboard/" also works
  const path = window.location.pathname.replace(/\/+$/, '')

  if (path === '/admin/dashboard') {
    // Without a saved login token, the dashboard must not open. Send the admin to the login page.
    if (!localStorage.getItem(ADMIN_TOKEN_KEY)) {
      window.location.replace('/admin')
      return null
    }
    return <AdminDashboard />
  }

  // "/admin" and anything else under /admin shows the login page
  return <AdminLogin />
}

export default AdminRouter
