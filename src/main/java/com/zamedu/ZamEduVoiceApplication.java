package com.zamedu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ZamEdu Voice - Main Application Class
 * Voice-enabled educational assistant with accessibility features
 * Designed specifically for Zambian users with cultural integration
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class ZamEduVoiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZamEduVoiceApplication.class, args);
        System.out.println("🎤 ZamEdu Voice Application Started Successfully!");
        System.out.println("🇿🇲 Empowering Zambian Education Through Voice Technology");
    }
}
