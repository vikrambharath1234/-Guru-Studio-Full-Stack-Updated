import '../styles/Hero.css'

function Hero() {
  return (
    <section className="hero" id="home">
      {/* Background photo. object-fit: cover keeps it filling the screen without stretching. */}
      <img
        className="hero-image"
        src="/images/wedding/w-4.jpeg"
        alt="Couple in traditional wedding attire with flower garlands"
      />
      <div className="hero-overlay" />

      <div className="hero-content">
        <p className="hero-experience">25+ Years of Photography Experience</p>
        <h1>Capturing Moments. Preserving Memories.</h1>
        <p className="hero-text">
          From the first look to the final celebration, we capture the emotions, people, and small
          moments that make your special occasions unforgettable.
        </p>

        <div className="hero-buttons">
          <a className="button button-gold" href="#services">Explore Our Work</a>
          <a className="button button-outline" href="#contact">Book Your Event</a>
        </div>
      </div>
    </section>
  )
}

export default Hero
