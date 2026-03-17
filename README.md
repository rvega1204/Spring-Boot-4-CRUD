# User Management API

A Spring Boot 4 REST API for user registration, authentication, and user management using JWT-based stateless security.

## Overview

This project was built as a CRUD + authentication practice API with a clean layered structure.  
It exposes public authentication and registration endpoints, protects user management endpoints with JWT, validates incoming requests, and returns consistent JSON error responses.

## Main Features

- Public user registration
- Login with JWT token generation
- Stateless authentication with Spring Security
- Protected user management endpoints
- Password hashing with BCrypt
- Global exception handling
- Request validation
- Unit and controller testing with Mockito and MockMvc

## Tech Stack

- Java 21
- Spring Boot 4.0.3
- Spring Security 7
- Spring Data JPA
- Hibernate
- MySQL
- Maven
- JJWT 0.12.6
- JUnit 5
- Mockito

## Dependencies

Main dependencies used in the project:

- `spring-boot-starter-webmvc`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-security`
- `spring-boot-starter-validation`
- `mysql-connector-j`
- `jjwt-api`
- `jjwt-impl`
- `jjwt-jackson`

## Project Structure

```text
src
├── main
│   ├── java
│   │   └── dev
│   │       └── rvg
│   │           └── usermanagement
│   │               ├── config
│   │               ├── controller
│   │               ├── dto
│   │               ├── entity
│   │               ├── exception
│   │               ├── mapper
│   │               ├── repository
│   │               ├── security
│   │               └── service
│   └── resources
│       └── application.properties
└── test
    └── java
        └── dev
            └── rvg
                └── usermanagement
```

## Architecture

The application follows a layered architecture with clear separation of concerns:

### Controller Layer
Responsible for HTTP request handling and response generation.

### Service Layer
Contains the business logic for authentication and user operations.

### Repository Layer
Uses Spring Data JPA to access the database.

### DTO Layer
Separates API contracts from persistence models and avoids exposing sensitive fields like passwords.

### Security Layer
Handles JWT validation, authentication flow, and route protection.

### Exception Layer
Centralizes error handling and returns consistent error responses.

## Current Domain Model

The main entity is User.

### User fields
- `id`
- `firstName`
- `lastName`
- `email`
- `password`

## Configuration

### Java Version
This project uses Java 21.

### Maven Parent
The project uses spring-boot-starter-parent version 4.0.3.

### Database
The project is designed to work with MySQL.

### Example database creation:

```sql
CREATE DATABASE usermanagement;
```

### Example application.properties

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/usermanagement
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

jwt.secret=your_very_secure_secret_key_here
jwt.expiration=86400000
```

## Security Model

The application uses JWT-based stateless authentication.

### Public Endpoints

These endpoints are accessible without a token:

- `POST /api/v1/auth/login`
- `POST /api/v1/auth/logout`
- `POST /api/v1/users`

### Protected Endpoints

These endpoints require a valid JWT token:

- `GET /api/v1/users`
- `GET /api/v1/users/{id}`
- `GET /api/v1/users/email/{email}`
- `PUT /api/v1/users/{id}`
- `DELETE /api/v1/users/{id}`

### Authorization Header

Protected endpoints must receive the token like this:

```
Authorization: Bearer <your_jwt_token>
```

## Authentication Flow

### 1. Register a user

A new user can be created without authentication.

Request:

```
POST /api/v1/users
Content-Type: application/json
```

```json
{
  "firstName": "Magaly",
  "lastName": "Ramirez",
  "email": "magaly@gmail.com",
  "password": "password123"
}
```

### 2. Login

The user sends email and password to authenticate.

Request:

```
POST /api/v1/auth/login
Content-Type: application/json
```

```json
{
  "email": "magaly@gmail.com",
  "password": "password123"
}
```

Expected response:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1
}
```

### 3. Access protected endpoints

Use the JWT token in the Authorization header.

### 4. Logout

Logout is implemented as a simple stateless endpoint.
The server responds successfully, and the client is responsible for discarding the token.

Request:

```
POST /api/v1/auth/logout
```

## API Endpoints

### Auth Endpoints

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/v1/auth/login` | Public | Authenticates a user and returns a JWT token |
| POST | `/api/v1/auth/logout` | Public | Performs stateless logout; client must discard the token |

### User Endpoints

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/v1/users` | Public | Registers a new user |
| GET | `/api/v1/users` | Protected | Returns all users |
| GET | `/api/v1/users/{id}` | Protected | Returns a user by id |
| GET | `/api/v1/users/email/{email}` | Protected | Returns a user by email |
| PUT | `/api/v1/users/{id}` | Protected | Updates an existing user |
| DELETE | `/api/v1/users/{id}` | Protected | Deletes a user |

## DTO Design

The API uses dedicated DTOs to keep the contract clean and avoid leaking internal fields.

### Typical DTOs in the project

- `CreateUserDto`
- `UserDto`
- `LoginRequestDto`
- `LoginResponseDto`
- `ErrorResponseDto`

### Design Notes

- `CreateUserDto` receives the password during registration
- `UserDto` does not expose the password
- `LoginRequestDto` contains email and password
- `LoginResponseDto` returns the token and user id
- `ErrorResponseDto` standardizes API error responses

## Password Handling

Passwords are encoded using BCryptPasswordEncoder.

This ensures:

- Plain text passwords are never stored directly
- Authentication compares the raw password with the encoded value
- The service layer remains responsible for password encoding before persistence

## Validation

Request validation is applied to input DTOs.

### Registration validations

Expected rules include:

- `firstName` is required
- `lastName` is required
- `email` is required
- `email` must have a valid format
- `password` is required
- `password` must have at least 8 characters

### Login validations

Expected rules include:

- `email` is required
- `email` must have a valid format
- `password` is required

## Error Handling

The project uses custom exceptions and a global exception handler to return consistent JSON responses.

### Known exception cases

- Resource not found
- Invalid credentials
- Duplicate email
- Validation errors
- Unexpected server errors

### Example error response

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id 99",
  "timestamp": "2026-03-17T15:20:00"
}
```

### Common HTTP status codes

| Status | Meaning |
|--------|---------|
| 400 | Validation error |
| 401 | Invalid credentials |
| 404 | Resource not found |
| 409 | Duplicate email |
| 500 | Unexpected error |

## Build and Run

### Build the project

```bash
mvn clean install
```

### Run the application

```bash
mvn spring-boot:run
```

The API starts by default at:

```
http://localhost:8080
```

## Example Usage

### Create User

```
POST http://localhost:8080/api/v1/users
Content-Type: application/json

{
  "firstName": "Magaly",
  "lastName": "Ramirez",
  "email": "magaly@gmail.com",
  "password": "password123"
}
```

### Login

```
POST http://localhost:8080/api/v1/auth/login
Content-Type: application/json

{
  "email": "magaly@gmail.com",
  "password": "password123"
}
```

### Get All Users

```
GET http://localhost:8080/api/v1/users
Authorization: Bearer <token>
```

### Get User By Id

```
GET http://localhost:8080/api/v1/users/1
Authorization: Bearer <token>
```

### Update User

```
PUT http://localhost:8080/api/v1/users/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "id": 1,
  "firstName": "Magaly",
  "lastName": "Lopez",
  "email": "magaly@gmail.com"
}
```

### Delete User

```
DELETE http://localhost:8080/api/v1/users/1
Authorization: Bearer <token>
```

## Testing

The project includes both service and controller tests.

### Test Types

- Unit tests for service layer
- Unit tests for exception handling
- Controller tests using MockMvc
- Mockito-based dependency mocking

### Covered Test Classes

- `UserServiceImplTest`
- `AuthServiceImplTest`
- `GlobalExceptionHandlerTest`
- `UserControllerTest`
- `AuthControllerTest`

### Run Tests

```bash
mvn test
```

## Design Decisions

### Why CreateUserDto and UserDto are separated

This prevents password exposure in API responses and keeps input contracts different from output contracts.

### Why logout is simple

Because the application uses stateless JWT authentication, logout does not invalidate the token on the server side.
Instead, the client removes the token locally.

### Why user registration is public

A public registration endpoint solves the bootstrap problem of creating the first user before authentication is possible.

## Possible Improvements

- Add role-based authorization (ADMIN, USER)
- Add refresh tokens
- Implement token blacklist for server-side logout
- Add integration tests
- Add Testcontainers for MySQL testing
- Add Docker support
- Add OpenAPI / Swagger documentation
- Add audit fields such as createdAt and updatedAt

## License

MIT License
Copyright (c) 2026 Ricardo