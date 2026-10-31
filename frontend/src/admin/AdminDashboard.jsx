import { useState } from 'react'
import { ADMIN_TOKEN_KEY } from '../services/api.js'
import OverviewPanel from './OverviewPanel.jsx'
import BookingsPanel from './BookingsPanel.jsx'
import MessagesPanel from './MessagesPanel.jsx'
import GalleryPanel from './GalleryPanel.jsx'
import ReviewsPanel from './ReviewsPanel.jsx'
import UserPanel from './UserPanel.jsx'

// The sidebar sections. "id" selects the panel, "label" is shown in the sidebar.
const sections = [
  { id: 'overview', label: 'Overview', title: 'Dashboard Overview' },
  { id: 'bookings', label: 'Bookings', title: 'Booking Management' },
  { id: 'messages', label: 'Contact Messages', title: 'Contact Messages' },
  { id: 'gallery', label: 'Gallery', title: 'Gallery Management' },
  { id: 'reviews', label: 'Reviews', title: 'Review Management' },
  { id: 'users', label: 'Users', title: 'User Management' },
]

// The admin dashboard at /admin/dashboard. A sidebar on wide screens, a top menu on phones.
function AdminDashboard() {
  const [activeSection, setActiveSection] = useState('overview')
  // Going up by one each time a panel changes data, so the overview numbers reload
  const [refreshKey, setRefreshKey] = useState(0)

  function handleDataChanged() {
    setRefreshKey((oldKey) => oldKey + 1)
  }

  function handleLogout() {
    // The server keeps no session (login tokens are stateless), so logging out means
    // forgetting the token in this browser. Without it, the dashboard cannot be opened.
    localStorage.removeItem(ADMIN_TOKEN_KEY)
    window.location.href = '/admin'
  }

  const current = sections.find((section) => section.id === activeSection)

  let activePanel = null
  if (activeSection === 'overview') {
    activePanel = <OverviewPanel refreshKey={refreshKey} />
  } else if (activeSection === 'bookings') {
    activePanel = <BookingsPanel onDataChanged={handleDataChanged} />
  } else if (activeSection === 'messages') {
    activePanel = <MessagesPanel onDataChanged={handleDataChanged} />
  } else if (activeSection === 'gallery') {
    activePanel = <GalleryPanel onDataChanged={handleDataChanged} />
  } else if (activeSection === 'reviews') {
    activePanel = <ReviewsPanel onDataChanged={handleDataChanged} />
  } else if (activeSection === 'users') {
    activePanel = <UserPanel />
  }

  return (
    <div className="admin-shell">
      <aside className="admin-sidebar">
        <div className="admin-sidebar-brand">
          <img className="admin-sidebar-logo" src="/images/logo.png" alt="Guru Studio logo" />
          <div>
            <p className="admin-sidebar-name">Guru Studio</p>
            <p className="admin-sidebar-role">Admin</p>
          </div>
        </div>

        <nav className="admin-nav" aria-label="Admin sections">
          {sections.map((section) => (
            <button
              key={section.id}
              type="button"
              className={activeSection === section.id ? 'admin-nav-item active' : 'admin-nav-item'}
              aria-current={activeSection === section.id ? 'page' : undefined}
              onClick={() => setActiveSection(section.id)}
            >
              {section.label}
            </button>
          ))}
        </nav>

        <button className="admin-nav-logout" type="button" onClick={handleLogout}>
          Logout
        </button>
      </aside>

      <main className="admin-content">
        <header className="admin-content-header">
          <p className="eyebrow">Guru Studio Admin</p>
          <h1 className="admin-title">{current.title}</h1>
        </header>

        <section className="admin-panel" aria-live="polite">
          {activePanel}
        </section>
      </main>
    </div>
  )
}

export default AdminDashboard
