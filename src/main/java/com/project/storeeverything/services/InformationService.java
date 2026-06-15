package com.project.storeeverything.services;

import com.project.storeeverything.entities.InformationEntity;
import com.project.storeeverything.entities.UserEntity;
import com.project.storeeverything.repositories.InformationRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class handling the business logic for information payloads.
 * Manages active user records inside temporary state arrays prior to database commits.
 */
@Service
public class InformationService {

    @Autowired
    private InformationRepository informationRepository;

    // Cache lookup pointer descriptor used within the active container session
    private static final String SESSION_KEY = "PENDING_NOTES";

    /**
     * Internal helper to extract or instantiate memory cached notes from the HTTP session state.
     */
    @SuppressWarnings("unchecked")
    private List<InformationEntity> getSessionNotes(HttpSession session, UserEntity user) {
        List<InformationEntity> notes = (List<InformationEntity>) session.getAttribute(SESSION_KEY);
        if (notes == null) {
            // Lazy initialization fallback: Populate memory list from permanent database baseline tables
            notes = new ArrayList<>(informationRepository.findAll());
            session.setAttribute(SESSION_KEY, notes);
        }
        return notes;
    }

    /**
     * Fetches all registered metadata records directly bypassing the localized state cache.
     */
    public List<InformationEntity> getAllInformations() {
        return informationRepository.findAll();
    }

    /**
     * Persists information updates directly into the database repository.
     * Required by the controller's sharing methods to update relational meta tables instantly.
     */
    public void saveInformation(InformationEntity info) {
        informationRepository.save(info);
    }

    /**
     * Read localized information context records dynamically resolved out of session memory.
     */
    public List<InformationEntity> getUserInformation(UserEntity user, HttpSession session) {
        return getSessionNotes(session, user).stream()
                .filter(info -> info.getUser() != null && info.getUser().getId().equals(user.getId()))
                .collect(Collectors.toList());
    }

    /**
     *  Cache added/modified object states temporarily into the user context session memory stack.
     */
    public void saveToSession(InformationEntity info, HttpSession session, UserEntity user) {
        List<InformationEntity> notes = getSessionNotes(session, user);

        if (info.getId() != null) {
            // Update Scenario:  Purge old entry reference signatures before adding the modified context record
            notes.removeIf(n -> n.getId() != null && n.getId().equals(info.getId()));
        } else {
            // Creation Scenario: Assign a negative transient placeholder ID to support Thymeleaf evaluation workflows
            long tempId = notes.stream().mapToLong(n -> n.getId() != null ? n.getId() : 0).min().orElse(0) - 1;
            info.setId(tempId < 0 ? tempId : -1L);
        }

        notes.add(info);
        session.setAttribute(SESSION_KEY, notes);
    }

    /**
     * Purge objects directly from active volatile memory registers.
     */
    public void deleteFromSession(Long id, HttpSession session, UserEntity user) {
        List<InformationEntity> notes = getSessionNotes(session, user);
        notes.removeIf(n -> n.getId() != null && n.getId().equals(id));
        session.setAttribute(SESSION_KEY, notes);
    }

    /**
     * Triggered downstream at logout lifecycle boundaries to write transaction logs into persistent databases.
     */
    public void flushSessionToDatabase(HttpSession session) {
        @SuppressWarnings("unchecked")
        List<InformationEntity> notes = (List<InformationEntity>) session.getAttribute(SESSION_KEY);
        if (notes != null) {
            for (InformationEntity info : notes) {
                // Clear transient trace pointers below zero to allow MySQL auto-increment generation engines to run
                if (info.getId() != null && info.getId() < 0) {
                    info.setId(null);
                }
                informationRepository.save(info);
            }
        }
    }

    /**
     * Handles advanced processing metrics including category sorting streams and sorting directions.
     */
    public List<InformationEntity> getFilteredAndSortedInformation(
            UserEntity user, Long categoryId, String sortBy, String direction, HttpSession session) {

        // Source context feeds from dynamic memory allocations
        List<InformationEntity> list = getUserInformation(user, session);

        // Filter out unmatched properties when parameters are present
        if (categoryId != null && categoryId > 0) {
            list = list.stream()
                    .filter(info -> info.getCategory() != null && info.getCategory().getId().equals(categoryId))
                    .collect(Collectors.toList());
        }

        // Apply dynamic conditional structures depending on cookie tracker variables
        if ("category".equals(sortBy)) {
            list.sort((a, b) -> {
                String catA = (a.getCategory() != null) ? a.getCategory().getName() : "";
                String catB = (b.getCategory() != null) ? b.getCategory().getName() : "";
                return "desc".equals(direction) ? catB.compareTo(catA) : catA.compareTo(catB);
            });
        } else {
            if ("asc".equals(direction)) {
                list.sort(Comparator.comparing(InformationEntity::getDateAdded, Comparator.nullsLast(Comparator.naturalOrder())));
            } else {
                list.sort(Comparator.comparing(InformationEntity::getDateAdded, Comparator.nullsLast(Comparator.reverseOrder())));
            }
        }
        return list;
    }
}