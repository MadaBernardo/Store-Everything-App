package com.project.storeeverything.entities;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * Entity representing the saved information/notes.
 * Rules: title (3-20), content (5-500), optional link.
 */
@Entity
@Table(name = "information")
@Data
@NoArgsConstructor
public class InformationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Title is mandatory")
    @Size(min = 3, max = 20, message = "Title must be between 3 and 20 characters")
    private String title;

    @Column(nullable = false, length = 500)
    @NotBlank(message = "Content is mandatory")
    @Size(min = 5, max = 500, message = "Content must be between 5 and 500 characters")
    private String content;

    private String link;

    @Column(nullable = false)
    private LocalDate dateAdded;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    // --- SHARING links ---

    @Column(nullable = false)
    private boolean sharedLink = false; // true = anyone with the link can view

    /**
     * Many-to-Many relationship representing users who have been
     * specifically granted access to view this piece of information.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "information_shared_users",
            joinColumns = @JoinColumn(name = "information_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private java.util.Set<UserEntity> sharedWithUsers = new java.util.HashSet<>();
}