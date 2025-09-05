package com.java.moveminds.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.java.moveminds.models.entities.CityEntity;
import com.java.moveminds.services.CityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class DatabaseTestController {
    
    private final CityService cityService;
    
    @GetMapping("/database")
    public ResponseEntity<Map<String, Object>> testDatabaseConnection() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Test database connection by fetching cities
            List<CityEntity> cities = cityService.getCities();
            
            response.put("status", "success");
            response.put("message", "Database connection successful");
            response.put("citiesCount", cities.size());
            response.put("cities", cities);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Database connection failed: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("database", "MySQL");
        response.put("timestamp", java.time.Instant.now().toString());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/test-logging")
    public ResponseEntity<Map<String, String>> testLogging() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Test logging endpoint called");
        response.put("timestamp", java.time.Instant.now().toString());
        
        // This will trigger the aspect logging
        return ResponseEntity.ok(response);
    }
}
