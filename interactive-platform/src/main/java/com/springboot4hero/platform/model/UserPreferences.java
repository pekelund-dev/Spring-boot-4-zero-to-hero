package com.springboot4hero.platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_preferences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferences {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperatingSystem operatingSystem;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BuildTool buildTool;
    
    @Column(nullable = false)
    private String javaVersion;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IDE ide;
    
    public enum OperatingSystem {
        MAC, WINDOWS, LINUX, WINDOWS_WSL2
    }
    
    public enum BuildTool {
        MAVEN, GRADLE
    }
    
    public enum IDE {
        INTELLIJ_IDEA, VS_CODE, ECLIPSE, NETBEANS
    }
}
