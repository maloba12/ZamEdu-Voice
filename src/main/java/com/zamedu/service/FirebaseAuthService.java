package com.zamedu.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.zamedu.entity.User;
import com.zamedu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Firebase Authentication Service
 * Handles Google authentication and user management
 */
@Service
public class FirebaseAuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MultilingualService multilingualService;
    
    private FirebaseAuth firebaseAuth;
    
    @PostConstruct
    public void initialize() {
        try {
            // Initialize Firebase Auth
            this.firebaseAuth = FirebaseAuth.getInstance();
            System.out.println("🔥 Firebase Authentication initialized successfully!");
        } catch (Exception e) {
            System.err.println("❌ Failed to initialize Firebase Auth: " + e.getMessage());
        }
    }
    
    /**
     * Verify Firebase ID token and get user information
     * @param idToken Firebase ID token from client
     * @return CompletableFuture with user information
     */
    public CompletableFuture<AuthResult> verifyIdToken(String idToken) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Verify the Firebase ID token
                FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
                
                // Get user information from Firebase
                UserRecord userRecord = firebaseAuth.getUser(decodedToken.getUid());
                
                // Find or create user in our database
                Optional<User> existingUser = userRepository.findByFirebaseUid(decodedToken.getUid());
                User user;
                
                if (existingUser.isPresent()) {
                    user = existingUser.get();
                    user.updateLastLogin();
                    userRepository.save(user);
                } else {
                    // Create new user
                    user = createNewUser(userRecord);
                }
                
                // Generate personalized greeting
                String greeting = generatePersonalizedGreeting(user);
                
                return new AuthResult(
                    true,
                    user,
                    greeting,
                    "Authentication successful",
                    decodedToken.getUid()
                );
                
            } catch (FirebaseAuthException e) {
                return new AuthResult(
                    false,
                    null,
                    null,
                    "Authentication failed: " + e.getMessage(),
                    null
                );
            } catch (Exception e) {
                return new AuthResult(
                    false,
                    null,
                    null,
                    "Unexpected error: " + e.getMessage(),
                    null
                );
            }
        });
    }
    
    /**
     * Create new user from Firebase user record
     */
    private User createNewUser(UserRecord userRecord) {
        User user = new User();
        user.setFirebaseUid(userRecord.getUid());
        user.setEmail(userRecord.getEmail());
        user.setName(userRecord.getDisplayName() != null ? 
                   userRecord.getDisplayName() : 
                   userRecord.getEmail().split("@")[0]);
        
        // Set profile picture if available
        if (userRecord.getPhotoUrl() != null) {
            user.setProfilePictureUrl(userRecord.getPhotoUrl());
        }
        
        // Set default language to English
        user.setPreferredLanguage(User.Language.ENGLISH);
        
        // Save to database
        user = userRepository.save(user);
        
        System.out.println("✅ New user created: " + user.getName() + " (" + user.getEmail() + ")");
        return user;
    }
    
    /**
     * Generate personalized greeting in user's preferred language
     */
    private String generatePersonalizedGreeting(User user) {
        String name = user.getName();
        User.Language language = user.getPreferredLanguage();
        
        // Get current time for time-appropriate greeting
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        
        // Generate greeting based on language and time
        String greeting;
        if (language == User.Language.ENGLISH) {
            if (hour < 12) {
                greeting = "Good morning";
            } else if (hour < 18) {
                greeting = "Good afternoon";
            } else {
                greeting = "Good evening";
            }
            greeting += ", " + name + "! Welcome to ZamEdu Voice. What can I help you learn today?";
        } else {
            // Use multilingual service for Zambian languages
            greeting = multilingualService.getGreeting(language, name, hour);
        }
        
        return greeting;
    }
    
    /**
     * Update user profile information
     */
    public CompletableFuture<User> updateUserProfile(String firebaseUid, Map<String, Object> profileData) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<User> userOpt = userRepository.findByFirebaseUid(firebaseUid);
                if (userOpt.isEmpty()) {
                    throw new RuntimeException("User not found");
                }
                
                User user = userOpt.get();
                
                // Update profile fields
                if (profileData.containsKey("name")) {
                    user.setName((String) profileData.get("name"));
                }
                if (profileData.containsKey("preferredLanguage")) {
                    String langCode = (String) profileData.get("preferredLanguage");
                    user.setPreferredLanguage(User.Language.valueOf(langCode.toUpperCase()));
                }
                if (profileData.containsKey("voiceEnabled")) {
                    user.setVoiceEnabled((Boolean) profileData.get("voiceEnabled"));
                }
                if (profileData.containsKey("textToSpeechEnabled")) {
                    user.setTextToSpeechEnabled((Boolean) profileData.get("textToSpeechEnabled"));
                }
                if (profileData.containsKey("highContrastMode")) {
                    user.setHighContrastMode((Boolean) profileData.get("highContrastMode"));
                }
                if (profileData.containsKey("largeTextMode")) {
                    user.setLargeTextMode((Boolean) profileData.get("largeTextMode"));
                }
                
                return userRepository.save(user);
                
            } catch (Exception e) {
                throw new RuntimeException("Failed to update profile: " + e.getMessage());
            }
        });
    }
    
    /**
     * Delete user account
     */
    public CompletableFuture<Boolean> deleteUser(String firebaseUid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Delete from Firebase
                firebaseAuth.deleteUser(firebaseUid);
                
                // Delete from our database
                Optional<User> userOpt = userRepository.findByFirebaseUid(firebaseUid);
                if (userOpt.isPresent()) {
                    userRepository.delete(userOpt.get());
                }
                
                return true;
                
            } catch (Exception e) {
                System.err.println("Failed to delete user: " + e.getMessage());
                return false;
            }
        });
    }
    
    /**
     * Get user by Firebase UID
     */
    public Optional<User> getUserByFirebaseUid(String firebaseUid) {
        return userRepository.findByFirebaseUid(firebaseUid);
    }
    
    /**
     * Check if user exists in Firebase
     */
    public CompletableFuture<Boolean> userExists(String firebaseUid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                firebaseAuth.getUser(firebaseUid);
                return true;
            } catch (FirebaseAuthException e) {
                return false;
            }
        });
    }
    
    /**
     * Create custom token for user
     */
    public CompletableFuture<String> createCustomToken(String uid, Map<String, Object> claims) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return firebaseAuth.createCustomToken(uid, claims);
            } catch (FirebaseAuthException e) {
                throw new RuntimeException("Failed to create custom token: " + e.getMessage());
            }
        });
    }
    
    /**
     * Set custom user claims (for roles, permissions, etc.)
     */
    public CompletableFuture<Void> setCustomUserClaims(String uid, Map<String, Object> claims) {
        return CompletableFuture.runAsync(() -> {
            try {
                firebaseAuth.setCustomUserClaims(uid, claims);
            } catch (FirebaseAuthException e) {
                throw new RuntimeException("Failed to set custom claims: " + e.getMessage());
            }
        });
    }
    
    /**
     * Authentication Result
     */
    public static class AuthResult {
        private final boolean success;
        private final User user;
        private final String greeting;
        private final String message;
        private final String firebaseUid;
        
        public AuthResult(boolean success, User user, String greeting, String message, String firebaseUid) {
            this.success = success;
            this.user = user;
            this.greeting = greeting;
            this.message = message;
            this.firebaseUid = firebaseUid;
        }
        
        public boolean isSuccess() { return success; }
        public User getUser() { return user; }
        public String getGreeting() { return greeting; }
        public String getMessage() { return message; }
        public String getFirebaseUid() { return firebaseUid; }
    }
}
