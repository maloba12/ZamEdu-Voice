package com.zamedu.service;

import com.zamedu.model.SpeechSynthesisRequest;
import com.zamedu.model.TextToSpeechRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface SpeechService {
    ResponseEntity<?> recognizeSpeech(String token, MultipartFile audioFile, String languageCode);
    ResponseEntity<?> synthesizeSpeech(String token, SpeechSynthesisRequest request);
    ResponseEntity<?> convertTextToSpeech(String token, TextToSpeechRequest request);
}