package fr.corentin.javatheque.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.corentin.javatheque.model.Film;
import fr.corentin.javatheque.model.Person;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class TMDBService {
    
    private final RestClient restClient;
    private final Gson gson;
    private final String apiKey;
    
    private static final int MAX_ACTORS = 10;
    
    public TMDBService(
            @Value("${tmdb.api.key}") String apiKey,
            @Value("${tmdb.api.base-url}") String baseUrl,
            RestClient.Builder restClientBuilder) {
        this.apiKey = apiKey;
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
        this.gson = new Gson();
    }

    @Cacheable(value = "tmdbSearches", key = "#title + '_' + #language + '_' + #page")
    public String searchMovies(String title, String language, int page) {
        try {
            String response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search/movie")
                            .queryParam("api_key", apiKey)
                            .queryParam("query", title)
                            .queryParam("language", language)
                            .queryParam("page", page)
                            .build())
                    .retrieve()
                    .body(String.class);
            
            return response;
        } catch (Exception e) {
            return null;
        }
    }

    @Cacheable(value = "tmdbMovies", key = "#movieId + '_' + #language + '_details'")
    public String getMovieDetails(int movieId, String language) {
        try {
            String response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/movie/{movieId}")
                            .queryParam("api_key", apiKey)
                            .queryParam("language", language)
                            .build(movieId))
                    .retrieve()
                    .body(String.class);
            
            return response;
        } catch (Exception e) {
            return null;
        }
    }

    @Cacheable(value = "tmdbMovies", key = "#movieId + '_' + #language + '_credits'")
    public String getCreditDetails(int movieId, String language) {
        try {
            String response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/movie/{movieId}/credits")
                            .queryParam("api_key", apiKey)
                            .queryParam("language", language)
                            .build(movieId))
                    .retrieve()
                    .body(String.class);
            
            return response;
        } catch (Exception e) {
            return null;
        }
    }

    public Film getFilmFromTMDB(int tmdbId, String lang, String support) {
        try {
            JsonObject movieDetails = gson.fromJson(getMovieDetails(tmdbId, lang), JsonObject.class);
            JsonObject movieCredits = gson.fromJson(getCreditDetails(tmdbId, lang), JsonObject.class);
            
            String poster = movieDetails.has("poster_path") && !movieDetails.get("poster_path").isJsonNull() 
                    ? movieDetails.get("poster_path").getAsString() 
                    : "";
            String title = movieDetails.get("title").getAsString();
            String description = movieDetails.has("overview") && !movieDetails.get("overview").isJsonNull()
                    ? movieDetails.get("overview").getAsString()
                    : "";
            String releaseDate = movieDetails.has("release_date") && !movieDetails.get("release_date").isJsonNull()
                    ? movieDetails.get("release_date").getAsString()
                    : "";
            
            String year = "";
            if (!releaseDate.isEmpty() && releaseDate.contains("-")) {
                year = releaseDate.split("-")[0];
            }
            if (year.isEmpty()) {
                year = "Unknown";
            }
            
            JsonArray cast = movieCredits.has("cast") ? movieCredits.getAsJsonArray("cast") : new JsonArray();
            JsonArray crew = movieCredits.has("crew") ? movieCredits.getAsJsonArray("crew") : new JsonArray();
            
            Person director = getDirectorFromCredits(crew);
            List<Person> actors = getActorsFromCredits(cast);
            
            Film film = new Film();
            film.setId(tmdbId);
            film.setPoster(poster);
            film.setLang(lang);
            film.setSupport(support);
            film.setTitle(title);
            film.setDescription(description);
            film.setReleaseDate(releaseDate);
            film.setYear(year);
            film.setRate(0.0f);
            film.setOpinion("No opinion yet");
            film.setDirector(director);
            film.setActors(actors);
            
            return film;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching film from TMDB", e);
        }
    }

    private Person getDirectorFromCredits(JsonArray crewArray) {
        return StreamSupport.stream(crewArray.spliterator(), false)
                .map(JsonElement::getAsJsonObject)
                .filter(jsonObject -> jsonObject.has("job") && "Director".equals(jsonObject.get("job").getAsString()))
                .findFirst()
                .map(jsonObject -> parsePersonName(jsonObject.get("name").getAsString()))
                .orElse(new Person("John", "Doe"));
    }

    private List<Person> getActorsFromCredits(JsonArray castArray) {
        return StreamSupport.stream(castArray.spliterator(), false)
                .map(JsonElement::getAsJsonObject)
                .filter(jsonObject -> jsonObject.has("known_for_department") && 
                        "Acting".equalsIgnoreCase(jsonObject.get("known_for_department").getAsString()))
                .limit(MAX_ACTORS)
                .map(jsonObject -> parsePersonName(jsonObject.get("name").getAsString()))
                .collect(Collectors.toList());
    }

    private Person parsePersonName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return new Person("John", "Doe");
        }
        
        String[] nameParts = fullName.trim().split("\\s+");
        if (nameParts.length == 1) {
            return new Person("", nameParts[0]);
        }
        
        String lastName = nameParts[nameParts.length - 1];
        String firstName = String.join(" ", Arrays.copyOfRange(nameParts, 0, nameParts.length - 1));
        return new Person(firstName, lastName);
    }
    
}

