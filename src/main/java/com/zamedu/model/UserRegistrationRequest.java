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
}
