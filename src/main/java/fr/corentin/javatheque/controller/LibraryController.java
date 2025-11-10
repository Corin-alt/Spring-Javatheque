package fr.corentin.javatheque.controller;

import fr.corentin.javatheque.dto.FilmDTO;
import fr.corentin.javatheque.service.LibraryService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
public class LibraryController {
    
    private final LibraryService libraryService;

    @GetMapping
    public ResponseEntity<List<FilmDTO.Response>> getLibrary(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            List<FilmDTO.Response> films = libraryService.getUserLibraryFilms(userId);
            return ResponseEntity.ok(films);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<FilmDTO.Response>> searchLibrary(
            @RequestParam(required = false) String search,
            HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            List<FilmDTO.Response> films;
            if (search == null || search.isEmpty() || "all".equalsIgnoreCase(search)) {
                films = libraryService.getUserLibraryFilms(userId);
            } else {
                films = libraryService.searchUserLibraryFilms(userId, search);
            }
            return ResponseEntity.ok(films);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
}

