package com.java.moveminds.services.admin;

import com.java.moveminds.dto.requests.admin.ProgramActivationRequest;
import com.java.moveminds.dto.response.admin.ProgramActivationResponse;
import com.java.moveminds.dto.response.AdminProgramResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminProgramActivationService {
    
    /**
     * Activate or deactivate a program
     * @param request Program activation request
     * @param adminUsername Username of the admin performing the action
     * @return Program activation response
     */
    ProgramActivationResponse activateProgram(ProgramActivationRequest request, String adminUsername);
    
    /**
     * Get all active programs with pagination
     * @param pageable Pagination parameters
     * @return Page of active programs
     */
    Page<AdminProgramResponse> getActivePrograms(Pageable pageable);
    
    /**
     * Get all inactive programs with pagination
     * @param pageable Pagination parameters
     * @return Page of inactive programs
     */
    Page<AdminProgramResponse> getInactivePrograms(Pageable pageable);
    
    /**
     * Get program activation statistics
     * @return Statistics about active/inactive programs
     */
    ProgramActivationStats getProgramActivationStats();
    
    /**
     * Get programs pending activation (recently created inactive programs)
     * @param pageable Pagination parameters
     * @return Page of programs pending activation
     */
    Page<AdminProgramResponse> getProgramsPendingActivation(Pageable pageable);
    
    /**
     * Inner class for program activation statistics
     */
    class ProgramActivationStats {
        private long totalPrograms;
        private long activePrograms;
        private long inactivePrograms;
        private long pendingActivation;
        private double activationRate;
        
        // Constructors, getters, and setters
        public ProgramActivationStats() {}
        
        public ProgramActivationStats(long totalPrograms, long activePrograms, long inactivePrograms, long pendingActivation) {
            this.totalPrograms = totalPrograms;
            this.activePrograms = activePrograms;
            this.inactivePrograms = inactivePrograms;
            this.pendingActivation = pendingActivation;
            this.activationRate = totalPrograms > 0 ? (double) activePrograms / totalPrograms * 100 : 0.0;
        }
        
        public long getTotalPrograms() { return totalPrograms; }
        public void setTotalPrograms(long totalPrograms) { this.totalPrograms = totalPrograms; }
        
        public long getActivePrograms() { return activePrograms; }
        public void setActivePrograms(long activePrograms) { this.activePrograms = activePrograms; }
        
        public long getInactivePrograms() { return inactivePrograms; }
        public void setInactivePrograms(long inactivePrograms) { this.inactivePrograms = inactivePrograms; }
        
        public long getPendingActivation() { return pendingActivation; }
        public void setPendingActivation(long pendingActivation) { this.pendingActivation = pendingActivation; }
        
        public double getActivationRate() { return activationRate; }
        public void setActivationRate(double activationRate) { this.activationRate = activationRate; }
    }
}
