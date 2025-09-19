package com.java.moveminds.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.java.moveminds.dto.response.InstructorStatsResponse;
import com.java.moveminds.services.InstructorService;
import com.java.moveminds.services.instructor.InstructorProgramManagementService;
import com.java.moveminds.services.instructor.InstructorStudentManagementService;
import com.java.moveminds.services.instructor.InstructorDashboardService;

import java.security.Principal;

@RestController
@RequestMapping("/instructor")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
public class InstructorController {
    
    private final InstructorService instructorService;
    private final InstructorProgramManagementService instructorProgramManagementService;
    private final InstructorStudentManagementService instructorStudentManagementService;
    private final InstructorDashboardService instructorDashboardService;

    /**
     * Get instructor dashboard statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<InstructorStatsResponse> getInstructorStats(Principal principal) {
        InstructorStatsResponse stats = instructorService.getInstructorStats(principal);
        return ResponseEntity.ok(stats);
    }

}
