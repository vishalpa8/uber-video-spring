# Uber Video API Documentation

## Overview
Spring Boot REST API for user registration with JWT authentication and PostgreSQL database.

## Prerequisites
- Java 21
- PostgreSQL
- Maven

## Setup

### 1. Database Setup
```sql
CREATE DATABASE uber_video;

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'ROLE_USER',
    socket_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 2. Configuration
Update `src/main/resources/application.properties`:
```properties
spring.application.name=uber-video
server.port=4000

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/uber_video
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# JWT Configuration
spring.jwtSecret=your_jwt_secret_key_here_make_it_long_and_secure
spring.jwtExpiration=86400000
spring.jwtCookieName=token
```

### 3. Run Application
```bash
./mvnw spring-boot:run
```

Or build and run:
```bash
./mvnw clean package
java -jar target/uber-video-0.0.1-SNAPSHOT.jar
```

## API Endpoints

### POST /api/auth/user/register

Register a new user in the system.

#### Request

**URL:** `http://localhost:4000/api/auth/user/register`

**Method:** `POST`

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "fullName": {
    "firstName": "John",
    "lastName": "Doe"
  },
  "email": "john@example.com",
  "password": "password123",
  "role": "user"
}
```

#### Validation Rules

- `fullName.firstName`: Required, minimum 3 characters
- `fullName.lastName`: Optional, minimum 3 characters if provided
- `email`: Required, must be valid email format
- `password`: Required, minimum 6 characters
- `role`: Optional, defaults to "ROLE_USER", use "admin" for "ROLE_ADMIN"

#### Response

**Success (201):**
```json
{
  "user": {
    "id": 1,
    "first_name": "John",
    "last_name": "Doe",
    "email": "john@example.com",
    "socket_id": null,
    "created_at": "2025-01-23T14:24:05.652",
    "updated_at": "2025-01-23T14:24:05.652"
  }
}
```

**Error (400) - Validation:**
```json
{
  "errors": [
    {
      "msg": "First name must be at least 3 characters long",
      "param": "fullName.firstName",
      "location": "body"
    }
  ]
}
```

**Error (400) - User Exists:**
```json
{
  "message": "User already exists"
}
```

#### cURL Example

```bash
curl -X POST http://localhost:4000/api/auth/user/register -H "Content-Type: application/json" -d '{"fullName":{"firstName":"John","lastName":"Doe"},"email":"john@example.com","password":"password123"}'
```

### POST /api/auth/user/login

Login with existing user credentials.

#### Request

**URL:** `http://localhost:4000/api/auth/user/login`

**Method:** `POST`

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

#### Response

**Success (200):**
```json
{
  "user": {
    "id": 1,
    "first_name": "John",
    "last_name": "Doe",
    "email": "john@example.com",
    "socket_id": null,
    "created_at": "2025-01-23T14:24:05.652",
    "updated_at": "2025-01-23T14:24:05.652"
  },
  "token": "ResponseCookie object"
}
```

**Set-Cookie Header:**
```
token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...; Path=/; Max-Age=86400000; HttpOnly; SameSite=Lax
```

**Error (401) - Invalid Credentials:**
```json
{
  "message": "Invalid email or password"
}
```

#### cURL Example

```bash
curl -X POST http://localhost:4000/api/auth/user/login -H "Content-Type: application/json" -d '{"email":"john@example.com","password":"password123"}'
```

### GET /api/auth/user

Get all registered users (ADMIN only).

#### Request

**URL:** `http://localhost:4000/api/auth/user`

**Method:** `GET`

**Headers:**
```
Authorization: Bearer <jwt_token>
```

#### Response

**Success (200):**
```json
[
  {
    "id": 1,
    "first_name": "John",
    "last_name": "Doe",
    "email": "john@example.com",
    "socket_id": null,
    "created_at": "2025-01-23T14:24:05.652",
    "updated_at": "2025-01-23T14:24:05.652"
  }
]
```

**Error (403) - Forbidden:**
```json
{
  "message": "Access Denied"
}
```

### POST /api/auth/user/logout

Logout the current user and clear authentication cookie.

#### Request

**URL:** `http://localhost:4000/api/auth/user/logout`

**Method:** `POST`

**Headers:**
```
Authorization: Bearer <jwt_token>
```

#### Response

**Success (200):**
```
Logged out successfully
```

**Set-Cookie Header:**
```
token=; Path=/; Max-Age=0; HttpOnly; SameSite=Lax
```

**Error (401) - Not Logged In:**
```json
{
  "message": "User is not logged in"
}
```

#### cURL Example

```bash
curl -X POST http://localhost:4000/api/auth/user/logout -H "Authorization: Bearer <jwt_token>"
```

### DELETE /api/auth/user/delete/{id}

Delete a user by ID (ADMIN only).

#### Request

**URL:** `http://localhost:4000/api/auth/user/delete/1`

**Method:** `DELETE`

**Headers:**
```
Authorization: Bearer <jwt_token>
```

#### Response

**Success (200):**
```json
{
  "message": "User deleted successfully"
}
```

**Error (403) - Forbidden:**
```json
{
  "message": "Access Denied"
}
```

**Error (400) - User Not Found:**
```json
{
  "message": "User not found with id: 999"
}
```

## Project Structure

```
src/main/java/com/personal/uber_video/
├── UberVideoApplication.java
├── controller/
│   └── UserController.java
├── dto/
│   ├── FullNameDto.java
│   ├── LoginDto.java
│   └── UserRegistrationDto.java
├── entity/
│   └── User.java
├── exception/
│   ├── ApiException.java
│   └── GlobalExceptionHandler.java
├── repository/
│   └── UserRepository.java
├── response/
│   ├── ApiResponse.java
│   └── UserResponseDto.java
├── security/
│   ├── JwtAuthenticationFilter.java
│   ├── SecurityConfig.java
│   └── UserDetailsServiceImpl.java
├── service/
│   ├── UserService.java
│   └── UserServiceImpl.java
└── util/
    ├── AuthUtil.java
    └── JwtUtil.java
```

## Dependencies

- Spring Boot 3.2.0
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- Spring Boot Starter Validation
- PostgreSQL Driver
- Lombok 1.18.30
- JWT (jjwt) 0.12.3

## Features

- User registration with validation
- User login with JWT authentication
- Password encryption using BCrypt
- JWT token generation (HS384 algorithm)
- Role-based access control (ADMIN/USER)
- PostgreSQL database integration
- Input validation with custom error messages
- Global exception handling
- RESTful API design
- Automatic timestamp management
- Spring Security integration with AuthenticationManager
- CSRF protection disabled for stateless API
- Cookie-based authentication with HTTP-only cookies
- Secure logout with cookie expiration

## Testing

Run tests:
```bash
./mvnw test
```

Test coverage includes:
- Successful user registration (with and without lastName, with roles)
- User login (success and invalid credentials)
- User logout (clears cookie and security context)
- First name validation (required, min 3 characters)
- Email validation (required, valid format)
- Password validation (required, min 6 characters)
- Duplicate user detection
- Role-based access control (ADMIN-only endpoints)
- JWT authentication flow