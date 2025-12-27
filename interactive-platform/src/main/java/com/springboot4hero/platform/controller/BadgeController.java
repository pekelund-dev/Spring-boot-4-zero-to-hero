package com.springboot4hero.platform.controller;

import com.springboot4hero.platform.model.Badge;
import com.springboot4hero.platform.model.UserBadge;
import com.springboot4hero.platform.service.BadgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/badge")
public class BadgeController {

    @Autowired
    private BadgeService badgeService;

    @GetMapping("/all")
    public ResponseEntity<List<Badge>> getAllBadges() {
        List<Badge> badges = badgeService.getAllBadges();
        return ResponseEntity.ok(badges);
    }

    @GetMapping("/user")
    public ResponseEntity<List<UserBadge>> getUserBadges(@AuthenticationPrincipal UserDetails userDetails) {
        List<UserBadge> badges = badgeService.getUserBadges(userDetails.getUsername());
        return ResponseEntity.ok(badges);
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<BadgeService.LeaderboardEntry>> getLeaderboard() {
        List<BadgeService.LeaderboardEntry> leaderboard = badgeService.getLeaderboard();
        return ResponseEntity.ok(leaderboard);
    }
}
