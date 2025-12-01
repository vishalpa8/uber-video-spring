# Uber Video API

## Overview
Spring Boot REST API for an Uber-like ride-sharing application with JWT authentication, PostgreSQL database, and comprehensive Swagger documentation.

**Features:**
- 👤 User & Captain (Driver) Management
- 🔐 JWT Authentication with HTTP-only cookies
- 🚗 Vehicle Management
- 🛡️ Advanced Security (Token Blacklist, XSS Protection)
- 📚 Interactive Swagger/OpenAPI Documentation
- ✅ 107 Tests (100% passing)

---

## Prerequisites
- **Java 21+** (Tested with Java 25)
- **PostgreSQL 12+** (Tested with 18.1)
- **Maven 3.9+**

---

## Quick Start

### 1. Database Setup
```sql
CREATE DATABASE uber_video;
```

The application uses Hibernate auto-DDL to create tables automatically.

### 2. Configuration
Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/uber_video
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jwtSecret=your_jwt_secret_key_here_make_it_long_and_secure
spring.jwtExpiration=86400000
```

### 3. Run Application
```bash
./mvnw spring-boot:run
```

### 4. Access Swagger Documentation
Open your browser and navigate to:
```
http://localhost:4000
```
**Automatically redirects to Swagger UI!** 🎉

---

## 📚 API Documentation

### Swagger UI (Interactive)
- **URL:** `http://localhost:4000/swagger-ui.html`
- **Features:**
  - Try out APIs directly from browser
  - Auto-generated request/response examples
  - JWT authentication testing
  - Schema explorer

### OpenAPI Specification
- **JSON:** `http://localhost:4000/api-docs`
- **OpenAPI v3:** `http://localhost:4000/v3/api-docs`

---

## 🔌 API Endpoints

### 👤 User Management

#### POST `/api/auth/user/register`
Register a new user.

**Request:**
```json
{
  "fullName": {
    "firstName": "John",
    "lastName": "Doe"
  },
  "email": "john@example.com",
  "password": "password123",
  "role": "admin"
}
```

**Response (201):**
```json
{
  "user": {
    "fullName": "John Doe",
    "email": "john@example.com",
    "socket_id": null,
    "created_at": "2025-12-01T23:00:00.000",
    "updated_at": "2025-12-01T23:00:00.000",
    "Role": "Admin"
  },
  "message": "User Registered Successfully!"
}
```

#### POST `/api/auth/user/login`
Login with email and password.

**Request:**
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response (200):**
```json
{
  "user": {
    "fullName": "John Doe",
    "email": "john@example.com",
    "Role": "User"
  }
}
```
**Set-Cookie:** `token=<jwt_token>; HttpOnly; SameSite=Lax; Max-Age=86400000`

#### GET `/api/auth/user/profile` 🔒
Get current user profile (requires authentication).

#### POST `/api/auth/user/logout` 🔒
Logout and invalidate JWT token.

#### GET `/api/auth/user` 🔒 👑
Get all users (ADMIN only).

#### DELETE `/api/auth/user/delete/{email}` 🔒 👑
Delete user by email (ADMIN only).

---

### 🚗 Captain Management

#### POST `/api/auth/captain/register`
Register a new captain (driver) with vehicle details.

**Request:**
```json
{
  "fullName": {
    "firstName": "John",
    "lastName": "Kumar"
  },
  "email": "john.kumar@example.com",
  "password": "password123",
  "vehicleType": "Bike",
  "plate": "UP16DC6447",
  "capacity": 2,
  "color": "black"
}
```

**Response (201):**
```json
{
  "captain": {
    "fullName": "John Kumar",
    "email": "john.kumar@example.com",
    "socketId": null,
    "createdAt": "2025-12-01T23:00:00.000",
    "updatedAt": "2025-12-01T23:00:00.000",
    "role": "ROLE_CAPTAIN",
    "status": "Inactive",
    "vehicleType": "BIKE",
    "vehicleColor": "black",
    "vehicleNumber": "UP16DC6447",
    "vehicleCapacity": 2
  },
  "message": "Captain registered successfully!"
}
```

**Vehicle Types:** `Car`, `Bike`, `Auto`
**Capacity Range:** 2-8 passengers

#### POST `/api/auth/captain/login`
Captain login endpoint (🚧 Not yet implemented - returns 501).

---

## 🔐 Security Features

### JWT Authentication
- **Algorithm:** HS384
- **Storage:** HTTP-only cookies
- **Expiration:** 24 hours (86400000ms)
- **Cookie Name:** `token`
- **SameSite:** Lax

### Token Blacklist
- Prevents token reuse after logout
- In-memory storage
- Automatic cleanup

### Input Validation
- **XSS Protection:** OWASP HTML Sanitizer
- **SQL Injection:** JPA parameterized queries
- **Field Validation:** Hibernate Validator
- **Email Normalization:** Lowercase + strip

### Password Security
- **Hashing:** BCrypt
- **Strength:** Auto-generated salt
- **Minimum:** 6 characters

### Role-Based Access Control (RBAC)
- **ROLE_USER:** Standard users
- **ROLE_ADMIN:** Admin operations
- **ROLE_CAPTAIN:** Drivers/Captains

---

## 🗄️ Database Schema

### Users Table
```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'ROLE_USER',
    socket_id VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### Captain Table
```sql
CREATE TABLE captain (
    captain_id UUID PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'ROLE_CAPTAIN',
    status VARCHAR(255) DEFAULT 'Inactive',
    socket_id VARCHAR(255),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    vehicle_id UUID,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (vehicle_id) REFERENCES vehicle(vehicle_id)
);
```

### Vehicle Table
```sql
CREATE TABLE vehicle (
    vehicle_id UUID PRIMARY KEY,
    vehicle_type VARCHAR(255) NOT NULL,
    color VARCHAR(255) NOT NULL,
    plate VARCHAR(255) UNIQUE NOT NULL,
    capacity INT CHECK (capacity >= 2 AND capacity <= 8)
);
```

---

## 🧪 Testing

### Run All Tests
```bash
./mvnw test
```

### Run Specific Test Class
```bash
./mvnw test -Dtest=CaptainRegistrationControllerTest
./mvnw test -Dtest=UserLoginControllerEdgeCasesTest
```

### Test Coverage (107 Tests)

| Test Suite | Tests | Description |
|------------|-------|-------------|
| **CaptainRegistrationControllerTest** | 21 | Captain registration (happy path, validation, edge cases) |
| **UserRegistrationControllerTest** | 9 | User registration (success, roles, validation) |
| **UserRegistrationControllerEdgeCasesTest** | 23 | User registration edge cases (XSS, malformed input) |
| **UserLoginControllerEdgeCasesTest** | 21 | User login scenarios (success, failures, tokens) |
| **UserLogoutControllerEdgeCasesTest** | 9 | Logout scenarios (success, blacklist, invalid tokens) |
| **UserManagementControllerTest** | 8 | Admin operations (get all, delete, RBAC) |
| **TokenBlacklistServiceTest** | 9 | Token blacklist functionality |
| **SecurityValidatorTest** | 6 | Input validation and XSS protection |
| **UberVideoApplicationTests** | 1 | Spring context loading |

**Total: 107 tests | 0 failures | Build time: ~5s**

---

## 📦 Project Structure

```
src/main/java/com/personal/uber_video/
├── UberVideoApplication.java
├── config/
│   └── OpenApiConfig.java              # Swagger/OpenAPI configuration
├── controller/
│   ├── HomeController.java             # Root → Swagger redirect
│   ├── UserController.java             # User endpoints
│   └── CaptainController.java          # Captain endpoints
├── dto/
│   ├── FullNameDto.java
│   ├── LoginDto.java
│   ├── UserRegistrationDto.java
│   └── CaptainRegistrationDto.java     # Captain registration DTO
├── entity/
│   ├── User.java
│   ├── Captain.java                    # Captain entity
│   └── Vehicle.java                    # Vehicle entity
├── exception/
│   ├── ApiException.java
│   └── GlobalExceptionHandler.java     # Centralized error handling
├── model/
│   ├── Location.java                   # Embeddable location (lat/lng)
│   ├── Status.java                     # Captain status enum
│   └── VehicleType.java                # Vehicle type enum
├── repository/
│   ├── UserRepository.java
│   └── CaptainRepository.java          # Captain repository
├── response/
│   ├── ApiResponse.java
│   ├── UserResponseDto.java
│   └── CaptainResponseDto.java         # Captain response DTO
├── security/
│   ├── AuthEntryPointJwt.java          # Custom auth entry point
│   ├── JwtAuthenticationFilter.java    # JWT filter
│   ├── SecurityConfig.java             # Security configuration
│   ├── TokenBlacklistService.java      # Token blacklist
│   └── UserDetailService.java          # User details loader
├── service/
│   ├── UserService.java
│   ├── UserServiceImpl.java
│   ├── CaptainService.java             # Captain service interface
│   └── CaptainServiceImpl.java         # Captain service implementation
└── util/
    ├── AuthUtil.java                   # Auth helper utilities
    ├── JwtUtil.java                    # JWT utilities
    └── SecurityValidator.java          # Input validation/sanitization
```

---

## 📚 Technology Stack

### Core
- **Spring Boot:** 4.0.0
- **Java:** 25
- **Maven:** 3.9.11

### Database
- **PostgreSQL Driver:** 42.7.8
- **Hibernate ORM:** 7.1.8
- **Spring Data JPA:** 4.0.0

### Security
- **Spring Security:** 7.0.0
- **JWT (jjwt):** 0.12.3
- **OWASP HTML Sanitizer:** 20240325.1
- **BCrypt:** Included in Spring Security

### Documentation
- **SpringDoc OpenAPI:** 2.7.0
- **Swagger UI:** Included

### Utilities
- **Lombok:** 1.18.42
- **Jackson:** 2.20.1

### Testing
- **JUnit 5:** 5.11.4
- **Mockito:** 5.16.0
- **Spring Boot Test:** 4.0.0

---

## 🚀 Development Workflow

### 1. Start PostgreSQL
```bash
# macOS with Homebrew
brew services start postgresql

# Or Docker
docker run --name uber-postgres -e POSTGRES_PASSWORD=password -p 5432:5432 -d postgres
```

### 2. Create Database
```bash
psql -U postgres -c "CREATE DATABASE uber_video;"
```

### 3. Run Application
```bash
./mvnw spring-boot:run
```

### 4. Access Application
- **Swagger UI:** http://localhost:4000
- **API Base:** http://localhost:4000/api/auth
- **Health Check:** http://localhost:4000/actuator/health (if enabled)

---

## 🧪 Testing with cURL

### Register Captain
```bash
curl -X POST http://localhost:4000/api/auth/captain/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": {
      "firstName": "John",
      "lastName": "Kumar"
    },
    "email": "john.kumar@example.com",
    "password": "password123",
    "vehicleType": "Bike",
    "plate": "UP16DC6447",
    "capacity": 2,
    "color": "black"
  }'
```

### Register User
```bash
curl -X POST http://localhost:4000/api/auth/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": {
      "firstName": "Jane",
      "lastName": "Doe"
    },
    "email": "jane@example.com",
    "password": "password123",
    "role": "admin"
  }'
```

### Login User
```bash
curl -X POST http://localhost:4000/api/auth/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "jane@example.com",
    "password": "password123"
  }' -c cookies.txt
```

### Get Profile (Protected)
```bash
curl -X GET http://localhost:4000/api/auth/user/profile \
  -b cookies.txt
```

### Logout
```bash
curl -X POST http://localhost:4000/api/auth/user/logout \
  -b cookies.txt
```

---

## 🛡️ Security Configuration

### Public Endpoints (No Auth)
- `/` - Root (redirects to Swagger)
- `/swagger-ui/**` - Swagger UI
- `/api-docs/**` - OpenAPI docs
- `/api/auth/user/register` - User registration
- `/api/auth/captain/register` - Captain registration
- `/api/auth/user/login` - User login
- `/api/auth/captain/login` - Captain login

### Protected Endpoints (Auth Required)
- `/api/auth/user/logout` - User logout
- `/api/auth/user/profile` - Get profile

### Admin Only Endpoints
- `/api/auth/user` - Get all users
- `/api/auth/user/delete/{email}` - Delete user

### WebSecurityCustomizer
Swagger paths completely bypass the security filter chain for optimal performance.

---

## 📋 Validation Rules

### User/Captain Registration
- **firstName:** Required, min 3 characters
- **lastName:** Optional, min 3 characters if provided
- **email:** Required, valid email format
- **password:** Required, min 6 characters
- **role:** Optional, defaults to "ROLE_USER" (for users) or "ROLE_CAPTAIN" (for captains)

### Captain-Specific Fields
- **vehicleType:** Required, min 3 characters (Car, Bike, Auto)
- **plate:** Required, min 10 characters, unique
- **capacity:** Required, range 2-8
- **color:** Required, min 3 characters

---

## 🚗 Vehicle Types

| Type | Typical Capacity | Use Case |
|------|------------------|----------|
| **Bike** | 2 | Motorcycle taxi |
| **Auto** | 3 | Auto-rickshaw |
| **Car** | 4-6 | Sedan/SUV |

---

## 🔧 Advanced Features

### Token Blacklist Service
Prevents token reuse after logout by maintaining an in-memory blacklist.

**Features:**
- Automatic token invalidation on logout
- Thread-safe concurrent operations
- Periodic cleanup (future enhancement)

### Input Sanitization
Uses OWASP HTML Sanitizer to prevent XSS attacks on user inputs.

**Protected Fields:**
- First name
- Last name
- Passwords (hashed with BCrypt)

**Excluded:**
- Email (needs `@` and `.` symbols)

### Global Exception Handling
Centralized error handling with specific messages:
- Validation errors (400)
- Authentication failures (401)
- Access denied (403)
- Not found (404)
- Database constraints (400)
- Transaction failures (400)
- Generic errors (500)

---

## 🧪 Test Coverage

### Run Tests
```bash
# All tests
./mvnw test

# Specific test class
./mvnw test -Dtest=CaptainRegistrationControllerTest

# Pattern matching
./mvnw test -Dtest=*Registration*Test

# With coverage report
./mvnw test jacoco:report
```

### Coverage Breakdown

**Controller Tests (99 tests):**
- Captain Registration: 21 tests
- User Registration: 32 tests (9 + 23 edge cases)
- User Login: 21 tests
- User Logout: 9 tests
- User Management: 8 tests

**Service Tests (7 tests):**
- Token Blacklist: 9 tests

**Utility Tests (6 tests):**
- Security Validator: 6 tests

**Integration Tests (1 test):**
- Application Context: 1 test

**Total: 107 tests | 0 failures | ~5s build time**

---

## 📊 Dependencies Overview

### Production Dependencies
```
spring-boot-starter-web ............. Web MVC
spring-boot-starter-data-jpa ........ Database access
spring-boot-starter-security ........ Authentication/Authorization
spring-boot-starter-validation ...... Input validation
postgresql .......................... PostgreSQL driver
jjwt-api/impl/jackson ............... JWT library
owasp-java-html-sanitizer ........... XSS protection
springdoc-openapi-starter-webmvc-ui . Swagger/OpenAPI
lombok .............................. Boilerplate reduction
```

### Test Dependencies
```
spring-boot-starter-test ............ Testing framework
spring-security-test ................ Security testing
mockito ............................. Mocking framework
junit-jupiter ....................... JUnit 5
```

---

## 🐛 Known Issues & Solutions

### Issue 1: `create-drop` Mode
**Problem:** Database is wiped on restart.
**Solution:** Change in `application.properties`:
```properties
spring.jpa.hibernate.ddl-auto=update
```

### Issue 2: Java 25 Warnings
**Warning:** `sun.misc.Unsafe` deprecation warnings from Guice.
**Impact:** No functional impact, but may break in future Java versions.
**Solution:** Consider using Java 21 LTS for production.

### Issue 3: Mockito Self-Attaching
**Warning:** Mockito agent warnings in tests.
**Solution:** Add to `pom.xml` (optional):
```xml
<plugin>
    <groupId>org.apache.maven.surefire</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <argLine>-javaagent:${settings.localRepository}/org/mockito/mockito-core/5.16.0/mockito-core-5.16.0.jar</argLine>
    </configuration>
</plugin>
```

---

## 🔒 Environment Variables (Recommended)

Create a `.env` file (already in `.gitignore`):
```bash
DB_HOST=localhost
DB_PORT=5432
DB_NAME=uber_video
DB_USERNAME=postgres
DB_PASSWORD=your_password
JWT_SECRET=your_jwt_secret_key_here_make_it_long_and_secure_at_least_64_characters
JWT_EXPIRATION=86400000
```

Then update `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:uber_video}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD}
spring.jwtSecret=${JWT_SECRET}
spring.jwtExpiration=${JWT_EXPIRATION:86400000}
```

---

## 📖 API Response Examples

### Success Response
```json
{
  "user": { ... },
  "message": "Operation successful"
}
```

### Validation Error (400)
```json
{
  "firstName": "First name must be at least 3 characters long",
  "email": "Email must be valid",
  "password": "Password must be at least 6 characters long"
}
```

### Business Error (400)
```json
{
  "message": "User already exists with email: john@example.com"
}
```

### Authentication Error (401)
```json
{
  "message": "Invalid email or password"
}
```

### Authorization Error (403)
```json
{
  "message": "Access Denied"
}
```

### Not Found (404)
```json
{
  "message": "User not found with id: 123"
}
```

---

## 🚀 Deployment

### Build JAR
```bash
./mvnw clean package -DskipTests
```

### Run JAR
```bash
java -jar target/uber-video-0.0.1-SNAPSHOT.jar
```

### Docker (Future)
```dockerfile
FROM openjdk:21-jdk-slim
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

---

## 📈 Roadmap

### Completed ✅
- [x] User registration & authentication
- [x] Captain registration with vehicle
- [x] JWT authentication with cookies
- [x] Token blacklist service
- [x] Role-based access control
- [x] Input validation & sanitization
- [x] Global exception handling
- [x] Swagger/OpenAPI documentation
- [x] Comprehensive test coverage (107 tests)

### In Progress 🚧
- [ ] Captain login endpoint
- [ ] Captain profile management
- [ ] Ride management (create, accept, track)
- [ ] Real-time location tracking
- [ ] WebSocket integration

### Planned 📋
- [ ] Rating system
- [ ] Payment integration
- [ ] Ride history
- [ ] Driver availability
- [ ] Surge pricing
- [ ] Admin dashboard

---

## 🤝 Contributing

### Coding Standards
- Java code style: Google Java Style Guide
- Commit messages: Conventional Commits
- Test coverage: Minimum 80%

### Before Committing
```bash
# Run tests
./mvnw test

# Check formatting
./mvnw spotless:check

# Build
./mvnw clean package
```

---

## 📞 Support

For issues or questions:
- 📧 Email: support@ubervideo.com
- 📚 Docs: http://localhost:4000/swagger-ui.html
- 🐛 Issues: GitHub Issues

---

## 📄 License

Apache License 2.0

---

## 🎯 Quick Links

- **Swagger UI:** http://localhost:4000
- **API Docs JSON:** http://localhost:4000/api-docs
- **OpenAPI v3:** http://localhost:4000/v3/api-docs

---

**Made with ❤️ using Spring Boot 4.0**
