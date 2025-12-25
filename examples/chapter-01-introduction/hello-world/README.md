# Hello World Spring Boot Application

A simple Spring Boot 4.x application demonstrating basic concepts.

## What This Example Demonstrates

- `@SpringBootApplication` annotation
- Embedded Tomcat server
- REST endpoints with `@RestController`
- Path variables with `@PathVariable`
- Query parameters with `@RequestParam`
- Java 21 features (Records, Switch expressions)

## Running the Application

### Using Maven:
```bash
./mvnw spring-boot:run
```

### Using Java:
```bash
./mvnw clean package
java -jar target/hello-world-1.0.0.jar
```

## Testing the Endpoints

Once the application is running, try these endpoints:

### 1. Basic Hello World
```bash
curl http://localhost:8080/
# Output: Hello, Spring Boot 4.x! 🚀
```

### 2. Personalized Greeting with Path Variable
```bash
curl http://localhost:8080/hello/John
# Output: Hello, John! Welcome to Spring Boot 4.x!
```

### 3. Multi-language Greeting with Query Parameters
```bash
# English (default)
curl http://localhost:8080/greet?name=John
# Output: Hello, John!

# Spanish
curl "http://localhost:8080/greet?name=Maria&language=es"
# Output: ¡Hola, Maria!

# French
curl "http://localhost:8080/greet?name=Pierre&language=fr"
# Output: Bonjour, Pierre!

# German
curl "http://localhost:8080/greet?name=Hans&language=de"
# Output: Hallo, Hans!

# Japanese
curl "http://localhost:8080/greet?name=Yuki&language=ja"
# Output: こんにちは, Yuki!
```

### 4. Health Check
```bash
curl http://localhost:8080/health
# Output: {"status":"UP","message":"Hello World Application is running!"}
```

## Project Structure

```
hello-world/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/helloworld/
│   │   │       └── HelloWorldApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── pom.xml
└── README.md
```

## Key Concepts

### @SpringBootApplication
This annotation combines three annotations:
- `@Configuration`: Indicates this class can be used for bean definitions
- `@EnableAutoConfiguration`: Enables Spring Boot's auto-configuration
- `@ComponentScan`: Enables component scanning

### @RestController
Combines `@Controller` and `@ResponseBody`, making all methods return data rather than views.

### Record Classes (Java 21)
The `HealthResponse` uses a record, a compact way to create immutable data classes:
```java
record HealthResponse(String status, String message) {}
```

### Switch Expressions (Java 21)
Modern switch syntax with arrow notation and return values:
```java
return switch (language.toLowerCase()) {
    case "es" -> String.format("¡Hola, %s!", name);
    case "fr" -> String.format("Bonjour, %s!", name);
    default -> String.format("Hello, %s!", name);
};
```

## Exercises

1. **Add More Languages**: Extend the `greet` endpoint to support more languages
2. **Add a POST Endpoint**: Create an endpoint that accepts a JSON body
3. **Add Custom Port**: Modify `application.properties` to run on port 8081
4. **Add Request Logging**: Log every incoming request
5. **Add Error Handling**: Create a custom error response for invalid inputs

## Next Steps

- Add more REST endpoints
- Implement request validation
- Add unit tests
- Connect to a database
- Add authentication

## Related Chapters

- [Chapter 1: Introduction to Spring Boot 4.x](../../../chapters/01-introduction/README.md)
- [Chapter 7: RESTful Web Services](../../../chapters/07-rest-services/README.md)
