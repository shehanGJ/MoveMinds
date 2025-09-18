package com.java.moveminds.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.java.moveminds.entities.CityEntity;
import com.java.moveminds.repositories.CityEntityRepository;
import com.java.moveminds.services.CityService;
import com.java.moveminds.services.LogService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {

    private final CityEntityRepository repository;
    private final LogService logService;

    @Override
    public CityEntity addCity(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("City name cannot be empty.");
        }

        logService.log(null, "Dodavanje grada " + name);

        return this.repository.saveAndFlush(new CityEntity(name));
    }

    @Override
    public CityEntity getCityById(int id) {
        logService.log(null, "Prikaz grada sa id " + id);
        return this.repository.findById(id).orElse(null);
    }

    @Override
    public List<CityEntity> getCities() {
        logService.log(null, "Prikaz svih gradova");
        return this.repository.findAll();
    }
}

