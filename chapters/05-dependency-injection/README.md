# Chapter 5: Dependency Injection and IoC Container

Dependency Injection (DI) is the heart of Spring Boot. This chapter explores how to effectively use DI to build loosely coupled, testable applications.

## Understanding Dependency Injection

### The Problem Without DI

```java
public class OrderService {
    // Tight coupling - hard to test, inflexible
    private PaymentService paymentService = new PaymentService();
    private EmailService emailService = new EmailService();
    
    public void processOrder(Order order) {
        paymentService.charge(order);
        emailService.sendConfirmation(order);
    }
}
```

**Problems**:
- Can't mock dependencies for testing
- Hard to change implementations
- OrderService creates its own dependencies

### Solution: Dependency Injection

```java
@Service
public class OrderService {
    private final PaymentService paymentService;
    private final EmailService emailService;
    
    // Dependencies injected via constructor
    public OrderService(PaymentService paymentService, EmailService emailService) {
        this.paymentService = paymentService;
        this.emailService = emailService;
    }
    
    public void processOrder(Order order) {
        paymentService.charge(order);
        emailService.sendConfirmation(order);
    }
}
```

**Benefits**:
- Loose coupling
- Easy to test (inject mocks)
- Flexible (change implementations)
- Dependencies are clear

## Stereotype Annotations

### @Component

Generic stereotype for any Spring-managed component:

```java
@Component
public class EmailValidator {
    public boolean isValid(String email) {
        return email != null && email.contains("@");
    }
}
```

### @Service

Marks business logic layer:

```java
@Service
public class UserService {
    private final UserRepository repository;
    
    public UserService(UserRepository repository) {
        this.repository = repository;
    }
    
    public User registerUser(String username, String email) {
        // Business logic here
        User user = new User(username, email);
        return repository.save(user);
    }
}
```

### @Repository

Marks data access layer:

```java
@Repository
public class UserRepository {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    
    public User save(User user) {
        users.put(user.getId(), user);
        return user;
    }
    
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }
}
```

### @Controller and @RestController

Marks web layer:

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping
    public User registerUser(@RequestBody UserRequest request) {
        return userService.registerUser(request.username(), request.email());
    }
}
```

## Types of Injection

### 1. Constructor Injection (Recommended)

```java
@Service
public class OrderService {
    private final PaymentService paymentService;
    private final EmailService emailService;
    
    // @Autowired optional for single constructor
    public OrderService(PaymentService paymentService, EmailService emailService) {
        this.paymentService = paymentService;
        this.emailService = emailService;
    }
}
```

**Advantages**:
- Immutable (using `final`)
- Required dependencies are obvious
- Easy to test
- Prevents circular dependencies

### 2. Setter Injection

```java
@Service
public class NotificationService {
    private EmailService emailService;
    
    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
}
```

**Use when**:
- Dependency is optional
- Need to reconfigure after construction

### 3. Field Injection (Not Recommended)

```java
@Service
public class UserService {
    @Autowired  // Avoid this!
    private UserRepository repository;
}
```

**Why avoid**:
- Can't use `final`
- Hides dependencies
- Makes testing harder
- Can lead to NullPointerException

## Using Lombok for Cleaner Code

### @RequiredArgsConstructor

```java
@Service
@RequiredArgsConstructor  // Generates constructor for final fields
public class UserService {
    private final UserRepository repository;
    private final EmailService emailService;
    
    // Constructor generated automatically
    
    public User createUser(String username) {
        User user = new User(username);
        return repository.save(user);
    }
}
```

### @AllArgsConstructor

```java
@Service
@AllArgsConstructor  // Constructor for all fields
public class ProductService {
    private final ProductRepository repository;
    private final PriceCalculator calculator;
    private boolean debugMode;  // Non-final also included
}
```

## Working with Multiple Implementations

### Using @Qualifier

```java
// Two implementations of the same interface
public interface PaymentProcessor {
    void process(Payment payment);
}

@Component("creditCard")
public class CreditCardProcessor implements PaymentProcessor {
    public void process(Payment payment) {
        // Credit card logic
    }
}

@Component("paypal")
public class PayPalProcessor implements PaymentProcessor {
    public void process(Payment payment) {
        // PayPal logic
    }
}

// Specify which one to inject
@Service
public class PaymentService {
    private final PaymentProcessor processor;
    
    public PaymentService(@Qualifier("creditCard") PaymentProcessor processor) {
        this.processor = processor;
    }
}
```

### Using @Primary

```java
@Component
@Primary  // Use this one by default
public class DefaultPaymentProcessor implements PaymentProcessor {
    // Default implementation
}

@Component
public class SpecialPaymentProcessor implements PaymentProcessor {
    // Alternative implementation
}

@Service
public class PaymentService {
    // Injects DefaultPaymentProcessor
    private final PaymentProcessor processor;
    
    public PaymentService(PaymentProcessor processor) {
        this.processor = processor;
    }
}
```

## Bean Scopes

### Singleton (Default)

```java
@Service
@Scope("singleton")  // Default, can omit
public class UserService {
    // One instance for entire application
}
```

### Prototype

```java
@Service
@Scope("prototype")
public class ReportGenerator {
    // New instance created each time it's requested
}
```

### Request and Session (Web)

```java
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, 
       proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestContext {
    // New instance per HTTP request
}

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION,
       proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ShoppingCart {
    // One instance per user session
}
```

## Configuration Classes

### @Configuration and @Bean

```java
@Configuration
public class AppConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

### Injecting Configuration Beans

```java
@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    
    public UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    
    public User registerUser(String username, String password) {
        String encoded = passwordEncoder.encode(password);
        return new User(username, encoded);
    }
}
```

## Conditional Bean Creation

### @ConditionalOnProperty

```java
@Configuration
public class CacheConfig {
    
    @Bean
    @ConditionalOnProperty(name = "cache.enabled", havingValue = "true")
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("users", "products");
    }
}
```

### @ConditionalOnMissingBean

```java
@Configuration
public class DefaultConfig {
    
    @Bean
    @ConditionalOnMissingBean  // Only if no other EmailService exists
    public EmailService emailService() {
        return new DefaultEmailService();
    }
}
```

### @ConditionalOnClass

```java
@Configuration
public class RedisConfig {
    
    @Bean
    @ConditionalOnClass(name = "redis.clients.jedis.Jedis")
    public RedisCache redisCache() {
        return new RedisCache();
    }
}
```

## Profile-Specific Beans

```java
@Configuration
public class DataSourceConfig {
    
    @Bean
    @Profile("dev")
    public DataSource devDataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .build();
    }
    
    @Bean
    @Profile("prod")
    public DataSource prodDataSource() {
        // Production database configuration
        return new HikariDataSource();
    }
}
```

Activate with:
```properties
spring.profiles.active=dev
```

Or command line:
```bash
java -jar app.jar --spring.profiles.active=prod
```

## Circular Dependencies

### Problem

```java
@Service
public class ServiceA {
    private final ServiceB serviceB;
    
    public ServiceA(ServiceB serviceB) {
        this.serviceB = serviceB;
    }
}

@Service
public class ServiceB {
    private final ServiceA serviceA;
    
    public ServiceB(ServiceA serviceA) {
        this.serviceA = serviceA;
    }
}
// ERROR: Circular dependency!
```

### Solutions

**1. Redesign** (Best):
```java
// Extract common logic to new service
@Service
public class SharedService {
    // Common functionality
}

@Service
public class ServiceA {
    private final SharedService sharedService;
    
    public ServiceA(SharedService sharedService) {
        this.sharedService = sharedService;
    }
}
```

**2. Use @Lazy**:
```java
@Service
public class ServiceA {
    private final ServiceB serviceB;
    
    public ServiceA(@Lazy ServiceB serviceB) {
        this.serviceB = serviceB;
    }
}
```

## Best Practices

### ✅ Do

1. **Use constructor injection**
```java
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
}
```

2. **Use final fields**
```java
private final UserRepository repository;  // Immutable
```

3. **Keep dependencies clear**
```java
// All dependencies visible in constructor
public UserService(UserRepository repo, EmailService email) {
    // ...
}
```

### ❌ Don't

1. **Avoid field injection**
```java
@Autowired  // Don't do this
private UserRepository repository;
```

2. **Don't create circular dependencies**
```java
// Service A depends on B, B depends on A - redesign!
```

3. **Don't inject too many dependencies**
```java
// If you need 7+ dependencies, your class is doing too much
public UserService(Dep1 d1, Dep2 d2, ... Dep8 d8) {
    // Split this class!
}
```

## Summary

You learned:
- Dependency Injection principles
- Stereotype annotations
- Constructor vs setter vs field injection
- Using Lombok for cleaner code
- Working with multiple implementations
- Bean scopes
- Configuration classes
- Conditional beans
- Best practices

---

## 🏋️ Exercises

**Exercise 1**: Create payment system with multiple processors  
**Exercise 2**: Build notification service with email and SMS  
**Exercise 3**: Implement caching with conditional beans  
**Exercise 4**: Create profile-specific configurations  
**Exercise 5**: Refactor code to use constructor injection

## 🎯 Quiz

**Q1**: Recommended injection type?  
✅ Constructor injection

**Q2**: Annotation for business logic?  
✅ @Service

**Q3**: Default bean scope?  
✅ Singleton

**Q4**: How to choose specific implementation?  
✅ @Qualifier

**Q5**: Make a bean default choice?  
✅ @Primary

---

**Previous**: [← Chapter 4: Building Your First Application](../04-first-application/README.md)  
**Next**: [Chapter 6: Spring Boot Configuration →](../06-configuration/README.md)
