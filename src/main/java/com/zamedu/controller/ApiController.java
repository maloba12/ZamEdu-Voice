package com.zamedu.controller;

import com.zamedu.model.UserPreferences;
import com.zamedu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/preferences")
    public ResponseEntity<?> getUserPreferences(@RequestHeader("Authorization") String authToken) {
        try {
            // Validate token and get user ID
            String userId = userService.validateToken(authToken);
            if (userId == null) {
                return ResponseEntity.status(401).body("Unauthorized");
            }
            
            // Get user preferences
            UserPreferences preferences = userService.getUserPreferences(userId);
            return ResponseEntity.ok(preferences);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving user preferences: " + e.getMessage());
        }
    }

    @PostMapping("/user/preferences")
    public ResponseEntity<?> saveUserPreferences(
            @RequestHeader("Authorization") String authToken,
            @RequestBody Map<String, Object> preferences) {
        try {
            String userId = userService.validateToken(authToken);
            if (userId == null) {
                return ResponseEntity.status(401).body("Unauthorized");
            }
            
            userService.saveUserPreferences(userId, preferences);
            return ResponseEntity.ok("Preferences saved successfully");
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving preferences: " + e.getMessage());
        }
    }

    @PostMapping("/speech/process")
    public ResponseEntity<?> processSpeech(
            @RequestHeader("Authorization") String authToken,
            @RequestBody Map<String, String> request) {
        try {
            String userId = userService.validateToken(authToken);
            if (userId == null) {
                return ResponseEntity.status(401).body("Unauthorized");
            }
            
            String text = request.get("text");
            String language = request.get("language");
            
            // Process the speech (this is where you'd add your speech processing logic)
            String response = userService.processSpeech(text, language, userId);
            
            return ResponseEntity.ok(Map.of("response", response));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing speech: " + e.getMessage());
        }
    }
}
