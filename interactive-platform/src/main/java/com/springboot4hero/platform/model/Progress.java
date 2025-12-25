package com.springboot4hero.platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "progress")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Progress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String chapterId;
    
    @Column(nullable = false)
    private String sectionId;
    
    @Column(nullable = false)
    private boolean completed;
    
    @Column(nullable = false)
    private LocalDateTime lastAccessedAt;
    
    private LocalDateTime completedAt;
    
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastAccessedAt = LocalDateTime.now();
        if (completed && completedAt == null) {
            completedAt = LocalDateTime.now();
        }
    }
}
