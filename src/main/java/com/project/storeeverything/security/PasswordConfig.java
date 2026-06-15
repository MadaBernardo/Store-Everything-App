package com.project.storeeverything.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration for password hashing.
 * Defined separately to avoid circular dependency issues during registration.
 */
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Standard BCrypt encoder with default strength
        return new BCryptPasswordEncoder();
    }
}
