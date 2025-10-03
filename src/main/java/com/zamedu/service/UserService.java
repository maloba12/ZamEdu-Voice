package com.zamedu.service;

import com.zamedu.model.UserPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {
    
    private final FirebaseAuth firebaseAuth;
    private final Firestore firestore;
    
    public UserService() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firestore = FirestoreClient.getFirestore();
    }
    
    public String validateToken(String authToken) {
        try {
            if (authToken != null && authToken.startsWith("Bearer ")) {
                String idToken = authToken.substring(7);
                FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
                return decodedToken.getUid();
            }
            return null;
        } catch (FirebaseAuthException e) {
            return null;
        }
    }
    
    public UserPreferences getUserPreferences(String userId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("userPreferences").document(userId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        
        if (document.exists()) {
            return document.toObject(UserPreferences.class);
        } else {
            // Return default preferences if none exist
            return new UserPreferences();
        }
    }
    
    public void saveUserPreferences(String userId, Map<String, Object> preferences) {
        DocumentReference docRef = firestore.collection("userPreferences").document(userId);
        docRef.set(preferences, SetOptions.merge());
    }
    
    public String processSpeech(String text, String language, String userId) {
        // This is where you would implement your speech processing logic
        // For now, we'll just echo the text back
        return "You said: " + text + " in " + language;
    }
}
