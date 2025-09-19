package com.java.moveminds.repositories;

import com.java.moveminds.entities.FitnessProgramEntity;
import com.java.moveminds.entities.UserEntity;
import com.java.moveminds.entities.UserProgramEntity;
import com.java.moveminds.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserProgramEntityRepository extends JpaRepository<UserProgramEntity, Integer> {
    Page<UserProgramEntity> findAllByUserByUserId(UserEntity user, Pageable pageable);
    boolean existsByUserByUserIdAndFitnessProgramByProgramId(UserEntity user, FitnessProgramEntity fitnessProgram);
    
    // Instructor service methods
    long countByFitnessProgramByProgramId_User(UserEntity instructor);
    long countByFitnessProgramByProgramId_UserAndStatus(UserEntity instructor, Status status);
    Page<UserProgramEntity> findByFitnessProgramByProgramId_User(UserEntity instructor, Pageable pageable);
    
    // Enhanced methods for instructor student management
    @Query("SELECT up FROM UserProgramEntity up WHERE up.fitnessProgramByProgramId.user.id = :instructorId")
    Page<UserProgramEntity> findByInstructorId(@Param("instructorId") Integer instructorId, Pageable pageable);
    
    @Query("SELECT up FROM UserProgramEntity up WHERE up.fitnessProgramByProgramId.id = :programId")
    List<UserProgramEntity> findByProgramId(@Param("programId") Integer programId);
    
    // Analytics methods - placeholder methods since UserProgramEntity doesn't have createdAt field
    // These would need to be implemented with custom queries if date tracking is needed
    long countByFitnessProgramByProgramId(FitnessProgramEntity program);
}
