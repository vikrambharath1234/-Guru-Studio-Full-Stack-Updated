# Guru Studio – Full Stack Project

This folder holds the whole Guru Studio website:

- **frontend/** – the public website and the admin dashboard (React + Vite).
- **backend/** – the server that saves bookings, messages, reviews and photos (Spring Boot + MySQL).

The two apps run separately. The frontend asks the backend for data over HTTP, using a language called JSON.

---

## 1. Quick start (do these in order)

### Step A – Create the database (MySQL Workbench)

1. Open **MySQL Workbench** and double-click your local connection (for example *Local instance MySQL80*). Type your MySQL password if asked.
2. Click the **Create a new schema** icon in the toolbar (a cylinder with a small `+`). If you cannot find it, use the menu **Database → Create Schema…**
3. In the **Name** box, type exactly: `gurustudio`
4. Click **Apply**, then **Apply**, then **Finish**.
5. In the left panel, under *SCHEMAS*, you should see `gurustudio`.

You do **not** create any tables. The backend creates them automatically the first time it starts.

(Prefer typing? Open a new SQL tab, type `CREATE DATABASE gurustudio;`, and click the lightning bolt icon.)

### Step B – Set the backend's environment variables

The backend will not start until these are set. Use the values from `.env.example` as a guide. Replace every placeholder with your own value.

| Variable | Required? | What it is |
| --- | --- | --- |
| `DB_URL` | Optional | Database address. Default: `jdbc:mysql://localhost:3306/gurustudio` |
| `DB_USERNAME` | **Yes** | Your MySQL username (often `root`) |
| `DB_PASSWORD` | **Yes** | Your MySQL password |
| `JWT_SECRET` | **Yes** | A random text of at least 32 characters. It signs the admin's login token. |
| `ADMIN_USERNAME` | **Yes** (for admin) | The admin login name you choose |
| `ADMIN_PASSWORD` | **Yes** (for admin) | The admin password you choose (at least 8 characters) |
| `CLOUDINARY_CLOUD_NAME` | Later | Needed only for photo uploads (see section 4) |
| `CLOUDINARY_API_KEY` | Later | Same |
| `CLOUDINARY_API_SECRET` | Later | Same |
| `FRONTEND_URL` | Optional | The website address allowed to call the backend. Local website (`http://localhost:5173`) is always allowed. |

**Generating a JWT_SECRET** (PowerShell, run once, copy the output):

```powershell
-join ((48..57) + (65..90) + (97..122) | Get-Random -Count 64 | ForEach-Object { [char]$_ })
```

**Setting them in IntelliJ IDEA (recommended, the values are saved only on your computer):**

1. Open the `backend` folder in IntelliJ (the folder with `pom.xml`). Wait for Maven to finish loading.
2. Open `backend/src/main/java/com/example/demo/DemoApplication.java`, click the green ▶ next to `main`. It will fail the first time. That is expected, because the variables are not set yet. It creates a run configuration.
3. Go to **Run → Edit Configurations…**, select **DemoApplication**.
4. Under **Environment variables**, click the 📄 icon and add the variables in the form `NAME=value`, separated by semicolons. For example: `DB_USERNAME=root;DB_PASSWORD=your-password;JWT_SECRET=your-long-secret;ADMIN_USERNAME=your-admin;ADMIN_PASSWORD=your-admin-password`
5. Click **Apply**, then **OK**.

**Setting them in PowerShell (for one window only):** type each line, then start the backend from the same window:

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your-mysql-password"
$env:JWT_SECRET="your-long-random-secret-at-least-32-characters"
$env:ADMIN_USERNAME="your-admin-name"
$env:ADMIN_PASSWORD="your-admin-password"
cd backend
./mvnw spring-boot:run
```

### Step C – Start the backend

Run `DemoApplication` in IntelliJ (or use the PowerShell commands above). When you see a line ending with `Started DemoApplication`, the API is running at **http://localhost:8080**.

Check the log for `Admin account is ready.` If you see `ADMIN_USERNAME or ADMIN_PASSWORD is not set`, the admin variables are missing.

### Step D – Start the frontend

Open a terminal in the `frontend` folder and run:

```powershell
npm install
npm run dev
```

`npm install` is needed once (it downloads the libraries, including Axios). Then open **http://localhost:5173** to see the website.

Optional: copy `.env.example` to `frontend/.env` if you want to change the backend address. Locally the default already works.

### Step E – Open the admin dashboard

Go to **http://localhost:5173/admin** and log in with your `ADMIN_USERNAME` and `ADMIN_PASSWORD`.

The admin page is not linked from the public website. Only someone who knows the address can find it.

---

## 2. Admin account

- There is **one** admin account. It is created from `ADMIN_USERNAME` and `ADMIN_PASSWORD` every time the backend starts.
- There is **no public admin sign-up**. Customers who register through the public API get a normal account that cannot open the dashboard.
- The password is stored only as a **BCrypt hash** (a scrambled form that cannot be turned back into the password). The plain password never goes into the database.
- To change the password: change `ADMIN_PASSWORD` and restart the backend. The new password takes effect immediately.
- Login tokens last 60 minutes by default (`JWT_EXPIRATION_MINUTES`). After that, the dashboard asks for the password again.

---

## 3. Cloudinary (photo storage)

Photos are not stored in MySQL. They are uploaded to **Cloudinary**, an image hosting service, and MySQL only keeps each photo's web address and category.

1. Create a free account at https://cloudinary.com.
2. On the Cloudinary **Dashboard**, copy the **Cloud name**, **API Key** and **API Secret**.
3. Set `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET` in your run settings (same way as Step B) and restart the backend.
4. In the admin dashboard, open the **Gallery** tab, choose a category, pick one or more photos and click **Upload Photos**.

Until these variables are set, the rest of the site works, but uploads show the message "Image storage is not configured yet."

Photos are uploaded into folders named `guru-studio/<category>` in Cloudinary. Deleting a photo in the dashboard deletes it from Cloudinary too.

---

## 4. How the pieces work together

```
 Customer's browser                React (frontend)                 Spring Boot (backend)            MySQL
 ───────────────────               ────────────────                 ─────────────────────            ─────
 fills the enquiry form  ───────►  Contact.jsx                      
                                   └─ publicService.js ──HTTP────►  BookingController
                                      (Axios)                         └─ BookingService
                                                                         └─ BookingRepository ──► bookings table
 
 admin clicks "Approve"  ───────►  ReviewsPanel.jsx
                                   └─ adminService.js ──HTTP────►  AdminReviewController
                                      (Axios + token)                 └─ ReviewService
                                                                         └─ ReviewRepository ───► reviews table
```

Step by step, for a booking request:

1. The visitor fills the form in `Contact.jsx` and clicks **Send Enquiry**.
2. `Contact.jsx` calls a function in `publicService.js`. That function uses **Axios** (a library that makes HTTP requests) to send `POST /api/bookings` with the form data as JSON.
3. Spring Boot receives the request in **`BookingController`**. The `@Valid` annotation checks the data against the rules in **`BookingRequest`** (for example, a valid email and phone number).
4. The controller passes the data to **`BookingService`**, which trims the text and creates a **`Booking`** object.
5. **`BookingRepository`** saves that object. Spring Data JPA turns the save into SQL and writes a row into the **`bookings`** table in MySQL.
6. The backend replies `201 Created` with a message, and the form shows it to the visitor.

**Admin requests** work the same way, with two extra steps: the browser attaches the login token to the request (`api.js` does this), and the backend's security rules (`SecurityConfig`) reject any request without a valid admin token.

**Reading data** goes the other way: the repository reads rows from MySQL, the service turns them into response objects (DTOs), the controller sends them back as JSON, and React shows them.

**The layers, in one sentence each:**

- **Controller** – receives the HTTP request and returns the answer. It does not contain business rules.
- **Service** – the business rules (for example, "a new booking is always PENDING").
- **Repository** – the only place that talks to the database. You write method names, and Spring writes the SQL.
- **Entity** – a Java class that matches one database table. Each field is one column.
- **DTO** – a small class that defines the exact data going in or out of an endpoint. It keeps internal fields (such as the password hash) away from the browser.

---

## 5. Project structure

```
GuruStudio-FullStack/
├── .env.example            Template of every environment variable (no real values)
├── .gitignore              Stops secrets, node_modules and build files from being committed
├── README.md               This file
├── backend/                Spring Boot API (Java)
│   ├── pom.xml             The list of libraries (dependencies) Maven downloads
│   ├── Dockerfile          Used when deploying the backend
│   └── src/
│       ├── main/java/com/example/demo/
│       │   ├── DemoApplication.java   The starting point of the backend
│       │   ├── config/        Settings: security, CORS, admin account setup
│       │   ├── controller/    The URLs (endpoints) the frontend calls
│       │   ├── service/       The business rules
│       │   ├── repository/    Database access
│       │   ├── entity/        Classes that match database tables
│       │   ├── dto/           Request and response shapes
│       │   ├── security/      Login tokens (JWT) and the filter that reads them
│       │   └── exception/     Turns errors into clear JSON messages
│       ├── main/resources/application.properties   Reads settings from environment variables
│       └── test/           Automated tests
└── frontend/               React website and admin dashboard
    ├── package.json        The list of frontend libraries
    ├── index.html          The one HTML page React runs inside
    ├── public/             Files served as they are (logo, favicon, all photos)
    └── src/
        ├── App.jsx         The page layout, and the switch between the website and /admin
        ├── main.jsx        Starts React
        ├── components/     The sections of the public website
        ├── styles/         The CSS for the public website
        ├── services/       All calls to the backend (api.js, publicService.js, adminService.js)
        ├── admin/          The admin login and dashboard
        ├── photos.js       The list of built-in photos (this file was not changed)
        └── siteInfo.js     Phone, email and address shown on the site
```

---

## 6. Backend: what each important class does

### Starting and settings (`config`, `DemoApplication`)

| Class | What it does |
| --- | --- |
| `DemoApplication` | The `main` method. Starts Spring Boot. |
| `SecurityConfig` | The rules for who may call what. Public: contact, booking, review submit, gallery and approved reviews, and login. Admin only: everything under `/api/admin/`. Everything else needs a login. Passwords are hashed with BCrypt here. |
| `CorsConfig` | Lets only the frontend's address (`localhost:5173`, or `FRONTEND_URL`) call the API from a browser. |
| `AdminAccountInitializer` | Runs once at startup. Creates or updates the admin account from `ADMIN_USERNAME` and `ADMIN_PASSWORD`. |

### Endpoints (`controller`)

| Class | URL(s) | Who can use it | What it does |
| --- | --- | --- | --- |
| `BookingController` | `POST /api/bookings` | Public | Saves a booking request as PENDING. |
| `ContactController` | `POST /api/contact` | Public | Saves a contact form message. |
| `ReviewController` | `POST /api/reviews`, `GET /api/reviews` | Public | Saves a review. Lists only the approved ones. |
| `GalleryController` | `GET /api/gallery` (optional `?category=weddings`) | Public | Lists uploaded photos. |
| `AuthController` | `POST /api/auth/login` | Public | Checks the username and password and returns a token. |
| `AdminDashboardController` | `GET /api/admin/summary` | Admin | The numbers at the top of the dashboard. |
| `AdminBookingController` | `GET /api/admin/bookings`, `PUT /api/admin/bookings/{id}/status` | Admin | Lists bookings and sets PENDING, CONFIRMED or REJECTED. |
| `AdminContactController` | `GET /api/admin/contact-messages`, `DELETE /api/admin/contact-messages/{id}` | Admin | Lists and deletes contact messages. |
| `AdminReviewController` | `GET /api/admin/reviews`, `PUT /api/admin/reviews/{id}/status` | Admin | Lists reviews and approves or rejects them. |
| `AdminGalleryController` | `GET`, `POST` (upload), `DELETE /api/admin/gallery...` | Admin | Lists, uploads and deletes photos. |
| `UserController` | `GET/PUT /api/users/me` | Logged-in user | The customer's own profile (existing feature). |

### Business rules (`service`)

| Class | What it does |
| --- | --- |
| `BookingService` | Saves bookings (always PENDING) and changes their status. It never confirms a booking by itself. |
| `ContactService` | Saves and lists contact messages, and deletes them. |
| `ReviewService` | Saves reviews as PENDING. Lists approved ones. The admin can only set APPROVED or REJECTED. |
| `GalleryService` | Checks the category and file types, uploads each photo to Cloudinary, and saves its web address in MySQL. |
| `CloudinaryService` | Talks to Cloudinary. It signs each request with your API secret (the secret itself is never sent). |
| `DashboardService` | Counts the rows for the dashboard numbers. |
| `AuthService` | Checks a login and returns a token (existing feature). |
| `UserService` | Reads and updates a customer's own profile (existing feature). |

### Database classes (`entity` and `repository`)

| Class | Table | Notes |
| --- | --- | --- |
| `Booking` | `bookings` | Name, phone, email, event type, date, location, details, status, time received. |
| `ContactMessage` | `contact_messages` | Name, phone, email, subject, message, time received. |
| `Review` | `reviews` | Name, rating (1–5), message, status, time received. |
| `GalleryImage` | `gallery_images` | Web address, Cloudinary id and category of one photo. |
| `User` | `users` | Customer accounts and the admin account. Stores only the password hash. |
| `BookingStatus`, `ReviewStatus`, `Role` | (enums) | A fixed list of allowed values. |
| `BookingRepository` and the other `...Repository` interfaces | — | You write only method names like `findAllByOrderByCreatedAtDesc`. Spring writes the SQL. |

### Request and response shapes (`dto`) and errors (`exception`)

- Each `...Request` record defines what the frontend must send, with rules such as `@NotBlank` and `@Email`.
- Each `...Response` record defines what the frontend receives. It never includes the password hash.
- `GlobalExceptionHandler` turns every error into the same JSON shape, so the frontend always gets a readable message.

### Login tokens (`security`)

- `JwtService` creates a signed token after a successful login and checks it on later requests.
- `JwtAuthenticationFilter` reads the `Authorization: Bearer ...` header on each request.

---

## 7. Frontend: what each important part does

### Pages and layout

| File | What it does |
| --- | --- |
| `App.jsx` | Shows the public website. If the address starts with `/admin`, it shows the admin pages instead. |
| `main.jsx` | Starts React and loads the global styles. |

### Public website components (`components/`)

| File | What it does |
| --- | --- |
| `Header.jsx`, `Hero.jsx`, `About.jsx`, `Footer.jsx` | Unchanged. |
| `Services.jsx`, `PhotoSlideshow.jsx` | Unchanged. The service cards and 2-second slideshows. |
| `Gallery.jsx` | Shows the built-in photos, **plus** the photos uploaded from the admin dashboard. It asks the backend when the page opens. If the backend is offline, only the built-in photos show. |
| `Reviews.jsx` | New section. Shows approved reviews and a form for customers to write one. |
| `Contact.jsx` | The enquiry form. It now sends the enquiry to the backend (as a booking and as a contact message) instead of opening an email app. |

### Services (`services/`) – all backend calls live here

| File | What it does |
| --- | --- |
| `api.js` | Creates the Axios connection to the backend. Adds the admin token to requests. If a request returns "not logged in" (401), it logs the admin out. |
| `publicService.js` | Calls for the public website (send booking, send contact message, get gallery, get reviews, send review). |
| `adminService.js` | Calls for the admin dashboard (login, numbers, bookings, messages, gallery, reviews). |

**Axios** is a library that sends HTTP requests. For example, `api.post('/api/bookings', data)` sends a `POST` request with `data` as JSON, and `await` waits for the reply.

### Admin dashboard (`admin/`)

| File | What it does |
| --- | --- |
| `AdminRouter.jsx` | Chooses between the login page and the dashboard, based on the address. Sends a visitor without a login token back to `/admin`. |
| `AdminLogin.jsx` | The login form at `/admin`. Saves the token in the browser after a successful login. |
| `AdminDashboard.jsx` | The dashboard at `/admin/dashboard`. Shows the five summary numbers, the tabs, and the logout button. |
| `BookingsPanel.jsx` | Lists booking requests and a dropdown to set Pending, Confirmed or Rejected. |
| `MessagesPanel.jsx` | Lists contact messages and a delete button. |
| `GalleryPanel.jsx` | Upload form (category + one or more photos), the uploaded photos, and delete buttons. |
| `ReviewsPanel.jsx` | Lists reviews with Approve and Reject buttons. |
| `adminHelpers.js` | Category names and date formatting. |
| `Admin.css` | The dashboard's styles. It reuses the site's colours and fonts. |

---

## 8. Changes made to the existing frontend (so you can check them)

The design was kept. These are the only changes:

1. **Enquiry form (Contact section):** now sends to the backend. Added a required **Event location** field, styled like the others. **Event date** is now required, because a booking needs one. The text under the button now says that the booking is confirmed only after the studio replies.
2. **Gallery:** shows uploaded photos in their category, after the built-in photos. Nothing else in the gallery changed.
3. **Reviews (new section):** placed between the Gallery and the Contact section. It shows only approved reviews, and has a review form. This section is new, because the site had no reviews before. If you would rather not have a visible reviews section, tell me, and I can change how it is shown.
4. **Admin pages:** `/admin` and `/admin/dashboard`. They are not linked from the public header or footer.
5. **Not changed:** header, footer, logo, favicon, service cards, the Wedding card, the Pre-Wedding card and its photos, slideshow timing, image positions, the hero image, colours, fonts and navigation.

**Why the enquiry form sends two records:** the form asks for booking details (event type, date, location). The site also has a contact message feature. So each enquiry is saved once as a booking and once as a contact message, and both show in the dashboard. If you only want one of them, tell me which one, and I will change it.

---

## 9. Security notes

- Secrets (database password, admin password, JWT secret, Cloudinary secret) are read only from environment variables. None are written in the code.
- Admin pages and `/api/admin/**` need a valid admin token. Without one, the answer is `401`. A logged-in non-admin gets `403`.
- Customer booking, contact and review details are never shown on public URLs.
- CORS allows only the frontend's own address. There is no `*` wildcard.
- Passwords are hashed with BCrypt.
- Input is checked on the server (not only in the browser). Photos must be JPG, PNG or WEBP and up to 10 MB.
- Do not commit a real `.env` file. The `.gitignore` file already excludes it.

---

## 10. Deploying (short version)

- **Backend:** deploy the `backend` folder (it has a `Dockerfile`) to a host such as Render. Set every environment variable from Step B in the host's settings. Set `FRONTEND_URL` to your real website address.
- **Frontend:** run `npm run build` in `frontend`. The result is in `frontend/dist`. Host it as a static site. Set `VITE_API_URL` to the deployed backend address **before** building.
- **Important for /admin on a static host:** configure a rewrite rule so that every address goes to `index.html` (for example `/*` → `/index.html`). Without it, `/admin` shows a "page not found" error after a refresh.

---

## 11. Testing status

These checks were **not** all run, because the environment used to prepare this project could not download libraries (no internet access). Please run them yourself:

1. `npm install` and `npm run build` in `frontend`.
2. Start the backend with your real environment variables. The first run creates the tables in `gurustudio`.
3. `mvnw test` in `backend` runs the automated tests (the contact tests were updated for the new phone field).
4. Test the site: send an enquiry, open `/admin`, log in, confirm a booking, delete a message, upload a photo (after Cloudinary is set), approve a review and check it appears on the website.

---

## 12. Troubleshooting

| Problem | Likely cause and fix |
| --- | --- |
| The backend stops at start with a message about `DB_USERNAME` or `JWT_SECRET` | The variable is not set in your run settings. |
| `Access denied for user` | Wrong `DB_USERNAME` or `DB_PASSWORD`. |
| `Unknown database 'gurustudio'` | Create the database (Step A). |
| `ADMIN_USERNAME or ADMIN_PASSWORD is not set` in the log | Set both admin variables and restart. |
| Login says "Invalid email or password" | Check the username and password you set in `ADMIN_*`. Remember that changing them needs a restart. |
| The enquiry form says it cannot reach the studio | The backend is not running, or `VITE_API_URL` points to the wrong address. |
| Browser shows a CORS error | Your frontend address is not allowed. Use `http://localhost:5173`, or set `FRONTEND_URL`. |
| Upload says "Image storage is not configured yet" | Set the three `CLOUDINARY_*` variables and restart. |
| Upload says "The file is too large" | Photos must be 10 MB or smaller. |
