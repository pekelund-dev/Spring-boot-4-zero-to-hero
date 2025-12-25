# Chapter 6: Spring Boot Configuration

Spring Boot's configuration system is flexible and powerful. This chapter explores how to configure your applications for different environments and use cases.

## Configuration Files

### application.properties

```properties
# Server
server.port=8080
server.servlet.context-path=/api

# Application
spring.application.name=myapp

# Logging
logging.level.root=INFO
logging.level.com.example=DEBUG
logging.file.name=logs/app.log

# Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=
```

### application.yml (Preferred)

```yaml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  application:
    name: myapp
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password: ''

logging:
  level:
    root: INFO
    com.example: DEBUG
  file:
    name: logs/app.log
```

## Profile-Specific Configuration

### Create Profile Files

```
application.yml           # Default
application-dev.yml       # Development
application-test.yml      # Testing
application-prod.yml      # Production
```

### Development Profile

```yaml
# application-dev.yml
spring:
  datasource:
    url: jdbc:h2:mem:devdb
logging:
  level:
    root: DEBUG
```

### Production Profile

```yaml
# application-prod.yml
spring:
  datasource:
    url: jdbc:postgresql://prod-server:5432/mydb
    username: ${DB_USER}
    password: ${DB_PASSWORD}
logging:
  level:
    root: WARN
```

### Activating Profiles

**application.properties**:
```properties
spring.profiles.active=dev
```

**Command line**:
```bash
java -jar app.jar --spring.profiles.active=prod
```

**Environment variable**:
```bash
export SPRING_PROFILES_ACTIVE=prod
```

## @ConfigurationProperties

### Type-Safe Configuration

```java
@ConfigurationProperties(prefix = "app")
@Validated
@Component
public class AppProperties {
    @NotBlank
    private String name;
    
    @Min(1)
    @Max(100)
    private int maxConnections;
    
    private Security security = new Security();
    
    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getMaxConnections() { return maxConnections; }
    public void setMaxConnections(int maxConnections) { 
        this.maxConnections = maxConnections; 
    }
    
    public Security getSecurity() { return security; }
    public void setSecurity(Security security) { this.security = security; }
    
    public static class Security {
        private boolean enabled = true;
        private String secret;
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        
        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
    }
}
```

**Configuration**:
```yaml
app:
  name: MyApplication
  max-connections: 50
  security:
    enabled: true
    secret: ${APP_SECRET}
```

### Using Configuration Properties

```java
@Service
@RequiredArgsConstructor
public class AppService {
    private final AppProperties properties;
    
    public void doSomething() {
        String name = properties.getName();
        int connections = properties.getMaxConnections();
        // Use configuration
    }
}
```

## Environment Variables

### Reading Environment Variables

```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DB_USER:admin}      # Default value: admin
    password: ${DB_PASSWORD:secret} # Default value: secret
```

### Precedence Order

1. Command line arguments
2. Java System properties
3. OS environment variables
4. Profile-specific files
5. application.properties/yml
6. Default properties

## Custom Property Sources

### Load Properties from External File

```java
@Configuration
@PropertySource("file:/config/external.properties")
public class ExternalConfig {
    @Value("${custom.property}")
    private String customProperty;
}
```

### Multiple Property Sources

```java
@Configuration
@PropertySources({
    @PropertySource("classpath:app.properties"),
    @PropertySource("file:/config/override.properties")
})
public class AppConfig {
    // Configuration
}
```

## @Value Annotation

```java
@Component
public class MessageService {
    
    @Value("${app.message:Hello}")  // Default: Hello
    private String message;
    
    @Value("${app.timeout:30}")
    private int timeout;
    
    @Value("${app.enabled:true}")
    private boolean enabled;
    
    @Value("#{systemProperties['user.home']}")
    private String userHome;
    
    @Value("#{${app.numbers}}")  // For lists
    private List<Integer> numbers;
}
```

## Configuration Validation

```java
@ConfigurationProperties(prefix = "app")
@Validated
@Component
public class ValidatedConfig {
    
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 3, max = 50)
    private String name;
    
    @Min(1)
    @Max(65535)
    private int port;
    
    @Email
    private String adminEmail;
    
    @Pattern(regexp = "^(dev|test|prod)$")
    private String environment;
    
    // Getters and setters
}
```

## Conditional Configuration

```java
@Configuration
public class ConditionalConfig {
    
    @Bean
    @ConditionalOnProperty(name = "feature.cache", havingValue = "true")
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }
    
    @Bean
    @ConditionalOnMissingBean
    public EmailService defaultEmailService() {
        return new ConsoleEmailService();
    }
    
    @Bean
    @Profile("!prod")  // Not in production
    public DataSource h2DataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .build();
    }
}
```

## Externalized Configuration

### 1. File Locations (Priority Order)

1. `/config` subdirectory of current directory
2. Current directory
3. Classpath `/config` package
4. Classpath root

### 2. Using Spring Cloud Config

```yaml
spring:
  cloud:
    config:
      uri: http://config-server:8888
      name: myapp
      profile: prod
```

### 3. Using Environment Variables

```bash
export SERVER_PORT=9000
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost/mydb
```

## Common Configuration Properties

### Server Configuration

```yaml
server:
  port: 8080
  address: 0.0.0.0
  shutdown: graceful
  compression:
    enabled: true
  ssl:
    enabled: false
```

### Logging Configuration

```yaml
logging:
  level:
    root: INFO
    org.springframework.web: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
  file:
    name: logs/application.log
    max-size: 10MB
```

### Jackson Configuration

```yaml
spring:
  jackson:
    serialization:
      indent-output: true
      write-dates-as-timestamps: false
    deserialization:
      fail-on-unknown-properties: false
```

## Best Practices

### ✅ Do

1. **Use YAML for complex configurations**
2. **Use profiles for environments**
3. **Validate configuration with @Validated**
4. **Use environment variables for secrets**
5. **Document custom properties**

### ❌ Don't

1. **Don't hardcode values**
2. **Don't commit secrets to Git**
3. **Don't use @Value excessively** (use @ConfigurationProperties)
4. **Don't mix configuration styles**

## Summary

You learned:
- Configuration files (properties vs YAML)
- Profile-specific configuration
- Type-safe configuration with @ConfigurationProperties
- Environment variables
- Configuration validation
- Externalized configuration
- Best practices

---

## 🏋️ Exercises

**Exercise 1**: Create dev, test, prod profiles  
**Exercise 2**: Build type-safe configuration class  
**Exercise 3**: Add configuration validation  
**Exercise 4**: Use environment variables  
**Exercise 5**: Create conditional beans

## 🎯 Quiz

**Q1**: Preferred configuration format?  
✅ YAML

**Q2**: Activate profile via command line?  
✅ --spring.profiles.active=prod

**Q3**: Type-safe configuration annotation?  
✅ @ConfigurationProperties

**Q4**: Default value in @Value?  
✅ ${property:defaultValue}

**Q5**: Highest priority configuration?  
✅ Command line arguments

---

**Previous**: [← Chapter 5: Dependency Injection](../05-dependency-injection/README.md)  
**Next**: [Chapter 7: RESTful Web Services →](../07-rest-services/README.md)
