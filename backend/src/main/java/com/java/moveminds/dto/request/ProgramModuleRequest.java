package com.java.moveminds.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ProgramModuleRequest {
    @NotBlank(message = "Module title is required")
    private String title;
    
    private String description;
    
    @Min(value = 0, message = "Order index must be non-negative")
    private Integer orderIndex;
    
    private Boolean isPublished = false;
}
