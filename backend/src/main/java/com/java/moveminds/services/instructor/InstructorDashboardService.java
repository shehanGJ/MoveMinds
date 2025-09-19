package com.java.moveminds.services.instructor;

import com.java.moveminds.dto.response.instructor.InstructorDashboardResponse;
import com.java.moveminds.dto.response.InstructorStatsResponse;

import java.security.Principal;

/**
 * Service interface for instructor dashboard operations.
 * Provides comprehensive dashboard data and analytics for instructors.
 */
public interface InstructorDashboardService {
    
    /**
     * Get comprehensive instructor dashboard data
     */
    InstructorDashboardResponse getInstructorDashboard(Principal principal);
    
    /**
     * Get basic instructor statistics
     */
    InstructorStatsResponse getInstructorStats(Principal principal);
    
    /**
     * Get recent enrollments
     */
    Object getRecentEnrollments(Principal principal, int limit);
    
    /**
     * Get program performance overview
     */
    Object getProgramPerformance(Principal principal);
    
    /**
     * Get student progress overview
     */
    Object getStudentProgress(Principal principal);
    
    /**
     * Get revenue trends
     */
    Object getRevenueTrends(Principal principal, String period);
    
    /**
     * Get upcoming deadlines
     */
    Object getUpcomingDeadlines(Principal principal);
    
    /**
     * Get instructor performance metrics
     */
    Object getPerformanceMetrics(Principal principal, String period);
    
    /**
     * Get instructor goals and targets
     */
    Object getInstructorGoals(Principal principal);
    
    /**
     * Update instructor goals
     */
    Object updateInstructorGoals(Principal principal, Object goals);
    
    /**
     * Get instructor notifications
     */
    Object getInstructorNotifications(Principal principal);
    
    /**
     * Mark notification as read
     */
    void markNotificationAsRead(Principal principal, String notificationId);
    
    /**
     * Get instructor calendar events
     */
    Object getInstructorCalendar(Principal principal, String startDate, String endDate);
    
    /**
     * Get instructor insights and recommendations
     */
    Object getInstructorInsights(Principal principal);
}
