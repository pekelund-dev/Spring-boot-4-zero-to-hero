package com.springboot4hero.platform.repository;

import com.springboot4hero.platform.model.ExerciseSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseSubmissionRepository extends JpaRepository<ExerciseSubmission, Long> {
    List<ExerciseSubmission> findByUserId(Long userId);
    List<ExerciseSubmission> findByUserIdAndChapterIdAndExerciseId(Long userId, String chapterId, String exerciseId);
}
