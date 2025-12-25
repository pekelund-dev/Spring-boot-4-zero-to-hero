# Chapter 7: RESTful Web Services

REST (Representational State Transfer) is the dominant architectural style for building web APIs. This chapter teaches you how to build production-ready RESTful services with Spring Boot 4.x.

## REST Principles

### What is REST?

REST is an architectural style that uses HTTP as the underlying protocol and leverages its features:

1. **Stateless**: Each request contains all information needed
2. **Client-Server**: Separation of concerns
3. **Cacheable**: Responses can be cached
4. **Uniform Interface**: Consistent way to interact with resources
5. **Layered System**: Architecture can have multiple layers

### REST Resources

Resources are the fundamental concept in REST:
- **Resource**: Any information that can be named (user, product, order)
- **URI**: Unique identifier for a resource (`/users/123`)
- **Representation**: Format of the resource (JSON, XML)

## Your First REST Controller

### Basic REST Controller

```java
package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    @GetMapping
    public String getAllUsers() {
        return "List of all users";
    }
    
    @GetMapping("/{id}")
    public String getUser(@PathVariable Long id) {
        return "User with ID: " + id;
    }
    
    @PostMapping
    public String createUser(@RequestBody String userData) {
        return "User created: " + userData;
    }
    
    @PutMapping("/{id}")
    public String updateUser(@PathVariable Long id, @RequestBody String userData) {
        return "User " + id + " updated: " + userData;
    }
    
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        return "User " + id + " deleted";
    }
}
```

### @RestController vs @Controller

```java
// @RestController automatically adds @ResponseBody to all methods
@RestController
public class ApiController {
    @GetMapping("/data")
    public Data getData() {
        return new Data();  // Automatically serialized to JSON
    }
}

// @Controller returns view names by default
@Controller
public class WebController {
    @GetMapping("/page")
    public String getPage() {
        return "page";  // Returns view name "page"
    }
    
    @GetMapping("/api/data")
    @ResponseBody  // Need this to return data instead of view
    public Data getData() {
        return new Data();
    }
}
```

## HTTP Methods and Their Usage

### GET - Retrieve Resources

```java
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    // Get all products
    @GetMapping
    public List<ProductDTO> getAllProducts() {
        return productService.findAll();
    }
    
    // Get single product
    @GetMapping("/{id}")
    public ProductDTO getProduct(@PathVariable Long id) {
        return productService.findById(id);
    }
    
    // Get with query parameters
    @GetMapping("/search")
    public List<ProductDTO> searchProducts(
            @RequestParam String name,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        return productService.search(name, minPrice, maxPrice);
    }
}
```

### POST - Create Resources

```java
@PostMapping
public ResponseEntity<ProductDTO> createProduct(@RequestBody @Valid ProductRequest request) {
    ProductDTO created = productService.create(request);
    
    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(created.getId())
        .toUri();
    
    return ResponseEntity.created(location).body(created);
}
```

### PUT - Update Entire Resource

```java
@PutMapping("/{id}")
public ResponseEntity<ProductDTO> updateProduct(
        @PathVariable Long id,
        @RequestBody @Valid ProductRequest request) {
    ProductDTO updated = productService.update(id, request);
    return ResponseEntity.ok(updated);
}
```

### PATCH - Partial Update

```java
@PatchMapping("/{id}")
public ResponseEntity<ProductDTO> partialUpdateProduct(
        @PathVariable Long id,
        @RequestBody Map<String, Object> updates) {
    ProductDTO updated = productService.partialUpdate(id, updates);
    return ResponseEntity.ok(updated);
}
```

### DELETE - Remove Resources

```java
@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    productService.delete(id);
    return ResponseEntity.noContent().build();
}
```

## Request and Response Handling

### Path Variables

```java
@GetMapping("/users/{userId}/orders/{orderId}")
public OrderDTO getOrder(
        @PathVariable Long userId,
        @PathVariable Long orderId) {
    return orderService.findByUserAndId(userId, orderId);
}

// With variable name different from parameter
@GetMapping("/users/{id}")
public UserDTO getUser(@PathVariable("id") Long userId) {
    return userService.findById(userId);
}
```

### Query Parameters

```java
@GetMapping("/products")
public Page<ProductDTO> getProducts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "name") String sortBy,
        @RequestParam(defaultValue = "asc") String direction,
        @RequestParam(required = false) String category) {
    
    Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") 
        ? Sort.Direction.DESC 
        : Sort.Direction.ASC;
    
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
    
    return productService.findAll(category, pageable);
}
```

### Request Headers

```java
@GetMapping("/data")
public ResponseEntity<DataDTO> getData(
        @RequestHeader("Authorization") String authHeader,
        @RequestHeader(value = "Accept-Language", defaultValue = "en") String language) {
    
    DataDTO data = dataService.getData(authHeader, language);
    return ResponseEntity.ok(data);
}
```

### Request Body

```java
// Simple request body
@PostMapping("/users")
public UserDTO createUser(@RequestBody @Valid UserRequest request) {
    return userService.create(request);
}

// Complex nested request
@PostMapping("/orders")
public OrderDTO createOrder(@RequestBody @Valid OrderRequest request) {
    return orderService.create(request);
}

// Request DTO with validation
public record UserRequest(
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50)
    String name,
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,
    
    @NotNull
    @Min(18)
    Integer age
) {}
```

## Data Transfer Objects (DTOs)

### Why Use DTOs?

1. **Decouple API from domain model**
2. **Control what data is exposed**
3. **Support multiple representations**
4. **Enable API versioning**

### DTO Example

```java
// Entity (internal)
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    private String email;
    private String password;  // Never expose this!
    
    @OneToMany(mappedBy = "user")
    private List<Order> orders;
    
    // getters, setters
}

// DTO (external)
public record UserDTO(
    Long id,
    String username,
    String email,
    LocalDateTime createdAt
) {}

// Mapper
@Component
public class UserMapper {
    
    public UserDTO toDTO(User user) {
        return new UserDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getCreatedAt()
        );
    }
    
    public User toEntity(UserRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        return user;
    }
}
```

### Using MapStruct for Mapping

```java
// Add dependency
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.5.Final</version>
</dependency>

// Mapper interface
@Mapper(componentModel = "spring")
public interface UserMapper {
    
    UserDTO toDTO(User user);
    
    User toEntity(UserRequest request);
    
    @Mapping(target = "id", ignore = true)
    User updateEntity(@MappingTarget User user, UserRequest request);
    
    List<UserDTO> toDTOList(List<User> users);
}
```

## HTTP Status Codes

### Common Status Codes

```java
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    // 200 OK - Success with response body
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }
    
    // 201 Created - Resource created
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserRequest request) {
        UserDTO created = userService.create(request);
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.getId())
            .toUri();
        return ResponseEntity.created(location).body(created);
    }
    
    // 204 No Content - Success without response body
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    // 400 Bad Request - Client error
    @PostMapping("/validate")
    public ResponseEntity<?> validateUser(@RequestBody UserRequest request) {
        if (!isValid(request)) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Invalid user data"));
        }
        return ResponseEntity.ok().build();
    }
    
    // 404 Not Found - Resource doesn't exist
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        return userService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
```

### HTTP Status Code Guide

| Code | Meaning | Use Case |
|------|---------|----------|
| 200 | OK | Successful GET, PUT, PATCH |
| 201 | Created | Successful POST |
| 204 | No Content | Successful DELETE |
| 400 | Bad Request | Validation error |
| 401 | Unauthorized | Missing/invalid authentication |
| 403 | Forbidden | User lacks permission |
| 404 | Not Found | Resource doesn't exist |
| 409 | Conflict | Duplicate resource |
| 422 | Unprocessable Entity | Semantic validation error |
| 500 | Internal Server Error | Server error |

## Exception Handling

### Global Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    // Handle resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Resource Not Found",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    // Handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        ValidationErrorResponse response = new ValidationErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Failed",
            errors,
            LocalDateTime.now()
        );
        
        return ResponseEntity.badRequest().body(response);
    }
    
    // Handle duplicate resource
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateResourceException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            "Duplicate Resource",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    // Handle generic exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred",
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

// Error response DTOs
record ErrorResponse(
    int status,
    String error,
    String message,
    LocalDateTime timestamp
) {}

record ValidationErrorResponse(
    int status,
    String error,
    Map<String, String> errors,
    LocalDateTime timestamp
) {}
```

### Custom Exceptions

```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Long id) {
        super(String.format("%s with id %d not found", resource, id));
    }
}

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}

public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(String message) {
        super(message);
    }
}
```

## Input Validation

### Bean Validation Annotations

```java
public record ProductRequest(
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    String name,
    
    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    String description,
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Price cannot exceed 999999.99")
    BigDecimal price,
    
    @NotNull(message = "Category is required")
    @Pattern(regexp = "^(ELECTRONICS|CLOTHING|FOOD|BOOKS)$", 
             message = "Invalid category")
    String category,
    
    @Min(value = 0, message = "Stock cannot be negative")
    Integer stock,
    
    @Email(message = "Invalid email format")
    String contactEmail,
    
    @Past(message = "Manufacturing date must be in the past")
    LocalDate manufacturedDate
) {}
```

### Custom Validators

```java
// Custom annotation
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmailValidator.class)
public @interface UniqueEmail {
    String message() default "Email already exists";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

// Validator implementation
@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
    
    private final UserRepository userRepository;
    
    public UniqueEmailValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null) {
            return true;  // Let @NotNull handle null
        }
        return !userRepository.existsByEmail(email);
    }
}

// Usage
public record UserRequest(
    @NotBlank
    String username,
    
    @NotBlank
    @Email
    @UniqueEmail
    String email,
    
    @NotBlank
    @Size(min = 8)
    String password
) {}
```

## Pagination and Sorting

### Spring Data Pagination

```java
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping
    public Page<ProductDTO> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        return productService.findAll(pageable);
    }
}
```

### Custom Pagination Response

```java
public record PageResponse<T>(
    List<T> content,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast()
        );
    }
}

@GetMapping
public ResponseEntity<PageResponse<ProductDTO>> getProducts(Pageable pageable) {
    Page<ProductDTO> page = productService.findAll(pageable);
    return ResponseEntity.ok(PageResponse.from(page));
}
```

## API Versioning

### URI Versioning (Recommended)

```java
@RestController
@RequestMapping("/api/v1/users")
public class UserControllerV1 {
    @GetMapping("/{id}")
    public UserDTOV1 getUser(@PathVariable Long id) {
        return userService.findByIdV1(id);
    }
}

@RestController
@RequestMapping("/api/v2/users")
public class UserControllerV2 {
    @GetMapping("/{id}")
    public UserDTOV2 getUser(@PathVariable Long id) {
        return userService.findByIdV2(id);
    }
}
```

### Header Versioning

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping(value = "/{id}", headers = "X-API-VERSION=1")
    public UserDTOV1 getUserV1(@PathVariable Long id) {
        return userService.findByIdV1(id);
    }
    
    @GetMapping(value = "/{id}", headers = "X-API-VERSION=2")
    public UserDTOV2 getUserV2(@PathVariable Long id) {
        return userService.findByIdV2(id);
    }
}
```

### Content Negotiation Versioning

```java
@GetMapping(value = "/{id}", produces = "application/vnd.company.v1+json")
public UserDTOV1 getUserV1(@PathVariable Long id) {
    return userService.findByIdV1(id);
}

@GetMapping(value = "/{id}", produces = "application/vnd.company.v2+json")
public UserDTOV2 getUserV2(@PathVariable Long id) {
    return userService.findByIdV2(id);
}
```

## CORS Configuration

```java
@Configuration
public class WebConfig {
    
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("http://localhost:3000", "https://app.example.com")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);
            }
        };
    }
}

// Or per-controller
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    // ...
}
```

## Content Negotiation

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    // Returns JSON by default
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO getUserAsJson(@PathVariable Long id) {
        return userService.findById(id);
    }
    
    // Returns XML when requested
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_XML_VALUE)
    public UserDTO getUserAsXml(@PathVariable Long id) {
        return userService.findById(id);
    }
    
    // Accepts both JSON and XML
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserDTO createUser(@RequestBody UserRequest request) {
        return userService.create(request);
    }
}
```

## Summary

In this chapter, you learned:
- REST principles and best practices
- Creating REST controllers with Spring Boot
- HTTP methods and their proper usage
- Request and response handling
- Using DTOs to decouple your API
- Proper HTTP status codes
- Global exception handling
- Input validation with Bean Validation
- Pagination and sorting
- API versioning strategies
- CORS configuration
- Content negotiation

---

## 🏋️ Coding Exercises

### Exercise 1: Build a Blog API
Create a REST API for a blog with:
- Posts (CRUD operations)
- Comments on posts
- Categories for posts
- Pagination and sorting

### Exercise 2: Implement Validation
Add comprehensive validation:
- Title: 5-100 characters
- Content: Required, max 5000 characters
- Category: Must be from predefined list
- Published date: Cannot be in future

### Exercise 3: Exception Handling
Implement global exception handling for:
- Resource not found
- Duplicate title
- Invalid category
- Validation errors

### Exercise 4: API Versioning
Create two versions of your API:
- V1: Simple post with title and content
- V2: Enhanced post with author, tags, and metadata

### Exercise 5: Add Pagination
Implement pagination with:
- Page number and size
- Sorting by multiple fields
- Filtering by category
- Search by title

---

## 🎯 Quiz

### Question 1
Which HTTP method should be used to create a resource?
- A) GET
- B) POST ✅
- C) PUT
- D) DELETE

### Question 2
What status code indicates successful resource creation?
- A) 200 OK
- B) 201 Created ✅
- C) 204 No Content
- D) 202 Accepted

### Question 3
What is the purpose of DTOs?
- A) Database access
- B) Decouple API from domain model ✅
- C) Handle exceptions
- D) Configure security

### Question 4
Which annotation combines @Controller and @ResponseBody?
- A) @RestAPI
- B) @APIController
- C) @RestController ✅
- D) @WebController

### Question 5
What is the recommended API versioning strategy?
- A) Query parameter
- B) Header versioning
- C) URI versioning ✅
- D) Cookie versioning

---

## 📚 Additional Resources

- [REST API Tutorial](https://restfulapi.net/)
- [Spring Web MVC Documentation](https://docs.spring.io/spring-framework/reference/web/webmvc.html)
- [Bean Validation Specification](https://beanvalidation.org/)
- [HTTP Status Codes](https://httpstatuses.com/)

---

**Previous Chapter**: [← Chapter 6: Spring Boot Configuration](../06-configuration/README.md)

**Next Chapter**: [Chapter 8: Data Access with Spring Data JPA](../08-data-access/README.md) →
