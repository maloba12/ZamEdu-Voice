package com.zamedu.model;

import lombok.Data;

@Data
public class UserRegistrationRequest {
    private String username;
    private String email;
    private String password;
    private String fullName;
    private boolean hasDisability;
    private String preferredLanguage;
    
    // Manual getters/setters for compilation
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public boolean isHasDisability() { return hasDisability; }
    public void setHasDisability(boolean hasDisability) { this.hasDisability = hasDisability; }
    
    public String getPreferredLanguage() { return preferredLanguage; }
    public void setPreferredLanguage(String preferredLanguage) { this.preferredLanguage = preferredLanguage; }
}
