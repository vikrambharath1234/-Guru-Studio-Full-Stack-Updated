// Calls that only the admin dashboard makes. The api.js file adds the login token for us.
import { api } from './api.js'

// Logs the admin in. The backend calls the login name "email", so we send the username in that field.
export async function loginAdmin(username, password) {
  const response = await api.post('/api/auth/login', { email: username, password })
  return response.data
}

export async function getDashboardSummary() {
  const response = await api.get('/api/admin/summary')
  return response.data
}

export async function getAllBookings() {
  const response = await api.get('/api/admin/bookings')
  return response.data
}

export async function updateBookingStatus(bookingId, status) {
  const response = await api.put('/api/admin/bookings/' + bookingId + '/status', { status })
  return response.data
}

export async function getAllContactMessages() {
  const response = await api.get('/api/admin/contact-messages')
  return response.data
}

export async function deleteContactMessage(messageId) {
  await api.delete('/api/admin/contact-messages/' + messageId)
}

export async function getAllGalleryImages() {
  const response = await api.get('/api/admin/gallery')
  return response.data
}

// Uploads one or more photos. "files" is a list of File objects from the file picker.
export async function uploadGalleryPhotos(files, category) {
  const formData = new FormData()
  files.forEach((file) => formData.append('files', file))
  formData.append('category', category)
  const response = await api.post('/api/admin/gallery', formData)
  return response.data
}

export async function deleteGalleryImage(imageId) {
  await api.delete('/api/admin/gallery/' + imageId)
}

export async function getAllReviews() {
  const response = await api.get('/api/admin/reviews')
  return response.data
}

export async function updateReviewStatus(reviewId, status) {
  const response = await api.put('/api/admin/reviews/' + reviewId + '/status', { status })
  return response.data
}

// The logged-in user's own profile. The backend returns role ("ADMIN" or "USER").
// The login page uses it to make sure the account is an admin.
export async function getCurrentUserProfile() {
  const response = await api.get('/api/users/me')
  return response.data
}
