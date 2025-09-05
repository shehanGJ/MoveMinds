package com.java.moveminds.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.java.moveminds.models.entities.ProgramImageEntity;

import java.util.Optional;

@Repository
public interface ProgramImageEntityRepository extends JpaRepository<ProgramImageEntity, Integer> {
    Optional<ProgramImageEntity> findByImageUrl(String imageUrl);
}
