package com.project.storeeverything.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing application users.
 * first name (3-20, letters, first uppercase),
 * last name (3-50, letters, first uppercase),
 * login (3-20, lowercase), password (min 5), age (min. 18).
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(min = 3, max = 20)
    @Pattern(regexp = "^[A-Z][a-z]*$", message = "{err.user.name.format}")
    private String firstName; // 3-20, first letter uppercase

    @Column(nullable = false)
    @Size(min = 3, max = 50)
    @Pattern(regexp = "^[A-Z][a-z]*$", message = "{err.user.name.format}")
    private String lastName; // 3-50, first letter uppercase

    @Column(nullable = false, unique = true)
    @Size(min = 3, max = 20)
    @Pattern(regexp = "^[a-z]*$", message = "{err.user.login.format}")
    private String login; //  3-20, lowercase

    @Column(nullable = false)
    @Size(min = 5)
    private String password; //  at least 5 characters

    @Column(nullable = false)
    @Min(value = 18, message = "{err.user.age}")
    private int age; //min. 18

    @Column(nullable = false)
    private String role; // ADMIN, FULL_USER, LIMITED_USER
}
