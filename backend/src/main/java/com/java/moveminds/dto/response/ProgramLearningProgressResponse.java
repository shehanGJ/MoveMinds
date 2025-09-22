package com.java.moveminds.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class ProgramLearningProgressResponse {
    
    private Integer programId;
    private String programName;
    private Integer totalLessons;
    private Integer completedLessons;
    private Double progressPercentage;
    private Integer totalWatchTimeSeconds;
    private Boolean isProgramCompleted;
    private List<LessonProgressResponse> lessonProgress;
    
    @Data
    public static class LessonProgressResponse {
        private Integer lessonId;
        private String lessonTitle;
        private String moduleTitle;
        private Boolean isCompleted;
        private Integer watchTimeSeconds;
        private Integer durationMinutes;
        private Boolean isPreview;
    }
}
