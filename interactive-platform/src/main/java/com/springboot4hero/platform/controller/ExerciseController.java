package com.springboot4hero.platform.controller;

import com.springboot4hero.platform.model.ExerciseSubmission;
import com.springboot4hero.platform.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exercise")
public class ExerciseController {

    @Autowired
    private ExerciseService exerciseService;

    @GetMapping
    public ResponseEntity<List<ExerciseSubmission>> getUserExercises(@AuthenticationPrincipal UserDetails userDetails) {
        List<ExerciseSubmission> submissions = exerciseService.getUserExercises(userDetails.getUsername());
        return ResponseEntity.ok(submissions);
    }

    @PostMapping
    public ResponseEntity<ExerciseSubmission> submitExercise(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request) {
        String chapterId = request.get("chapterId");
        String exerciseId = request.get("exerciseId");
        String code = request.get("code");
        
        ExerciseSubmission submission = exerciseService.submitExercise(
                userDetails.getUsername(), chapterId, exerciseId, code);
        return ResponseEntity.ok(submission);
    }
}
