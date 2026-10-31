import { useEffect, useState } from 'react'
import { deleteGalleryImage, getAllGalleryImages, uploadGalleryPhotos } from '../services/adminService.js'
import { getAdminErrorMessage } from './adminHelpers.js'
import { CATEGORY_OPTIONS, getCategoryLabel } from './adminHelpers.js'

// Upload photos into a category, see them, and delete them.
// Uploaded photos show on the public Gallery under their category.
function GalleryPanel({ onDataChanged }) {
  const [images, setImages] = useState([])
  const [loading, setLoading] = useState(true)
  // The category chosen in the dropdown for the next upload
  const [category, setCategory] = useState(CATEGORY_OPTIONS[0].value)
  // The photos picked in the file box
  const [selectedFiles, setSelectedFiles] = useState([])
  // Changing this key makes React show an empty file box again after an upload
  const [fileBoxKey, setFileBoxKey] = useState(0)
  const [uploading, setUploading] = useState(false)
  const [message, setMessage] = useState('')
  const [errorMessage, setErrorMessage] = useState('')

  function loadImages() {
    setLoading(true)
    return getAllGalleryImages()
      .then((data) => setImages(data))
      .catch((error) => setErrorMessage(getAdminErrorMessage(error, 'Could not load photos.')))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    loadImages()
  }, [])

  async function handleUpload(event) {
    event.preventDefault()
    setMessage('')
    setErrorMessage('')

    if (selectedFiles.length === 0) {
      setErrorMessage('Choose at least one photo first.')
      return
    }

    setUploading(true)
    try {
      await uploadGalleryPhotos(selectedFiles, category)
      setMessage('Uploaded ' + selectedFiles.length + ' photo(s) to ' + getCategoryLabel(category) + '.')
      setSelectedFiles([])
      setFileBoxKey((oldKey) => oldKey + 1)
      await loadImages()
      onDataChanged()
    } catch (error) {
      setErrorMessage(getAdminErrorMessage(error, 'Upload failed. Please try again.'))
    } finally {
      setUploading(false)
    }
  }

  async function handleDelete(image) {
    if (!window.confirm('Delete this photo from the gallery? This cannot be undone.')) {
      return
    }
    setMessage('')
    setErrorMessage('')
    try {
      await deleteGalleryImage(image.id)
      await loadImages()
      onDataChanged()
    } catch (error) {
      setErrorMessage(getAdminErrorMessage(error, 'Could not delete the photo. Please try again.'))
    }
  }

  return (
    <div>

      <form className="admin-card admin-upload-form" onSubmit={handleUpload}>
        <div className="admin-field">
          <label htmlFor="uploadCategory">Category</label>
          <select id="uploadCategory" value={category} onChange={(event) => setCategory(event.target.value)}>
            {CATEGORY_OPTIONS.map((option) => (
              <option key={option.value} value={option.value}>{option.label}</option>
            ))}
          </select>
        </div>

        <div className="admin-field">
          <label htmlFor="uploadFiles">Photos (JPG, PNG or WEBP, up to 10 MB each)</label>
          <input
            key={fileBoxKey}
            id="uploadFiles"
            type="file"
            multiple
            accept="image/jpeg,image/png,image/webp"
            onChange={(event) => setSelectedFiles(Array.from(event.target.files))}
          />
        </div>

        <button className="admin-button" type="submit" disabled={uploading}>
          {uploading ? 'Uploading…' : 'Upload Photos'}
        </button>
        {message && <p className="admin-success" role="status">{message}</p>}
        {errorMessage && <p className="admin-error" role="alert">{errorMessage}</p>}
      </form>

      <h3 className="admin-subtitle">Uploaded photos</h3>
      {loading && images.length === 0 && <p className="admin-muted">Loading photos…</p>}
      {!loading && images.length === 0 && <p className="admin-muted">No uploaded photos yet.</p>}

      <div className="admin-photo-grid">
        {images.map((image) => (
          <figure className="admin-card admin-photo" key={image.id}>
            <img src={image.imageUrl} alt={'Gallery photo in ' + getCategoryLabel(image.category)} loading="lazy" />
            <figcaption>
              <span className="admin-muted">{getCategoryLabel(image.category)}</span>
              <button className="admin-button admin-button-danger" type="button" onClick={() => handleDelete(image)}>
                Delete
              </button>
            </figcaption>
          </figure>
        ))}
      </div>
    </div>
  )
}

export default GalleryPanel
