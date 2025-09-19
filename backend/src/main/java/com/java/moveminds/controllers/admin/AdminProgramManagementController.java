package com.java.moveminds.controllers.admin;

import com.java.moveminds.dto.response.AdminProgramResponse;
import com.java.moveminds.services.admin.AdminProgramManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * REST Controller for admin program management operations.
 */
@Slf4j
@RestController
@RequestMapping("/admin/programs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminProgramManagementController {
    
    private final AdminProgramManagementService adminProgramManagementService;
    
    /**
     * Get paginated list of programs with advanced filtering
     */
    @GetMapping
    public ResponseEntity<Page<AdminProgramResponse>> getAllPrograms(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "id") String sort,
            @RequestParam(value = "direction", defaultValue = "desc") String direction,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "difficulty", required = false) String difficulty,
            Principal principal) {
        
        log.info("Admin {} requesting programs list with filters: search={}, category={}, difficulty={}", 
                principal.getName(), search, category, difficulty);
        
        Sort sortObj = Sort.by(Sort.Direction.fromString(direction), sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);
        
        Page<AdminProgramResponse> programs = adminProgramManagementService.getAllPrograms(
                principal, pageable, search, category, difficulty);
        
        return ResponseEntity.ok(programs);
    }
    
    /**
     * Get program details by ID
     */
    @GetMapping("/{programId}")
    public ResponseEntity<AdminProgramResponse> getProgramById(
            @PathVariable Integer programId,
            Principal principal) {
        
        log.info("Admin {} requesting program details for ID: {}", principal.getName(), programId);
        
        AdminProgramResponse program = adminProgramManagementService.getProgramById(principal, programId);
        return ResponseEntity.ok(program);
    }
    
    /**
     * Delete a program
     */
    @DeleteMapping("/{programId}")
    public ResponseEntity<Void> deleteProgram(
            @PathVariable Integer programId,
            Principal principal) {
        
        log.info("Admin {} deleting program ID: {}", principal.getName(), programId);
        
        adminProgramManagementService.deleteProgram(principal, programId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Update program status (activate/deactivate)
     */
    @PutMapping("/{programId}/status")
    public ResponseEntity<AdminProgramResponse> updateProgramStatus(
            @PathVariable Integer programId,
            @RequestParam boolean isActive,
            Principal principal) {
        
        log.info("Admin {} updating program ID: {} status to: {}", 
                principal.getName(), programId, isActive);
        
        AdminProgramResponse updatedProgram = adminProgramManagementService.updateProgramStatus(
                principal, programId, isActive);
        return ResponseEntity.ok(updatedProgram);
    }
    
    /**
     * Get program statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Object> getProgramStatistics(Principal principal) {
        
        log.info("Admin {} requesting program statistics", principal.getName());
        
        Object statistics = adminProgramManagementService.getProgramStatistics(principal);
        return ResponseEntity.ok(statistics);
    }
}
