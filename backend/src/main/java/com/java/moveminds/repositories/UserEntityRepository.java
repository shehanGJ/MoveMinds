package com.java.moveminds.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.java.moveminds.entities.UserEntity;
import com.java.moveminds.enums.Roles;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Integer>, JpaSpecificationExecutor<UserEntity> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUsername(String username);
    List<UserEntity> findAllByRole(Roles role);
    List<UserEntity> findAllByRoleNotAndUsernameNot(Roles role, String username);
    
    // Admin service methods
    long countByRole(Roles role);
    long countByIsVerified(boolean isVerified);
    long countByIsVerifiedTrue();
    long countByIsVerifiedFalse();
    Page<UserEntity> findByRole(Roles role, Pageable pageable);
    Page<UserEntity> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
    Page<UserEntity> findByRoleAndUsernameContainingIgnoreCase(Roles role, String username, Pageable pageable);
    List<UserEntity> findByRole(Roles role);
    
    // Enhanced query methods for admin operations
    @Query("SELECT u FROM UserEntity u WHERE u.role = :role AND u.isVerified = :isVerified")
    List<UserEntity> findByRoleAndIsVerified(@Param("role") Roles role, @Param("isVerified") boolean isVerified);
    
    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.role = :role AND u.isVerified = true")
    long countActiveUsersByRole(@Param("role") Roles role);
    
    @Query("SELECT u FROM UserEntity u WHERE u.role = :role ORDER BY u.id DESC")
    List<UserEntity> findByRoleOrderByCreatedAtDesc(@Param("role") Roles role);
    
    // Count methods for admin dashboard
    @Query("SELECT COUNT(fp) FROM FitnessProgramEntity fp WHERE fp.user.id = :userId")
    long countFitnessProgramsByInstructorId(@Param("userId") Integer userId);
    
    @Query("SELECT COUNT(up) FROM UserProgramEntity up WHERE up.userByUserId.id = :userId")
    long countUserProgramsByUserId(@Param("userId") Integer userId);
    
    // Analytics methods - placeholder methods since UserEntity doesn't have createdAt/lastLoginAt fields
    // These would need to be implemented with custom queries if date tracking is needed
}
