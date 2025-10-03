package com.zamedu.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface FingerprintService {
    ResponseEntity<?> registerFingerprint(String token, MultipartFile fingerprintData);
    ResponseEntity<?> verifyFingerprint(String token, MultipartFile fingerprintData);
}