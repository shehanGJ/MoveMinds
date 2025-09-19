package com.java.moveminds.services.impl;

import com.java.moveminds.dto.requests.admin.ProgramActivationRequest;
import com.java.moveminds.dto.response.admin.ProgramActivationResponse;
import com.java.moveminds.dto.response.AdminProgramResponse;
import com.java.moveminds.entities.FitnessProgramEntity;
import com.java.moveminds.repositories.FitnessProgramEntityRepository;
import com.java.moveminds.services.admin.AdminProgramActivationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminProgramActivationServiceImpl implements AdminProgramActivationService {
    
    private final FitnessProgramEntityRepository fitnessProgramRepository;
    
    @Override
    @Transactional
    public ProgramActivationResponse activateProgram(ProgramActivationRequest request, String adminUsername) {
        log.info("Admin {} attempting to {} program with ID: {}", 
                adminUsername, request.getIsActive() ? "activate" : "deactivate", request.getProgramId());
        
        FitnessProgramEntity program = fitnessProgramRepository.findById(request.getProgramId())
                .orElseThrow(() -> new RuntimeException("Program not found with ID: " + request.getProgramId()));
        
        // Update the program's active status
        program.setIsActive(request.getIsActive());
        FitnessProgramEntity savedProgram = fitnessProgramRepository.save(program);
        
        log.info("Program '{}' has been {} by admin {}", 
                savedProgram.getName(), request.getIsActive() ? "activated" : "deactivated", adminUsername);
        
        return ProgramActivationResponse.builder()
                .programId(savedProgram.getId())
                .programName(savedProgram.getName())
                .isActive(savedProgram.getIsActive())
                .adminNotes(request.getAdminNotes())
                .activationDate(LocalDateTime.now())
                .activatedBy(adminUsername)
                .message(String.format("Program '%s' has been %s successfully", 
                        savedProgram.getName(), request.getIsActive() ? "activated" : "deactivated"))
                .instructorName(savedProgram.getUser().getUsername())
                .categoryName(savedProgram.getCategory() != null ? savedProgram.getCategory().getName() : "N/A")
                .difficultyLevel(savedProgram.getDifficultyLevel().toString())
                .build();
    }
    
    @Override
    public Page<AdminProgramResponse> getActivePrograms(Pageable pageable) {
        log.info("Fetching active programs with pagination: {}", pageable);
        
        Page<FitnessProgramEntity> activePrograms = fitnessProgramRepository.findByIsActive(true, pageable);
        
        return activePrograms.map(this::convertToAdminProgramResponse);
    }
    
    @Override
    public Page<AdminProgramResponse> getInactivePrograms(Pageable pageable) {
        log.info("Fetching inactive programs with pagination: {}", pageable);
        
        Page<FitnessProgramEntity> inactivePrograms = fitnessProgramRepository.findByIsActive(false, pageable);
        
        return inactivePrograms.map(this::convertToAdminProgramResponse);
    }
    
    @Override
    public ProgramActivationStats getProgramActivationStats() {
        log.info("Calculating program activation statistics");
        
        long totalPrograms = fitnessProgramRepository.count();
        long activePrograms = fitnessProgramRepository.countByIsActiveTrue();
        long inactivePrograms = fitnessProgramRepository.countByIsActiveFalse();
        
        // Consider programs created in the last 7 days as pending activation
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        long pendingActivation = fitnessProgramRepository.countByIsActiveAndCreatedAtAfter(false, sevenDaysAgo);
        
        return new ProgramActivationStats(totalPrograms, activePrograms, inactivePrograms, pendingActivation);
    }
    
    @Override
    public Page<AdminProgramResponse> getProgramsPendingActivation(Pageable pageable) {
        log.info("Fetching programs pending activation with pagination: {}", pageable);
        
        // Get programs created in the last 7 days that are still inactive
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<FitnessProgramEntity> pendingPrograms = fitnessProgramRepository
                .findByIsActiveAndCreatedAtAfter(false, sevenDaysAgo);
        
        // Convert to Page manually since we're filtering by date range
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), pendingPrograms.size());
        List<FitnessProgramEntity> pageContent = pendingPrograms.subList(start, end);
        
        Page<FitnessProgramEntity> page = new org.springframework.data.domain.PageImpl<>(
                pageContent, pageable, pendingPrograms.size());
        
        return page.map(this::convertToAdminProgramResponse);
    }
    
    private AdminProgramResponse convertToAdminProgramResponse(FitnessProgramEntity program) {
        return AdminProgramResponse.builder()
                .id(program.getId())
                .name(program.getName())
                .description(program.getDescription())
                .difficultyLevel(program.getDifficultyLevel())
                .duration(program.getDuration())
                .price(program.getPrice())
                .youtubeUrl(program.getYoutubeUrl())
                .createdAt(program.getCreatedAt())
                .instructorId(program.getUser().getId())
                .instructorName(program.getUser().getUsername())
                .instructorEmail(program.getUser().getEmail())
                .categoryId(program.getCategory() != null ? program.getCategory().getId() : 0)
                .categoryName(program.getCategory() != null ? program.getCategory().getName() : "N/A")
                .locationId(program.getLocation() != null ? program.getLocation().getId() : 0)
                .locationName(program.getLocation() != null ? program.getLocation().getName() : "N/A")
                .enrollmentCount(0L) // Will be calculated separately if needed
                .commentCount(0L) // Will be calculated separately if needed
                .averageRating(0.0) // Will be calculated separately if needed
                .status(program.getIsActive() ? "ACTIVE" : "INACTIVE")
                .isActive(program.getIsActive())
                .build();
    }
}
