package com.java.moveminds.services.admin;

import com.java.moveminds.dto.response.AdminProgramResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.Principal;

/**
 * Service interface for admin program management operations.
 * Provides comprehensive program management capabilities for administrators.
 */
public interface AdminProgramManagementService {
    
    /**
     * Get all programs with pagination and filtering
     */
    Page<AdminProgramResponse> getAllPrograms(Principal principal, Pageable pageable, 
                                            String search, String category, String difficulty);
    
    /**
     * Get program details by ID
     */
    AdminProgramResponse getProgramById(Principal principal, Integer programId);
    
    /**
     * Delete a program
     */
    void deleteProgram(Principal principal, Integer programId);
    
    /**
     * Update program status (activate/deactivate)
     */
    AdminProgramResponse updateProgramStatus(Principal principal, Integer programId, boolean isActive);
    
    /**
     * Get program statistics
     */
    Object getProgramStatistics(Principal principal);
}
