package com.java.moveminds.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.java.moveminds.models.entities.AttributeValueEntity;

import java.util.List;

@Repository
public interface AttributeValueEntityRepository extends JpaRepository<AttributeValueEntity, Integer> {
    List<AttributeValueEntity> findByAttributeId(Integer attributeId);
}
