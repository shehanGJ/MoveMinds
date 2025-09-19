package com.java.moveminds.controllers.instructor;

import com.java.moveminds.dto.response.instructor.InstructorDashboardResponse;
import com.java.moveminds.dto.response.InstructorStatsResponse;
import com.java.moveminds.services.instructor.InstructorDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * REST Controller for instructor dashboard operations.
 * Provides comprehensive dashboard data and analytics for instructors.
 */
@Slf4j
@RestController
@RequestMapping("/instructor/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
public class InstructorDashboardController {
    
    private final InstructorDashboardService instructorDashboardService;
    
    /**
     * Get comprehensive instructor dashboard data
     */
    @GetMapping
    public ResponseEntity<InstructorDashboardResponse> getInstructorDashboard(Principal principal) {
        
        log.info("Instructor {} requesting dashboard data", principal.getName());
        
        InstructorDashboardResponse dashboard = instructorDashboardService.getInstructorDashboard(principal);
        return ResponseEntity.ok(dashboard);
    }
    
    /**
     * Get basic instructor statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<InstructorStatsResponse> getInstructorStats(Principal principal) {
        
        log.info("Instructor {} requesting basic statistics", principal.getName());
        
        InstructorStatsResponse stats = instructorDashboardService.getInstructorStats(principal);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Get recent enrollments
     */
    @GetMapping("/recent-enrollments")
    public ResponseEntity<Object> getRecentEnrollments(
            @RequestParam(defaultValue = "10") int limit,
            Principal principal) {
        
        log.info("Instructor {} requesting recent enrollments: limit={}", principal.getName(), limit);
        
        Object recentEnrollments = instructorDashboardService.getRecentEnrollments(principal, limit);
        return ResponseEntity.ok(recentEnrollments);
    }
    
    /**
     * Get program performance overview
     */
    @GetMapping("/program-performance")
    public ResponseEntity<Object> getProgramPerformance(Principal principal) {
        
        log.info("Instructor {} requesting program performance overview", principal.getName());
        
        Object performance = instructorDashboardService.getProgramPerformance(principal);
        return ResponseEntity.ok(performance);
    }
    
    /**
     * Get student progress overview
     */
    @GetMapping("/student-progress")
    public ResponseEntity<Object> getStudentProgress(Principal principal) {
        
        log.info("Instructor {} requesting student progress overview", principal.getName());
        
        Object progress = instructorDashboardService.getStudentProgress(principal);
        return ResponseEntity.ok(progress);
    }
    
    /**
     * Get revenue trends
     */
    @GetMapping("/revenue-trends")
    public ResponseEntity<Object> getRevenueTrends(
            @RequestParam(defaultValue = "30d") String period,
            Principal principal) {
        
        log.info("Instructor {} requesting revenue trends for period: {}", principal.getName(), period);
        
        Object trends = instructorDashboardService.getRevenueTrends(principal, period);
        return ResponseEntity.ok(trends);
    }
    
    /**
     * Get upcoming deadlines
     */
    @GetMapping("/upcoming-deadlines")
    public ResponseEntity<Object> getUpcomingDeadlines(Principal principal) {
        
        log.info("Instructor {} requesting upcoming deadlines", principal.getName());
        
        Object deadlines = instructorDashboardService.getUpcomingDeadlines(principal);
        return ResponseEntity.ok(deadlines);
    }
    
    /**
     * Get instructor performance metrics
     */
    @GetMapping("/performance-metrics")
    public ResponseEntity<Object> getPerformanceMetrics(
            @RequestParam(defaultValue = "30d") String period,
            Principal principal) {
        
        log.info("Instructor {} requesting performance metrics for period: {}", principal.getName(), period);
        
        Object metrics = instructorDashboardService.getPerformanceMetrics(principal, period);
        return ResponseEntity.ok(metrics);
    }
    
    /**
     * Get instructor goals and targets
     */
    @GetMapping("/goals")
    public ResponseEntity<Object> getInstructorGoals(Principal principal) {
        
        log.info("Instructor {} requesting goals and targets", principal.getName());
        
        Object goals = instructorDashboardService.getInstructorGoals(principal);
        return ResponseEntity.ok(goals);
    }
    
    /**
     * Update instructor goals
     */
    @PutMapping("/goals")
    public ResponseEntity<Object> updateInstructorGoals(
            @RequestBody Object goals,
            Principal principal) {
        
        log.info("Instructor {} updating goals and targets", principal.getName());
        
        Object updatedGoals = instructorDashboardService.updateInstructorGoals(principal, goals);
        return ResponseEntity.ok(updatedGoals);
    }
    
    /**
     * Get instructor notifications
     */
    @GetMapping("/notifications")
    public ResponseEntity<Object> getInstructorNotifications(Principal principal) {
        
        log.info("Instructor {} requesting notifications", principal.getName());
        
        Object notifications = instructorDashboardService.getInstructorNotifications(principal);
        return ResponseEntity.ok(notifications);
    }
    
    /**
     * Mark notification as read
     */
    @PutMapping("/notifications/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(
            @PathVariable String notificationId,
            Principal principal) {
        
        log.info("Instructor {} marking notification as read: {}", principal.getName(), notificationId);
        
        instructorDashboardService.markNotificationAsRead(principal, notificationId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Get instructor calendar events
     */
    @GetMapping("/calendar")
    public ResponseEntity<Object> getInstructorCalendar(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Principal principal) {
        
        log.info("Instructor {} requesting calendar events: start={}, end={}", 
                principal.getName(), startDate, endDate);
        
        Object calendar = instructorDashboardService.getInstructorCalendar(principal, startDate, endDate);
        return ResponseEntity.ok(calendar);
    }
    
    /**
     * Get instructor insights and recommendations
     */
    @GetMapping("/insights")
    public ResponseEntity<Object> getInstructorInsights(Principal principal) {
        
        log.info("Instructor {} requesting insights and recommendations", principal.getName());
        
        Object insights = instructorDashboardService.getInstructorInsights(principal);
        return ResponseEntity.ok(insights);
    }
}
