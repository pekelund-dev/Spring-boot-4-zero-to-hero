package com.example.helloworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hello World Spring Boot Application
 * 
 * This is the simplest possible Spring Boot application demonstrating:
 * - @SpringBootApplication annotation
 * - Embedded web server
 * - REST endpoints
 * - Path variables and query parameters
 */
@SpringBootApplication
@RestController
public class HelloWorldApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloWorldApplication.class, args);
    }

    /**
     * Simple GET endpoint
     * Access: http://localhost:8080/
     */
    @GetMapping("/")
    public String hello() {
        return "Hello, Spring Boot 4.x! 🚀";
    }

    /**
     * GET endpoint with path variable
     * Access: http://localhost:8080/hello/John
     */
    @GetMapping("/hello/{name}")
    public String helloName(@PathVariable String name) {
        return String.format("Hello, %s! Welcome to Spring Boot 4.x!", name);
    }

    /**
     * GET endpoint with query parameter
     * Access: http://localhost:8080/greet?name=John&language=en
     */
    @GetMapping("/greet")
    public String greet(
            @RequestParam(defaultValue = "World") String name,
            @RequestParam(defaultValue = "en") String language) {
        
        return switch (language.toLowerCase()) {
            case "es" -> String.format("¡Hola, %s!", name);
            case "fr" -> String.format("Bonjour, %s!", name);
            case "de" -> String.format("Hallo, %s!", name);
            case "it" -> String.format("Ciao, %s!", name);
            case "ja" -> String.format("こんにちは, %s!", name);
            default -> String.format("Hello, %s!", name);
        };
    }

    /**
     * Health check endpoint
     * Access: http://localhost:8080/health
     */
    @GetMapping("/health")
    public HealthResponse health() {
        return new HealthResponse("UP", "Hello World Application is running!");
    }

    /**
     * Simple DTO for health response
     */
    record HealthResponse(String status, String message) {}
}
