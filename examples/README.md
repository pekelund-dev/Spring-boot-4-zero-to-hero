# Spring Boot 4.x Code Examples

This directory contains all the code examples referenced throughout the Spring Boot 4.x learning guide. Each example is a complete, runnable Spring Boot application demonstrating specific concepts and best practices.

## Structure

The examples are organized by chapter:

```
examples/
├── chapter-01-introduction/
│   └── hello-world/              # Simple Hello World application
├── chapter-04-first-application/
│   └── basic-app/                # Your first Spring Boot app
├── chapter-05-dependency-injection/
│   └── di-examples/              # DI patterns and examples
├── chapter-07-rest-services/
│   ├── todo-api/                 # Complete REST API
│   └── rest-best-practices/      # REST conventions
├── chapter-08-data-access/
│   ├── jpa-basics/               # JPA fundamentals
│   └── ecommerce-backend/        # Product catalog example
├── chapter-09-security/
│   ├── basic-security/           # Basic authentication
│   └── jwt-authentication/       # JWT implementation
├── chapter-10-testing/
│   └── testing-strategies/       # Testing examples
├── chapter-11-microservices/
│   ├── service-discovery/        # Eureka example
│   ├── api-gateway/              # Gateway pattern
│   └── config-server/            # Configuration management
├── chapter-12-observability/
│   └── monitoring-app/           # Actuator and metrics
└── chapter-13-advanced/
    ├── reactive-app/             # WebFlux example
    └── batch-processing/         # Spring Batch example
```

## How to Run the Examples

### Prerequisites

- Java 21 or higher
- Maven 3.8+ or Gradle 8+
- Your favorite IDE (IntelliJ IDEA, VS Code, or Eclipse)

### Running an Example

#### Using Maven:

```bash
cd examples/chapter-xx-name/project-name
./mvnw spring-boot:run
```

#### Using Gradle:

```bash
cd examples/chapter-xx-name/project-name
./gradlew bootRun
```

#### Using Your IDE:

1. Import the project into your IDE
2. Find the main application class (annotated with `@SpringBootApplication`)
3. Run it as a Java application

### Building a JAR:

```bash
# Maven
./mvnw clean package
java -jar target/project-name-0.0.1-SNAPSHOT.jar

# Gradle
./gradlew build
java -jar build/libs/project-name-0.0.1-SNAPSHOT.jar
```

## Example Projects

### Beginner Level

1. **hello-world** - Basic Spring Boot application
   - Location: `chapter-01-introduction/hello-world`
   - Concepts: @SpringBootApplication, REST endpoint

2. **basic-app** - Project structure and configuration
   - Location: `chapter-04-first-application/basic-app`
   - Concepts: Project organization, properties

### Intermediate Level

3. **todo-api** - Complete CRUD REST API
   - Location: `chapter-07-rest-services/todo-api`
   - Concepts: REST, JPA, validation, error handling

4. **ecommerce-backend** - Product catalog with authentication
   - Location: `chapter-08-data-access/ecommerce-backend`
   - Concepts: Complex data models, relationships, security

### Advanced Level

5. **microservices-demo** - Complete microservices system
   - Location: `chapter-11-microservices/`
   - Concepts: Service discovery, API gateway, config server

6. **reactive-app** - Reactive web application
   - Location: `chapter-13-advanced/reactive-app`
   - Concepts: WebFlux, reactive streams

## Testing the Examples

Each example includes comprehensive tests:

```bash
# Run all tests
./mvnw test          # Maven
./gradlew test       # Gradle

# Run specific test class
./mvnw test -Dtest=UserControllerTest
./gradlew test --tests UserControllerTest
```

## Database Configuration

Most examples use H2 in-memory database for simplicity. To use a different database:

1. Add the appropriate driver dependency
2. Update `application.yml` or `application.properties`
3. Configure connection settings

Example for PostgreSQL:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb
    username: postgres
    password: password
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
```

## API Documentation

Examples with REST APIs include:
- Swagger/OpenAPI documentation at `/swagger-ui.html`
- API documentation at `/api-docs`

## Common Issues and Solutions

### Port Already in Use

Change the port in `application.properties`:
```properties
server.port=8081
```

### Database Connection Issues

1. Check if the database is running
2. Verify connection settings
3. Check firewall rules

### Build Failures

1. Clean the build: `./mvnw clean` or `./gradlew clean`
2. Update dependencies: `./mvnw dependency:resolve`
3. Check Java version: `java -version`

## Contributing

Found an issue or want to improve an example? Contributions are welcome!

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## License

All examples are available under the MIT License. Feel free to use them in your own projects!

---

**Need Help?** Check the main [README](../README.md) or open an issue on GitHub.
