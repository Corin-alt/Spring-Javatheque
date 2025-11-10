package fr.corentin.javatheque.controller;

import fr.corentin.javatheque.dto.UserDTO;
import fr.corentin.javatheque.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO.Response> register(
            @Valid @RequestBody UserDTO.Register request,
            HttpSession session) {
        try {
            UserDTO.Response response = userService.registerUser(request);
            session.setAttribute("userId", response.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO.Response> login(
            @Valid @RequestBody UserDTO.Login request,
            HttpSession session) {
        try {
            UserDTO.Response response = userService.loginUser(request);
            session.setAttribute("userId", response.getId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO.Response> getCurrentUser(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            UserDTO.Response response = userService.getUserById(userId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
}

