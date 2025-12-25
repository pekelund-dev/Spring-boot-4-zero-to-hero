# Adding Content to the Interactive Platform

This guide explains how to integrate the existing markdown chapters with the interactive platform.

## Overview

The interactive platform is designed to work alongside the existing markdown-based course content. Here's how to integrate them:

## 1. Content Service (Future Enhancement)

Create a `ContentService` that reads markdown files from the `chapters/` directory:

```java
@Service
public class ContentService {
    
    @Value("${content.path}")
    private String contentPath;
    
    public String getChapterContent(String chapterId, UserPreferences prefs) {
        // Read markdown file
        // Parse and customize based on user preferences
        // Return HTML content
    }
}
```

## 2. Quiz Data Structure

Create JSON files for each chapter's quiz:

```json
{
  "chapterId": "01-introduction",
  "questions": [
    {
      "id": 1,
      "question": "What is Spring Boot?",
      "options": [
        "A web framework",
        "An opinionated framework for building Spring applications",
        "A database",
        "A programming language"
      ],
      "correctAnswer": 1,
      "explanation": "Spring Boot is an opinionated framework..."
    }
  ]
}
```

## 3. Exercise Templates

Create exercise definitions with test cases:

```json
{
  "exerciseId": "hello-controller",
  "chapterId": "01-introduction",
  "title": "Create Your First Controller",
  "description": "Create a REST controller that returns 'Hello, Spring Boot!'",
  "starterCode": "@RestController\npublic class HelloController {\n\n}",
  "testCases": [
    {
      "input": "GET /hello",
      "expectedOutput": "Hello, Spring Boot!",
      "points": 10
    }
  ]
}
```

## 4. Customizing Content by User Preferences

### Maven vs Gradle Examples

```java
if (prefs.getBuildTool() == BuildTool.MAVEN) {
    return """
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        """;
} else {
    return """
        implementation 'org.springframework.boot:spring-boot-starter-web'
        """;
}
```

### OS-Specific Commands

```java
switch (prefs.getOperatingSystem()) {
    case MAC, LINUX -> return "chmod +x mvnw && ./mvnw spring-boot:run";
    case WINDOWS -> return "mvnw.cmd spring-boot:run";
    case WINDOWS_WSL2 -> return "wsl chmod +x mvnw && ./mvnw spring-boot:run";
}
```

### IDE-Specific Instructions

```java
if (prefs.getIde() == IDE.INTELLIJ_IDEA) {
    return "Right-click on the main class and select 'Run'";
} else if (prefs.getIde() == IDE.VS_CODE) {
    return "Press F5 or click 'Run' in the Run and Debug panel";
}
```

## 5. REST Endpoint for Chapter Content

Add to `ContentController.java`:

```java
@RestController
@RequestMapping("/api/content")
public class ContentController {
    
    @Autowired
    private ContentService contentService;
    
    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<ChapterContent> getChapter(
            @PathVariable String chapterId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        UserPreferences prefs = userService.getUserPreferences(userDetails.getUsername());
        ChapterContent content = contentService.getChapterContent(chapterId, prefs);
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/quiz/{chapterId}")
    public ResponseEntity<Quiz> getQuiz(@PathVariable String chapterId) {
        Quiz quiz = contentService.getQuiz(chapterId);
        return ResponseEntity.ok(quiz);
    }
    
    @GetMapping("/exercise/{exerciseId}")
    public ResponseEntity<Exercise> getExercise(@PathVariable String exerciseId) {
        Exercise exercise = contentService.getExercise(exerciseId);
        return ResponseEntity.ok(exercise);
    }
}
```

## 6. Frontend Integration

Update the frontend to fetch and display content dynamically:

```javascript
async function loadChapter(chapterId) {
    const response = await fetch(`/api/content/chapter/${chapterId}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });
    const content = await response.json();
    displayContent(content);
}
```

## 7. Markdown Processing

Use the Flexmark library (already in dependencies) to convert markdown to HTML:

```java
MutableDataSet options = new MutableDataSet();
Parser parser = Parser.builder(options).build();
HtmlRenderer renderer = HtmlRenderer.builder(options).build();

Node document = parser.parse(markdownContent);
String html = renderer.render(document);
```

## 8. Directory Structure for Content

```
interactive-platform/
└── src/main/resources/
    └── content/
        ├── quizzes/
        │   ├── chapter-01.json
        │   ├── chapter-02.json
        │   └── ...
        └── exercises/
            ├── hello-controller.json
            ├── dependency-injection.json
            └── ...
```

## 9. Badge Criteria Mapping

Update `BadgeService` to map chapter IDs to badge requirements:

```java
private static final Map<String, Integer> CHAPTER_BADGES = Map.of(
    "First Chapter", 1,
    "Five Chapters", 5,
    "Ten Chapters", 10,
    "Course Complete", 13
);
```

## 10. Real-time Code Execution (Advanced)

For actual code execution, consider:

1. **Docker Containers**: Spin up isolated containers for code execution
2. **Sandboxing**: Use Java SecurityManager or external sandboxing services
3. **Time Limits**: Enforce execution time limits
4. **Resource Limits**: Limit CPU and memory usage

Example with Docker:

```java
public ExerciseResult executeCode(String code) {
    // Create temporary directory
    // Write code to file
    // Build Docker image
    // Run container with limits
    // Capture output
    // Clean up
    return new ExerciseResult(output, passed);
}
```

## Example: Complete Flow

1. User logs in → Save preferences
2. User selects Chapter 1 → Fetch content customized for their setup
3. User reads content → Track progress
4. User completes exercise → Submit code, get feedback
5. User takes quiz → Record score
6. System awards badges → Update leaderboard

## Testing Content Integration

```java
@Test
public void testContentCustomization() {
    UserPreferences prefs = new UserPreferences();
    prefs.setBuildTool(BuildTool.MAVEN);
    prefs.setOperatingSystem(OperatingSystem.MAC);
    
    String content = contentService.getChapterContent("01", prefs);
    
    assertTrue(content.contains("<dependency>"));
    assertTrue(content.contains("./mvnw"));
}
```

## Best Practices

1. **Cache processed content** to avoid re-parsing markdown
2. **Version control** quiz and exercise definitions
3. **A/B test** different explanations for effectiveness
4. **Collect analytics** on where students struggle
5. **Regular updates** to exercises based on feedback

## Future Enhancements

- Video integration with YouTube API
- Community discussion forum
- Peer code review system
- AI-powered hints using OpenAI API
- Mobile app version
- Offline mode support

---

This guide provides the foundation for connecting the interactive platform with actual course content. The platform is designed to be extensible and can be enhanced incrementally!
