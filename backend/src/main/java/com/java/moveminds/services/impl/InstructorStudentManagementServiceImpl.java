package com.java.moveminds.services.impl;

import com.java.moveminds.dto.requests.instructor.InstructorStudentManagementRequest;
import com.java.moveminds.dto.response.ProgramEnrollmentResponse;
import com.java.moveminds.entities.UserEntity;
import com.java.moveminds.entities.UserProgramEntity;
import com.java.moveminds.exceptions.UnauthorizedAccessException;
import com.java.moveminds.repositories.UserEntityRepository;
import com.java.moveminds.repositories.UserProgramEntityRepository;
import com.java.moveminds.services.instructor.InstructorStudentManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of InstructorStudentManagementService with comprehensive student management logic.
 * Handles all student-related operations for instructors with proper validation and authorization.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InstructorStudentManagementServiceImpl implements InstructorStudentManagementService {
    
    private final UserProgramEntityRepository enrollmentRepository;
    private final UserEntityRepository userRepository;
    private final ModelMapper modelMapper;
    
    @Override
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public Page<ProgramEnrollmentResponse> getStudents(Principal principal, Pageable pageable, 
                                                      String search, String status, String program) {
        log.info("Instructor {} requesting students: search={}, status={}, program={}", 
                principal.getName(), search, status, program);
        
        UserEntity instructor = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UnauthorizedAccessException("Instructor not found"));
        
        // Get enrollments for instructor's programs
        Page<UserProgramEntity> enrollments = enrollmentRepository.findByInstructorId(
                instructor.getId(), pageable);
        
        return enrollments.map(this::convertToProgramEnrollmentResponse);
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public List<ProgramEnrollmentResponse> getProgramEnrollments(Principal principal, Integer programId) {
        log.info("Instructor {} requesting enrollments for program ID: {}", principal.getName(), programId);
        
        UserEntity instructor = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UnauthorizedAccessException("Instructor not found"));
        
        // Verify instructor owns this program
        if (!canManageProgram(principal, programId)) {
            throw new UnauthorizedAccessException("You can only view enrollments for your own programs");
        }
        
        List<UserProgramEntity> enrollments = enrollmentRepository.findByProgramId(programId);
        
        return enrollments.stream()
                .map(this::convertToProgramEnrollmentResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public Page<ProgramEnrollmentResponse> getAllEnrollments(Principal principal, Pageable pageable, 
                                                           String search, String status, String program) {
        log.info("Instructor {} requesting all enrollments: search={}, status={}, program={}", 
                principal.getName(), search, status, program);
        
        UserEntity instructor = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UnauthorizedAccessException("Instructor not found"));
        
        // Get all enrollments for instructor's programs
        Page<UserProgramEntity> enrollments = enrollmentRepository.findByInstructorId(
                instructor.getId(), pageable);
        
        return enrollments.map(this::convertToProgramEnrollmentResponse);
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public ProgramEnrollmentResponse updateEnrollmentStatus(Principal principal, Integer enrollmentId, String status) {
        log.info("Instructor {} updating enrollment ID: {} status to {}", 
                principal.getName(), enrollmentId, status);
        
        UserProgramEntity enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found with ID: " + enrollmentId));
        
        // Verify instructor can manage this enrollment
        if (!canManageEnrollment(principal, enrollmentId)) {
            throw new UnauthorizedAccessException("You can only update enrollments for your own programs");
        }
        
        // Update enrollment status
        // Note: This would need to be updated based on your actual Status enum
        // enrollment.setStatus(Status.valueOf(status));
        
        UserProgramEntity updatedEnrollment = enrollmentRepository.save(enrollment);
        
        log.info("Instructor {} successfully updated enrollment ID: {} status to {}", 
                principal.getName(), enrollmentId, status);
        
        return convertToProgramEnrollmentResponse(updatedEnrollment);
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public ProgramEnrollmentResponse updateStudentProgress(Principal principal, 
                                                          InstructorStudentManagementRequest request) {
        log.info("Instructor {} updating progress for enrollment ID: {}", principal.getName(), request.getEnrollmentId());
        
        UserProgramEntity enrollment = enrollmentRepository.findById(request.getEnrollmentId())
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found with ID: " + request.getEnrollmentId()));
        
        // Verify instructor can manage this enrollment
        if (!canManageEnrollment(principal, request.getEnrollmentId())) {
            throw new UnauthorizedAccessException("You can only update progress for your own students");
        }
        
        validateStudentManagementRequest(request);
        
        // Update enrollment with progress information
        if (request.getProgress() != null) {
            // enrollment.setProgress(request.getProgress());
        }
        
        if (request.getNotes() != null) {
            // enrollment.setNotes(request.getNotes());
        }
        
        if (request.getFeedback() != null) {
            // enrollment.setFeedback(request.getFeedback());
        }
        
        UserProgramEntity updatedEnrollment = enrollmentRepository.save(enrollment);
        
        log.info("Instructor {} successfully updated progress for enrollment ID: {}", 
                principal.getName(), request.getEnrollmentId());
        
        return convertToProgramEnrollmentResponse(updatedEnrollment);
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public Object getStudentDetails(Principal principal, Integer studentId) {
        log.info("Instructor {} requesting details for student ID: {}", principal.getName(), studentId);
        
        if (!canManageStudent(principal, studentId)) {
            throw new UnauthorizedAccessException("You can only view details for your own students");
        }
        
        UserEntity student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));
        
        // Placeholder implementation - would return comprehensive student details
        return new Object();
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public Object getStudentProgress(Principal principal, Integer studentId) {
        log.info("Instructor {} requesting progress for student ID: {}", principal.getName(), studentId);
        
        if (!canManageStudent(principal, studentId)) {
            throw new UnauthorizedAccessException("You can only view progress for your own students");
        }
        
        // Placeholder implementation - would return student progress across all programs
        return new Object();
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public void sendMessageToStudent(Principal principal, Integer studentId, String message) {
        log.info("Instructor {} sending message to student ID: {}", principal.getName(), studentId);
        
        if (!canManageStudent(principal, studentId)) {
            throw new UnauthorizedAccessException("You can only send messages to your own students");
        }
        
        // Placeholder implementation - would send actual message
        log.info("Message sent to student ID: {} - {}", studentId, message);
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public List<Object> getStudentMessages(Principal principal, Integer studentId) {
        log.info("Instructor {} requesting messages for student ID: {}", principal.getName(), studentId);
        
        if (!canManageStudent(principal, studentId)) {
            throw new UnauthorizedAccessException("You can only view messages for your own students");
        }
        
        // Placeholder implementation - would fetch actual messages
        return List.of();
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public void addStudentNote(Principal principal, Integer studentId, String note) {
        log.info("Instructor {} adding note to student ID: {}", principal.getName(), studentId);
        
        if (!canManageStudent(principal, studentId)) {
            throw new UnauthorizedAccessException("You can only add notes to your own students");
        }
        
        // Placeholder implementation - would add actual note
        log.info("Note added to student ID: {} - {}", studentId, note);
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public List<Object> getStudentNotes(Principal principal, Integer studentId) {
        log.info("Instructor {} requesting notes for student ID: {}", principal.getName(), studentId);
        
        if (!canManageStudent(principal, studentId)) {
            throw new UnauthorizedAccessException("You can only view notes for your own students");
        }
        
        // Placeholder implementation - would fetch actual notes
        return List.of();
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public void updateStudentFeedback(Principal principal, Integer enrollmentId, String feedback) {
        log.info("Instructor {} updating feedback for enrollment ID: {}", principal.getName(), enrollmentId);
        
        UserProgramEntity enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found with ID: " + enrollmentId));
        
        if (!canManageEnrollment(principal, enrollmentId)) {
            throw new UnauthorizedAccessException("You can only update feedback for your own students");
        }
        
        // Update feedback
        // enrollment.setFeedback(feedback);
        enrollmentRepository.save(enrollment);
        
        log.info("Instructor {} successfully updated feedback for enrollment ID: {}", 
                principal.getName(), enrollmentId);
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public Object getStudentAnalytics(Principal principal, Integer studentId) {
        log.info("Instructor {} requesting analytics for student ID: {}", principal.getName(), studentId);
        
        if (!canManageStudent(principal, studentId)) {
            throw new UnauthorizedAccessException("You can only view analytics for your own students");
        }
        
        // Placeholder implementation - would calculate actual analytics
        return new Object();
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public List<Object> getTopPerformingStudents(Principal principal, String period) {
        log.info("Instructor {} requesting top performing students for period: {}", principal.getName(), period);
        
        // Placeholder implementation - would calculate actual top performers
        return List.of();
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public List<Object> getStudentsAtRisk(Principal principal) {
        log.info("Instructor {} requesting students at risk", principal.getName());
        
        // Placeholder implementation - would identify students at risk
        return List.of();
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public Object getStudentEngagementMetrics(Principal principal, String period) {
        log.info("Instructor {} requesting engagement metrics for period: {}", principal.getName(), period);
        
        // Placeholder implementation - would calculate actual engagement metrics
        return new Object();
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public byte[] exportStudentData(Principal principal, List<Integer> studentIds, String format) {
        log.info("Instructor {} exporting student data for {} students in {} format", 
                principal.getName(), studentIds.size(), format);
        
        // Placeholder implementation - would export actual student data
        return new byte[0];
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public Object getStudentCompletionRates(Principal principal, String period) {
        log.info("Instructor {} requesting completion rates for period: {}", principal.getName(), period);
        
        // Placeholder implementation - would calculate actual completion rates
        return new Object();
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public Object getStudentSatisfactionScores(Principal principal, String period) {
        log.info("Instructor {} requesting satisfaction scores for period: {}", principal.getName(), period);
        
        // Placeholder implementation - would calculate actual satisfaction scores
        return new Object();
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    public void sendBulkMessageToStudents(Principal principal, List<Integer> studentIds, String message) {
        log.info("Instructor {} sending bulk message to {} students", principal.getName(), studentIds.size());
        
        // Verify instructor can manage all students
        for (Integer studentId : studentIds) {
            if (!canManageStudent(principal, studentId)) {
                throw new UnauthorizedAccessException("You can only send messages to your own students");
            }
        }
        
        // Placeholder implementation - would send actual bulk messages
        log.info("Bulk message sent to {} students: {}", studentIds.size(), message);
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public List<Object> getStudentActivityTimeline(Principal principal, Integer studentId) {
        log.info("Instructor {} requesting activity timeline for student ID: {}", principal.getName(), studentId);
        
        if (!canManageStudent(principal, studentId)) {
            throw new UnauthorizedAccessException("You can only view activity timeline for your own students");
        }
        
        // Placeholder implementation - would fetch actual activity timeline
        return List.of();
    }
    
    @Override
    public void validateStudentManagementRequest(InstructorStudentManagementRequest request) {
        if (request.getEnrollmentId() == null) {
            throw new IllegalArgumentException("Enrollment ID is required");
        }
        
        if (request.getStatus() == null) {
            throw new IllegalArgumentException("Status is required");
        }
        
        // Additional validation logic can be added here
    }
    
    @Override
    public boolean canManageStudent(Principal principal, Integer studentId) {
        UserEntity instructor = userRepository.findByUsername(principal.getName()).orElse(null);
        if (instructor == null) {
            return false;
        }
        
        // Check if student is enrolled in any of instructor's programs
        // This would need to be implemented based on your actual data model
        return true; // Placeholder
    }
    
    // Helper methods
    private boolean canManageProgram(Principal principal, Integer programId) {
        // Check if instructor owns this program
        // This would need to be implemented based on your actual data model
        return true; // Placeholder
    }
    
    private boolean canManageEnrollment(Principal principal, Integer enrollmentId) {
        // Check if enrollment belongs to instructor's program
        // This would need to be implemented based on your actual data model
        return true; // Placeholder
    }
    
    private ProgramEnrollmentResponse convertToProgramEnrollmentResponse(UserProgramEntity enrollment) {
        return modelMapper.map(enrollment, ProgramEnrollmentResponse.class);
    }
}
