package com.springboot4hero.platform.config;

import com.springboot4hero.platform.model.Badge;
import com.springboot4hero.platform.repository.BadgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private BadgeRepository badgeRepository;

    @Override
    public void run(String... args) {
        // Initialize badges if not already present
        if (badgeRepository.count() == 0) {
            initializeBadges();
        }
    }

    private void initializeBadges() {
        Badge[] badges = {
            Badge.builder()
                .name("First Chapter")
                .description("Complete your first chapter")
                .iconUrl("/badges/first-chapter.png")
                .type(Badge.BadgeType.CHAPTER_COMPLETION)
                .pointsRequired(1)
                .build(),
            
            Badge.builder()
                .name("Five Chapters")
                .description("Complete five chapters")
                .iconUrl("/badges/five-chapters.png")
                .type(Badge.BadgeType.CHAPTER_COMPLETION)
                .pointsRequired(5)
                .build(),
            
            Badge.builder()
                .name("Ten Chapters")
                .description("Complete ten chapters")
                .iconUrl("/badges/ten-chapters.png")
                .type(Badge.BadgeType.CHAPTER_COMPLETION)
                .pointsRequired(10)
                .build(),
            
            Badge.builder()
                .name("Quiz Master")
                .description("Score 100% on five quizzes")
                .iconUrl("/badges/quiz-master.png")
                .type(Badge.BadgeType.QUIZ_MASTER)
                .pointsRequired(5)
                .build(),
            
            Badge.builder()
                .name("Code Ninja")
                .description("Complete 10 coding exercises")
                .iconUrl("/badges/code-ninja.png")
                .type(Badge.BadgeType.EXERCISE_MASTER)
                .pointsRequired(10)
                .build(),
            
            Badge.builder()
                .name("Spring Boot Hero")
                .description("Complete all chapters with 90%+ average")
                .iconUrl("/badges/spring-boot-hero.png")
                .type(Badge.BadgeType.SPECIAL)
                .pointsRequired(100)
                .build()
        };

        for (Badge badge : badges) {
            badgeRepository.save(badge);
        }
    }
}
