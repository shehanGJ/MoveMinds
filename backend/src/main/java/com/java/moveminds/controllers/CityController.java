package com.java.moveminds.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.java.moveminds.models.dto.requests.CityRequest;
import com.java.moveminds.models.entities.CityEntity;
import com.java.moveminds.services.CityService;

import java.util.List;

@RestController
@RequestMapping("/cities")
@RequiredArgsConstructor
public class CityController {
    private final CityService cityService;

    // Endpoint to get all cities
    @GetMapping
    public List<CityEntity> getAllCities() {
        return this.cityService.getCities();
    }

    // Endpoint to add a new city
    @PostMapping
    public CityEntity addCity(@RequestBody CityRequest request) {
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new IllegalArgumentException("Incomplete information about the city!");
        }
        return this.cityService.addCity(request.getName());
    }
}
