package com.springboot4hero.platform.repository;

import com.springboot4hero.platform.model.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {
    List<Progress> findByUserId(Long userId);
    Optional<Progress> findByUserIdAndChapterIdAndSectionId(Long userId, String chapterId, String sectionId);
    List<Progress> findByUserIdAndCompleted(Long userId, boolean completed);
}
