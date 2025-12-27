package com.springboot4hero.platform.service;

import com.springboot4hero.platform.model.QuizResult;
import com.springboot4hero.platform.model.User;
import com.springboot4hero.platform.repository.QuizResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuizService {

    @Autowired
    private QuizResultRepository quizResultRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BadgeService badgeService;

    public List<QuizResult> getUserQuizResults(String email) {
        User user = userService.getUserByEmail(email);
        return quizResultRepository.findByUserId(user.getId());
    }

    public List<QuizResult> getChapterQuizResults(String email, String chapterId) {
        User user = userService.getUserByEmail(email);
        return quizResultRepository.findByUserIdAndChapterId(user.getId(), chapterId);
    }

    @Transactional
    public QuizResult submitQuizResult(String email, String chapterId, int totalQuestions,
                                       int correctAnswers, String answers) {
        User user = userService.getUserByEmail(email);
        
        int score = (int) ((correctAnswers / (double) totalQuestions) * 100);
        
        QuizResult quizResult = QuizResult.builder()
                .user(user)
                .chapterId(chapterId)
                .totalQuestions(totalQuestions)
                .correctAnswers(correctAnswers)
                .score(score)
                .answers(answers)
                .build();

        QuizResult savedResult = quizResultRepository.save(quizResult);

        // Check for badge awards
        badgeService.checkAndAwardBadges(user.getId());

        return savedResult;
    }
}
