package com.zamedu.model;

import lombok.Data;
import java.util.Map;

@Data
public class UIPreferences {
    private String theme;
    private String fontSize;
    private String fontFamily;
    private boolean highContrast;
    private boolean screenReaderEnabled;
    private boolean reduceMotion;
    private Map<String, Object> customPreferences;
}
