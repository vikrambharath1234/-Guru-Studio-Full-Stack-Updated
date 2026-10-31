import { useState } from 'react'
import { studioInfo } from '../siteInfo.js'
import { submitBooking, submitContactMessage } from '../services/publicService.js'
import { getErrorMessage } from '../services/api.js'
import '../styles/Contact.css'

// The form starts empty. We use this again after the form is sent.
const emptyForm = {
  name: '',
  phone: '',
  email: '',
  eventType: '',
  eventDate: '',
  location: '',
  message: '',
}

function Contact() {
  // formData holds what the visitor has typed so far
  const [formData, setFormData] = useState(emptyForm)
  // "sending" is true while the request is on its way. It stops double clicks.
  const [sending, setSending] = useState(false)
  // statusMessage is the text shown under the form after sending. "isError" changes its colour.
  const [statusMessage, setStatusMessage] = useState('')
  const [isError, setIsError] = useState(false)

  function handleChange(event) {
    // event.target.name is the field, like "email". event.target.value is what was typed.
    const fieldName = event.target.name
    const fieldValue = event.target.value
    setFormData({ ...formData, [fieldName]: fieldValue })
  }

  async function handleSubmit(event) {
    // Stop the browser from reloading the page
    event.preventDefault()
    setSending(true)
    setStatusMessage('')

    // 1. Save the booking request. Its fields match BookingRequest.java.
    const bookingData = {
      customerName: formData.name,
      phone: formData.phone,
      email: formData.email,
      eventType: formData.eventType,
      eventDate: formData.eventDate,
      location: formData.location,
      details: formData.message,
    }

    // 2. Save the same enquiry as a contact message. Its fields match ContactRequest.java.
    const contactData = {
      name: formData.name,
      phone: formData.phone,
      email: formData.email,
      subject: 'Enquiry: ' + formData.eventType,
      message: formData.message || 'No additional details given.',
    }

    try {
      await submitBooking(bookingData)
      await submitContactMessage(contactData)
      setFormData(emptyForm)
      setIsError(false)
      setStatusMessage('Thank you. Your enquiry has been received. We will get back to you soon.')
    } catch (error) {
      setIsError(true)
      setStatusMessage(
        getErrorMessage(error, 'Sorry, we could not send your enquiry. Please call or WhatsApp us instead.'),
      )
    } finally {
      setSending(false)
    }
  }

  return (
    <>
      {/* Reserve-your-date call to action */}
      <section className="cta-band">
        <p className="eyebrow eyebrow-light">Reserve Your Date</p>
        <h2>Your special moment is waiting to be captured.</h2>
        <p className="cta-text">
          Planning a wedding, engagement, birthday, ceremony, or celebration? Let’s create photographs
          you will love for years to come.
        </p>
        <a className="button button-gold" href="#contact">Start Your Enquiry</a>
      </section>

      <section className="section" id="contact">
        <div className="section-heading">
          <p className="eyebrow">Contact</p>
          <h2>Let’s Create Something Beautiful Together</h2>
          <p className="body-text">Have an upcoming event? We’d love to hear about it.</p>
        </div>

        <div className="contact-links">
          <a className="contact-card" href={studioInfo.phoneLink}>
            <span className="contact-label">Call Us</span>
            <span className="contact-value">{studioInfo.phoneDisplay}</span>
          </a>
          <a className="contact-card" href={studioInfo.whatsappLink} target="_blank" rel="noreferrer">
            <span className="contact-label">WhatsApp</span>
            <span className="contact-value">Chat with us</span>
          </a>
          <a className="contact-card" href={'mailto:' + studioInfo.email}>
            <span className="contact-label">Email</span>
            <span className="contact-value">{studioInfo.email}</span>
          </a>
          <a className="contact-card" href={studioInfo.mapsLink} target="_blank" rel="noreferrer">
            <span className="contact-label">Studio</span>
            <span className="contact-value">{studioInfo.address}</span>
            <span className="contact-label">Open in Google Maps</span>
          </a>
        </div>

        <form className="enquiry-form" onSubmit={handleSubmit}>
          <div className="form-field">
            <label htmlFor="name">Full name *</label>
            <input id="name" name="name" type="text" required value={formData.name} onChange={handleChange} />
          </div>

          <div className="form-field">
            <label htmlFor="phone">Phone number *</label>
            <input id="phone" name="phone" type="tel" required value={formData.phone} onChange={handleChange} />
          </div>

          <div className="form-field">
            <label htmlFor="email">Email address *</label>
            <input id="email" name="email" type="email" required value={formData.email} onChange={handleChange} />
          </div>

          <div className="form-field">
            <label htmlFor="eventType">Event type *</label>
            <select id="eventType" name="eventType" required value={formData.eventType} onChange={handleChange}>
              <option value="" disabled>Select an event</option>
              <option value="Wedding">Wedding</option>
              <option value="Pre-wedding">Pre-wedding</option>
              <option value="Post-wedding">Post-wedding</option>
              <option value="Birthday">Birthday</option>
              <option value="Engagement">Engagement</option>
              <option value="Puberty ceremony">Puberty ceremony</option>
              <option value="Other">Other</option>
            </select>
          </div>

          <div className="form-field">
            <label htmlFor="eventDate">Event date *</label>
            <input id="eventDate" name="eventDate" type="date" required value={formData.eventDate} onChange={handleChange} />
          </div>

          <div className="form-field">
            <label htmlFor="location">Event location *</label>
            <input id="location" name="location" type="text" required value={formData.location} onChange={handleChange} />
          </div>

          <div className="form-field">
            <label htmlFor="message">Tell us about your event</label>
            <textarea id="message" name="message" rows="4" value={formData.message} onChange={handleChange} />
          </div>

          <button className="submit-button" type="submit" disabled={sending}>
            {sending ? 'Sending…' : 'Send Enquiry'}
          </button>
          <p className={isError ? 'small-text form-note form-message-error' : 'small-text form-note'} role="status">
            {statusMessage || 'Your booking is confirmed only after we reply to you.'}
          </p>
        </form>
      </section>
    </>
  )
}

export default Contact
