package fr.corentin.javatheque.service;

import fr.corentin.javatheque.dto.FilmDTO;
import fr.corentin.javatheque.dto.PersonDto;
import fr.corentin.javatheque.model.Film;
import fr.corentin.javatheque.model.Person;
import fr.corentin.javatheque.repository.FilmRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    
    private final FilmRepository filmRepository;
    private final TMDBService tmdbService;

    @Transactional
    public FilmDTO.Response addFilm(FilmDTO.Request request, String libraryId) {
        // Check if film already exists in this library
        if (filmRepository.existsByIdAndLibraryId(request.getTmdbId(), libraryId)) {
            throw new IllegalArgumentException("Film already exists in this library");
        }
        
        // Fetch film data from TMDB
        Film film = tmdbService.getFilmFromTMDB(
                request.getTmdbId(),
                request.getLang(),
                request.getSupport()
        );
        
        film.setLibraryId(libraryId);
        if (request.getRate() != null) {
            film.setRate(request.getRate());
        }
        if (request.getOpinion() != null) {
            film.setOpinion(request.getOpinion());
        }
        
        film = filmRepository.save(film);
        
        return mapToFilmResponse(film);
    }

    public List<FilmDTO.Response> getFilmsByLibrary(String libraryId) {
        List<Film> films = filmRepository.findByLibraryId(libraryId);
        return films.stream()
                .map(this::mapToFilmResponse)
                .collect(Collectors.toList());
    }

    public List<FilmDTO.Response> searchFilmsInLibrary(String libraryId, String title) {
        List<Film> films = filmRepository.findByLibraryIdAndTitleContainingIgnoreCase(libraryId, title);
        return films.stream()
                .map(this::mapToFilmResponse)
                .collect(Collectors.toList());
    }

    public FilmDTO.Response getFilmById(Integer filmId) {
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new IllegalArgumentException("Film not found"));
        
        return mapToFilmResponse(film);
    }

    @Transactional
    public FilmDTO.Response updateFilm(Integer filmId, FilmDTO.Request request) {
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new IllegalArgumentException("Film not found"));
        
        if (request.getLang() != null) {
            film.setLang(request.getLang());
        }
        if (request.getSupport() != null) {
            film.setSupport(request.getSupport());
        }
        if (request.getRate() != null) {
            film.setRate(request.getRate());
        }
        if (request.getOpinion() != null) {
            film.setOpinion(request.getOpinion());
        }
        
        film = filmRepository.save(film);
        
        return mapToFilmResponse(film);
    }

    @Transactional
    public void deleteFilm(Integer filmId) {
        if (!filmRepository.existsById(filmId)) {
            throw new IllegalArgumentException("Film not found");
        }
        
        filmRepository.deleteById(filmId);
    }

    private FilmDTO.Response mapToFilmResponse(Film film) {
        FilmDTO.Response response = new FilmDTO.Response();
        response.setId(film.getId());
        response.setLibraryId(film.getLibraryId());
        response.setPoster(film.getPoster());
        response.setLang(film.getLang());
        response.setSupport(film.getSupport());
        response.setTitle(film.getTitle());
        response.setDescription(film.getDescription());
        response.setReleaseDate(film.getReleaseDate());
        response.setYear(film.getYear());
        response.setRate(film.getRate());
        response.setOpinion(film.getOpinion());
        response.setDirector(mapToPersonDto(film.getDirector()));
        response.setActors(film.getActors().stream()
                .map(this::mapToPersonDto)
                .collect(Collectors.toList()));
        return response;
    }

    private PersonDto mapToPersonDto(Person person) {
        if (person == null) {
            return null;
        }
        return new PersonDto(person.getFirstname(), person.getLastname());
    }
    
}

