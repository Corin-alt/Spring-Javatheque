package fr.corentin.javatheque.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.corentin.javatheque.dto.FilmDTO;
import fr.corentin.javatheque.service.FilmService;
import fr.corentin.javatheque.service.LibraryService;
import fr.corentin.javatheque.service.TMDBService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/films")
@RequiredArgsConstructor
public class FilmController {
    
    private final FilmService filmService;
    private final LibraryService libraryService;
    private final TMDBService tmdbService;
    private final Gson gson = new Gson();

    @GetMapping(value = "/search", produces = "application/json")
    public ResponseEntity<String> searchFilms(
            @RequestParam String title,
            @RequestParam String language,
            @RequestParam(defaultValue = "1") int page) {
        String jsonString = tmdbService.searchMovies(title, language, page);
        
        if (jsonString == null || jsonString.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("{}");
        }
        
        // Return the raw JSON string directly - Spring will handle it as JSON
        return ResponseEntity.ok(jsonString);
    }

    @PostMapping
    public ResponseEntity<FilmDTO.Response> addFilm(
            @Valid @RequestBody FilmDTO.Request request,
            HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            var library = libraryService.getLibraryByOwnerId(userId);
            FilmDTO.Response response = filmService.addFilm(request, library.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{filmId}")
    public ResponseEntity<FilmDTO.Response> getFilm(@PathVariable Integer filmId) {
        try {
            FilmDTO.Response response = filmService.getFilmById(filmId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{filmId}")
    public ResponseEntity<FilmDTO.Response> updateFilm(
            @PathVariable Integer filmId,
            @Valid @RequestBody FilmDTO.Request request,
            HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            FilmDTO.Response response = filmService.updateFilm(filmId, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{filmId}")
    public ResponseEntity<Void> deleteFilm(
            @PathVariable Integer filmId,
            HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            filmService.deleteFilm(filmId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
}

