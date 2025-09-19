package com.java.moveminds.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsResponse {
    
    // User Statistics
    private Long totalUsers;
    private Long activeUsers;
    private Long totalInstructors;
    private Long totalAdmins;
    
    // Program Statistics
    private Long totalPrograms;
    private Long activePrograms;
    
    // Enrollment Statistics
    private Long totalEnrollments;
    private Long activeEnrollments;
    
    // Revenue Statistics
    private BigDecimal totalRevenue;
    private BigDecimal monthlyRevenue;
}