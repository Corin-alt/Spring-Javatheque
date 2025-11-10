package fr.corentin.javatheque.service;

import fr.corentin.javatheque.dto.FilmDTO;
import fr.corentin.javatheque.model.Library;
import fr.corentin.javatheque.repository.LibraryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LibraryService {
    
    private final LibraryRepository libraryRepository;
    private final FilmService filmService;

    public Library getLibraryByOwnerId(String ownerId) {
        return libraryRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Library not found for user"));
    }

    public List<FilmDTO.Response> getUserLibraryFilms(String userId) {
        Library library = getLibraryByOwnerId(userId);
        return filmService.getFilmsByLibrary(library.getId());
    }

    public List<FilmDTO.Response> searchUserLibraryFilms(String userId, String title) {
        Library library = getLibraryByOwnerId(userId);
        return filmService.searchFilmsInLibrary(library.getId(), title);
    }
    
}

