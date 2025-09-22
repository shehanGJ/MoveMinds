package com.java.moveminds.repositories;

import com.java.moveminds.entities.UserProgressEntity;
import com.java.moveminds.entities.UserEntity;
import com.java.moveminds.entities.FitnessProgramEntity;
import com.java.moveminds.entities.ProgramLessonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProgressEntityRepository extends JpaRepository<UserProgressEntity, Integer> {
    
    // Find progress for a specific user and lesson
    Optional<UserProgressEntity> findByUserAndLesson(UserEntity user, ProgramLessonEntity lesson);
    
    // Find all progress for a user in a specific program
    List<UserProgressEntity> findByUserAndFitnessProgram(UserEntity user, FitnessProgramEntity fitnessProgram);
    
    // Find all completed lessons for a user in a program
    @Query("SELECT up FROM UserProgressEntity up WHERE up.user = :user AND up.fitnessProgram = :program AND up.isCompleted = true")
    List<UserProgressEntity> findCompletedLessonsByUserAndProgram(@Param("user") UserEntity user, @Param("program") FitnessProgramEntity program);
    
    // Count completed lessons for a user in a program
    @Query("SELECT COUNT(up) FROM UserProgressEntity up WHERE up.user = :user AND up.fitnessProgram = :program AND up.isCompleted = true")
    Long countCompletedLessonsByUserAndProgram(@Param("user") UserEntity user, @Param("program") FitnessProgramEntity program);
    
    // Check if a specific lesson is completed by user
    @Query("SELECT CASE WHEN COUNT(up) > 0 THEN true ELSE false END FROM UserProgressEntity up WHERE up.user = :user AND up.lesson = :lesson AND up.isCompleted = true")
    Boolean isLessonCompletedByUser(@Param("user") UserEntity user, @Param("lesson") ProgramLessonEntity lesson);
    
    // Get progress for all lessons in a program
    @Query("SELECT up FROM UserProgressEntity up WHERE up.user = :user AND up.fitnessProgram = :program")
    List<UserProgressEntity> findAllProgressByUserAndProgram(@Param("user") UserEntity user, @Param("program") FitnessProgramEntity program);
    
    // Find progress by user ID and lesson ID
    Optional<UserProgressEntity> findByUserIdAndLessonId(Integer userId, Integer lessonId);
    
    // Find progress by user ID and program ID
    List<UserProgressEntity> findByUserIdAndFitnessProgramId(Integer userId, Integer programId);
}
