package com.zamedu.model;

import lombok.Data;
import java.util.List;

@Data
public class TranslationRequest {
    private List<String> texts;
    private String sourceLanguage;
    private String targetLanguage;
    private boolean preserveFormatting;
    
    // Manual getters/setters
    public List<String> getTexts() { return texts; }
    public void setTexts(List<String> texts) { this.texts = texts; }
    
    public String getSourceLanguage() { return sourceLanguage; }
    public void setSourceLanguage(String sourceLanguage) { this.sourceLanguage = sourceLanguage; }
    
    public String getTargetLanguage() { return targetLanguage; }
    public void setTargetLanguage(String targetLanguage) { this.targetLanguage = targetLanguage; }
    
    public boolean isPreserveFormatting() { return preserveFormatting; }
    public void setPreserveFormatting(boolean preserveFormatting) { this.preserveFormatting = preserveFormatting; }
}
