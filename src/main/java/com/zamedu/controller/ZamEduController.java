package com.zamedu.controller;

import com.zamedu.entity.User;
import com.zamedu.model.*;
import com.zamedu.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ZamEduController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private SpeechService speechService;
    
    @Autowired
    private LanguageService languageService;
    
    @Autowired
    private AccessibilityService accessibilityService;
    
    @Autowired
    private FingerprintService fingerprintService;

    // User Authentication Endpoints
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequest request) {
        return userService.registerUser(request);
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        return userService.authenticateUser(request);
    }
    
    @GetMapping("/user/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String token) {
        return userService.getUserProfile(token);
    }

    // Fingerprint and Disability Detection Endpoints
    @PostMapping("/fingerprint/register")
    public ResponseEntity<?> registerFingerprint(
            @RequestHeader("Authorization") String token,
            @RequestParam("fingerprintData") MultipartFile fingerprintData) {
        return fingerprintService.registerFingerprint(token, fingerprintData);
    }
    
    @PostMapping("/fingerprint/verify")
    public ResponseEntity<?> verifyFingerprint(
            @RequestHeader("Authorization") String token,
            @RequestParam("fingerprintData") MultipartFile fingerprintData) {
        return fingerprintService.verifyFingerprint(token, fingerprintData);
    }
    
    @PostMapping("/accessibility/detect")
    public ResponseEntity<?> detectAccessibilityNeeds(
            @RequestHeader("Authorization") String token,
            @RequestBody AccessibilityDetectionRequest request) {
        return accessibilityService.detectAccessibilityNeeds(token, request);
    }

    // Language and Speech Endpoints
    @GetMapping("/languages")
    public ResponseEntity<List<User.Language>> getSupportedLanguages() {
        return languageService.getSupportedLanguages();
    }
    
    @PostMapping("/speech/recognize")
    public ResponseEntity<?> recognizeSpeech(
            @RequestHeader("Authorization") String token,
            @RequestParam("audio") MultipartFile audioFile,
            @RequestParam("language") String languageCode) {
        return speechService.recognizeSpeech(token, audioFile, languageCode);
    }
    
    @PostMapping("/speech/synthesize")
    public ResponseEntity<?> synthesizeSpeech(
            @RequestHeader("Authorization") String token,
            @RequestBody SpeechSynthesisRequest request) {
        return speechService.synthesizeSpeech(token, request);
    }

    // UI Customization Endpoints
    @GetMapping("/ui/preferences")
    public ResponseEntity<?> getUIPreferences(@RequestHeader("Authorization") String token) {
        return accessibilityService.getUIPreferences(token);
    }
    
    @PostMapping("/ui/preferences")
    public ResponseEntity<?> updateUIPreferences(
            @RequestHeader("Authorization") String token,
            @RequestBody UIPreferences preferences) {
        return accessibilityService.updateUIPreferences(token, preferences);
    }
    
    // Text-to-Speech and Accessibility Features
    @PostMapping("/text-to-speech")
    public ResponseEntity<?> convertTextToSpeech(
            @RequestHeader("Authorization") String token,
            @RequestBody TextToSpeechRequest request) {
        return speechService.convertTextToSpeech(token, request);
    }
    
    @GetMapping("/ui/themes")
    public ResponseEntity<List<UITheme>> getAvailableThemes() {
        return accessibilityService.getAvailableThemes();
    }
    
    @PostMapping("/ui/theme")
    public ResponseEntity<?> setTheme(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> themeRequest) {
        return accessibilityService.setTheme(token, themeRequest.get("themeId"));
    }

    // Content Delivery Endpoints
    @GetMapping("/content/{contentId}")
    public ResponseEntity<?> getContent(
            @RequestHeader("Authorization") String token,
            @PathVariable String contentId,
            @RequestParam(required = false, defaultValue = "en") String language) {
        return userService.getContent(token, contentId, language);
    }
    
    @PostMapping("/content/translate")
    public ResponseEntity<?> translateContent(
            @RequestHeader("Authorization") String token,
            @RequestBody TranslationRequest request) {
        return languageService.translateContent(token, request);
    }
    
    // System Status and Health Check
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        return ResponseEntity.ok(Map.of(
            "status", "operational",
            "version", "1.0.0",
            "services", List.of(
                Map.of("name", "authentication", "status", "up"),
                Map.of("name", "speech", "status", "up"),
                Map.of("name", "language", "status", "up")
            )
        ));
    }
    
    // Error handling for invalid endpoints
    @RequestMapping("/**")
    public ResponseEntity<?> handleInvalidEndpoint() {
        return ResponseEntity.notFound().build();
    }
}
