# AssetSync Backend

Spring Boot REST API for the AssetSync university resource allocation system. It manages equipment inventory, room requests, scheduling allocations, and conflict alternatives.

## Tech stack

- Java 17
- Spring Boot
- Spring Data JPA / Hibernate
- MySQL
- Jakarta Bean Validation
- Maven

## Requirements

- JDK 17+
- MySQL 8+

## Local setup

1. Create a MySQL database, for example `AssetSync_db`.
2. Configure the following environment variables before starting the application:

   | Variable | Description | Local default |
   |---|---|---|
   | `DB_URL` | JDBC connection URL | `jdbc:mysql://localhost:3306/AssetSync_db?serverTimezone=UTC&createDatabaseIfNotExist=true` |
   | `DB_USERNAME` | Database username | `root` |
   | `DB_PASSWORD` | Database password | empty |
   | `JPA_DDL_AUTO` | Hibernate schema mode | `update` |
   | `PORT` | HTTP port | `8085` |

3. Run the application:

   ```bash
   ./mvnw spring-boot:run
   ```

For production, use a dedicated database user, a managed secret store, TLS for database connections, and a versioned migration tool. Do not use the local defaults in production.

## API routes

- `GET /api/resources` — list resources
- `GET /api/resources?type=LOCATION` — filter by resource type
- `POST /api/equipment/book` — book equipment
- `POST /api/locations/request` — submit a location request
- `POST /api/locations/process-batch` — process pending location requests

## Validation and concurrency

Booking request payloads are validated with Jakarta Bean Validation. Equipment inventory changes run inside a transaction and lock the resource row to prevent concurrent requests from allocating the same remaining stock. Location batch processing is transactional and locks candidate resource rows before checking overlaps and creating allocations.

## Tests

Run the test suite:

```bash
./mvnw test
```

GitHub Actions runs the Maven verification lifecycle against a temporary MySQL service for pull requests and pushes to the default development branches.

## Security note

The current API does not yet implement user authentication or role-based authorization. Do not expose write endpoints publicly or treat requester-supplied names/roles as verified identity. Add an authentication provider and server-side authorization before production deployment.


## Security status

This academic API currently does not implement user authentication or server-side role authorization. The booking and batch-processing endpoints must not be exposed to untrusted networks as a production service until identity verification, resource-level authorization, and administrator-only batch processing are implemented. Frontend role checks are not a security boundary.
