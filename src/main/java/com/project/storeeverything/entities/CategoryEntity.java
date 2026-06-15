package com.project.storeeverything.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing information categories.
 * name (3-20, lowercase).
 */
@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Size(min = 3, max = 20, message = "{err.category.name.length}")
    @Pattern(regexp = "^[a-z]*$", message = "{err.category.name.lowercase}")
    private String name; // Rules: 3-20 characters, lowercase
}
