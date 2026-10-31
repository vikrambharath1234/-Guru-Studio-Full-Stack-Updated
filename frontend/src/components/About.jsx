import '../styles/About.css'

function About() {
  return (
    <section className="section section-light" id="about">
      <div className="about-grid">
        <div className="about-text">
          <p className="eyebrow">About Guru Studio</p>
          <h2>We Don’t Just Capture Moments. We Preserve Emotions.</h2>
          <p className="body-text">
            Guru Studio is a photography studio that tells the story of life’s special days. We photograph
            weddings, pre-wedding and post-wedding shoots, birthday celebrations, puberty ceremonies, and
            engagements, with a focus on natural emotions and family traditions.
          </p>

          {/* Experience highlight */}
          <div className="experience-card">
            <p className="experience-number">25+</p>
            <p className="experience-label">Years of Photography Experience</p>
          </div>
        </div>

        <figure className="about-photo">
          <img src="/images/weddings/wedding-09.jpg" alt="Bride in a traditional saree by a window" />
        </figure>
      </div>
    </section>
  )
}

export default About
