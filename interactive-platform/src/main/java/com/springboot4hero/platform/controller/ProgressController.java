package com.springboot4hero.platform.controller;

import com.springboot4hero.platform.model.Progress;
import com.springboot4hero.platform.service.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    @Autowired
    private ProgressService progressService;

    @GetMapping
    public ResponseEntity<List<Progress>> getUserProgress(@AuthenticationPrincipal UserDetails userDetails) {
        List<Progress> progress = progressService.getUserProgress(userDetails.getUsername());
        return ResponseEntity.ok(progress);
    }

    @PostMapping
    public ResponseEntity<Progress> updateProgress(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> request) {
        String chapterId = (String) request.get("chapterId");
        String sectionId = (String) request.get("sectionId");
        Boolean completed = (Boolean) request.get("completed");
        
        Progress progress = progressService.updateProgress(
                userDetails.getUsername(), chapterId, sectionId, completed);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Integer>> getProgressStats(@AuthenticationPrincipal UserDetails userDetails) {
        int completedChapters = progressService.getCompletedChaptersCount(userDetails.getUsername());
        return ResponseEntity.ok(Map.of("completedChapters", completedChapters));
    }
}
