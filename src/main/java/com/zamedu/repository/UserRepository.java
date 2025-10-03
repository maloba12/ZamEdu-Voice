package com.zamedu.repository;

import com.zamedu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * User Repository
 * Data access layer for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by Firebase UID
     */
    Optional<User> findByFirebaseUid(String firebaseUid);
    
    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find users with disabilities
     */
    List<User> findByHasDisabilityTrue();
    
    /**
     * Find users by disability type
     */
    List<User> findByDisabilityType(User.DisabilityType disabilityType);
    
    /**
     * Find users by preferred language
     */
    List<User> findByPreferredLanguage(User.Language language);
    
    /**
     * Find active users
     */
    List<User> findByIsActiveTrue();
    
    /**
     * Find users by user type
     */
    List<User> findByUserType(User.UserType userType);
    
    /**
     * Find users with learning streaks above threshold
     */
    @Query("SELECT u FROM User u WHERE u.learningStreak >= :minStreak")
    List<User> findUsersWithLearningStreak(@Param("minStreak") Integer minStreak);
    
    /**
     * Find top learners by questions asked
     */
    @Query("SELECT u FROM User u ORDER BY u.totalQuestionsAsked DESC")
    List<User> findTopLearners();
    
    /**
     * Find users who logged in recently
     */
    @Query("SELECT u FROM User u WHERE u.lastLogin >= :since")
    List<User> findRecentUsers(@Param("since") LocalDateTime since);
    
    /**
     * Count users by disability type
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.disabilityType = :disabilityType")
    Long countByDisabilityType(@Param("disabilityType") User.DisabilityType disabilityType);
    
    /**
     * Count users by language preference
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.preferredLanguage = :language")
    Long countByLanguage(@Param("language") User.Language language);
    
    /**
     * Find users with accessibility features enabled
     */
    @Query("SELECT u FROM User u WHERE u.voiceEnabled = true OR u.textToSpeechEnabled = true OR u.highContrastMode = true OR u.largeTextMode = true")
    List<User> findAccessibilityUsers();
    
    /**
     * Find users by fingerprint hash (for disability detection)
     */
    Optional<User> findByFingerprintHash(String fingerprintHash);
    
    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if Firebase UID exists
     */
    boolean existsByFirebaseUid(String firebaseUid);
}
