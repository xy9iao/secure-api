# Secure API JWT Demo

This project is a Java Spring Boot demo application that simulates secure client-server communication through RESTful APIs using JWT authentication.

It demonstrates how a client logs in with a username and password, how the backend checks user information from a MySQL database, how the server generates a JWT token, and how protected APIs use that JWT token for authentication and role-based authorization.

## Features

- RESTful API login endpoint
- MySQL database user table
- JWT token generation
- JWT token validation
- Custom JWT request filter
- Public API access without login
- User-level protected API access
- Admin-only API access
- Simple frontend dashboard

## Tech Stack

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- MySQL
- JWT
- Maven
- HTML / CSS / JavaScript

## Database Setup

This project uses a local MySQL database.

Students who clone this repo need to create their own local database. The database is not included in GitHub.

### Step 1: Install MySQL

Install MySQL and MySQL Workbench on your computer.

### Step 2: Run the database script

Open MySQL Workbench and run the script saved in:

```text
database/init.sql
```

## Application Configuration

Open:

`src/main/resources/application.properties`

Use this configuration:

```properties
spring.application.name=secure-api

spring.datasource.url=jdbc:mysql://localhost:3306/secure_api_db
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

jwt.secret=${JWT_SECRET}
jwt.expiration-minutes=30
```

The database username, database password, and JWT secret are read from environment variables.

Do not commit your real MySQL password or JWT secret to GitHub.

## Run the Application

From the project root, set your MySQL username, MySQL password, and JWT secret.

PowerShell:

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_mysql_password"
$env:JWT_SECRET="use-a-long-demo-secret-key-at-least-32-characters"
```

Run:

```powershell
.\mvnw spring-boot:run
```

The application will start at:

```text
http://localhost:8080
```

## Expected Access Behavior

| Login Status  | Public API | User API         | Admin API        |
| ------------- | ---------- | ---------------- | ---------------- |
| Not logged in | 200 OK     | 401 Unauthorized | 401 Unauthorized |
| user / USER   | 200 OK     | 200 OK           | 403 Forbidden    |
| admin / ADMIN | 200 OK     | 200 OK           | 200 OK           |

## Request Flow

### Login Flow

```text
Frontend
to
POST /api/auth/login
to
AuthController
to
AuthService
to
UserRepository
to
MySQL users table
to
JwtService
to
Return JWT to frontend
```

### Protected API Flow

```text
Frontend sends request with JWT
to
JwtAuthenticationFilter checks Authorization header
to
JwtService validates token
to
Filter stores username and role on the request
to
Filter checks whether the endpoint requires USER or ADMIN access
to
Controller returns response
```
