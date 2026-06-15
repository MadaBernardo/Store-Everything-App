package com.project.storeeverything.controllers;

import com.project.storeeverything.entities.CategoryEntity;
import com.project.storeeverything.services.CategoryService;
import org.springframework.security.core.context.SecurityContextHolder; // Added for security lookup
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller responsible for managing information categories.
 * Accessible only to authenticated users with proper authority clear level.
 */
@Controller
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Constructor injection for CategoryService.
     */
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Displays the page to view all categories and add a new one.
     * Injects current username principal context to support global navbar binding tags.
     */
    @GetMapping("/categories")
    public String listCategories(Model model) {
        // Fetch the logged-in username to support header greetings in thymeleaf views
        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("username", loggedInUsername);

        // Sends the list of existing categories to the HTML
        model.addAttribute("categories", categoryService.getAllCategories());
        // Sends an empty category object for the creation form
        model.addAttribute("newCategory", new CategoryEntity());

        return "categories";
    }

    /**
     * Processes the creation of a new category.
     */
    @PostMapping("/categories/add")
    public String addCategory(@ModelAttribute("newCategory") CategoryEntity category) {
        // Enforce lowercase naming convention guidelines locally
        if (category.getName() != null) {
            category.setName(category.getName().toLowerCase());
        }
        categoryService.saveCategory(category);
        // Reloads the categories page to show the newly added item
        return "redirect:/categories";
    }
}