// Firebase configuration (same as in accessibility.js)
const firebaseConfig = {
   // For Firebase JS SDK v7.20.0 and later, measurementId is optional
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

const auth = firebase.auth();
const db = firebase.firestore();

class AuthManager {
    constructor() {
        this.currentUser = null;
        this.initEventListeners();
        this.checkAuthState();
    }

    initEventListeners() {
        // Login form submission
        const loginForm = document.getElementById('loginForm');
        if (loginForm) {
            loginForm.addEventListener('submit', (e) => {
                e.preventDefault();
                this.handleEmailPasswordLogin();
            });
        }

        // Google Sign In
        const googleSignInBtn = document.getElementById('googleSignIn');
        if (googleSignInBtn) {
            googleSignInBtn.addEventListener('click', () => this.signInWithGoogle());
        }

        // Facebook Sign In
        const facebookSignInBtn = document.getElementById('facebookSignIn');
        if (facebookSignInBtn) {
            facebookSignInBtn.addEventListener('click', () => this.signInWithFacebook());
        }
    }

    checkAuthState() {
        auth.onAuthStateChanged((user) => {
            if (user) {
                this.currentUser = user;
                this.redirectBasedOnUserStatus();
            }
        });
    }

    async handleEmailPasswordLogin() {
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const errorElement = document.getElementById('errorMessage');

        try {
            const userCredential = await auth.signInWithEmailAndPassword(email, password);
            this.currentUser = userCredential.user;
            this.redirectBasedOnUserStatus();
        } catch (error) {
            this.showError(error.message);
        }
    }

    async signInWithGoogle() {
        const provider = new firebase.auth.GoogleAuthProvider();
        try {
            const result = await auth.signInWithPopup(provider);
            this.currentUser = result.user;
            await this.createUserProfileIfNotExists();
            this.redirectBasedOnUserStatus();
        } catch (error) {
            this.showError(error.message);
        }
    }

    async signInWithFacebook() {
        const provider = new firebase.auth.FacebookAuthProvider();
        try {
            const result = await auth.signInWithPopup(provider);
            this.currentUser = result.user;
            await this.createUserProfileIfNotExists();
            this.redirectBasedOnUserStatus();
        } catch (error) {
            this.showError(error.message);
        }
    }

    async createUserProfileIfNotExists() {
        if (!this.currentUser) return;

        const userRef = db.collection('users').doc(this.currentUser.uid);
        const doc = await userRef.get();

        if (!doc.exists) {
            await userRef.set({
                displayName: this.currentUser.displayName || this.currentUser.email.split('@')[0],
                email: this.currentUser.email,
                photoURL: this.currentUser.photoURL || '',
                createdAt: firebase.firestore.FieldValue.serverTimestamp(),
                lastLogin: firebase.firestore.FieldValue.serverTimestamp()
            });
        } else {
            await userRef.update({
                lastLogin: firebase.firestore.FieldValue.serverTimestamp()
            });
        }
    }

    async redirectBasedOnUserStatus() {
        if (!this.currentUser) return;

        // Check if user has completed onboarding
        const userPrefs = await db.collection('userPreferences').doc(this.currentUser.uid).get();
        
        if (userPrefs.exists && userPrefs.data().onboardingComplete) {
            // Redirect to main app
            window.location.href = 'index.html';
        } else {
            // Redirect to accessibility settings
            window.location.href = 'accessibility.html';
        }
    }

    showError(message) {
        const errorElement = document.getElementById('errorMessage');
        if (errorElement) {
            errorElement.textContent = message;
            errorElement.style.display = 'block';
            
            // Hide error after 5 seconds
            setTimeout(() => {
                errorElement.style.display = 'none';
            }, 5000);
        } else {
            alert(`Error: ${message}`);
        }
    }

    // Static method to check authentication status
    static async requireAuth() {
        return new Promise((resolve, reject) => {
            const unsubscribe = auth.onAuthStateChanged(user => {
                unsubscribe();
                if (user) {
                    resolve(user);
                } else {
                    window.location.href = 'login.html';
                    reject(new Error('User not authenticated'));
                }
            });
        });
    }
}

// Initialize auth manager when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    window.authManager = new AuthManager();
});

// Export for use in other modules
window.AuthManager = AuthManager;
