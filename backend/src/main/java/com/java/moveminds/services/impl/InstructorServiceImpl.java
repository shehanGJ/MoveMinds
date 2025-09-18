package com.java.moveminds.services.impl;

import com.java.moveminds.dto.requests.FitnessProgramRequest;
import com.java.moveminds.dto.response.FitnessProgramListResponse;
import com.java.moveminds.dto.response.FitnessProgramResponse;
import com.java.moveminds.dto.response.InstructorStatsResponse;
import com.java.moveminds.dto.response.ProgramEnrollmentResponse;
import com.java.moveminds.entities.UserEntity;
import com.java.moveminds.entities.UserProgramEntity;
import com.java.moveminds.enums.Roles;
import com.java.moveminds.enums.Status;
import com.java.moveminds.exceptions.UnauthorizedException;
import com.java.moveminds.exceptions.UserNotFoundException;
import com.java.moveminds.repositories.UserEntityRepository;
import com.java.moveminds.repositories.UserProgramEntityRepository;
import com.java.moveminds.services.FitnessProgramService;
import com.java.moveminds.services.InstructorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InstructorServiceImpl implements InstructorService {

    private final FitnessProgramService fitnessProgramService;
    private final UserEntityRepository userRepository;
    private final UserProgramEntityRepository userProgramRepository;

    @Override
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public InstructorStatsResponse getInstructorStats(Principal principal) {
        validateInstructorAccess(principal);
        
        UserEntity instructor = getCurrentUser(principal);
        
        // Calculate stats based on instructor's programs and enrollments
        long totalPrograms = instructor.getFitnessPrograms() != null ? instructor.getFitnessPrograms().size() : 0;
        long totalEnrollments = userProgramRepository.countByFitnessProgramByProgramId_User(instructor);
        long activeEnrollments = userProgramRepository.countByFitnessProgramByProgramId_UserAndStatus(instructor, Status.ACTIVE);
        long completedEnrollments = 0L; // TODO: Implement completed status logic
        
        return InstructorStatsResponse.builder()
                .totalPrograms(totalPrograms)
                .totalStudents(0L) // TODO: Calculate unique students
                .totalEnrollments(totalEnrollments)
                .activeEnrollments(activeEnrollments)
                .completedEnrollments(completedEnrollments)
                .totalRevenue(0L) // TODO: Calculate from program prices and enrollments
                .monthlyRevenue(0L) // TODO: Calculate monthly revenue
                .averageRating(0.0) // TODO: Calculate from reviews/ratings
                .totalReviews(0L) // TODO: Count reviews
                .newEnrollmentsThisMonth(0L) // TODO: Calculate monthly enrollments
                .programsThisMonth(0L) // TODO: Calculate monthly programs
                .build();
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public FitnessProgramResponse createProgram(Principal principal, FitnessProgramRequest programRequest, List<MultipartFile> files) throws IOException {
        validateInstructorAccess(principal);
        return fitnessProgramService.addFitnessProgram(principal, programRequest, files);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public FitnessProgramResponse updateProgram(Principal principal, Integer programId, FitnessProgramRequest programRequest, List<MultipartFile> files, List<String> removedImages) throws IOException {
        validateInstructorAccess(principal);
        validateProgramOwnership(principal, programId);
        return fitnessProgramService.updateFitnessProgram(programId, programRequest, files, removedImages);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public void deleteProgram(Principal principal, Integer programId) throws IOException {
        validateInstructorAccess(principal);
        validateProgramOwnership(principal, programId);
        fitnessProgramService.deleteFitnessProgram(programId, principal);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public Page<FitnessProgramListResponse> getMyPrograms(Principal principal, Pageable pageable, String sort) {
        validateInstructorAccess(principal);
        return fitnessProgramService.getMyFitnessPrograms(principal, pageable);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public FitnessProgramResponse getProgramDetails(Principal principal, Integer programId) {
        validateInstructorAccess(principal);
        validateProgramOwnership(principal, programId);
        return fitnessProgramService.getFitnessProgram(programId);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public List<ProgramEnrollmentResponse> getProgramEnrollments(Principal principal, Integer programId) {
        validateInstructorAccess(principal);
        validateProgramOwnership(principal, programId);
        
        // TODO: Implement program enrollment retrieval
        return List.of(); // Placeholder
    }

    @Override
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public Page<ProgramEnrollmentResponse> getAllEnrollments(Principal principal, Pageable pageable) {
        validateInstructorAccess(principal);
        
        UserEntity instructor = getCurrentUser(principal);
        Page<UserProgramEntity> enrollments = userProgramRepository.findByFitnessProgramByProgramId_User(instructor, pageable);
        
        return enrollments.map(this::mapToProgramEnrollmentResponse);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public ProgramEnrollmentResponse updateEnrollmentStatus(Principal principal, Integer enrollmentId, String status) {
        validateInstructorAccess(principal);
        
        UserProgramEntity enrollment = userProgramRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        
        validateEnrollmentOwnership(principal, enrollment);
        
        try {
            Status newStatus = Status.valueOf(status.toUpperCase());
            enrollment.setStatus(newStatus);
            UserProgramEntity savedEnrollment = userProgramRepository.save(enrollment);
            return mapToProgramEnrollmentResponse(savedEnrollment);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }
    }

    @Override
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public Page<ProgramEnrollmentResponse> getStudents(Principal principal, Pageable pageable) {
        validateInstructorAccess(principal);
        return getAllEnrollments(principal, pageable);
    }

    private void validateInstructorAccess(Principal principal) {
        if (principal == null) {
            throw new UnauthorizedException("Authentication required");
        }
        
        UserEntity user = getCurrentUser(principal);
        
        if (user.getRole() != Roles.INSTRUCTOR && user.getRole() != Roles.ADMIN) {
            throw new UnauthorizedException("Instructor or Admin access required");
        }
    }

    private UserEntity getCurrentUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private void validateProgramOwnership(Principal principal, Integer programId) {
        UserEntity user = getCurrentUser(principal);
        
        // For admins, allow access to all programs
        if (user.getRole() == Roles.ADMIN) {
            return;
        }
        
        // For instructors, only allow access to their own programs
        boolean ownsProgram = user.getFitnessPrograms() != null &&
                user.getFitnessPrograms().stream()
                        .anyMatch(program -> program.getId().equals(programId));
        
        if (!ownsProgram) {
            throw new UnauthorizedException("Access denied: You can only manage your own programs");
        }
    }

    private void validateEnrollmentOwnership(Principal principal, UserProgramEntity enrollment) {
        UserEntity user = getCurrentUser(principal);
        
        // For admins, allow access to all enrollments
        if (user.getRole() == Roles.ADMIN) {
            return;
        }
        
        // For instructors, only allow access to enrollments in their programs
        boolean ownsEnrollment = user.getFitnessPrograms() != null &&
                user.getFitnessPrograms().stream()
                        .anyMatch(program -> program.getId().equals(enrollment.getFitnessProgramByProgramId().getId()));
        
        if (!ownsEnrollment) {
            throw new UnauthorizedException("Access denied: You can only manage enrollments in your own programs");
        }
    }

    private ProgramEnrollmentResponse mapToProgramEnrollmentResponse(UserProgramEntity enrollment) {
        return ProgramEnrollmentResponse.builder()
                .enrollmentId(enrollment.getId())
                .userId(enrollment.getUserByUserId().getId())
                .username(enrollment.getUserByUserId().getUsername())
                .firstName(enrollment.getUserByUserId().getFirstName())
                .lastName(enrollment.getUserByUserId().getLastName())
                .email(enrollment.getUserByUserId().getEmail())
                .avatarUrl(enrollment.getUserByUserId().getAvatarUrl())
                .programId(enrollment.getFitnessProgramByProgramId().getId())
                .programName(enrollment.getFitnessProgramByProgramId().getName())
                .startDate(enrollment.getStartDate().toLocalDate())
                .endDate(enrollment.getEndDate().toLocalDate())
                .status(enrollment.getStatus().name())
                .enrolledAt(enrollment.getStartDate().toLocalDate().atStartOfDay()) // TODO: Add proper enrolledAt field
                .progress(0) // TODO: Calculate actual progress
                .cityName(enrollment.getUserByUserId().getCity() != null ? 
                        enrollment.getUserByUserId().getCity().getName() : null)
                .build();
    }
}
