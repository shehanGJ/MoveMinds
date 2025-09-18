package com.java.moveminds.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.java.moveminds.dto.requests.FitnessProgramRequest;
import com.java.moveminds.dto.response.FitnessProgramListResponse;
import com.java.moveminds.dto.response.FitnessProgramResponse;
import com.java.moveminds.dto.response.InstructorStatsResponse;
import com.java.moveminds.dto.response.ProgramEnrollmentResponse;
import com.java.moveminds.services.InstructorService;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/instructor")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
public class InstructorController {
    
    private final InstructorService instructorService;

    /**
     * Get instructor dashboard statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<InstructorStatsResponse> getInstructorStats(Principal principal) {
        InstructorStatsResponse stats = instructorService.getInstructorStats(principal);
        return ResponseEntity.ok(stats);
    }

    /**
     * Create a new fitness program
     */
    @PostMapping("/programs")
    public ResponseEntity<FitnessProgramResponse> createProgram(
            @RequestPart("program") FitnessProgramRequest programRequest,
            @RequestPart("files") List<MultipartFile> files,
            Principal principal) {
        try {
            FitnessProgramResponse response = instructorService.createProgram(principal, programRequest, files);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update an existing fitness program
     */
    @PutMapping("/programs/{programId}")
    public ResponseEntity<FitnessProgramResponse> updateProgram(
            @PathVariable Integer programId,
            @RequestPart("program") FitnessProgramRequest programRequest,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestPart(value = "removedImages", required = false) List<String> removedImages,
            Principal principal) {
        try {
            FitnessProgramResponse response = instructorService.updateProgram(principal, programId, programRequest, files, removedImages);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete a fitness program
     */
    @DeleteMapping("/programs/{programId}")
    public ResponseEntity<Void> deleteProgram(
            @PathVariable Integer programId,
            Principal principal) {
        try {
            instructorService.deleteProgram(principal, programId);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get all programs created by the instructor
     */
    @GetMapping("/programs")
    public ResponseEntity<Page<FitnessProgramListResponse>> getMyPrograms(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", required = false) String sort,
            Principal principal) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FitnessProgramListResponse> programs = instructorService.getMyPrograms(principal, pageable, sort);
        return ResponseEntity.ok(programs);
    }

    /**
     * Get program details
     */
    @GetMapping("/programs/{programId}")
    public ResponseEntity<FitnessProgramResponse> getProgramDetails(
            @PathVariable Integer programId,
            Principal principal) {
        FitnessProgramResponse program = instructorService.getProgramDetails(principal, programId);
        return ResponseEntity.ok(program);
    }

    /**
     * Get enrollments for a specific program
     */
    @GetMapping("/programs/{programId}/enrollments")
    public ResponseEntity<List<ProgramEnrollmentResponse>> getProgramEnrollments(
            @PathVariable Integer programId,
            Principal principal) {
        List<ProgramEnrollmentResponse> enrollments = instructorService.getProgramEnrollments(principal, programId);
        return ResponseEntity.ok(enrollments);
    }

    /**
     * Get all enrollments across all instructor's programs
     */
    @GetMapping("/enrollments")
    public ResponseEntity<Page<ProgramEnrollmentResponse>> getAllEnrollments(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Principal principal) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProgramEnrollmentResponse> enrollments = instructorService.getAllEnrollments(principal, pageable);
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
        ProgramEnrollmentResponse enrollment = instructorService.updateEnrollmentStatus(principal, enrollmentId, status);
        return ResponseEntity.ok(enrollment);
    }

    /**
     * Get instructor's students
     */
    @GetMapping("/students")
    public ResponseEntity<Page<ProgramEnrollmentResponse>> getStudents(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Principal principal) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProgramEnrollmentResponse> students = instructorService.getStudents(principal, pageable);
        return ResponseEntity.ok(students);
    }
}
