package com.zamedu.model;

import lombok.Data;
import java.util.Map;

@Data
public class TextToSpeechRequest {
    private String text;
    private String languageCode;
    private String voiceId;
    private double speed = 1.0;
    private double pitch = 1.0;
    private Map<String, Object> ssmlMarks;
}
