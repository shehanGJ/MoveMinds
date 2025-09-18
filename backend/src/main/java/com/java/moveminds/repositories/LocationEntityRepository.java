package com.java.moveminds.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.java.moveminds.entities.LocationEntity;

import java.util.Optional;

@Repository
public interface LocationEntityRepository extends JpaRepository<LocationEntity, Integer> {
    Optional<LocationEntity> findByName(String name);
}
