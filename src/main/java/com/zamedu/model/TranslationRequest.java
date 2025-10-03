package com.zamedu.model;

import lombok.Data;
import java.util.List;

@Data
public class TranslationRequest {
    private List<String> texts;
    private String sourceLanguage;
    private String targetLanguage;
    private boolean preserveFormatting;
}
