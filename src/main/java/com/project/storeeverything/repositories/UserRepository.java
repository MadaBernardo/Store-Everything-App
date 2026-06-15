package com.project.storeeverything.repositories;

import com.project.storeeverything.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for UserEntity.
 * Extends JpaRepository to handle user data persistence.
 */
@Repository //essa anotação indica que a classe é da camada de persistencia
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Finds a user by their unique login string.
     * Used during the authentication process.
     * @param login the user's login
     * @return an Optional containing the user if found
     */
    Optional<UserEntity> findByLogin(String login);
}
