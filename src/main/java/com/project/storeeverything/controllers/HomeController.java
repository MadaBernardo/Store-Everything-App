package com.project.storeeverything.controllers;

import com.project.storeeverything.entities.InformationEntity;
import com.project.storeeverything.entities.UserEntity;
import com.project.storeeverything.repositories.UserRepository;
import com.project.storeeverything.services.InformationService;
import com.project.storeeverything.services.CategoryService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final InformationService informationService;
    private final CategoryService categoryService;

    @Autowired
    private UserRepository userRepository;

    public HomeController(InformationService informationService, CategoryService categoryService) {
        this.informationService = informationService;
        this.categoryService = categoryService;
    }

    /**
     * If the user is already authenticated, it redirects them to the dashboard.
     * Otherwise, it shows the public welcome page.
     */
    @GetMapping("/")
    public String showWelcomePage() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/dashboard";
        }

        return "welcome";
    }

    /**
     * Enhanced Dashboard with Filtering, Sorting, and Cookie management.
     * Integrates HttpSession to pull user records dynamically from memory cache.
     */
    @GetMapping("/dashboard")
    public String showDashboardPage(
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "direction", required = false) String direction,
            @CookieValue(value = "cookieSortBy", defaultValue = "date") String cookieSortBy,
            @CookieValue(value = "cookieDirection", defaultValue = "desc") String cookieDirection,
            @CookieValue(value = "cookieCategory", defaultValue = "0") String cookieCategory,
            HttpServletResponse response,
            HttpSession session, // Injected HTTP Session to monitor workflow pipelines
            Model model) {

        // Retrieve the authenticated user from the session context security principal
        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByLogin(loggedInUsername).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        // Cookie Fallback: If request parameters are absent, read from cookies to remember user choice
        if (sortBy == null) sortBy = cookieSortBy;
        if (direction == null) direction = cookieDirection;
        if (categoryId == null) categoryId = Long.parseLong(cookieCategory);

        // Update Cookies: Persist current configuration back to the client's browser
        Cookie sCookie = new Cookie("cookieSortBy", sortBy);
        Cookie dCookie = new Cookie("cookieDirection", direction);
        Cookie cCookie = new Cookie("cookieCategory", String.valueOf(categoryId));

        // Define a 24-hour lifespan for cookies
        sCookie.setMaxAge(86400);
        dCookie.setMaxAge(86400);
        cCookie.setMaxAge(86400);

        response.addCookie(sCookie);
        response.addCookie(dCookie);
        response.addCookie(cCookie);

        // Added the mandatory session parameter at the end to match the updated service logic
        model.addAttribute("informations", informationService.getFilteredAndSortedInformation(user, categoryId, sortBy, direction, session));
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("newInformation", new InformationEntity());

        // Synchronize selected dropdown states in HTML
        model.addAttribute("currentCategory", categoryId);
        model.addAttribute("currentSortBy", sortBy);
        model.addAttribute("currentDirection", direction);

        return "index";
    }
}