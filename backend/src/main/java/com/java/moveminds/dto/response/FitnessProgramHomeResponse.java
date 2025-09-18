package com.java.moveminds.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FitnessProgramHomeResponse {
    private Integer id;
    private String name;
    private BigDecimal price;
    private List<String> images;
    private Integer instructorId;
}
