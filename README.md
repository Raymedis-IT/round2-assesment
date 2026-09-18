# InterviewMart

Lean Spring Boot 3 / Java 17 REST API (users, products, JWT access + refresh tokens). Intended for a live backend interview.

## Run

Requires **JDK 17** and Maven (or the included wrapper).

```bash
./mvnw spring-boot:run
```

Windows:

```bash
mvnw.cmd spring-boot:run
```

App starts on `http://localhost:8080` with an in-memory **H2** database.

- H2 console: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:interviewmart`
- User: `sa` / (empty password)

To use PostgreSQL, create a database named `interviewmart` and uncomment the PostgreSQL datasource settings in `src/main/resources/application.properties`.

### Seeded accounts

| Email | Password | Role |
| --- | --- | --- |
| `admin@interviewmart.com` | `Admin@123` | ADMIN |
| `jane@interviewmart.com` | `User@123` | USER |

## API

| Method | Path | Auth |
| --- | --- | --- |
| POST | `/auth/register` | public |
| POST | `/auth/login` | public |
| POST | `/auth/refresh` | public (refresh token) |
| POST | `/auth/logout` | public (refresh token body) |
| GET | `/products` | public |
| GET | `/products/{id}` | public |
| POST | `/products` | Bearer access token |
| PUT | `/products/{id}` | Bearer access token (creator or ADMIN) |
| DELETE | `/products/{id}` | Bearer access token (creator or ADMIN) |

## Sample requests

### Register

```bash
curl -s -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Alex\",\"email\":\"alex@example.com\",\"password\":\"secret12\"}"
```

### Login

```bash
curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"jane@interviewmart.com\",\"password\":\"User@123\"}"
```

### Refresh

```bash
curl -s -X POST http://localhost:8080/auth/refresh \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\":\"PASTE_REFRESH_TOKEN\"}"
```

### Logout

```bash
curl -s -X POST http://localhost:8080/auth/logout \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\":\"PASTE_REFRESH_TOKEN\"}"
```

### Products

```bash
curl -s http://localhost:8080/products

curl -s http://localhost:8080/products/1

curl -s -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer PASTE_ACCESS_TOKEN" \
  -d "{\"name\":\"Notebook\",\"description\":\"A5 dotted\",\"price\":6.99,\"category\":\"stationery\",\"stockQuantity\":25}"

curl -s -X PUT http://localhost:8080/products/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer PASTE_ACCESS_TOKEN" \
  -d "{\"name\":\"Mechanical Keyboard\",\"description\":\"Updated copy\",\"price\":79.99,\"category\":\"electronics\",\"stockQuantity\":10}"

curl -s -X DELETE http://localhost:8080/products/1 \
  -H "Authorization: Bearer PASTE_ACCESS_TOKEN"
```

Access tokens are short-lived (15 minutes). Refresh tokens last 7 days and are stored in the database.

## Suggested follow-up (if time remains)

Add one small feature, for example:

- `GET /products/search?category=electronics`
- Pagination on `GET /products` (`page`, `size`)
