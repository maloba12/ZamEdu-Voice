package com.zamedu.service;

import com.zamedu.entity.User;
import com.zamedu.model.TranslationRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LanguageService {
    ResponseEntity<List<User.Language>> getSupportedLanguages();
    ResponseEntity<?> translateContent(String token, TranslationRequest request);
}