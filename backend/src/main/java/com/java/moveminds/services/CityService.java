package com.java.moveminds.services;

import org.springframework.stereotype.Service;
import com.java.moveminds.models.entities.CityEntity;

import java.util.List;

@Service
public interface CityService {
    CityEntity addCity(String name);
    CityEntity getCityById(int id);
    List<CityEntity> getCities();
}
