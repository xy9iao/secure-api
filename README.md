# Secure API JWT Demo

Basic Spring Boot demo for JWT login.

## Features

- A login page sends username and password to the backend.
- The backend checks the user in MySQL.
- The backend generates a JWT after successful login.
- The JWT is saved to `generated-jwts.txt` for students to copy into jwt.io.
- The browser stores the JWT and uses it to call the protected products API.
- Admin users can see all products. Normal users see only their own products.

## Main Files

```text
src/main/java/sg/edu/nus/secure_api/controller/AuthController.java
src/main/java/sg/edu/nus/secure_api/controller/ProductController.java
src/main/java/sg/edu/nus/secure_api/security/JwtAuthenticationFilter.java
src/main/java/sg/edu/nus/secure_api/service/AuthService.java
src/main/java/sg/edu/nus/secure_api/service/JwtService.java
src/main/resources/static/index.html
src/main/resources/static/product.html
src/main/resources/static/login-failure.html
src/main/resources/static/app.js
database/init.sql
```

## Database Setup

Open MySQL Workbench and run:

```text
database/init.sql
```

This creates:

- `profiles`
- `products`

## Run

Set environment variables in PowerShell:

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="YOUR_PASSWORD"
$env:JWT_SECRET="use-a-long-demo-secret-key-at-least-32-characters"
```

Start the app:

```powershell
.\mvnw.cmd spring-boot:run
```

Open:

```text
http://localhost:8080
```

## JWT Output File

After each successful login, the server writes the generated JWT to:

```text
generated-jwts.txt
```

The file keeps only the latest tokens. Change this number in:

```properties
jwt.output-max-entries=5
```
