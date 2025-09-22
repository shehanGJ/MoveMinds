package com.java.moveminds.dto.response;

import lombok.Data;

@Data
public class UserProgressStatsResponse {
    
    private Integer totalProgramsEnrolled;
    private Integer completedPrograms;
    private Integer inProgressPrograms;
    private Double averageProgressPercentage;
    private Integer totalLessonsCompleted;
    private Integer totalWatchTimeHours;
    private Long currentStreakDays;
    private Long longestStreakDays;
}
