import { useEffect, useState } from 'react'
import { getAllReviews, getDashboardSummary } from '../services/adminService.js'
import { getAdminErrorMessage } from './adminHelpers.js'

// The five number cards at the top of the dashboard.
// The numbers come from the server. Nothing is made up here.
function OverviewPanel({ refreshKey }) {
  const [stats, setStats] = useState(null)
  const [loading, setLoading] = useState(true)
  const [errorMessage, setErrorMessage] = useState('')

  // Loads the summary numbers, and the full review list for the total review count.
  // The summary has only the pending review count, so the total is counted from the list.
  function loadOverview() {
    setLoading(true)
    setErrorMessage('')
    return Promise.all([getDashboardSummary(), getAllReviews()])
      .then(([summary, reviews]) => {
        setStats({
          totalBookings: summary.totalBookings,
          pendingBookings: summary.pendingBookings,
          totalContactMessages: summary.totalContactMessages,
          totalGalleryImages: summary.totalGalleryImages,
          totalReviews: reviews.length,
          pendingReviews: summary.pendingReviews,
        })
      })
      .catch((error) => setErrorMessage(getAdminErrorMessage(error, 'Could not load the dashboard numbers.')))
      .finally(() => setLoading(false))
  }

  // Load on open, and again whenever another panel changes data (refreshKey)
  useEffect(() => {
    loadOverview()
  }, [refreshKey])

  if (errorMessage) {
    return (
      <div className="admin-state admin-state-error" role="alert">
        <p>{errorMessage}</p>
        <button className="admin-button" type="button" onClick={loadOverview}>Try again</button>
      </div>
    )
  }

  if (loading && !stats) {
    return <p className="admin-muted">Loading dashboard numbers…</p>
  }

  return (
    <div className="admin-stats">
      <StatCard label="Total bookings" value={stats.totalBookings} note={stats.pendingBookings + ' pending'} />
      <StatCard label="Pending bookings" value={stats.pendingBookings} note="Waiting for your reply" />
      <StatCard label="Contact messages" value={stats.totalContactMessages} note="All enquiries received" />
      <StatCard label="Gallery images" value={stats.totalGalleryImages} note="Uploaded photos" />
      <StatCard label="Reviews" value={stats.totalReviews} note={stats.pendingReviews + ' waiting for approval'} />
    </div>
  )
}

// One number card
function StatCard({ label, value, note }) {
  return (
    <article className="admin-card admin-stat">
      <p className="admin-stat-label">{label}</p>
      <p className="admin-stat-value">{value}</p>
      <p className="admin-stat-note">{note}</p>
    </article>
  )
}

export default OverviewPanel
