package com.springboot4hero.platform.repository;

import com.springboot4hero.platform.model.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    List<UserBadge> findByUserId(Long userId);
    
    @Query("SELECT ub FROM UserBadge ub WHERE ub.user.id = ?1 AND ub.badge.id = ?2")
    UserBadge findByUserIdAndBadgeId(Long userId, Long badgeId);
}
