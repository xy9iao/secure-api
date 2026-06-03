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

## Application Flow

1. The student opens the frontend login view: `src/main/resources/static/index.html`.
2. The login form uses `src/main/resources/static/app.js` to send the username and password to `POST /api/auth/login`.
3. The request reaches `src/main/java/sg/edu/nus/secure_api/controller/AuthController.java`.
4. `AuthController.java` calls `src/main/java/sg/edu/nus/secure_api/service/AuthService.java`.
5. `AuthService.java` checks the username and password using `src/main/java/sg/edu/nus/secure_api/repository/ProfileRepository.java`.
6. If the login is valid, `AuthService.java` calls `src/main/java/sg/edu/nus/secure_api/service/JwtService.java` to generate a JWT.
7. `AuthService.java` saves the generated JWT into `generated-jwts.txt`.
8. The backend returns the JWT to `app.js`.
9. `app.js` stores the JWT in the browser and sends the user to `src/main/resources/static/product.html`.
10. `product.html` uses `app.js` to call `GET /api/products` with the JWT in the `Authorization` header.
11. Before the request reaches the controller, `src/main/java/sg/edu/nus/secure_api/security/JwtAuthenticationFilter.java` checks whether the JWT is valid.
12. If the JWT is valid, the request reaches `src/main/java/sg/edu/nus/secure_api/controller/ProductController.java`.
13. `ProductController.java` reads products using `src/main/java/sg/edu/nus/secure_api/repository/ProductRepository.java`.
14. The product data is returned to `app.js`.
15. `app.js` displays the final frontend product view inside `product.html`.
16. If login fails, `app.js` sends the user to `src/main/resources/static/login-failure.html`.

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
