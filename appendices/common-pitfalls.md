# Appendix B: Common Pitfalls and Solutions

Learn from common mistakes and avoid them in your Spring Boot 4.x applications. This guide covers frequent issues developers encounter and provides practical solutions.

## 🏗️ Architecture & Design

### Pitfall 1: Exposing Entities Directly in REST APIs

**Problem:**
```java
@RestController
public class UserController {
    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Long id) {
        return userRepository.findById(id).orElseThrow();
    }
}
```

**Why It's Bad:**
- Exposes internal database structure
- Can cause lazy loading exceptions
- Makes API changes difficult
- Security risk (password fields, etc.)
- Circular reference issues with Jackson

**Solution:** Use DTOs (Data Transfer Objects)
```java
@RestController
public class UserController {
    @GetMapping("/users/{id}")
    public UserDTO getUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow();
        return UserMapper.toDTO(user);
    }
}

public record UserDTO(Long id, String username, String email) {}
```

### Pitfall 2: Business Logic in Controllers

**Problem:**
```java
@RestController
public class OrderController {
    @PostMapping("/orders")
    public Order createOrder(@RequestBody OrderRequest request) {
        // Validate order
        if (request.items().isEmpty()) {
            throw new IllegalArgumentException("Order must have items");
        }
        // Calculate total
        double total = request.items().stream()
            .mapToDouble(item -> item.price() * item.quantity())
            .sum();
        // Save to database
        Order order = new Order(request.userId(), total);
        return orderRepository.save(order);
    }
}
```

**Why It's Bad:**
- Hard to test
- Violates Single Responsibility Principle
- Can't reuse logic
- Difficult to maintain

**Solution:** Move logic to service layer
```java
@RestController
public class OrderController {
    private final OrderService orderService;
    
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    @PostMapping("/orders")
    public OrderDTO createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }
}

@Service
public class OrderService {
    public OrderDTO createOrder(OrderRequest request) {
        validateOrder(request);
        double total = calculateTotal(request);
        Order order = buildOrder(request, total);
        return saveAndConvert(order);
    }
}
```

### Pitfall 3: Not Using Constructor Injection

**Problem:**
```java
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;
}
```

**Why It's Bad:**
- Makes testing harder
- Hides dependencies
- Allows circular dependencies
- Can lead to NullPointerException
- Field injection is not recommended by Spring

**Solution:** Use constructor injection
```java
@Service
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    
    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }
}

// Or with Lombok
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;
}
```

## 💾 Database & JPA

### Pitfall 4: N+1 Query Problem

**Problem:**
```java
@Entity
public class Author {
    @OneToMany(mappedBy = "author")
    private List<Book> books;
}

// This causes N+1 queries
List<Author> authors = authorRepository.findAll();
authors.forEach(author -> {
    System.out.println(author.getBooks().size()); // Triggers separate query per author
});
```

**Solution:** Use JOIN FETCH or Entity Graphs
```java
public interface AuthorRepository extends JpaRepository<Author, Long> {
    @Query("SELECT a FROM Author a JOIN FETCH a.books")
    List<Author> findAllWithBooks();
    
    // Or use EntityGraph
    @EntityGraph(attributePaths = "books")
    List<Author> findAll();
}
```

### Pitfall 5: Not Using Proper Fetch Types

**Problem:**
```java
@Entity
public class User {
    @OneToMany(mappedBy = "user") // Default is LAZY, but might be overridden
    private List<Order> orders;
    
    @ManyToOne  // Default is EAGER - loads even when not needed
    private Department department;
}
```

**Solution:** Be explicit about fetch types
```java
@Entity
public class User {
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Department department;
}
```

### Pitfall 6: Missing @Transactional

**Problem:**
```java
@Service
public class UserService {
    public void updateUser(Long id, String newEmail) {
        User user = userRepository.findById(id).orElseThrow();
        user.setEmail(newEmail);
        // Changes might not be persisted!
    }
}
```

**Solution:** Add @Transactional
```java
@Service
public class UserService {
    @Transactional
    public void updateUser(Long id, String newEmail) {
        User user = userRepository.findById(id).orElseThrow();
        user.setEmail(newEmail);
        // Changes automatically persisted at end of transaction
    }
}
```

### Pitfall 7: Using Bidirectional Relationships Incorrectly

**Problem:**
```java
@Entity
public class Author {
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Book> books = new ArrayList<>();
}

@Entity
public class Book {
    @ManyToOne
    private Author author;
}

// Adding a book
Book book = new Book();
author.getBooks().add(book);  // Relationship not properly set!
```

**Solution:** Use helper methods
```java
@Entity
public class Author {
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Book> books = new ArrayList<>();
    
    public void addBook(Book book) {
        books.add(book);
        book.setAuthor(this);
    }
    
    public void removeBook(Book book) {
        books.remove(book);
        book.setAuthor(null);
    }
}
```

## 🔒 Security

### Pitfall 8: Not Validating User Input

**Problem:**
```java
@PostMapping("/users")
public User createUser(@RequestBody UserRequest request) {
    return userService.createUser(request);
}
```

**Solution:** Use validation annotations
```java
public record UserRequest(
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    String username,
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    String password
) {}

@PostMapping("/users")
public User createUser(@Valid @RequestBody UserRequest request) {
    return userService.createUser(request);
}
```

### Pitfall 9: Storing Passwords in Plain Text

**Problem:**
```java
@Entity
public class User {
    private String password;  // Stored as plain text!
}

public void register(String username, String password) {
    User user = new User(username, password);
    userRepository.save(user);
}
```

**Solution:** Always hash passwords
```java
@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    
    public void register(String username, String password) {
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(username, hashedPassword);
        userRepository.save(user);
    }
}

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### Pitfall 10: Not Implementing Proper Error Handling

**Problem:**
```java
@GetMapping("/users/{id}")
public User getUser(@PathVariable Long id) {
    return userRepository.findById(id).orElseThrow();
    // Returns 500 with stack trace to client!
}
```

**Solution:** Implement global exception handler
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Resource not found",
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .toList();
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed",
            errors.toString()
        );
        return ResponseEntity.badRequest().body(error);
    }
}

record ErrorResponse(int status, String message, String details) {}
```

## 🚀 Performance

### Pitfall 11: Not Using Pagination

**Problem:**
```java
@GetMapping("/products")
public List<Product> getAllProducts() {
    return productRepository.findAll();  // Could return millions of records!
}
```

**Solution:** Always use pagination
```java
@GetMapping("/products")
public Page<ProductDTO> getAllProducts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "id,asc") String[] sort) {
    
    Sort.Direction direction = sort[1].equalsIgnoreCase("desc") 
        ? Sort.Direction.DESC : Sort.Direction.ASC;
    Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
    
    return productRepository.findAll(pageable)
        .map(ProductMapper::toDTO);
}
```

### Pitfall 12: Synchronous Calls to External Services

**Problem:**
```java
@Service
public class OrderService {
    public OrderDTO createOrder(OrderRequest request) {
        Order order = saveOrder(request);
        
        // These block the thread!
        paymentService.processPayment(order);
        emailService.sendConfirmation(order);
        inventoryService.updateStock(order);
        
        return toDTO(order);
    }
}
```

**Solution:** Use async processing
```java
@Service
public class OrderService {
    @Async
    public CompletableFuture<Void> processPayment(Order order) {
        return CompletableFuture.runAsync(() -> 
            paymentService.processPayment(order)
        );
    }
    
    @Transactional
    public OrderDTO createOrder(OrderRequest request) {
        Order order = saveOrder(request);
        
        // Fire and forget for non-critical operations
        CompletableFuture.allOf(
            processPayment(order),
            sendEmail(order),
            updateInventory(order)
        );
        
        return toDTO(order);
    }
}

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        return executor;
    }
}
```

### Pitfall 13: Not Using Caching

**Problem:**
```java
@Service
public class ProductService {
    public Product getProduct(Long id) {
        // Hits database every time
        return productRepository.findById(id).orElseThrow();
    }
}
```

**Solution:** Implement caching
```java
@Service
public class ProductService {
    @Cacheable(value = "products", key = "#id")
    public Product getProduct(Long id) {
        return productRepository.findById(id).orElseThrow();
    }
    
    @CacheEvict(value = "products", key = "#product.id")
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }
}

@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("products", "users");
    }
}
```

## 🧪 Testing

### Pitfall 14: Not Mocking External Dependencies

**Problem:**
```java
@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;
    
    @Test
    public void testCreateUser() {
        // This might send real emails!
        userService.createUser(new UserRequest(...));
    }
}
```

**Solution:** Mock external dependencies
```java
@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;
    
    @MockBean
    private EmailService emailService;
    
    @Test
    public void testCreateUser() {
        UserRequest request = new UserRequest("test", "test@example.com");
        
        when(emailService.sendWelcomeEmail(any()))
            .thenReturn(true);
        
        User user = userService.createUser(request);
        
        assertThat(user).isNotNull();
        verify(emailService).sendWelcomeEmail(any());
    }
}
```

### Pitfall 15: Slow Integration Tests

**Problem:**
```java
@SpringBootTest
public class IntegrationTest {
    // Loads entire application context for every test
    // Uses real database
    // Very slow!
}
```

**Solution:** Use slice tests and Testcontainers
```java
@WebMvcTest(UserController.class)
public class UserControllerTest {
    // Only loads web layer
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
}

@DataJpaTest
@Testcontainers
public class UserRepositoryTest {
    // Only loads JPA components
    @Container
    static PostgreSQLContainer<?> postgres = 
        new PostgreSQLContainer<>("postgres:15");
    
    @Autowired
    private UserRepository repository;
}
```

## 📝 Configuration

### Pitfall 16: Hardcoding Configuration Values

**Problem:**
```java
@Service
public class EmailService {
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;
    // Hard to change for different environments!
}
```

**Solution:** Use external configuration
```java
@Service
@ConfigurationProperties(prefix = "email")
@Validated
public class EmailProperties {
    @NotBlank
    private String smtpHost;
    
    @Min(1)
    @Max(65535)
    private int smtpPort;
    
    // Getters and setters
}

// application.yml
email:
  smtp-host: ${SMTP_HOST:smtp.gmail.com}
  smtp-port: ${SMTP_PORT:587}
```

### Pitfall 17: Not Using Profiles

**Problem:**
```java
// Same configuration for dev, test, and production
```

**Solution:** Use Spring profiles
```yaml
# application.yml (common)
spring:
  application:
    name: my-app

# application-dev.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
logging:
  level:
    root: DEBUG

# application-prod.yml
spring:
  datasource:
    url: jdbc:postgresql://prod-db:5432/mydb
logging:
  level:
    root: WARN
```

## 🔄 REST APIs

### Pitfall 18: Inconsistent API Design

**Problem:**
```java
@GetMapping("/getUser/{id}")      // Bad: verb in URL
@PostMapping("/user/create")       // Bad: inconsistent
@DeleteMapping("/removeUser/{id}") // Bad: verb in URL
```

**Solution:** Follow REST conventions
```java
@GetMapping("/users/{id}")         // Good
@PostMapping("/users")             // Good
@PutMapping("/users/{id}")         // Good
@DeleteMapping("/users/{id}")      // Good
@PatchMapping("/users/{id}")       // Good
```

### Pitfall 19: Not Using Proper HTTP Status Codes

**Problem:**
```java
@PostMapping("/users")
public ResponseEntity<User> createUser(@RequestBody UserRequest request) {
    User user = userService.createUser(request);
    return ResponseEntity.ok(user);  // Should be 201, not 200!
}
```

**Solution:** Use appropriate status codes
```java
@PostMapping("/users")
public ResponseEntity<User> createUser(@RequestBody UserRequest request) {
    User user = userService.createUser(request);
    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(user.getId())
        .toUri();
    return ResponseEntity.created(location).body(user);
}

@GetMapping("/users/{id}")
public ResponseEntity<User> getUser(@PathVariable Long id) {
    return userService.findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
}

@DeleteMapping("/users/{id}")
public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.delete(id);
    return ResponseEntity.noContent().build();
}
```

## 📊 Logging

### Pitfall 20: Using System.out.println()

**Problem:**
```java
public void processOrder(Order order) {
    System.out.println("Processing order: " + order.getId());
    // Can't control log levels, format, or destination
}
```

**Solution:** Use proper logging
```java
@Slf4j  // Lombok annotation
public class OrderService {
    public void processOrder(Order order) {
        log.info("Processing order: {}", order.getId());
        try {
            // process order
            log.debug("Order {} processed successfully", order.getId());
        } catch (Exception e) {
            log.error("Failed to process order {}", order.getId(), e);
            throw new OrderProcessingException("Failed to process order", e);
        }
    }
}
```

## Summary

Avoiding these common pitfalls will help you:
- Write more maintainable code
- Improve application performance
- Enhance security
- Create better APIs
- Write more effective tests

Remember: Learning from mistakes (yours and others') is a key part of becoming a better developer!

---

**Related Resources:**
- [Appendix A: Best Practices Checklist](./best-practices.md)
- [Appendix C: Useful Resources](./resources.md)
- [Main Guide](../README.md)
