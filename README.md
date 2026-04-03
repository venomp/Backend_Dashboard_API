# Finance Dashboard API – Complete Documentation

## 📌 Overview

The **Finance Dashboard API** is a Spring Boot–based backend system designed to manage user authentication and financial transactions (income/expenses). It provides secure APIs for registering users, logging in, and managing financial records.

---

# 🏗️ Project Architecture

The application follows a **layered architecture**:

```
Controller → Service → Repository → Database
                ↓
             Security (JWT)
```

---

# 📁 Project Structure

```
com.finance.dashboard
│
├── controller        → REST API endpoints
├── service           → Business logic
├── repository        → Database interaction (JPA)
├── entity            → Database mappings
├── dto               → Request/Response objects
├── security          → JWT & authentication filters
├── exception         → Global error handling
└── config            → Security & bean configurations
```

---

# 🔐 Authentication Flow (JWT-based)

### 1. Register

* User sends details
* Password is **hashed using BCrypt**
* User stored in DB

### 2. Login

* Email + password validated
* JWT token generated

### 3. Access APIs

* Client sends:

```
Authorization: Bearer <JWT_TOKEN>
```

---

# 🧠 Layer-by-Layer Explanation

---

## 🎮 1. Controller Layer

Handles HTTP requests.

### Example:

```
POST /auth/register
POST /auth/login
```

Responsibilities:

* Accept request
* Validate input (`@Valid`)
* Call service
* Return response

---

## ⚙️ 2. Service Layer

Contains **business logic**.

### Example:

* Check if email exists
* Encode password
* Validate login credentials
* Generate JWT

---

## 🗄️ 3. Repository Layer

Uses **Spring Data JPA**.

### Example:

```java
Optional<UserEntity> findByEmail(String email);
boolean existsByEmail(String email);
```

---

## 🧩 4. Entity Layer

Maps Java objects to database tables.

### Example:

```java
@Entity
@Table(name = "financial_records")
```

Handles:

* Column mapping
* Relationships (`@ManyToOne`)
* Constraints

---

## 📦 5. DTO Layer

Used for API communication.

### Example:

* `RegisterRequest`
* `LoginRequest`
* `AuthResponse`

Purpose:

* Avoid exposing internal entities
* Validate input

---

## 🔒 6. Security Layer

Components:

* JWT Service → token generation/validation
* Authentication Filter → intercept requests
* PasswordEncoder → BCrypt hashing

---

## ⚠️ 7. Exception Handling

Global exception handler:

* Handles validation errors
* Handles custom exceptions
* Prevents stack trace exposure

---

# 🗄️ Database Design

## Database: `finance_db`

---

## 👤 Table: `users`

| Column       | Type      | Description              |
| ------------ | --------- | ------------------------ |
| id           | BIGSERIAL | Primary key              |
| email        | VARCHAR   | Unique login             |
| password     | VARCHAR   | BCrypt hash              |
| full_name    | VARCHAR   | User name                |
| phone_number | VARCHAR   | Validated                |
| role         | VARCHAR   | ADMIN / ANALYST / VIEWER |
| active       | BOOLEAN   | Status                   |
| created_at   | TIMESTAMP | Created time             |

---

## 💰 Table: `financial_records`

| Column           | Type          | Description       |
| ---------------- | ------------- | ----------------- |
| id               | BIGSERIAL     | Primary key       |
| user_id          | BIGINT        | FK → users        |
| amount           | NUMERIC(19,4) | Transaction value |
| type             | VARCHAR       | INCOME / EXPENSE  |
| category         | VARCHAR       | Label             |
| transaction_date | TIMESTAMP     | Business date     |
| description      | TEXT          | Optional          |
| created_at       | TIMESTAMP     | Auto              |

---

## 🔗 Relationships

```
User (1) ──── (Many) FinancialRecords
```

---

## 📊 Views

### `v_user_balance`

* Total income
* Total expense
* Net balance per user

### `v_category_summary`

* Category-wise breakdown

---

# 🚀 API Endpoints

---

## 🔐 Auth APIs

### Register

```
POST /auth/register
```

#### Request:

```json
{
  "email": "user@test.com",
  "password": "Test@123",
  "fullName": "Test User",
  "phoneNumber": "9999999999"
}
```

---

### Login

```
POST /auth/login
```

#### Request:

```json
{
  "email": "user@test.com",
  "password": "Test@123"
}
```

#### Response:

```json
{
  "token": "jwt_token_here",
  "type": "Bearer",
  "userId": 1
}
```

---

## 💰 Financial APIs (example)

### Create record

```
POST /financial-records
```

```json
{
  "amount": 5000,
  "type": "INCOME",
  "category": "Salary",
  "transactionDate": "2026-04-02T10:00:00",
  "description": "Monthly salary"
}
```

---

# ⚙️ Application Configuration

### application.properties

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/finance_db
spring.datasource.username=postgres
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
```

---
#Spring Boot
```
Make sure you are in dashboard folder
mvn spring-boot:run
Or run DashboardApplication.java directly from compiler
```

# 🛠️ Database Setup

### Step 1: Create DB

```sql
CREATE DATABASE finance_db;
```

### Step 2: Run init script

```bash
psql -U postgres -d finance_db -f init.sql
```

---

# 🔐 Security Notes

* Passwords stored using **BCrypt**
* JWT used for stateless authentication
* Roles:

  * ADMIN
  * ANALYST
  * VIEWER

---

# 🧪 Testing

### Tool:

* Postman / cURL

### Steps:

1. Register user
2. Login → get token
3. Use token for secured APIs

---


# 🚧 Future Improvements


* Pagination for records
* Filtering & analytics APIs
* Flyway for DB migrations
* Unit & integration tests

---

# 📌 Summary

This project demonstrates:

* Clean Spring Boot architecture
* Secure authentication (JWT)

* Proper database design
* Scalable backend structure

---
<img width="1920" height="1080" alt="Screenshot from 2026-04-02 15-51-46" src="https://github.com/user-attachments/assets/b322e6c6-4f73-497d-8b00-8929b6eb5736" />
---
<img width="1920" height="1080" alt="Screenshot from 2026-04-02 15-47-53" src="https://github.com/user-attachments/assets/6683e170-ee33-42d9-90df-5c62de8492aa" />

<img width="1920" height="1080" alt="Screenshot from 2026-04-02 15-47-18" src="https://github.com/user-attachments/assets/7662d5f6-b8fa-4159-8ff5-9a542084a19c" />

<img width="1920" height="1080" alt="Screenshot from 2026-04-02 15-46-52" src="https://github.com/user-attachments/assets/c0f6fd02-eb86-4039-be7e-f825e4da7016" />
<img width="1920" height="1080" alt="Screenshot from 2026-04-02 12-49-49" src="https://github.com/user-attachments/assets/4ddc8cb5-2fbe-4d08-afc9-c94f5bf7bdc6" />

