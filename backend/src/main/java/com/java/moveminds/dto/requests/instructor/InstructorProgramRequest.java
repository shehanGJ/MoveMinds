package com.java.moveminds.dto.requests.instructor;

import com.java.moveminds.enums.DifficultyLevel;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstructorProgramRequest {
    
    @NotBlank(message = "Program name is required")
    @Size(min = 3, max = 100, message = "Program name must be between 3 and 100 characters")
    private String name;
    
    @NotBlank(message = "Program description is required")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;
    
    @NotNull(message = "Difficulty level is required")
    private DifficultyLevel difficultyLevel;
    
    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 week")
    @Max(value = 52, message = "Duration cannot exceed 52 weeks")
    private Integer duration;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price cannot be negative")
    @DecimalMax(value = "9999.99", message = "Price cannot exceed 9999.99")
    private BigDecimal price;
    
    @NotNull(message = "Category ID is required")
    private Integer categoryId;
    
    @NotNull(message = "Location ID is required")
    private Integer locationId;
    
    private String youtubeUrl;
    private List<String> attributes;
    private List<Integer> imageIds;
    private Boolean isActive;
}
