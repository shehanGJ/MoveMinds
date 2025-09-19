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
public class InstructorStatsResponse {
    
    // Program Statistics
    private Long totalPrograms;
    private Long activePrograms;
    
    // Student Statistics
    private Long totalStudents;
    private Long activeStudents;
    
    // Enrollment Statistics
    private Long totalEnrollments;
    private Long activeEnrollments;
    
    // Revenue Statistics
    private BigDecimal totalRevenue;
    private BigDecimal monthlyRevenue;
    
    // Performance Statistics
    private Double averageRating;
}