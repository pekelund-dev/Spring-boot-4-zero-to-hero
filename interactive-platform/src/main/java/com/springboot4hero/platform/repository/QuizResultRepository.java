package com.springboot4hero.platform.repository;

import com.springboot4hero.platform.model.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
    List<QuizResult> findByUserId(Long userId);
    List<QuizResult> findByUserIdAndChapterId(Long userId, String chapterId);
    
    @Query("SELECT qr FROM QuizResult qr WHERE qr.user.id = ?1 ORDER BY qr.completedAt DESC")
    List<QuizResult> findRecentByUserId(Long userId);
}
