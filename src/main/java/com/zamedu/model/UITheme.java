package com.zamedu.model;

import lombok.Data;
import java.util.Map;

@Data
public class UITheme {
    private String id;
    private String name;
    private String description;
    private Map<String, String> colors;
    private boolean highContrast;
    private boolean darkMode;
}
