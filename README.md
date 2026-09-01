# Spring Boot Blank Project

A simple Spring Boot REST API project for learning and reusing:

* Spring Boot REST API
* Spring Security
* JWT Authentication
* Role-based Authorization
* Spring Data JPA
* MySQL
* DTO and Mapper pattern
* Global Exception Handling
* Unit Testing
* Controller Testing with MockMvc
* Repository Testing with H2

---

# Technology Stack

| Technology       | Version / Usage         |
| ---------------- | ----------------------- |
| Java             | 17                      |
| Spring Boot      | 4.1.1                   |
| Spring Framework | 7.0.9                   |
| Spring Security  | 7.1.1                   |
| Spring Data JPA  | Spring Boot managed     |
| Hibernate        | Spring Boot managed     |
| JWT - JJWT       | 0.12.6                  |
| MySQL            | Production database     |
| H2               | Test database           |
| Maven            | Build tool              |
| Lombok           | Reduce boilerplate code |
| JUnit            | Unit testing            |
| Mockito          | Mocking dependencies    |
| MockMvc          | Controller/API testing  |

Spring Boot manages most dependency versions automatically through its Maven dependency management.

---

# Project Structure

```text
src
├── main
│   ├── java/com/java
│   │
│   ├── config
│   │   └── SpringSecurityConfig.java
│   │
│   ├── controller
│   │   ├── AuthController.java
│   │   └── EmployeeController.java
│   │
│   ├── dto
│   │   ├── request
│   │   │   ├── EmployeeRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   └── RegisteRequest.java
│   │   │
│   │   └── response
│   │       ├── EmployeeResponse.java
│   │       ├── JwtAuthResponse.java
│   │       └── UserResponse.java
│   │
│   ├── entity
│   │   ├── Employee.java
│   │   ├── Role.java
│   │   └── User.java
│   │
│   ├── enums
│   │   └── Gender.java
│   │
│   ├── exception
│   │   ├── EmployeeApiException.java
│   │   ├── ErrorDetails.java
│   │   ├── GlobalExceptionHandler.java
│   │   └── ResourceNotFoundException.java
│   │
│   ├── mapper
│   │   └── EmployeeMapper.java
│   │
│   ├── repository
│   │   ├── EmployeeRepository.java
│   │   ├── RoleRepository.java
│   │   └── UserRepository.java
│   │
│   ├── security
│   │   ├── CustomUserDetailsService.java
│   │   ├── JwtAuthenticationEntryPoint.java
│   │   ├── JwtAuthenticationFilter.java
│   │   └── JwtTokenProvider.java
│   │
│   ├── service
│   │   ├── AuthService.java
│   │   ├── EmployeeService.java
│   │   │
│   │   └── impl
│   │       ├── AuthServiceImpl.java
│   │       └── EmployeeServiceImpl.java
│   │
│   └── EmsBackendApplication.java
│
└── resources
    └── application.yaml
```

Test structure:

```text
src/test
├── java/com/java
│
├── controller
│   └── EmployeeControllerTest.java
│
├── entity
│   └── EmployeeTest.java
│
├── mapper
│   └── EmployeeMapperTest.java
│
├── repository
│   └── EmployeeRepositoryTest.java
│
├── service/impl
│   └── EmployeeServiceImplTest.java
│
└── EmsBackendApplicationTests.java

src/test/resources
└── application-test.yml
```

---

# Main Application Flow

The normal Employee API flow is:

```text
HTTP Request
     ↓
Spring Security / JWT Filter
     ↓
Controller
     ↓
Service
     ↓
Mapper
     ↓
Repository
     ↓
MySQL Database
```

Example:

```text
GET /api/employees/1
        ↓
JwtAuthenticationFilter
        ↓
EmployeeController
        ↓
EmployeeServiceImpl
        ↓
EmployeeRepository
        ↓
employees table
        ↓
EmployeeMapper
        ↓
EmployeeResponse
        ↓
JSON Response
```

---

# Authentication Flow

This project uses JWT authentication.

Main authentication classes:

```text
AuthController
      ↓
AuthServiceImpl
      ↓
AuthenticationManager
      ↓
CustomUserDetailsService
      ↓
UserRepository
      ↓
PasswordEncoder
      ↓
JwtTokenProvider
```

## Register

Endpoint:

```text
POST /api/auth/register
```

Example request:

```json
{
  "name": "Daniel Wong",
  "username": "daniel",
  "email": "daniel@example.com",
  "password": "Password123",
  "roles": [
    "ROLE_USER"
  ]
}
```

Register flow:

```text
Register Request
      ↓
Check username
      ↓
Check email
      ↓
Find requested roles
      ↓
BCrypt password encoding
      ↓
Save User
      ↓
Authenticate User
      ↓
Generate JWT
      ↓
Return JwtAuthResponse
```

---

# Login

Endpoint:

```text
POST /api/auth/login
```

Example:

```json
{
  "usernameOrEmail": "daniel",
  "password": "Password123"
}
```

Successful login returns information similar to:

```json
{
  "accessToken": "...",
  "tokenType": "Bearer",
  "id": 1,
  "name": "Daniel Wong",
  "username": "daniel",
  "email": "daniel@example.com",
  "roles": [
    "ROLE_USER"
  ]
}
```

Use the token for protected APIs:

```text
Authorization: Bearer <JWT_TOKEN>
```

---

# Spring Security

Security configuration is located in:

```text
config/SpringSecurityConfig.java
```

Important configuration:

```text
/api/auth/**        → Public
OPTIONS /**         → Public
Other APIs          → Authentication required
```

The application uses:

```text
SessionCreationPolicy.STATELESS
```

Therefore the server does not keep login sessions.

JWT is sent with every protected request.

```text
Client
  ↓
Authorization: Bearer JWT
  ↓
JwtAuthenticationFilter
  ↓
Validate JWT
  ↓
Set Authentication in SecurityContext
  ↓
Controller
```

---

# Role-Based Authorization

Employee APIs currently require:

```java
@PreAuthorize("hasRole('ADMIN')")
```

Therefore the user must have:

```text
ROLE_ADMIN
```

to access Employee CRUD APIs.

---

# Employee API

Base URL:

```text
http://localhost:8080/ems-backend
```

| Method | URL                   | Purpose            | Role   |
| ------ | --------------------- | ------------------ | ------ |
| POST   | `/api/auth/register`  | Register user      | Public |
| POST   | `/api/auth/login`     | Login              | Public |
| POST   | `/api/employees`      | Create employee(s) | ADMIN  |
| GET    | `/api/employees/{id}` | Get employee       | ADMIN  |
| GET    | `/api/employees`      | Get all employees  | ADMIN  |
| PUT    | `/api/employees/{id}` | Update employee    | ADMIN  |
| DELETE | `/api/employees/{id}` | Delete employee    | ADMIN  |

---

# Employee Layers

## Controller

```text
EmployeeController
```

Responsible for:

```text
HTTP Request
HTTP Response
HTTP Status
Validation
Authorization
```

It should not contain database logic.

---

## Service

```text
EmployeeService
        ↓
EmployeeServiceImpl
```

Responsible for business logic.

Example:

```text
Find employee
Update employee
Delete employee
Throw ResourceNotFoundException
```

---

## Mapper

```text
EmployeeMapper
```

Converts:

```text
EmployeeRequest
      ↓
Employee
```

and:

```text
Employee
      ↓
EmployeeResponse
```

This prevents the Entity from being directly exposed through the API.

---

## Repository

```text
EmployeeRepository
```

Extends:

```java
JpaRepository<Employee, Long>
```

It communicates with the database.

```text
EmployeeServiceImpl
       ↓
EmployeeRepository
       ↓
Hibernate / JPA
       ↓
MySQL
```

---

## Entity

```text
Employee
```

Database table:

```text
employees
```

Fields:

```text
id
firstName
lastName
gender
email
```

Before insert/update:

```java
@PrePersist
@PreUpdate
```

the Employee entity normalizes data.

Example:

```text
daniel                 → Daniel
wong                   → Wong
DANIEL@EXAMPLE.COM     → daniel@example.com
```

---

# Testing Strategy

This project separates tests by layer.

```text
Controller
    ↓
MockMvc + Mockito

Service
    ↓
JUnit + Mockito

Mapper
    ↓
JUnit

Repository
    ↓
@DataJpaTest + H2

Entity
    ↓
JUnit

Whole Application
    ↓
@SpringBootTest
```

---

# Controller Test

File:

```text
EmployeeControllerTest.java
```

Important annotations:

```java
@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
```

Dependencies are mocked:

```java
@MockitoBean
private EmployeeService employeeService;

@MockitoBean
private JwtAuthenticationFilter jwtAuthenticationFilter;
```

Controller test architecture:

```text
MockMvc
   ↓
REAL EmployeeController
   ↓
MOCK EmployeeService
   X
Database
```

The controller test does NOT access MySQL.

It tests:

```text
URL mapping
HTTP method
Request JSON
Response JSON
HTTP status
Controller → Service call
```

---

# Controller Test Pattern

Remember:

```text
Arrange
   ↓
Act
   ↓
Assert
   ↓
Verify
```

Example:

```java
when(employeeService.getEmployeeById(1L))
        .thenReturn(response);
```

means:

```text
Arrange:
Tell Mockito what the fake service should return.
```

Then:

```java
mockMvc.perform(
        get("/api/employees/{id}", 1L)
)
```

means:

```text
Act:
Send a simulated HTTP request to the real controller.
```

Then:

```java
.andExpect(status().isOk())
.andExpect(jsonPath("$.id").value(1))
.andExpect(jsonPath("$.firstName").value("Daniel"));
```

means:

```text
Assert:
Check HTTP status and JSON response.
```

Finally:

```java
verify(employeeService)
        .getEmployeeById(1L);
```

means:

```text
Verify:
Make sure the controller called the service.
```

---

# Employee Controller Tests

Current controller tests:

```text
shouldGetEmployeeById
shouldGetAllEmployees
shouldCreateEmployee
shouldUpdateEmployee
shouldDeleteEmployee
```

Important:

```text
MockMvc does NOT automatically check every JSON field.
```

If you write:

```java
.andExpect(jsonPath("$.firstName").value("Daniel"));
```

only `firstName` is checked.

If you want to verify gender:

```java
.andExpect(jsonPath("$.gender").value("MALE"));
```

you must explicitly add it.

---

# Service Test

File:

```text
EmployeeServiceImplTest.java
```

Architecture:

```text
JUnit
  ↓
REAL EmployeeServiceImpl
  ↓
MOCK EmployeeRepository
```

Important annotations:

```java
@ExtendWith(MockitoExtension.class)

@Mock
private EmployeeRepository employeeRepository;

@InjectMocks
private EmployeeServiceImpl employeeService;
```

Current tests include:

```text
Create Employee
Get Employee By ID
Employee Not Found
Get All Employees
Empty Employee List
Update Employee
Update Missing Employee
Delete Employee
Delete Missing Employee
```

---

# Mapper Test

File:

```text
EmployeeMapperTest.java
```

No Spring and no Mockito are required.

```text
EmployeeRequest
      ↓
EmployeeMapper
      ↓
Employee
```

and:

```text
Employee
      ↓
EmployeeMapper
      ↓
EmployeeResponse
```

---

# Repository Test

File:

```text
EmployeeRepositoryTest.java
```

Uses:

```java
@DataJpaTest
@ActiveProfiles("test")
```

Architecture:

```text
JUnit
   ↓
REAL EmployeeRepository
   ↓
Hibernate / JPA
   ↓
H2 In-Memory Database
```

It does NOT use the production MySQL database.

Current tests:

```text
Save Employee
Find Employee By ID
Find All Employees
Delete Employee
```

Test configuration:

```text
src/test/resources/application-test.yml
```

H2 is automatically destroyed after testing.

---

# Entity Test

File:

```text
EmployeeTest.java
```

Tests:

```text
Builder
Getters
Setters
Gender
Empty Employee
```

It is a pure Java/JUnit test.

---

# Test Flow Summary

```text
EmployeeControllerTest
        ↓
Controller REAL
Service MOCK


EmployeeServiceImplTest
        ↓
Service REAL
Repository MOCK


EmployeeMapperTest
        ↓
Mapper REAL


EmployeeRepositoryTest
        ↓
Repository REAL
H2 Database REAL


EmployeeTest
        ↓
Employee object
```

---

# Running Tests

Run one test in Eclipse:

```text
Right-click test class
→ Run As
→ JUnit Test
```

Examples:

```text
EmployeeControllerTest
EmployeeServiceImplTest
EmployeeMapperTest
EmployeeRepositoryTest
EmployeeTest
```

The current `pom.xml` contains:

```xml
<skipTests>true</skipTests>
```

Therefore a normal Maven package skips test execution.

To force Maven to run tests:

```bash
mvn test -DskipTests=false
```

To run one class:

```bash
mvn -Dtest=EmployeeControllerTest -DskipTests=false test
```

---

# Build Project

Build JAR:

```bash
mvn clean package
```

Because `skipTests=true` is currently configured, tests are skipped during the normal package build.

Generated JAR:

```text
target/ems-backend-0.0.1-SNAPSHOT.jar
```

Run:

```bash
java -jar target/ems-backend-0.0.1-SNAPSHOT.jar
```

---

# Database

Production:

```text
MySQL
Database: ems
```

Testing:

```text
H2 In-Memory Database
```

Do not store real database passwords or JWT secrets in Git.

Recommended configuration:

```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

app:
  jwt-secret: ${JWT_SECRET}
```

---

# Quick Memory Guide

When I forget the architecture, remember:

```text
Controller = API
Service    = Business Logic
Mapper     = DTO ↔ Entity
Repository = Database
Entity     = Database Table
Security   = Authentication / JWT
Config     = Spring Configuration
DTO        = Request / Response Data
Exception  = Error Handling
```

When I forget testing:

```text
Controller Test
= MockMvc + Mock Service

Service Test
= Real Service + Mock Repository

Mapper Test
= Pure JUnit

Repository Test
= Real Repository + H2

Entity Test
= Pure JUnit
```

When I forget authentication:

```text
Login
  ↓
AuthenticationManager
  ↓
CustomUserDetailsService
  ↓
UserRepository
  ↓
BCrypt Password Check
  ↓
JwtTokenProvider
  ↓
JWT
  ↓
Client sends Bearer Token
  ↓
JwtAuthenticationFilter
  ↓
SecurityContext
  ↓
Protected API
```

---

# Purpose

This project is intended to be a reusable Spring Boot reference project for remembering:

* REST API architecture
* Spring Security
* JWT authentication
* Role-based authorization
* JPA relationships
* DTO and Mapper design
* Exception handling
* Controller testing
* Mockito
* MockMvc
* Repository testing
* H2 test database
* Layered Spring Boot architecture
