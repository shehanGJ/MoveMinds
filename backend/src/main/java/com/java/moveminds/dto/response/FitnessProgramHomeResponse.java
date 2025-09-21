package com.java.moveminds.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.java.moveminds.enums.DifficultyLevel;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FitnessProgramHomeResponse {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer duration;
    private DifficultyLevel difficultyLevel;
    private List<String> images;
    private Integer instructorId;
    private String instructorName;
    private String locationName;
}
