package com.project.storeeverything.services;

import com.project.storeeverything.entities.UserEntity;
import com.project.storeeverything.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service layer handling user-related business logic.
 * Manages user creation and coordinates password encryption.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor injection for the required components.
     * The passwordEncoder is automatically provided by PasswordConfig.
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Secures and saves a new user to the database.
     * Encrypts the raw text password using BCrypt before persistence.
     *
     * @param user the user data collected from the registration form
     * @return the saved UserEntity containing the encrypted credentials
     */
    public UserEntity saveUser(UserEntity user) {
        // Encrypt the raw password from the form
        String encryptedPassword = passwordEncoder.encode(user.getPassword());

        // Overwrite the plain text password with the secure encrypted hash
        user.setPassword(encryptedPassword);

        // Save the updated entity using Spring Data JPA repository
        return userRepository.save(user);
    }
}