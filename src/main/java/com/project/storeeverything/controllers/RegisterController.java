package com.project.storeeverything.controllers;

import com.project.storeeverything.entities.UserEntity;
import com.project.storeeverything.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller responsible for handling user registration.
 * Allows new users to create accounts with encrypted passwords.
 */
@Controller
public class RegisterController {

    private final UserService userService;

    /**
     * Constructor injection for the UserService.
     */
    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Displays the registration form (GET request).
     */
    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new UserEntity());
        return "register";
    }

    /**
     * Processes the registration form submission (POST request).
     * Receives the filled UserEntity object from the Thymeleaf form,
     * delegates the saving logic to the UserService, and redirects to login.
     *
     * @param user the populated user entity from the form
     * @return a redirect instruction to the login page
     */
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") UserEntity user) {
        // Sends the user data to the service layer to be saved securely
        userService.saveUser(user);

        // After successfully saving, redirect the user to the login screen
        return "redirect:/login";
    }
}
