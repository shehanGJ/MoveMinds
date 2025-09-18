package com.java.moveminds.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.java.moveminds.dto.AttributeDTO;
import com.java.moveminds.services.AttributeService;

import java.util.List;

@RestController
@RequestMapping("/attributes")
@RequiredArgsConstructor
public class AttributeController {

    private static final Logger logger = LoggerFactory.getLogger(AttributeController.class);
    private final AttributeService attributeService;

    // Endpoint to get all attributes with values
    @GetMapping
    public ResponseEntity<List<AttributeDTO>> getAllAttributesWithValues() {
        List<AttributeDTO> attributes = attributeService.getAllAttributesWithValues();
        return ResponseEntity.ok(attributes);
    }

    // Endpoint to get attributes by category ID
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<AttributeDTO>> getAttributesByCategoryId(@PathVariable Integer categoryId) {
        if (categoryId == null || categoryId <= 0) {
            logger.error("Category ID is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        logger.info("Received request to get attributes for category ID: {}", categoryId);
        try {
            List<AttributeDTO> attributes = attributeService.getAttributesByCategoryId(categoryId);
            logger.info("Found {} attributes for category ID: {}", attributes.size(), categoryId);
            return ResponseEntity.ok(attributes);
        } catch (Exception ex) {
            logger.error("An unexpected error occurred", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
