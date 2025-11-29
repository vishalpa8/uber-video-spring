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
jwt.secret=your_jwt_secret_key_here_make_it_long_and_secure
jwt.expiration=86400000
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

### POST /users/register

Register a new user in the system.

#### Request

**URL:** `http://localhost:4000/users/register`

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
  "password": "password123"
}
```

#### Validation Rules

- `fullName.firstName`: Required, minimum 3 characters
- `fullName.lastName`: Optional, minimum 3 characters if provided
- `email`: Required, must be valid email format
- `password`: Required, minimum 6 characters

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
  },
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
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
curl -X POST http://localhost:4000/users/register -H "Content-Type: application/json" -d '{"fullName":{"firstName":"John","lastName":"Doe"},"email":"john@example.com","password":"password123"}'
```

## Project Structure

```
src/main/java/com/personal/uber_video/
├── UberVideoApplication.java
├── config/
│   └── SecurityConfig.java
├── controller/
│   └── UserController.java
├── dto/
│   ├── FullNameDto.java
│   ├── UserRegistrationDto.java
│   └── UserResponseDto.java
├── entity/
│   └── User.java
├── repository/
│   └── UserRepository.java
├── service/
│   └── UserService.java
└── util/
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
- Password encryption using BCrypt
- JWT token generation (HS384 algorithm)
- PostgreSQL database integration
- Input validation with custom error messages
- RESTful API design
- Automatic timestamp management
- CSRF protection disabled for public endpoints

## Testing

Run tests:
```bash
./mvnw test
```

Test coverage includes:
- Successful user registration
- Validation error handling
- Duplicate user detection