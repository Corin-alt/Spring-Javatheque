// Login form handler
document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');
    
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            
            try {
                const response = await fetch('/api/auth/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ email, password }),
                    credentials: 'include'
                });
                
                console.log('Login response status:', response.status);
                
                if (response.ok) {
                    const data = await response.json();
                    console.log('Login successful:', data);
                    console.log('Redirecting to /library.html...');
                    // Redirection immédiate
                    window.location.href = '/library.html';
                    return; // Stopper l'exécution
                } else {
                    console.error('Login failed with status:', response.status);
                    showError('error-message', 'Email ou mot de passe incorrect');
                }
            } catch (error) {
                console.error('Login error:', error);
                showError('error-message', 'Erreur de connexion au serveur');
            }
        });
    }
    
    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const lastname = document.getElementById('lastname').value;
            const firstname = document.getElementById('firstname').value;
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            
            try {
                const response = await fetch('/api/auth/register', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ lastname, firstname, email, password }),
                    credentials: 'include'
                });
                
                console.log('Register response status:', response.status);
                
                if (response.ok) {
                    const data = await response.json();
                    console.log('Registration successful:', data);
                    console.log('Redirecting to /library.html...');
                    // Redirection immédiate
                    window.location.href = '/library.html';
                    return; // Stopper l'exécution
                } else if (response.status === 400) {
                    console.error('Email already in use');
                    showError('error-message', 'Cet email est déjà utilisé');
                } else {
                    console.error('Registration failed with status:', response.status);
                    showError('error-message', 'Erreur lors de l\'inscription');
                }
            } catch (error) {
                console.error('Registration error:', error);
                showError('error-message', 'Erreur de connexion au serveur');
            }
        });
    }
});

