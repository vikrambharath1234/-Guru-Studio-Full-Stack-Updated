import { useEffect, useState } from 'react'
import '../styles/PhotoSlideshow.css'

// Shows a list of photos one after another, changing every 2 seconds.
// Each new photo fades in over the previous one, so there is no blank gap.
const SECONDS_BETWEEN_PHOTOS = 2000

function PhotoSlideshow({ photos }) {
  // "current" is the photo showing (and fading in). "previous" is the one underneath it.
  const [slides, setSlides] = useState({ previous: null, current: 0 })

  useEffect(() => {
    // Move to the next photo every 2 seconds. Loop back to the start at the end.
    const timerId = setInterval(() => {
      setSlides((oldSlides) => ({
        previous: oldSlides.current,
        current: (oldSlides.current + 1) % photos.length,
      }))
    }, SECONDS_BETWEEN_PHOTOS)

    // Stop the timer when this slideshow is removed from the page
    return () => clearInterval(timerId)
  }, [photos.length])

  // Load the next photo in the background so it is ready when the timer changes
  useEffect(() => {
    const nextIndex = (slides.current + 1) % photos.length
    const preloadImage = new Image()
    preloadImage.src = photos[nextIndex].src
  }, [slides.current, photos])

  const currentPhoto = photos[slides.current]
  const previousPhoto = slides.previous === null ? null : photos[slides.previous]

  return (
    <div className="slideshow">
      {/* The previous photo stays fully visible underneath while the new one fades in */}
      {previousPhoto && (
        <img
          key={'previous-' + previousPhoto.src}
          src={previousPhoto.src}
          alt=""
          aria-hidden="true"
          className="slideshow-photo"
          style={{ objectPosition: previousPhoto.position ?? 'center center' }}
        />
      )}
      {/* A new key makes React create a new image, which starts the fade-in */}
      <img
        key={currentPhoto.src}
        src={currentPhoto.src}
        alt={currentPhoto.alt}
        className="slideshow-photo slideshow-photo-fade-in"
        style={{ objectPosition: currentPhoto.position ?? 'center center' }}
      />
    </div>
  )
}

export default PhotoSlideshow
