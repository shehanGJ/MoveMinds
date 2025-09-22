package com.java.moveminds.repositories;

import com.java.moveminds.entities.UserProgramProgressEntity;
import com.java.moveminds.entities.UserEntity;
import com.java.moveminds.entities.FitnessProgramEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProgramProgressEntityRepository extends JpaRepository<UserProgramProgressEntity, Integer> {
    
    // Find program progress for a specific user and program
    Optional<UserProgramProgressEntity> findByUserAndFitnessProgram(UserEntity user, FitnessProgramEntity fitnessProgram);
    
    // Find program progress by user ID and program ID
    Optional<UserProgramProgressEntity> findByUserIdAndFitnessProgramId(Integer userId, Integer programId);
    
    // Find all program progress for a user
    List<UserProgramProgressEntity> findByUser(UserEntity user);
    
    // Find all program progress by user ID
    List<UserProgramProgressEntity> findByUserId(Integer userId);
    
    // Find completed programs for a user
    @Query("SELECT upp FROM UserProgramProgressEntity upp WHERE upp.user = :user AND upp.isProgramCompleted = true")
    List<UserProgramProgressEntity> findCompletedProgramsByUser(@Param("user") UserEntity user);
    
    // Find in-progress programs for a user
    @Query("SELECT upp FROM UserProgramProgressEntity upp WHERE upp.user = :user AND upp.isProgramCompleted = false AND upp.completedLessons > 0")
    List<UserProgramProgressEntity> findInProgressProgramsByUser(@Param("user") UserEntity user);
    
    // Count total programs enrolled by user
    @Query("SELECT COUNT(upp) FROM UserProgramProgressEntity upp WHERE upp.user = :user")
    Long countProgramsByUser(@Param("user") UserEntity user);
    
    // Count completed programs by user
    @Query("SELECT COUNT(upp) FROM UserProgramProgressEntity upp WHERE upp.user = :user AND upp.isProgramCompleted = true")
    Long countCompletedProgramsByUser(@Param("user") UserEntity user);
    
    // Get average progress across all user programs
    @Query("SELECT AVG(upp.progressPercentage) FROM UserProgramProgressEntity upp WHERE upp.user = :user")
    Double getAverageProgressByUser(@Param("user") UserEntity user);
}
