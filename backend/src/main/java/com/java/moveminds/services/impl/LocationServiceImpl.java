package com.java.moveminds.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.java.moveminds.exceptions.LocationAlreadyExistsException;
import com.java.moveminds.models.entities.LocationEntity;
import com.java.moveminds.repositories.LocationEntityRepository;
import com.java.moveminds.services.LocationService;
import com.java.moveminds.services.LogService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationEntityRepository locationRepository;
    private final LogService logService;

    @Override
    @Transactional
    public LocationEntity addLocation(String name) {
        Optional<LocationEntity> existingLocation = locationRepository.findByName(name);
        if (existingLocation.isPresent()) {
            throw new LocationAlreadyExistsException("Named location '" + name + "' it already exists.");
        }
        LocationEntity locationEntity = new LocationEntity();
        locationEntity.setName(name);
        locationRepository.saveAndFlush(locationEntity);

        logService.log(null, "Adding a location");

        return locationEntity;
    }

    @Override
    public List<LocationEntity> listLocations() {
        logService.log(null, "View all locations");
        return locationRepository.findAll();
    }
}
