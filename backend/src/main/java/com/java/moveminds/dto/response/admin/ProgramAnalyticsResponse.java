package com.java.moveminds.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramAnalyticsResponse {
    
    private Long totalPrograms;
    private Long activePrograms;
    private Long inactivePrograms;
    private Long totalEnrollments;
    private Double averageRating;
    private BigDecimal totalRevenue;
    
    private List<CategoryAnalytics> categoryAnalytics;
    private List<DifficultyAnalytics> difficultyAnalytics;
    private List<ProgramPerformance> topPerformingPrograms;
    private List<ProgramPerformance> recentPrograms;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryAnalytics {
        private String category;
        private Long programCount;
        private Long enrollmentCount;
        private Double averageRating;
        private BigDecimal totalRevenue;
        private Double marketShare;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DifficultyAnalytics {
        private String difficulty;
        private Long programCount;
        private Long enrollmentCount;
        private Double averageRating;
        private BigDecimal totalRevenue;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProgramPerformance {
        private Integer id;
        private String name;
        private String instructorName;
        private String category;
        private String difficulty;
        private Long enrollmentCount;
        private Double averageRating;
        private BigDecimal revenue;
        private String createdAt;
    }
}
