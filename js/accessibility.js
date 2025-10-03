// Initialize Firebase
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
    apiKey: "AIzaSyC3neyTmC4n7Hr3Rtd612-815ATUw01dF4",
    authDomain: "zamedu-voice.firebaseapp.com",
    projectId: "zamedu-voice",
    storageBucket: "zamedu-voice.firebasestorage.app",
    messagingSenderId: "646719016231",
    appId: "1:646719016231:web:2cfd0edf2709dd45942d02",
    measurementId: "G-4GLKJP3Z5C"
  };

// Initialize Firebase
if (!firebase.apps.length) {
    firebase.initializeApp(firebaseConfig);
}

const db = firebase.firestore();
const auth = firebase.auth();

class AccessibilityManager {
    constructor() {
        this.currentUser = null;
        this.isScanning = false;
        this.userPreferences = {
            visualImpairment: false,
            hearingImpairment: false,
            mobilityImpairment: false,
            learningDisability: false,
            interfaceLanguage: 'en',
            voiceLanguage: 'en-ZA',
            fingerprintRegistered: false
        };

        this.initEventListeners();
        this.checkAuthState();
    }

    initEventListeners() {
        // Save settings button
        document.getElementById('saveSettings')?.addEventListener('click', () => this.saveSettings());
        
        // Fingerprint scanner
        const fingerprintScanner = document.getElementById('fingerprintScanner');
        if (fingerprintScanner) {
            fingerprintScanner.addEventListener('click', () => this.toggleFingerprintScan());
        }
        
        // Language change handlers
        document.getElementById('interfaceLanguage')?.addEventListener('change', (e) => {
            this.userPreferences.interfaceLanguage = e.target.value;
        });
        
        document.getElementById('voiceLanguage')?.addEventListener('change', (e) => {
            this.userPreferences.voiceLanguage = e.target.value;
        });
        
        // Checkbox handlers
        const checkboxes = ['visual-impairment', 'hearing-impairment', 'mobility-impairment', 'learning-disability'];
        checkboxes.forEach(id => {
            const element = document.getElementById(id);
            if (element) {
                element.addEventListener('change', (e) => this.handleCheckboxChange(id, e.target.checked));
            }
        });
    }

    async checkAuthState() {
        auth.onAuthStateChanged(async (user) => {
            if (user) {
                this.currentUser = user;
                await this.loadUserPreferences();
            } else {
                // Redirect to login if not authenticated
                window.location.href = 'login.html';
            }
        });
    }

    async loadUserPreferences() {
        try {
            const userDoc = await db.collection('userPreferences').doc(this.currentUser.uid).get();
            if (userDoc.exists) {
                this.userPreferences = { ...this.userPreferences, ...userDoc.data() };
                this.updateUI();
            }
        } catch (error) {
            console.error('Error loading user preferences:', error);
        }
    }

    async saveSettings() {
        if (!this.currentUser) return;

        try {
            await db.collection('userPreferences').doc(this.currentUser.uid).set({
                ...this.userPreferences,
                lastUpdated: firebase.firestore.FieldValue.serverTimestamp()
            }, { merge: true });
            
            // Update UI to show success
            this.showNotification('Settings saved successfully!', 'success');
            
            // If this is the first time setting up, redirect to dashboard
            if (!this.userPreferences.onboardingComplete) {
                this.userPreferences.onboardingComplete = true;
                window.location.href = 'index.html';
            }
        } catch (error) {
            console.error('Error saving settings:', error);
            this.showNotification('Failed to save settings. Please try again.', 'error');
        }
    }

    toggleFingerprintScan() {
        if (this.isScanning) {
            this.stopFingerprintScan();
        } else {
            this.startFingerprintScan();
        }
    }

    startFingerprintScan() {
        if (!('credentials' in navigator)) {
            this.showNotification('Fingerprint authentication is not supported on this device.', 'error');
            return;
        }

        this.isScanning = true;
        const scanner = document.getElementById('fingerprintScanner');
        const status = document.getElementById('scanStatus');
        
        scanner.classList.add('scanning');
        status.textContent = 'Scanning your fingerprint...';
        
        // Simulate fingerprint scanning (in a real app, you would use the Web Authentication API)
        setTimeout(() => {
            if (this.isScanning) {
                this.completeFingerprintScan();
            }
        }, 2000);
    }

    stopFingerprintScan() {
        this.isScanning = false;
        const scanner = document.getElementById('fingerprintScanner');
        const status = document.getElementById('scanStatus');
        
        scanner?.classList.remove('scanning');
        status.textContent = 'Fingerprint scan cancelled';
    }

    async completeFingerprintScan() {
        if (!this.isScanning) return;
        
        this.isScanning = false;
        const scanner = document.getElementById('fingerprintScanner');
        const status = document.getElementById('scanStatus');
        
        scanner?.classList.remove('scanning');
        
        try {
            // In a real app, you would use the Web Authentication API here
            // For demo purposes, we'll simulate a successful scan
            this.userPreferences.fingerprintRegistered = true;
            status.textContent = 'Fingerprint registered successfully!';
            this.showNotification('Fingerprint registered successfully!', 'success');
            
            // Save the preference
            await this.saveSettings();
            
        } catch (error) {
            console.error('Fingerprint registration failed:', error);
            status.textContent = 'Fingerprint registration failed. Please try again.';
            this.showNotification('Fingerprint registration failed. Please try again.', 'error');
        }
    }

    handleCheckboxChange(id, isChecked) {
        switch(id) {
            case 'visual-impairment':
                this.userPreferences.visualImpairment = isChecked;
                break;
            case 'hearing-impairment':
                this.userPreferences.hearingImpairment = isChecked;
                break;
            case 'mobility-impairment':
                this.userPreferences.mobilityImpairment = isChecked;
                break;
            case 'learning-disability':
                this.userPreferences.learningDisability = isChecked;
                break;
        }
    }

    updateUI() {
        // Update checkboxes
        document.getElementById('visual-impairment').checked = this.userPreferences.visualImpairment;
        document.getElementById('hearing-impairment').checked = this.userPreferences.hearingImpairment;
        document.getElementById('mobility-impairment').checked = this.userPreferences.mobilityImpairment;
        document.getElementById('learning-disability').checked = this.userPreferences.learningDisability;
        
        // Update language selectors
        const interfaceLang = document.getElementById('interfaceLanguage');
        const voiceLang = document.getElementById('voiceLanguage');
        
        if (interfaceLang) interfaceLang.value = this.userPreferences.interfaceLanguage;
        if (voiceLang) voiceLang.value = this.userPreferences.voiceLanguage;
        
        // Update fingerprint status
        if (this.userPreferences.fingerprintRegistered) {
            const status = document.getElementById('scanStatus');
            if (status) status.textContent = 'Fingerprint registered';
        }
    }

    showNotification(message, type = 'info') {
        // In a real app, you would use a proper notification system
        alert(`${type.toUpperCase()}: ${message}`);
    }
}

// Initialize the accessibility manager when the DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    window.accessibilityManager = new AccessibilityManager();
});
