import { useEffect, useState } from 'react'
import { getCurrentUserProfile } from '../services/adminService.js'
import { formatDateTime, getAdminErrorMessage } from './adminHelpers.js'

// Basic user information. The backend has no endpoint that lists all users,
// so this panel shows only the account that is logged in right now.
function UserPanel() {
  const [profile, setProfile] = useState(null)
  const [loading, setLoading] = useState(true)
  const [errorMessage, setErrorMessage] = useState('')

  function loadProfile() {
    setLoading(true)
    setErrorMessage('')
    return getCurrentUserProfile()
      .then((data) => setProfile(data))
      .catch((error) => setErrorMessage(getAdminErrorMessage(error, 'Could not load your account details.')))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    loadProfile()
  }, [])

  return (
    <div className="admin-list">
      <div className="admin-card admin-note">
        <p>
          The studio server does not provide a list of all user accounts, so this section cannot show
          other users. Only the logged-in admin account is shown below.
        </p>
      </div>

      {loading && !profile && <p className="admin-muted">Loading account details…</p>}

      {errorMessage && (
        <div className="admin-state admin-state-error" role="alert">
          <p>{errorMessage}</p>
          <button className="admin-button" type="button" onClick={loadProfile}>Try again</button>
        </div>
      )}

      {profile && (
        <article className="admin-card admin-item">
          <div className="admin-item-header">
            <h3 className="admin-item-title">{profile.fullName}</h3>
            <span className={'admin-badge admin-badge-' + profile.role.toLowerCase()}>{profile.role}</span>
          </div>
          <dl className="admin-details">
            <dt>Login name</dt>
            <dd>{profile.email}</dd>
            <dt>Phone</dt>
            <dd>{profile.phone || '—'}</dd>
            <dt>Account created</dt>
            <dd>{formatDateTime(profile.createdAt)}</dd>
          </dl>
        </article>
      )}
    </div>
  )
}

export default UserPanel
