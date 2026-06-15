package com.project.storeeverything.services;

import com.project.storeeverything.entities.CategoryEntity;
import com.project.storeeverything.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Service class for managing Category logic.
 * Handles validation and interaction with CategoryRepository.
 */
@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Retrieves all categories from the database.
     * @return list of categories
     */
    public List<CategoryEntity> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Saves a new category after ensuring it meets business rules.
     * @param category the category to save
     * @return the saved category
     */
    public CategoryEntity saveCategory(CategoryEntity category) {
        // Logic to ensure name is lowercase can be added here
        category.setName(category.getName().toLowerCase());
        return categoryRepository.save(category);
    }
}
