package com.java.moveminds.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.java.moveminds.enums.DifficultyLevel;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FitnessProgramRequest {
    private Integer categoryId;
    private Integer locationId;
    private String name;
    private String description;
    private Integer duration;
    private DifficultyLevel difficultyLevel;
    private BigDecimal price;
    private String youtubeUrl;
    private List<SpecificAttribute> specificAttributes;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SpecificAttribute {
        private Integer attributeName;
        private Integer attributeValue;
    }
}
