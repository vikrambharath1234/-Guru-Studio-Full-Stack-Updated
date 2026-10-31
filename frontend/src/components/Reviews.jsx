import { useEffect, useState } from 'react'
import { getApprovedReviews, submitReview } from '../services/publicService.js'
import { getErrorMessage } from '../services/api.js'
import '../styles/Contact.css'
import '../styles/Reviews.css'

// The form starts empty. We use this again after a review is sent.
const emptyReview = { customerName: '', rating: '5', message: '' }

// Shows approved reviews, and a form for customers to write one.
// Reviews only appear on the site after the admin approves them.
function Reviews() {
  const [approvedReviews, setApprovedReviews] = useState([])
  const [reviewForm, setReviewForm] = useState(emptyReview)
  const [sending, setSending] = useState(false)
  const [statusMessage, setStatusMessage] = useState('')
  const [isError, setIsError] = useState(false)

  // When the page opens, load the approved reviews. If the backend is offline, the list stays empty.
  useEffect(() => {
    let ignore = false
    getApprovedReviews()
      .then((reviews) => {
        if (!ignore) {
          setApprovedReviews(reviews)
        }
      })
      .catch(() => {
        // Nothing to show. The rest of the page still works.
      })
    return () => {
      ignore = true
    }
  }, [])

  function handleChange(event) {
    setReviewForm({ ...reviewForm, [event.target.name]: event.target.value })
  }

  async function handleSubmit(event) {
    event.preventDefault()
    setSending(true)
    setStatusMessage('')

    try {
      await submitReview({
        customerName: reviewForm.customerName,
        rating: Number(reviewForm.rating),
        message: reviewForm.message,
      })
      setReviewForm(emptyReview)
      setIsError(false)
      setStatusMessage('Thank you for your review. It will appear here after it is approved.')
    } catch (error) {
      setIsError(true)
      setStatusMessage(getErrorMessage(error, 'Sorry, we could not send your review. Please try again later.'))
    } finally {
      setSending(false)
    }
  }

  return (
    <section className="section section-review" id="reviews">
      <div className="section-heading">
        <p className="eyebrow">Reviews</p>
        <h2>Kind Words From Our Clients</h2>
        <p className="body-text">Share your experience with Guru Studio.</p>
      </div>

      {approvedReviews.length > 0 && (
        <div className="review-grid">
          {approvedReviews.map((review) => (
            <article className="review-card" key={review.id}>
              <p className="review-stars" aria-label={review.rating + ' out of 5 stars'}>
                {'★'.repeat(review.rating)}
                <span className="review-stars-empty">{'★'.repeat(5 - review.rating)}</span>
              </p>
              <p className="small-text">{review.message}</p>
              <p className="review-name">{review.customerName}</p>
            </article>
          ))}
        </div>
      )}

      <form className="enquiry-form review-form" onSubmit={handleSubmit}>
        <div className="form-field">
          <label htmlFor="reviewName">Your name *</label>
          <input id="reviewName" name="customerName" type="text" required value={reviewForm.customerName} onChange={handleChange} />
        </div>

        <div className="form-field">
          <label htmlFor="reviewRating">Rating *</label>
          <select id="reviewRating" name="rating" required value={reviewForm.rating} onChange={handleChange}>
            <option value="5">5 - Excellent</option>
            <option value="4">4 - Very good</option>
            <option value="3">3 - Good</option>
            <option value="2">2 - Fair</option>
            <option value="1">1 - Poor</option>
          </select>
        </div>

        <div className="form-field">
          <label htmlFor="reviewMessage">Your review *</label>
          <textarea id="reviewMessage" name="message" rows="4" required value={reviewForm.message} onChange={handleChange} />
        </div>

        <button className="submit-button" type="submit" disabled={sending}>
          {sending ? 'Sending…' : 'Submit Review'}
        </button>
        <p className={isError ? 'small-text form-note form-message-error' : 'small-text form-note'} role="status">
          {statusMessage}
        </p>
      </form>
    </section>
  )
}

export default Reviews
