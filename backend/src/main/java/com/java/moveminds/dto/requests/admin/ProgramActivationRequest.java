package com.java.moveminds.dto.requests.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramActivationRequest {
    
    @NotNull(message = "Program ID is required")
    private Integer programId;
    
    @NotNull(message = "Activation status is required")
    private Boolean isActive;
    
    private String adminNotes; // Optional notes from admin about activation/deactivation
}
