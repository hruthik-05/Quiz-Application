# ProjectQuiz — Online Quiz & Contest Platform

> A full-stack assessment platform built with **Spring Boot, React, MongoDB, Redis, and Spring Security**, supporting practice quizzes, adaptive quizzes, timed contests, role-based administration, analytics, Google OAuth2, CSRF protection, caching, and email notifications.

---

## 📌 Overview

**ProjectQuiz** is a full-stack web application for online assessments and competitive quizzes.

The platform provides two main experiences:

- **Students** can practice quizzes, choose subjects and difficulty levels, take adaptive quizzes, participate in scheduled contests, submit answers, view results, update their profiles, and track performance.
- **Administrators** can manage the question bank, create and manage contests, inspect contest submissions, and view platform-wide analytics.

The backend is implemented using a layered **Spring Boot** architecture with controllers, services, repositories, models, security components, and configuration modules. **MongoDB** is used as the primary database and **Redis** is used for application caching.

---

## ✨ Features

### 🔐 Authentication & Security

- Session-based authentication with Spring Security
- Username/password registration and login
- BCrypt password hashing
- Google OAuth2 login
- Role-based authorization:
  - `ROLE_USER`
  - `ROLE_ADMIN`
- Protected REST endpoints
- Maximum of two concurrent sessions per user
- CSRF protection using a cookie-based token
- CORS configuration with credential support
- Security headers:
  - Content Security Policy
  - `X-Frame-Options`
  - `X-Content-Type-Options`
  - Referrer Policy
- Client-supplied `ADMIN` roles are rejected during registration
- User-specific authorization checks for analytics and contest history

> **Important:** Authentication is session-based. The application does **not** use JWT authentication.

---

# 🧑‍🎓 Student Features

## 📝 Practice Quizzes

Students can generate quizzes using:

- Random questions
- Subject/category
- Difficulty
- Mixed difficulty

The supported difficulty levels are:

```text
EASY
MEDIUM
HARD
```

The API returns `QuestionDto` objects to the quiz client, preventing the stored correct answer from being sent as part of the normal question payload.

---

## 🎯 Custom Quizzes

A student can configure a quiz using:

```text
Subject + Difficulty + Number of Questions
```

Example:

```text
Subject: Java
Difficulty: HARD
Questions: 20
```

The backend validates the requested question count and currently allows between **1 and 100 questions** per quiz request.

---

## 🧠 Adaptive Quizzes

ProjectQuiz includes an adaptive quiz service that uses a user's previous performance to adjust the difficulty distribution of a new quiz.

The adaptive engine calculates subject-wise accuracy and chooses a difficulty distribution accordingly.

### Accuracy ≥ 70%

```text
20% Easy
30% Medium
50% Hard
```

### Accuracy between 40% and 70%

```text
30% Easy
50% Medium
20% Hard
```

### Accuracy < 40%

```text
50% Easy
30% Medium
20% Hard
```

The service selects questions from the appropriate difficulty pools and randomizes the resulting question set.

---

## 📊 Quiz Evaluation

Practice quizzes support point-based evaluation.

For every submitted response, the backend:

1. Retrieves the corresponding question.
2. Validates that the question exists.
3. Compares the submitted answer with the stored answer.
4. Calculates the score.
5. Counts correct answers.
6. Counts wrong answers.
7. Tracks skipped answers.
8. Calculates time taken.
9. Returns the correct-answer mapping in the result.
10. Updates the user's performance statistics.

A result contains values such as:

```text
Total Score
Correct
Wrong
Skipped
Penalty
Time Taken
Correct Answers
```

---

## ⏱️ Time-Based Evaluation

ProjectQuiz also supports time-based evaluation.

When the configured time limit is exceeded, a penalty is calculated from the amount of time exceeded.

```text
Penalty = (Time Taken - Time Limit) / 10
```

The final score is protected from becoming negative:

```text
Final Score = max(Score - Penalty, 0)
```

The server also sanitizes client-supplied timing information before evaluation.

---

# 🏆 Contest System

ProjectQuiz provides a dedicated contest system for scheduled assessments.

Administrators can create contests with:

- Contest title
- Description
- Start time
- End time
- Contest questions
- Maximum attempts
- Negative marking configuration
- Negative marking factor
- Active state

---

## ⏰ Contest Scheduling

A participant cannot submit a contest before it starts.

The backend validates the submission time independently of the frontend.

A **5-minute submission grace period** is currently allowed after the configured contest end time.

```text
Contest End
     │
     └── 5 minute submission window
```

---

## 🔢 Attempt Limiting

Each contest can define a maximum number of attempts.

Before accepting a submission, the backend checks:

```text
Current Attempts >= Maximum Attempts
```

If the maximum is reached, the submission is rejected.

To provide database-level protection against duplicate attempt numbers, the application creates a unique compound MongoDB index:

```text
userId + contestId + attemptNumber
```

This provides an additional safeguard against concurrent submissions.

---

## ➖ Negative Marking

Contests can enable negative marking.

For a correct answer:

```text
Score += 1
```

For an incorrect answer:

```text
Score -= negativeMarkFactor
```

Negative marking is applied only when enabled for the contest.

---

## 🔒 Contest Question Protection

Contest questions are kept within the contest model while the contest is active.

After the contest ends, the application can publish the contest questions into the general question collection.

Correct answers are only exposed after the contest has ended when contest attempts/results are retrieved.

This helps prevent participants from obtaining correct answers while the contest is still running.

---

# 📈 Analytics & Performance Tracking

ProjectQuiz maintains user performance statistics.

The analytics system tracks information such as:

- Subject-wise accuracy
- Subject-wise attempts
- Quiz history
- Last update time
- User performance statistics
- Contest attempts

For quiz performance, accuracy is calculated from correct answers and attempted questions.

```text
Accuracy = Correct Answers / Attempted Questions × 100
```

The application also maintains recent quiz history for users.

---

## 👨‍💼 Admin Analytics

Administrators can access platform-level analytics such as:

- Total users
- Total questions
- Active contests
- Contest attempts
- User performance statistics

The frontend provides visual analytics using:

- Chart.js
- React Chart.js
- Recharts

---

# ⚡ Redis Caching

Redis is integrated through Spring Data Redis and Spring Cache.

The application caches frequently accessed contest and question-related data.

Current cache names include:

```text
contests
contests_all
contests_active
contest_questions
questions
```

The default Redis cache TTL is:

```text
10 minutes
```

Caching is combined with cache eviction when relevant data changes.

For example, creating a contest invalidates the relevant contest caches.

---

# 📧 Email Notifications

The application uses Spring Boot Mail and `JavaMailSender` to send result notifications.

Contest result emails can contain:

- Username
- Contest title
- Score
- Correct answers
- Wrong answers

Email sending is performed asynchronously using Spring's asynchronous execution support so that email delivery does not unnecessarily block the main request.

---

# 👨‍💼 Admin Module

Administrators have access to a separate admin experience.

## Question Bank Management

Admins can:

- View questions
- Add individual questions
- Add questions in bulk
- Update questions
- Delete questions
- Filter questions by category
- Filter questions by difficulty
- Retrieve questions by category and difficulty

A question contains information such as:

```text
Question
Options
Correct Answer
Category
Difficulty
Points
```

---

## 🏗️ Contest Management

Administrators can:

- Create contests
- View contests
- Manage contest questions
- View contest submissions
- View participant information
- View scores
- View correct answers after the contest ends

---

# 🖥️ Frontend

The frontend is built using:

- React 19
- Vite
- Tailwind CSS
- React Router
- Axios
- React Context API
- Chart.js
- React Chart.js
- Recharts

The application separates student and administrator experiences using:

```text
StudentLayout
AdminLayout
```

---

## 🧑‍🎓 Student Routes

Main student pages include:

```text
/login
/user/login
/register
/dashboard
/contests
/contest/:id
/quiz/setup/:mode
/quiz/play
/quiz/result
/profile
```

---

## 👨‍💼 Admin Routes

Main administrator pages include:

```text
/admin/login
/admin/dashboard
/admin/contest
/admin/contest-manager
/admin/contest-report/:id
/admin/analytics
```

---

# 🔄 Application Flow

A typical student quiz flow is:

```text
                ┌───────────────┐
                │     Login     │
                └───────┬───────┘
                        │
                        ▼
                ┌───────────────┐
                │   Dashboard   │
                └───────┬───────┘
                        │
              ┌─────────┴─────────┐
              │                   │
              ▼                   ▼
        Practice Quiz          Contest
              │                   │
              ▼                   ▼
        Quiz Configuration    Contest Player
              │                   │
              └─────────┬─────────┘
                        ▼
                  Answer Questions
                        │
                        ▼
                    Submission
                        │
                        ▼
                    Evaluation
                        │
                        ▼
                      Result
                        │
                        ▼
                    Analytics
```

---

# 🏛️ Backend Architecture

ProjectQuiz follows a layered architecture:

```text
                         React Frontend
                              │
                              │ HTTP / JSON
                              ▼
                     ┌──────────────────┐
                     │   Controllers    │
                     └────────┬─────────┘
                              │
                              ▼
                     ┌──────────────────┐
                     │     Services     │
                     │ Business Logic   │
                     └───────┬──────────┘
                             │
              ┌──────────────┼──────────────┐
              ▼              ▼              ▼
       ┌────────────┐  ┌────────────┐  ┌────────────┐
       │Repositories│  │   Redis    │  │   Email    │
       └─────┬──────┘  └────────────┘  └────────────┘
             │
             ▼
       ┌────────────┐
       │  MongoDB   │
       └────────────┘
```

---

# 📁 Project Structure

```text
projectdemo/
│
├── demo/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/projectquiz/demo/
│   │       │
│   │       ├── config/
│   │       │   ├── CacheConfig.java
│   │       │   ├── DataSeeder.java
│   │       │   └── GlobalExceptionHandler.java
│   │       │
│   │       ├── controllers/
│   │       │   ├── AdminController.java
│   │       │   ├── AnalyticsController.java
│   │       │   ├── AuthController.java
│   │       │   ├── ContestController.java
│   │       │   ├── EvaluateController.java
│   │       │   ├── QuestionController.java
│   │       │   ├── QuizController.java
│   │       │   └── UserController.java
│   │       │
│   │       ├── models/
│   │       │   ├── AdminAnalyticsDto.java
│   │       │   ├── Contest.java
│   │       │   ├── ContestAttempt.java
│   │       │   ├── ContestAttemptDto.java
│   │       │   ├── Difficulty.java
│   │       │   ├── Question.java
│   │       │   ├── QuestionDto.java
│   │       │   ├── QuizAttempt.java
│   │       │   ├── ResultDto.java
│   │       │   ├── Role.java
│   │       │   ├── User.java
│   │       │   ├── UserPerformanceStats.java
│   │       │   ├── UserPerformanceStatsDto.java
│   │       │   └── UserResponse.java
│   │       │
│   │       ├── repositories/
│   │       │   ├── ContestAttemptRepository.java
│   │       │   ├── ContestRepository.java
│   │       │   ├── QuestionRepository.java
│   │       │   ├── QuizAttemptRepository.java
│   │       │   ├── UserPerformanceStatsRepository.java
│   │       │   └── UserRepository.java
│   │       │
│   │       ├── security/
│   │       │   ├── CsrfCookieFilter.java
│   │       │   ├── WebSecurityConfig.java
│   │       │   ├── oauth2/
│   │       │   │   └── CustomOAuth2SuccessHandler.java
│   │       │   └── services/
│   │       │       ├── CustomOAuth2UserService.java
│   │       │       ├── UserDetailsImpl.java
│   │       │       └── UserDetailsServiceImpl.java
│   │       │
│   │       ├── services/
│   │       │   ├── AdaptiveQuizService.java
│   │       │   ├── AdminService.java
│   │       │   ├── AnalyticsService.java
│   │       │   ├── ContestService.java
│   │       │   ├── EmailService.java
│   │       │   ├── EvaluationService.java
│   │       │   ├── QuestionService.java
│   │       │   ├── QuizService.java
│   │       │   └── UserService.java
│   │       │
│   │       └── payload/
│   │           ├── request/
│   │           └── response/
│   │
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   │   ├── contexts/
│   │   │   └── AuthContext.jsx
│   │   ├── layouts/
│   │   │   ├── AdminLayout.jsx
│   │   │   └── StudentLayout.jsx
│   │   ├── pages/
│   │   ├── services/
│   │   │   └── api.js
│   │   ├── App.jsx
│   │   └── main.jsx
│   ├── package.json
│   ├── vite.config.js
│   └── tailwind.config.js
│
└── README.md
```

---

# 🧰 Tech Stack

## Backend

| Technology | Purpose |
|---|---|
| Java 17 | Backend programming language |
| Spring Boot 3.2.2 | Application framework |
| Spring Web | REST API development |
| Spring Security | Authentication and authorization |
| Spring Data MongoDB | MongoDB integration |
| Spring OAuth2 Client | Google OAuth2 login |
| Spring Boot Mail | Email notifications |
| Spring Data Redis | Redis integration |
| Spring Cache | Application caching |
| Lombok | Boilerplate reduction |
| Maven | Build and dependency management |

## Frontend

| Technology | Purpose |
|---|---|
| React 19.2 | User interface |
| Vite 5 | Development and build tooling |
| Tailwind CSS 4 | Styling |
| React Router DOM 7 | Client-side routing |
| Axios | HTTP communication |
| React Context API | Authentication state |
| Chart.js 4 | Charts |
| React Chart.js 2 | Chart.js React integration |
| Recharts 3 | Data visualization |

## Data & Infrastructure

| Technology | Purpose |
|---|---|
| MongoDB | Primary database |
| Redis | Caching |
| Google OAuth2 | Social authentication |
| Gmail SMTP | Email delivery |

---

# ⚙️ Prerequisites

Install the following before running the project:

- **Java JDK 17 or later**
- **Maven 3.8+**
- **Node.js**
- **npm**
- **MongoDB**
- **Redis**

Optional:

- Google OAuth2 credentials
- Gmail SMTP/App Password

---

# 🚀 Installation

## 1. Clone the repository

```bash
git clone <repository-url>
cd projectdemo
```

---

# 🔧 Backend Setup

Navigate to the backend:

```bash
cd demo
```

The backend runs on:

```text
http://localhost:8200
```

---

## 2. Configure MongoDB

The default MongoDB configuration is:

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/projectquiz
```

Make sure MongoDB is running before starting the backend.

---

## 3. Configure Redis

The default Redis configuration is:

```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.cache.type=redis
```

Start Redis before running the application.

For a local Redis installation:

```bash
redis-server
```

Verify it with:

```bash
redis-cli ping
```

Expected:

```text
PONG
```

---

# 🔑 Google OAuth2 Configuration

Create Google OAuth2 credentials and configure:

```properties
spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET
spring.security.oauth2.client.registration.google.scope=email,profile
```

The OAuth2 callback used by Spring Security is:

```text
http://localhost:8200/login/oauth2/code/google
```

Make sure this URL is configured as an authorized redirect URI in your Google OAuth client.

---

# 📧 Email Configuration

For Gmail SMTP:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=YOUR_GMAIL_USERNAME
spring.mail.password=YOUR_GMAIL_APP_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

Use a Gmail **App Password** rather than your normal Gmail password.

---

# 🌐 CORS Configuration

The default frontend URL is:

```properties
app.frontend.url=http://localhost:5173
```

Allowed origins can be configured using:

```properties
app.cors.allowed-origins=http://localhost:5173,http://localhost:3000,http://localhost:5174
```

The backend enables credentials because authentication uses HTTP sessions.

---

# ▶️ Run the Backend

From the `demo` directory:

```bash
mvn spring-boot:run
```

Or build the application:

```bash
mvn clean package
```

Then:

```bash
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

---

# 🎨 Frontend Setup

Open another terminal:

```bash
cd frontend
```

Install dependencies:

```bash
npm install
```

The frontend uses:

```text
http://localhost:5173
```

The API base URL defaults to:

```text
http://localhost:8200/api
```

It can be overridden using:

```env
VITE_API_BASE_URL=http://localhost:8200/api
```

Start the development server:

```bash
npm run dev
```

---

# 🗄️ MongoDB Data Model

The major MongoDB entities are:

### Users

Stores:

- Username
- Email
- Password hash
- Roles
- Authentication provider
- Profile information

---

### Questions

Stores:

- Question ID
- Question text
- Options
- Correct answer
- Category
- Difficulty
- Points

---

### Contests

Stores:

- Contest metadata
- Start time
- End time
- Active state
- Maximum attempts
- Negative marking settings
- Contest questions
- Question IDs

---

### Contest Attempts

Stores:

- User ID
- Contest ID
- Attempt number
- Responses
- Score
- Time taken
- Submission time
- Correct answers when available

---

### Quiz Attempts

Stores practice quiz attempt information.

---

### User Performance Statistics

Stores:

- Subject-wise performance
- Accuracy
- Attempt counts
- Quiz history
- Last updated timestamp

---

# 🔌 API Reference

## Authentication

Base URL:

```text
/api/auth
```

| Method | Endpoint | Description |
|---|---|---|
| POST | `/signup` | Register a user |
| POST | `/signin` | Authenticate a user |
| GET | `/me` | Get the currently authenticated user |
| POST | `/signout` | End the current session |

---

# 📝 Quiz APIs

Base URL:

```text
/api/quiz
```

| Method | Endpoint | Description |
|---|---|---|
| GET | `/create/{numberOfQuestions}` | Generate a random quiz |
| GET | `/createsubject/{subject}/{numberOfQuestions}` | Generate a subject quiz |
| GET | `/custom/{subject}/{difficulty}/{numberOfQuestions}` | Generate a custom difficulty quiz |
| GET | `/adaptive/{userId}/{subject}/{numberOfQuestions}` | Generate an adaptive quiz |
| POST | `/submit` | Submit a quiz |

The quiz endpoints return question DTOs rather than exposing the stored answer directly.

---

# 📊 Evaluation APIs

Base URL:

```text
/evaluate
```

| Method | Endpoint | Description |
|---|---|---|
| POST | `/pointsBasedEval` | Evaluate a point-based quiz |
| POST | `/timeBasedEval` | Evaluate a time-based quiz |

---

# ❓ Question APIs

Base URL:

```text
/api/question
```

| Method | Endpoint | Description |
|---|---|---|
| GET | `/allQuestions` | Retrieve all questions as DTOs |
| POST | `/addQuestion` | Add a question |
| DELETE | `/deleteQuestion/{id}` | Delete a question |
| PUT | `/updateQuestion` | Update a question |
| GET | `/getQuestionsByCategory/{category}` | Filter by category |
| GET | `/getQuestionById/{id}` | Retrieve a question |
| GET | `/getQuestionsByDifficulty/{difficulty}` | Filter by difficulty |
| GET | `/{category}/{difficulty}` | Filter by category and difficulty |
| POST | `/getQuestionsByListOfIds` | Retrieve questions by IDs |
| POST | `/bulk` | Add questions in bulk |

Administrative question-management endpoints are protected by the `ADMIN` role.

---

# 🏆 Contest APIs

Base URL:

```text
/api/contest
```

| Method | Endpoint | Description |
|---|---|---|
| POST | `/create` | Create a contest |
| GET | `/all` | Retrieve all contests |
| GET | `/{id}` | Retrieve a contest |
| GET | `/{id}/questions` | Retrieve contest questions |
| POST | `/submit` | Submit a contest |
| GET | `/{id}/attempts` | Retrieve contest attempts |
| GET | `/my-results/{userId}` | Retrieve a user's contest results |

Contest creation and contest-attempt reporting require administrator authorization.

Contest submission requires the `USER` role.

---

# 📈 Analytics APIs

Base URL:

```text
/api/analytics
```

| Method | Endpoint | Description |
|---|---|---|
| GET | `/{userId}` | Retrieve user performance statistics |
| GET | `/admin/stats` | Retrieve platform statistics |
| GET | `/admin/all-user-stats` | Retrieve all user statistics |
| GET | `/admin/all-contest-attempts` | Retrieve contest attempt analytics |

Users can access their own statistics, while administrators can access statistics for other users.

---

# 👤 User APIs

Base URL:

```text
/api/user
```

| Method | Endpoint | Description |
|---|---|---|
| PUT | `/profile` | Update the authenticated user's profile |

---

# 🔐 Authentication Flow

ProjectQuiz uses Spring Security's session-based security context.

```text
React Client
     │
     │ POST /api/auth/signin
     ▼
AuthenticationManager
     │
     ▼
UserDetailsService
     │
     ▼
BCrypt Password Verification
     │
     ▼
Authentication
     │
     ▼
SecurityContext
     │
     ▼
HTTP Session
     │
     ▼
Authenticated Requests
```

The frontend sends requests with credentials enabled:

```javascript
withCredentials: true
```

The browser therefore sends the authentication session cookie with API requests.

---

# 🔵 Google OAuth2 Flow

```text
User
 │
 ▼
Google Login
 │
 ▼
Google OAuth2 Provider
 │
 ▼
CustomOAuth2UserService
 │
 ├── Existing account
 │
 └── New account
 │
 ▼
CustomOAuth2SuccessHandler
 │
 ▼
Spring Security Session
 │
 ▼
React Application
```

New OAuth users are handled by the custom OAuth2 user service.

---

# 🛡️ CSRF Protection

Because the application uses session-based authentication, CSRF protection is enabled.

The backend uses:

```text
CookieCsrfTokenRepository
```

The CSRF token is exposed through a cookie and the frontend reads it before making requests.

The Axios client is configured with:

```javascript
withCredentials: true
```

and sends:

```text
X-XSRF-TOKEN
```

when the CSRF cookie is available.

---

# 🔒 Authorization Model

The application has two main roles:

```text
ROLE_USER
ROLE_ADMIN
```

General authenticated APIs require a valid session.

Administrative endpoints are protected using Spring Security role checks such as:

```java
@PreAuthorize("hasRole('ADMIN')")
```

The application also performs object-level checks for resources such as:

- User analytics
- Contest history

A user cannot request another user's private analytics unless the authenticated account has administrator privileges.

---

# ⚡ Caching Strategy

Spring Cache is backed by Redis.

The configured default TTL is:

```text
10 minutes
```

Frequently accessed contest information is cached using keys such as:

```text
contests_all
contests_active
contests
contest_questions
```

Cache eviction is performed when data changes.

For example:

```text
Create Contest
     │
     ▼
Save to MongoDB
     │
     ▼
Evict Contest Caches
     │
     ▼
Next Read → MongoDB
     │
     ▼
Store Fresh Data in Redis
```

---

# 🧵 Concurrent Contest Submissions

Contest attempts use both application-level and database-level protection.

The application first checks the current number of attempts:

```text
countByUserIdAndContestId(...)
```

It then assigns:

```text
attemptNumber = attemptsCount + 1
```

The database also enforces uniqueness using:

```text
(userId, contestId, attemptNumber)
```

This prevents duplicate attempt records when concurrent requests attempt to create the same attempt number.

---

# 📧 Result Notification Flow

After a contest submission:

```text
Contest Submission
       │
       ▼
Validate Contest
       │
       ▼
Validate Attempt Limit
       │
       ▼
Validate Time Window
       │
       ▼
Evaluate Answers
       │
       ▼
Save Contest Attempt
       │
       ▼
Send Result Email
```

The email contains the participant's contest result information.

---

# 🧪 Development Commands

## Backend

Run:

```bash
mvn spring-boot:run
```

Build:

```bash
mvn clean package
```

Skip tests:

```bash
mvn clean package -DskipTests
```

Run packaged application:

```bash
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

---

## Frontend

Install dependencies:

```bash
npm install
```

Development:

```bash
npm run dev
```

Lint:

```bash
npm run lint
```

Production build:

```bash
npm run build
```

Preview production build:

```bash
npm run preview
```

---

# 🚢 Production Build

## Backend

```bash
cd demo
mvn clean package -DskipTests
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

## Frontend

```bash
cd frontend
npm run build
```

The optimized frontend files will be generated in:

```text
frontend/dist/
```

---

# 🔧 Environment Configuration

For a production deployment, do not commit credentials to Git.

Recommended configuration:

```properties
spring.data.mongodb.uri=${MONGODB_URI}

spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID}
spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET}

spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}

spring.data.redis.host=${REDIS_HOST}
spring.data.redis.port=${REDIS_PORT}

app.frontend.url=${FRONTEND_URL}
app.backend.url=${BACKEND_URL}
app.cors.allowed-origins=${CORS_ALLOWED_ORIGINS}
```

Use environment variables or a dedicated secret-management solution for production credentials.

---

# 🐛 Troubleshooting

## MongoDB Connection Error

Verify that MongoDB is running:

```bash
sudo systemctl status mongod
```

Or start it:

```bash
sudo systemctl start mongod
```

Test the configured MongoDB URI:

```text
mongodb://localhost:27017/projectquiz
```

---

## Redis Connection Error

Check Redis:

```bash
redis-cli ping
```

Expected:

```text
PONG
```

If Redis is not running:

```bash
redis-server
```

---

## Port 8200 Already in Use

Check the process:

```bash
sudo lsof -i :8200
```

On Linux, the process can be stopped with:

```bash
fuser -k 8200/tcp
```

---

## Port 5173 Already in Use

Check the process:

```bash
sudo lsof -i :5173
```

Or let Vite choose another available port.

If the frontend moves to another port, update:

```properties
app.frontend.url=http://localhost:<PORT>
```

and:

```properties
app.cors.allowed-origins=http://localhost:<PORT>
```

---

## CORS Problems

Verify that the frontend origin is included in:

```properties
app.cors.allowed-origins=http://localhost:5173
```

Also make sure the frontend Axios client uses:

```javascript
withCredentials: true
```

Because authentication relies on HTTP sessions.

---

## Google OAuth2 Login Problems

Verify:

1. Google Client ID is correct.
2. Google Client Secret is correct.
3. OAuth redirect URI is configured correctly.
4. The backend is running on the expected port.
5. The frontend URL matches the configured application URL.

Default callback:

```text
http://localhost:8200/login/oauth2/code/google
```

---

# 📚 Important Design Decisions

## Why MongoDB?

MongoDB provides flexible document storage for entities containing structures such as:

- Question options
- Contest question lists
- User responses
- Performance statistics
- Quiz history

This makes it convenient for the assessment domain.

---

## Why Redis?

Contest lists, contest questions, and other frequently requested resources can be read repeatedly.

Redis reduces repeated database reads and improves response time for frequently accessed data.

---

## Why Session-Based Authentication?

The application uses Spring Security's HTTP session model, allowing authentication state to be maintained server-side.

This also works naturally with:

- CSRF protection
- Secure session cookies
- Spring Security's `SecurityContext`
- OAuth2 login

---

## Why DTOs for Questions?

The normal question API converts:

```text
Question
```

into:

```text
QuestionDto
```

The DTO contains the question and its options but does not expose the stored correct answer during normal quiz delivery.

This prevents the client from receiving the answer before submission.

---

# 📊 High-Level System Components

```text
┌─────────────────────────────────────────────────────────────┐
│                        React Frontend                       │
│                                                             │
│  Student UI                 Admin UI                        │
│  ├─ Dashboard               ├─ Dashboard                   │
│  ├─ Quiz Setup              ├─ Contest Management          │
│  ├─ Quiz Player             ├─ Contest Reports             │
│  ├─ Contest Player          └─ Analytics                   │
│  ├─ Results                                                │
│  └─ Profile                                                │
└─────────────────────────────┬───────────────────────────────┘
                              │
                              │ HTTP / JSON
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     Spring Boot Backend                     │
│                                                             │
│  Controllers → Services → Repositories                      │
│       │           │             │                           │
│       │           │             ▼                           │
│       │           │         MongoDB                         │
│       │           │                                         │
│       │           └──────────► Redis                        │
│       │                                                     │
│       └──────────────────────► Spring Security              │
│                                ├─ Sessions                  │
│                                ├─ OAuth2                     │
│                                ├─ CSRF                       │
│                                ├─ CORS                       │
│                                └─ RBAC                       │
│                                                             │
│                         Email Service                       │
└─────────────────────────────────────────────────────────────┘
```

---

# 🚀 Future Improvements

Potential improvements for future versions include:

- Automated unit and integration test coverage
- End-to-end frontend testing
- Server-side pagination for large question/attempt datasets
- Centralized API error response format
- Rate limiting for authentication and contest submission endpoints
- Stronger anti-cheating controls
- Improved adaptive question selection
- More advanced leaderboard functionality
- Real-time contest status updates
- WebSocket-based live rankings
- Detailed question-level analytics
- Audit logging
- Production secret management
- Docker-based deployment
- CI/CD automation
- Cloud deployment
- Better monitoring and observability

---

# 🎯 Learning Outcomes

This project demonstrates practical implementation of:

- Full-stack web application development
- Spring Boot REST APIs
- Layered backend architecture
- Spring Security
- Session-based authentication
- Google OAuth2
- Role-based authorization
- CSRF protection
- CORS configuration
- BCrypt password hashing
- MongoDB data modeling
- MongoDB indexing
- Redis caching
- Cache eviction
- Quiz generation algorithms
- Adaptive quiz logic
- Contest scheduling
- Contest evaluation
- Negative marking
- Concurrent submission protection
- User analytics
- Asynchronous email processing
- React routing
- React Context API
- Axios API integration
- Data visualization
- Responsive frontend development

---

# 📌 Project Highlights

ProjectQuiz is designed as more than a basic CRUD quiz application.

It combines:

```text
Authentication
      +
Role-Based Authorization
      +
Practice Quizzes
      +
Adaptive Quizzes
      +
Contest Management
      +
Timed Evaluation
      +
Negative Marking
      +
Performance Analytics
      +
Redis Caching
      +
Google OAuth2
      +
CSRF Protection
      +
Email Notifications
      +
MongoDB
      +
React
```

into a single full-stack online assessment platform.

---

# 📄 License

This project is intended for educational, learning, and portfolio purposes.

---

# 👨‍💻 Author

**Hruthik**

B.Tech Computer Science Student

GitHub: `hruthik-05`

---

## ⭐ Project

If you find ProjectQuiz useful, consider starring the repository and exploring the codebase to extend the platform with additional assessment, analytics, and contest capabilities.