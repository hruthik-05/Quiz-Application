# ProjectQuiz - Advanced Quiz Application



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
*   **Authentication:** JWT (JSON Web Tokens)
*   **Build Tool:** Maven

### Frontend (Client)
*   **Library:** React.js 18
*   **Build Tool:** Vite
*   **Styling:** Tailwind CSS
*   **State Management:** React Context API
*   **Routing:** React Router DOM
*   **HTTP Client:** Axios
*   **Charts:** Chart.js / React-Chartjs-2

---

## ✨ Key Features

### 🔐 Authentication & Security
*   **Secure Sign-Up/Login:** JWT-based stateless authentication.
*   **Role Management:** Automatic role assignment (Admin vs. User).
*   **Protected Routes:** Frontend route guards to prevent unauthorized access.

### 👨‍💼 Admin Module
*   **Dashboard Overview:** View total users, active contests, and question bank stats.
*   **Question Bank Management:**
    *   Add questions with categories (Java, Python, DSA, etc.) and difficulty levels (EASY, MEDIUM, HARD).
    *   Batch upload questions.
    *   Edit/Delete existing questions.
*   **Contest Creation:** Schedule specific contests with start/end times.
*   **Analytics:** View reports on user attempts and contest participation.

### 🧑‍💻 User Module
*   **Profile Management:** Update personal details.
*   **Take Quiz:** Participate in open contests or practice quizzes.
*   **Immediate Results:** View score, correct answers, and pass/fail status immediately after submission.
*   **History:** Track past attempts and performance trends.

---

## ⚙️ Setup & Installation

Follow these steps to get the project running on your local machine.

### Prerequisites
*   **Java JDK 17+** installed.
*   **Node.js (v18+) & npm** installed.
*   **MongoDB** installed and running locally on port `27017` (or use a cloud URI).

### 1. Clone the Repository
```bash
git clone <repository-url>
cd projectdemo
```

### 2. Backend Setup (`demo` directory)
The backend runs on port **8200**.

1.  Navigate to the backend folder:
    ```bash
    cd demo
    ```
2.  **Configure Environment Variables**:
    *   Duplicate the example properties file:
        ```bash
        cp src/main/resources/application.properties.example src/main/resources/application.properties
        ```
    *   Open `src/main/resources/application.properties` and configure:
        ```properties
        # MongoDB URI
        spring.data.mongodb.uri=mongodb://localhost:27017/projectquiz

        # Email Settings (for notifications)
        spring.mail.username=YOUR_GMAIL_USERNAME
        spring.mail.password=YOUR_APP_PASSWORD

        # Google OAuth (Optional)
        spring.security.oauth2.client.registration.google.client-id=YOUR_CLIENT_ID
        spring.security.oauth2.client.registration.google.client-secret=YOUR_CLIENT_SECRET
        ```
3.  **Run the Server**:
    ```bash
    mvn spring-boot:run
    ```

### 3. Frontend Setup (`frontend` directory)
The frontend runs on port **5173** (or 5174 if 5173 is busy).

1.  Navigate to the frontend folder:
    ```bash
    cd ../frontend
    ```
2.  **Configure Environment**:
    *   Duplicate the example env file:
        ```bash
        cp .env.example .env
        ```
    *   The `VITE_API_BASE_URL` should match your backend URL (default: `http://localhost:8200/api`).
3.  **Install Dependencies & Run**:
    ```bash
    npm install
    npm run dev
    ```

---

## 📚 API Reference

### Authentication (`/api/auth`)
*   `POST /signup` - Register a new user.
*   `POST /signin` - Login and receive JWT.

### Admin (`/api/admin`)
*   `GET /questions` - Get all questions.
*   `POST /questions` - Add a single question.
*   `POST /questions/batch` - Add multiple questions.
*   `DELETE /questions/{id}` - Delete a question.

### Contests (`/api/contest`)
*   `POST /create` - Create a new contest.
*   `GET /all` - List all contests.
*   `GET /{id}/questions` - Get questions for a specific contest.
*   `POST /submit` - Submit contest answers.

### Users (`/api/user`)
*   `PUT /profile` - Update user profile.
*   `GET /api/contest/my-results/{userId}` - Get user's contest history.

---

## 🗄️ Database Schema (MongoDB)

*   **Users Collection**: Stores user credentials, roles (`ROLE_USER`, `ROLE_ADMIN`), and profile info.
*   **Questions Collection**: Stores questions, options, correct answer, category, and difficulty.
*   **Contests Collection**: Stores contest metadata (title, start/end time) and list of question IDs.
*   **Results Collection**: Stores user attempts, scores, and detailed answers for review.

---

## 🚀 Deployment

### Backend
Build the JAR file:
```bash
cd demo
mvn clean package -DskipTests
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

### Frontend
Build static files for production:
```bash
cd frontend
npm run build
# The 'dist' folder now contains the optimized build.
```

---

## ❓ Troubleshooting

### Port 8200/5173 Already in Use
If you see "Port already in use", kill the existing process:
```bash
# Linux/Mac
fuser -k 8200/tcp
fuser -k 5173/tcp
```

### CORS Errors
If you see generic "Network Error" or "401 Unauthorized" immediately:
1.  Check `WebSecurityConfig.java` in backend to ensure `allowedOrigins` includes your frontend URL (e.g., `http://localhost:5174`).
2.  Ensure you are sending the `Authorization: Bearer <token>` header (handled automatically by `api.js`).

---


