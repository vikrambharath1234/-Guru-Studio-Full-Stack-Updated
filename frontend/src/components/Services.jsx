import PhotoSlideshow from './PhotoSlideshow.jsx'
import { photosByCategory, preWeddingPhotos } from '../photos.js'
import '../styles/Services.css'

// Edit this list to change the services.
// Each item becomes one card. "photos" is the list of photos that card slides through.
const services = [
  {
    title: 'Wedding Photography',
    text: 'Your wedding day, from the rituals and ceremonies to the quiet moments in between.',
    photos: photosByCategory.weddings,
  },
  {
    title: 'Pre-Wedding',
    text: 'Relaxed portrait sessions before the wedding, planned around the couple.',
    photos: preWeddingPhotos,
  },
  {
    title: 'Birthday Celebrations',
    text: 'Birthday celebrations, from the cake cutting to the candid laughter with family.',
    photos: photosByCategory.birthdays,
  },
  {
    title: 'Puberty Ceremony',
    text: 'Puberty ceremonies, documented with respect for family traditions.',
    photos: photosByCategory.puberty,
  },
  {
    title: 'Engagement Photography',
    text: 'The ring exchange and the families coming together to celebrate.',
    photos: photosByCategory.engagements,
  },
  {
    title: 'Special Events',
    text: 'Anniversaries, housewarming ceremonies, and family gatherings.',
    photos: photosByCategory.valaikappu,
  },
]

function Services() {
  return (
    <section className="section" id="services">
      <div className="section-heading">
        <p className="eyebrow">Our Services</p>
        <h2>Photography for Every Occasion</h2>
        <p className="body-text">
          Whatever the occasion, we are here to turn your special moments into lasting memories.
        </p>
      </div>

      <div className="service-grid">
        {services.map((service) => (
          <article className="service-card" key={service.title}>
            <div className="service-image-box">
              <PhotoSlideshow photos={service.photos} />
            </div>
            <div className="service-body">
              <h3>{service.title}</h3>
              <p className="small-text">{service.text}</p>
              <a className="text-link" href="#contact">Enquire Now →</a>
            </div>
          </article>
        ))}
      </div>
    </section>
  )
}

export default Services
