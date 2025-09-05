package com.java.moveminds.services.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.java.moveminds.models.dto.AttributeDTO;
import com.java.moveminds.models.dto.AttributeValueDTO;
import com.java.moveminds.repositories.AttributeEntityRepository;
import com.java.moveminds.services.AttributeService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttributeServiceImpl implements AttributeService {

    private final AttributeEntityRepository attributeRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<AttributeDTO> getAttributesByCategoryId(Integer categoryId) {
        return this.attributeRepository
                .findByCategoryId(categoryId)
                .stream()
                .map(attribute -> modelMapper.map(attribute, AttributeDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<AttributeDTO> getAllAttributesWithValues() {
        return attributeRepository.findDistinctByAttributeValues_ProgramAttributes_FitnessProgramIsNotNull()
                .stream()
                .map(attribute -> {
                    AttributeDTO dto = new AttributeDTO();
                    dto.setId(attribute.getId());
                    dto.setName(attribute.getName());
                    dto.setDescription(attribute.getDescription());

                    List<AttributeValueDTO> values = attribute
                            .getAttributeValues()
                            .stream()
                            .filter(value -> !value.getProgramAttributes().isEmpty())
                            .map(value -> new AttributeValueDTO(value.getId(), value.getName()))
                            .collect(Collectors.toList());
                    dto.setValues(values);
                    return dto;
                }).collect(Collectors.toList());
    }
}
