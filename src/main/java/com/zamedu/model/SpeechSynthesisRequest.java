package com.zamedu.model;

import lombok.Data;

@Data
public class SpeechSynthesisRequest {
    private String text;
    private String languageCode;
    private String voiceId;
    private double speed = 1.0;
    private double pitch = 1.0;
}
