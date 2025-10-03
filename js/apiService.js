class ApiService {
    constructor() {
        this.baseUrl = 'http://localhost:8080/api';
        this.authToken = localStorage.getItem('authToken');
    }

    setAuthToken(token) {
        this.authToken = token;
        if (token) {
            localStorage.setItem('authToken', token);
        } else {
            localStorage.removeItem('authToken');
        }
    }

    getHeaders() {
        const headers = {
            'Content-Type': 'application/json'
        };
        
        if (this.authToken) {
            headers['Authorization'] = `Bearer ${this.authToken}`;
        }
        
        return headers;
    }

    async getUserPreferences() {
        try {
            const response = await fetch(`${this.baseUrl}/user/preferences`, {
                method: 'GET',
                headers: this.getHeaders()
            });
            
            if (!response.ok) {
                throw new Error('Failed to fetch user preferences');
            }
            
            return await response.json();
        } catch (error) {
            console.error('Error fetching user preferences:', error);
            throw error;
        }
    }

    async saveUserPreferences(preferences) {
        try {
            const response = await fetch(`${this.baseUrl}/user/preferences`, {
                method: 'POST',
                headers: this.getHeaders(),
                body: JSON.stringify(preferences)
            });
            
            if (!response.ok) {
                throw new Error('Failed to save user preferences');
            }
            
            return await response.json();
        } catch (error) {
            console.error('Error saving user preferences:', error);
            throw error;
        }
    }

    async processSpeech(text, language) {
        try {
            const response = await fetch(`${this.baseUrl}/speech/process`, {
                method: 'POST',
                headers: this.getHeaders(),
                body: JSON.stringify({ text, language })
            });
            
            if (!response.ok) {
                throw new Error('Failed to process speech');
            }
            
            return await response.json();
        } catch (error) {
            console.error('Error processing speech:', error);
            throw error;
        }
    }

    // Add more API methods as needed
}

// Create a singleton instance
const apiService = new ApiService();

export default apiService;
