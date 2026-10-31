import { useState } from 'react'
import '../styles/Header.css'

function Header() {
  // true when the mobile menu is open
  const [menuOpen, setMenuOpen] = useState(false)

  function closeMenu() {
    setMenuOpen(false)
  }

  return (
    <header className="site-header">
      <div className="header-inner">
        {/* Menu button: shown on phones and tablets only (see Header.css) */}
        <button
          type="button"
          className="menu-button"
          aria-expanded={menuOpen}
          aria-controls="main-nav"
          aria-label={menuOpen ? 'Close menu' : 'Open menu'}
          onClick={() => setMenuOpen(!menuOpen)}
        >
          Menu
        </button>

        {/* The real Guru Studio logo image */}
        <a href="#home" className="logo-link" onClick={closeMenu}>
          <img className="logo-image" src="/images/logo.png" alt="Guru Studio logo" />
        </a>

        <nav id="main-nav" className={menuOpen ? 'main-nav open' : 'main-nav'} aria-label="Main">
          <a href="#home" onClick={closeMenu}>Home</a>
          <a href="#about" onClick={closeMenu}>About</a>
          <a href="#services" onClick={closeMenu}>Services</a>
          <a href="#gallery" onClick={closeMenu}>Gallery</a>
          <a href="#contact" onClick={closeMenu}>Contact</a>
        </nav>

        <a className="header-book" href="#contact">Book</a>
      </div>
    </header>
  )
}

export default Header
