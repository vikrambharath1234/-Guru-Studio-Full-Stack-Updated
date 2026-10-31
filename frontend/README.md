# Guru Studio: Photography Website (React + Vite)

The Guru Studio website, built with React and Vite. There is no backend yet.

## 1. What you need

- Node.js 20 or newer (Node.js 22 LTS recommended) from https://nodejs.org
- VS Code (optional) and Google Chrome

Check Node.js in a terminal:

```
node -v
npm -v
```

## 2. Install the packages (first time only)

Open a terminal in the `GuruStudio` folder (the one that contains `package.json`) and run:

```
npm install
```

This creates the `node_modules` folder. It is needed once.

## 3. Start the website on your computer

```
npm run dev
```

Open the address shown in the terminal, usually `http://localhost:5173`.
Press **Ctrl + C** in the terminal to stop the server.

## 4. Build the finished website

```
npm run build
```

This creates a `dist` folder. That is the version you upload to a web host (for example Vercel).

## 5. Where to change things

| What | File |
| --- | --- |
| Phone, email, address, Google Maps link (placeholders) | `src/siteInfo.js` |
| Gallery photos and filter buttons | `src/photos.js` and `src/components/Gallery.jsx` |
| Photos on the service cards (slideshows) | `src/photos.js` and `src/components/Services.jsx` |
| Slideshow timing (2 seconds) | `src/components/PhotoSlideshow.jsx` |
| Header and menu | `src/components/Header.jsx` |
| Logo size | `src/styles/Header.css` |
| Hero text and background photo | `src/components/Hero.jsx` |
| About text and photo | `src/components/About.jsx` |
| Services text | `src/components/Services.jsx` |
| Contact section and enquiry form | `src/components/Contact.jsx` |
| Footer | `src/components/Footer.jsx` |
| Colours and fonts | `src/styles/global.css` (top of the file) |

## 6. Contact details

Open `src/siteInfo.js` to change the phone number, email, address or Google Maps link. The contact section and the footer both read from this file.

## 7. Service slideshows and photo crops

- Each service card cycles through its photos every 2 seconds, fading from one to the next (`src/components/PhotoSlideshow.jsx`).
- If a face is cut off in a card, change that photo's `position` in `src/photos.js`, for example `position: 'center 18%'`. Smaller percentages show more of the top of the photo.

## 8. Photos

- The photos are in `public/images/` in these folders: `weddings`, `engagements`, `puberty`, `birthdays`, `valaikappu`.
- The Home page hero photo is `public/images/wedding/w-4.jpeg`. Do not replace it unless you want a different hero.
- The logo is `public/images/logo.png`. The header and footer both use it.
- To add a photo: copy the file into the right folder, then add one line to `src/photos.js`:

```js
{ src: '/images/weddings/wedding-28.jpg', alt: 'Wedding photograph 28 by Guru Studio' },
```

## 9. Project structure

```
GuruStudio/
├── index.html           Page shell and font links
├── package.json         Project details and package list
├── vite.config.js       Vite settings
├── public/images/       Photos and logo used by the website
└── src/
    ├── main.jsx         Starts React
    ├── App.jsx          Sets the order of the sections
    ├── photos.js        List of all photos by category
    ├── siteInfo.js      Contact details (placeholders)
    ├── components/      One file per section, plus the slideshow
    └── styles/          One CSS file per section
```
