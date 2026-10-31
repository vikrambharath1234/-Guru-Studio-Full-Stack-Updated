import { useEffect, useState } from 'react'
import { getAllBookings, updateBookingStatus } from '../services/adminService.js'
import { getAdminErrorMessage } from './adminHelpers.js'
import { formatDateTime, formatEventDate } from './adminHelpers.js'

// The three statuses the admin can choose from
const STATUS_OPTIONS = [
  { value: 'PENDING', label: 'Pending' },
  { value: 'CONFIRMED', label: 'Confirmed' },
  { value: 'REJECTED', label: 'Rejected' },
]

// Lists booking requests. The admin changes each one's status with the dropdown.
function BookingsPanel({ onDataChanged }) {
  const [bookings, setBookings] = useState([])
  const [loading, setLoading] = useState(true)
  const [errorMessage, setErrorMessage] = useState('')

  // Loads the list from the backend
  function loadBookings() {
    setLoading(true)
    return getAllBookings()
      .then((data) => {
        setBookings(data)
        setErrorMessage('')
      })
      .catch((error) => setErrorMessage(getAdminErrorMessage(error, 'Could not load bookings.')))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    loadBookings()
  }, [])

  // Called when the admin picks a new status from the dropdown
  async function handleStatusChange(bookingId, newStatus) {
    setErrorMessage('')
    try {
      await updateBookingStatus(bookingId, newStatus)
      await loadBookings()
      onDataChanged()
    } catch (error) {
      setErrorMessage(getAdminErrorMessage(error, 'Could not update the status. Please try again.'))
    }
  }

  if (loading && bookings.length === 0) {
    return <p className="admin-muted">Loading bookings…</p>
  }

  return (
    <div>
      {errorMessage && <p className="admin-error" role="alert">{errorMessage}</p>}
      {!loading && bookings.length === 0 && <p className="admin-muted">No booking requests yet.</p>}

      <div className="admin-list">
        {bookings.map((booking) => (
          <article className="admin-card admin-item" key={booking.id}>
            <div className="admin-item-header">
              <h3 className="admin-item-title">{booking.customerName}</h3>
              <span className={'admin-badge admin-badge-' + booking.status.toLowerCase()}>
                {booking.status}
              </span>
            </div>

            <dl className="admin-details">
              <dt>Event</dt>
              <dd>{booking.eventType} on {formatEventDate(booking.eventDate)}</dd>
              <dt>Location</dt>
              <dd>{booking.location}</dd>
              <dt>Phone</dt>
              <dd><a href={'tel:' + booking.phone}>{booking.phone}</a></dd>
              <dt>Email</dt>
              <dd><a href={'mailto:' + booking.email}>{booking.email}</a></dd>
              <dt>Details</dt>
              <dd className="admin-pre-wrap">{booking.details || '—'}</dd>
              <dt>Received</dt>
              <dd>{formatDateTime(booking.createdAt)}</dd>
            </dl>

            <div className="admin-field">
              <label htmlFor={'status-' + booking.id}>Status</label>
              <select
                id={'status-' + booking.id}
                value={booking.status}
                onChange={(event) => handleStatusChange(booking.id, event.target.value)}
              >
                {STATUS_OPTIONS.map((option) => (
                  <option key={option.value} value={option.value}>{option.label}</option>
                ))}
              </select>
            </div>
          </article>
        ))}
      </div>
    </div>
  )
}

export default BookingsPanel
