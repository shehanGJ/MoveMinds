package com.java.moveminds.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.java.moveminds.entities.LocationEntity;

import java.util.List;

@Service
public interface LocationService {
    @Transactional
    LocationEntity addLocation(String name);
    List<LocationEntity> listLocations();
}
