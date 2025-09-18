package com.java.moveminds.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.java.moveminds.entities.CityEntity;

@Repository
public interface CityEntityRepository  extends JpaRepository<CityEntity, Integer> {
}
