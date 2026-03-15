# 💰 Expense Tracker

A full-stack personal finance application for tracking expenses, managing categories, and visualizing spending habits. Built as a structured learning project to practice modern full-stack development across the entire software lifecycle.

---

## 🧱 Tech Stack

| Layer | Technology |
|---|---|
| **Frontend** | React 18, TypeScript, Vite |
| **State / Data Fetching** | React Query, Axios |
| **Validation** | Zod |
| **Charts** | Chart.js / react-chartjs-2 |
| **Backend** | Java 17, Spring Boot |
| **Security** | Spring Security, JWT (jjwt), BCrypt |
| **Database** | PostgreSQL, Spring Data JPA / Hibernate |
| **Build Tool** | Maven |
| **Testing** | JUnit 5, Mockito, Vitest, React Testing Library |
| **Containerization** | Docker, Docker Compose |

---

## ✨ Features

- **Authentication** — Register and log in with JWT-based sessions
- **Transaction Management** — Add, edit, delete, and filter income/expense transactions
- **Category Management** — Create and manage custom spending categories per user
- **Dashboard** — Visual overview of spending with charts and monthly summaries
- **Analytics** — Category breakdowns, monthly trends, and comparison cards
- **Data Isolation** — All data is scoped per authenticated user
- **Security Hardening** — CORS configuration, security headers, rate limiting on login

---

## 🚀 Getting Started

**Prerequisites:** Java 17+, Maven 3.9+, Node.js 18+, PostgreSQL 15+

**1. Create the database**
```sql
CREATE DATABASE expense_tracker;
```

**2. Configure `src/main/resources/application.properties`**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/expense_tracker
spring.datasource.username=YOUR_DB_USER
spring.datasource.password=YOUR_DB_PASSWORD
spring.jpa.hibernate.ddl-auto=update
app.jwt.secret=YOUR_SECRET_KEY
```

**3. Run the backend** → `http://localhost:8080`
```bash
cd backend
mvn spring-boot:run
```

**4. Run the frontend** → `http://localhost:5173`
```bash
cd frontend
npm install
npm run dev
```

---

## 🐳 Docker

Once stable locally, the full stack can be launched with:

```bash
docker-compose up --build
```

This spins up the PostgreSQL database, Spring Boot backend (`localhost:8080`), and the React frontend via Nginx (`localhost:3000`).

---

## 📚 Learning Goals

This project was built to practice and solidify skills across the full stack:

- Designing a relational schema and working with JPA/Hibernate
- Building a secure REST API with Spring Boot and Spring Security
- Implementing JWT authentication end-to-end
- Connecting a React/TypeScript frontend to a live backend API
- Managing server state with React Query
- Writing unit and integration tests at every layer
- Containerizing a multi-service application with Docker

---

## 📄 License

This project is for educational purposes. Feel free to fork and build on it.
