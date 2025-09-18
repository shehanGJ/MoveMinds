package com.java.moveminds.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsResponse {
    private long totalUsers;
    private long totalInstructors;
    private long totalAdmins;
    private long totalPrograms;
    private long totalEnrollments;
    private long totalActivities;
    private long activeUsers;
    private long inactiveUsers;
    private long newUsersThisMonth;
    private long newProgramsThisMonth;
    private long totalRevenue;
    private long monthlyRevenue;
}
