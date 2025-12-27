package com.springboot4hero.platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "badges")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Badge {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(nullable = false)
    private String description;
    
    @Column(nullable = false)
    private String iconUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BadgeType type;
    
    @Column(nullable = false)
    private int pointsRequired;
    
    @OneToMany(mappedBy = "badge", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<UserBadge> userBadges = new HashSet<>();
    
    public enum BadgeType {
        CHAPTER_COMPLETION,
        QUIZ_MASTER,
        EXERCISE_MASTER,
        STREAK,
        SPECIAL
    }
}
