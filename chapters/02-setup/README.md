# Chapter 2: Setting Up Your Development Environment

Before diving into Spring Boot development, you need to set up a proper development environment. This chapter guides you through installing and configuring all necessary tools for Spring Boot 4.x development.

## Prerequisites Checklist

Before you begin, ensure you have:
- [ ] A computer with Windows, macOS, or Linux
- [ ] Administrative/sudo access for installations
- [ ] Stable internet connection
- [ ] At least 8GB RAM (16GB recommended)
- [ ] 10GB free disk space

## Installing Java 21 (LTS)

Spring Boot 4.x requires Java 21 or higher. Java 21 is a Long Term Support (LTS) release, making it ideal for production applications.

### Why Java 21?

- Virtual Threads (Project Loom) for improved concurrency
- Pattern Matching for switch expressions
- Record Patterns
- Sequenced Collections
- String Templates (Preview)
- Improved performance and security

### Installation on Different Platforms

#### Windows

**Option 1: Using Microsoft Build of OpenJDK**

```bash
# Using winget (Windows Package Manager)
winget install Microsoft.OpenJDK.21
```

**Option 2: Using Oracle JDK**

1. Download from [Oracle JDK Downloads](https://www.oracle.com/java/technologies/downloads/#java21)
2. Run the installer
3. Follow the installation wizard

**Option 3: Using SDKMAN! (Recommended for managing multiple versions)**

```bash
# Install SDKMAN! in Git Bash or WSL
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Install Java 21
sdk install java 21-open
```

#### macOS

**Option 1: Using Homebrew (Recommended)**

```bash
# Install Homebrew if not already installed
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install Java 21
brew install openjdk@21

# Link it so the system can find it
sudo ln -sfn /opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk \
  /Library/Java/JavaVirtualMachines/openjdk-21.jdk
```

**Option 2: Using SDKMAN!**

```bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 21-open
```

#### Linux (Ubuntu/Debian)

```bash
# Update package list
sudo apt update

# Install OpenJDK 21
sudo apt install openjdk-21-jdk

# Verify installation
java -version
```

#### Linux (Fedora/RHEL/CentOS)

```bash
# Install OpenJDK 21
sudo dnf install java-21-openjdk-devel

# Verify installation
java -version
```

### Verifying Java Installation

```bash
# Check Java version
java -version

# Expected output:
# openjdk version "21.0.x" 2024-xx-xx
# OpenJDK Runtime Environment (build 21.0.x+xx)
# OpenJDK 64-Bit Server VM (build 21.0.x+xx, mixed mode, sharing)

# Check Java compiler
javac -version

# Check JAVA_HOME
echo $JAVA_HOME  # Unix/macOS
echo %JAVA_HOME% # Windows
```

### Setting JAVA_HOME

#### Windows

```bash
# Set permanently through System Properties
# 1. Search for "Environment Variables" in Start menu
# 2. Click "Environment Variables"
# 3. Under "System variables", click "New"
# 4. Variable name: JAVA_HOME
# 5. Variable value: C:\Program Files\Java\jdk-21 (adjust path)
# 6. Add %JAVA_HOME%\bin to PATH variable
```

#### macOS/Linux

Add to `~/.bashrc`, `~/.zshrc`, or `~/.bash_profile`:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)  # macOS
# OR
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64  # Linux

export PATH=$JAVA_HOME/bin:$PATH
```

Apply changes:
```bash
source ~/.bashrc  # or ~/.zshrc
```

## Choosing Your IDE

### IntelliJ IDEA (Recommended)

IntelliJ IDEA is the most popular IDE for Spring Boot development, offering excellent Spring support.

#### Editions

- **Community Edition**: Free, good for most Spring Boot development
- **Ultimate Edition**: Paid, includes advanced Spring features, database tools, and web development support

#### Installation

```bash
# macOS with Homebrew
brew install --cask intellij-idea-ce  # Community
brew install --cask intellij-idea     # Ultimate

# Linux with Snap
sudo snap install intellij-idea-community --classic
sudo snap install intellij-idea-ultimate --classic

# Windows: Download from https://www.jetbrains.com/idea/download/
```

#### Essential Plugins

1. **Spring Boot** (Ultimate only, built-in)
2. **Lombok** - Reduces boilerplate code
3. **SonarLint** - Code quality analysis
4. **GitToolBox** - Enhanced Git integration
5. **Rainbow Brackets** - Better code readability
6. **Key Promoter X** - Learn keyboard shortcuts

#### Configuration

1. **Enable Auto-Import**:
   - Settings → Editor → General → Auto Import
   - Check "Add unambiguous imports on the fly"

2. **Set Java 21**:
   - File → Project Structure → Project SDK → Java 21

3. **Code Style**:
   - Settings → Editor → Code Style → Java
   - Follow Google Java Style Guide or Spring conventions

### Visual Studio Code

VS Code is lightweight and highly customizable, excellent for developers who prefer a simpler setup.

#### Installation

```bash
# macOS with Homebrew
brew install --cask visual-studio-code

# Linux with Snap
sudo snap install code --classic

# Windows: Download from https://code.visualstudio.com/
```

#### Essential Extensions

1. **Extension Pack for Java** (by Microsoft)
   - Language Support for Java
   - Debugger for Java
   - Test Runner for Java
   - Maven for Java
   - Project Manager for Java

2. **Spring Boot Extension Pack** (by VMware)
   - Spring Boot Tools
   - Spring Initializr
   - Spring Boot Dashboard

3. **Additional Recommended Extensions**
   - Lombok Annotations Support
   - SonarLint
   - GitLens
   - REST Client
   - Docker
   - YAML

#### Configuration

Create `.vscode/settings.json` in your project:

```json
{
  "java.configuration.runtimes": [
    {
      "name": "JavaSE-21",
      "path": "/path/to/jdk-21",
      "default": true
    }
  ],
  "java.compile.nullAnalysis.mode": "automatic",
  "spring-boot.ls.java.home": "/path/to/jdk-21",
  "files.exclude": {
    "**/.git": true,
    "**/.DS_Store": true,
    "**/target": true
  }
}
```

### Eclipse

Eclipse is a mature, open-source IDE with strong Java support.

#### Installation

Download from [Eclipse Downloads](https://www.eclipse.org/downloads/)
- Choose "Eclipse IDE for Enterprise Java and Web Developers"

#### Essential Plugins

1. **Spring Tools 4 (STS)**
   - Help → Eclipse Marketplace → Search "Spring Tools 4"

2. **Lombok**
   - Download lombok.jar from [projectlombok.org](https://projectlombok.org/)
   - Run: `java -jar lombok.jar`

## Build Tools: Maven vs Gradle

Spring Boot supports both Maven and Gradle. Choose based on your needs and team preferences.

### Maven

**Pros:**
- Industry standard
- Extensive plugin ecosystem
- XML-based (explicit and clear)
- Better IDE support
- Simpler for beginners

**Cons:**
- Verbose XML configuration
- Slower builds for large projects
- Less flexible than Gradle

**Installation:**

```bash
# macOS with Homebrew
brew install maven

# Linux (Ubuntu/Debian)
sudo apt install maven

# Linux (Fedora/RHEL)
sudo dnf install maven

# Windows with Chocolatey
choco install maven

# Verify
mvn -version
```

### Gradle

**Pros:**
- Faster builds (incremental compilation)
- Groovy or Kotlin DSL (more concise)
- Better for large multi-module projects
- More flexible and powerful

**Cons:**
- Steeper learning curve
- DSL can be less explicit
- Slightly less IDE support

**Installation:**

```bash
# macOS with Homebrew
brew install gradle

# Linux with SDKMAN!
sdk install gradle

# Windows with Chocolatey
choco install gradle

# Verify
gradle -version
```

### Comparison: pom.xml vs build.gradle

**Maven (pom.xml):**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>4.0.0</version>
    </parent>
    
    <groupId>com.example</groupId>
    <artifactId>demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>
</project>
```

**Gradle (build.gradle):**

```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '4.0.0'
    id 'io.spring.dependency-management' version '1.1.0'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '21'

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
}
```

## Spring Initializr

Spring Initializr ([start.spring.io](https://start.spring.io)) is the fastest way to bootstrap a Spring Boot project.

### Web Interface

1. Visit [start.spring.io](https://start.spring.io)
2. Configure your project:
   - **Project**: Maven or Gradle
   - **Language**: Java
   - **Spring Boot**: 4.0.x (or latest 4.x version)
   - **Group**: com.example (or your package)
   - **Artifact**: demo (your project name)
   - **Packaging**: Jar
   - **Java**: 21

3. Add dependencies:
   - Spring Web
   - Spring Data JPA
   - H2 Database
   - Spring Boot DevTools
   - Lombok

4. Click "Generate" to download the project

### Using IntelliJ IDEA

1. File → New → Project
2. Select "Spring Initializr"
3. Configure as above
4. Click "Next" and select dependencies
5. Click "Finish"

### Using Spring Boot CLI (Optional)

```bash
# Install Spring Boot CLI
sdk install springboot

# Create a project
spring init --dependencies=web,data-jpa,h2,devtools,lombok \
  --build=maven \
  --java-version=21 \
  --boot-version=4.0.0 \
  my-app

cd my-app
```

### Using curl

```bash
curl https://start.spring.io/starter.zip \
  -d dependencies=web,data-jpa,h2,devtools,lombok \
  -d type=maven-project \
  -d javaVersion=21 \
  -d bootVersion=4.0.0 \
  -d groupId=com.example \
  -d artifactId=demo \
  -o demo.zip

unzip demo.zip
cd demo
```

## Project Structure Best Practices

A well-organized project structure is crucial for maintainability:

```
my-app/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── demo/
│   │   │               ├── DemoApplication.java
│   │   │               ├── config/          # Configuration classes
│   │   │               ├── controller/      # REST controllers
│   │   │               ├── service/         # Business logic
│   │   │               ├── repository/      # Data access
│   │   │               ├── model/           # Domain models
│   │   │               ├── dto/             # Data Transfer Objects
│   │   │               ├── exception/       # Custom exceptions
│   │   │               └── util/            # Utility classes
│   │   └── resources/
│   │       ├── application.yml              # Main configuration
│   │       ├── application-dev.yml          # Dev profile
│   │       ├── application-prod.yml         # Production profile
│   │       ├── static/                      # Static resources
│   │       └── templates/                   # Templates (if using Thymeleaf)
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── demo/
│                       ├── DemoApplicationTests.java
│                       ├── controller/       # Controller tests
│                       ├── service/          # Service tests
│                       └── repository/       # Repository tests
├── target/                                   # Build output (Maven)
├── .gitignore
├── pom.xml                                   # Maven configuration
└── README.md
```

### Package Naming Conventions

- Use reverse domain name: `com.company.project`
- Keep packages shallow (max 4-5 levels)
- Use singular nouns for package names
- Organize by feature, not by layer (for larger projects)

## Essential Tools and Plugins

### 1. Git

```bash
# macOS
brew install git

# Linux
sudo apt install git  # Ubuntu/Debian
sudo dnf install git  # Fedora/RHEL

# Windows
winget install Git.Git

# Verify
git --version

# Configure
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
```

### 2. Docker (for containerization)

```bash
# macOS
brew install --cask docker

# Linux
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Windows: Download from https://www.docker.com/products/docker-desktop

# Verify
docker --version
docker-compose --version
```

### 3. Postman or Insomnia (API testing)

```bash
# macOS
brew install --cask postman

# Linux (Snap)
sudo snap install postman

# Windows: Download from https://www.postman.com/downloads/
```

### 4. Database Clients

**DBeaver (Universal):**
```bash
brew install --cask dbeaver-community  # macOS
sudo snap install dbeaver-ce           # Linux
```

**pgAdmin (PostgreSQL):**
```bash
brew install --cask pgadmin4  # macOS
```

### 5. Terminal Enhancements (Optional)

**iTerm2 (macOS):**
```bash
brew install --cask iterm2
```

**Windows Terminal (Windows):**
```bash
winget install Microsoft.WindowsTerminal
```

**Oh My Zsh:**
```bash
sh -c "$(curl -fsSL https://raw.githubusercontent.com/ohmyzsh/ohmyzsh/master/tools/install.sh)"
```

## Development Workflow Setup

### Create a Workspace

```bash
# Create a dedicated workspace
mkdir -p ~/workspace/spring-boot-learning
cd ~/workspace/spring-boot-learning

# Clone this repository
git clone https://github.com/pekelund-dev/Spring-boot-4-zero-to-hero.git
cd Spring-boot-4-zero-to-hero
```

### IDE Configuration

#### IntelliJ IDEA

1. **Import the project**:
   - File → Open → Select project directory
   - IntelliJ will auto-detect Maven/Gradle

2. **Configure JDK**:
   - File → Project Structure → Project SDK → Java 21

3. **Enable annotation processing**:
   - Settings → Build → Compiler → Annotation Processors
   - Check "Enable annotation processing"

4. **Optimize for Spring Boot**:
   - Settings → Build → Build Tools → Maven/Gradle
   - Check "Delegate IDE build/run actions to Maven/Gradle"

#### VS Code

1. **Open the project**: File → Open Folder

2. **Trust the workspace**: Click "Yes, I trust the authors"

3. **Wait for Java extension** to download dependencies

4. **Configure launch.json** for debugging:

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Spring Boot App",
      "request": "launch",
      "mainClass": "com.example.demo.DemoApplication",
      "projectName": "demo"
    }
  ]
}
```

## Testing Your Setup

Let's verify everything works by creating and running a simple Spring Boot application.

### Create a Test Project

```bash
curl https://start.spring.io/starter.zip \
  -d dependencies=web \
  -d type=maven-project \
  -d javaVersion=21 \
  -d bootVersion=4.0.0 \
  -d groupId=com.example \
  -d artifactId=setup-test \
  -o setup-test.zip

unzip setup-test.zip -d setup-test
cd setup-test
```

### Run the Application

**Using Maven:**
```bash
./mvnw spring-boot:run
# Windows: mvnw.cmd spring-boot:run
```

**Using Gradle:**
```bash
./gradlew bootRun
# Windows: gradlew.bat bootRun
```

**Expected Output:**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v4.0.0)

...
2024-xx-xx ... : Started DemoApplication in 2.345 seconds
```

### Test the Application

```bash
curl http://localhost:8080
# Should return a Whitelabel Error Page (expected, as we haven't created any endpoints)
```

If you see the Spring Boot banner and the application starts successfully, congratulations! Your environment is ready. 🎉

## Troubleshooting Common Issues

### Issue: Wrong Java Version

**Symptom:** Error about unsupported Java version

**Solution:**
```bash
# Check active Java version
java -version

# If using SDKMAN!, set default
sdk default java 21-open

# If multiple Java installations, set JAVA_HOME explicitly
export JAVA_HOME=/path/to/jdk-21
```

### Issue: Port 8080 Already in Use

**Symptom:** "Port 8080 is already in use"

**Solution:**
```bash
# Find process using port 8080
lsof -i :8080  # macOS/Linux
netstat -ano | findstr :8080  # Windows

# Kill the process or change Spring Boot port
# Add to application.properties:
server.port=8081
```

### Issue: Maven/Gradle Not Found

**Symptom:** "mvn: command not found"

**Solution:**
```bash
# Use wrapper scripts (included in project)
./mvnw spring-boot:run  # Maven
./gradlew bootRun       # Gradle

# Or install globally
sdk install maven
sdk install gradle
```

### Issue: Lombok Not Working

**Symptom:** Compilation errors with @Data, @Getter, etc.

**Solution:**
1. Enable annotation processing in IDE
2. Install Lombok plugin
3. Restart IDE

### Issue: Slow Startup

**Symptom:** Application takes too long to start

**Solution:**
1. Ensure you're using Java 21
2. Increase memory: `export MAVEN_OPTS="-Xmx2g"`
3. Disable unnecessary auto-configurations
4. Use DevTools for hot reload

## Summary

In this chapter, you learned:
- How to install Java 21 on different operating systems
- Choosing and configuring an IDE (IntelliJ IDEA, VS Code, or Eclipse)
- Maven vs Gradle and how to set them up
- Using Spring Initializr to bootstrap projects
- Best practices for project structure
- Essential development tools
- How to verify your setup

You now have a complete development environment ready for Spring Boot 4.x development!

---

## 🏋️ Coding Exercises

### Exercise 1: Environment Verification
Create a simple script that checks your environment:
```bash
#!/bin/bash
echo "Java Version:"
java -version
echo "\nMaven Version:"
mvn -version
echo "\nGradle Version:"
gradle -version
echo "\nGit Version:"
git --version
echo "\nDocker Version:"
docker --version
```

Save as `check-env.sh`, make executable (`chmod +x check-env.sh`), and run it.

### Exercise 2: Create Multiple Projects
Using Spring Initializr (web or CLI), create three projects:
1. A REST API project (dependencies: web, devtools)
2. A data project (dependencies: web, data-jpa, h2)
3. A secure API (dependencies: web, security, devtools)

### Exercise 3: IDE Mastery
1. Import one of your projects into your chosen IDE
2. Learn these keyboard shortcuts:
   - Run application
   - Debug application
   - Find class
   - Find file
   - Navigate to definition
3. Create a custom run configuration

### Exercise 4: Docker Container
Create a Dockerfile for a Spring Boot application:
```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run it:
```bash
mvn clean package
docker build -t my-spring-app .
docker run -p 8080:8080 my-spring-app
```

---

## 🎯 Quiz

### Question 1
What is the minimum Java version required for Spring Boot 4.x?
- A) Java 11
- B) Java 17
- C) Java 21 ✅
- D) Java 25

### Question 2
Which tool is used to bootstrap Spring Boot projects?
- A) Spring Creator
- B) Spring Initializr ✅
- C) Spring Generator
- D) Spring Wizard

### Question 3
What is the default embedded server in Spring Boot?
- A) Jetty
- B) Tomcat ✅
- C) Undertow
- D) WebLogic

### Question 4
Which build tool uses XML for configuration?
- A) Gradle
- B) Ant
- C) Maven ✅
- D) Bazel

### Question 5
What is the command to run a Maven Spring Boot application?
- A) mvn start
- B) mvn run
- C) mvn spring-boot:run ✅
- D) mvn boot:run

### Question 6
Which IDE is most popular for Spring Boot development?
- A) NetBeans
- B) Eclipse
- C) IntelliJ IDEA ✅
- D) Sublime Text

### Question 7
What is the default port for Spring Boot applications?
- A) 80
- B) 443
- C) 3000
- D) 8080 ✅

### Question 8
Which file contains the main application configuration in Spring Boot?
- A) config.properties
- B) application.properties ✅
- C) spring.properties
- D) boot.properties

### Question 9
What is JAVA_HOME?
- A) A Java program
- B) An environment variable pointing to the JDK installation ✅
- C) A Java library
- D) A Spring Boot feature

### Question 10
Which annotation enables annotation processing for Lombok?
- A) It's automatic if the plugin is installed ✅
- B) @EnableLombok
- C) @ProcessAnnotations
- D) @Lombok

---

## 📚 Additional Resources

- [Java 21 Features](https://openjdk.org/projects/jdk/21/)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [IntelliJ IDEA Spring Boot Guide](https://www.jetbrains.com/help/idea/spring-boot.html)
- [VS Code Java Extensions](https://code.visualstudio.com/docs/java/java-spring-boot)
- [Maven Documentation](https://maven.apache.org/guides/)
- [Gradle Documentation](https://docs.gradle.org/)

---

**Previous Chapter**: [← Chapter 1: Introduction to Spring Boot 4.x](../01-introduction/README.md)

**Next Chapter**: [Chapter 3: Core Concepts and Architecture](../03-core-concepts/README.md) →
