# Employee Management API

REST API for managing employee records built with Spring Boot.

## Tech Stack

| Layer | Technology |
|-------|------------|
| Framework | Spring Boot 3.x |
| Database | H2 (dev) / PostgreSQL (prod) |
| ORM | Spring Data JPA / Hibernate |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Build | Maven |

## Prerequisites

- Java 17+
- Maven 3.8+
- (Optional) Docker for PostgreSQL

## Getting Started

### Clone

```bash
git clone https://github.com/san123desh/employee-management.git
cd employee-management
```

### Run
mvn spring-boot:run

App starts at http://localhost:8080.

### Swagger UI

Open: **http://localhost:8080/swagger-ui.html**

![Swagger UI](docs/screenshots/swagger-ui.png)

### H2 Console (dev)

Open: **http://localhost:8080/h2-console**

| Field | Value |
|-------|-------|
| JDBC URL | `jdbc:h2:mem:employee_db` |
| User | `sa` |
| Password | *(empty)* |   


## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/auth/login` | Get JWT token (public) |
| GET | `/api/employees` | Get all active employees |
| GET | `/api/employees/{id}` | Get employee by ID |
| GET | `/api/employees/search?keyword=` | Search by name/email |
| POST | `/api/employees` | Create employee |
| PUT | `/api/employees/{id}` | Update employee |
| DELETE | `/api/employees/{id}` | Hard delete |
| PATCH | `/api/employees/{id}/deactivate` | Soft delete |   

## Request Flow

```
Client (Postman / Browser)
        │
        ▼
┌─────────────────────────┐
│   Controller            │  ← @RestController
│   (EmployeeController)  │     Validates input via @Valid
└────────────┬────────────┘
             │  calls
             ▼
┌─────────────────────────┐
│   Service               │  ← @Service
│   (EmployeeService)     │     Business logic
└────────────┬────────────┘
             │  calls
             ▼
┌─────────────────────────┐
│   Repository            │  ← Spring Data JPA
│   (EmployeeRepository)  │     Auto-generated queries
└────────────┬────────────┘
             │  SQL
             ▼
┌─────────────────────────┐
│   Database              │  ← H2 / PostgreSQL
│   (employees table)     │
└─────────────────────────┘
```

### Exception Flow

```
Any layer throws exception
        │
        ▼
┌─────────────────────────────────────┐
│   GlobalExceptionHandler            │  ← @RestControllerAdvice
│   (matches exception type)          │
│   → returns structured JSON error   │
└─────────────────────────────────────┘
```   

## Code Pattern (Layered Architecture)

Each feature follows the same 5-layer pattern:

```
Controller → Service (interface + impl) → Repository → Entity
     ↕              ↕
   DTOs        Exception handling (global)
```

### Rules

| Rule | Why |
|------|-----|
| Controller never touches Repository directly | Separation of concerns |
| DTOs separate from Entity | Security + decoupling |
| Service interface + impl | Testable with mocks |
| All exceptions handled in one place | Consistent error shape |
| Constructor injection (no `@Autowired`) | Immutable, testable |

## Project Structure

```
employee-management/
├── .gitignore
├── README.md
├── pom.xml
├── docker-compose.yml          ← (add later)
├── Dockerfile                  ← (add later)
├── docs/
│   └── screenshots/
│       ├── swagger-ui.png
│       └── swagger-endpoint.png
└── src/
├── main/
│   ├── java/
│   │   └── com/example/employeemanagement/
│   │       ├── EmployeeManagementApplication.java
│   │       │
│   │       ├── config/
│   │       │   ├── SecurityConfig.java
│   │       │   └── OpenApiConfig.java
│   │       │
│   │       ├── controller/
│   │       │   └── EmployeeController.java
│   │       │
│   │       ├── service/
│   │       │   ├── EmployeeService.java          ← interface
│   │       │   └── EmployeeServiceImpl.java      ← implementation
│   │       │
│   │       ├── repository/
│   │       │   └── EmployeeRepository.java
│   │       │
│   │       ├── model/
│   │       │   └── Employee.java                 ← JPA entity
│   │       │
│   │       ├── dto/
│   │       │   ├── EmployeeRequestDto.java       ← for POST/PUT body
│   │       │   └── EmployeeResponseDto.java      ← for GET response
│   │       │
│   │       ├── exception/
│   │       │   ├── ResourceNotFoundException.java
│   │       │   └── GlobalExceptionHandler.java
│   │       │
│   │       └── seeder/
│   │           └── DataSeeder.java
│   │
│   └── resources/
│       ├── application.yml
│       ├── application-prod.yml          ← (PostgreSQL config, gitignored)
│       └── db/
│           └── migration/                ← (Flyway/Liquibase later)
│
└── test/
└── java/
└── com/example/employeemanagement/
├── controller/
│   └── EmployeeControllerIT.java   ← integration tests
└── service/
└── EmployeeServiceTest.java    ← unit tests
...
```


## Running with Docker

```bash
docker-compose up   
```
This starts the app + PostgreSQL together.


---

### License

```markdown


## License

MIT   

```


## File Reference

| File | Package | Must-Have Annotations | What It Does | Why |
|------|---------|----------------------|--------------|-----|
| `Employee.java` | `model/` | `@Entity`, `@Table`, `@Id`, `@GeneratedValue` | Maps to DB table | DB schema definition |
| `EmployeeRequestDto.java` | `dto/` | `@Data`, `@NotBlank`, `@Email`, etc. | Accepts client input | Hides server fields from client |
| `EmployeeResponseDto.java` | `dto/` | `@Data` | Returns data to client | Includes `id`, `createdAt` |
| `EmployeeRepository.java` | `repository/` | *(none — extends `JpaRepository`)* | DB queries | Spring auto-generates impl |
| `EmployeeService.java` | `service/` | *(interface — no annotations)* | Contract for business logic | Decouples controller from impl |
| `EmployeeServiceImpl.java` | `service/` | `@Service` | Business logic + DTO mapping | Swappable, testable |
| `EmployeeController.java` | `controller/` | `@RestController`, `@RequestMapping` | HTTP endpoint | Entry point for requests |
| `ResourceNotFoundException.java` | `exception/` | *(extends `RuntimeException`)* | Custom 404 signal | Semantic error type |
| `GlobalExceptionHandler.java` | `exception/` | `@RestControllerAdvice`, `@ExceptionHandler` | Catches all errors → JSON | Consistent error shape |
| `SecurityConfig.java` | `config/` | `@Configuration`, `@EnableWebSecurity` | Auth rules | Protects endpoints |
| `OpenApiConfig.java` | `config/` | `@Configuration`, `@Bean` | Swagger metadata | API documentation |
| `DataSeeder.java` | `seeder/` | `@Component`, implements `CommandLineRunner` | Seeds sample data on startup | Dev convenience |
| `EmployeeManagementApplication.java` | root | `@SpringBootApplication` | Boots the app | Entry point |

---

### Per-File Cheat Sheet

#### `model/Employee.java`

**Why:** Defines the DB schema. Hibernate generates `CREATE TABLE` from this.

---

#### `dto/EmployeeRequestDto.java`


**Why:** Client can ONLY send these fields. No `id`, no `active`, no `createdAt` — server controls those.

---

#### `repository/EmployeeRepository.java`



**Why:** Data access layer. You write method names, Spring writes the SQL.

---

#### `service/EmployeeService.java` (interface)


**Why:** Contract. Controller depends on this interface, not the impl. Makes mocking in tests trivial.

---

#### `service/EmployeeServiceImpl.java`


**Why:** Business logic lives here. Controller stays thin.

---

#### `controller/EmployeeController.java`


**Why:** Only layer that talks HTTP. Delegates everything to service.

---

#### `exception/GlobalExceptionHandler.java`

**Why:** One place for all error formatting. No try-catch in controllers.

---

#### `config/SecurityConfig.java`


**Why:** Central place to define who can access what.

---

#### `seeder/DataSeeder.java`



**Why:** Gives you sample data instantly on first run. No manual SQL.

---

#### `EmployeeManagementApplication.java`


**Why:** The single entry point. Everything else is wired by Spring from here.








## Authentication (JWT)

All endpoints (except `/api/auth/**`) require a valid JWT token.

### How It Works

```
┌──────────┐         POST /api/auth/login          ┌──────────────────┐
│  Client  │  ──────────────────────────────────▶   │  AuthController  │
│          │  { "username", "password" }            │                  │
│          │                                         │  Validates creds │
│          │  ◀──────────────────────────────────    │  Generates JWT  │
└──────────┘  { "token": "eyJhbGci..." }            └──────────────────┘

┌──────────┐         GET /api/employees             ┌──────────────────────┐
│  Client  │  ──────────────────────────────────▶   │ JwtAuthenticationFilter│
│          │  Header: Authorization: Bearer eyJ...   │                      │
│          │                                         │  Validates token     │
│          │                                         │  Sets SecurityContext│
│          │  ◀──────────────────────────────────    │  → Controller runs   │
└──────────┘  200 + JSON data                        └──────────────────────┘
```

### Getting a Token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6..."
}
```

### Using the Token

Add the `Authorization` header to every protected request:

```
Authorization: Bearer <your-token-here>
```

**Postman:**
1. Go to **Authorization** tab
2. Type: **Bearer Token**
3. Paste your token (without "Bearer " prefix)

**cURL:**
```bash
curl http://localhost:8080/api/employees \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

### Token Details

| Property | Value |
|----------|-------|
| Algorithm | HS256 (HMAC-SHA256) |
| Expiry | 1 hour |
| Subject | Username |
| Stateless | No server-side sessions |

### Security Configuration

| Endpoint | Access |
|----------|--------|
| `POST /api/auth/login` | Public (no token needed) |
| `/api/employees/**` | Authenticated (token required) |
| `/swagger-ui/**`, `/v3/api-docs/**` | Public |
| `/h2-console/**` | Public (dev only) |

### Error Responses

| Status | When |
|--------|------|
| `401` | No token, expired token, or invalid credentials |
| `400` | Missing username/password in login body |

### Configuration

| Property | Source | Purpose |
|----------|--------|---------|
| `app.jwt.secret` | `.env` → `JWT_SECRET` | HMAC signing key (min 256 bits) |

The secret is injected via environment variable and never hardcoded in source. See `.env.example` for the expected format.

### Files

| File | Responsibility |
|------|---------------|
| `security/JwtTokenProvider.java` | Generate, validate, parse tokens |
| `security/JwtAuthenticationFilter.java` | Intercept requests, extract & validate token |
| `config/SecurityConfig.java` | Define access rules, register JWT filter |
| `controller/AuthController.java` | Login endpoint |
| `dto/LoginRequestDto.java` | Login request body shape |   











