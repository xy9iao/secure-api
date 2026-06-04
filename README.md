# Spring Boot JWT Demo

Basic Spring Boot and Thymeleaf demo for JWT login.

## Features

- A login page sends username and password to the backend.
- The backend checks the user in MySQL.
- The backend generates a JWT after successful login.
- The JWT is saved to `generated-jwts.txt` for students to copy into jwt.io.
- The backend stores the JWT in an `HttpOnly` cookie for the browser.
- Thymeleaf renders the login, failure, and product pages on the server.
- Admin users can see all products. Normal users see only their own products.
- The frontend uses server-rendered HTML forms and tables only. There are no `.js` files or `<script>` tags.

## Application Flow

1. The student opens the frontend login view at `/`.
2. Spring MVC renders `src/main/resources/templates/index.html`.
3. The login form submits username and password to `POST /login`.
4. The request reaches `src/main/java/sg/edu/nus/secure_api/controller/PageController.java`.
5. `PageController.java` calls `src/main/java/sg/edu/nus/secure_api/service/AuthService.java`.
6. `AuthService.java` checks the username and password using `src/main/java/sg/edu/nus/secure_api/repository/ProfileRepository.java`.
7. If the login is valid, `AuthService.java` calls `src/main/java/sg/edu/nus/secure_api/service/JwtService.java` to generate a JWT.
8. `AuthService.java` saves the generated JWT into `generated-jwts.txt`.
9. `PageController.java` stores the JWT in an `HttpOnly` cookie and redirects to `/products`.
10. Before `/products` reaches the controller, `src/main/java/sg/edu/nus/secure_api/security/JwtAuthenticationFilter.java` checks whether the JWT cookie is valid.
11. If the JWT is valid, `PageController.java` reads products using `src/main/java/sg/edu/nus/secure_api/repository/ProductRepository.java`.
12. Spring MVC renders the final product view with `src/main/resources/templates/products.html`.
13. If login fails, Spring MVC redirects to `/login-failure` and renders `src/main/resources/templates/login-failure.html`.

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
