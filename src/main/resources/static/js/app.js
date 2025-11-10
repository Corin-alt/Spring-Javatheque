// API Base URL
const API_URL = '/api';

// Utility functions
const showError = (elementId, message) => {
    const element = document.getElementById(elementId);
    element.textContent = message;
    element.style.display = 'block';
    setTimeout(() => {
        element.style.display = 'none';
    }, 5000);
};

// Custom confirmation dialog
const showConfirm = (options) => {
    return new Promise((resolve) => {
        const modal = document.createElement('div');
        modal.className = 'confirm-modal';
        modal.innerHTML = `
            <div class="confirm-content">
                <div class="confirm-icon">${options.icon || '⚠️'}</div>
                <h2 class="confirm-title">${options.title || 'Confirmation'}</h2>
                <p class="confirm-message">${options.message || 'Êtes-vous sûr ?'}</p>
                <div class="confirm-buttons">
                    <button class="btn btn-secondary" id="confirm-cancel">
                        ${options.cancelText || 'Annuler'}
                    </button>
                    <button class="btn ${options.confirmClass || 'btn-primary'}" id="confirm-ok">
                        ${options.confirmText || 'Confirmer'}
                    </button>
                </div>
            </div>
        `;
        
        document.body.appendChild(modal);
        
        const handleConfirm = () => {
            document.body.removeChild(modal);
            resolve(true);
        };
        
        const handleCancel = () => {
            document.body.removeChild(modal);
            resolve(false);
        };
        
        document.getElementById('confirm-ok').addEventListener('click', handleConfirm);
        document.getElementById('confirm-cancel').addEventListener('click', handleCancel);
        
        // Close on background click
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                handleCancel();
            }
        });
        
        // Close on Escape key
        const handleEscape = (e) => {
            if (e.key === 'Escape') {
                handleCancel();
                document.removeEventListener('keydown', handleEscape);
            }
        };
        document.addEventListener('keydown', handleEscape);
    });
};

const showLoading = (show = true) => {
    const loading = document.getElementById('loading');
    if (loading) {
        loading.style.display = show ? 'block' : 'none';
    }
};

// API calls with error handling
const apiCall = async (url, options = {}) => {
    try {
        const response = await fetch(`${API_URL}${url}`, {
            ...options,
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            credentials: 'include' // Important for session cookies
        });
        
        if (!response.ok) {
            if (response.status === 401) {
                // Redirect to login if unauthorized
                window.location.href = '/login.html';
                return null;
            }
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return await response.json();
        }
        return null;
    } catch (error) {
        console.error('API call error:', error);
        throw error;
    }
};

// Check if user is logged in
const checkAuth = async () => {
    try {
        const user = await apiCall('/auth/me');
        return user;
    } catch (error) {
        return null;
    }
};

// Format TMDB poster URL
const getTMDBPosterUrl = (posterPath, size = 'w500') => {
    if (!posterPath) return '/images/no-poster.jpg';
    return `https://image.tmdb.org/t/p/${size}${posterPath}`;
};

// Initialize logout button
document.addEventListener('DOMContentLoaded', () => {
    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', async () => {
            const confirmed = await showConfirm({
                icon: '👋',
                title: 'Se déconnecter ?',
                message: 'Êtes-vous sûr de vouloir vous déconnecter ?',
                confirmText: '👋 Déconnexion',
                cancelText: 'Rester connecté',
                confirmClass: 'btn-delete'
            });
            
            if (!confirmed) return;
            
            try {
                await apiCall('/auth/logout', { method: 'POST' });
                window.location.href = '/';
            } catch (error) {
                console.error('Logout error:', error);
            }
        });
    }
});

