package com.java.moveminds.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.java.moveminds.models.entities.UserEntity;
import com.java.moveminds.models.enums.Roles;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUsername(String username);
    List<UserEntity> findAllByRole(Roles role);
    List<UserEntity> findAllByRoleNotAndUsernameNot(Roles role, String username);
}
