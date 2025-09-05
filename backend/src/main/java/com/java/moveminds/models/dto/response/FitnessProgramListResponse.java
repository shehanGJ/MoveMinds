package com.java.moveminds.models.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.java.moveminds.models.enums.DifficultyLevel;

import java.math.BigDecimal;
import java.sql.Date;

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
    private Date startDate;
    private Date endDate;
    private String status;
    private String instructorName;
    private Integer instructorId;
    private Integer purchaseId;
}
