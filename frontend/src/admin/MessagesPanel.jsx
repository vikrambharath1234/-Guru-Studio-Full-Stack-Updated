import { useEffect, useState } from 'react'
import { deleteContactMessage, getAllContactMessages } from '../services/adminService.js'
import { getAdminErrorMessage } from './adminHelpers.js'
import { formatDateTime } from './adminHelpers.js'

// Lists contact form messages, newest first. The admin can delete them.
function MessagesPanel({ onDataChanged }) {
  const [messages, setMessages] = useState([])
  const [loading, setLoading] = useState(true)
  const [errorMessage, setErrorMessage] = useState('')

  function loadMessages() {
    setLoading(true)
    return getAllContactMessages()
      .then((data) => {
        setMessages(data)
        setErrorMessage('')
      })
      .catch((error) => setErrorMessage(getAdminErrorMessage(error, 'Could not load messages.')))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    loadMessages()
  }, [])

  async function handleDelete(message) {
    // Ask first, because deleting cannot be undone
    if (!window.confirm('Delete the message from ' + message.name + '?')) {
      return
    }
    setErrorMessage('')
    try {
      await deleteContactMessage(message.id)
      await loadMessages()
      onDataChanged()
    } catch (error) {
      setErrorMessage(getAdminErrorMessage(error, 'Could not delete the message. Please try again.'))
    }
  }

  if (loading && messages.length === 0) {
    return <p className="admin-muted">Loading messages…</p>
  }

  return (
    <div>
      {errorMessage && <p className="admin-error" role="alert">{errorMessage}</p>}
      {!loading && messages.length === 0 && <p className="admin-muted">No messages yet.</p>}

      <div className="admin-list">
        {messages.map((message) => (
          <article className="admin-card admin-item" key={message.id}>
            <div className="admin-item-header">
              <h3 className="admin-item-title">{message.name}</h3>
              <span className="admin-muted">{formatDateTime(message.createdAt)}</span>
            </div>

            <dl className="admin-details">
              <dt>Phone</dt>
              <dd><a href={'tel:' + message.phone}>{message.phone || '—'}</a></dd>
              <dt>Email</dt>
              <dd><a href={'mailto:' + message.email}>{message.email}</a></dd>
              <dt>Subject</dt>
              <dd>{message.subject}</dd>
              <dt>Message</dt>
              <dd className="admin-pre-wrap">{message.message}</dd>
            </dl>

            <div>
              <button className="admin-button admin-button-danger" type="button" onClick={() => handleDelete(message)}>
                Delete
              </button>
            </div>
          </article>
        ))}
      </div>
    </div>
  )
}

export default MessagesPanel
