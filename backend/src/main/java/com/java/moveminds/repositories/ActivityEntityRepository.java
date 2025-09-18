package com.java.moveminds.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.java.moveminds.entities.ActivityEntity;
import com.java.moveminds.entities.UserEntity;

import java.util.List;

@Repository
public interface ActivityEntityRepository extends JpaRepository<ActivityEntity, Integer>{
    List<ActivityEntity> findAllByUser(UserEntity user);
}
