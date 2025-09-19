package com.java.moveminds.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramActivationResponse {
    
    private Integer programId;
    private String programName;
    private Boolean isActive;
    private String adminNotes;
    private LocalDateTime activationDate;
    private String activatedBy; // Admin username
    private String message;
    
    // Additional program details for context
    private String instructorName;
    private String categoryName;
    private String difficultyLevel;
}
