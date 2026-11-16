# SpendLens Backend

A RESTful backend for **SpendLens**, a full-stack personal expense management application built using Java and Spring Boot.

The backend provides APIs for user authentication, expense management, and monthly budget tracking. It uses Spring Security and JWT for authentication, Spring Data JPA for database operations, and BCrypt for secure password encryption.

## 🔗 Related Repository

**Frontend:** [SpendLens – Flutter Application](https://github.com/TaranSingh-111/SpendLens)

## ✨ Features

### Authentication & Security
- User registration and login.
- JWT-based authentication and authorization.
- Password hashing using BCrypt.
- Secure endpoint protection using Spring Security.
- Token-based authentication for API requests.

### Expense Management
- Create, retrieve, and update expenses.
- Retrieve user-specific expense records.
- Organize expenses using categories.
- Store expense amounts, dates, and descriptions.
- Retrieve expenses for individual users.

### Budget Management
- Create and update monthly budgets.
- Retrieve budgets based on month and year.
- Maintain user-specific budget records.
- Track monthly spending against allocated budgets.

### Database Management
- Relational database integration.
- Entity mapping using Hibernate.
- Database operations using Spring Data JPA.
- User-specific data management.

## 🛠️ Tech Stack

| Category | Technologies |
|---|---|
| Language | Java |
| Backend Framework | Spring Boot |
| Security | Spring Security, JWT |
| Password Encryption | BCrypt |
| ORM | Hibernate |
| Database Access | Spring Data JPA |
| Database | SQL |
| API Architecture | RESTful APIs |
| Build Tool | Maven |
| API Testing | Postman |

## 🏗️ Architecture

The application follows a layered architecture to maintain separation of concerns and improve maintainability.

- **Controller Layer:** Handles incoming HTTP requests and returns API responses.
- **Service Layer:** Contains business logic and application operations.
- **Repository Layer:** Handles database interactions using Spring Data JPA.
- **Entity Layer:** Defines database entities and their relationships.
- **Security Layer:** Handles authentication, authorization, and JWT validation.

## 📂 Project Structure

```text
expense-tracker/
└── src/
    ├── main/
    │   ├── java/
    │   │   └── ...
    │   │       ├── controller/
    │   │       ├── service/
    │   │       ├── repository/
    │   │       ├── entity/
    │   │       ├── security/
    │   │       └── config/
    │   │
    │   └── resources/
    │       └── application.properties
    │
    └── test/
```

## 🚀 Getting Started

### Prerequisites

- Java JDK
- Apache Maven
- SQL database
- IntelliJ IDEA or another Java IDE
- Postman (optional, for API testing)

### 1. Clone the Repository

```bash
git clone https://github.com/TaranSingh-111/SpendLens-backend.git

cd SpendLens-backend/expense-tracker
```

### 2. Configure the Database

Configure your database connection in `application.properties`.

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/spendlens
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

Adjust the database URL and driver configuration according to your SQL database.

### 3. Configure JWT Secret

Set the JWT secret as an environment variable:

```text
JWT_SECRET=YOUR_SECRET_KEY
```

Keep database credentials and JWT secrets outside source control.

### 4. Run the Application

Using Maven:

```bash
mvn spring-boot:run
```

The backend will be available at:

```text
http://localhost:8080
```

## 🔌 API Overview

| Method | Endpoint | Description |
|---|---|---|
| POST | `/users` | User registration |
| POST | `/auth/...` | Authentication operations |
| GET | `/expenses` | Retrieve expenses |
| GET | `/expenses/{id}` | Retrieve expense by ID |
| POST | `/expenses` | Create an expense |
| PUT | `/expenses/{id}` | Update an expense |
| GET | `/budgets` | Retrieve monthly budget |
| PUT | `/budgets/current` | Create or update current budget |

Protected endpoints require valid authentication.

## 📱 Frontend Integration

The backend is integrated with a Flutter application using Dio for HTTP communication.

The Flutter application uses BLoC for state management and Clean Architecture to organize its presentation, domain, and data layers.

**Frontend Repository:** [SpendLens – Flutter](https://github.com/TaranSingh-111/SpendLens)

For Android Emulator development, the API base URL is:

```text
http://10.0.2.2:8080
```

## 🔮 Future Improvements

- Automated unit and integration testing.
- API documentation using Swagger/OpenAPI.
- Docker-based deployment.
- Advanced expense analytics.
- Improved exception handling and request validation.

## 👨‍💻 Author

**Taran Singh**

GitHub: [TaranSingh-111](https://github.com/TaranSingh-111)

---

<p align="center">
  <b>SpendLens Backend</b> — Powering smarter expense management.
</p>
