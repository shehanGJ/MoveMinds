package com.java.moveminds.repositories;

import com.java.moveminds.entities.FitnessProgramEntity;
import com.java.moveminds.entities.UserEntity;
import com.java.moveminds.entities.UserProgramEntity;
import com.java.moveminds.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProgramEntityRepository extends JpaRepository<UserProgramEntity, Integer> {
    Page<UserProgramEntity> findAllByUserByUserId(UserEntity user, Pageable pageable);
    boolean existsByUserByUserIdAndFitnessProgramByProgramId(UserEntity user, FitnessProgramEntity fitnessProgram);
    
    // Instructor service methods
    long countByFitnessProgramByProgramId_User(UserEntity instructor);
    long countByFitnessProgramByProgramId_UserAndStatus(UserEntity instructor, Status status);
    Page<UserProgramEntity> findByFitnessProgramByProgramId_User(UserEntity instructor, Pageable pageable);
}
