```markdown
# E-Commerce Backend API

A RESTful e-commerce backend built with **Spring Boot 3** — JWT auth, product/cart/order management, and Stripe payments.

---

## 🛠 Tech Stack

| Technology         | Purpose               |
| ------------------ | --------------------- |
| Java 17            | Language              |
| Spring Boot 3.5.13 | Framework             |
| Spring Security    | Auth & authorization  |
| Spring Data JPA    | ORM                   |
| H2 / MySQL         | Database (dev / prod) |
| JWT (jjwt)         | Token authentication  |
| Stripe SDK         | Payment processing    |
| Docker             | Containerization      |
| JUnit 5 + Mockito  | Testing               |

---

## 📊 Entity Relationships
```

User ──1:1──► Cart ──1:N──► CartItem ──N:1──► Product
│ ▲
└──1:N──► Order ──1:N──► OrderItem ──N:1─────────┘

````

---

## 🚀 Quick Start

### Option A — Local (H2 Database)

```bash
git clone https://github.com/yourusername/ecommerce-backend.git
cd ecommerce-backend
mvn clean install
mvn spring-boot:run
````

App runs at `http://localhost:8080` | H2 Console at `http://localhost:8080/h2-console`

### Option B — Docker (MySQL)

```bash
cp .env.example .env        # edit .env with your values
docker compose up --build -d
```

App runs at `http://localhost:8080` | MySQL at `localhost:3307`

### Stop Docker

```bash
docker compose down          # keep data
docker compose down -v       # remove data
```

---

## ⚙ Configuration

### `.env`

```env
APP_PORT=8080
SPRING_PROFILES_ACTIVE=docker

MYSQL_HOST=mysql
MYSQL_PORT=3306
MYSQL_DATABASE=ecommerce
MYSQL_ROOT_PASSWORD=rootpassword123
MYSQL_USER=ecommerce_user
MYSQL_PASSWORD=ecommerce_pass123

JWT_SECRET=ThisIsAVeryLongSecretKeyForJWTTokenGeneration123456!
JWT_EXPIRATION=86400000

STRIPE_API_KEY=sk_test_REPLACE_WITH_YOUR_KEY
```

### Profiles

| Mode      | Command                | DB    | Profile |
| --------- | ---------------------- | ----- | ------- |
| Local Dev | `mvn spring-boot:run`  | H2    | default |
| Docker    | `docker compose up -d` | MySQL | docker  |
| Tests     | `mvn test`             | H2    | default |

---

## 👤 Default Accounts

| Email             | Password | Role  |
| ----------------- | -------- | ----- |
| admin@example.com | admin123 | ADMIN |

3 sample products (Laptop, Smartphone, Headphones) are seeded on startup.

---

## 📡 API Reference

> **Base URL:** `http://localhost:8080`
> **Auth Header:** `Authorization: Bearer <token>`

### 1. Authentication (Public)

| Method | Endpoint             | Body                          |
| ------ | -------------------- | ----------------------------- |
| POST   | `/api/auth/register` | `{"name","email","password"}` |
| POST   | `/api/auth/login`    | `{"email","password"}`        |

**Response:** `{"token":"eyJ...","email":"...","role":"USER"}`

### 2. Products

| Method | Endpoint             | Auth | Role  |
| ------ | -------------------- | ---- | ----- |
| GET    | `/api/products`      | ❌   | Any   |
| GET    | `/api/products/{id}` | ❌   | Any   |
| POST   | `/api/products`      | ✅   | ADMIN |
| PUT    | `/api/products/{id}` | ✅   | ADMIN |
| DELETE | `/api/products/{id}` | ✅   | ADMIN |

**Product Body:**

```json
{
  "name": "Tablet",
  "description": "10 inch",
  "price": 399.99,
  "stockQuantity": 30,
  "imageUrl": "https://..."
}
```

### 3. Cart (Auth Required)

| Method | Endpoint                          | Description     |
| ------ | --------------------------------- | --------------- |
| GET    | `/api/cart`                       | View cart       |
| POST   | `/api/cart/items`                 | Add item        |
| PUT    | `/api/cart/items/{id}?quantity=3` | Update quantity |
| DELETE | `/api/cart/items/{id}`            | Remove item     |

**Add Item Body:** `{"productId":1,"quantity":2}`

### 4. Orders (Auth Required)

| Method | Endpoint               | Description            |
| ------ | ---------------------- | ---------------------- |
| POST   | `/api/orders/checkout` | Create order from cart |
| GET    | `/api/orders`          | List my orders         |
| GET    | `/api/orders/{id}`     | View order details     |

**Checkout Body:** `{"shippingAddress":"123 Main St"}`

### 5. Payments (Auth Required)

| Method | Endpoint            | Description   |
| ------ | ------------------- | ------------- |
| POST   | `/api/payments/pay` | Pay for order |

**Payment Body:** `{"orderId":1,"paymentMethodId":"pm_card_visa"}`

---

## 🧪 Full API Test Flow

```bash
# 1. Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"John","email":"john@test.com","password":"pass123"}'

# Save token from response
TOKEN="eyJ..."

# 2. Browse products
curl http://localhost:8080/api/products

# 3. Add to cart
curl -X POST http://localhost:8080/api/cart/items \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"productId":1,"quantity":2}'

# 4. View cart
curl http://localhost:8080/api/cart \
  -H "Authorization: Bearer $TOKEN"

# 5. Checkout
curl -X POST http://localhost:8080/api/orders/checkout \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"shippingAddress":"123 Main Street"}'

# 6. Pay
curl -X POST http://localhost:8080/api/payments/pay \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"orderId":1,"paymentMethodId":"pm_card_visa"}'

# 7. View orders
curl http://localhost:8080/api/orders \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🧪 Running Tests

```bash
mvn test                              # all tests
mvn test -Dtest=ProductServiceTest    # specific test
mvn test -Dtest=CartServiceTest
mvn test -Dtest=OrderServiceTest
mvn test -Dtest=AuthControllerTest
```

---

## 📁 Project Structure

```
├── .env / .env.example          # Environment variables
├── .gitignore / .dockerignore   # Ignore rules
├── Dockerfile                   # Multi-stage build
├── docker-compose.yml           # MySQL + App
├── docker/mysql/init.sql        # DB init script
├── pom.xml
└── src/main/java/com/ecommerce/
    ├── config/                  # StripeConfig, DataInitializer
    ├── controller/              # Auth, Product, Cart, Order, Payment
    ├── dto/                     # Request/Response DTOs
    ├── exception/               # Global error handler
    ├── model/                   # JPA entities
    ├── repository/              # Data access
    ├── security/                # JWT filter, SecurityConfig
    └── service/                 # Business logic
```

---

## 📊 HTTP Status Codes

| Code | When                                      |
| ---- | ----------------------------------------- |
| 200  | Successful GET, PUT, login, payment       |
| 201  | Successful POST (register, create, order) |
| 204  | Successful DELETE                         |
| 400  | Validation error, insufficient stock      |
| 401  | Bad credentials, missing/expired JWT      |
| 403  | Non-admin on admin endpoints              |
| 404  | Resource not found                        |

---

## 🔒 Security Summary

- **Passwords** → BCrypt hashed
- **Authentication** → JWT (24h expiry)
- **Sessions** → Stateless
- **Cart/Orders** → User-isolated (can only access own data)
- **Product CRUD** → Admin-only (POST/PUT/DELETE)
- **Payments** → Server-side Stripe (keys never exposed to client)
- **Docker** → Non-root container user

---

## 💳 Stripe Setup

1. Sign up at [stripe.com](https://dashboard.stripe.com/register)
2. Get test secret key from [API Keys](https://dashboard.stripe.com/test/apikeys)
3. Set `STRIPE_API_KEY=sk_test_...` in `.env` or `application.properties`

**Test payment methods:**

| Card           | ID                       |
| -------------- | ------------------------ |
| Visa (success) | `pm_card_visa`           |
| Declined       | `pm_card_chargeDeclined` |

---

## 🐳 Docker Commands

```bash
docker compose up --build -d          # build & start
docker compose logs -f app            # follow app logs
docker compose ps                     # list containers
docker compose restart app            # restart app
docker compose exec mysql mysql -u root -p   # mysql shell
docker compose down -v                # stop & remove everything
```

---

## 🔧 Troubleshooting

| Problem                  | Fix                                                |
| ------------------------ | -------------------------------------------------- |
| Port 8080 in use         | `server.port=8081` or kill existing process        |
| JWT signature mismatch   | Ensure consistent `JWT_SECRET` value               |
| 403 on product create    | Login as `admin@example.com`                       |
| Stripe invalid key       | Replace with your actual `sk_test_` key            |
| Cart not found           | Register a new user (cart auto-created)            |
| Lombok not working       | Enable annotation processing in IDE                |
| MySQL connection refused | Wait for health check: `docker compose logs mysql` |

---

n the original while keeping every essential piece of information — setup instructions, API reference, Docker commands, testing, security, and troubleshooting — all scannable at a glance.
