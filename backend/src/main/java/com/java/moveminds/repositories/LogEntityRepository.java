package com.java.moveminds.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.java.moveminds.entities.LogEntity;

@Repository
public interface LogEntityRepository extends JpaRepository<LogEntity, Integer> {
}
