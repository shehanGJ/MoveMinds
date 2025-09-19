package com.java.moveminds.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramEnrollmentResponse {
    
    private Integer enrollmentId;
    private Integer userId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String avatarUrl;
    
    private Integer programId;
    private String programName;
    private String programDescription;
    private String instructorName;
    
    private String status;
    private LocalDateTime enrolledAt;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    private Integer progress;
    private BigDecimal amount;
    private String notes;
    private String feedback;
    
    // Additional fields for instructor view
    private String difficultyLevel;
    private Integer duration;
    private LocalDateTime lastActivityAt;
    private Double programRating;
}