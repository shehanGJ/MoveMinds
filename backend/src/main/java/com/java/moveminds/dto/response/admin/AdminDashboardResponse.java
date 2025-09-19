package com.java.moveminds.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {
    
    // User Statistics
    private Long totalUsers;
    private Long activeUsers;
    private Long inactiveUsers;
    private Long newUsersThisMonth;
    private Long newUsersThisWeek;
    
    // Role-based Statistics
    private Long totalAdmins;
    private Long totalInstructors;
    private Long totalRegularUsers;
    
    // Program Statistics
    private Long totalPrograms;
    private Long activePrograms;
    private Long inactivePrograms;
    private Long programsCreatedThisMonth;
    
    // Enrollment Statistics
    private Long totalEnrollments;
    private Long activeEnrollments;
    private Long completedEnrollments;
    private Long enrollmentsThisMonth;
    
    // Revenue Statistics
    private BigDecimal totalRevenue;
    private BigDecimal monthlyRevenue;
    private BigDecimal weeklyRevenue;
    private BigDecimal averageProgramPrice;
    
    // System Statistics
    private Long totalCategories;
    private Long totalLocations;
    private Long totalCities;
    private Long totalComments;
    private Long totalMessages;
    
    // Recent Activity
    private List<RecentActivity> recentActivities;
    private List<TopInstructor> topInstructors;
    private List<PopularProgram> popularPrograms;
    private List<SystemAlert> systemAlerts;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentActivity {
        private String type;
        private String description;
        private String user;
        private LocalDateTime timestamp;
        private String severity;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopInstructor {
        private Integer instructorId;
        private String instructorName;
        private Long programCount;
        private Long studentCount;
        private BigDecimal totalRevenue;
        private Double averageRating;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PopularProgram {
        private Integer programId;
        private String programName;
        private String instructorName;
        private Long enrollmentCount;
        private BigDecimal revenue;
        private Double averageRating;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SystemAlert {
        private String type;
        private String message;
        private String severity;
        private LocalDateTime timestamp;
        private Boolean isResolved;
    }
}
