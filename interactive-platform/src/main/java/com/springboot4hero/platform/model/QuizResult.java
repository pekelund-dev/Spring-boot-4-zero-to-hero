package com.springboot4hero.platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_results")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String chapterId;
    
    @Column(nullable = false)
    private int totalQuestions;
    
    @Column(nullable = false)
    private int correctAnswers;
    
    @Column(nullable = false)
    private int score;
    
    @Column(nullable = false)
    private LocalDateTime completedAt;
    
    @Column(columnDefinition = "TEXT")
    private String answers;
    
    @PrePersist
    protected void onCreate() {
        completedAt = LocalDateTime.now();
    }
}
