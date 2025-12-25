# Chapter 13: Advanced Topics

This chapter explores advanced Spring Boot concepts for experienced developers building production-ready applications.

## Reactive Programming with WebFlux

### Dependencies

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

### Reactive Controller

```java
@RestController
@RequestMapping("/api/users")
public class ReactiveUserController {
    
    private final ReactiveUserRepository repository;
    
    public ReactiveUserController(ReactiveUserRepository repository) {
        this.repository = repository;
    }
    
    @GetMapping
    public Flux<User> getAllUsers() {
        return repository.findAll();
    }
    
    @GetMapping("/{id}")
    public Mono<User> getUser(@PathVariable Long id) {
        return repository.findById(id);
    }
    
    @PostMapping
    public Mono<User> createUser(@RequestBody User user) {
        return repository.save(user);
    }
    
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<User> streamUsers() {
        return repository.findAll()
            .delayElements(Duration.ofSeconds(1));
    }
}
```

### Reactive Repository

```java
public interface ReactiveUserRepository extends ReactiveCrudRepository<User, Long> {
    Flux<User> findByUsername(String username);
    Mono<Boolean> existsByEmail(String email);
}
```

## Caching Strategies

### Enable Caching

```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
            "users", "products", "orders"
        );
    }
}
```

### Using Cache Annotations

```java
@Service
public class UserService {
    
    @Cacheable(value = "users", key = "#id")
    public User getUser(Long id) {
        // Expensive operation
        return repository.findById(id).orElseThrow();
    }
    
    @CachePut(value = "users", key = "#result.id")
    public User updateUser(User user) {
        return repository.save(user);
    }
    
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        repository.deleteById(id);
    }
    
    @CacheEvict(value = "users", allEntries = true)
    public void clearCache() {
        // Clear all user cache
    }
}
```

### Redis Cache

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

```yaml
spring:
  redis:
    host: localhost
    port: 6379
  cache:
    type: redis
    redis:
      time-to-live: 600000
```

## Async Processing

### Enable Async

```java
@Configuration
@EnableAsync
public class AsyncConfig {
    
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        return executor;
    }
}
```

### Async Methods

```java
@Service
public class NotificationService {
    
    @Async
    public CompletableFuture<Void> sendEmail(String to, String subject) {
        // Send email asynchronously
        return CompletableFuture.completedFuture(null);
    }
    
    @Async
    public void processInBackground(Order order) {
        // Long-running process
    }
}
```

## Scheduled Tasks

```java
@Configuration
@EnableScheduling
public class SchedulingConfig {
}

@Component
public class ScheduledTasks {
    
    // Every 5 seconds
    @Scheduled(fixedRate = 5000)
    public void reportStatus() {
        log.info("Status check at: {}", LocalDateTime.now());
    }
    
    // Every day at 2 AM
    @Scheduled(cron = "0 0 2 * * ?")
    public void performDailyCleanup() {
        log.info("Performing daily cleanup");
    }
    
    // 1 hour after last completion
    @Scheduled(fixedDelay = 3600000)
    public void processQueue() {
        // Process queued items
    }
}
```

## WebSockets

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

### Configuration

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").withSockJS();
    }
}
```

### Controller

```java
@Controller
public class ChatController {
    
    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(ChatMessage message) {
        return message;
    }
}
```

## GraphQL

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-graphql</artifactId>
</dependency>
```

### Schema

```graphql
# schema.graphqls
type Query {
    users: [User]
    user(id: ID!): User
}

type Mutation {
    createUser(username: String!, email: String!): User
}

type User {
    id: ID!
    username: String!
    email: String!
}
```

### Resolver

```java
@Controller
public class UserController {
    
    private final UserService userService;
    
    @QueryMapping
    public List<User> users() {
        return userService.findAll();
    }
    
    @QueryMapping
    public User user(@Argument Long id) {
        return userService.findById(id);
    }
    
    @MutationMapping
    public User createUser(@Argument String username, @Argument String email) {
        return userService.createUser(username, email);
    }
}
```

## Batch Processing

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-batch</artifactId>
</dependency>
```

### Job Configuration

```java
@Configuration
@EnableBatchProcessing
public class BatchConfig {
    
    @Bean
    public Job importUserJob(JobRepository jobRepository, Step step1) {
        return new JobBuilder("importUserJob", jobRepository)
            .start(step1)
            .build();
    }
    
    @Bean
    public Step step1(JobRepository jobRepository, 
                     PlatformTransactionManager transactionManager,
                     ItemReader<User> reader,
                     ItemProcessor<User, User> processor,
                     ItemWriter<User> writer) {
        return new StepBuilder("step1", jobRepository)
            .<User, User>chunk(10, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .build();
    }
}
```

## Native Images with GraalVM

### Configuration

```xml
<plugin>
    <groupId>org.graalvm.buildtools</groupId>
    <artifactId>native-maven-plugin</artifactId>
</plugin>
```

### Build

```bash
./mvnw -Pnative native:compile
```

### Hints

```java
@RegisterReflectionForBinding({User.class, UserDTO.class})
@SpringBootApplication
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

## Performance Optimization

### 1. Connection Pooling

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
```

### 2. Query Optimization

```java
// Use projections
public interface UserProjection {
    String getUsername();
    String getEmail();
}

public interface UserRepository extends JpaRepository<User, Long> {
    List<UserProjection> findAllProjectedBy();
}
```

### 3. Lazy Loading

```java
@Entity
public class User {
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders;
}
```

## Production Deployment

### Docker

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Kubernetes

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp
spec:
  replicas: 3
  selector:
    matchLabels:
      app: myapp
  template:
    metadata:
      labels:
        app: myapp
    spec:
      containers:
      - name: myapp
        image: myapp:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: prod
        resources:
          limits:
            memory: "512Mi"
            cpu: "500m"
```

## Summary

You learned:
- Reactive programming with WebFlux
- Caching strategies
- Async processing
- Scheduled tasks
- WebSockets
- GraphQL
- Batch processing
- Native images
- Performance optimization
- Production deployment

---

## 🏋️ Exercises

**Exercise 1**: Build reactive API  
**Exercise 2**: Implement caching  
**Exercise 3**: Create async tasks  
**Exercise 4**: Add scheduled jobs  
**Exercise 5**: Deploy to Docker

## 🎯 Quiz

**Q1**: Reactive programming framework?  
✅ WebFlux

**Q2**: Enable caching with?  
✅ @EnableCaching

**Q3**: Async processing annotation?  
✅ @Async

**Q4**: Scheduled tasks annotation?  
✅ @Scheduled

**Q5**: Native image tool?  
✅ GraalVM

---

**Previous**: [← Chapter 12: Observability](../12-observability/README.md)  
**Next**: [Appendices →](../../appendices/best-practices.md)
