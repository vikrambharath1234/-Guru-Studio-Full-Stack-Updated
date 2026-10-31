import { useEffect, useState } from 'react'
import { photosByCategory } from '../photos.js'
import { getGalleryImages } from '../services/publicService.js'
import '../styles/Gallery.css'

// The filter buttons. "id" must match the category name in src/photos.js.
const categories = [
  { id: 'all', label: 'All' },
  { id: 'weddings', label: 'Weddings' },
  { id: 'engagements', label: 'Engagements' },
  { id: 'puberty', label: 'Puberty Ceremonies' },
  { id: 'birthdays', label: 'Birthdays' },
  { id: 'valaikappu', label: 'Valaikappu' },
]

// Build one list of every photo from photos.js, and remember which category each photo belongs to
const builtInPhotos = Object.entries(photosByCategory).flatMap(([category, photos]) =>
  photos.map((photo) => ({ ...photo, category })),
)

function Gallery() {
  // The category the visitor clicked. "all" shows every photo.
  const [activeCategory, setActiveCategory] = useState('all')
  // Photos the admin uploaded from the dashboard. Empty until the backend answers.
  const [uploadedPhotos, setUploadedPhotos] = useState([])

  // When the page opens, ask the backend for uploaded photos.
  // If the backend is offline, the built-in photos still show.
  useEffect(() => {
    let ignore = false
    getGalleryImages()
      .then((images) => {
        if (!ignore) {
          setUploadedPhotos(
            images.map((image) => ({ src: image.imageUrl, alt: 'Photograph by Guru Studio', category: image.category })),
          )
        }
      })
      .catch(() => {
        // No uploaded photos to show. The built-in photos are still shown below.
      })
    return () => {
      ignore = true
    }
  }, [])

  // The built-in photos first, then the uploaded ones
  const galleryPhotos = [...builtInPhotos, ...uploadedPhotos]

  // Build a new list with only the photos that match the active category
  const visiblePhotos =
    activeCategory === 'all'
      ? galleryPhotos
      : galleryPhotos.filter((photo) => photo.category === activeCategory)

  return (
    <section className="section section-light" id="gallery">
      <div className="section-heading">
        <p className="eyebrow">Gallery</p>
        <h2>Stories We’ve Captured</h2>
        <p className="body-text">
          A collection of beautiful moments, genuine emotions, and unforgettable celebrations.
        </p>
      </div>

      <div className="filter-bar" role="group" aria-label="Filter photos by category">
        {categories.map((category) => (
          <button
            key={category.id}
            type="button"
            className={activeCategory === category.id ? 'filter-button active' : 'filter-button'}
            aria-pressed={activeCategory === category.id}
            onClick={() => setActiveCategory(category.id)}
          >
            {category.label}
          </button>
        ))}
      </div>

      {/* CSS columns keep every photo at its own shape, so nothing is stretched or cropped */}
      <div className="gallery-grid">
        {visiblePhotos.map((photo) => (
          <figure className="gallery-item" key={photo.src}>
            <img src={photo.src} alt={photo.alt} loading="lazy" />
          </figure>
        ))}
      </div>
    </section>
  )
}

export default Gallery
