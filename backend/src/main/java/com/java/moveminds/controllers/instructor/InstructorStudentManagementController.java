package com.java.moveminds.controllers.instructor;

import com.java.moveminds.dto.requests.instructor.InstructorStudentManagementRequest;
import com.java.moveminds.dto.response.ProgramEnrollmentResponse;
import com.java.moveminds.services.instructor.InstructorStudentManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * REST Controller for instructor student management operations.
 * Handles all student-related operations for instructors.
 */
@Slf4j
@RestController
@RequestMapping("/instructor/students")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
public class InstructorStudentManagementController {
    
    private final InstructorStudentManagementService instructorStudentManagementService;
    
    /**
     * Get all students with filtering and search
     */
    @GetMapping
    public ResponseEntity<Page<ProgramEnrollmentResponse>> getStudents(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "enrollmentDate") String sort,
            @RequestParam(value = "direction", defaultValue = "desc") String direction,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "program", required = false) String program,
            Principal principal) {
        
        log.info("Instructor {} requesting students: search={}, status={}, program={}", 
                principal.getName(), search, status, program);
        
        Sort sortObj = Sort.by(Sort.Direction.fromString(direction), sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);
        
        Page<ProgramEnrollmentResponse> students = instructorStudentManagementService.getStudents(
                principal, pageable, search, status, program);
        
        return ResponseEntity.ok(students);
    }
    
    /**
     * Get enrollments for specific program
     */
    @GetMapping("/programs/{programId}/enrollments")
    public ResponseEntity<List<ProgramEnrollmentResponse>> getProgramEnrollments(
            @PathVariable Integer programId,
            Principal principal) {
        
        log.info("Instructor {} requesting enrollments for program ID: {}", principal.getName(), programId);
        
        List<ProgramEnrollmentResponse> enrollments = instructorStudentManagementService.getProgramEnrollments(
                principal, programId);
        return ResponseEntity.ok(enrollments);
    }
    
    /**
     * Get all enrollments across instructor's programs
     */
    @GetMapping("/enrollments")
    public ResponseEntity<Page<ProgramEnrollmentResponse>> getAllEnrollments(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "program", required = false) String program,
            Principal principal) {
        
        log.info("Instructor {} requesting all enrollments: search={}, status={}, program={}", 
                principal.getName(), search, status, program);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProgramEnrollmentResponse> enrollments = instructorStudentManagementService.getAllEnrollments(
                principal, pageable, search, status, program);
        
        return ResponseEntity.ok(enrollments);
    }
    
    /**
     * Update enrollment status
     */
    @PutMapping("/enrollments/{enrollmentId}/status")
    public ResponseEntity<ProgramEnrollmentResponse> updateEnrollmentStatus(
            @PathVariable Integer enrollmentId,
            @RequestParam String status,
            Principal principal) {
        
        log.info("Instructor {} updating enrollment ID: {} status to {}", 
                principal.getName(), enrollmentId, status);
        
        ProgramEnrollmentResponse enrollment = instructorStudentManagementService.updateEnrollmentStatus(
                principal, enrollmentId, status);
        return ResponseEntity.ok(enrollment);
    }
    
    /**
     * Update student progress
     */
    @PutMapping("/enrollments/{enrollmentId}/progress")
    public ResponseEntity<ProgramEnrollmentResponse> updateStudentProgress(
            @PathVariable Integer enrollmentId,
            @Valid @RequestBody InstructorStudentManagementRequest request,
            Principal principal) {
        
        log.info("Instructor {} updating progress for enrollment ID: {}", principal.getName(), enrollmentId);
        
        ProgramEnrollmentResponse enrollment = instructorStudentManagementService.updateStudentProgress(
                principal, request);
        return ResponseEntity.ok(enrollment);
    }
    
    /**
     * Get student details
     */
    @GetMapping("/{studentId}")
    public ResponseEntity<Object> getStudentDetails(
            @PathVariable Integer studentId,
            Principal principal) {
        
        log.info("Instructor {} requesting details for student ID: {}", principal.getName(), studentId);
        
        Object studentDetails = instructorStudentManagementService.getStudentDetails(principal, studentId);
        return ResponseEntity.ok(studentDetails);
    }
    
    /**
     * Get student progress
     */
    @GetMapping("/{studentId}/progress")
    public ResponseEntity<Object> getStudentProgress(
            @PathVariable Integer studentId,
            Principal principal) {
        
        log.info("Instructor {} requesting progress for student ID: {}", principal.getName(), studentId);
        
        Object progress = instructorStudentManagementService.getStudentProgress(principal, studentId);
        return ResponseEntity.ok(progress);
    }
    
    /**
     * Send message to student
     */
    @PostMapping("/{studentId}/messages")
    public ResponseEntity<Void> sendMessageToStudent(
            @PathVariable Integer studentId,
            @RequestParam String message,
            Principal principal) {
        
        log.info("Instructor {} sending message to student ID: {}", principal.getName(), studentId);
        
        instructorStudentManagementService.sendMessageToStudent(principal, studentId, message);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Get student messages
     */
    @GetMapping("/{studentId}/messages")
    public ResponseEntity<List<Object>> getStudentMessages(
            @PathVariable Integer studentId,
            Principal principal) {
        
        log.info("Instructor {} requesting messages for student ID: {}", principal.getName(), studentId);
        
        List<Object> messages = instructorStudentManagementService.getStudentMessages(principal, studentId);
        return ResponseEntity.ok(messages);
    }
    
    /**
     * Add note to student
     */
    @PostMapping("/{studentId}/notes")
    public ResponseEntity<Void> addStudentNote(
            @PathVariable Integer studentId,
            @RequestParam String note,
            Principal principal) {
        
        log.info("Instructor {} adding note to student ID: {}", principal.getName(), studentId);
        
        instructorStudentManagementService.addStudentNote(principal, studentId, note);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Get student notes
     */
    @GetMapping("/{studentId}/notes")
    public ResponseEntity<List<Object>> getStudentNotes(
            @PathVariable Integer studentId,
            Principal principal) {
        
        log.info("Instructor {} requesting notes for student ID: {}", principal.getName(), studentId);
        
        List<Object> notes = instructorStudentManagementService.getStudentNotes(principal, studentId);
        return ResponseEntity.ok(notes);
    }
    
    /**
     * Update student feedback
     */
    @PutMapping("/enrollments/{enrollmentId}/feedback")
    public ResponseEntity<Void> updateStudentFeedback(
            @PathVariable Integer enrollmentId,
            @RequestParam String feedback,
            Principal principal) {
        
        log.info("Instructor {} updating feedback for enrollment ID: {}", principal.getName(), enrollmentId);
        
        instructorStudentManagementService.updateStudentFeedback(principal, enrollmentId, feedback);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Get student analytics
     */
    @GetMapping("/{studentId}/analytics")
    public ResponseEntity<Object> getStudentAnalytics(
            @PathVariable Integer studentId,
            Principal principal) {
        
        log.info("Instructor {} requesting analytics for student ID: {}", principal.getName(), studentId);
        
        Object analytics = instructorStudentManagementService.getStudentAnalytics(principal, studentId);
        return ResponseEntity.ok(analytics);
    }
    
    /**
     * Get top performing students
     */
    @GetMapping("/top-performing")
    public ResponseEntity<List<Object>> getTopPerformingStudents(
            @RequestParam(defaultValue = "30d") String period,
            Principal principal) {
        
        log.info("Instructor {} requesting top performing students for period: {}", principal.getName(), period);
        
        List<Object> topStudents = instructorStudentManagementService.getTopPerformingStudents(principal, period);
        return ResponseEntity.ok(topStudents);
    }
    
    /**
     * Get students at risk
     */
    @GetMapping("/at-risk")
    public ResponseEntity<List<Object>> getStudentsAtRisk(Principal principal) {
        
        log.info("Instructor {} requesting students at risk", principal.getName());
        
        List<Object> atRiskStudents = instructorStudentManagementService.getStudentsAtRisk(principal);
        return ResponseEntity.ok(atRiskStudents);
    }
    
    /**
     * Get student engagement metrics
     */
    @GetMapping("/engagement-metrics")
    public ResponseEntity<Object> getStudentEngagementMetrics(
            @RequestParam(defaultValue = "30d") String period,
            Principal principal) {
        
        log.info("Instructor {} requesting engagement metrics for period: {}", principal.getName(), period);
        
        Object metrics = instructorStudentManagementService.getStudentEngagementMetrics(principal, period);
        return ResponseEntity.ok(metrics);
    }
    
    /**
     * Export student data
     */
    @GetMapping(value = "/export", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> exportStudentData(
            @RequestParam List<Integer> studentIds,
            @RequestParam(defaultValue = "csv") String format,
            Principal principal) {
        
        log.info("Instructor {} exporting student data for {} students in {} format", 
                principal.getName(), studentIds.size(), format);
        
        byte[] data = instructorStudentManagementService.exportStudentData(principal, studentIds, format);
        
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=students_export." + format)
                .body(data);
    }
    
    /**
     * Get student completion rates
     */
    @GetMapping("/completion-rates")
    public ResponseEntity<Object> getStudentCompletionRates(
            @RequestParam(defaultValue = "30d") String period,
            Principal principal) {
        
        log.info("Instructor {} requesting completion rates for period: {}", principal.getName(), period);
        
        Object completionRates = instructorStudentManagementService.getStudentCompletionRates(principal, period);
        return ResponseEntity.ok(completionRates);
    }
    
    /**
     * Get student satisfaction scores
     */
    @GetMapping("/satisfaction-scores")
    public ResponseEntity<Object> getStudentSatisfactionScores(
            @RequestParam(defaultValue = "30d") String period,
            Principal principal) {
        
        log.info("Instructor {} requesting satisfaction scores for period: {}", principal.getName(), period);
        
        Object satisfactionScores = instructorStudentManagementService.getStudentSatisfactionScores(principal, period);
        return ResponseEntity.ok(satisfactionScores);
    }
    
    /**
     * Send bulk message to students
     */
    @PostMapping("/bulk-message")
    public ResponseEntity<Void> sendBulkMessageToStudents(
            @RequestParam List<Integer> studentIds,
            @RequestParam String message,
            Principal principal) {
        
        log.info("Instructor {} sending bulk message to {} students", principal.getName(), studentIds.size());
        
        instructorStudentManagementService.sendBulkMessageToStudents(principal, studentIds, message);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Get student activity timeline
     */
    @GetMapping("/{studentId}/activity-timeline")
    public ResponseEntity<List<Object>> getStudentActivityTimeline(
            @PathVariable Integer studentId,
            Principal principal) {
        
        log.info("Instructor {} requesting activity timeline for student ID: {}", principal.getName(), studentId);
        
        List<Object> timeline = instructorStudentManagementService.getStudentActivityTimeline(principal, studentId);
        return ResponseEntity.ok(timeline);
    }
}
