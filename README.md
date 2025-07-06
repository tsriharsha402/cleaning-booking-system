# Cleaning Booking System (Local)

This is a simple Spring Boot-based booking management system for scheduling cleaning appointments with available cleaners and vehicles. It includes APIs to check availability, book slots, and manage cleaner assignments.

---

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.6+
- IDE (e.g., IntelliJ, VS Code)
- (Optional) Postman or Swagger for API testing

---

## Run Locally

```bash
# Clone the repo
git clone <repo-url>

# Navigate into project
cd cleaning_service

# Run the application
./mvnw spring-boot:run
```
---
**Note:** Set your PostgreSQL credentials via environment variables:
```bash
export DB_USERNAME=db_username
export DB_PASSWORD=db_password
```
## Running Tests

```bash
# Unit & functional tests
./mvnw test
```

Unit and controller-level functional tests are included for:
- BookingService
- AvailabilityService
- CleanerService
- BookingController
- CleanerController
---

## API Documentation (Swagger)

Visit:
```
http://localhost:8080/swagger-ui.html
```
---

## API Endpoints
| Method | Endpoint                       | Description                          |
|--------|--------------------------------|--------------------------------------|
| GET    | `/api/v1/availability`         | Get daily availability by date       |
| GET    | `/api/v1/availability/slot`    | Get available cleaners for time slot |
| POST   | `/api/v1/bookings`             | Create a booking                     |
| PATCH  | `/api/v1/bookings/{id}`        | Update a booking                     |
| GET    | `/api/v1/bookings/{id}`        | Get booking details                  |
| DELETE | `/api/v1/bookings/{id}`        | Delete a booking                     |
| GET    | `/api/v1/cleaners/available`   | Get available cleaners (raw)         |
---

## Technologies Used
- Spring Boot 3.x
- Spring Web & Data JPA
- PostgreSQL
- Swagger (springdoc-openapi)
- JUnit 5 & Mockito for testing