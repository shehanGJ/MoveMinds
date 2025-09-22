package com.java.moveminds.repositories;

import com.java.moveminds.entities.ProgramLessonEntity;
import com.java.moveminds.entities.ProgramResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramResourceEntityRepository extends JpaRepository<ProgramResourceEntity, Integer> {
    
    List<ProgramResourceEntity> findByProgramLessonOrderByOrderIndex(ProgramLessonEntity programLesson);
    
    @Query("SELECT r FROM ProgramResourceEntity r WHERE r.programLesson.id = :lessonId ORDER BY r.orderIndex")
    List<ProgramResourceEntity> findByLessonIdOrderByOrderIndex(@Param("lessonId") Integer lessonId);
    
    @Query("SELECT MAX(r.orderIndex) FROM ProgramResourceEntity r WHERE r.programLesson.id = :lessonId")
    Integer findMaxOrderIndexByLessonId(@Param("lessonId") Integer lessonId);
}
