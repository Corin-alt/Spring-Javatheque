package fr.corentin.javatheque.repository;

import fr.corentin.javatheque.model.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilmRepository extends JpaRepository<Film, Integer> {

    List<Film> findByLibraryId(String libraryId);

    @Query("SELECT f FROM Film f WHERE f.libraryId = :libraryId AND LOWER(f.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Film> findByLibraryIdAndTitleContainingIgnoreCase(@Param("libraryId") String libraryId, @Param("title") String title);

    void deleteByLibraryId(String libraryId);

    boolean existsByIdAndLibraryId(Integer id, String libraryId);
    
}

