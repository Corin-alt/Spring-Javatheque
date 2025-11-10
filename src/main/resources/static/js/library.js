// Library page logic
document.addEventListener('DOMContentLoaded', async () => {
    const filmsGrid = document.getElementById('films-grid');
    const emptyState = document.getElementById('empty-state');
    const searchInput = document.getElementById('search-input');
    const searchBtn = document.getElementById('search-btn');
    const userInfo = document.getElementById('user-info');
    
    // Check authentication
    const user = await checkAuth();
    if (!user) {
        window.location.href = '/login.html';
        return;
    }
    
    if (userInfo) {
        userInfo.textContent = `👤 ${user.firstname} ${user.lastname}`;
    }
    
    // Load films
    const loadFilms = async (searchQuery = '') => {
        showLoading(true);
        try {
            const url = searchQuery ? `/library/search?search=${encodeURIComponent(searchQuery)}` : '/library';
            const films = await apiCall(url);
            
            showLoading(false);
            
            if (!films || films.length === 0) {
                filmsGrid.innerHTML = '';
                filmsGrid.style.display = 'none';
                emptyState.style.display = 'block';
                return;
            }
            
            emptyState.style.display = 'none';
            filmsGrid.style.display = 'grid';
            
            filmsGrid.innerHTML = films.map(film => `
                <div class="film-card" data-film-id="${film.id}">
                    <img src="${getTMDBPosterUrl(film.poster)}" alt="${film.title}" class="film-poster" 
                         onerror="this.src='/images/no-poster.jpg'">
                    <div class="film-info">
                        <div class="film-title">${film.title}</div>
                        <div class="film-year">${film.year}</div>
                        ${film.rate ? `<div class="film-rate">⭐ ${film.rate}/10</div>` : ''}
                    </div>
                </div>
            `).join('');
            
            // Add click handlers
            document.querySelectorAll('.film-card').forEach(card => {
                card.addEventListener('click', () => {
                    const filmId = card.dataset.filmId;
                    showFilmDetail(filmId);
                });
            });
            
        } catch (error) {
            showLoading(false);
            showError('error-message', 'Erreur lors du chargement des films');
        }
    };
    
    // Show film detail in modal
    const showFilmDetail = async (filmId) => {
        try {
            const film = await apiCall(`/films/${filmId}`);
            
            const modal = document.getElementById('film-modal');
            const detail = document.getElementById('film-detail');
            
            detail.innerHTML = `
                <div style="display: flex; gap: 30px;">
                    <img src="${getTMDBPosterUrl(film.poster, 'w342')}" alt="${film.title}" 
                         style="width: 300px; border-radius: 12px;" onerror="this.src='/images/no-poster.jpg'">
                    <div>
                        <h2>${film.title}</h2>
                        <p><strong>Année:</strong> ${film.year}</p>
                        <p><strong>Langue:</strong> ${film.lang}</p>
                        <p><strong>Support:</strong> ${film.support}</p>
                        <p><strong>Réalisateur:</strong> ${film.director?.firstname} ${film.director?.lastname}</p>
                        ${film.rate ? `<p><strong>Note:</strong> ⭐ ${film.rate}/10</p>` : ''}
                        ${film.opinion ? `<p><strong>Avis:</strong> ${film.opinion}</p>` : ''}
                        <p><strong>Description:</strong> ${film.description}</p>
                        ${film.actors?.length > 0 ? `
                            <p><strong>Acteurs:</strong> ${film.actors.slice(0, 5).map(a => `${a.firstname} ${a.lastname}`).join(', ')}</p>
                        ` : ''}
                        <div style="margin-top: 20px;">
                            <button onclick="deleteFilm(${film.id})" class="btn btn-delete">🗑️ Supprimer</button>
                        </div>
                    </div>
                </div>
            `;
            
            modal.style.display = 'flex';
            
        } catch (error) {
            showError('error-message', 'Erreur lors du chargement des détails du film');
        }
    };
    
    // Delete film
    window.deleteFilm = async (filmId) => {
        const confirmed = await showConfirm({
            icon: '🗑️',
            title: 'Supprimer ce film ?',
            message: 'Êtes-vous sûr de vouloir supprimer ce film de votre bibliothèque ? Cette action est irréversible.',
            confirmText: '🗑️ Supprimer',
            cancelText: 'Annuler',
            confirmClass: 'btn-delete'
        });
        
        if (!confirmed) return;
        
        try {
            await apiCall(`/films/${filmId}`, { method: 'DELETE' });
            document.getElementById('film-modal').style.display = 'none';
            loadFilms();
        } catch (error) {
            showError('error-message', 'Erreur lors de la suppression du film');
        }
    };
    
    // Close modal
    document.querySelector('.close-btn').addEventListener('click', () => {
        document.getElementById('film-modal').style.display = 'none';
    });
    
    // Search handler
    const performSearch = () => {
        const query = searchInput.value.trim();
        loadFilms(query);
    };
    
    searchBtn.addEventListener('click', performSearch);
    searchInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') performSearch();
    });
    
    // Initial load
    loadFilms();
});

