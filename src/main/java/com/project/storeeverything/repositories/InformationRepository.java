package com.project.storeeverything.repositories;

import com.project.storeeverything.entities.InformationEntity;
import com.project.storeeverything.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for InformationEntity.
 * Manages persistence for notes and noteworthy information.
 */
@Repository
public interface InformationRepository extends JpaRepository<InformationEntity, Long> {

    /**
     * Finds all information entries belonging to a specific user.
     * @param user the owner of the information
     * @return a list of information entities
     */
    List<InformationEntity> findByUser(UserEntity user);
}
