package com.java.moveminds.services.instructor;

import com.java.moveminds.dto.requests.instructor.InstructorProgramRequest;
import com.java.moveminds.dto.response.FitnessProgramListResponse;
import com.java.moveminds.dto.response.FitnessProgramResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

/**
 * Service interface for instructor program management operations.
 * Handles all program-related business logic for instructors.
 */
public interface InstructorProgramManagementService {
    
    /**
     * Create a new fitness program with comprehensive validation
     */
    FitnessProgramResponse createProgram(Principal principal, InstructorProgramRequest request, 
                                       List<MultipartFile> files) throws IOException;
    
    /**
     * Update existing program with validation and audit trail
     */
    FitnessProgramResponse updateProgram(Principal principal, Integer programId, 
                                       InstructorProgramRequest request, 
                                       List<MultipartFile> files, 
                                       List<String> removedImages) throws IOException;
    
    /**
     * Delete program with proper cleanup and validation
     */
    void deleteProgram(Principal principal, Integer programId) throws IOException;
    
    /**
     * Get instructor's programs with advanced filtering and sorting
     */
    Page<FitnessProgramListResponse> getMyPrograms(Principal principal, Pageable pageable, 
                                                  String sort, String filter, String search);
    
    /**
     * Get program details with comprehensive information
     */
    FitnessProgramResponse getProgramDetails(Principal principal, Integer programId);
    
    /**
     * Duplicate existing program
     */
    FitnessProgramResponse duplicateProgram(Principal principal, Integer programId, String newName);
    
    /**
     * Publish draft program
     */
    FitnessProgramResponse publishProgram(Principal principal, Integer programId);
    
    /**
     * Unpublish active program
     */
    FitnessProgramResponse unpublishProgram(Principal principal, Integer programId);
    
    /**
     * Archive program (soft delete)
     */
    FitnessProgramResponse archiveProgram(Principal principal, Integer programId);
    
    /**
     * Restore archived program
     */
    FitnessProgramResponse restoreProgram(Principal principal, Integer programId);
    
    /**
     * Get program analytics and performance metrics
     */
    Object getProgramAnalytics(Principal principal, Integer programId);
    
    /**
     * Get program enrollment statistics
     */
    Object getProgramEnrollmentStats(Principal principal, Integer programId);
    
    /**
     * Get program revenue statistics
     */
    Object getProgramRevenueStats(Principal principal, Integer programId);
    
    /**
     * Get program reviews and ratings
     */
    Object getProgramReviews(Principal principal, Integer programId);
    
    /**
     * Update program pricing
     */
    FitnessProgramResponse updateProgramPricing(Principal principal, Integer programId, 
                                              Double newPrice);
    
    /**
     * Update program availability
     */
    FitnessProgramResponse updateProgramAvailability(Principal principal, Integer programId, 
                                                   Boolean isAvailable);
    
    /**
     * Get program templates for quick creation
     */
    List<Object> getProgramTemplates(Principal principal);
    
    /**
     * Create program from template
     */
    FitnessProgramResponse createFromTemplate(Principal principal, String templateId, 
                                            InstructorProgramRequest customizations);
    
    /**
     * Validate program data and business rules
     */
    void validateProgramData(InstructorProgramRequest request);
    
    /**
     * Check if program can be deleted (no active enrollments)
     */
    boolean canDeleteProgram(Integer programId);
    
    /**
     * Get program performance comparison
     */
    Object compareProgramPerformance(Principal principal, List<Integer> programIds);
}
