package fr.corentin.javatheque.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "films")
public class Film {
    
    @Id
    private Integer id;
    
    @Column(name = "library_id", nullable = false)
    private String libraryId;
    
    @Column(name = "poster")
    private String poster;
    
    @Column(name = "lang")
    private String lang;
    
    @Column(name = "support")
    private String support;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "release_date")
    private String releaseDate;
    
    @Column(name = "year")
    private String year;
    
    @Column(name = "rate")
    private Float rate;
    
    @Column(name = "opinion", columnDefinition = "TEXT")
    private String opinion;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "firstname", column = @Column(name = "director_firstname")),
        @AttributeOverride(name = "lastname", column = @Column(name = "director_lastname"))
    })
    private Person director;
    
    @ElementCollection
    @CollectionTable(name = "film_actors", joinColumns = @JoinColumn(name = "film_id"))
    @AttributeOverrides({
        @AttributeOverride(name = "firstname", column = @Column(name = "actor_firstname")),
        @AttributeOverride(name = "lastname", column = @Column(name = "actor_lastname"))
    })
    private List<Person> actors = new ArrayList<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Library library;
    
}

