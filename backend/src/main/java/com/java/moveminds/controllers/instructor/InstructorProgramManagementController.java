package com.java.moveminds.controllers.instructor;

import com.java.moveminds.dto.requests.instructor.InstructorProgramRequest;
import com.java.moveminds.dto.response.FitnessProgramListResponse;
import com.java.moveminds.dto.response.FitnessProgramResponse;
import com.java.moveminds.services.instructor.InstructorProgramManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

/**
 * REST Controller for instructor program management operations.
 * Handles all program-related operations for instructors.
 */
@Slf4j
@RestController
@RequestMapping("/instructor/programs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
public class InstructorProgramManagementController {
    
    private final InstructorProgramManagementService instructorProgramManagementService;
    
    /**
     * Create a new fitness program
     */
    @PostMapping
    public ResponseEntity<FitnessProgramResponse> createProgram(
            @RequestPart("program") @Valid InstructorProgramRequest programRequest,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            Principal principal) {
        
        log.info("Instructor {} creating new program: {}", principal.getName(), programRequest.getName());
        
        try {
            FitnessProgramResponse response = instructorProgramManagementService.createProgram(
                    principal, programRequest, files);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            log.error("Error creating program: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Update existing program
     */
    @PutMapping("/{programId}")
    public ResponseEntity<FitnessProgramResponse> updateProgram(
            @PathVariable Integer programId,
            @RequestPart("program") @Valid InstructorProgramRequest programRequest,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestPart(value = "removedImages", required = false) List<String> removedImages,
            Principal principal) {
        
        log.info("Instructor {} updating program ID: {}", principal.getName(), programId);
        
        try {
            FitnessProgramResponse response = instructorProgramManagementService.updateProgram(
                    principal, programId, programRequest, files, removedImages);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("Error updating program: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Delete program
     */
    @DeleteMapping("/{programId}")
    public ResponseEntity<Void> deleteProgram(
            @PathVariable Integer programId,
            Principal principal) {
        
        log.info("Instructor {} deleting program ID: {}", principal.getName(), programId);
        
        try {
            instructorProgramManagementService.deleteProgram(principal, programId);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            log.error("Error deleting program: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get instructor's programs with filtering and sorting
     */
    @GetMapping
    public ResponseEntity<Page<FitnessProgramListResponse>> getMyPrograms(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(value = "direction", defaultValue = "desc") String direction,
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "search", required = false) String search,
            Principal principal) {
        
        log.info("Instructor {} requesting programs: filter={}, search={}", 
                principal.getName(), filter, search);
        
        Sort sortObj = Sort.by(Sort.Direction.fromString(direction), sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);
        
        Page<FitnessProgramListResponse> programs = instructorProgramManagementService.getMyPrograms(
                principal, pageable, sort, filter, search);
        
        return ResponseEntity.ok(programs);
    }
    
    /**
     * Get program details
     */
    @GetMapping("/{programId}")
    public ResponseEntity<FitnessProgramResponse> getProgramDetails(
            @PathVariable Integer programId,
            Principal principal) {
        
        log.info("Instructor {} requesting program details for ID: {}", principal.getName(), programId);
        
        FitnessProgramResponse program = instructorProgramManagementService.getProgramDetails(principal, programId);
        return ResponseEntity.ok(program);
    }
    
    /**
     * Duplicate program
     */
    @PostMapping("/{programId}/duplicate")
    public ResponseEntity<FitnessProgramResponse> duplicateProgram(
            @PathVariable Integer programId,
            @RequestParam String newName,
            Principal principal) {
        
        log.info("Instructor {} duplicating program ID: {} with new name: {}", 
                principal.getName(), programId, newName);
        
        FitnessProgramResponse duplicatedProgram = instructorProgramManagementService.duplicateProgram(
                principal, programId, newName);
        return ResponseEntity.ok(duplicatedProgram);
    }
    
    /**
     * Publish program
     */
    @PostMapping("/{programId}/publish")
    public ResponseEntity<FitnessProgramResponse> publishProgram(
            @PathVariable Integer programId,
            Principal principal) {
        
        log.info("Instructor {} publishing program ID: {}", principal.getName(), programId);
        
        FitnessProgramResponse publishedProgram = instructorProgramManagementService.publishProgram(
                principal, programId);
        return ResponseEntity.ok(publishedProgram);
    }
    
    /**
     * Unpublish program
     */
    @PostMapping("/{programId}/unpublish")
    public ResponseEntity<FitnessProgramResponse> unpublishProgram(
            @PathVariable Integer programId,
            Principal principal) {
        
        log.info("Instructor {} unpublishing program ID: {}", principal.getName(), programId);
        
        FitnessProgramResponse unpublishedProgram = instructorProgramManagementService.unpublishProgram(
                principal, programId);
        return ResponseEntity.ok(unpublishedProgram);
    }
    
    /**
     * Archive program
     */
    @PostMapping("/{programId}/archive")
    public ResponseEntity<FitnessProgramResponse> archiveProgram(
            @PathVariable Integer programId,
            Principal principal) {
        
        log.info("Instructor {} archiving program ID: {}", principal.getName(), programId);
        
        FitnessProgramResponse archivedProgram = instructorProgramManagementService.archiveProgram(
                principal, programId);
        return ResponseEntity.ok(archivedProgram);
    }
    
    /**
     * Restore archived program
     */
    @PostMapping("/{programId}/restore")
    public ResponseEntity<FitnessProgramResponse> restoreProgram(
            @PathVariable Integer programId,
            Principal principal) {
        
        log.info("Instructor {} restoring program ID: {}", principal.getName(), programId);
        
        FitnessProgramResponse restoredProgram = instructorProgramManagementService.restoreProgram(
                principal, programId);
        return ResponseEntity.ok(restoredProgram);
    }
    
    /**
     * Get program analytics
     */
    @GetMapping("/{programId}/analytics")
    public ResponseEntity<Object> getProgramAnalytics(
            @PathVariable Integer programId,
            Principal principal) {
        
        log.info("Instructor {} requesting analytics for program ID: {}", principal.getName(), programId);
        
        Object analytics = instructorProgramManagementService.getProgramAnalytics(principal, programId);
        return ResponseEntity.ok(analytics);
    }
    
    /**
     * Get program enrollment statistics
     */
    @GetMapping("/{programId}/enrollment-stats")
    public ResponseEntity<Object> getProgramEnrollmentStats(
            @PathVariable Integer programId,
            Principal principal) {
        
        log.info("Instructor {} requesting enrollment stats for program ID: {}", principal.getName(), programId);
        
        Object stats = instructorProgramManagementService.getProgramEnrollmentStats(principal, programId);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Get program revenue statistics
     */
    @GetMapping("/{programId}/revenue-stats")
    public ResponseEntity<Object> getProgramRevenueStats(
            @PathVariable Integer programId,
            Principal principal) {
        
        log.info("Instructor {} requesting revenue stats for program ID: {}", principal.getName(), programId);
        
        Object stats = instructorProgramManagementService.getProgramRevenueStats(principal, programId);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Get program reviews
     */
    @GetMapping("/{programId}/reviews")
    public ResponseEntity<Object> getProgramReviews(
            @PathVariable Integer programId,
            Principal principal) {
        
        log.info("Instructor {} requesting reviews for program ID: {}", principal.getName(), programId);
        
        Object reviews = instructorProgramManagementService.getProgramReviews(principal, programId);
        return ResponseEntity.ok(reviews);
    }
    
    /**
     * Update program pricing
     */
    @PutMapping("/{programId}/pricing")
    public ResponseEntity<FitnessProgramResponse> updateProgramPricing(
            @PathVariable Integer programId,
            @RequestParam Double newPrice,
            Principal principal) {
        
        log.info("Instructor {} updating pricing for program ID: {} to {}", 
                principal.getName(), programId, newPrice);
        
        FitnessProgramResponse updatedProgram = instructorProgramManagementService.updateProgramPricing(
                principal, programId, newPrice);
        return ResponseEntity.ok(updatedProgram);
    }
    
    /**
     * Update program availability
     */
    @PutMapping("/{programId}/availability")
    public ResponseEntity<FitnessProgramResponse> updateProgramAvailability(
            @PathVariable Integer programId,
            @RequestParam Boolean isAvailable,
            Principal principal) {
        
        log.info("Instructor {} updating availability for program ID: {} to {}", 
                principal.getName(), programId, isAvailable);
        
        FitnessProgramResponse updatedProgram = instructorProgramManagementService.updateProgramAvailability(
                principal, programId, isAvailable);
        return ResponseEntity.ok(updatedProgram);
    }
    
    /**
     * Get program templates
     */
    @GetMapping("/templates")
    public ResponseEntity<List<Object>> getProgramTemplates(Principal principal) {
        
        log.info("Instructor {} requesting program templates", principal.getName());
        
        List<Object> templates = instructorProgramManagementService.getProgramTemplates(principal);
        return ResponseEntity.ok(templates);
    }
    
    /**
     * Create program from template
     */
    @PostMapping("/templates/{templateId}/create")
    public ResponseEntity<FitnessProgramResponse> createFromTemplate(
            @PathVariable String templateId,
            @RequestBody InstructorProgramRequest customizations,
            Principal principal) {
        
        log.info("Instructor {} creating program from template: {}", principal.getName(), templateId);
        
        FitnessProgramResponse newProgram = instructorProgramManagementService.createFromTemplate(
                principal, templateId, customizations);
        return ResponseEntity.ok(newProgram);
    }
    
    /**
     * Compare program performance
     */
    @PostMapping("/compare-performance")
    public ResponseEntity<Object> compareProgramPerformance(
            @RequestBody List<Integer> programIds,
            Principal principal) {
        
        log.info("Instructor {} comparing performance for programs: {}", principal.getName(), programIds);
        
        Object comparison = instructorProgramManagementService.compareProgramPerformance(principal, programIds);
        return ResponseEntity.ok(comparison);
    }
}
