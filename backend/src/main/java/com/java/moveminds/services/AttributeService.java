package com.java.moveminds.services;

import org.springframework.stereotype.Service;
import com.java.moveminds.dto.AttributeDTO;

import java.util.List;

@Service
public interface AttributeService {
    List<AttributeDTO> getAttributesByCategoryId(Integer categoryId);
    List<AttributeDTO> getAllAttributesWithValues();
}
