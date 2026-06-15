package com.project.storeeverything.repositories;

import com.project.storeeverything.entities.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for CategoryEntity.
 * Provides standard CRUD operations via JpaRepository.
 */
@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    // Custom query methods can be added here if needed
}
