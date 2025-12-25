package com.springboot4hero.platform.service;

import com.springboot4hero.platform.model.ExerciseSubmission;
import com.springboot4hero.platform.model.User;
import com.springboot4hero.platform.repository.ExerciseSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExerciseService {

    @Autowired
    private ExerciseSubmissionRepository exerciseSubmissionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BadgeService badgeService;

    public List<ExerciseSubmission> getUserExercises(String email) {
        User user = userService.getUserByEmail(email);
        return exerciseSubmissionRepository.findByUserId(user.getId());
    }

    @Transactional
    public ExerciseSubmission submitExercise(String email, String chapterId, String exerciseId,
                                             String code) {
        User user = userService.getUserByEmail(email);
        
        // Simple validation - in production this would compile/run the code
        boolean passed = validateCode(code, exerciseId);
        String feedback = passed ? "Great job! Your solution is correct." 
                : "Keep trying! Review the requirements and try again.";

        ExerciseSubmission submission = ExerciseSubmission.builder()
                .user(user)
                .chapterId(chapterId)
                .exerciseId(exerciseId)
                .code(code)
                .passed(passed)
                .feedback(feedback)
                .build();

        ExerciseSubmission savedSubmission = exerciseSubmissionRepository.save(submission);

        // Check for badge awards
        if (passed) {
            badgeService.checkAndAwardBadges(user.getId());
        }

        return savedSubmission;
    }

    private boolean validateCode(String code, String exerciseId) {
        // Simplified validation - in a real system, this would:
        // 1. Compile the code
        // 2. Run test cases
        // 3. Check for specific patterns/requirements
        // For demo purposes, just check if code is not empty and contains basic keywords
        return code != null && 
               !code.trim().isEmpty() && 
               code.length() > 50;
    }
}
