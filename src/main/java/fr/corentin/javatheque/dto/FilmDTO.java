package fr.corentin.javatheque.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class FilmDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotNull(message = "TMDB ID is required")
        private Integer tmdbId;
        
        @NotBlank(message = "Language is required")
        private String lang;
        
        @NotBlank(message = "Support is required")
        private String support;
        
        @Min(value = 0, message = "Rate must be at least 0")
        @Max(value = 10, message = "Rate must be at most 10")
        private Float rate;
        
        private String opinion;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Integer id;
        private String libraryId;
        private String poster;
        private String lang;
        private String support;
        private String title;
        private String description;
        private String releaseDate;
        private String year;
        private Float rate;
        private String opinion;
        private PersonDto director;
        private List<PersonDto> actors;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchRequest {
        @NotBlank(message = "Title is required")
        private String title;
        
        @NotBlank(message = "Language is required")
        private String language;
        
        @Positive(message = "Page must be positive")
        private int page = 1;
    }
    
}

