# Appendix D: Migration Guide from Spring Boot 3.x to 4.x

This guide helps you migrate existing Spring Boot 3.x applications to Spring Boot 4.x. While Spring Boot maintains backward compatibility where possible, there are important changes to be aware of.

> **Note**: Spring Boot 4.0 is used as a reference for this guide. As Spring Boot 4.x is built on concepts similar to 3.x but with enhancements, this guide focuses on conceptual improvements and best practices for migration.

## Overview

### What's New in Spring Boot 4.x

- Enhanced Java 21+ support with Virtual Threads
- Improved native image compilation with GraalVM
- Better observability with OpenTelemetry integration
- Performance improvements and reduced startup time
- Updated dependency versions across the ecosystem
- Enhanced security features

### Migration Complexity

**Easy** (< 1 day):
- Simple REST APIs
- Basic CRUD applications
- Applications without deprecated features

**Moderate** (1-3 days):
- Applications with complex configurations
- Custom auto-configurations
- Applications using Spring Cloud

**Complex** (1+ weeks):
- Large enterprise applications
- Heavy customization of Spring internals
- Applications using deprecated features

## Prerequisites

Before starting the migration:

- [ ] Ensure your application works correctly on Spring Boot 3.x
- [ ] Update to the latest Spring Boot 3.x version first
- [ ] Review and fix all deprecation warnings
- [ ] Ensure tests have good coverage
- [ ] Create a backup or branch
- [ ] Document custom configurations

## Step-by-Step Migration Process

### Step 1: Update Java Version

Spring Boot 4.x requires Java 21 or later.

**Before (Spring Boot 3.x)**:
```xml
<properties>
    <java.version>17</java.version>
</properties>
```

**After (Spring Boot 4.x)**:
```xml
<properties>
    <java.version>21</java.version>
</properties>
```

#### Update Java in Your Environment

```bash
# Install Java 21
sdk install java 21-open

# Set as default
sdk default java 21-open

# Verify
java -version
```

### Step 2: Update Spring Boot Version

#### Maven

**Before**:
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.1</version>
</parent>
```

**After**:
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.0.0</version>
</parent>
```

#### Gradle

**Before**:
```groovy
plugins {
    id 'org.springframework.boot' version '3.2.1'
}
```

**After**:
```groovy
plugins {
    id 'org.springframework.boot' version '4.0.0'
}
```

### Step 3: Update Dependencies

Run dependency updates:

```bash
# Maven
./mvnw versions:display-dependency-updates

# Gradle
./gradlew dependencyUpdates
```

### Step 4: Build and Review Errors

```bash
# Maven
./mvnw clean compile

# Gradle
./gradlew clean build
```

Address compilation errors as they appear.

## Key Changes and How to Handle Them

### 1. Jakarta EE Namespace (if not already migrated in 3.x)

If you're coming from Spring Boot 2.x, note that javax.* packages have moved to jakarta.*

**Before**:
```java
import javax.persistence.Entity;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
```

**After**:
```java
import jakarta.persistence.Entity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
```

**Automated Migration**:
```bash
# Use OpenRewrite recipes
./mvnw org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.activeRecipes=org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta
```

### 2. Configuration Property Changes

Some configuration properties have been renamed or restructured.

**Before (Spring Boot 3.x)**:
```yaml
spring:
  datasource:
    type: com.zaxxer.hikari.HikariDataSource
```

**After (Spring Boot 4.x)**:
```yaml
spring:
  datasource:
    hikari:
      # HikariCP is still the default
```

### 3. Deprecation Removals

Features deprecated in Spring Boot 3.x may be removed in 4.x.

#### Example: Constructor Binding

**Before**:
```java
@ConfigurationProperties("app")
@ConstructorBinding
public class AppProperties {
    private final String name;
    
    public AppProperties(String name) {
        this.name = name;
    }
}
```

**After** (if changed):
```java
@ConfigurationProperties("app")
public class AppProperties {
    private final String name;
    
    // Constructor binding is now implied for records and immutable classes
    public AppProperties(String name) {
        this.name = name;
    }
}

// Or use records (recommended)
@ConfigurationProperties("app")
public record AppProperties(String name) {}
```

### 4. Actuator Changes

Actuator endpoints may have updated paths or configurations.

**Before**:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

**After**:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  # Additional observability features
  tracing:
    sampling:
      probability: 1.0
```

### 5. Security Configuration

Security configuration continues to use the component-based approach introduced in Spring Security 6.

**Modern Approach** (works in both 3.x and 4.x):
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            )
            .logout(logout -> logout
                .permitAll()
            );
        
        return http.build();
    }
}
```

### 6. Virtual Threads Support

Spring Boot 4.x has enhanced support for Java 21's virtual threads.

**Enable Virtual Threads**:
```yaml
spring:
  threads:
    virtual:
      enabled: true
```

**Or programmatically**:
```java
@Configuration
public class AsyncConfig {
    
    @Bean
    public AsyncTaskExecutor applicationTaskExecutor() {
        TaskExecutorAdapter adapter = new TaskExecutorAdapter(
            Executors.newVirtualThreadPerTaskExecutor()
        );
        return adapter;
    }
}
```

### 7. Native Image Improvements

GraalVM native image support is more mature in Spring Boot 4.x.

**Build Native Image**:
```bash
# Maven
./mvnw -Pnative native:compile

# Gradle
./gradlew nativeCompile
```

**Required hints** (most are auto-configured):
```java
@RegisterReflectionForBinding(UserDTO.class)
@SpringBootApplication
public class MyApplication {
    // ...
}
```

### 8. Testing Changes

**Before** (if using older syntax):
```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class MyTest {
    // ...
}
```

**After**:
```java
@SpringBootTest
public class MyTest {
    // No @RunWith needed with JUnit 5
}
```

## Common Migration Issues

### Issue 1: Compilation Errors with Removed APIs

**Error**: `Cannot find symbol` or `Class not found`

**Solution**: 
- Check Spring Boot 4.0 release notes for removed classes
- Find replacement APIs
- Update import statements

### Issue 2: Configuration Not Loading

**Error**: Properties not being bound

**Solution**:
```java
// Ensure @ConfigurationPropertiesScan is present
@SpringBootApplication
@ConfigurationPropertiesScan
public class MyApplication {
    // ...
}
```

### Issue 3: Test Failures

**Error**: Tests that passed in 3.x fail in 4.x

**Solution**:
- Review test configurations
- Update test dependencies
- Check for changed default behaviors

### Issue 4: Dependency Conflicts

**Error**: Version conflicts between dependencies

**Solution**:
```xml
<!-- Use Spring Boot's dependency management -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>4.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

## Migration Checklist

### Before Migration
- [ ] Update to latest Spring Boot 3.x version
- [ ] Fix all deprecation warnings
- [ ] Ensure all tests pass
- [ ] Document custom configurations
- [ ] Review Spring Boot 4.0 release notes

### During Migration
- [ ] Update Java to version 21
- [ ] Update Spring Boot version to 4.0.x
- [ ] Update all Spring dependencies
- [ ] Fix compilation errors
- [ ] Update configuration files
- [ ] Address deprecation warnings

### After Migration
- [ ] Run all tests
- [ ] Perform integration testing
- [ ] Test in staging environment
- [ ] Review application performance
- [ ] Update documentation
- [ ] Train team on new features

## Testing Your Migration

### 1. Unit Tests

```bash
./mvnw test
```

Ensure all unit tests pass.

### 2. Integration Tests

```bash
./mvnw verify
```

Run full integration test suite.

### 3. Manual Testing

- Test all critical user flows
- Verify authentication and authorization
- Check database connectivity
- Test external integrations
- Verify API endpoints

### 4. Performance Testing

Compare performance metrics:
- Application startup time
- Memory usage
- Response times
- Throughput

## Leveraging New Features

### 1. Virtual Threads

```java
@Service
public class UserService {
    
    @Async
    public CompletableFuture<User> fetchUser(Long id) {
        // Automatically uses virtual threads if enabled
        return CompletableFuture.supplyAsync(() -> {
            return userRepository.findById(id).orElseThrow();
        });
    }
}
```

### 2. Enhanced Observability

```yaml
management:
  tracing:
    sampling:
      probability: 1.0
  metrics:
    export:
      prometheus:
        enabled: true
```

### 3. Improved Auto-Configuration

Take advantage of new auto-configurations:
```java
// Less manual configuration needed
@SpringBootApplication
public class MyApplication {
    // More features work out-of-the-box
}
```

## Rollback Plan

If migration fails, have a rollback plan:

1. **Keep Spring Boot 3.x branch**:
```bash
git checkout -b spring-boot-3.x-stable
```

2. **Document migration issues**

3. **Prepare rollback procedure**:
```bash
# Revert to previous version
git checkout main
git revert <migration-commit>
```

## Best Practices

1. **Migrate in stages**: 
   - First, update to latest 3.x
   - Then, migrate to 4.x
   - Finally, adopt new features

2. **Test thoroughly**: 
   - Run automated tests
   - Perform manual testing
   - Test in staging environment

3. **Monitor after deployment**:
   - Watch for errors
   - Monitor performance
   - Check logs

4. **Keep dependencies updated**:
   - Regularly update dependencies
   - Address security vulnerabilities
   - Follow Spring Boot releases

## Resources

### Official Documentation
- [Spring Boot 4.0 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Release-Notes)
- [Spring Boot Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)

### Tools
- [OpenRewrite](https://docs.openrewrite.org/) - Automated migration tool
- [Spring Boot Migrator](https://github.com/spring-projects-experimental/spring-boot-migrator)

### Community Support
- [Stack Overflow - Spring Boot](https://stackoverflow.com/questions/tagged/spring-boot)
- [Spring Community Forums](https://community.spring.io/)
- [GitHub Issues](https://github.com/spring-projects/spring-boot/issues)

## Summary

Migrating from Spring Boot 3.x to 4.x is generally straightforward:

1. **Update Java to 21**
2. **Update Spring Boot version**
3. **Update dependencies**
4. **Fix compilation errors**
5. **Test thoroughly**
6. **Deploy and monitor**

The benefits of Spring Boot 4.x—improved performance, better observability, and enhanced features—make the migration worthwhile.

---

**Related Resources:**
- [Chapter 1: Introduction to Spring Boot 4.x](../chapters/01-introduction/README.md)
- [Appendix A: Best Practices](./best-practices.md)
- [Main Guide](../README.md)
