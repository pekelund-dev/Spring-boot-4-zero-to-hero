package com.springboot4hero.platform.service;

import com.springboot4hero.platform.model.Badge;
import com.springboot4hero.platform.model.UserBadge;
import com.springboot4hero.platform.model.User;
import com.springboot4hero.platform.model.ExerciseSubmission;
import com.springboot4hero.platform.model.QuizResult;
import com.springboot4hero.platform.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BadgeService {

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private UserBadgeRepository userBadgeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProgressRepository progressRepository;

    @Autowired
    private QuizResultRepository quizResultRepository;

    @Autowired
    private ExerciseSubmissionRepository exerciseSubmissionRepository;

    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }

    public List<UserBadge> getUserBadges(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userBadgeRepository.findByUserId(user.getId());
    }

    @Transactional
    public void checkAndAwardBadges(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check for chapter completion badges
        long completedChapters = progressRepository.findByUserIdAndCompleted(userId, true)
                .stream()
                .map(p -> p.getChapterId())
                .distinct()
                .count();

        awardBadgeIfEligible(user, "First Chapter", completedChapters >= 1);
        awardBadgeIfEligible(user, "Five Chapters", completedChapters >= 5);
        awardBadgeIfEligible(user, "Ten Chapters", completedChapters >= 10);

        // Check for quiz master badges
        long perfectQuizzes = quizResultRepository.findByUserId(userId)
                .stream()
                .filter(qr -> qr.getScore() == 100)
                .count();

        awardBadgeIfEligible(user, "Quiz Master", perfectQuizzes >= 5);

        // Check for exercise completion badges
        long passedExercises = exerciseSubmissionRepository.findByUserId(userId)
                .stream()
                .filter(ExerciseSubmission::isPassed)
                .count();

        awardBadgeIfEligible(user, "Code Ninja", passedExercises >= 10);
    }

    private void awardBadgeIfEligible(User user, String badgeName, boolean eligible) {
        if (!eligible) {
            return;
        }

        Badge badge = badgeRepository.findByName(badgeName).orElse(null);
        if (badge == null) {
            return;
        }

        // Check if user already has this badge
        UserBadge existing = userBadgeRepository.findByUserIdAndBadgeId(user.getId(), badge.getId());
        if (existing == null) {
            UserBadge userBadge = UserBadge.builder()
                    .user(user)
                    .badge(badge)
                    .build();
            userBadgeRepository.save(userBadge);
        }
    }

    public List<LeaderboardEntry> getLeaderboard() {
        return userRepository.findAll().stream()
                .map(user -> {
                    long badgeCount = userBadgeRepository.findByUserId(user.getId()).size();
                    long completedChapters = progressRepository.findByUserIdAndCompleted(user.getId(), true)
                            .stream()
                            .map(p -> p.getChapterId())
                            .distinct()
                            .count();
                    int totalScore = quizResultRepository.findByUserId(user.getId())
                            .stream()
                            .mapToInt(QuizResult::getScore)
                            .sum();
                    
                    return new LeaderboardEntry(user.getName(), badgeCount, completedChapters, totalScore);
                })
                .sorted((a, b) -> Integer.compare(b.getTotalScore(), a.getTotalScore()))
                .limit(10)
                .collect(Collectors.toList());
    }

    public static class LeaderboardEntry {
        private String name;
        private long badgeCount;
        private long completedChapters;
        private int totalScore;

        public LeaderboardEntry(String name, long badgeCount, long completedChapters, int totalScore) {
            this.name = name;
            this.badgeCount = badgeCount;
            this.completedChapters = completedChapters;
            this.totalScore = totalScore;
        }

        public String getName() { return name; }
        public long getBadgeCount() { return badgeCount; }
        public long getCompletedChapters() { return completedChapters; }
        public int getTotalScore() { return totalScore; }
    }
}
