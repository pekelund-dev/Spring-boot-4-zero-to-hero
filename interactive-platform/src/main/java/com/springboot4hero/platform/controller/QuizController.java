package com.springboot4hero.platform.controller;

import com.springboot4hero.platform.model.QuizResult;
import com.springboot4hero.platform.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @GetMapping
    public ResponseEntity<List<QuizResult>> getUserQuizResults(@AuthenticationPrincipal UserDetails userDetails) {
        List<QuizResult> results = quizService.getUserQuizResults(userDetails.getUsername());
        return ResponseEntity.ok(results);
    }

    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<List<QuizResult>> getChapterQuizResults(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String chapterId) {
        List<QuizResult> results = quizService.getChapterQuizResults(userDetails.getUsername(), chapterId);
        return ResponseEntity.ok(results);
    }

    @PostMapping
    public ResponseEntity<QuizResult> submitQuiz(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> request) {
        String chapterId = (String) request.get("chapterId");
        Integer totalQuestions = (Integer) request.get("totalQuestions");
        Integer correctAnswers = (Integer) request.get("correctAnswers");
        String answers = (String) request.get("answers");
        
        QuizResult result = quizService.submitQuizResult(
                userDetails.getUsername(), chapterId, totalQuestions, correctAnswers, answers);
        return ResponseEntity.ok(result);
    }
}
