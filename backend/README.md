# Guru Studio Backend

The Spring Boot API for the Guru Studio website. It handles:

- Account registration (passwords stored as BCrypt hashes)
- Login that returns a JWT token
- Viewing and updating your own profile
- The contact form, saved to MySQL
- CORS for the React frontend, so it can call this API from another address

---

## 1. What you need

| Tool | Why | Link |
| --- | --- | --- |
| Java 21 (JDK) | Runs the app | https://adoptium.net |
| MySQL 8 | Stores users and contact messages | https://dev.mysql.com/downloads/mysql/ |
| IntelliJ IDEA (Community is free) | Your editor and run button | https://www.jetbrains.com/idea/ |
| Postman | Testing the API | https://www.postman.com/downloads/ |
| Git | Pushing to GitHub | https://git-scm.com |

You do **not** need to install Maven. The project includes `mvnw` (Maven Wrapper), which downloads Maven for you.

---

## 2. Set up MySQL (once)

Open the MySQL command line (or MySQL Workbench) and run:

```sql
CREATE DATABASE gurustudio;
```

Remember the username and password you chose for MySQL. You will type them into the environment variables below.
The tables are created automatically when the app starts. You never create them by hand.

---

## 3. Environment variables

The app reads its settings from environment variables. **Never type real passwords or secrets into the code or into `application.properties`.**

| Variable | Required? | Example | What it does |
| --- | --- | --- | --- |
| `DB_USERNAME` | **Yes** | `root` | MySQL username |
| `DB_PASSWORD` | **Yes** | `your-mysql-password` | MySQL password |
| `JWT_SECRET` | **Yes** (extra setting) | a long random string | Signs login tokens. At least 32 characters. Keep it secret. |
| `DB_URL` | No | `jdbc:mysql://localhost:3306/gurustudio?...` | Database address. The default already points to local `gurustudio`. |
| `FRONTEND_URL` | No | `https://your-site.onrender.com` | Extra website address allowed by CORS. `http://localhost:5173` is always allowed. |
| `JWT_EXPIRATION_MINUTES` | No | `60` | How long a login lasts. Default is 60. |
| `PORT` | No (Render sets it) | `8080` | Port the server listens on. Default is 8080. |

**Generating a JWT secret.** Run this once in PowerShell and copy the output into `JWT_SECRET`:

```powershell
-join ((48..57) + (65..90) + (97..122) | Get-Random -Count 64 | ForEach-Object { [char]$_ })
```

For routine local use, you only need to enter `DB_USERNAME` and `DB_PASSWORD`. Keep `JWT_SECRET` set too, because the app refuses to start without it.

---

## 4. Run the app in IntelliJ

1. Choose **File → Open** and select the `GuruStudio-Backend` folder (the one with `pom.xml`).
2. Wait for IntelliJ to finish loading Maven. The first time this can take a few minutes.
3. Open `src/main/java/com/example/demo/DemoApplication.java`.
4. Click the green ▶ next to `main`. The first run will fail because the variables are not set yet. This creates a run configuration.
5. Choose **Run → Edit Configurations…**, select **DemoApplication**.
6. In **Environment variables**, click the 📄 icon and add:
   - `DB_USERNAME=root`
   - `DB_PASSWORD=your-mysql-password`
   - `JWT_SECRET=your-long-random-secret`
7. Click **Apply**, then **OK**, then press ▶ again.

When you see `Started DemoApplication`, the API is running at **http://localhost:8080**.

---

## 5. Run the app in the terminal (PowerShell, Windows)

Go to the project folder, then set the variables for this window:

```powershell
cd C:\path\to\GuruStudio-Backend
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your-mysql-password"
$env:JWT_SECRET  = "your-long-random-secret"
.\mvnw.cmd spring-boot:run
```

To run the tests only:

```powershell
.\mvnw.cmd test
```

Stop the server with **Ctrl + C**. The variables last only for this PowerShell window.

On Mac or Linux, use `export DB_USERNAME=root` instead of `$env:...`, and `./mvnw spring-boot:run` instead of `.\mvnw.cmd`.

---

## 6. API endpoints

| Method | URL | Login needed? | What it does |
| --- | --- | --- | --- |
| POST | `/api/auth/register` | No | Create an account. Returns 201. |
| POST | `/api/auth/login` | No | Log in. Returns a token. |
| GET | `/api/users/me` | **Yes** | Your profile. |
| PUT | `/api/users/me` | **Yes** | Change your name and phone. |
| POST | `/api/contact` | No | Send a contact form message. Returns 201. |

There is no endpoint that lists contact messages. Read them in MySQL, for example:

```sql
SELECT id, name, email, subject, created_at FROM contact_messages ORDER BY created_at DESC;
```

### Request examples

**Register**

```json
POST /api/auth/register
{ "fullName": "Jane Doe", "email": "jane@example.com", "password": "secret123" }
```

**Login** (copy `accessToken` from the response)

```json
POST /api/auth/login
{ "email": "jane@example.com", "password": "secret123" }
```

Response:

```json
{ "accessToken": "eyJhbGciOi...", "tokenType": "Bearer", "expiresInMinutes": 60 }
```

**Get your profile**

```
GET /api/users/me
Authorization: Bearer eyJhbGciOi...
```

**Update your profile**

```json
PUT /api/users/me
Authorization: Bearer eyJhbGciOi...
{ "fullName": "Jane Smith", "phone": "0700 123 456" }
```

**Contact form**

```json
POST /api/contact
{ "name": "Jane Doe", "email": "jane@example.com", "subject": "Wedding photography", "message": "Hello, I would like a quote." }
```

### Error format

Every error uses the same shape:

```json
{
  "timestamp": "2026-10-11T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed. Please check the highlighted fields.",
  "path": "/api/contact",
  "fieldErrors": { "email": "Enter a valid email address." }
}
```

| Status | Meaning |
| --- | --- |
| 400 | Invalid data (see `fieldErrors`) |
| 401 | No token, bad token, expired token, or wrong email/password |
| 404 | Not found |
| 409 | Email already registered |
| 500 | Unexpected server error (details are in the server log, not in the response) |

---

## 7. Test with Postman

1. Create a new request, method **POST**, URL `http://localhost:8080/api/auth/register`.
2. Go to **Body → raw → JSON**, paste the register example, click **Send**. You should get **201**.
3. Change the URL to `http://localhost:8080/api/auth/login`, send the login example, and copy `accessToken`.
4. Create a **GET** request to `http://localhost:8080/api/users/me`.
5. Go to **Authorization → Type: Bearer Token**, paste the token, and click **Send**. You should get your profile.
6. Without the token, the same request should return **401**.
7. Send the contact form with a **POST** to `http://localhost:8080/api/contact`. You should get **201**, and a new row should appear in `contact_messages`.

---

## 8. Make an admin (manually, in MySQL only)

The API never lets a user become an admin. To make someone an admin, run this in MySQL:

```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'jane@example.com';
```

The change takes effect on the next login, because the role is stored inside the token.

---

## 9. Save the project to GitHub

1. Open `.gitignore` and confirm it contains `.env` and `target/`. Those files must never go to GitHub.
2. Do **not** put `DB_PASSWORD`, `JWT_SECRET`, or any other secret in a file in this project.
3. In PowerShell, inside the project folder:

```powershell
git init
git add .
git commit -m "Add Guru Studio backend"
git branch -M main
git remote add origin https://github.com/YOUR-USERNAME/GuruStudio-Backend.git
git push -u origin main
```

Before `git add .`, run `git status` and check that no secret file is listed.

---

## 10. Deploy to Render

The project includes a `Dockerfile`, so Render can build and run it.

### Step A: create a MySQL database

Render does not provide MySQL. Use one of these:

- A MySQL service from a provider such as **Aiven** or **PlanetScale**, or any MySQL host you already use.
- Copy its host, port, database name, username and password.

Build the connection string as: `jdbc:mysql://HOST:PORT/DATABASE_NAME?useSSL=true&serverTimezone=UTC`
(Most hosted MySQL services require SSL, so use `useSSL=true` there.)

### Step B: create the web service

1. Sign in at https://render.com and choose **New → Web Service**.
2. Connect your GitHub account and select the `GuruStudio-Backend` repository.
3. Set **Runtime** to **Docker**. Leave the Dockerfile path as `./Dockerfile`.
4. Choose a plan. Render's free plan is fine for testing.

### Step C: add environment variables

In the Render service, open **Environment** and add:

| Key | Value |
| --- | --- |
| `DB_URL` | `jdbc:mysql://HOST:PORT/DATABASE_NAME?useSSL=true&serverTimezone=UTC` |
| `DB_USERNAME` | your hosted database username |
| `DB_PASSWORD` | your hosted database password |
| `JWT_SECRET` | a new long random string (do not reuse your local one) |
| `FRONTEND_URL` | the address of your frontend on Render, for example `https://guru-studio-frontend.onrender.com` |

Render sets `PORT` for you, so do not add it.

Click **Deploy**. When the logs show `Started`, open `https://YOUR-SERVICE.onrender.com/api/auth/login` in a browser. A **405** or JSON error response means the server is running. A connection error means it is not.

Note: on the free plan Render may sleep the service after inactivity, so the first request can be slow.

---

## 11. Run the tests

```powershell
.\mvnw.cmd test
```

The tests check password hashing, duplicate email handling, login errors, JWT creation and rejection of tampered tokens, profile updates, contact form saving, and validation rules. They do not need MySQL.

---

## 12. Security notes

- Passwords: stored as BCrypt hashes only. Never returned by any endpoint.
- Login errors say "Invalid email or password." for both a wrong email and a wrong password.
- Tokens expire after `JWT_EXPIRATION_MINUTES` (60 by default).
- There is **no logout** and **no token revocation**. A token stays valid until it expires. To shorten that window, lower `JWT_EXPIRATION_MINUTES`.
- A user cannot change their own role. Admins are set only in MySQL (section 8).
- Keep `JWT_SECRET` secret. Changing it logs everyone out.
- Use HTTPS in production (Render provides it automatically).

---

## 13. Project structure

```
GuruStudio-Backend/
├── Dockerfile                    Build and run instructions for Render
├── .dockerignore                 Files the Docker build must skip
├── .gitignore                    Files Git must not upload (secrets, build output)
├── pom.xml                       Libraries and build settings
├── mvnw / mvnw.cmd               Maven Wrapper (no Maven install needed)
└── src/
    ├── main/java/com/example/demo/
    │   ├── DemoApplication.java      Starts the app
    │   ├── config/                   Security, CORS
    │   ├── security/                 JWT creation and checking
    │   ├── controller/               The URLs (endpoints)
    │   ├── service/                  The business rules
    │   ├── repository/               Database access
    │   ├── entity/                   Database tables (User, ContactMessage)
    │   ├── dto/                      Request and response shapes
    │   └── exception/                Error handling
    ├── main/resources/application.properties   Settings (read from environment variables)
    └── test/java/...                 Unit tests
```

---

## 14. Troubleshooting

| Problem | Fix |
| --- | --- |
| `Could not resolve placeholder 'DB_USERNAME'` | Set `DB_USERNAME` (see section 3). |
| `Could not resolve placeholder 'JWT_SECRET'` | Set `JWT_SECRET`. |
| `JWT_SECRET must be at least 32 characters long` | Use a longer secret. |
| `Access denied for user` | Wrong `DB_USERNAME` or `DB_PASSWORD`. |
| `Unknown database 'gurustudio'` | Run `CREATE DATABASE gurustudio;` (section 2). |
| `Port 8080 was already in use` | Stop the other program, or set `PORT=8081` (and use that port in Postman). |
| Browser says CORS error | Add your frontend address to `FRONTEND_URL`. |
| 401 on `/api/users/me` | Add `Authorization: Bearer <token>`, and get a fresh token if the old one expired. |
