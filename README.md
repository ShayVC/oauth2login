# OAuth2 Login Demo (Spring Boot + Google + GitHub)

## 🧠 Overview
This project is a **Spring Boot OAuth2 Login Application** integrating authentication via **Google** and **GitHub**.  
It demonstrates secure OAuth2 login, automatic user registration, profile management, and session-based authentication using **Spring Security** and **JPA**.

> 💡 *Currently uses **Thymeleaf** for demonstration purposes; however, a **React frontend** can easily consume the same backend endpoints (`/api/profile`) for a full single-page experience.*

---

## 🎯 Features

| Feature | Description |
|----------|-------------|
| 🔐 **OAuth2 Login (Google & GitHub)** | Login using Google or GitHub through Spring Security OAuth2. |
| 👤 **Automatic User Registration** | Creates a user record upon first login. |
| 🔁 **Unified Login** | Google and GitHub logins map to the same user if emails match. |
| 📝 **Profile Management** | Authenticated users can view and update profile info. |
| 💾 **Database Integration** | Uses H2 for development (can switch to MySQL/PostgreSQL). |
| 🧱 **Session-based Security** | Managed via Spring Security (no JWT required). |
| 🚪 **Logout Support** | Clean session invalidation and redirect to home. |
| ⚠️ **Custom Error Page** | Friendly fallback for invalid routes. |

---

## 🧩 Architecture Overview

### 🧭 System Flow Diagram
<img width="420" height="132" alt="image" src="https://github.com/user-attachments/assets/c253e96e-e5b6-44a5-970a-fdeb6a732af0" />
<img width="198" height="437" alt="image" src="https://github.com/user-attachments/assets/65f1ee11-7475-4432-8197-696de328a8f6" />

## ⚙️ Technologies Used

Spring Boot 3.5+

Spring Security (OAuth2 Client)

Spring Data JPA

Thymeleaf

H2 Database (for development)

Maven

## 🚀 Getting Started
1️⃣ Clone the Repository
git clone https://github.com/YOUR_USERNAME/oauth2login-main.git
cd oauth2login-main

2️⃣ Create OAuth Credentials
Google

Go to Google Cloud Console

Create an OAuth Client (Web Application)

Add Authorized redirect URI:

http://localhost:8080/login/oauth2/code/google

GitHub

Go to GitHub Developer Settings → OAuth Apps

Register a new OAuth app

Add Authorization callback URL:

http://localhost:8080/login/oauth2/code/github

3️⃣ Configure Environment Variables
Windows (PowerShell)
setx GOOGLE_CLIENT_ID "your-google-client-id"
setx GOOGLE_CLIENT_SECRET "your-google-client-secret"
setx GITHUB_CLIENT_ID "your-github-client-id"
setx GITHUB_CLIENT_SECRET "your-github-client-secret"


Restart IntelliJ or your terminal after setting them.

4️⃣ Run the Application
./mvnw spring-boot:run


Open your browser and visit:

http://localhost:8080/

## 🔐 Endpoints Summary
Method	Path	Description	Access
GET	/	Home with Login buttons	Public
GET	/profile	View profile	Authenticated
POST	/profile	Update display name and bio	Authenticated
GET	/logout	Logout and redirect to home	Authenticated
GET	/error	Custom error page	Public
## 🧰 Project Structure
<img width="274" height="471" alt="image" src="https://github.com/user-attachments/assets/fa5f8a07-2781-49ce-9511-a110c66fd8de" />

## 🧩 Security Overview

Session-based Authentication: Managed by Spring Security (no JWT).

CSRF Protection: Enabled (disabled only for H2 console).

OAuth2 Providers: Integrated via spring-boot-starter-oauth2-client.

Provider Linking: Unifies user identity across Google & GitHub.

Custom Error Handling: Displays friendly UI for failed routes.

## 🧠 Notes on React Integration

The backend already exposes REST-compatible endpoints (/profile and /api/profile).
A React frontend can easily:

Fetch user info via GET /api/profile

Update it via POST /api/profile

Handle login redirects through /oauth2/authorization/google or /oauth2/authorization/github

This ensures the backend is React-ready with no structural changes needed.

## 🧱 Future Enhancements
Feature	Description

🌐 Full React Frontend	Replace Thymeleaf with a React SPA using REST APIs

🗄️ Switch to MySQL	Persist user records across sessions

🧠 Add /api/profile	Expand API support for external clients

🎨 Enhanced UI	Add Bootstrap or Tailwind styling

🧱 Role-based Auth	Introduce ADMIN / USER permissions

## 👨‍💻 Author
Shayne Angus
Cebu City, Philippines 🇵🇭
Built as part of a learning project integrating Spring Boot, OAuth2, and user profile management.

## 🏁 License

This project is licensed under the MIT License.
