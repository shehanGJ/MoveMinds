package com.java.moveminds.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.java.moveminds.entities.CategoryEntity;
import com.java.moveminds.entities.FitnessProgramEntity;
import com.java.moveminds.entities.UserEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FitnessProgramEntityRepository extends JpaRepository<FitnessProgramEntity, Integer>, JpaSpecificationExecutor<FitnessProgramEntity> {
    Optional<FitnessProgramEntity> findByName(String name);
    Page<FitnessProgramEntity> findAllByUserId(Integer userId, Pageable pageable);
    Page<FitnessProgramEntity> findDistinctByProgramAttributes_AttributeValue_Id(Integer attributeValueId, Pageable pageable);
    Page<FitnessProgramEntity> findDistinctByProgramAttributes_AttributeValue_IdIn(List<Integer> attributeValueIds, Pageable pageable);
    Page<FitnessProgramEntity> findAllByCategoryId(Integer categoryId, Pageable pageable);
    List<FitnessProgramEntity> findAllByCategoryAndCreatedAtAfter(CategoryEntity category, LocalDateTime createdAt);
    Page<FitnessProgramEntity> findByUser(UserEntity user, Pageable pageable);
    
    // Count methods for admin dashboard
    @Query("SELECT COUNT(up) FROM UserProgramEntity up WHERE up.fitnessProgramByProgramId.id = :programId")
    long countUserProgramsByProgramId(@Param("programId") Integer programId);
    
    @Query("SELECT COUNT(c) FROM CommentEntity c WHERE c.fitnessProgram.id = :programId")
    long countCommentsByProgramId(@Param("programId") Integer programId);
    
    // Analytics methods
    long countByCreatedAtAfter(LocalDateTime dateTime);
    List<FitnessProgramEntity> findByCreatedAtAfter(LocalDateTime dateTime);
    long countByUser(UserEntity user);
    
    // Program activation methods
    long countByIsActiveTrue();
    long countByIsActiveFalse();
    long countByIsActiveAndCreatedAtAfter(Boolean isActive, LocalDateTime dateTime);
    Page<FitnessProgramEntity> findByIsActive(Boolean isActive, Pageable pageable);
    List<FitnessProgramEntity> findByIsActiveAndCreatedAtAfter(Boolean isActive, LocalDateTime dateTime);
}
