package com.java.moveminds.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserProgressResponse {
    
    private Integer id;
    private Integer userId;
    private Integer programId;
    private Integer lessonId;
    private String lessonTitle;
    private String moduleTitle;
    private Boolean isCompleted;
    private LocalDateTime completedAt;
    private Integer watchTimeSeconds;
    private LocalDateTime lastWatchedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
