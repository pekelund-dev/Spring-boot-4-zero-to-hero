# Chapter 4: Building Your First Application

Now that you understand Spring Boot's core concepts, it's time to build your first real application! This chapter guides you through creating a complete, working Spring Boot application from scratch.

## Creating a New Project

### Using Spring Initializr (Web)

Visit [start.spring.io](https://start.spring.io) and configure:

- **Project**: Maven
- **Language**: Java  
- **Spring Boot**: 3.2.1
- **Group**: com.example
- **Artifact**: myapp
- **Packaging**: Jar
- **Java**: 21

**Dependencies**:
- Spring Web
- Spring Boot DevTools
- Lombok

Click **Generate** to download the project.

### Project Structure

```
myapp/
├── src/
│   ├── main/
│   │   ├── java/com/example/myapp/
│   │   │   └── MyappApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/java/com/example/myapp/
│       └── MyappApplicationTests.java
├── pom.xml
└── mvnw
```

## The Main Application Class

```java
package com.example.myapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MyappApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyappApplication.class, args);
    }
}
```

This single annotation `@SpringBootApplication` does three things:
1. **@Configuration**: Marks class as configuration source
2. **@EnableAutoConfiguration**: Enables auto-configuration
3. **@ComponentScan**: Scans for components in this package and sub-packages

## Running the Application

### From IDE
- IntelliJ: Right-click main class → Run
- VS Code: Click Run button in Spring Boot Dashboard

### From Command Line
```bash
./mvnw spring-boot:run
```

### Build and Run JAR
```bash
./mvnw clean package
java -jar target/myapp-0.0.1-SNAPSHOT.jar
```

## Creating Your First Controller

```java
package com.example.myapp.controller;

import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api")
public class HelloController {
    
    @GetMapping("/hello")
    public String hello() {
        return "Hello, Spring Boot!";
    }
    
    @GetMapping("/time")
    public Map<String, Object> getTime() {
        return Map.of(
            "timestamp", LocalDateTime.now(),
            "timezone", "UTC"
        );
    }
    
    @GetMapping("/greet/{name}")
    public Greeting greet(@PathVariable String name) {
        return new Greeting("Hello, " + name + "!");
    }
    
    record Greeting(String message) {}
}
```

Test it:
```bash
curl http://localhost:8080/api/hello
curl http://localhost:8080/api/greet/John
```

## Spring Boot DevTools

DevTools provides:
- **Automatic restart** when classpath changes
- **LiveReload** browser plugin support
- **Property defaults** for development

Already included in your dependencies:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

Try it: Modify your controller and save - the app restarts automatically!

## Building a Complete Application

Let's build a task management API.

### 1. Domain Model

```java
package com.example.myapp.model;

import java.time.LocalDateTime;

public class Task {
    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;
    
    public Task(Long id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = false;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
```

### 2. Service Layer

```java
package com.example.myapp.service;

import com.example.myapp.model.Task;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskService {
    private final Map<Long, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong();
    
    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }
    
    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(tasks.get(id));
    }
    
    public Task create(String title, String description) {
        Long id = counter.incrementAndGet();
        Task task = new Task(id, title, description);
        tasks.put(id, task);
        return task;
    }
    
    public boolean delete(Long id) {
        return tasks.remove(id) != null;
    }
}
```

### 3. Controller

```java
package com.example.myapp.controller;

import com.example.myapp.model.Task;
import com.example.myapp.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;
    
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
    
    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Long id) {
        return taskService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Task createTask(@RequestBody TaskRequest request) {
        return taskService.create(request.title(), request.description());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        return taskService.delete(id)
            ? ResponseEntity.noContent().build()
            : ResponseEntity.notFound().build();
    }
    
    record TaskRequest(String title, String description) {}
}
```

## Configuration

### application.properties

```properties
server.port=8080
spring.application.name=myapp
logging.level.com.example.myapp=DEBUG
```

## Packaging and Deployment

### Create JAR

```bash
./mvnw clean package
```

Creates: `target/myapp-0.0.1-SNAPSHOT.jar`

### Run JAR

```bash
java -jar target/myapp-0.0.1-SNAPSHOT.jar
```

This is a "fat JAR" containing:
- Your code
- All dependencies  
- Embedded Tomcat

### Custom Port

```bash
java -jar target/myapp-0.0.1-SNAPSHOT.jar --server.port=9000
```

## Summary

You learned:
- Creating Spring Boot projects
- Understanding the main class
- Building REST APIs
- Using DevTools
- Packaging applications

---

## 🏋️ Exercises

**Exercise 1**: Add update endpoint for tasks  
**Exercise 2**: Add filtering by completion status  
**Exercise 3**: Add due date to tasks  
**Exercise 4**: Create a counter API  
**Exercise 5**: Change port to 8081

## 🎯 Quiz

**Q1**: What does @SpringBootApplication combine?  
✅ @Configuration, @EnableAutoConfiguration, @ComponentScan

**Q2**: Default embedded server?  
✅ Tomcat

**Q3**: Command to run with Maven?  
✅ ./mvnw spring-boot:run

**Q4**: What is a fat JAR?  
✅ JAR with all dependencies

**Q5**: DevTools scope?  
✅ runtime

---

**Previous**: [← Chapter 3: Core Concepts](../03-core-concepts/README.md)  
**Next**: [Chapter 5: Dependency Injection →](../05-dependency-injection/README.md)
