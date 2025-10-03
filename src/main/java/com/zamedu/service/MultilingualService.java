package com.zamedu.service;

import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import com.zamedu.entity.User;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Multilingual Service
 * Handles dynamic language switching for voice and text
 * Supports Zambian languages with cultural context
 */
@Service
public class MultilingualService {
    
    private TextToSpeechClient textToSpeechClient;
    private final Map<String, LanguageTranslation> translations = new HashMap<>();
    
    @PostConstruct
    public void initialize() {
        try {
            // Initialize Google Text-to-Speech client
            this.textToSpeechClient = TextToSpeechClient.create();
            initializeTranslations();
            System.out.println("🗣️ Multilingual Service initialized successfully!");
        } catch (IOException e) {
            System.err.println("❌ Failed to initialize Text-to-Speech client: " + e.getMessage());
        }
    }
    
    /**
     * Initialize translation mappings for Zambian languages
     */
    private void initializeTranslations() {
        // English translations
        translations.put("en", new LanguageTranslation(
            "Welcome", "What can I help you with?", "Good morning", "Good afternoon", "Good evening",
            "Learn with Voice", "Education", "Location", "Profile", "Settings",
            "Hello", "Thank you", "Please", "Yes", "No", "Help", "Exit", "Search", "Save", "Cancel"
        ));
        
        // Bemba translations
        translations.put("bem", new LanguageTranslation(
            "Mwabonwa", "Ninshi ningasenda?", "Mwabukeni", "Mwaiseni", "Mwaileni",
            "Landa na Moyo", "Ukwishiba", "Lwapato", "Lupako", "Imisambo",
            "Moni", "Natotela", "Nakupa", "Ee", "Awe", "Amusende", "Fuma", "Fwaya", "Lunga", "Lekani"
        ));
        
        // Nyanja translations
        translations.put("nya", new LanguageTranslation(
            "Moni", "Zimene ndingakuthandizeni?", "Mwadzuka bwanji", "Mwadzuka bwanji", "Mwadzuka bwanji",
            "Dzidza neMimhanzi", "Phunziro", "Malo", "Mawu", "Zosintha",
            "Moni", "Zikomo", "Chonde", "Inde", "Iyayi", "Thandizo", "Pita", "Fufuzani", "Sunga", "Letsani"
        ));
        
        // Tonga translations
        translations.put("toi", new LanguageTranslation(
            "Mwapona", "Ninshi ningasenda?", "Mwabukeni", "Mwaiseni", "Mwaileni",
            "Ilamba neMwindo", "Ukufunda", "Lwapato", "Lupako", "Imisambo",
            "Mwapona", "Natotela", "Nakupa", "Ee", "Awe", "Amusende", "Fuma", "Fwaya", "Lunga", "Lekani"
        ));
        
        // Lozi translations
        translations.put("loz", new LanguageTranslation(
            "Lumela", "Ke ka ku thusa joang?", "Lumela", "Lumela", "Lumela",
            "Ithuta ka Lentswe", "Thuto", "Sebaka", "Profaele", "Dikgetho",
            "Lumela", "Ke a leboha", "Ka kopo", "Ee", "Che", "Thuso", "Tswa", "Batla", "Boloka", "Khansela"
        ));
    }
    
    /**
     * Get greeting in specified language
     */
    public String getGreeting(User.Language language, String name, int hour) {
        String langCode = language.getCode();
        LanguageTranslation translation = translations.get(langCode);
        
        if (translation == null) {
            return "Welcome, " + name + "! What can I help you with today?";
        }
        
        String timeGreeting;
        if (hour < 12) {
            timeGreeting = translation.getGoodMorning();
        } else if (hour < 18) {
            timeGreeting = translation.getGoodAfternoon();
        } else {
            timeGreeting = translation.getGoodEvening();
        }
        
        return timeGreeting + ", " + name + "! " + translation.getWelcomeMessage();
    }
    
    /**
     * Translate text to specified language
     */
    public String translateText(String text, User.Language fromLanguage, User.Language toLanguage) {
        if (fromLanguage == toLanguage) {
            return text;
        }
        
        String langCode = toLanguage.getCode();
        LanguageTranslation translation = translations.get(langCode);
        
        if (translation == null) {
            return text; // Return original if translation not available
        }
        
        // Simple word-by-word translation (in production, use professional translation API)
        return performTranslation(text, fromLanguage.getCode(), langCode, translation);
    }
    
    /**
     * Perform text translation
     */
    private String performTranslation(String text, String fromLang, String toLang, LanguageTranslation translation) {
        String lowerText = text.toLowerCase();
        
        // Map common words and phrases
        Map<String, String> wordMap = getWordMapping(translation);
        
        String result = text;
        for (Map.Entry<String, String> entry : wordMap.entrySet()) {
            result = result.replaceAll("(?i)\\b" + entry.getKey() + "\\b", entry.getValue());
        }
        
        return result;
    }
    
    /**
     * Get word mapping for translation
     */
    private Map<String, String> getWordMapping(LanguageTranslation translation) {
        Map<String, String> mapping = new HashMap<>();
        
        mapping.put("welcome", translation.getWelcome());
        mapping.put("help", translation.getHelp());
        mapping.put("hello", translation.getHello());
        mapping.put("thank you", translation.getThankYou());
        mapping.put("please", translation.getPlease());
        mapping.put("yes", translation.getYes());
        mapping.put("no", translation.getNo());
        mapping.put("education", translation.getEducation());
        mapping.put("location", translation.getLocation());
        mapping.put("profile", translation.getProfile());
        mapping.put("settings", translation.getSettings());
        mapping.put("search", translation.getSearch());
        mapping.put("save", translation.getSave());
        mapping.put("cancel", translation.getCancel());
        
        return mapping;
    }
    
    /**
     * Convert text to speech in specified language
     */
    public CompletableFuture<byte[]> textToSpeech(String text, User.Language language, String voiceGender) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Translate text if needed
                String translatedText = translateText(text, User.Language.ENGLISH, language);
                
                // Set up synthesis input
                SynthesisInput input = SynthesisInput.newBuilder()
                    .setText(translatedText)
                    .build();
                
                // Configure voice parameters
                VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode(getLanguageCode(language))
                    .setSsmlGender(getVoiceGender(voiceGender))
                    .build();
                
                // Configure audio output
                AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .setSpeakingRate(0.8)
                    .setPitch(0.0)
                    .build();
                
                // Perform synthesis
                SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(
                    input, voice, audioConfig
                );
                
                return response.getAudioContent().toByteArray();
                
            } catch (Exception e) {
                throw new RuntimeException("Failed to synthesize speech: " + e.getMessage());
            }
        });
    }
    
    /**
     * Get Google Cloud language code
     */
    private String getLanguageCode(User.Language language) {
        switch (language) {
            case ENGLISH: return "en-US";
            case BEMBA: return "en-US"; // Fallback to English for Zambian languages
            case NYANJA: return "en-US";
            case TONGA: return "en-US";
            case LOZI: return "en-US";
            default: return "en-US";
        }
    }
    
    /**
     * Get voice gender for synthesis
     */
    private SsmlVoiceGender getVoiceGender(String gender) {
        switch (gender.toLowerCase()) {
            case "male": return SsmlVoiceGender.MALE;
            case "female": return SsmlVoiceGender.FEMALE;
            default: return SsmlVoiceGender.NEUTRAL;
        }
    }
    
    /**
     * Get available voices for language
     */
    public CompletableFuture<Map<String, String>> getAvailableVoices(User.Language language) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ListVoicesRequest request = ListVoicesRequest.newBuilder()
                    .setLanguageCode(getLanguageCode(language))
                    .build();
                
                ListVoicesResponse response = textToSpeechClient.listVoices(request);
                Map<String, String> voices = new HashMap<>();
                
                for (Voice voice : response.getVoicesList()) {
                    voices.put(voice.getName(), voice.getName() + " (" + voice.getSsmlGender() + ")");
                }
                
                return voices;
                
            } catch (Exception e) {
                throw new RuntimeException("Failed to get available voices: " + e.getMessage());
            }
        });
    }
    
    /**
     * Language Translation class
     */
    private static class LanguageTranslation {
        private final String welcome, welcomeMessage, goodMorning, goodAfternoon, goodEvening;
        private final String learnWithVoice, education, location, profile, settings;
        private final String hello, thankYou, please, yes, no, help, exit, search, save, cancel;
        
        public LanguageTranslation(String welcome, String welcomeMessage, String goodMorning, 
                                String goodAfternoon, String goodEvening, String learnWithVoice,
                                String education, String location, String profile, String settings,
                                String hello, String thankYou, String please, String yes, String no,
                                String help, String exit, String search, String save, String cancel) {
            this.welcome = welcome;
            this.welcomeMessage = welcomeMessage;
            this.goodMorning = goodMorning;
            this.goodAfternoon = goodAfternoon;
            this.goodEvening = goodEvening;
            this.learnWithVoice = learnWithVoice;
            this.education = education;
            this.location = location;
            this.profile = profile;
            this.settings = settings;
            this.hello = hello;
            this.thankYou = thankYou;
            this.please = please;
            this.yes = yes;
            this.no = no;
            this.help = help;
            this.exit = exit;
            this.search = search;
            this.save = save;
            this.cancel = cancel;
        }
        
        // Getters
        public String getWelcome() { return welcome; }
        public String getWelcomeMessage() { return welcomeMessage; }
        public String getGoodMorning() { return goodMorning; }
        public String getGoodAfternoon() { return goodAfternoon; }
        public String getGoodEvening() { return goodEvening; }
        public String getLearnWithVoice() { return learnWithVoice; }
        public String getEducation() { return education; }
        public String getLocation() { return location; }
        public String getProfile() { return profile; }
        public String getSettings() { return settings; }
        public String getHello() { return hello; }
        public String getThankYou() { return thankYou; }
        public String getPlease() { return please; }
        public String getYes() { return yes; }
        public String getNo() { return no; }
        public String getHelp() { return help; }
        public String getExit() { return exit; }
        public String getSearch() { return search; }
        public String getSave() { return save; }
        public String getCancel() { return cancel; }
    }
}
