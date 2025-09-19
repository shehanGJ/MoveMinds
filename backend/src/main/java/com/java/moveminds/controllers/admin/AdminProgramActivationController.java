package com.java.moveminds.controllers.admin;

import com.java.moveminds.dto.requests.admin.ProgramActivationRequest;
import com.java.moveminds.dto.response.admin.ProgramActivationResponse;
import com.java.moveminds.dto.response.AdminProgramResponse;
import com.java.moveminds.services.admin.AdminProgramActivationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/admin/programs/activation")
@RequiredArgsConstructor
public class AdminProgramActivationController {
    
    private final AdminProgramActivationService programActivationService;
    
    /**
     * Activate or deactivate a program
     */
    @PostMapping("/toggle")
    public ResponseEntity<ProgramActivationResponse> activateProgram(
            @Valid @RequestBody ProgramActivationRequest request,
            Authentication authentication) {
        
        String adminUsername = authentication.getName();
        log.info("Admin {} requesting to {} program {}", 
                adminUsername, request.getIsActive() ? "activate" : "deactivate", request.getProgramId());
        
        ProgramActivationResponse response = programActivationService.activateProgram(request, adminUsername);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get all active programs
     */
    @GetMapping("/active")
    public ResponseEntity<Page<AdminProgramResponse>> getActivePrograms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<AdminProgramResponse> activePrograms = programActivationService.getActivePrograms(pageable);
        return ResponseEntity.ok(activePrograms);
    }
    
    /**
     * Get all inactive programs
     */
    @GetMapping("/inactive")
    public ResponseEntity<Page<AdminProgramResponse>> getInactivePrograms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<AdminProgramResponse> inactivePrograms = programActivationService.getInactivePrograms(pageable);
        return ResponseEntity.ok(inactivePrograms);
    }
    
    /**
     * Get programs pending activation (recently created inactive programs)
     */
    @GetMapping("/pending")
    public ResponseEntity<Page<AdminProgramResponse>> getProgramsPendingActivation(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<AdminProgramResponse> pendingPrograms = programActivationService.getProgramsPendingActivation(pageable);
        return ResponseEntity.ok(pendingPrograms);
    }
    
    /**
     * Get program activation statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<AdminProgramActivationService.ProgramActivationStats> getProgramActivationStats() {
        AdminProgramActivationService.ProgramActivationStats stats = programActivationService.getProgramActivationStats();
        return ResponseEntity.ok(stats);
    }
}
