# Secure API JWT Demo

This project is a Java Spring Boot demo application that simulates secure client-server communication through RESTful APIs using JWT authentication.

The demo shows how a client logs in with a username and password, receives a JWT token from the server, and then uses that token to access protected API endpoints.

## Tech Stack

- Java
- Spring Boot
- Spring Web
- Spring Security
- JWT
- Maven

## Project Structure

```text
src/main/java/sg/edu/nus/secure_api
├── controller
│   ├── AuthController.java
│   └── SecureController.java
├── model
│   ├── LoginRequest.java
│   └── LoginResponse.java
├── security
│   ├── JwtAuthenticationFilter.java
│   └── SecurityConfig.java
├── service
│   ├── AuthService.java
│   └── JwtService.java
└── SecureApiApplication.java
Demo Account
username: alice
password: password123
Run the Application
.\mvnw spring-boot:run

Or, if Maven is installed globally:

mvn spring-boot:run

The application runs on:

http://localhost:8080
API Endpoints
1. Login
POST /api/auth/login

Request body:

{
  "username": "alice",
  "password": "password123"
}

Successful response:

{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
2. Protected API
GET /api/secure/hello

This endpoint requires a JWT token in the request header:

Authorization: Bearer <your_token>

Successful response:

Hello, this is a protected API. You can only see this with a valid JWT.

Without a valid JWT token, the request will return an unauthorized or forbidden response.

PowerShell Test Commands
Login and store token
$body = @{
    username = "alice"
    password = "password123"
} | ConvertTo-Json

$response = Invoke-RestMethod `
    -Uri "http://localhost:8080/api/auth/login" `
    -Method Post `
    -ContentType "application/json" `
    -Body $body

$token = $response.token
$token
Access protected API with token
Invoke-RestMethod `
    -Uri "http://localhost:8080/api/secure/hello" `
    -Method Get `
    -Headers @{ Authorization = "Bearer $token" }
Access protected API without token
Invoke-RestMethod `
    -Uri "http://localhost:8080/api/secure/hello" `
    -Method Get

Expected result: unauthorized or forbidden response.