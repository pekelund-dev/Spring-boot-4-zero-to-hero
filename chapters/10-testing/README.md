# Chapter 10: Testing Spring Boot Applications

Testing is crucial for building reliable applications. This chapter covers unit testing, integration testing, and best practices for Spring Boot applications.

## Test Dependencies

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

Includes: JUnit 5, Mockito, AssertJ, Hamcrest, Spring Test

## Unit Testing

### Testing Services

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository repository;
    
    @InjectMocks
    private UserService service;
    
    @Test
    void shouldCreateUser() {
        // Given
        User user = new User("john", "john@example.com");
        when(repository.save(any(User.class))).thenReturn(user);
        
        // When
        User created = service.createUser("john", "john@example.com");
        
        // Then
        assertThat(created).isNotNull();
        assertThat(created.getUsername()).isEqualTo("john");
        verify(repository).save(any(User.class));
    }
    
    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> service.getUserById(1L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("User not found");
    }
}
```

## Integration Testing

### @SpringBootTest

```java
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void shouldCreateUser() throws Exception {
        UserRequest request = new UserRequest("john", "john@example.com");
        
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("john"))
            .andExpect(jsonPath("$.email").value("john@example.com"));
    }
    
    @Test
    void shouldGetUserById() throws Exception {
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }
}
```

## Testing Web Layer

### @WebMvcTest

```java
@WebMvcTest(UserController.class)
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Test
    void shouldReturnUser() throws Exception {
        User user = new User(1L, "john", "john@example.com");
        when(userService.getUserById(1L)).thenReturn(user);
        
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("john"));
        
        verify(userService).getUserById(1L);
    }
    
    @Test
    void shouldReturn404WhenUserNotFound() throws Exception {
        when(userService.getUserById(999L))
            .thenThrow(new ResourceNotFoundException("User not found"));
        
        mockMvc.perform(get("/api/users/999"))
            .andExpect(status().isNotFound());
    }
}
```

## Testing Data Layer

### @DataJpaTest

```java
@DataJpaTest
class UserRepositoryTest {
    
    @Autowired
    private UserRepository repository;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    void shouldFindUserByUsername() {
        // Given
        User user = new User("john", "john@example.com");
        entityManager.persist(user);
        entityManager.flush();
        
        // When
        Optional<User> found = repository.findByUsername("john");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("john@example.com");
    }
    
    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        Optional<User> found = repository.findByUsername("nonexistent");
        assertThat(found).isEmpty();
    }
}
```

## Testing with Testcontainers

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>
```

```java
@SpringBootTest
@Testcontainers
class UserRepositoryIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @Autowired
    private UserRepository repository;
    
    @Test
    void shouldSaveAndRetrieveUser() {
        User user = new User("john", "john@example.com");
        User saved = repository.save(user);
        
        Optional<User> found = repository.findById(saved.getId());
        assertThat(found).isPresent();
    }
}
```

## Testing REST APIs

### RestTemplate Testing

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserApiTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void shouldCreateUser() {
        UserRequest request = new UserRequest("john", "john@example.com");
        
        ResponseEntity<User> response = restTemplate.postForEntity(
            "/api/users",
            request,
            User.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUsername()).isEqualTo("john");
    }
}
```

## Test Configuration

### application-test.yml

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
  jpa:
    hibernate:
      ddl-auto: create-drop
logging:
  level:
    org.springframework: INFO
```

### Test Configuration Class

```java
@TestConfiguration
public class TestConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public Clock clock() {
        return Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), 
                          ZoneId.of("UTC"));
    }
}
```

## Best Practices

### AAA Pattern

```java
@Test
void shouldCalculateTotal() {
    // Arrange (Given)
    Order order = new Order();
    order.addItem(new Item("Product", 10.0, 2));
    
    // Act (When)
    double total = order.calculateTotal();
    
    // Assert (Then)
    assertThat(total).isEqualTo(20.0);
}
```

### Test Naming

```java
// Good names
@Test
void shouldReturnUserWhenIdExists()

@Test
void shouldThrowExceptionWhenUserNotFound()

@Test
void shouldCalculateTotalWithDiscount()
```

## Code Coverage

### JaCoCo Plugin

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.10</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

Run: `./mvnw test jacoco:report`

## Summary

You learned:
- Unit testing with Mockito
- Integration testing with @SpringBootTest
- Testing web layer with @WebMvcTest
- Testing data layer with @DataJpaTest
- Using Testcontainers
- Code coverage with JaCoCo

---

## 🏋️ Exercises

**Exercise 1**: Write unit tests for service layer  
**Exercise 2**: Create integration tests  
**Exercise 3**: Test REST endpoints  
**Exercise 4**: Add Testcontainers  
**Exercise 5**: Achieve 80% code coverage

## 🎯 Quiz

**Q1**: Annotation for unit tests?  
✅ @ExtendWith(MockitoExtension.class)

**Q2**: Mock dependencies with?  
✅ @Mock

**Q3**: Test only web layer?  
✅ @WebMvcTest

**Q4**: Test data layer?  
✅ @DataJpaTest

**Q5**: Code coverage tool?  
✅ JaCoCo

---

**Previous**: [← Chapter 9: Security](../09-security/README.md)  
**Next**: [Chapter 11: Microservices →](../11-microservices/README.md)
