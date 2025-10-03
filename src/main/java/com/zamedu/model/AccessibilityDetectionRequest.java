package com.zamedu.model;

import lombok.Data;
import java.util.Map;

@Data
public class AccessibilityDetectionRequest {
    private boolean useFingerprint;
    private boolean useCamera;
    private boolean useQuestionnaire;
    private Map<String, Object> detectionData;
}
