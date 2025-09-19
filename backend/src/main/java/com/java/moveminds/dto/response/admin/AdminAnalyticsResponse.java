package com.java.moveminds.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAnalyticsResponse {
    
    // Overview metrics
    private Long totalUsers;
    private Long totalInstructors;
    private Long totalPrograms;
    private Long activePrograms;
    private Long inactivePrograms;
    private Long totalEnrollments;
    private BigDecimal totalRevenue;
    private Double averageRating;
    
    // Growth metrics
    private Long newUsersThisMonth;
    private Long newProgramsThisMonth;
    private Long newEnrollmentsThisMonth;
    private BigDecimal revenueThisMonth;
    
    // Chart data
    private List<ChartDataPoint> userGrowthChart;
    private List<ChartDataPoint> programEnrollmentChart;
    private List<ChartDataPoint> revenueChart;
    private List<CategoryDistribution> categoryDistribution;
    private List<DifficultyDistribution> difficultyDistribution;
    
    // Top performers
    private List<TopInstructor> topInstructors;
    private List<TopProgram> topPrograms;
    
    // System health
    private SystemHealthMetrics systemHealth;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChartDataPoint {
        private String label;
        private Long value;
        private String date;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryDistribution {
        private String category;
        private Long count;
        private Double percentage;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DifficultyDistribution {
        private String difficulty;
        private Long count;
        private Double percentage;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopInstructor {
        private Integer id;
        private String name;
        private String email;
        private Long programCount;
        private Long enrollmentCount;
        private Double averageRating;
        private BigDecimal totalRevenue;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopProgram {
        private Integer id;
        private String name;
        private String instructorName;
        private String category;
        private Long enrollmentCount;
        private Double averageRating;
        private BigDecimal revenue;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SystemHealthMetrics {
        private Double serverUptime;
        private Long activeUsers;
        private Long totalRequests;
        private Double averageResponseTime;
        private String databaseStatus;
        private String lastBackup;
    }
}
