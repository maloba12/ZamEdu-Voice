package com.zamedu.model;

import java.util.HashMap;
import java.util.Map;

public class UserPreferences {
    private String userId;
    private String interfaceLanguage = "en";
    private String voiceLanguage = "en-US";
    private boolean visualImpairment = false;
    private boolean hearingImpairment = false;
    private boolean mobilityImpairment = false;
    private boolean learningDisability = false;
    private String textSize = "medium";
    private double voiceSpeed = 1.0;
    private boolean highContrast = false;
    private boolean fingerprintRegistered = false;
    private Map<String, Object> additionalPreferences = new HashMap<>();

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getInterfaceLanguage() {
        return interfaceLanguage;
    }

    public void setInterfaceLanguage(String interfaceLanguage) {
        this.interfaceLanguage = interfaceLanguage;
    }

    public String getVoiceLanguage() {
        return voiceLanguage;
    }

    public void setVoiceLanguage(String voiceLanguage) {
        this.voiceLanguage = voiceLanguage;
    }

    public boolean isVisualImpairment() {
        return visualImpairment;
    }

    public void setVisualImpairment(boolean visualImpairment) {
        this.visualImpairment = visualImpairment;
    }

    public boolean isHearingImpairment() {
        return hearingImpairment;
    }

    public void setHearingImpairment(boolean hearingImpairment) {
        this.hearingImpairment = hearingImpairment;
    }

    public boolean isMobilityImpairment() {
        return mobilityImpairment;
    }

    public void setMobilityImpairment(boolean mobilityImpairment) {
        this.mobilityImpairment = mobilityImpairment;
    }

    public boolean isLearningDisability() {
        return learningDisability;
    }

    public void setLearningDisability(boolean learningDisability) {
        this.learningDisability = learningDisability;
    }

    public String getTextSize() {
        return textSize;
    }

    public void setTextSize(String textSize) {
        this.textSize = textSize;
    }

    public double getVoiceSpeed() {
        return voiceSpeed;
    }

    public void setVoiceSpeed(double voiceSpeed) {
        this.voiceSpeed = voiceSpeed;
    }

    public boolean isHighContrast() {
        return highContrast;
    }

    public void setHighContrast(boolean highContrast) {
        this.highContrast = highContrast;
    }

    public boolean isFingerprintRegistered() {
        return fingerprintRegistered;
    }

    public void setFingerprintRegistered(boolean fingerprintRegistered) {
        this.fingerprintRegistered = fingerprintRegistered;
    }

    public Map<String, Object> getAdditionalPreferences() {
        return additionalPreferences;
    }

    public void setAdditionalPreferences(Map<String, Object> additionalPreferences) {
        this.additionalPreferences = additionalPreferences;
    }
}
