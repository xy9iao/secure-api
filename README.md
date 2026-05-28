# Secure API JWT Demo

This project is a Java Spring Boot demo application that simulates secure client-server communication through RESTful APIs using JWT authentication.

It demonstrates how a client logs in with a username and password, how the backend checks user information from a MySQL database, how the server generates a JWT token, and how protected APIs use that JWT token for authentication and role-based authorization.

## Features

- RESTful API login endpoint
- MySQL database user table
- JWT token generation
- JWT token validation
- Spring Security filter chain
- Public API access without login
- User-level protected API access
- Admin-only API access
- Simple frontend dashboard

## Tech Stack

- Java
- Spring Boot
- Spring Web
- Spring Security
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
spring.application.name=secure_api

spring.datasource.url=jdbc:mysql://localhost:3306/secure_api_db
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

The database username and password are read from environment variables or you can directly set in the configuration file.

Do not commit your real MySQL password to GitHub.

## Run the Application

From the project root, set your MySQL username and password or set in the configuration file.

PowerShell:

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_mysql_password"
```

Run:

```powershell
.\mvnw spring-boot:run
```

The application will start at (browser):

```text
http://localhost:8080
```

## Expected Access Behavior

| Login Status  | Public API | User API         | Admin API        |
| ------------- | ---------- | ---------------- | ---------------- |
| Not logged in | 200 OK     | 401 Unauthorized | 401 Unauthorized |
| alice / USER  | 200 OK     | 200 OK           | 401 Unauthorized |
| admin / ADMIN | 200 OK     | 200 OK           | 200 OK           |



## Request Flow

### Login Flow

```text
Frontend
↓
POST /api/auth/login
↓
AuthController
↓
AuthService
↓
UserRepository
↓
MySQL users table
↓
JwtService
↓
Return JWT to frontend
```

### Protected API Flow

```text
Frontend sends request with JWT
↓
JwtAuthenticationFilter checks Authorization header
↓
JwtService validates token
↓
Spring Security stores authenticated user
↓
SecurityConfig checks access rules
↓
Controller returns response
```
