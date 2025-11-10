package fr.corentin.javatheque.repository;

import fr.corentin.javatheque.model.Library;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LibraryRepository extends JpaRepository<Library, String> {

    Optional<Library> findByOwnerId(String ownerId);

    void deleteByOwnerId(String ownerId);

    boolean existsByOwnerId(String ownerId);
    
}

