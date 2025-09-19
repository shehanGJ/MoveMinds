package com.java.moveminds.dto.response.instructor;

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
public class InstructorDashboardResponse {
    
    // Program Statistics
    private Long totalPrograms;
    private Long activePrograms;
    private Long draftPrograms;
    private Long programsCreatedThisMonth;
    
    // Student Statistics
    private Long totalStudents;
    private Long activeStudents;
    private Long completedStudents;
    private Long newStudentsThisMonth;
    private Long newStudentsThisWeek;
    
    // Enrollment Statistics
    private Long totalEnrollments;
    private Long activeEnrollments;
    private Long completedEnrollments;
    private Long pendingEnrollments;
    
    // Revenue Statistics
    private BigDecimal totalRevenue;
    private BigDecimal monthlyRevenue;
    private BigDecimal weeklyRevenue;
    private BigDecimal averageProgramPrice;
    private BigDecimal revenueGrowth;
    
    // Performance Metrics
    private Double averageRating;
    private Long totalReviews;
    private Long fiveStarReviews;
    private Long fourStarReviews;
    private Long threeStarReviews;
    private Long twoStarReviews;
    private Long oneStarReviews;
    
    // Recent Activity
    private List<RecentEnrollment> recentEnrollments;
    private List<ProgramPerformance> programPerformance;
    private List<StudentProgress> studentProgress;
    private List<RevenueTrend> revenueTrends;
    private List<UpcomingDeadline> upcomingDeadlines;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentEnrollment {
        private Integer enrollmentId;
        private String studentName;
        private String programName;
        private LocalDateTime enrollmentDate;
        private String status;
        private BigDecimal amount;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProgramPerformance {
        private Integer programId;
        private String programName;
        private Long enrollmentCount;
        private BigDecimal revenue;
        private Double rating;
        private Long reviewCount;
        private String status;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentProgress {
        private Integer studentId;
        private String studentName;
        private String programName;
        private Integer progressPercentage;
        private LocalDateTime lastActivity;
        private String status;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueTrend {
        private String period;
        private BigDecimal revenue;
        private Long enrollmentCount;
        private LocalDateTime date;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpcomingDeadline {
        private String type;
        private String description;
        private LocalDateTime deadline;
        private String priority;
        private Integer relatedId;
    }
}
