# Chapter 8: Data Access with Spring Data JPA

Spring Data JPA simplifies database access by eliminating boilerplate code. This chapter teaches you how to work with databases effectively.

## Setup

### Dependencies

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

### Configuration

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        format_sql: true
  h2:
    console:
      enabled: true
```

## Entity Modeling

### Basic Entity

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String email;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Constructors, getters, setters
}
```

### Relationships

```java
// One-to-Many
@Entity
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Book> books = new ArrayList<>();
    
    public void addBook(Book book) {
        books.add(book);
        book.setAuthor(this);
    }
}

@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;
}
```

## Repository Pattern

### Basic Repository

```java
public interface UserRepository extends JpaRepository<User, Long> {
    // Built-in methods:
    // save(), findById(), findAll(), deleteById(), count(), etc.
}
```

### Query Methods

```java
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find by username
    Optional<User> findByUsername(String username);
    
    // Find by email
    Optional<User> findByEmail(String email);
    
    // Find users created after date
    List<User> findByCreatedAtAfter(LocalDateTime date);
    
    // Find by username containing (LIKE)
    List<User> findByUsernameContaining(String keyword);
    
    // Check if exists
    boolean existsByEmail(String email);
    
    // Count users
    long countByUsername(String username);
    
    // Delete users
    void deleteByUsername(String username);
}
```

### Custom Queries

```java
public interface UserRepository extends JpaRepository<User, Long> {
    
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmailCustom(@Param("email") String email);
    
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword%")
    List<User> searchByUsername(@Param("keyword") String keyword);
    
    @Query(value = "SELECT * FROM users WHERE created_at > :date", 
           nativeQuery = true)
    List<User> findRecentUsers(@Param("date") LocalDateTime date);
    
    @Modifying
    @Query("UPDATE User u SET u.email = :email WHERE u.id = :id")
    int updateEmail(@Param("id") Long id, @Param("email") String email);
}
```

## CRUD Operations

```java
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository repository;
    
    // Create
    public User createUser(String username, String email) {
        User user = new User(username, email);
        return repository.save(user);
    }
    
    // Read
    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }
    
    public List<User> findAll() {
        return repository.findAll();
    }
    
    // Update
    public User updateUser(Long id, String newEmail) {
        User user = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setEmail(newEmail);
        return repository.save(user);
    }
    
    // Delete
    public void deleteUser(Long id) {
        repository.deleteById(id);
    }
}
```

## Pagination and Sorting

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findByUsernameContaining(String keyword, Pageable pageable);
}

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    
    public Page<User> findUsers(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return repository.findAll(pageable);
    }
    
    public Page<User> searchUsers(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByUsernameContaining(keyword, pageable);
    }
}
```

## Transactions

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    
    @Transactional
    public Order placeOrder(OrderRequest request) {
        // All operations in one transaction
        Order order = new Order(request);
        orderRepository.save(order);
        
        inventoryService.updateStock(order.getItems());
        
        return order;
        // Commits if no exception, rolls back if exception thrown
    }
    
    @Transactional(readOnly = true)
    public List<Order> findOrders() {
        return orderRepository.findAll();
    }
}
```

## Database Migrations

### Flyway

**Dependency**:
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

**Migration Script** (`src/main/resources/db/migration/V1__init.sql`):
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Summary

You learned:
- Entity modeling with JPA
- Repository pattern
- Query methods and custom queries
- CRUD operations
- Pagination and sorting
- Transactions
- Database migrations

---

## 🏋️ Exercises

**Exercise 1**: Create Product entity with Category relationship  
**Exercise 2**: Implement search with pagination  
**Exercise 3**: Add custom queries  
**Exercise 4**: Create migration scripts  
**Exercise 5**: Implement audit fields

## 🎯 Quiz

**Q1**: What does JPA stand for?  
✅ Java Persistence API

**Q2**: Default fetch type for @ManyToOne?  
✅ EAGER

**Q3**: Annotation for database operations?  
✅ @Transactional

**Q4**: How to enable pagination?  
✅ Use Pageable parameter

**Q5**: Tool for database migrations?  
✅ Flyway or Liquibase

---

**Previous**: [← Chapter 7: RESTful Web Services](../07-rest-services/README.md)  
**Next**: [Chapter 9: Security with Spring Security →](../09-security/README.md)
