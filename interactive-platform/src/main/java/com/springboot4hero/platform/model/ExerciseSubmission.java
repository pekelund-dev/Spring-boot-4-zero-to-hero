package com.springboot4hero.platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "exercise_submissions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseSubmission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String chapterId;
    
    @Column(nullable = false)
    private String exerciseId;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String code;
    
    @Column(nullable = false)
    private boolean passed;
    
    @Column(columnDefinition = "TEXT")
    private String feedback;
    
    @Column(nullable = false)
    private LocalDateTime submittedAt;
    
    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
    }
}
