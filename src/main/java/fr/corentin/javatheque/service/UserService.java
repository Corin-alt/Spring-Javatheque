package fr.corentin.javatheque.service;

import fr.corentin.javatheque.dto.UserDTO;
import fr.corentin.javatheque.model.Library;
import fr.corentin.javatheque.model.User;
import fr.corentin.javatheque.repository.LibraryRepository;
import fr.corentin.javatheque.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final LibraryRepository libraryRepository;
    private final PasswordService passwordService;

    @Transactional
    public UserDTO.Response registerUser(UserDTO.Register request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        // Create new user (without library first)
        User user = new User();
        user.setLastname(request.getLastname());
        user.setFirstname(request.getFirstname());
        user.setEmail(request.getEmail());
        user.setPassword(passwordService.encryptPassword(request.getPassword()));
        
        // Save user first to generate ID
        user = userRepository.save(user);
        
        // Now create library with the generated user ID
        Library library = new Library(user.getId());
        library = libraryRepository.save(library);
        
        // Update user with library reference
        user.setLibrary(library);
        user = userRepository.save(user);
        
        return mapToUserResponse(user);
    }
    

    public UserDTO.Response loginUser(UserDTO.Login request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        
        User user = userOpt.get();
        
        if (!passwordService.verifyPassword(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        
        return mapToUserResponse(user);
    }

    public UserDTO.Response getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return mapToUserResponse(user);
    }

    public UserDTO.Response getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return mapToUserResponse(user);
    }

    @Transactional
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Delete library if exists
        if (user.getLibrary() != null) {
            libraryRepository.delete(user.getLibrary());
        }
        
        userRepository.delete(user);
    }

    private UserDTO.Response mapToUserResponse(User user) {
        UserDTO.Response response = new UserDTO.Response();
        response.setId(user.getId());
        response.setLastname(user.getLastname());
        response.setFirstname(user.getFirstname());
        response.setEmail(user.getEmail());
        response.setLibraryId(user.getLibrary() != null ? user.getLibrary().getId() : null);
        return response;
    }
    
}

