package com.java.moveminds.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.java.moveminds.enums.DifficultyLevel;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FitnessProgramListResponse {
    private Integer id;
    private String name;
    private String description;
    private Integer duration;
    private BigDecimal price;
    private DifficultyLevel difficultyLevel;
    private String youtubeUrl;
    private String locationName;
    private String status;
    private String instructorName;
    private Integer instructorId;
    private Integer purchaseId;
}
