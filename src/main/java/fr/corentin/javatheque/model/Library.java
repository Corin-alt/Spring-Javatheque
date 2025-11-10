package fr.corentin.javatheque.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "libraries")
public class Library {
    
    @Id
    @Column(name = "id", length = 36)
    private String id;
    
    @Column(name = "owner_id", nullable = false, unique = true, length = 36)
    private String ownerId;
    
    @OneToMany(mappedBy = "library", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Film> films = new ArrayList<>();
    
    @OneToOne(mappedBy = "library")
    private User owner;
    
    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

    public Library(String ownerId) {
        this.id = UUID.randomUUID().toString();
        this.ownerId = ownerId;
        this.films = new ArrayList<>();
    }
    
}

