package com.java.moveminds.dto.response;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgramLearningContentResponse {
    private Integer id;
    private String name;
    private String description;
    private String difficultyLevel;
    private Integer duration;
    private BigDecimal price;
    private String instructorName;
    private String instructorAvatarUrl;
    private String categoryName;
    private String locationName;
    private LocalDateTime createdAt;
    private List<ProgramModuleResponse> modules;
    private Integer totalLessons;
    private Integer totalDurationMinutes;
    private Integer completedLessons;
    private Double progressPercentage;
}
