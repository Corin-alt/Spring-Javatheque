package fr.corentin.javatheque.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Register {
        @NotBlank(message = "Lastname is required")
        private String lastname;
        
        @NotBlank(message = "Firstname is required")
        private String firstname;
        
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        private String email;
        
        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters long")
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Login {
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        private String email;
        
        @NotBlank(message = "Password is required")
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private String id;
        private String lastname;
        private String firstname;
        private String email;
        private String libraryId;
    }
    
}

