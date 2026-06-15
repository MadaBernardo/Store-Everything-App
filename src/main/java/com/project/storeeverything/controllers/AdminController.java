package com.project.storeeverything.controllers;

import com.project.storeeverything.entities.UserEntity;
import com.project.storeeverything.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * Controller restricted to ADMIN users.
 * Manages user accounts and handles promotion operations.
 */
@Controller
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    /**
     * Display the list of all users in the system.
     */
    @GetMapping("/admin/users")
    public String listUsers(Model model) {
        List<UserEntity> allUsers = userRepository.findAll();
        model.addAttribute("users", allUsers);
        return "admin-users";
    }

    /**
     * Role management (Promoting limited user 'USER' to 'FULL_USER').
     */
    @PostMapping("/admin/users/promote/{id}")
    public String promoteUser(@PathVariable("id") Long id) {
        UserEntity user = userRepository.findById(id).orElse(null);

        if (user != null && "USER".equals(user.getRole())) {
            user.setRole("FULL_USER");
            userRepository.save(user);
        }

        return "redirect:/admin/users";
    }
}
