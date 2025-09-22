package com.java.moveminds.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserProgramProgressResponse {
    
    private Integer id;
    private Integer userId;
    private Integer programId;
    private String programName;
    private Integer totalLessons;
    private Integer completedLessons;
    private Double progressPercentage;
    private Integer totalWatchTimeSeconds;
    private LocalDateTime lastAccessedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Boolean isProgramCompleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
