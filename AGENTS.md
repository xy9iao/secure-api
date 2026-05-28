# AGENTS.md

Guidance for agents and contributors working on this project.

## Project Purpose

This repository is a teaching demo for beginner-to-intermediate Java students. The goal is to build a simple web application that demonstrates secure communication between a browser frontend and RESTful backend APIs using JWT authentication.

The project should feel close to industry practice, but it must stay small enough that students can understand, explain, and reimplement it by themselves.

## Current Planning Status

The project is still in planning and improvement mode. Do not make broad implementation changes unless the user explicitly asks for code changes.

When proposing or implementing changes, keep the learning path clear:

- Prefer simple Java and Spring MVC patterns over advanced framework magic.
- Explain security decisions in a way students can follow.
- Keep the app realistic without turning it into a production-scale system.
- Make each layer easy to identify: controller, service, repository, model/entity, and frontend.

## Workflow Rules

- Do not modify `README.md` unless the user explicitly asks for README changes.
- Testing will be done by the user. Do not run tests unless the user explicitly asks for testing or verification commands.
- After completing a task, it is acceptable to briefly update this `AGENTS.md` file with notes that help future tasks, especially when a decision has been made or a task has been completed.
- Keep `AGENTS.md` updates short and practical. Do not turn it into a changelog.

## Current Repository Snapshot

The current codebase is a Spring Boot JWT demo that is being migrated into the planned car-rental web app.

Current structure and behavior:

- Maven project using Java 17 and Spring Boot.
- Main package: `sg.edu.nus.secure_api`.
- Static frontend: `src/main/resources/static/index.html`.
- Database scripts: `database/profiles.sql` and `database/products.sql`.
- Current database tables: `profiles` and `products`.
- Current domain models: `Profile` and `Product`.
- Current repositories: `ProfileRepository` and `ProductRepository`.
- Current login DTOs: `LoginRequest` and `LoginResponse`.
- Current controllers:
  - `AuthController` handles `POST /api/auth/login`.
  - `ProductController` handles `GET /api/products`.
  - `PublicController` exposes `GET /api/public/hello`.
  - `SecureController` exposes `GET /api/secure/hello`.
  - `AdminController` exposes `GET /api/admin/hello`.
- Current JWT code:
  - `JwtService` generates, validates, and reads JWT claims.
  - `JwtAuthenticationFilter` reads the `Authorization: Bearer ...` header.
  - `JwtAuthenticationFilter` enforces protected endpoint access without Spring Security.

Important current limitations to address in the enhanced version:

- The frontend now has login, product, unauthorized, JWT display, product loading, and logout states split across `index.html`, `styles.css`, and `app.js`.
- The product view is currently routed with `#products`; a separate static product page can be added later if the lesson needs a visible `/products.html` URL.
- Old classroom demo endpoints such as `/api/secure/hello` and `/api/admin/hello` still exist and can be removed later if the final app should only show the car-rental flow.

## Teaching Goals

Students should learn how to:

- Log in with a username and password stored in a database.
- Generate a JWT after successful authentication.
- Send the JWT from the frontend to protected REST APIs.
- Validate the JWT before returning protected data.
- Apply role-based access rules using simple, readable application code.
- Store secrets outside source code.
- Understand why authentication, authorization, logout, and token expiry matter.

## Important Constraints

### Security Frameworks

Do not use Java EE Security or Spring Boot Security for the final teaching implementation unless the user asks otherwise.

The current version uses a custom JWT request filter instead of Spring Security so students can see what is happening. Continue using standard Spring MVC components such as controllers, services, filters/interceptors, and repositories where appropriate.

### JWT Storage

For this classroom demo, storing the JWT in frontend JavaScript state or browser storage is acceptable. Make the tradeoff visible in comments or documentation:

- It is simple for students to inspect and understand.
- It is not the only possible production approach.
- Any real production design must also consider XSS protection, HTTPS, token lifetime, refresh strategy, and cookie settings if cookies are used.

### Secret Key Handling

Never hard-code the JWT secret key in Java code, HTML, JavaScript, SQL, or committed configuration files.

Use environment variables or local-only configuration. Recommended property pattern:

```properties
jwt.secret=${JWT_SECRET}
jwt.expiration-minutes=30
```

For local development, document how to set `JWT_SECRET`. Do not commit a real secret.

### Password Handling

For a beginner demo, plain sample passwords may appear in SQL seed data if the lesson is focused on JWT flow. However, the documentation should clearly state that production systems must store password hashes, not raw passwords.

If password hashing is added later, keep it understandable and avoid hiding the core JWT lesson behind framework behavior.

## Target Web App Story

Use a small car-rental inventory story.

The app represents a small rental company where staff members manage vehicles assigned to them:

- `renter1`: a normal user who can view vehicles owned by `renter1`.
- `renter2`: a normal user who can view vehicles owned by `renter2`.
- `admin`: an administrator who can view all vehicles.

This story gives students a clear reason for authentication, ownership, and role-based access without adding unnecessary business complexity.

## Required Database Changes

The old `database/init.sql` structure has been replaced with two clearer scripts.

### `database/profiles.sql`

Stores login and role data.

Required columns:

- `id`
- `username`
- `password`
- `role`

Required seed users:

- `renter1` with role `USER`
- `renter2` with role `USER`
- `admin` with role `ADMIN`

Use sample passwords that are easy for students to test, and document that they are demo-only.

### `database/products.sql`

Stores rental products or vehicles.

Required columns:

- `id`
- `name`
- `category`
- `description`
- `owner`

The `owner` value should match a username from `profiles.sql`. Example categories may include `Sedan`, `SUV`, `Van`, or `Electric`.

## Required Application Flow

### 1. Login Page

The login page accepts a username and password.

Expected flow:

1. Frontend sends credentials to the backend login API.
2. Backend checks the profile data in the database.
3. Backend generates a JWT when credentials are valid.
4. Frontend stores the JWT for the current session.
5. Frontend navigates to the product page.

The login response should include enough information for the frontend to show the user state clearly, such as username, role, and token.

### 2. Product Page

The product page must require a valid JWT.

Expected behavior:

- Show the JWT on the page so students can inspect it.
- Load products by calling a protected backend API with the JWT.
- Show products according to role:
  - `USER`: only products owned by the logged-in username.
  - `ADMIN`: all products.
- Include a logout button.

### 3. Logout

Logout should remove the JWT from the frontend and make the current session unable to access protected pages.

For this demo, client-side token removal is acceptable. If server-side invalidation is introduced, keep it simple and explain the tradeoff because stateless JWT logout can be confusing for beginners.

### 4. Direct Protected Page Access

If a user opens the product page URL directly without a valid JWT, the app must deny access and redirect to login or show a clear unauthorized message.

Protected API calls without a valid JWT must return an appropriate error, such as `401 Unauthorized`.

## Recommended API Shape

Keep endpoints simple and predictable:

- `POST /api/auth/login`
- `POST /api/auth/logout`
- `GET /api/products`

Possible response behavior:

- Login success: `200 OK` with username, role, and JWT.
- Login failure: `401 Unauthorized`.
- Missing or invalid JWT: `401 Unauthorized`.
- Valid JWT but insufficient role: `403 Forbidden`.

## Suggested Code Organization

Use clear MVC-style structure:

```text
src/main/java/.../controller
src/main/java/.../service
src/main/java/.../repository
src/main/java/.../model
src/main/java/.../security
src/main/resources/static
database
```

Suggested responsibilities:

- Controllers handle HTTP requests and responses.
- Services hold business logic such as login, product filtering, and JWT generation.
- Repositories handle database access.
- Models/entities represent profiles, products, and request/response DTOs.
- Security utilities handle JWT creation, validation, claims, expiry, and request checks.
- Static frontend files handle login, product display, token storage, and logout.

## Security Design Expectations

The enhanced version should demonstrate industry-style thinking while staying teachable:

- JWTs must have an expiry time.
- JWTs should include only necessary claims, such as username and role.
- The JWT secret must come from an environment variable or local-only configuration.
- Protected APIs must validate the token on every request.
- Role and ownership checks must happen on the backend, not only in frontend JavaScript.
- Frontend checks are useful for navigation, but backend checks are required for security.
- Error messages should be clear but should not leak sensitive details.

## Frontend Expectations

The frontend should be a simple web app, not only API buttons.

Required pages or views:

- Login view.
- Product view.
- Unauthorized or login-required state.

Required interface behavior:

- Show logged-in username and role on the product page.
- Show the JWT after login on the product page.
- Provide a logout button.
- Prevent product data from appearing when no valid JWT exists.
- Keep styling clean and readable for classroom demonstration.

## Documentation Expectations

Update documentation whenever the implementation changes.

README content should explain:

- Project purpose.
- How to create the database.
- How to run `profiles.sql` and `products.sql`.
- How to configure database credentials.
- How to configure `JWT_SECRET`.
- Demo usernames and passwords.
- Expected login and access behavior for `renter1`, `renter2`, and `admin`.
- Why secrets and production passwords should not be committed.

## Acceptance Criteria

The enhanced project is successful when:

- Students can run the app locally with MySQL and Maven.
- Login succeeds for seeded users.
- Login fails for invalid credentials.
- A JWT is generated and visible after login.
- Products load only with a valid JWT.
- Normal users see only their own products.
- Admin sees all products.
- Direct product page access without a JWT is blocked.
- Logout removes access to protected product data.
- The JWT secret is not committed to source control.
- The code remains small, readable, and suitable for classroom explanation.

## Writing Style for Future Changes

When editing this project:

- Use clear names such as `Profile`, `Product`, `JwtService`, and `ProductController`.
- Fix spelling in user-facing text and documentation.
- Prefer explicit code over clever abstractions.
- Add comments only where they help students understand a security decision.
- Avoid unrelated refactors.
- Keep examples consistent with the car-rental story.
