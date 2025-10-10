package com.zamedu.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.config}")
    private String firebaseConfig;

    @Value("${firebase.database.url}")
    private String databaseUrl;

    @PostConstruct
    public void initialize() {
        // Only initialize Firebase if config is provided
        if (firebaseConfig == null || firebaseConfig.trim().isEmpty()) {
            System.out.println("Firebase configuration not provided - skipping Firebase initialization");
            return;
        }
        
        try {
            // Decode the base64 encoded service account JSON
            byte[] decodedBytes = Base64.getDecoder().decode(firebaseConfig);
            String serviceAccountJson = new String(decodedBytes, StandardCharsets.UTF_8);
            
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(
                    new ByteArrayInputStream(serviceAccountJson.getBytes())))
                .setDatabaseUrl(databaseUrl)
                .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (Exception e) {
            System.out.println("Failed to initialize Firebase: " + e.getMessage());
            // Don't throw exception - allow app to start without Firebase
        }
    }

    @Bean
    public FirebaseAuth firebaseAuth() {
        try {
            return FirebaseApp.getApps().isEmpty() ? null : FirebaseAuth.getInstance();
        } catch (Exception e) {
            return null;
        }
    }

    @Bean
    public Firestore firestore() {
        try {
            return FirebaseApp.getApps().isEmpty() ? null : FirestoreClient.getFirestore();
        } catch (Exception e) {
            return null;
        }
    }
}
