package com.java.moveminds.dto;

import lombok.Data;

import java.util.List;

@Data
public class AttributeDTO {
    private Integer id;
    private String name;
    private String description;
    private List<AttributeValueDTO> values;
    private AttributeValueDTO selectedValue;
}