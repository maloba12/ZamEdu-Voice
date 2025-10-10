package com.zamedu.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * User Entity - Represents a ZamEdu Voice user with accessibility features
 */
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;
    
    @Email(message = "Invalid email format")
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(name = "firebase_uid", unique = true)
    private String firebaseUid;
    
    @Column(name = "profile_picture_url")
    private String profilePictureUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType = UserType.NORMAL;
    
    @Column(name = "fingerprint_hash")
    private String fingerprintHash;
    
    @Column(name = "has_disability")
    private Boolean hasDisability = false;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "disability_type")
    private DisabilityType disabilityType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_language", nullable = false)
    private Language preferredLanguage = Language.ENGLISH;
    
    @Column(name = "voice_enabled")
    private Boolean voiceEnabled = true;
    
    @Column(name = "text_to_speech_enabled")
    private Boolean textToSpeechEnabled = true;
    
    @Column(name = "high_contrast_mode")
    private Boolean highContrastMode = false;
    
    @Column(name = "large_text_mode")
    private Boolean largeTextMode = false;
    
    @Column(name = "learning_streak")
    private Integer learningStreak = 0;
    
    @Column(name = "total_questions_asked")
    private Integer totalQuestionsAsked = 0;
    
    @Column(name = "study_time_hours")
    private Double studyTimeHours = 0.0;
    
    @Column(name = "achievements_count")
    private Integer achievementsCount = 0;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<LearningProgress> learningProgress;
    
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Achievement> achievements;

    @JsonIgnore
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ChatMessage> sentMessages;

    @JsonIgnore
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ChatMessage> receivedMessages;

    @JsonIgnore
    @OneToMany(mappedBy = "requester", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<FriendRequest> sentFriendRequests;

    @JsonIgnore
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<FriendRequest> receivedFriendRequests;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
        name = "user_friends",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<User> friends = new HashSet<>();
    
    // Constructors
    public User() {
        this.createdAt = LocalDateTime.now();
    }
    
    public User(String name, String email, String firebaseUid) {
        this();
        this.name = name;
        this.email = email;
        this.firebaseUid = firebaseUid;
    }
    
    // Enums
    public enum UserType {
        NORMAL, VISUALLY_IMPAIRED, HEARING_IMPAIRED, MOTOR_IMPAIRED, COGNITIVE_IMPAIRED
    }
    
    public enum DisabilityType {
        VISUAL_IMPAIRMENT, HEARING_IMPAIRMENT, MOTOR_IMPAIRMENT, COGNITIVE_IMPAIRMENT, MULTIPLE
    }
    
    public enum Language {
        ENGLISH("en", "English"),
        BEMBA("bem", "Ichibemba"),
        NYANJA("nya", "Chinyanja"),
        TONGA("toi", "Chitonga"),
        LOZI("loz", "Silozi");
        
        private final String code;
        private final String nativeName;
        
        Language(String code, String nativeName) {
            this.code = code;
            this.nativeName = nativeName;
        }
        
        public String getCode() { return code; }
        public String getNativeName() { return nativeName; }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFirebaseUid() { return firebaseUid; }
    public void setFirebaseUid(String firebaseUid) { this.firebaseUid = firebaseUid; }
    
    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
    
    public UserType getUserType() { return userType; }
    public void setUserType(UserType userType) { this.userType = userType; }
    
    public String getFingerprintHash() { return fingerprintHash; }
    public void setFingerprintHash(String fingerprintHash) { this.fingerprintHash = fingerprintHash; }
    
    public Boolean getHasDisability() { return hasDisability; }
    public void setHasDisability(Boolean hasDisability) { this.hasDisability = hasDisability; }
    
    public DisabilityType getDisabilityType() { return disabilityType; }
    public void setDisabilityType(DisabilityType disabilityType) { this.disabilityType = disabilityType; }
    
    public Language getPreferredLanguage() { return preferredLanguage; }
    public void setPreferredLanguage(Language preferredLanguage) { this.preferredLanguage = preferredLanguage; }
    
    public Boolean getVoiceEnabled() { return voiceEnabled; }
    public void setVoiceEnabled(Boolean voiceEnabled) { this.voiceEnabled = voiceEnabled; }
    
    public Boolean getTextToSpeechEnabled() { return textToSpeechEnabled; }
    public void setTextToSpeechEnabled(Boolean textToSpeechEnabled) { this.textToSpeechEnabled = textToSpeechEnabled; }
    
    public Boolean getHighContrastMode() { return highContrastMode; }
    public void setHighContrastMode(Boolean highContrastMode) { this.highContrastMode = highContrastMode; }
    
    public Boolean getLargeTextMode() { return largeTextMode; }
    public void setLargeTextMode(Boolean largeTextMode) { this.largeTextMode = largeTextMode; }
    
    public Integer getLearningStreak() { return learningStreak; }
    public void setLearningStreak(Integer learningStreak) { this.learningStreak = learningStreak; }
    
    public Integer getTotalQuestionsAsked() { return totalQuestionsAsked; }
    public void setTotalQuestionsAsked(Integer totalQuestionsAsked) { this.totalQuestionsAsked = totalQuestionsAsked; }
    
    public Double getStudyTimeHours() { return studyTimeHours; }
    public void setStudyTimeHours(Double studyTimeHours) { this.studyTimeHours = studyTimeHours; }
    
    public Integer getAchievementsCount() { return achievementsCount; }
    public void setAchievementsCount(Integer achievementsCount) { this.achievementsCount = achievementsCount; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Set<LearningProgress> getLearningProgress() { return learningProgress; }
    public void setLearningProgress(Set<LearningProgress> learningProgress) { this.learningProgress = learningProgress; }
    
    public Set<Achievement> getAchievements() { return achievements; }
    public void setAchievements(Set<Achievement> achievements) { this.achievements = achievements; }
    
    public Set<ChatMessage> getSentMessages() { return sentMessages; }
    public void setSentMessages(Set<ChatMessage> sentMessages) { this.sentMessages = sentMessages; }
    
    public Set<ChatMessage> getReceivedMessages() { return receivedMessages; }
    public void setReceivedMessages(Set<ChatMessage> receivedMessages) { this.receivedMessages = receivedMessages; }
    
    public Set<FriendRequest> getSentFriendRequests() { return sentFriendRequests; }
    public void setSentFriendRequests(Set<FriendRequest> sentFriendRequests) { this.sentFriendRequests = sentFriendRequests; }
    
    public Set<FriendRequest> getReceivedFriendRequests() { return receivedFriendRequests; }
    public void setReceivedFriendRequests(Set<FriendRequest> receivedFriendRequests) { this.receivedFriendRequests = receivedFriendRequests; }
    
    public Set<User> getFriends() { return friends; }
    public void setFriends(Set<User> friends) { this.friends = friends; }
    
    // Utility methods
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }
    
    public void incrementQuestionsAsked() {
        this.totalQuestionsAsked++;
    }
    
    public void addStudyTime(double hours) {
        this.studyTimeHours += hours;
    }
    
    public void incrementAchievements() {
        this.achievementsCount++;
    }
    
    public boolean isAccessibilityUser() {
        return this.hasDisability || this.userType != UserType.NORMAL;
    }
}
