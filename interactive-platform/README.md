# Spring Boot 4 Interactive Learning Platform

An interactive web-based learning platform for the Spring Boot 4 course, featuring user authentication, personalized learning paths, coding exercises, quizzes, and gamification.

## Features

### 🔐 Authentication
- Google OAuth2 login integration
- JWT-based session management
- Secure user profile management

### ⚙️ Personalized Learning
- Initial setup wizard for user preferences:
  - Operating System (Mac/Windows/Linux/WSL2)
  - Build Tool (Maven/Gradle)
  - Java Version (21/23/25)
  - IDE (IntelliJ IDEA/VS Code/Eclipse/NetBeans)
- Content customized based on user preferences

### 📚 Interactive Course Content
- 13 comprehensive chapters
- Progress tracking for each section
- Personalized code examples based on user setup

### 💻 Interactive Code Exercises
- In-browser code editor
- Code submission and validation
- Instant feedback on solutions
- Hints and guidance for learning

### ❓ Chapter Quizzes
- Multiple choice questions
- Instant feedback and explanations
- Score tracking and history
- Completion certificates

### 🏆 Gamification System
- Badge system for achievements:
  - Chapter completion badges
  - Quiz master badges
  - Exercise completion badges
  - Special achievement badges
- User leaderboard
- Progress statistics
- Competitive learning environment

### 📊 Progress Dashboard
- Visual progress indicators
- Chapter completion tracking
- Quiz performance analytics
- Badge collection display
- Personal statistics

## Technology Stack

### Backend
- **Spring Boot 4.0.1** (latest GA release with Java 25 support)
- **Spring Security** with OAuth2
- **Spring Data JPA** for persistence
- **H2 Database** (development) / PostgreSQL (production)
- **JWT** for token management
- **Java 25** (default) with support for Java 17 and 21

### Frontend
- HTML5/CSS3/JavaScript
- Responsive design
- Interactive components
- Real-time feedback

## Getting Started

### Prerequisites
- **Java 25** (required for default configuration)
- Maven 3.9+ (for Java 25 support)
- Google OAuth2 credentials (for authentication)

### Important: Java 25 Configuration &amp; Troubleshooting

**Spring Boot Version:** This project uses **Spring Boot 4.0.1**, the latest GA release with full Java 25 support.

**For Java 25 (default):**
1. Ensure you have JDK 25 installed (full JDK, not just JRE):
```bash
java -version  # Should show "openjdk version \"25\"" or similar
echo $JAVA_HOME  # Should point to JDK 25 installation directory
```

2. Ensure you have Maven 3.9.9 or higher:
```bash
mvn -version  # Should show Maven 3.9.9+
# Maven must also report "Java version: 25"
```

3. Verify your JDK supports the release flag:
```bash
javac --release 25 --version
# Should succeed without errors
```

**If you encounter "error: release version 25 not supported":**

This error usually means one of the following:
1. **JAVA_HOME is not set to JDK 25** - Maven is using a different Java version
2. **Maven's Java is different from command line Java** - Check `mvn -version`
3. **JDK 25 installation is incomplete** - Reinstall JDK 25
4. **Compiler doesn't recognize release 25** - Your javac version may not support it yet

**Solutions:**

**Option 1: Fix Java 25 Setup (Recommended)**
```bash
# On Linux/Mac - set JAVA_HOME
export JAVA_HOME=/path/to/jdk-25
export PATH=$JAVA_HOME/bin:$PATH

# On Windows
set JAVA_HOME=C:\path\to\jdk-25
set PATH=%JAVA_HOME%\bin;%PATH%

# Verify
mvn -version  # Should show Java version: 25
javac --release 25 --version  # Should work

# Clean and rebuild
cd interactive-platform
mvn clean compile
```

**Option 2: Use Java 21 LTS (Temporary Workaround)**
If Java 25 setup continues to fail, use Java 21 (Long Term Support version):

1. Update `pom.xml`:
```xml
<properties>
    <java.version>21</java.version>  <!-- Changed from 25 to 21 -->
    ...
</properties>
```

2. Ensure JDK 21 is installed:
```bash
# Download from: https://adoptium.net/temurin/releases/?version=21
java -version  # Should show version 21
```

3. Rebuild:
```bash
mvn clean compile
```

**Option 3: Use Java 17 (Maximum Compatibility)**
```xml
<properties>
    <java.version>17</java.version>
    ...
</properties>
```

### Java Version Configuration

The project is configured with **Java 25 as the default**. To use an older Java version:

1. Update `pom.xml` to use your Java version:
```xml
<properties>
    <java.version>17</java.version>  <!-- or 21 for LTS -->
    ...
</properties>
```

2. Rebuild:
```bash
mvn clean compile
```

### Lombok Setup

This project uses **Project Lombok** to reduce boilerplate code. The Maven configuration follows the official Lombok setup guide: https://projectlombok.org/setup/maven

**Maven Configuration:**
The `pom.xml` includes Lombok annotation processing configuration:
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.13.0</version>
    <configuration>
        <source>${java.version}</source>
        <target>${java.version}</target>
        <annotationProcessorPaths>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${lombok.version}</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

**IDE Setup:**

**IntelliJ IDEA:**
1. Install the Lombok plugin: `File` → `Settings` → `Plugins` → Search for "Lombok" → Install
2. Enable annotation processing: `File` → `Settings` → `Build, Execution, Deployment` → `Compiler` → `Annotation Processors` → Check "Enable annotation processing"
3. Restart IntelliJ IDEA

**VS Code:**
1. Install the "Lombok Annotations Support" extension
2. Reload VS Code

**Eclipse:**
1. Download `lombok.jar` from https://projectlombok.org/download
2. Run: `java -jar lombok.jar`
3. Follow the installer to add Lombok to Eclipse
4. Restart Eclipse

**NetBeans:**
1. Lombok should work automatically with Maven projects
2. If not, enable annotation processing in project properties

**Common Lombok Issues:**

If you see "cannot find symbol" errors for getter/setter methods:
1. Ensure annotation processing is enabled in your IDE
2. Clean and rebuild: `mvn clean compile`
3. Reimport the Maven project in your IDE
4. Restart your IDE

### Installation

1. Clone the repository:
```bash
cd /path/to/Spring-boot-4-zero-to-hero/interactive-platform
```

2. Configure Google OAuth2:
   - Go to [Google Cloud Console](https://console.cloud.google.com/)
   - Create a new project or select existing
   - Enable Google+ API
   - Create OAuth 2.0 credentials
   - Add authorized redirect URI: `http://localhost:8080/login/oauth2/code/google`

3. Set environment variables:
```bash
export GOOGLE_CLIENT_ID=your-client-id
export GOOGLE_CLIENT_SECRET=your-client-secret
export JWT_SECRET=your-secure-jwt-secret-key-at-least-256-bits
```

4. Build and run:
```bash
mvn clean install
mvn spring-boot:run
```

5. Access the platform:
```
http://localhost:8080
```

## Configuration

### Application Properties

Key configurations in `application.properties`:

```properties
# Database
spring.datasource.url=jdbc:h2:mem:springboot4hero

# OAuth2
spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID}
spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET}

# JWT
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000

# CORS
cors.allowed-origins=http://localhost:3000,http://localhost:8080
```

### Production Configuration

For production deployment, create `application-prod.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/springboot4hero
spring.datasource.username=your-username
spring.datasource.password=your-password
spring.jpa.hibernate.ddl-auto=validate
```

## API Endpoints

### Authentication
- `GET /oauth2/authorization/google` - Initiate Google OAuth login
- `GET /login/oauth2/code/google` - OAuth callback

### User Management
- `GET /api/user/profile` - Get user profile
- `GET /api/user/preferences` - Get user preferences
- `PUT /api/user/preferences` - Update user preferences

### Progress Tracking
- `GET /api/progress` - Get user progress
- `POST /api/progress` - Update progress
- `GET /api/progress/stats` - Get progress statistics

### Quizzes
- `GET /api/quiz` - Get all user quiz results
- `GET /api/quiz/chapter/{chapterId}` - Get chapter quiz results
- `POST /api/quiz` - Submit quiz answers

### Exercises
- `GET /api/exercise` - Get user exercise submissions
- `POST /api/exercise` - Submit exercise code

### Badges & Leaderboard
- `GET /api/badge/all` - Get all available badges
- `GET /api/badge/user` - Get user's earned badges
- `GET /api/badge/leaderboard` - Get top 10 leaderboard

## Database Schema

### Tables
- `users` - User accounts
- `user_preferences` - User learning preferences
- `progress` - Chapter/section completion tracking
- `quiz_results` - Quiz attempt history
- `exercise_submissions` - Code exercise submissions
- `badges` - Available badges
- `user_badges` - User earned badges

## Development

### Running Tests
```bash
mvn test
```

### Building for Production
```bash
mvn clean package -Pprod
java -jar target/interactive-platform-1.0.0.jar --spring.profiles.active=prod
```

### H2 Console (Development Only)
Access the H2 console at: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:springboot4hero`
- Username: `sa`
- Password: (empty)

## Architecture

### Package Structure
```
com.springboot4hero.platform
├── config          # Configuration classes
├── controller      # REST controllers
├── model           # JPA entities
├── repository      # Data repositories
├── security        # Security configuration
└── service         # Business logic
```

## Future Enhancements

- [ ] Real-time code execution in sandbox environment
- [ ] Peer code review system
- [ ] Discussion forums per chapter
- [ ] Video tutorial integration
- [ ] Mobile app for iOS/Android
- [ ] AI-powered code suggestions
- [ ] Team/classroom management features
- [ ] Certificates of completion
- [ ] Integration with GitHub for project submissions
- [ ] Advanced analytics dashboard

## Contributing

We welcome contributions! Please see [CONTRIBUTING.md](../CONTRIBUTING.md) for guidelines.

## License

This project is licensed under the MIT License - see the [LICENSE](../LICENSE) file for details.

## Support

For issues and questions:
- Open an issue on GitHub
- Email: support@springboot4hero.com
- Discord: [Join our community](https://discord.gg/springboot4hero)

## Acknowledgments

- Spring Boot team for the amazing framework
- All contributors and students using the platform
- Open source community

---

**Happy Learning! 🚀**

Ready to become a Spring Boot Hero? Start your journey at [http://localhost:8080](http://localhost:8080)
