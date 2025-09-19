package com.java.moveminds.dto.response;

import com.java.moveminds.enums.DifficultyLevel;
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
public class AdminProgramResponse {
    
    private Integer id;
    private String name;
    private String description;
    private DifficultyLevel difficultyLevel;
    private Integer duration;
    private BigDecimal price;
    private String youtubeUrl;
    private LocalDateTime createdAt;
    
    // Instructor information
    private Integer instructorId;
    private String instructorName;
    private String instructorEmail;
    
    // Category information
    private Integer categoryId;
    private String categoryName;
    
    // Location information
    private Integer locationId;
    private String locationName;
    
    // Statistics
    private Long enrollmentCount;
    private Long commentCount;
    private Double averageRating;
    private String status;
    
    // Additional fields for admin view
    private String imageUrl;
    private boolean isActive;
}
