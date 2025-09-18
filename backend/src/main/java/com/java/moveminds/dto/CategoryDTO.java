package com.java.moveminds.dto;

import lombok.Data;
import java.util.List;

@Data
public class CategoryDTO {
    private Integer id;
    private String name;
    private String description;
    private List<AttributeDTO> attributes;
}
