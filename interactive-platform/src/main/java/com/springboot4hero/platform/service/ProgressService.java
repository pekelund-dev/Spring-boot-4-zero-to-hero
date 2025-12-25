package com.springboot4hero.platform.service;

import com.springboot4hero.platform.model.Progress;
import com.springboot4hero.platform.model.User;
import com.springboot4hero.platform.repository.ProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProgressService {

    @Autowired
    private ProgressRepository progressRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BadgeService badgeService;

    public List<Progress> getUserProgress(String email) {
        User user = userService.getUserByEmail(email);
        return progressRepository.findByUserId(user.getId());
    }

    @Transactional
    public Progress updateProgress(String email, String chapterId, String sectionId, boolean completed) {
        User user = userService.getUserByEmail(email);
        
        Progress progress = progressRepository.findByUserIdAndChapterIdAndSectionId(
                user.getId(), chapterId, sectionId)
                .orElseGet(() -> {
                    Progress newProgress = new Progress();
                    newProgress.setUser(user);
                    newProgress.setChapterId(chapterId);
                    newProgress.setSectionId(sectionId);
                    return newProgress;
                });

        progress.setCompleted(completed);
        Progress savedProgress = progressRepository.save(progress);

        // Check for badge awards
        if (completed) {
            badgeService.checkAndAwardBadges(user.getId());
        }

        return savedProgress;
    }

    public int getCompletedChaptersCount(String email) {
        User user = userService.getUserByEmail(email);
        List<Progress> completedProgress = progressRepository.findByUserIdAndCompleted(user.getId(), true);
        return (int) completedProgress.stream()
                .map(Progress::getChapterId)
                .distinct()
                .count();
    }
}
