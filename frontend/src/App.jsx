import Header from './components/Header.jsx'
import Hero from './components/Hero.jsx'
import About from './components/About.jsx'
import Services from './components/Services.jsx'
import Gallery from './components/Gallery.jsx'
import Reviews from './components/Reviews.jsx'
import Contact from './components/Contact.jsx'
import Footer from './components/Footer.jsx'
import AdminRouter from './admin/AdminRouter.jsx'

// The whole page. Sections appear in this order from top to bottom.
function App() {
  // The admin pages live under /admin. They do not show the public header or footer,
  // and there is no link to them from the public website.
  if (window.location.pathname.startsWith('/admin')) {
    return <AdminRouter />
  }

  return (
    <>
      <Header />
      <main>
        <Hero />
        <About />
        <Services />
        <Gallery />
        <Reviews />
        <Contact />
      </main>
      <Footer />
    </>
  )
}

export default App
