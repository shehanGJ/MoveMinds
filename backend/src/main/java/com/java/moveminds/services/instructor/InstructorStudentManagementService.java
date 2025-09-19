package com.java.moveminds.services.instructor;

import com.java.moveminds.dto.requests.instructor.InstructorStudentManagementRequest;
import com.java.moveminds.dto.response.ProgramEnrollmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.Principal;
import java.util.List;

/**
 * Service interface for instructor student management operations.
 * Handles all student-related business logic for instructors.
 */
public interface InstructorStudentManagementService {
    
    /**
     * Get all students enrolled in instructor's programs
     */
    Page<ProgramEnrollmentResponse> getStudents(Principal principal, Pageable pageable, 
                                              String search, String status, String program);
    
    /**
     * Get enrollments for a specific program
     */
    List<ProgramEnrollmentResponse> getProgramEnrollments(Principal principal, Integer programId);
    
    /**
     * Get all enrollments across instructor's programs
     */
    Page<ProgramEnrollmentResponse> getAllEnrollments(Principal principal, Pageable pageable, 
                                                    String search, String status, String program);
    
    /**
     * Update enrollment status with validation
     */
    ProgramEnrollmentResponse updateEnrollmentStatus(Principal principal, Integer enrollmentId, 
                                                   String status);
    
    /**
     * Update student progress and notes
     */
    ProgramEnrollmentResponse updateStudentProgress(Principal principal, 
                                                  InstructorStudentManagementRequest request);
    
    /**
     * Get student details and history
     */
    Object getStudentDetails(Principal principal, Integer studentId);
    
    /**
     * Get student progress across all programs
     */
    Object getStudentProgress(Principal principal, Integer studentId);
    
    /**
     * Send message to student
     */
    void sendMessageToStudent(Principal principal, Integer studentId, String message);
    
    /**
     * Get student messages and communication history
     */
    List<Object> getStudentMessages(Principal principal, Integer studentId);
    
    /**
     * Add note to student profile
     */
    void addStudentNote(Principal principal, Integer studentId, String note);
    
    /**
     * Get student notes
     */
    List<Object> getStudentNotes(Principal principal, Integer studentId);
    
    /**
     * Update student feedback
     */
    void updateStudentFeedback(Principal principal, Integer enrollmentId, String feedback);
    
    /**
     * Get student analytics and performance metrics
     */
    Object getStudentAnalytics(Principal principal, Integer studentId);
    
    /**
     * Get top performing students
     */
    List<Object> getTopPerformingStudents(Principal principal, String period);
    
    /**
     * Get students at risk (low progress, inactive)
     */
    List<Object> getStudentsAtRisk(Principal principal);
    
    /**
     * Get student engagement metrics
     */
    Object getStudentEngagementMetrics(Principal principal, String period);
    
    /**
     * Export student data
     */
    byte[] exportStudentData(Principal principal, List<Integer> studentIds, String format);
    
    /**
     * Get student completion rates
     */
    Object getStudentCompletionRates(Principal principal, String period);
    
    /**
     * Get student satisfaction scores
     */
    Object getStudentSatisfactionScores(Principal principal, String period);
    
    /**
     * Send bulk message to students
     */
    void sendBulkMessageToStudents(Principal principal, List<Integer> studentIds, String message);
    
    /**
     * Get student activity timeline
     */
    List<Object> getStudentActivityTimeline(Principal principal, Integer studentId);
    
    /**
     * Validate student management request
     */
    void validateStudentManagementRequest(InstructorStudentManagementRequest request);
    
    /**
     * Check if instructor can manage student
     */
    boolean canManageStudent(Principal principal, Integer studentId);
}
