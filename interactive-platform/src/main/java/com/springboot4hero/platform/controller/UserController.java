package com.springboot4hero.platform.controller;

import com.springboot4hero.platform.model.UserPreferences;
import com.springboot4hero.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/preferences")
    public ResponseEntity<UserPreferences> getPreferences(@AuthenticationPrincipal UserDetails userDetails) {
        UserPreferences preferences = userService.getUserPreferences(userDetails.getUsername());
        return ResponseEntity.ok(preferences);
    }

    @PutMapping("/preferences")
    public ResponseEntity<UserPreferences> updatePreferences(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserPreferences preferences) {
        UserPreferences updated = userService.updateUserPreferences(userDetails.getUsername(), preferences);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        var user = userService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(user);
    }
}
