# Chapter 3: Core Concepts and Architecture

Understanding Spring Boot's core concepts is essential for becoming proficient with the framework. This chapter explores the fundamental principles that make Spring Boot powerful and easy to use.

## Spring Framework Fundamentals

Spring Boot is built on top of the Spring Framework. Understanding Spring's core principles helps you leverage Spring Boot effectively.

### The Spring Container

At the heart of Spring is the **ApplicationContext**, a sophisticated container that manages the lifecycle of your application's objects (beans).

```java
@SpringBootApplication
public class MyApplication {
    public static void main(String[] args) {
        // This creates and starts the Spring ApplicationContext
        ApplicationContext context = SpringApplication.run(MyApplication.class, args);
        
        // You can retrieve beans from the context
        MyService service = context.getBean(MyService.class);
        service.doSomething();
    }
}
```

### What is a Bean?

A **bean** is simply an object that is instantiated, assembled, and managed by the Spring container.

```java
@Component
public class UserService {
    // This class is a Spring bean
    // Spring creates and manages its lifecycle
}
```

## Inversion of Control (IoC)

**Inversion of Control** is a design principle where the control of object creation and lifecycle is transferred from the application code to a framework (Spring).

### Traditional Approach (Without IoC)

```java
public class OrderService {
    // We create and manage dependencies ourselves
    private PaymentService paymentService = new PaymentService();
    private InventoryService inventoryService = new InventoryService();
    
    public void processOrder(Order order) {
        paymentService.charge(order);
        inventoryService.updateStock(order);
    }
}
```

**Problems:**
- Tight coupling between classes
- Hard to test (can't mock dependencies)
- Difficult to change implementations
- Manual lifecycle management

### Spring IoC Approach

```java
@Service
public class OrderService {
    // Spring injects dependencies for us
    private final PaymentService paymentService;
    private final InventoryService inventoryService;
    
    public OrderService(PaymentService paymentService, 
                       InventoryService inventoryService) {
        this.paymentService = paymentService;
        this.inventoryService = inventoryService;
    }
    
    public void processOrder(Order order) {
        paymentService.charge(order);
        inventoryService.updateStock(order);
    }
}
```

**Benefits:**
- Loose coupling
- Easy to test (can inject mocks)
- Flexible (can change implementations)
- Spring manages lifecycle

## Dependency Injection (DI)

**Dependency Injection** is the mechanism by which IoC is achieved. Spring provides dependencies to your classes rather than having them create dependencies themselves.

### Types of Dependency Injection

#### 1. Constructor Injection (Recommended)

```java
@Service
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    
    // Spring injects dependencies through constructor
    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }
}
```

**Advantages:**
- Immutable (using `final`)
- Required dependencies are clear
- Easy to test
- Prevents circular dependencies

#### 2. Setter Injection

```java
@Service
public class UserService {
    private UserRepository userRepository;
    
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

**Use Cases:**
- Optional dependencies
- When you need to reconfigure the bean after construction

#### 3. Field Injection (Not Recommended)

```java
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;  // Avoid this!
}
```

**Why Not Recommended:**
- Can't make fields `final`
- Makes testing harder
- Hides dependencies
- Can lead to NullPointerException

### @Autowired vs Constructor Injection

With modern Spring Boot, constructor injection doesn't require `@Autowired`:

```java
// No @Autowired needed!
@Service
public class UserService {
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}

// Or use Lombok for even cleaner code
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
}
```

## Spring Boot Auto-Configuration

One of Spring Boot's killer features is **auto-configuration**. It automatically configures your application based on the dependencies on your classpath.

### How Auto-Configuration Works

```
1. Spring Boot detects dependencies on classpath
   ↓
2. Applies conditional configuration based on what it finds
   ↓
3. Creates beans with sensible defaults
   ↓
4. Allows you to override with custom configuration
```

### Example: Database Auto-Configuration

When you add this dependency:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

Spring Boot automatically:
- Configures a DataSource
- Sets up JPA EntityManager
- Configures Hibernate
- Creates transaction manager
- Sets up connection pooling

### Viewing Auto-Configuration Report

Run your application with debug mode:
```bash
java -jar myapp.jar --debug
```

Or add to `application.properties`:
```properties
logging.level.org.springframework.boot.autoconfigure=DEBUG
```

Output shows:
- Positive matches (what was auto-configured)
- Negative matches (what wasn't and why)
- Exclusions

### Disabling Specific Auto-Configuration

```java
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class
})
public class MyApplication {
    // ...
}
```

## Component Scanning

Spring Boot automatically scans for components in your package and sub-packages.

### How Component Scanning Works

```java
package com.example.myapp;  // Root package

@SpringBootApplication  // This annotation enables component scanning
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

Spring scans these packages:
```
com.example.myapp               ✓
com.example.myapp.controller    ✓
com.example.myapp.service       ✓
com.example.myapp.repository    ✓
com.example.other               ✗ (not scanned by default)
```

### Stereotype Annotations

These annotations mark classes for component scanning:

```java
@Component     // Generic component
@Service       // Business logic layer
@Repository    // Data access layer
@Controller    // Web layer (MVC)
@RestController // REST API endpoints
@Configuration // Configuration class
```

### Example: Component Hierarchy

```java
// Generic component
@Component
public class EmailValidator {
    public boolean isValid(String email) {
        return email.contains("@");
    }
}

// Service layer
@Service
public class UserService {
    private final UserRepository userRepository;
    private final EmailValidator emailValidator;
    
    public UserService(UserRepository userRepository, 
                      EmailValidator emailValidator) {
        this.userRepository = userRepository;
        this.emailValidator = emailValidator;
    }
}

// Repository layer
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}

// REST controller
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable Long id) {
        return userService.findById(id);
    }
}
```

### Custom Component Scanning

```java
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.example.myapp",
    "com.example.shared"
})
public class MyApplication {
    // Now scans both packages
}
```

## Bean Lifecycle

Understanding the bean lifecycle helps you leverage Spring's initialization and destruction hooks.

### Lifecycle Phases

```
1. Bean Definition → 2. Instantiation → 3. Populate Properties
                                            ↓
6. Bean Ready ← 5. Initialization Complete ← 4. Initialize
                                            ↓
7. Use Bean
                                            ↓
8. Destruction → 9. Bean Destroyed
```

### Lifecycle Callbacks

#### 1. @PostConstruct and @PreDestroy

```java
@Component
public class DatabaseConnection {
    
    @PostConstruct
    public void init() {
        System.out.println("Establishing database connection...");
        // Called after dependency injection
    }
    
    @PreDestroy
    public void cleanup() {
        System.out.println("Closing database connection...");
        // Called before bean destruction
    }
}
```

#### 2. InitializingBean and DisposableBean Interfaces

```java
@Component
public class CacheManager implements InitializingBean, DisposableBean {
    
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("Initializing cache...");
    }
    
    @Override
    public void destroy() throws Exception {
        System.out.println("Clearing cache...");
    }
}
```

#### 3. @Bean with initMethod and destroyMethod

```java
@Configuration
public class AppConfig {
    
    @Bean(initMethod = "init", destroyMethod = "cleanup")
    public DataSource dataSource() {
        return new CustomDataSource();
    }
}

public class CustomDataSource {
    public void init() {
        System.out.println("Initializing data source...");
    }
    
    public void cleanup() {
        System.out.println("Closing data source...");
    }
}
```

### Complete Lifecycle Example

```java
@Component
public class UserService implements InitializingBean, DisposableBean {
    
    private final UserRepository repository;
    private CacheManager cacheManager;
    
    // 1. Constructor called
    public UserService(UserRepository repository) {
        System.out.println("1. Constructor called");
        this.repository = repository;
    }
    
    // 2. Setter injection (if any)
    @Autowired
    public void setCacheManager(CacheManager cacheManager) {
        System.out.println("2. Setter injection");
        this.cacheManager = cacheManager;
    }
    
    // 3. Post construct
    @PostConstruct
    public void postConstruct() {
        System.out.println("3. @PostConstruct");
    }
    
    // 4. After properties set
    @Override
    public void afterPropertiesSet() {
        System.out.println("4. afterPropertiesSet()");
    }
    
    // 5. Bean ready for use
    public void doSomething() {
        System.out.println("5. Bean in use");
    }
    
    // 6. Pre destroy
    @PreDestroy
    public void preDestroy() {
        System.out.println("6. @PreDestroy");
    }
    
    // 7. Destroy
    @Override
    public void destroy() {
        System.out.println("7. destroy()");
    }
}
```

## ApplicationContext

The **ApplicationContext** is the central interface for Spring's IoC container.

### Getting the ApplicationContext

```java
@Component
public class ApplicationContextProvider implements ApplicationContextAware {
    private static ApplicationContext context;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }
    
    public static ApplicationContext getContext() {
        return context;
    }
}
```

### Using ApplicationContext

```java
@Service
public class DynamicBeanService {
    private final ApplicationContext context;
    
    public DynamicBeanService(ApplicationContext context) {
        this.context = context;
    }
    
    public void demonstrateCapabilities() {
        // Get bean by type
        UserService userService = context.getBean(UserService.class);
        
        // Get bean by name
        Object myBean = context.getBean("myBeanName");
        
        // Check if bean exists
        boolean exists = context.containsBean("userService");
        
        // Get all beans of a type
        Map<String, UserService> services = 
            context.getBeansOfType(UserService.class);
        
        // Get bean names
        String[] beanNames = context.getBeanDefinitionNames();
        
        // Publish events
        context.publishEvent(new CustomEvent(this));
    }
}
```

### Application Events

```java
// Custom event
public class UserRegisteredEvent extends ApplicationEvent {
    private final User user;
    
    public UserRegisteredEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
    
    public User getUser() {
        return user;
    }
}

// Event publisher
@Service
public class UserService {
    private final ApplicationEventPublisher eventPublisher;
    
    public UserService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    public User registerUser(UserRequest request) {
        User user = createUser(request);
        // Publish event
        eventPublisher.publishEvent(new UserRegisteredEvent(this, user));
        return user;
    }
}

// Event listener
@Component
public class UserEventListener {
    
    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) {
        User user = event.getUser();
        System.out.println("New user registered: " + user.getEmail());
        // Send welcome email, log analytics, etc.
    }
}

// Or use @TransactionalEventListener
@Component
public class TransactionalEventListener {
    
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAfterCommit(UserRegisteredEvent event) {
        // Only executes if transaction commits successfully
        sendWelcomeEmail(event.getUser());
    }
}
```

## Bean Scopes

Scopes define the lifecycle and visibility of beans.

### Available Scopes

#### 1. Singleton (Default)

```java
@Service
@Scope("singleton")  // This is default, can be omitted
public class UserService {
    // One instance per Spring container
    // Shared across the entire application
}
```

#### 2. Prototype

```java
@Service
@Scope("prototype")
public class ReportGenerator {
    // New instance created each time it's requested
}
```

#### 3. Request (Web Applications)

```java
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestContextHolder {
    // One instance per HTTP request
}
```

#### 4. Session (Web Applications)

```java
@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserSession {
    // One instance per HTTP session
}
```

#### 5. Application

```java
@Component
@Scope(value = WebApplicationContext.SCOPE_APPLICATION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ApplicationSettings {
    // One instance per ServletContext
}
```

### Scope Example

```java
@RestController
public class ScopeController {
    
    @Autowired
    private SingletonBean singletonBean;  // Same instance always
    
    @Autowired
    private Provider<PrototypeBean> prototypeProvider;  // New instance each time
    
    @GetMapping("/test-scopes")
    public Map<String, Object> testScopes() {
        return Map.of(
            "singleton1", singletonBean.getId(),
            "singleton2", singletonBean.getId(),  // Same ID
            "prototype1", prototypeProvider.get().getId(),
            "prototype2", prototypeProvider.get().getId()  // Different ID
        );
    }
}
```

## @SpringBootApplication Explained

The `@SpringBootApplication` annotation is a convenience annotation that combines three annotations:

```java
@SpringBootApplication
// Is equivalent to:
@Configuration       // Marks class as source of bean definitions
@EnableAutoConfiguration  // Enables auto-configuration
@ComponentScan      // Enables component scanning

public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

### Customizing @SpringBootApplication

```java
@SpringBootApplication(
    scanBasePackages = {"com.example.app", "com.example.shared"},
    exclude = {DataSourceAutoConfiguration.class},
    excludeName = {"org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration"}
)
public class MyApplication {
    // Custom configuration
}
```

## Configuration Classes

Use `@Configuration` classes to define beans programmatically.

```java
@Configuration
public class AppConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate template = new RestTemplate();
        template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        return template;
    }
    
    @Bean
    @ConditionalOnProperty(name = "feature.advanced", havingValue = "true")
    public AdvancedFeature advancedFeature() {
        return new AdvancedFeature();
    }
}
```

## Conditional Beans

Create beans conditionally based on various factors.

```java
@Configuration
public class ConditionalConfig {
    
    // Only create if property exists
    @Bean
    @ConditionalOnProperty(name = "cache.enabled", havingValue = "true")
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }
    
    // Only create if class exists on classpath
    @Bean
    @ConditionalOnClass(name = "com.redis.RedisClient")
    public RedisCache redisCache() {
        return new RedisCache();
    }
    
    // Only create if bean doesn't exist
    @Bean
    @ConditionalOnMissingBean
    public DefaultEmailService emailService() {
        return new DefaultEmailService();
    }
    
    // Only create in specific profile
    @Bean
    @Profile("production")
    public ProductionDataSource dataSource() {
        return new ProductionDataSource();
    }
}
```

## Summary

In this chapter, you learned:
- Spring Framework fundamentals and the ApplicationContext
- Inversion of Control (IoC) and its benefits
- Dependency Injection types and best practices
- Spring Boot's powerful auto-configuration
- Component scanning and stereotype annotations
- Bean lifecycle and callback methods
- Bean scopes and when to use them
- How @SpringBootApplication works
- Creating configuration classes
- Conditional bean creation

These core concepts form the foundation of Spring Boot development. Master them, and you'll be well on your way to becoming a Spring Boot expert!

---

## 🏋️ Coding Exercises

### Exercise 1: Dependency Injection Practice
Create a simple application with:
1. A `CalculatorService` with addition and subtraction methods
2. A `CalculatorController` that depends on `CalculatorService`
3. Use constructor injection
4. Test that Spring wires everything correctly

### Exercise 2: Bean Lifecycle
Create a bean that:
1. Logs a message in the constructor
2. Logs a message in `@PostConstruct`
3. Logs a message in `@PreDestroy`
4. Run the application and observe the output

### Exercise 3: Component Scanning
Create a multi-module project structure:
```
src/main/java/
  ├── com.example.app/
  │   └── MyApplication.java
  ├── com.example.services/
  │   └── UserService.java
  └── com.example.external/
      └── ExternalService.java
```
Configure component scanning to include all packages.

### Exercise 4: Bean Scopes
Create:
1. A singleton bean that counts how many times it's called
2. A prototype bean that generates a random ID
3. A REST endpoint that uses both
4. Call the endpoint multiple times and observe the behavior

### Exercise 5: Conditional Configuration
Create configuration that:
1. Enables caching only if `cache.enabled=true`
2. Uses Redis cache if Redis is on classpath
3. Falls back to in-memory cache otherwise

---

## 🎯 Quiz

### Question 1
What is Inversion of Control?
- A) A way to control program flow
- B) Transferring object creation control to a framework ✅
- C) A design pattern for controllers
- D) A method of controlling dependencies

### Question 2
Which dependency injection type is recommended?
- A) Field injection
- B) Setter injection
- C) Constructor injection ✅
- D) Method injection

### Question 3
What does @SpringBootApplication combine?
- A) @Component, @Service, @Repository
- B) @Configuration, @EnableAutoConfiguration, @ComponentScan ✅
- C) @Bean, @Autowired, @Component
- D) @RestController, @Service, @Repository

### Question 4
What is the default bean scope?
- A) Prototype
- B) Request
- C) Session
- D) Singleton ✅

### Question 5
Which annotation marks a class for component scanning?
- A) @Bean
- B) @Component ✅
- C) @Autowired
- D) @Inject

### Question 6
When is @PostConstruct called?
- A) Before constructor
- B) After dependency injection ✅
- C) Before dependency injection
- D) During application shutdown

### Question 7
What does auto-configuration do?
- A) Configures beans manually
- B) Requires XML configuration
- C) Automatically configures beans based on classpath ✅
- D) Disables configuration

### Question 8
Which scope creates a new instance each time?
- A) Singleton
- B) Prototype ✅
- C) Request
- D) Session

### Question 9
What is ApplicationContext?
- A) A database connection
- B) The Spring IoC container ✅
- C) A configuration file
- D) A web context

### Question 10
How do you publish an event?
- A) Using EventPublisher
- B) Using ApplicationEventPublisher ✅
- C) Using EventDispatcher
- D) Using MessagePublisher

---

## 📚 Additional Resources

- [Spring Framework Core Documentation](https://docs.spring.io/spring-framework/reference/core.html)
- [Spring Boot Auto-Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.auto-configuration)
- [Understanding Dependency Injection](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)
- [Bean Scopes in Spring](https://www.baeldung.com/spring-bean-scopes)

---

**Previous Chapter**: [← Chapter 2: Setting Up Your Development Environment](../02-setup/README.md)

**Next Chapter**: [Chapter 4: Building Your First Application](../04-first-application/README.md) →
