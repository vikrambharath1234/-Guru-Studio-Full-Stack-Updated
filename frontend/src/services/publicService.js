// Calls that the public website makes (no login needed).
import { api } from './api.js'

// Sends a photography booking request. Fields match BookingRequest.java.
export async function submitBooking(bookingData) {
  const response = await api.post('/api/bookings', bookingData)
  return response.data
}

// Sends a message through the contact form. Fields match ContactRequest.java.
export async function submitContactMessage(messageData) {
  const response = await api.post('/api/contact', messageData)
  return response.data
}

// Gets every gallery photo uploaded by the admin. Returns a list of { id, imageUrl, category }.
export async function getGalleryImages() {
  const response = await api.get('/api/gallery')
  return response.data
}

// Gets the reviews the admin has approved.
export async function getApprovedReviews() {
  const response = await api.get('/api/reviews')
  return response.data
}

// Sends a customer review. It stays hidden until the admin approves it.
export async function submitReview(reviewData) {
  const response = await api.post('/api/reviews', reviewData)
  return response.data
}
