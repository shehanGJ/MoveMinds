package com.java.moveminds.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ProgramResourceRequest {
    @NotBlank(message = "Resource title is required")
    private String title;
    
    private String description;
    
    private String fileUrl;
    
    @NotBlank(message = "File type is required")
    private String fileType;
    
    @Min(value = 0, message = "File size must be non-negative")
    private Long fileSizeBytes;
    
    @Min(value = 0, message = "Order index must be non-negative")
    private Integer orderIndex;
}
