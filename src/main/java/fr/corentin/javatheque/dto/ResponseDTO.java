package fr.corentin.javatheque.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ResponseDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Success {
        private String message;
        private Object data;
        
        public Success(String message) {
            this.message = message;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Error {
        private String message;
        private int status;
        private LocalDateTime timestamp;
        
        public Error(String message, int status) {
            this.message = message;
            this.status = status;
            this.timestamp = LocalDateTime.now();
        }
    }
    
}

