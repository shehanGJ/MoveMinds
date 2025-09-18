package com.java.moveminds.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.java.moveminds.entities.CategoryEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryEntityRepository extends JpaRepository<CategoryEntity, Integer> {
    Optional<CategoryEntity> findByName(String name);

    /**
     * Retrieves all CategoryEntity objects that have associated fitness programs,
     * program attributes, and attribute values.
     *
     * @return a list of CategoryEntity objects with associated fitness programs,
     * program attributes, and attribute values
     */
    @Query("SELECT DISTINCT c FROM CategoryEntity c " +
            "JOIN c.fitnessPrograms fp " +
            "JOIN fp.programAttributes pa " +
            "JOIN pa.attributeValue av " +
            "JOIN av.attribute a " +
            "WHERE fp.id IS NOT NULL")
    List<CategoryEntity> findAllWithProgramsAndAttributesAndValues();
}
