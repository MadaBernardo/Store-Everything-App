# 🚀 StoreEverything - Enterprise Web Application

StoreEverything is a robust, secure, and fully-featured web application built with **Spring Boot** for efficient information lifecycle management. The platform allows users to dynamically create, categorize, filter, and share notes within a highly secure environment.

The core architectural highlight of this project is its **hybrid session-persistence pipeline**: user data operations are managed inside an active, volatile memory cache (`HttpSession`) and are atomically synchronized (*flushed*) to a persistent **MySQL database** only when the user gracefully terminates their session (Logout).

---

## ✨ Key Features

### 🔄 Hybrid State Persistence

Implements a localized transient memory buffer utilizing `HttpSession` to isolate active structural mutations (Create, Update, Delete) from the relational database layer until explicit logout boundaries are triggered.

### 📝 Dynamic Category & Note Management

Complete CRUD lifecycles for custom notes with strict form data validations.

### 🔍 Advanced Query Engine

Dashboard sorting and filtering pipelines driven by Java Streams, with multi-layered parameter fallback mechanisms leveraging browser Cookies to persist user layout preferences across 24-hour lifespans.

### 🔒 Enterprise-Grade Security

Robust authentication and authorization powered by **Spring Security**, utilizing `BCryptPasswordEncoder` for secure password hashing and custom role-based access control:

* `ADMIN`
* `USER`
* `FULL_USER`

### 📊 Administrative Telemetry

Dedicated administrative routing corridors (`/admin/**`) for global ecosystem monitoring and account access control management.

---

## 🏗️ Architectural Layout (MVC Layers)

The codebase strictly follows the **Layered Architecture (MVC Pattern)**, decoupling infrastructure configurations, persistent domain structures, and presentation interfaces into distinct packages:

```text
src/main/
├── java/com/project/storeeverything/
│   ├── controllers/    # Presentation Layer: Intercepts HTTP Requests & binds routing paths
│   ├── entities/       # Domain Layer: ORM structures (The Model)
│   ├── repositories/   # Data Access Objects (DAO): JPA Query interfaces
│   ├── services/       # Business Logic Layer: Session cache algorithms
│   └── security/       # Security Infrastructure: Access control firewalls
└── resources/
    ├── static/         # CSS styles and static assets
    └── templates/      # Thymeleaf HTML templates (The View)
```

---

## 🛠️ Technology Stack

| Layer             | Technology                  |
| ----------------- | --------------------------- |
| Backend Framework | Spring Boot                 |
| Web Layer         | Spring MVC                  |
| Security          | Spring Security             |
| ORM               | Spring Data JPA (Hibernate) |
| Database          | MySQL Server                |
| Template Engine   | Thymeleaf                   |
| Build Tool        | Maven                       |
| Productivity      | Lombok                      |
| Language          | Java 17                     |

---

## 🔧 Core Mechanics Deep Dive

### 1. Session-to-Database Flush Pipeline

To minimize active transactional overhead on the relational database, a custom lifecycle strategy was implemented:

1. Active mutations are written to a localized `ArrayList`.
2. The collection is mapped against a unique `SESSION_KEY` stored inside the client's `HttpSession`.
3. Upon logout, a custom `addLogoutHandler` configured inside `WebSecurityConfig` intercepts the request.
4. The handler extracts the active session data.
5. The method:

```java
informationService.flushSessionToDatabase(session);
```

is executed to synchronize all pending changes with MySQL.

6. The session is invalidated after a successful flush operation.

This approach creates a hybrid persistence model that separates active user interactions from direct database transactions.

---

### 2. Cookie Fallback Mechanism

The dashboard controller implements an intelligent data-binding lookup algorithm:

#### Step 1: Request Parameter Lookup

The controller scans incoming HTTP requests for explicit filtering parameters using:

```java
@RequestParam
```

#### Step 2: Cookie Recovery

If no parameters are found, it reconstructs the previous dashboard state through:

```java
@CookieValue
```

#### Step 3: Context Refresh

Updated preferences are written back to the browser through:

```java
cookie.setMaxAge(86400);
response.addCookie(cookie);
```

This mechanism preserves user dashboard preferences for 24 hours.

---

## 🚦 Getting Started

### Prerequisites

* Java Development Kit (JDK) 17+
* Apache Maven 3.8+
* MySQL Server 8.0+

---

## 🗄️ Database Configuration

Create a new database schema:

```sql
CREATE DATABASE store_everything;
```

Configure your credentials inside:

```text
src/main/resources/application.properties
```

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/storeeverything_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
spring.jpa.hibernate.ddl-auto=update
```

---

## ▶️ Running the Application

Navigate to the project root directory and execute:

```bash
mvn spring-boot:run
```

The embedded Tomcat server will start and expose the application at:

```text
http://localhost:8080/
```

---

## 🔒 Security Mappings Reference

| Endpoint Route                        | Access Rule                        | Description                       |
| ------------------------------------- | ---------------------------------- | --------------------------------- |
| `/`, `/login`, `/register`, `/css/**` | `permitAll()`                      | Public pages and static resources |
| `/dashboard/**`                       | `authenticated()`                  | Main user dashboard               |
| `/information/**`, `/categories/**`   | `hasAnyRole('ADMIN', 'FULL_USER')` | Core information management       |
| `/admin/**`                           | `hasRole('ADMIN')`                 | Administrative operations         |

---

## 📚 Academic Context

This project was developed as part of a university coursework assignment focused on:

* Enterprise Java Web Development
* Spring Boot Ecosystem
* Session Management Strategies
* MVC Architecture
* Authentication & Authorization
* Database Persistence Patterns

The application explores state persistence paradigms and enterprise security architectures within the Spring ecosystem.

---

## 📄 License & Credits

### Developed By

**Madalena Bernardo**

### License

Licensed under the **MIT License**.

### Project Type

Semester Project for **Web Applications Development in Java** (Erasmus Program).


```
```
