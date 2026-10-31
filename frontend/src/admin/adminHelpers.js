import { getErrorMessage } from '../services/api.js'

// Small helpers shared by the admin pages.

// Gallery categories. The "value" must match the list on the backend (GalleryService.CATEGORIES).
export const CATEGORY_OPTIONS = [
  { value: 'weddings', label: 'Weddings' },
  { value: 'engagements', label: 'Engagements' },
  { value: 'puberty', label: 'Puberty Ceremonies' },
  { value: 'birthdays', label: 'Birthdays' },
  { value: 'valaikappu', label: 'Valaikappu' },
]

// Turns a category value like "weddings" into the label "Weddings"
export function getCategoryLabel(categoryValue) {
  const found = CATEGORY_OPTIONS.find((option) => option.value === categoryValue)
  return found ? found.label : categoryValue
}

// The event date comes as "2026-12-25" (a date with no time). Show it as "25 Dec 2026".
export function formatEventDate(dateText) {
  if (!dateText) {
    return '—'
  }
  const [year, month, day] = dateText.split('-').map(Number)
  return new Date(year, month - 1, day).toLocaleDateString('en-IN', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
  })
}

// Creation times come with a time of day. Show them as "25 Dec 2026, 10:30 am".
export function formatDateTime(timestampText) {
  if (!timestampText) {
    return '—'
  }
  return new Date(timestampText).toLocaleString('en-IN', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
    hour: 'numeric',
    minute: '2-digit',
  })
}

// Error text for admin requests. The backend answers 403 when a logged-in account is not an admin.
export function getAdminErrorMessage(error, fallback) {
  if (error.response?.status === 403) {
    return 'This account does not have admin access.'
  }
  if (!error.response) {
    return 'Could not connect to the studio server. Please check that the backend is running.'
  }
  return getErrorMessage(error, fallback)
}
