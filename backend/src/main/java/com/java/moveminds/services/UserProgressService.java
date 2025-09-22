package com.java.moveminds.services;

import com.java.moveminds.dto.request.MarkLessonCompleteRequest;
import com.java.moveminds.dto.response.ProgramLearningProgressResponse;
import com.java.moveminds.dto.response.UserProgressResponse;
import com.java.moveminds.dto.response.UserProgramProgressResponse;
import com.java.moveminds.dto.response.UserProgressStatsResponse;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface UserProgressService {
    
    /**
     * Mark a lesson as completed for the current user
     */
    @PreAuthorize("hasRole('USER')")
    UserProgressResponse markLessonComplete(MarkLessonCompleteRequest request);
    
    /**
     * Mark a lesson as incomplete for the current user
     */
    @PreAuthorize("hasRole('USER')")
    UserProgressResponse markLessonIncomplete(Integer lessonId);
    
    /**
     * Get progress for a specific program for the current user
     */
    @PreAuthorize("hasRole('USER')")
    ProgramLearningProgressResponse getProgramProgress(Integer programId);
    
    /**
     * Get all program progress for the current user
     */
    @PreAuthorize("hasRole('USER')")
    List<UserProgramProgressResponse> getAllUserProgress();
    
    /**
     * Get progress for a specific lesson for the current user
     */
    @PreAuthorize("hasRole('USER')")
    UserProgressResponse getLessonProgress(Integer lessonId);
    
    /**
     * Update watch time for a lesson
     */
    @PreAuthorize("hasRole('USER')")
    UserProgressResponse updateWatchTime(Integer lessonId, Integer watchTimeSeconds);
    
    /**
     * Initialize progress tracking for a user when they enroll in a program
     */
    @PreAuthorize("hasRole('USER')")
    void initializeProgramProgress(Integer programId);
    
    /**
     * Get completion statistics for the current user
     */
    @PreAuthorize("hasRole('USER')")
    UserProgressStatsResponse getUserProgressStats();
    
    /**
     * Check if a lesson is completed by the current user
     */
    @PreAuthorize("hasRole('USER')")
    Boolean isLessonCompleted(Integer lessonId);
    
    /**
     * Get progress for instructor to see student progress
     */
    @PreAuthorize("hasRole('INSTRUCTOR')")
    List<UserProgressResponse> getStudentProgress(Integer programId, Integer userId);
    
    /**
     * Get all students' progress for a program (instructor view)
     */
    @PreAuthorize("hasRole('INSTRUCTOR')")
    List<UserProgressResponse> getAllStudentsProgress(Integer programId);
}
