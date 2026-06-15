package com.project.storeeverything.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller responsible for handling user authentication routes.
 * Maps the custom endpoints required by our WebSecurityConfig.
 */
@Controller
public class LoginController {

    /**
     * Handles HTTP GET requests for the "/login" URL.
     * This method is triggered automatically by Spring Security when a user
     * tries to access a protected page without being authenticated.
     *
     * @return the name of the Thymeleaf template to render (login.html)
     */
    @GetMapping("/login")
    public String showLoginPage() {
        // Returns the view name. Spring Boot will search for a file
        return "login";
    }
}
