# Secure API JWT Demo

Basic Spring Boot demo for JWT login.

## Features

- A login page sends username and password to the backend.
- The backend checks the user in MySQL.
- The backend generates a JWT after successful login.
- The JWT is saved to `generated-jwts.txt` for students to copy into jwt.io.
- The backend stores the JWT in an `HttpOnly` cookie for the browser.
- Thymeleaf renders the login, failure, and product pages on the server.
- Admin users can see all products. Normal users see only their own products.
- The frontend uses server-rendered HTML forms and tables only. There are no `.js` files or `<script>` tags.

## Main Files

```text
src/main/java/sg/edu/nus/secure_api/controller/AuthController.java
src/main/java/sg/edu/nus/secure_api/controller/ProductController.java
src/main/java/sg/edu/nus/secure_api/security/JwtAuthenticationFilter.java
src/main/java/sg/edu/nus/secure_api/service/AuthService.java
src/main/java/sg/edu/nus/secure_api/service/JwtService.java
src/main/resources/templates/index.html
src/main/resources/templates/products.html
src/main/resources/templates/login-failure.html
database/init.sql
```

## Application Flow

1. The student opens the frontend login view at `/`.
2. Spring MVC renders `src/main/resources/templates/index.html`.
3. The login form submits username and password to `POST /login`.
4. The request reaches `src/main/java/sg/edu/nus/secure_api/controller/PageController.java`.
5. `PageController.java` calls `src/main/java/sg/edu/nus/secure_api/service/AuthService.java`.
5. `AuthService.java` checks the username and password using `src/main/java/sg/edu/nus/secure_api/repository/ProfileRepository.java`.
6. If the login is valid, `AuthService.java` calls `src/main/java/sg/edu/nus/secure_api/service/JwtService.java` to generate a JWT.
7. `AuthService.java` saves the generated JWT into `generated-jwts.txt`.
8. `PageController.java` stores the JWT in an `HttpOnly` cookie and redirects to `/products`.
9. Before `/products` reaches the controller, `src/main/java/sg/edu/nus/secure_api/security/JwtAuthenticationFilter.java` checks whether the JWT cookie is valid.
10. If the JWT is valid, `PageController.java` reads products using `src/main/java/sg/edu/nus/secure_api/repository/ProductRepository.java`.
11. Spring MVC renders the final product view with `src/main/resources/templates/products.html`.
12. If login fails, Spring MVC redirects to `/login-failure` and renders `src/main/resources/templates/login-failure.html`.

`GET /api/products` still accepts the standard `Authorization: Bearer <jwt>` header for API testing.

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
