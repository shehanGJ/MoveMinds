package com.java.moveminds.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.java.moveminds.entities.UserEntity;
import com.java.moveminds.enums.Roles;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUsername(String username);
    List<UserEntity> findAllByRole(Roles role);
    List<UserEntity> findAllByRoleNotAndUsernameNot(Roles role, String username);
    
    // Admin service methods
    long countByRole(Roles role);
    long countByIsActivated(boolean isActivated);
    Page<UserEntity> findByRole(Roles role, Pageable pageable);
    Page<UserEntity> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
    Page<UserEntity> findByRoleAndUsernameContainingIgnoreCase(Roles role, String username, Pageable pageable);
}
