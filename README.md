# Spring Boot 4.x - Zero to Hero 🚀

Welcome to the ultimate Spring Boot 4.x learning guide! This comprehensive resource is designed to take you from a complete beginner to a Spring Boot expert. Whether you're just starting your journey or looking to master advanced concepts, this guide has something for everyone.

> **Note**: This guide is forward-looking and prepares you for Spring Boot 4.x based on current Spring Boot patterns and the announced roadmap. While Spring Boot 4.0 has not been officially released at the time of writing, this resource focuses on modern Spring Boot development practices, Java 21+ features, and anticipated enhancements that will benefit current Spring Boot 3.x developers and prepare them for future versions.

## About This Guide

This learning site focuses on **modern Spring Boot development** with forward-looking content for 4.x. Each chapter includes:
- 📚 Comprehensive explanations and concepts
- 💻 Practical, hands-on examples
- 🏋️ Coding exercises to reinforce learning
- 🎯 Quizzes to test your knowledge
- ✅ Best practices and industry standards

All code examples are available in the [examples directory](./examples) for easy access and experimentation.

## Table of Contents

### Part I: Getting Started

#### [Chapter 1: Introduction to Spring Boot 4.x](./chapters/01-introduction/README.md)
- What is Spring Boot?
- Why Spring Boot 4.x?
- Key Features and Improvements in 4.x
- The Spring Ecosystem
- When to Use Spring Boot
- **Exercises & Quiz**

#### [Chapter 2: Setting Up Your Development Environment](./chapters/02-setup/README.md)
- Installing Java 21+ (LTS)
- Choosing Your IDE (IntelliJ IDEA, VS Code, Eclipse)
- Maven vs Gradle
- Spring Initializr
- Project Structure Best Practices
- Essential Tools and Plugins
- **Exercises & Quiz**

#### [Chapter 3: Core Concepts and Architecture](./chapters/03-core-concepts/README.md)
- Spring Framework Fundamentals
- Inversion of Control (IoC)
- Dependency Injection (DI)
- Spring Boot Auto-Configuration
- Component Scanning
- Bean Lifecycle
- ApplicationContext
- **Exercises & Quiz**

### Part II: Building Applications

#### [Chapter 4: Building Your First Application](./chapters/04-first-application/README.md)
- Creating a Spring Boot Project
- Understanding the Main Application Class
- Running Your Application
- Hot Reload with DevTools
- Project Structure Deep Dive
- Packaging and Running JAR Files
- **Exercises & Quiz**

#### [Chapter 5: Dependency Injection and IoC Container](./chapters/05-dependency-injection/README.md)
- Understanding Dependency Injection
- @Component, @Service, @Repository, @Controller
- Constructor vs Field vs Setter Injection
- @Autowired and @Qualifier
- Bean Scopes
- Conditional Beans
- Configuration Classes with @Bean
- **Exercises & Quiz**

#### [Chapter 6: Spring Boot Configuration](./chapters/06-configuration/README.md)
- application.properties vs application.yml
- Profile-Specific Configuration
- @ConfigurationProperties
- Environment Variables
- Externalized Configuration
- Custom Property Sources
- Configuration Validation
- **Exercises & Quiz**

### Part III: Web Development

#### [Chapter 7: RESTful Web Services](./chapters/07-rest-services/README.md)
- REST Principles
- @RestController and @RequestMapping
- HTTP Methods (GET, POST, PUT, DELETE, PATCH)
- Request and Response Handling
- Path Variables and Query Parameters
- Request/Response Body Mapping
- Exception Handling with @ControllerAdvice
- HATEOAS
- API Versioning
- **Exercises & Quiz**

#### [Chapter 8: Data Access with Spring Data JPA](./chapters/08-data-access/README.md)
- Introduction to Spring Data JPA
- Entity Modeling with @Entity
- Repository Pattern
- CRUD Operations
- Custom Queries with @Query
- Query Methods and Naming Conventions
- Pagination and Sorting
- Transactions with @Transactional
- Database Migrations with Flyway/Liquibase
- **Exercises & Quiz**

#### [Chapter 9: Security with Spring Security](./chapters/09-security/README.md)
- Spring Security Fundamentals
- Authentication vs Authorization
- Configuring Security
- User Details and User Details Service
- Password Encoding
- Method-Level Security
- JWT Authentication
- OAuth 2.0 and OpenID Connect
- CORS Configuration
- Security Best Practices
- **Exercises & Quiz**

### Part IV: Testing and Quality

#### [Chapter 10: Testing Spring Boot Applications](./chapters/10-testing/README.md)
- Testing Strategy and Pyramid
- Unit Testing with JUnit 5
- Mocking with Mockito
- @SpringBootTest
- @WebMvcTest for Controllers
- @DataJpaTest for Repositories
- Integration Testing
- Test Containers
- Testing REST APIs
- Code Coverage with JaCoCo
- **Exercises & Quiz**

### Part V: Advanced Topics

#### [Chapter 11: Microservices Architecture](./chapters/11-microservices/README.md)
- Microservices Principles
- Service Discovery with Eureka
- API Gateway with Spring Cloud Gateway
- Configuration Management with Config Server
- Circuit Breakers with Resilience4j
- Distributed Tracing
- Inter-Service Communication
- Event-Driven Architecture with Spring Cloud Stream
- **Exercises & Quiz**

#### [Chapter 12: Observability and Monitoring](./chapters/12-observability/README.md)
- Spring Boot Actuator
- Health Checks and Metrics
- Prometheus Integration
- Grafana Dashboards
- Distributed Tracing with Micrometer
- Logging Best Practices
- Application Performance Monitoring (APM)
- OpenTelemetry
- **Exercises & Quiz**

#### [Chapter 13: Advanced Topics](./chapters/13-advanced/README.md)
- Reactive Programming with WebFlux
- Caching Strategies
- Async Processing
- Scheduled Tasks
- WebSockets
- GraphQL with Spring
- Batch Processing with Spring Batch
- Native Images with GraalVM
- Performance Optimization
- Production Deployment Strategies
- **Exercises & Quiz**

### Part VI: Appendices

#### [Appendix A: Best Practices Checklist](./appendices/best-practices.md)
#### [Appendix B: Common Pitfalls and Solutions](./appendices/common-pitfalls.md)
#### [Appendix C: Useful Resources](./appendices/resources.md)
#### [Appendix D: Migration Guide from Spring Boot 3.x](./appendices/migration-guide.md)

## 🎮 Interactive Learning Platform

**NEW!** Experience the course through our interactive web-based learning platform!

✨ **Features:**
- 🔐 Google OAuth authentication
- ⚙️ Personalized learning paths based on your environment (Mac/Windows/Linux, Maven/Gradle, Java version, IDE)
- 💻 Interactive code exercises with instant feedback
- ❓ Chapter quizzes to test your knowledge
- 🏆 Badge system and leaderboard for gamification
- 📊 Progress tracking across all chapters
- 🎯 Customized code examples based on your preferences

**[Get Started with the Interactive Platform →](./interactive-platform/)**

Run locally:
```bash
cd interactive-platform
mvn spring-boot:run
# Open http://localhost:8080
```

## How to Use This Guide

1. **Beginners**: Start from Chapter 1 and work your way through sequentially
2. **Intermediate**: Jump to specific chapters that interest you
3. **Advanced**: Focus on Part V and VI for advanced concepts
4. **Practice**: Complete all exercises and quizzes for hands-on experience
5. **Experiment**: Clone the examples and modify them to deepen understanding
6. **Interactive Mode**: Use the [interactive platform](./interactive-platform/) for a guided, gamified learning experience

## Prerequisites

- Basic understanding of Java programming
- Familiarity with object-oriented programming concepts
- Eagerness to learn!

## Contributing

Found an error or want to improve the content? Contributions are welcome! Please open an issue or submit a pull request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

---

**Ready to become a Spring Boot hero?** Let's start with [Chapter 1: Introduction to Spring Boot 4.x](./chapters/01-introduction/README.md)! 🎉