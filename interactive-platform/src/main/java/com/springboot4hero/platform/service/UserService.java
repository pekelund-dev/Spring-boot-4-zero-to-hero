package com.springboot4hero.platform.service;

import com.springboot4hero.platform.model.User;
import com.springboot4hero.platform.model.UserPreferences;
import com.springboot4hero.platform.repository.UserPreferencesRepository;
import com.springboot4hero.platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPreferencesRepository userPreferencesRepository;

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public UserPreferences updateUserPreferences(String email, UserPreferences preferences) {
        User user = getUserByEmail(email);
        
        UserPreferences existingPreferences = userPreferencesRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    UserPreferences newPrefs = new UserPreferences();
                    newPrefs.setUser(user);
                    return newPrefs;
                });

        existingPreferences.setOperatingSystem(preferences.getOperatingSystem());
        existingPreferences.setBuildTool(preferences.getBuildTool());
        existingPreferences.setJavaVersion(preferences.getJavaVersion());
        existingPreferences.setIde(preferences.getIde());

        return userPreferencesRepository.save(existingPreferences);
    }

    public UserPreferences getUserPreferences(String email) {
        User user = getUserByEmail(email);
        return userPreferencesRepository.findByUserId(user.getId())
                .orElse(null);
    }
}
