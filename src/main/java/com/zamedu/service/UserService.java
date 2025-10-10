package com.zamedu.service;

import com.zamedu.entity.User;
import com.zamedu.model.LoginRequest;
import com.zamedu.model.UserPreferences;
import com.zamedu.model.UserRegistrationRequest;
import com.zamedu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public UserService() {
        // Constructor
    }
    
    public String validateToken(String authToken) {
        // Simplified token validation for testing
        if (authToken != null && authToken.startsWith("Bearer ")) {
            return authToken.substring(7);
        }
        return null;
    }
    
    public ResponseEntity<?> registerUser(UserRegistrationRequest request) {
        // Stub implementation
        User user = new User();
        user.setName(request.getFullName());
        user.setEmail(request.getEmail());
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "User registered", "userId", user.getId()));
    }
    
    public ResponseEntity<?> authenticateUser(LoginRequest request) {
        // Stub implementation - find by username or email
        Optional<User> user = userRepository.findByEmail(request.getUsername());
        if (user.isPresent()) {
            return ResponseEntity.ok(Map.of("message", "Login successful", "userId", user.get().getId()));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials"));
    }
    
    public ResponseEntity<?> getUserProfile(String userId) {
        // Stub implementation
        try {
            Long id = Long.parseLong(userId);
            Optional<User> user = userRepository.findById(id);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            }
        } catch (NumberFormatException e) {
            // Invalid ID format
        }
        return ResponseEntity.notFound().build();
    }
    
    public ResponseEntity<?> getContent(String token, String subject, String level) {
        // Stub implementation
        Map<String, Object> content = new HashMap<>();
        content.put("subject", subject);
        content.put("level", level);
        content.put("content", "Sample educational content");
        return ResponseEntity.ok(content);
    }
    
    public UserPreferences getUserPreferences(String userId) {
        // Return default preferences
        return new UserPreferences();
    }
    
    public void saveUserPreferences(String userId, Map<String, Object> preferences) {
        // Stub implementation
    }
    
    public String processSpeech(String text, String language, String userId) {
        // Echo back for testing
        return "You said: " + text + " in " + language;
    }
}
