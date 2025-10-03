package com.zamedu.service;

import com.zamedu.model.Language;
import com.zamedu.model.TranslationRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LanguageService {
    ResponseEntity<List<Language>> getSupportedLanguages();
    ResponseEntity<?> translateContent(String token, TranslationRequest request);
}