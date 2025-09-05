package com.java.moveminds.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.java.moveminds.models.entities.UserEntity;
import com.java.moveminds.models.entities.UserProgramEntity;

@Repository
public interface UserProgramEntityRepository extends JpaRepository<UserProgramEntity, Integer> {
    Page<UserProgramEntity> findAllByUserByUserId(UserEntity user, Pageable pageable);
}
