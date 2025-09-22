package com.java.moveminds.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class MarkLessonCompleteRequest {
    
    @NotNull(message = "Lesson ID is required")
    private Integer lessonId;
    
    @Min(value = 0, message = "Watch time must be non-negative")
    private Integer watchTimeSeconds = 0;
}
