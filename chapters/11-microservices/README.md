# Chapter 11: Microservices Architecture

Microservices architecture allows building scalable, independently deployable services. This chapter introduces microservices patterns with Spring Boot and Spring Cloud.

## Microservices Principles

### Characteristics

- **Independent deployment**
- **Single responsibility**
- **Decentralized data**
- **Failure isolation**
- **Technology diversity**

### When to Use

✅ **Use when**:
- Large, complex applications
- Multiple teams
- Need independent scaling
- Different technologies needed

❌ **Avoid when**:
- Small applications
- Single team
- Simple requirements
- Limited resources

## Service Discovery with Eureka

### Eureka Server

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

```yaml
server:
  port: 8761

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

### Eureka Client

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

```yaml
spring:
  application:
    name: user-service

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

## API Gateway

### Spring Cloud Gateway

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://USER-SERVICE
          predicates:
            - Path=/users/**
        - id: order-service
          uri: lb://ORDER-SERVICE
          predicates:
            - Path=/orders/**
```

## Configuration Management

### Config Server

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```

```java
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
```

```yaml
server:
  port: 8888

spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/yourorg/config-repo
          default-label: main
```

### Config Client

```yaml
spring:
  application:
    name: user-service
  cloud:
    config:
      uri: http://localhost:8888
      fail-fast: true
```

## Circuit Breaker with Resilience4j

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
</dependency>
```

```java
@Service
public class UserService {
    
    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackGetUser")
    public User getUser(Long id) {
        return restTemplate.getForObject(
            "http://user-service/users/" + id, 
            User.class
        );
    }
    
    private User fallbackGetUser(Long id, Exception e) {
        return new User(id, "Fallback User", "fallback@example.com");
    }
}
```

```yaml
resilience4j:
  circuitbreaker:
    instances:
      userService:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10000
```

## Inter-Service Communication

### REST with RestTemplate

```java
@Configuration
public class RestConfig {
    
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

@Service
@RequiredArgsConstructor
public class OrderService {
    private final RestTemplate restTemplate;
    
    public User getUser(Long userId) {
        return restTemplate.getForObject(
            "http://USER-SERVICE/users/" + userId,
            User.class
        );
    }
}
```

### OpenFeign

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

```java
@FeignClient(name = "user-service")
public interface UserClient {
    
    @GetMapping("/users/{id}")
    User getUser(@PathVariable Long id);
    
    @PostMapping("/users")
    User createUser(@RequestBody UserRequest request);
}

@Service
@RequiredArgsConstructor
public class OrderService {
    private final UserClient userClient;
    
    public User getUser(Long id) {
        return userClient.getUser(id);
    }
}
```

## Distributed Tracing

### Zipkin

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-zipkin</artifactId>
</dependency>
```

```yaml
spring:
  zipkin:
    base-url: http://localhost:9411
  sleuth:
    sampler:
      probability: 1.0
```

## Event-Driven Architecture

### Spring Cloud Stream

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-stream-kafka</artifactId>
</dependency>
```

```java
@Component
public class EventPublisher {
    
    @Autowired
    private StreamBridge streamBridge;
    
    public void publishUserCreated(User user) {
        streamBridge.send("user-created", user);
    }
}

@Component
public class EventConsumer {
    
    @StreamListener("user-created")
    public void handleUserCreated(User user) {
        // Process event
    }
}
```

## Summary

You learned:
- Microservices principles
- Service discovery with Eureka
- API Gateway pattern
- Configuration management
- Circuit breakers
- Inter-service communication
- Event-driven architecture

---

## 🏋️ Exercises

**Exercise 1**: Create Eureka server and clients  
**Exercise 2**: Implement API Gateway  
**Exercise 3**: Add circuit breakers  
**Exercise 4**: Use Feign for inter-service calls  
**Exercise 5**: Implement event-driven communication

## 🎯 Quiz

**Q1**: Service discovery tool?  
✅ Eureka

**Q2**: Pattern for fault tolerance?  
✅ Circuit Breaker

**Q3**: Inter-service communication tool?  
✅ OpenFeign

**Q4**: Configuration management?  
✅ Config Server

**Q5**: Distributed tracing tool?  
✅ Zipkin

---

**Previous**: [← Chapter 10: Testing](../10-testing/README.md)  
**Next**: [Chapter 12: Observability →](../12-observability/README.md)
