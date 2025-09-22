package com.java.moveminds.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ProgramLessonRequest {
    @NotBlank(message = "Lesson title is required")
    private String title;
    
    private String description;
    
    private String content;
    
    private String videoUrl;
    
    @Min(value = 0, message = "Duration must be non-negative")
    private Integer durationMinutes;
    
    @Min(value = 0, message = "Order index must be non-negative")
    private Integer orderIndex;
    
    private Boolean isPublished = false;
    
    private Boolean isPreview = false;
}
