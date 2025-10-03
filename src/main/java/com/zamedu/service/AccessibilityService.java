package com.zamedu.service;

import com.zamedu.model.UIPreferences;
import com.zamedu.model.UITheme;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AccessibilityService {
    ResponseEntity<?> detectAccessibilityNeeds(String token, Object request);
    ResponseEntity<?> getUIPreferences(String token);
    ResponseEntity<?> updateUIPreferences(String token, UIPreferences preferences);
    ResponseEntity<List<UITheme>> getAvailableThemes();
    ResponseEntity<?> setTheme(String token, String themeId);
}