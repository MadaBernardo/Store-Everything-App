package com.project.storeeverything.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import com.project.storeeverything.entities.InformationEntity;
import com.project.storeeverything.entities.UserEntity;
import com.project.storeeverything.repositories.UserRepository;
import com.project.storeeverything.services.InformationService;
import com.project.storeeverything.services.CategoryService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

/**
 * Controller handling Information (Notes) lifecycle.
 * Validates requirements for Title (3-20), Content (5-500), and Date.
 */
@Controller
public class InformationController {

    private final InformationService informationService;
    private final CategoryService categoryService;
    private final UserRepository userRepository;

    /**
     * Constructor injection for required services and repositories.
     */
    public InformationController(InformationService informationService,
                                 CategoryService categoryService,
                                 UserRepository userRepository) {
        this.informationService = informationService;
        this.categoryService = categoryService;
        this.userRepository = userRepository;
    }

    /**
     * Captures form data from the Dashboard to create a new Information card.
     * Saves the data temporarily into the HTTP Session instead of DB.
     */
    @PostMapping("/information/add")
    public String addInformation(@Valid @ModelAttribute("newInformation") InformationEntity information,
                                 BindingResult bindingResult,
                                 Model model,
                                 HttpSession session) {

        // Check if any validation rule (Size, NotBlank) was violated
        if (bindingResult.hasErrors()) {
            String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            UserEntity user = userRepository.findByLogin(loggedInUsername).orElse(null);

            if (user != null) {
                // Fetch current list from session to prevent rendering issues in Thymeleaf
                model.addAttribute("informations", informationService.getUserInformation(user, session));
            } else {
                model.addAttribute("informations", informationService.getAllInformations());
            }
            model.addAttribute("categories", categoryService.getAllCategories());

            // Return to the dashboard view directly to display validation errors
            return "index";
        }

        try {
            String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            UserEntity user = userRepository.findByLogin(loggedInUsername).orElse(null);

            if (user != null) {
                information.setUser(user);
                information.setDateAdded(LocalDate.now());

                // SESSION : Save temporary card state into HTTP Session only
                informationService.saveToSession(information, session, user);
            }
        } catch (Exception e) {
            System.out.println("Error saving information to session: " + e.getMessage());
        }

        return "redirect:/dashboard";
    }

    /**
     * Removes a specific note record from the temporary session storage list.
     */
    @PostMapping("/information/delete/{id}")
    public String deleteInformation(@PathVariable("id") Long id, HttpSession session) {
        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByLogin(loggedInUsername).orElse(null);

        if (user != null) {
            // Remove logically from session active list only
            informationService.deleteFromSession(id, session, user);
        }

        return "redirect:/dashboard";
    }

    /**
     * Shows the edit form for a specific information card by picking data from active cache.
     */
    @GetMapping("/information/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        // Find the targeted note in the list for editing purposes
        InformationEntity information = informationService.getAllInformations()
                .stream()
                .filter(i -> i.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (information == null) {
            return "redirect:/dashboard";
        }

        model.addAttribute("information", information);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "edit-information";
    }

    /**
     * Processes the modification request and applies changes to the active session registry.
     */
    @PostMapping("/information/edit/{id}")
    public String updateInformation(@PathVariable("id") Long id,
                                    @ModelAttribute("information") InformationEntity updatedInfo,
                                    HttpSession session) {
        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByLogin(loggedInUsername).orElse(null);

        if (user != null) {
            updatedInfo.setId(id); // Keep tracking ID identity reference
            updatedInfo.setUser(user);
            updatedInfo.setDateAdded(LocalDate.now());

            // SESSION: Update target item metadata into HTTP Session only
            informationService.saveToSession(updatedInfo, session, user);
        }

        return "redirect:/dashboard";
    }

    /**
     * Opens the sharing configuration panel for a specific data item.
     */
    @GetMapping("/information/share/{id}")
    public String showShareForm(@PathVariable("id") Long id, Model model) {
        InformationEntity information = informationService.getAllInformations()
                .stream()
                .filter(i -> i.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (information == null) {
            return "redirect:/dashboard";
        }

        model.addAttribute("information", information);
        return "share-information";
    }

    /**
     * Toggles the permission state for public link views.
     */
    @PostMapping("/information/share/{id}/toggle-link")
    public String togglePublicLink(@PathVariable("id") Long id, @RequestParam("sharedLink") boolean sharedLink) {
        InformationEntity information = informationService.getAllInformations()
                .stream()
                .filter(i -> i.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (information != null) {
            information.setSharedLink(sharedLink);
            // Save sharing metadata settings
            informationService.saveInformation(information);
        }
        return "redirect:/information/share/" + id;
    }

    /**
     * Shares a data element directly with another authenticated account using their login handle.
     */
    @PostMapping("/information/share/{id}/add-user")
    public String shareWithUser(@PathVariable("id") Long id, @RequestParam("userLogin") String userLogin, Model model) {
        InformationEntity information = informationService.getAllInformations()
                .stream()
                .filter(i -> i.getId().equals(id))
                .findFirst()
                .orElse(null);

        UserEntity targetUser = userRepository.findByLogin(userLogin).orElse(null);

        if (information != null && targetUser != null) {
            // Guard Rule: Prevent users from sharing records with themselves
            if (!information.getUser().getLogin().equals(userLogin)) {
                information.getSharedWithUsers().add(targetUser);
                informationService.saveInformation(information);
            }
        }
        return "redirect:/information/share/" + id;
    }

    /**
     * Displays all data elements that have been directly shared with the active user account.
     */
    @GetMapping("/information/shared-with-me")
    public String showSharedWithMePage(Model model) {
        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity currentUser = userRepository.findByLogin(loggedInUsername).orElse(null);

        if (currentUser == null) {
            return "redirect:/login";
        }

        // Filters system elements to locate records shared with this profile
        java.util.List<InformationEntity> sharedNotes = informationService.getAllInformations()
                .stream()
                .filter(info -> info.getSharedWithUsers().contains(currentUser))
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("sharedInformations", sharedNotes);
        return "shared-with-me";
    }

    /**
     * Open gateway route allowing unauthenticated web clients to access public share payloads.
     */
    @GetMapping("/public/share/{id}")
    public String showPublicSharedNote(@PathVariable("id") Long id, Model model) {
        InformationEntity information = informationService.getAllInformations()
                .stream()
                .filter(i -> i.getId().equals(id))
                .findFirst()
                .orElse(null);

        // Security Guard: Revoke route access if item does not exist or public flag is false
        if (information == null || !information.isSharedLink()) {
            return "redirect:/login";
        }

        model.addAttribute("information", information);
        return "public-view";
    }
}