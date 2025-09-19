package com.java.moveminds.dto.requests.instructor;

import com.java.moveminds.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstructorStudentManagementRequest {
    
    @NotNull(message = "Enrollment ID is required")
    private Integer enrollmentId;
    
    @NotNull(message = "Status is required")
    private Status status;
    
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer progress;
    private String notes;
    private String feedback;
}
