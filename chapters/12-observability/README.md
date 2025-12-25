# Chapter 12: Observability and Monitoring

Observability is essential for understanding system behavior in production. This chapter covers monitoring, metrics, logging, and tracing.

## Spring Boot Actuator

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### Configuration

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
  metrics:
    export:
      prometheus:
        enabled: true
```

### Health Checks

```java
@Component
public class CustomHealthIndicator implements HealthIndicator {
    
    @Override
    public Health health() {
        boolean databaseUp = checkDatabase();
        
        if (databaseUp) {
            return Health.up()
                .withDetail("database", "Available")
                .build();
        }
        
        return Health.down()
            .withDetail("database", "Unavailable")
            .build();
    }
    
    private boolean checkDatabase() {
        // Check database connectivity
        return true;
    }
}
```

### Info Endpoint

```yaml
info:
  app:
    name: @project.name@
    version: @project.version@
    description: My Application
```

## Metrics with Micrometer

### Custom Metrics

```java
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository repository;
    private final MeterRegistry meterRegistry;
    
    public User createUser(String username) {
        // Counter
        meterRegistry.counter("users.created").increment();
        
        User user = new User(username);
        return repository.save(user);
    }
    
    @Timed(value = "users.get", description = "Time to get user")
    public User getUser(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    
    public void recordUserCount() {
        // Gauge
        Gauge.builder("users.total", repository, UserRepository::count)
            .register(meterRegistry);
    }
}
```

## Prometheus Integration

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

Access metrics: `http://localhost:8080/actuator/prometheus`

### Prometheus Configuration

```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'spring-boot-app'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8080']
```

## Grafana Dashboards

### Connect to Prometheus

1. Add Prometheus data source
2. Import Spring Boot dashboard (ID: 4701)
3. Visualize metrics

### Example Queries

```promql
# Request rate
rate(http_server_requests_seconds_count[5m])

# Memory usage
jvm_memory_used_bytes

# Error rate
rate(http_server_requests_seconds_count{status="500"}[5m])
```

## Structured Logging

### Logback Configuration

```xml
<!-- logback-spring.xml -->
<configuration>
    <appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="JSON"/>
    </root>
</configuration>
```

### Logging in Code

```java
@Slf4j
@Service
public class UserService {
    
    public User createUser(String username) {
        log.info("Creating user: {}", username);
        
        try {
            User user = new User(username);
            user = repository.save(user);
            log.info("User created successfully: id={}", user.getId());
            return user;
        } catch (Exception e) {
            log.error("Failed to create user: {}", username, e);
            throw e;
        }
    }
}
```

### MDC for Correlation

```java
@Component
public class CorrelationIdFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
```

## Distributed Tracing

### OpenTelemetry

```xml
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-spring-boot-starter</artifactId>
</dependency>
```

```yaml
management:
  tracing:
    sampling:
      probability: 1.0
  zipkin:
    tracing:
      endpoint: http://localhost:9411/api/v2/spans
```

### Custom Spans

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final Tracer tracer;
    
    public Order processOrder(OrderRequest request) {
        Span span = tracer.spanBuilder("process-order").startSpan();
        
        try (Scope scope = span.makeCurrent()) {
            span.setAttribute("order.id", request.getId());
            span.setAttribute("order.amount", request.getAmount());
            
            // Business logic
            Order order = createOrder(request);
            
            span.addEvent("Order processed successfully");
            return order;
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR);
            throw e;
        } finally {
            span.end();
        }
    }
}
```

## Application Performance Monitoring

### Key Metrics to Monitor

**Application Metrics**:
- Request rate
- Response time
- Error rate
- Throughput

**JVM Metrics**:
- Heap memory
- GC pauses
- Thread count
- CPU usage

**Database Metrics**:
- Connection pool
- Query time
- Slow queries

## Alerting

### Prometheus Alerts

```yaml
# alerts.yml
groups:
  - name: application
    rules:
      - alert: HighErrorRate
        expr: rate(http_server_requests_seconds_count{status="500"}[5m]) > 0.05
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: High error rate detected
          
      - alert: HighMemoryUsage
        expr: jvm_memory_used_bytes / jvm_memory_max_bytes > 0.9
        for: 5m
        labels:
          severity: warning
```

## Best Practices

### ✅ Do

1. **Enable Actuator in production**
2. **Monitor key business metrics**
3. **Use structured logging**
4. **Implement distributed tracing**
5. **Set up alerts**

### ❌ Don't

1. **Don't expose all endpoints publicly**
2. **Don't ignore high cardinality metrics**
3. **Don't log sensitive data**
4. **Don't skip health checks**

## Summary

You learned:
- Spring Boot Actuator
- Custom metrics with Micrometer
- Prometheus and Grafana
- Structured logging
- Distributed tracing
- Application performance monitoring
- Alerting strategies

---

## 🏋️ Exercises

**Exercise 1**: Enable Actuator and explore endpoints  
**Exercise 2**: Create custom metrics  
**Exercise 3**: Set up Prometheus and Grafana  
**Exercise 4**: Implement structured logging  
**Exercise 5**: Add distributed tracing

## 🎯 Quiz

**Q1**: Production monitoring tool?  
✅ Spring Boot Actuator

**Q2**: Metrics library?  
✅ Micrometer

**Q3**: Time-series database?  
✅ Prometheus

**Q4**: Visualization tool?  
✅ Grafana

**Q5**: Distributed tracing?  
✅ OpenTelemetry/Zipkin

---

**Previous**: [← Chapter 11: Microservices](../11-microservices/README.md)  
**Next**: [Chapter 13: Advanced Topics →](../13-advanced/README.md)
