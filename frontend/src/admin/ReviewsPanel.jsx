import { useEffect, useState } from 'react'
import { getAllReviews, updateReviewStatus } from '../services/adminService.js'
import { getAdminErrorMessage } from './adminHelpers.js'
import { formatDateTime } from './adminHelpers.js'

// Lists customer reviews. The admin approves or rejects each one.
// Only approved reviews appear on the public website.
function ReviewsPanel({ onDataChanged }) {
  const [reviews, setReviews] = useState([])
  const [loading, setLoading] = useState(true)
  const [errorMessage, setErrorMessage] = useState('')

  function loadReviews() {
    setLoading(true)
    return getAllReviews()
      .then((data) => {
        setReviews(data)
        setErrorMessage('')
      })
      .catch((error) => setErrorMessage(getAdminErrorMessage(error, 'Could not load reviews.')))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    loadReviews()
  }, [])

  async function handleStatusChange(reviewId, newStatus) {
    setErrorMessage('')
    try {
      await updateReviewStatus(reviewId, newStatus)
      await loadReviews()
      onDataChanged()
    } catch (error) {
      setErrorMessage(getAdminErrorMessage(error, 'Could not update the review. Please try again.'))
    }
  }

  if (loading && reviews.length === 0) {
    return <p className="admin-muted">Loading reviews…</p>
  }

  return (
    <div>
      {errorMessage && <p className="admin-error" role="alert">{errorMessage}</p>}
      {!loading && reviews.length === 0 && <p className="admin-muted">No reviews yet.</p>}

      <div className="admin-list">
        {reviews.map((review) => (
          <article className="admin-card admin-item" key={review.id}>
            <div className="admin-item-header">
              <h3 className="admin-item-title">{review.customerName}</h3>
              <span className={'admin-badge admin-badge-' + review.status.toLowerCase()}>{review.status}</span>
            </div>

            <p className="admin-muted">{review.rating} / 5 · {formatDateTime(review.createdAt)}</p>
            <p className="admin-pre-wrap">{review.message}</p>

            <div className="admin-button-row">
              <button
                className="admin-button"
                type="button"
                disabled={review.status === 'APPROVED'}
                onClick={() => handleStatusChange(review.id, 'APPROVED')}
              >
                Approve
              </button>
              <button
                className="admin-button admin-button-danger"
                type="button"
                disabled={review.status === 'REJECTED'}
                onClick={() => handleStatusChange(review.id, 'REJECTED')}
              >
                Reject
              </button>
            </div>
          </article>
        ))}
      </div>
    </div>
  )
}

export default ReviewsPanel
