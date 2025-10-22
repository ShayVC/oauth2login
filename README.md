🧾 OAuth2 Login Demo (Spring Boot + Google + GitHub)
🧠 Overview

This project is a Spring Boot OAuth2 Login Application that integrates authentication via Google and GitHub.
It demonstrates secure OAuth2 login, automatic user registration, profile management, and session-based authentication using Spring Security and JPA.

💡 Currently uses Thymeleaf for demonstration purposes; however, a React frontend can easily consume the same backend endpoints (/api/profile) for a full single-page experience.

🎯 Features
Feature	Description
🔐 OAuth2 Login (Google & GitHub)	Login via either Google or GitHub using Spring Security.
👤 Automatic User Registration	Creates a user record on first login.
🔁 Unified Login	Subsequent logins map to the same user by email or OAuth ID.
📝 Profile Management	View and update your display name and bio.
💾 Database Integration	Uses H2 (in-memory) for development; compatible with MySQL or PostgreSQL.
🧱 Spring Security + CSRF Protection	Provides session-based security (no JWT).
🚪 Logout Support	Secure logout with redirect to home.
⚠️ Custom Error Page	Friendly error message for invalid routes.
🧩 Architecture Overview
🧭 System Flow Diagram (Mermaid)
flowchart TD
    A[User Browser] -->|Login Request| B(Spring Boot Application)
    B -->|OAuth2 Redirect| C[Google / GitHub]
    C -->|Access Token + User Info| B
    B -->|Save or Update User| D[(Database: H2/MySQL)]
    B -->|Authenticated Session| A
    A -->|Access| E[/Profile Page/]

🧱 Layer Diagram
┌─────────────────────────────┐
│         Frontend            │
│  (Thymeleaf Templates)      │
│  *React integration possible*│
└─────────────┬───────────────┘
              │
┌─────────────▼───────────────┐
│     Spring Boot Backend     │
│  - SecurityConfig.java      │
│  - CustomOAuth2UserService  │
│  - ProfileController        │
│  - AuthProviderService      │
└─────────────┬───────────────┘
              │
┌─────────────▼───────────────┐
│        JPA Layer            │
│  - UserRepository           │
│  - AuthProviderRepository   │
└─────────────┬───────────────┘
              │
┌─────────────▼───────────────┐
│    Database (H2/MySQL)      │
│  Stores user & provider data│
└─────────────────────────────┘

⚙️ Technologies Used

Spring Boot 3.5+

Spring Security (OAuth2 Client)

Spring Data JPA

Thymeleaf (React-ready backend)

H2 Database (for dev)

Maven

🚀 Getting Started
1️⃣ Clone the Repository
git clone https://github.com/YOUR_USERNAME/oauth2login-main.git
cd oauth2login-main

2️⃣ Create OAuth Credentials
Google

Go to Google Cloud Console

Create OAuth Client (Web Application)

Add Authorized redirect URI:

http://localhost:8080/login/oauth2/code/google

GitHub

Go to GitHub Developer Settings → OAuth Apps

Register a new application

Add Authorization callback URL:

http://localhost:8080/login/oauth2/code/github

3️⃣ Configure Environment Variables
Windows (PowerShell)
setx GOOGLE_CLIENT_ID "your-google-client-id"
setx GOOGLE_CLIENT_SECRET "your-google-client-secret"
setx GITHUB_CLIENT_ID "your-github-client-id"
setx GITHUB_CLIENT_SECRET "your-github-client-secret"


Then restart IntelliJ or your terminal.

4️⃣ Run the Application
./mvnw spring-boot:run


Visit:

http://localhost:8080/

🔐 Endpoints Summary
Method	Path	Description	Access
GET	/	Home page with login buttons	Public
GET	/profile	View own profile	Authenticated
POST	/profile	Update display name and bio	Authenticated
GET	/logout	Logout and redirect to home	Authenticated
GET	/error	Friendly error page	Public
🧰 Project Structure
src/
 ├─ main/
 │   ├─ java/com/example/oauth2login/
 │   │   ├─ controller/
 │   │   │   ├─ HomeController.java
 │   │   │   ├─ ProfileController.java
 │   │   │   └─ ErrorController.java
 │   │   ├─ model/
 │   │   │   ├─ User.java
 │   │   │   └─ AuthProvider.java
 │   │   ├─ repository/
 │   │   │   ├─ UserRepository.java
 │   │   │   └─ AuthProviderRepository.java
 │   │   ├─ security/
 │   │   │   ├─ SecurityConfig.java
 │   │   │   └─ CustomOAuth2UserService.java
 │   │   ├─ service/
 │   │   │   └─ AuthProviderService.java
 │   │   └─ OAuth2LoginDemoApplication.java
 │   └─ resources/
 │       ├─ templates/
 │       │   ├─ home.html
 │       │   ├─ profile.html
 │       │   └─ error.html
 │       └─ application.yml
 └─ test/

🧩 Security Overview

Session-based Security: No JWTs; session cookies managed by Spring.

CSRF Protection: Enabled (disabled only for H2 Console).

OAuth2 Client Integration: via spring-boot-starter-oauth2-client.

Provider Linking: Unified user across Google/GitHub using email or OAuth ID.

🧠 Notes on React Integration

💡 The backend already exposes endpoints that can easily be consumed by a React frontend (for example, /profile or /api/profile).
A simple React app could:

Fetch GET /api/profile for user info

Send updates via POST /api/profile

Handle login redirection to /oauth2/authorization/google or /oauth2/authorization/github

This design ensures that React or Thymeleaf can be swapped without backend changes.

🧱 Future Enhancements
Feature	Description
🌐 Full React Frontend	Replace Thymeleaf with a React SPA consuming REST endpoints
🗄️ Switch to MySQL	Persist data beyond dev sessions
🧠 Add REST /api/profile routes	Already compatible with your domain model
🎨 Enhanced UI	Use Tailwind or Bootstrap
🧱 Role-based Access	Add ADMIN / USER privileges
👨‍💻 Author

Shayne Angus
Cebu City, Philippines 🇵🇭
Built as part of a learning project integrating Spring Boot, OAuth2, and secure profile management.

🏁 License

This project is licensed under the MIT License.
