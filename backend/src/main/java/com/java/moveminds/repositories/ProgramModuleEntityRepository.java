package com.java.moveminds.repositories;

import com.java.moveminds.entities.FitnessProgramEntity;
import com.java.moveminds.entities.ProgramModuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramModuleEntityRepository extends JpaRepository<ProgramModuleEntity, Integer> {
    
    List<ProgramModuleEntity> findByFitnessProgramOrderByOrderIndex(FitnessProgramEntity fitnessProgram);
    
    List<ProgramModuleEntity> findByFitnessProgramAndIsPublishedTrueOrderByOrderIndex(FitnessProgramEntity fitnessProgram);
    
    @Query("SELECT DISTINCT m FROM ProgramModuleEntity m LEFT JOIN FETCH m.lessons WHERE m.fitnessProgram.id = :programId ORDER BY m.orderIndex")
    List<ProgramModuleEntity> findByProgramIdOrderByOrderIndex(@Param("programId") Integer programId);
    
    @Query("SELECT DISTINCT m FROM ProgramModuleEntity m LEFT JOIN FETCH m.lessons WHERE m.fitnessProgram.id = :programId AND m.isPublished = true ORDER BY m.orderIndex")
    List<ProgramModuleEntity> findPublishedByProgramIdOrderByOrderIndex(@Param("programId") Integer programId);
    
    @Query("SELECT MAX(m.orderIndex) FROM ProgramModuleEntity m WHERE m.fitnessProgram.id = :programId")
    Integer findMaxOrderIndexByProgramId(@Param("programId") Integer programId);
}
