package com.springboot4hero.platform.controller;

import com.springboot4hero.platform.model.Chapter;
import com.springboot4hero.platform.service.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chapters")
public class ChapterController {

    @Autowired
    private ChapterService chapterService;

    /**
     * Get all available chapters
     */
    @GetMapping
    public ResponseEntity<List<Chapter>> getAllChapters() {
        List<Chapter> chapters = chapterService.getAvailableChapters();
        return ResponseEntity.ok(chapters);
    }

    /**
     * Get a specific chapter by ID
     */
    @GetMapping("/{chapterId}")
    public ResponseEntity<Chapter> getChapter(@PathVariable String chapterId) {
        return chapterService.getChapterById(chapterId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Reload chapters from filesystem (admin endpoint)
     */
    @PostMapping("/reload")
    public ResponseEntity<String> reloadChapters() {
        chapterService.loadChaptersFromFilesystem();
        return ResponseEntity.ok("Chapters reloaded successfully");
    }
}
