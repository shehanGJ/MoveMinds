package com.java.moveminds.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstructorStatsResponse {
    private long totalPrograms;
    private long totalStudents;
    private long totalEnrollments;
    private long activeEnrollments;
    private long completedEnrollments;
    private long totalRevenue;
    private long monthlyRevenue;
    private double averageRating;
    private long totalReviews;
    private long newEnrollmentsThisMonth;
    private long programsThisMonth;
}
