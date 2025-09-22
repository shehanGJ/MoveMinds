package com.java.moveminds.repositories;

import com.java.moveminds.entities.ProgramLessonEntity;
import com.java.moveminds.entities.ProgramModuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramLessonEntityRepository extends JpaRepository<ProgramLessonEntity, Integer> {
    
    List<ProgramLessonEntity> findByProgramModuleOrderByOrderIndex(ProgramModuleEntity programModule);
    
    List<ProgramLessonEntity> findByProgramModuleAndIsPublishedTrueOrderByOrderIndex(ProgramModuleEntity programModule);
    
    @Query("SELECT l FROM ProgramLessonEntity l WHERE l.programModule.id = :moduleId ORDER BY l.orderIndex")
    List<ProgramLessonEntity> findByModuleIdOrderByOrderIndex(@Param("moduleId") Integer moduleId);
    
    @Query("SELECT l FROM ProgramLessonEntity l WHERE l.programModule.id = :moduleId AND l.isPublished = true ORDER BY l.orderIndex")
    List<ProgramLessonEntity> findPublishedByModuleIdOrderByOrderIndex(@Param("moduleId") Integer moduleId);
    
    @Query("SELECT MAX(l.orderIndex) FROM ProgramLessonEntity l WHERE l.programModule.id = :moduleId")
    Integer findMaxOrderIndexByModuleId(@Param("moduleId") Integer moduleId);
    
    @Query("SELECT l FROM ProgramLessonEntity l WHERE l.programModule.fitnessProgram.id = :programId ORDER BY l.programModule.orderIndex, l.orderIndex")
    List<ProgramLessonEntity> findByProgramIdOrderByModuleAndLessonIndex(@Param("programId") Integer programId);
    
    @Query("SELECT l FROM ProgramLessonEntity l LEFT JOIN FETCH l.resources WHERE l.id IN :lessonIds ORDER BY l.orderIndex")
    List<ProgramLessonEntity> findByIdsWithResources(@Param("lessonIds") List<Integer> lessonIds);
}
