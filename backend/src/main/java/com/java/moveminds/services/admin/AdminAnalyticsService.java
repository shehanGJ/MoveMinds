package com.java.moveminds.services.admin;

import com.java.moveminds.dto.response.admin.AdminAnalyticsResponse;
import com.java.moveminds.dto.response.admin.UserGrowthAnalyticsResponse;
import com.java.moveminds.dto.response.admin.ProgramAnalyticsResponse;
import com.java.moveminds.dto.response.admin.RevenueAnalyticsResponse;

import java.security.Principal;
import java.time.LocalDate;

/**
 * Service interface for admin analytics operations.
 * Provides comprehensive analytics and reporting capabilities for administrators.
 */
public interface AdminAnalyticsService {
    
    /**
     * Get comprehensive analytics overview
     */
    AdminAnalyticsResponse getAnalyticsOverview(Principal principal);
    
    /**
     * Get user growth analytics for a specific period
     */
    UserGrowthAnalyticsResponse getUserGrowthAnalytics(Principal principal, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get program analytics including enrollments, ratings, and categories
     */
    ProgramAnalyticsResponse getProgramAnalytics(Principal principal);
    
    /**
     * Get revenue analytics for a specific period
     */
    RevenueAnalyticsResponse getRevenueAnalytics(Principal principal, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get real-time dashboard metrics
     */
    Object getRealTimeMetrics(Principal principal);
}
