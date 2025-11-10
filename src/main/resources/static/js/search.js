// Search page logic
document.addEventListener('DOMContentLoaded', async () => {
    const searchForm = document.getElementById('search-form');
    const results = document.getElementById('results');
    
    // Check authentication
    const user = await checkAuth();
    if (!user) {
        window.location.href = '/login.html';
        return;
    }
    
    // Search form handler
    searchForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const title = document.getElementById('title').value;
        const language = document.getElementById('language').value;
        const page = document.getElementById('page').value;
        
        showLoading(true);
        results.style.display = 'none';
        
        try {
            const response = await apiCall(
                `/films/search?title=${encodeURIComponent(title)}&language=${language}&page=${page}`
            );
            
            showLoading(false);
            
            // TMDB returns {results: [...], page: ..., total_results: ...}
            const films = response?.results || [];
            
            if (!films || films.length === 0) {
                showError('error-message', 'Aucun résultat trouvé');
                return;
            }
            
            results.style.display = 'grid';
            results.innerHTML = films.map(film => `
                <div class="film-card" data-film='${JSON.stringify(film).replace(/'/g, "&#39;")}'>
                    <img src="${getTMDBPosterUrl(film.poster_path)}" alt="${film.title}" class="film-poster" 
                         onerror="this.src='/images/no-poster.jpg'">
                    <div class="film-info">
                        <div class="film-title">${film.title}</div>
                        <div class="film-year">${film.release_date ? film.release_date.substring(0, 4) : 'N/A'}</div>
                        ${film.vote_average ? `<div class="film-rate">⭐ ${film.vote_average.toFixed(1)}/10</div>` : ''}
                    </div>
                </div>
            `).join('');
            
            // Add click handlers
            document.querySelectorAll('.film-card').forEach(card => {
                card.addEventListener('click', () => {
                    const filmData = JSON.parse(card.dataset.film);
                    showAddModal(filmData);
                });
            });
            
        } catch (error) {
            showLoading(false);
            showError('error-message', 'Erreur lors de la recherche');
        }
    });
    
    // Show add film modal
    const showAddModal = (film) => {
        const modal = document.getElementById('add-modal');
        const container = document.getElementById('add-form-container');
        
        container.innerHTML = `
            <h2>Ajouter ${film.title}</h2>
            <form id="add-film-form">
                <input type="hidden" id="tmdbId" value="${film.id}">
                
                <div class="form-group">
                    <label for="lang">Langue</label>
                    <select id="lang" required>
                        <option value="fr">Français</option>
                        <option value="en">Anglais</option>
                        <option value="es">Espagnol</option>
                        <option value="de">Allemand</option>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="support">Support</label>
                    <select id="support" required>
                        <option value="DVD">DVD</option>
                        <option value="Blu-ray">Blu-ray</option>
                        <option value="4K UHD">4K UHD</option>
                        <option value="Numérique">Numérique</option>
                        <option value="VHS">VHS</option>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="rate">Note (0-10)</label>
                    <input type="number" id="rate" min="0" max="10" step="0.5" value="0">
                </div>
                
                <div class="form-group">
                    <label for="opinion">Votre avis</label>
                    <textarea id="opinion" rows="4"></textarea>
                </div>
                
                <button type="submit" class="btn btn-primary btn-block">Ajouter à ma bibliothèque</button>
            </form>
        `;
        
        modal.style.display = 'flex';
        
        // Add form handler
        document.getElementById('add-film-form').addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const tmdbId = parseInt(document.getElementById('tmdbId').value);
            const lang = document.getElementById('lang').value;
            const support = document.getElementById('support').value;
            const rate = parseFloat(document.getElementById('rate').value);
            const opinion = document.getElementById('opinion').value;
            
            try {
                await apiCall('/films', {
                    method: 'POST',
                    body: JSON.stringify({ tmdbId, lang, support, rate, opinion })
                });
                
                // Redirection directe vers la bibliothèque (sans alerte)
                window.location.href = '/library.html';
            } catch (error) {
                modal.style.display = 'none';
                showError('error-message', 'Erreur lors de l\'ajout du film. Il est peut-être déjà dans votre bibliothèque.');
            }
        });
    };
    
    // Close modal
    document.querySelector('.close-btn').addEventListener('click', () => {
        document.getElementById('add-modal').style.display = 'none';
    });
});

