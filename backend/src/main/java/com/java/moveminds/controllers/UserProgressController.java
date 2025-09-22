package com.java.moveminds.controllers;

import com.java.moveminds.dto.request.MarkLessonCompleteRequest;
import com.java.moveminds.dto.response.ProgramLearningProgressResponse;
import com.java.moveminds.dto.response.UserProgressResponse;
import com.java.moveminds.dto.response.UserProgramProgressResponse;
import com.java.moveminds.dto.response.UserProgressStatsResponse;
import com.java.moveminds.services.UserProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
@Slf4j
public class UserProgressController {
    
    private final UserProgressService userProgressService;
    
    /**
     * Mark a lesson as completed
     */
    @PostMapping("/lessons/complete")
    public ResponseEntity<UserProgressResponse> markLessonComplete(
            @Valid @RequestBody MarkLessonCompleteRequest request) {
        log.info("Marking lesson {} as complete", request.getLessonId());
        UserProgressResponse response = userProgressService.markLessonComplete(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Mark a lesson as incomplete
     */
    @PostMapping("/lessons/{lessonId}/incomplete")
    public ResponseEntity<UserProgressResponse> markLessonIncomplete(
            @PathVariable Integer lessonId) {
        log.info("Marking lesson {} as incomplete", lessonId);
        UserProgressResponse response = userProgressService.markLessonIncomplete(lessonId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get progress for a specific program
     */
    @GetMapping("/programs/{programId}")
    public ResponseEntity<ProgramLearningProgressResponse> getProgramProgress(
            @PathVariable Integer programId) {
        log.info("Getting progress for program {}", programId);
        ProgramLearningProgressResponse response = userProgressService.getProgramProgress(programId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get all program progress for the current user
     */
    @GetMapping("/programs")
    public ResponseEntity<List<UserProgramProgressResponse>> getAllUserProgress() {
        log.info("Getting all user progress");
        List<UserProgramProgressResponse> response = userProgressService.getAllUserProgress();
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get progress for a specific lesson
     */
    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<UserProgressResponse> getLessonProgress(
            @PathVariable Integer lessonId) {
        log.info("Getting progress for lesson {}", lessonId);
        UserProgressResponse response = userProgressService.getLessonProgress(lessonId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Update watch time for a lesson
     */
    @PutMapping("/lessons/{lessonId}/watch-time")
    public ResponseEntity<UserProgressResponse> updateWatchTime(
            @PathVariable Integer lessonId,
            @RequestParam Integer watchTimeSeconds) {
        log.info("Updating watch time for lesson {}: {} seconds", lessonId, watchTimeSeconds);
        UserProgressResponse response = userProgressService.updateWatchTime(lessonId, watchTimeSeconds);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Initialize program progress (called when user enrolls)
     */
    @PostMapping("/programs/{programId}/initialize")
    public ResponseEntity<Void> initializeProgramProgress(
            @PathVariable Integer programId) {
        log.info("Initializing program progress for program {}", programId);
        userProgressService.initializeProgramProgress(programId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Get user progress statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<UserProgressStatsResponse> getUserProgressStats() {
        log.info("Getting user progress statistics");
        UserProgressStatsResponse response = userProgressService.getUserProgressStats();
        return ResponseEntity.ok(response);
    }
    
    /**
     * Check if a lesson is completed
     */
    @GetMapping("/lessons/{lessonId}/completed")
    public ResponseEntity<Boolean> isLessonCompleted(
            @PathVariable Integer lessonId) {
        log.info("Checking if lesson {} is completed", lessonId);
        Boolean isCompleted = userProgressService.isLessonCompleted(lessonId);
        return ResponseEntity.ok(isCompleted);
    }
}
