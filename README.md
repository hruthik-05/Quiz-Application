# 🎯 ProjectQuiz - Advanced Quiz & Contest Platform

A production-ready full-stack Quiz and Contest Management Platform built using **Spring Boot**, **React**, **MongoDB**, **Redis**, and **Spring Security**.

ProjectQuiz enables administrators to create and manage quizzes, contests, and question banks while allowing users to participate in contests, track performance, and analyze results through an intuitive dashboard.

The application uses **Spring Security's Session-Based Authentication**, **Google OAuth 2.0 (Authorization Code Flow)**, **Redis Caching**, and **Role-Based Access Control (RBAC)** to provide a secure and scalable platform.

## 🚀 Overview
**ProjectQuiz** is a comprehensive, full-stack web application designed to facilitate online assessments, coding contests, and skill evaluations. It features a robust **Spring Boot** backend and a modern **React** frontend, offering a seamless experience for both administrators and participants.

The platform supports:
*   **Role-Based Access Control (RBAC):** Distinct portals for Admins and Users.
*   **Contest Management:** Create, schedule, and manage timed contests.
*   **Real-time Evaluation:** Instant feedback on quiz submissions.
*   **Detailed Analytics:** Visual insights into user performance using Chart.js.
*   **Adaptive Testing:** (Future Scope) Questions that adapt to user skill level.

## 🛠️ Tech Stack

### Backend (Server)
*   **Language:** Java 17
*   **Framework:** Spring Boot 3.2.2
    *   *Spring Security* (Authentication & Authorization)
    *   *Spring Data MongoDB* (Database Interaction)
    *   *Spring Boot Starter Mail* (Email Notifications)
*   **Database:** MongoDB
*   **Authentication:** Session-based (Spring Security + Google OAuth2)
*   **Cache & Session Store:** Redis
*   **Build Tool:** Maven

### Frontend (Client)
*   **Library:** React.js 18
*   **Build Tool:** Vite
*   **Styling:** Tailwind CSS
*   **State Management:** React Context API
*   **Routing:** React Router DOM
*   **HTTP Client:** Axios
*   **Charts:** Chart.js / React-Chartjs-2

## 🔐 Authentication & Security
* **Secure Sign-Up/Login:** Session-based stateful authentication using Spring Security session management.
* **Google OAuth2 Integration:** Single sign-on using Google login.
* **Caches & State:** Redis integration backing Spring Cache annotations (`@Cacheable`/`@CacheEvict`).
* **Role Management:** Automatic role assignment (Admin vs. User).
* **Protected Routes:** Frontend route guards to prevent unauthorized access.---

# 🚀 Features


- Spring Security Session-Based Authentication
- Google OAuth 2.0 (Authorization Code Flow)
- Email & Password Login
- Role-Based Authorization (Admin & User)
- Secure HttpSession Management
- Protected Routes
- BCrypt Password Encryption
- CSRF Protection
- CORS Configuration
- Security Headers

---

## 👨‍💼 Admin Module

- Dashboard Overview
- User Management
- Question Bank Management
- Create & Schedule Contests
- Batch Question Upload
- Edit/Delete Questions
- Contest Analytics
- Leaderboard Monitoring
- User Performance Reports

---

## 👨‍🎓 User Module

- Register & Login
- Google Sign-In
- Update Profile
- Participate in Contests
- Practice Quizzes
- View Contest Results
- Attempt History
- Performance Analytics

---

## 📝 Contest Features

- Timed Contests
- Multiple Categories
- Difficulty Levels
- Automatic Evaluation
- Leaderboards
- Contest History
- Detailed Result Analysis

---

## 📊 Analytics

- User Performance Charts
- Contest Statistics
- Category-wise Analysis
- Score Distribution
- Attempt History

---

# 🛠 Tech Stack

## Backend

- Java 17
- Spring Boot 3
- Spring Security
- Spring OAuth2 Client
- Spring Data MongoDB
- Spring Cache
- Redis
- MongoDB
- Maven
- JavaMail

---

## Frontend

- React 18
- Vite
- Tailwind CSS
- React Router DOM
- React Context API
- Axios
- Chart.js

---

## Database

MongoDB Collections

- Users
- Questions
- Contests
- Results
- Responses

---

## Cache

Redis

Used For

- Leaderboard Caching
- Contest Details Caching
- Frequently Accessed Quiz Data
- User Statistics
- Performance Optimization

---

# 🏛️ Project Architecture

```text
                React Frontend
                        │
                        ▼
                 REST API Requests
                        │
                        ▼
          Spring Boot REST Controllers
                        │
                        ▼
                 Service Layer
            ┌───────────┴───────────┐
            ▼                       ▼
       MongoDB                  Redis Cache
            │
            ▼
      Spring Security
            │
            ▼
 SecurityContext + HttpSession
            │
            ▼
 Google OAuth 2.0 (Authorization Code Flow)
```

---

# 🔐 Authentication Architecture

The project uses **Spring Security Session-Based Authentication**.

### Supported Login Methods

- Email & Password
- Google OAuth 2.0

### Authentication Flow

```text
User
        │
        ▼
Login / Continue with Google
        │
        ▼
AuthenticationManager / OAuth2Login
        │
        ▼
Spring Security
        │
        ▼
SecurityContext
        │
        ▼
HttpSession
        │
        ▼
JSESSIONID Cookie
        │
        ▼
Protected Resources
```

**No JWT tokens are used. Authentication is maintained using HttpSession and JSESSIONID cookies.**

---

# 📂 Project Structure

```text
ProjectQuiz
│
├── demo/                  # Spring Boot Backend
├── frontend/              # React Frontend
└── README.md
```

---

# ⚙️ Installation

## Prerequisites

- Java 17+
- Node.js 18+
- MongoDB
- Redis

---

## Clone Repository

```bash
git clone https://github.com/hruthik-05/ProjectQuiz.git

cd ProjectQuiz
```

---

# Backend Setup

Navigate to the backend

```bash
cd demo
```

Copy the example configuration

```bash
cp src/main/resources/application.properties.example \
src/main/resources/application.properties
```

Configure

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/projectquiz

spring.security.oauth2.client.registration.google.client-id=YOUR_CLIENT_ID

spring.security.oauth2.client.registration.google.client-secret=YOUR_CLIENT_SECRET

spring.mail.username=YOUR_EMAIL

spring.mail.password=YOUR_APP_PASSWORD
```

Run the backend

```bash
mvn spring-boot:run
```

Backend URL

```
http://localhost:8200
```

---

# Frontend Setup

Navigate to the frontend

```bash
cd frontend
```

Install dependencies

```bash
npm install
```

Run the frontend

```bash
npm run dev
```

Frontend URL

```
http://localhost:5173
```

---

# Redis Setup

Install Redis (Ubuntu)

```bash
sudo apt update

sudo apt install redis-server

sudo systemctl start redis-server

sudo systemctl enable redis-server
```

Verify Redis

```bash
redis-cli ping
```

Expected Output

```
PONG
```

---

# REST API Modules

## Authentication

```
POST /api/auth/signup

POST /api/auth/login

GET /oauth2/authorization/google

POST /logout
```

---

## Admin

```
GET /api/admin/questions

POST /api/admin/questions

DELETE /api/admin/questions/{id}

POST /api/admin/contest
```

---

## Contest

```
GET /api/contest/all

POST /api/contest/create

POST /api/contest/submit
```

---

## User

```
PUT /api/user/profile

GET /api/user/history
```

---

# 🔒 Security Features

- Spring Security
- Session-Based Authentication
- Google OAuth 2.0 (Authorization Code Flow)
- Role-Based Access Control (RBAC)
- BCrypt Password Encoding
- CSRF Protection
- CORS Configuration
- HttpOnly Secure Cookies
- Security Headers
- HttpSession Management
- Redis Caching

---

# ⚡ Redis Caching

Redis is integrated to improve application performance by caching:

- Leaderboards
- Contest Details
- Frequently Accessed Quiz Data
- User Statistics

This reduces repeated database queries and improves response time.

---

# 🚀 Build

Backend

```bash
mvn clean package
```

Run

```bash
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

Frontend

```bash
npm run build
```

---

# 🔮 Future Enhancements

- Coding Compiler Integration
- AI-based Question Generation
- Adaptive Testing
- Email Notifications
- Certificate Generation
- Docker Support
- Kubernetes Deployment
- CI/CD Pipeline
- WebSocket-based Live Contest Updates

---

# 👨‍💻 Author

**Hruthik Ambati**

B.Tech Computer Science & Engineering

Spring Boot • React • MongoDB • Redis • Spring Security • Google OAuth 2.0

---

# 📄 License

This project is developed for educational, learning, and portfolio purposes.
