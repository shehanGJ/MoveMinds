package com.java.moveminds.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.java.moveminds.entities.AttributeEntity;

import java.util.List;

@Repository
public interface AttributeEntityRepository extends JpaRepository<AttributeEntity, Integer> {
    List<AttributeEntity> findByCategoryId(Integer categoryId);
    List<AttributeEntity> findDistinctByAttributeValues_ProgramAttributes_FitnessProgramIsNotNull();
}
