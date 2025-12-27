package com.springboot4hero.platform.repository;

import com.springboot4hero.platform.model.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    
    Optional<Chapter> findByChapterId(String chapterId);
    
    List<Chapter> findByAvailableOrderByOrderIndex(boolean available);
    
    List<Chapter> findAllByOrderByOrderIndex();
}
