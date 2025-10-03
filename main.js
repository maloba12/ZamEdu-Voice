// ZamEdu Voice - Main JavaScript File
// Voice-enabled educational assistant with AI integration and accessibility features

// Import Firebase (make sure to include this in your HTML)
// <script src="https://www.gstatic.com/firebasejs/9.6.1/firebase-app-compat.js"></script>
// <script src="https://www.gstatic.com/firebasejs/9.6.1/firebase-auth-compat.js"></script>
// <script src="https://www.gstatic.com/firebasejs/9.6.1/firebase-firestore-compat.js"></script>

// Language configuration
const APP_STRINGS = {
    en: {
        welcome: 'Welcome back, {name}! How can I assist you today?',
        listening: 'Listening...',
        processing: 'Processing your request...',
        error: 'Sorry, I encountered an error. Please try again.'
    },
    bem: {
        welcome: 'Mwaiseni, {name}! Ndingakusakileni lelo?',
        listening: 'Nalekutanga...',
        processing: 'Nalekupanga...',
        error: 'Pawalifumineko inyangu. Ndapota lekeni kausa nakabili.'
    },
    // Add more languages as needed
};

class ZamEduVoice {
    constructor() {
        this.isListening = false;
        this.recognition = null;
        this.synthesis = null;
        this.currentLanguage = localStorage.getItem('userLanguage') || 'en';
        this.userName = 'Friend';
        this.userPreferences = {
            visualImpairment: false,
            hearingImpairment: false,
            highContrast: false,
            textSize: 'medium',
            voiceSpeed: 1.0
        };
        
        // Initialize Firebase
        this.initFirebase();
        this.checkAuthState();
    }
    
    async initFirebase() {
        try {
            // Initialize Firebase if not already initialized
            if (!firebase.apps.length) {
                const firebaseConfig = {
                    apiKey: "YOUR_API_KEY",
                    authDomain: "zamedu-voice.firebaseapp.com",
                    projectId: "zamedu-voice",
                    storageBucket: "zamedu-voice.appspot.com",
                    messagingSenderId: "YOUR_MESSAGING_SENDER_ID",
                    appId: "YOUR_APP_ID"
                };
                firebase.initializeApp(firebaseConfig);
            }
            
            this.auth = firebase.auth();
            this.db = firebase.firestore();
        } catch (error) {
            console.error('Firebase initialization error:', error);
        }
    }
    
    async checkAuthState() {
        this.auth?.onAuthStateChanged(async (user) => {
            if (user) {
                this.currentUser = user;
                await this.loadUserPreferences();
                this.init();
            } else {
                // Redirect to login if not authenticated
                window.location.href = 'login.html';
            }
        });
    }
    
    async loadUserPreferences() {
        if (!this.currentUser) return;
        
        try {
            const userDoc = await this.db.collection('users').doc(this.currentUser.uid).get();
            if (userDoc.exists) {
                const userData = userDoc.data();
                this.userName = userData.displayName || this.currentUser.displayName || this.currentUser.email.split('@')[0];
                
                // Load user preferences
                const prefsDoc = await this.db.collection('userPreferences').doc(this.currentUser.uid).get();
                if (prefsDoc.exists) {
                    const prefs = prefsDoc.data();
                    this.userPreferences = { ...this.userPreferences, ...prefs };
                    this.currentLanguage = prefs.interfaceLanguage || 'en';
                    
                    // Apply accessibility settings
                    this.applyAccessibilitySettings();
                }
            }
        } catch (error) {
            console.error('Error loading user data:', error);
        }
    }
    
    applyAccessibilitySettings() {
        const { visualImpairment, highContrast, textSize } = this.userPreferences;
        
        // Apply high contrast mode
        document.documentElement.classList.toggle('high-contrast', highContrast);
        
        // Apply text size
        const sizes = { small: '14px', medium: '16px', large: '18px', xlarge: '20px' };
        document.documentElement.style.setProperty('--base-font-size', sizes[textSize] || '16px');
        
        // If visual impairment is detected, enable screen reader mode
        if (visualImpairment) {
            this.enableScreenReaderMode();
        }
    }
    
    enableScreenReaderMode() {
        // Add ARIA attributes and other screen reader optimizations
        document.body.setAttribute('aria-live', 'polite');
        document.body.setAttribute('role', 'document');
        
        // Add skip to content link
        const skipLink = document.createElement('a');
        skipLink.href = '#main-content';
        skipLink.className = 'skip-link';
        skipLink.textContent = 'Skip to main content';
        document.body.insertBefore(skipLink, document.body.firstChild);
    }
    
    init() {
        this.setupSpeechRecognition();
        this.setupTextToSpeech();
        this.setupAnimations();
        this.setupEventListeners();
        this.updateTime();
        this.initializeParticles();
        this.greetUser();
    }
    
    getLanguageCode() {
        // Map language codes to speech recognition language codes
        const languageMap = {
            'en': 'en-US',
            'bem': 'bem-ZM',
            'ny': 'ny-MW',
            'toi': 'toi-ZM',
            'loz': 'loz-ZM'
        };
        return languageMap[this.currentLanguage] || 'en-US';
    }
    
    setupSpeechRecognition() {
        if ('webkitSpeechRecognition' in window || 'SpeechRecognition' in window) {
            const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
            this.recognition = new SpeechRecognition();
            
            this.recognition.continuous = false;
            this.recognition.interimResults = true;
            this.recognition.lang = this.getLanguageCode();
            
            this.recognition.onstart = () => {
                this.isListening = true;
                this.updateVoiceStatus(this.getLocalizedString('listening'));
                this.animateVoiceButton(true);
            };
            
            this.recognition.onresult = (event) => {
                let transcript = '';
                for (let i = event.resultIndex; i < event.results.length; i++) {
                    transcript += event.results[i][0].transcript;
                }
                this.displaySpeechOutput(transcript);
                
                if (event.results[event.results.length - 1].isFinal) {
                    this.processCommand(transcript);
                }
            };
            
            this.recognition.onerror = (event) => {
                console.error('Speech recognition error:', event.error);
                this.updateVoiceStatus('Voice recognition error. Please try again.');
                this.stopListening();
            };
            
            this.recognition.onend = () => {
                this.stopListening();
            };
        } else {
            console.warn('Speech recognition not supported');
            this.updateVoiceStatus('Voice recognition not supported in this browser');
        }
    }
    
    setupTextToSpeech() {
        this.synthesis = window.speechSynthesis;
        
        // Get available voices
        this.loadVoices();
        if (speechSynthesis.onvoiceschanged !== undefined) {
            speechSynthesis.onvoiceschanged = () => this.loadVoices();
        }
    }
    
    loadVoices() {
        this.voices = this.synthesis.getVoices();
    }
    
    getLanguageCode() {
        const languageMap = {
            'en': 'en-US',
            'bem': 'en-US', // Fallback for Bemba
            'nya': 'en-US', // Fallback for Nyanja
            'toi': 'en-US', // Fallback for Tonga
            'loz': 'en-US'  // Fallback for Lozi
        };
        return languageMap[this.currentLanguage] || 'en-US';
    }
    
    setupAnimations() {
        // Typewriter effect for hero title
        new Typed('#typed-title', {
            strings: [
                'Learn with Voice',
                'Landa na Moyo',
                'Dzidza neMimhanzi',
                'Ilamba neMwindo'
            ],
            typeSpeed: 80,
            backSpeed: 50,
            backDelay: 2000,
            loop: true,
            showCursor: true,
            cursorChar: '|'
        });
        
        // Animate feature cards on scroll
        this.setupScrollAnimations();
    }
    
    setupScrollAnimations() {
        const observerOptions = {
            threshold: 0.1,
            rootMargin: '0px 0px -50px 0px'
        };
        
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    anime({
                        targets: entry.target,
                        translateY: [50, 0],
                        opacity: [0, 1],
                        duration: 800,
                        easing: 'easeOutQuart',
                        delay: anime.stagger(100)
                    });
                }
            });
        }, observerOptions);
        
        // Observe feature cards and AI cards
        document.querySelectorAll('.feature-card, .ai-card').forEach(card => {
            observer.observe(card);
        });
    }
    
    setupEventListeners() {
        // Voice button
        const voiceBtn = document.getElementById('voice-btn');
        if (voiceBtn) {
            voiceBtn.addEventListener('click', () => this.toggleListening());
        }
        
        // Language selector
        const languageSelector = document.getElementById('language-selector');
        if (languageSelector) {
            languageSelector.addEventListener('change', (e) => {
                this.currentLanguage = e.target.value;
                if (this.recognition) {
                    this.recognition.lang = this.getLanguageCode();
                }
                this.greetUser();
            });
        }
        
        // AI card buttons
        document.querySelectorAll('.ai-card button').forEach(button => {
            button.addEventListener('click', (e) => {
                const aiName = e.target.closest('.ai-card').querySelector('h3').textContent;
                this.speak(`Opening ${aiName} for you`);
                // Simulate AI opening
                this.showNotification(`Opening ${aiName}...`, 'info');
                setTimeout(() => {
                    window.location.href = 'education.html';
                }, 1500);
            });
        });
        
        // Quick action buttons
        window.handleQuickAction = (action) => {
            this.handleQuickAction(action);
        };
    }
    
    toggleListening() {
        if (!this.recognition) {
            this.showNotification('Voice recognition not supported', 'error');
            return;
        }
        
        if (this.isListening) {
            this.stopListening();
        } else {
            this.startListening();
        }
    }
    
    startListening() {
        try {
            this.recognition.start();
        } catch (error) {
            console.error('Error starting recognition:', error);
            this.updateVoiceStatus('Could not start voice recognition');
        }
    }
    
    stopListening() {
        if (this.recognition && this.isListening) {
            this.recognition.stop();
            this.isListening = false;
            this.updateVoiceStatus('Tap to start speaking');
            this.animateVoiceButton(false);
        }
    }
    
    updateVoiceStatus(message) {
        const statusElement = document.getElementById('voice-status');
        if (statusElement) {
            statusElement.textContent = message;
        }
    }
    
    displaySpeechOutput(text) {
        const outputElement = document.getElementById('speech-output');
        if (outputElement) {
            outputElement.textContent = text;
            outputElement.scrollTop = outputElement.scrollHeight;
        }
    }
    
    animateVoiceButton(listening) {
        const voiceBtn = document.getElementById('voice-btn');
        if (voiceBtn) {
            if (listening) {
                voiceBtn.classList.add('listening');
            } else {
                voiceBtn.classList.remove('listening');
            }
        }
    }
    
    processCommand(transcript) {
        const command = transcript.toLowerCase().trim();
        
        // Educational commands
        if (command.includes('study') || command.includes('education') || command.includes('learn')) {
            this.speak('Opening educational tools for you');
            setTimeout(() => {
                window.location.href = 'education.html';
            }, 1500);
        }
        // Location commands
        else if (command.includes('find') || command.includes('location') || command.includes('near')) {
            this.speak('Opening location services');
            setTimeout(() => {
                window.location.href = 'location.html';
            }, 1500);
        }
        // Profile commands
        else if (command.includes('profile') || command.includes('settings') || command.includes('account')) {
            this.speak('Opening your profile');
            setTimeout(() => {
                window.location.href = 'profile.html';
            }, 1500);
        }
        // Greeting commands
        else if (command.includes('hello') || command.includes('hi') || command.includes('greeting')) {
            this.greetUser();
        }
        // Help commands
        else if (command.includes('help') || command.includes('assist')) {
            this.provideHelp();
        }
        // Default response
        else {
            this.speak("I heard you say: " + transcript + ". I can help you with education, finding places, or managing your profile.");
        }
    }
    
    handleQuickAction(action) {
        switch (action) {
            case 'education':
                this.speak('Opening educational tools');
                setTimeout(() => {
                    window.location.href = 'education.html';
                }, 1000);
                break;
            case 'location':
                this.speak('Opening location services');
                setTimeout(() => {
                    window.location.href = 'location.html';
                }, 1000);
                break;
            default:
                this.speak('Feature coming soon');
        }
    }
    
    speak(text) {
        if (!this.synthesis) return;
        
        // Cancel any ongoing speech
        this.synthesis.cancel();
        
        const utterance = new SpeechSynthesisUtterance(text);
        
        // Set voice based on user preferences
        const voice = this.getVoice();
        if (voice) {
            utterance.voice = voice;
            utterance.lang = voice.lang;
        } else {
            // Fallback to default voice for the current language
            utterance.lang = this.getLanguageCode();
        }
        
        // Apply user preferences
        const { voiceSpeed = 1.0 } = this.userPreferences;
        utterance.rate = Math.min(Math.max(0.5, voiceSpeed), 2); // Limit between 0.5 and 2
        
        // Handle visual feedback for users with hearing impairments
        if (this.userPreferences.hearingImpairment) {
            this.showVisualFeedback(text);
                voice.lang.startsWith('en')
            );
            if (preferredVoice) {
                utterance.voice = preferredVoice;
            }
        }
        
        this.synthesis.speak(utterance);
    }
    
    greetUser() {
        const now = new Date();
        const hour = now.getHours();
        let greeting;
        
        if (hour < 12) {
            greeting = 'Good morning';
        } else if (hour < 18) {
            greeting = 'Good afternoon';
        } else {
            greeting = 'Good evening';
        }
        
        // Add Zambian language greetings
        const zambianGreetings = {
            'bem': 'Mwabonwa',
            'nya': 'Moni',
            'toi': 'Mwapona',
            'loz': 'Mwapona'
        };
        
        if (this.currentLanguage !== 'en' && zambianGreetings[this.currentLanguage]) {
            greeting += `, ${zambianGreetings[this.currentLanguage]}`;
        }
        
        greeting += ` ${this.userName}! Welcome to ZamEdu Voice.`;
        
        // Only speak if not already speaking
        if (!this.synthesis.speaking) {
            setTimeout(() => {
                this.speak(greeting);
            }, 2000);
        }
    }
    
    provideHelp() {
        const helpText = `I can help you with several things. Say "study" or "education" to access learning tools. 
                         Say "find" or "location" to discover nearby places. Say "profile" to manage your settings. 
                         You can also switch languages using the dropdown menu.`;
        this.speak(helpText);
    }
    
    updateTime() {
        const updateClock = () => {
            const now = new Date();
            const timeString = now.toLocaleTimeString('en-US', {
                hour: '2-digit',
                minute: '2-digit',
                hour12: true
            });
            
            const timeElement = document.getElementById('current-time');
            if (timeElement) {
                timeElement.textContent = timeString;
            }
        };
        
        updateClock();
        setInterval(updateClock, 1000);
    }
    
    initializeParticles() {
        // Simple particle system using p5.js
        const sketch = (p) => {
            let particles = [];
            
            p.setup = () => {
                const canvas = p.createCanvas(p.windowWidth, p.windowHeight);
                canvas.parent('particles-container');
                
                // Create particles
                for (let i = 0; i < 50; i++) {
                    particles.push({
                        x: p.random(p.width),
                        y: p.random(p.height),
                        vx: p.random(-0.5, 0.5),
                        vy: p.random(-0.5, 0.5),
                        size: p.random(2, 6),
                        opacity: p.random(0.1, 0.3)
                    });
                }
            };
            
            p.draw = () => {
                p.clear();
                
                // Update and draw particles
                particles.forEach(particle => {
                    particle.x += particle.vx;
                    particle.y += particle.vy;
                    
                    // Wrap around edges
                    if (particle.x < 0) particle.x = p.width;
                    if (particle.x > p.width) particle.x = 0;
                    if (particle.y < 0) particle.y = p.height;
                    if (particle.y > p.height) particle.y = 0;
                    
                    // Draw particle
                    p.fill(184, 115, 51, particle.opacity * 255);
                    p.noStroke();
                    p.ellipse(particle.x, particle.y, particle.size);
                });
            };
            
            p.windowResized = () => {
                p.resizeCanvas(p.windowWidth, p.windowHeight);
            };
        };
        
        new p5(sketch);
    }
    
    showNotification(message, type = 'info') {
        // Create notification element
        const notification = document.createElement('div');
        notification.className = `fixed top-20 right-4 z-50 p-4 rounded-lg shadow-lg max-w-sm transform translate-x-full transition-transform duration-300`;
        
        // Set colors based on type
        const colors = {
            info: 'bg-blue-500 text-white',
            success: 'bg-green-500 text-white',
            warning: 'bg-yellow-500 text-black',
            error: 'bg-red-500 text-white'
        };
        
        notification.className += ` ${colors[type] || colors.info}`;
        notification.textContent = message;
        
        document.body.appendChild(notification);
        
        // Animate in
        setTimeout(() => {
            notification.style.transform = 'translateX(0)';
        }, 100);
        
        // Animate out and remove
        setTimeout(() => {
            notification.style.transform = 'translateX(100%)';
            setTimeout(() => {
                document.body.removeChild(notification);
            }, 300);
        }, 3000);
    }
}

// Utility functions for local storage and user management
class UserManager {
    static setUserName(name) {
        localStorage.setItem('userName', name);
    }
    
    static getUserName() {
        return localStorage.getItem('userName') || 'Friend';
    }
    
    static setLanguage(language) {
        localStorage.setItem('preferredLanguage', language);
    }
    
    static getLanguage() {
        return localStorage.getItem('preferredLanguage') || 'en';
    }
    
    static saveProgress(data) {
        const progress = this.getProgress();
        progress.push({
            ...data,
            timestamp: new Date().toISOString()
        });
        localStorage.setItem('userProgress', JSON.stringify(progress));
    }
    
    static getProgress() {
        const progress = localStorage.getItem('userProgress');
        return progress ? JSON.parse(progress) : [];
    }
}

// AI Service Manager
class AIServiceManager {
    constructor() {
        this.services = {
            gemini: { name: 'Gemini', status: 'online', specialty: 'Mathematics' },
            chatgpt: { name: 'ChatGPT', status: 'online', specialty: 'Literature' },
            kimi: { name: 'Kimi', status: 'online', specialty: 'Local Knowledge' },
            grok: { name: 'Grok', status: 'online', specialty: 'Science' },
            claude: { name: 'Claude', status: 'online', specialty: 'Writing' },
            perplexity: { name: 'Perplexity', status: 'online', specialty: 'Research' }
        };
    }
    
    getServiceStatus(serviceName) {
        return this.services[serviceName.toLowerCase()] || null;
    }
    
    getAllServices() {
        return this.services;
    }
    
    simulateQuery(query, serviceName) {
        // Simulate AI response (in real implementation, this would call actual AI APIs)
        const responses = {
            gemini: "I'll help you solve this mathematical problem step by step...",
            chatgpt: "Here's a comprehensive explanation of that topic...",
            kimi: "Based on Zambian context and local knowledge...",
            grok: "From a scientific perspective, here's what you need to know...",
            claude: "Let me help you structure your thoughts and writing...",
            perplexity: "Here are the most relevant and up-to-date sources..."
        };
        
        return new Promise((resolve) => {
            setTimeout(() => {
                resolve({
                    service: serviceName,
                    response: responses[serviceName.toLowerCase()] || "I'm here to help you learn!",
                    confidence: Math.random() * 0.3 + 0.7, // 0.7 to 1.0
                    timestamp: new Date().toISOString()
                });
            }, 1000 + Math.random() * 2000); // 1-3 second delay
        });
    }
}

// Location Service Manager
class LocationServiceManager {
    constructor() {
        this.userLocation = null;
        this.nearbyPlaces = [];
    }
    
    async getCurrentLocation() {
        return new Promise((resolve, reject) => {
            if (!navigator.geolocation) {
                reject(new Error('Geolocation not supported'));
                return;
            }
            
            navigator.geolocation.getCurrentPosition(
                (position) => {
                    this.userLocation = {
                        lat: position.coords.latitude,
                        lng: position.coords.longitude,
                        accuracy: position.coords.accuracy
                    };
                    resolve(this.userLocation);
                },
                (error) => {
                    reject(error);
                },
                {
                    enableHighAccuracy: true,
                    timeout: 10000,
                    maximumAge: 300000
                }
            );
        });
    }
    
    async findNearbyPlaces(category, location = null) {
        const currentLocation = location || await this.getCurrentLocation();
        
        // Simulate finding nearby places (in real implementation, this would use Google Places API or similar)
        const mockPlaces = {
            restaurant: [
                { name: 'Mama Africa Restaurant', rating: 4.5, distance: '0.5 km', type: 'Zambian Cuisine' },
                { name: 'The Retreat Restaurant', rating: 4.2, distance: '0.8 km', type: 'International' },
                { name: 'Chit Chat Café', rating: 4.0, distance: '1.2 km', type: 'Café' }
            ],
            school: [
                { name: 'Lusaka International School', rating: 4.7, distance: '1.5 km', type: 'International School' },
                { name: 'Kabulonga Boys Secondary', rating: 4.3, distance: '2.1 km', type: 'Secondary School' },
                { name: 'Rhodes Park School', rating: 4.1, distance: '2.8 km', type: 'Primary School' }
            ],
            hospital: [
                { name: 'University Teaching Hospital', rating: 4.2, distance: '3.2 km', type: 'General Hospital' },
                { name: 'Coptic Hospital', rating: 4.0, distance: '1.8 km', type: 'Private Hospital' },
                { name: 'Lusaka Trust Hospital', rating: 3.9, distance: '2.5 km', type: 'District Hospital' }
            ]
        };
        
        return mockPlaces[category] || [];
    }
}

// Initialize the application
document.addEventListener('DOMContentLoaded', () => {
    // Initialize main voice assistant
    window.zamEduVoice = new ZamEduVoice();
    
    // Initialize service managers
    window.aiServiceManager = new AIServiceManager();
    window.locationServiceManager = new LocationServiceManager();
    window.userManager = UserManager;
    
    // Set initial language from local storage
    const savedLanguage = UserManager.getLanguage();
    const languageSelector = document.getElementById('language-selector');
    if (languageSelector && savedLanguage) {
        languageSelector.value = savedLanguage;
    }
    
    console.log('ZamEdu Voice initialized successfully!');
});