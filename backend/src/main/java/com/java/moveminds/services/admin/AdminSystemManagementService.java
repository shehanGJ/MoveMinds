package com.java.moveminds.services.admin;

import com.java.moveminds.dto.response.admin.AdminDashboardResponse;
import com.java.moveminds.dto.response.AdminStatsResponse;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * Service interface for admin system management operations.
 * Handles system-wide operations, analytics, and administrative functions.
 */
public interface AdminSystemManagementService {
    
    /**
     * Get comprehensive admin dashboard data with real-time statistics
     */
    AdminDashboardResponse getAdminDashboard(Principal principal);
    
    /**
     * Get basic admin statistics
     */
    AdminStatsResponse getAdminStats(Principal principal);
    
    /**
     * Get system health status and performance metrics
     */
    Map<String, Object> getSystemHealth(Principal principal);
    
    /**
     * Get system logs with filtering and pagination
     */
    List<String> getSystemLogs(Principal principal, int limit, String level, String category);
    
    /**
     * Clear system logs older than specified days
     */
    void clearOldLogs(Principal principal, int daysOld);
    
    /**
     * Get database statistics and performance metrics
     */
    Map<String, Object> getDatabaseStatistics(Principal principal);
    
    /**
     * Perform database maintenance operations
     */
    void performDatabaseMaintenance(Principal principal);
    
    /**
     * Get system configuration and settings
     */
    Map<String, Object> getSystemConfiguration(Principal principal);
    
    /**
     * Update system configuration
     */
    void updateSystemConfiguration(Principal principal, Map<String, Object> configuration);
    
    /**
     * Get system alerts and notifications
     */
    List<AdminDashboardResponse.SystemAlert> getSystemAlerts(Principal principal);
    
    /**
     * Resolve system alert
     */
    void resolveSystemAlert(Principal principal, String alertId);
    
    /**
     * Get backup status and create backup
     */
    Map<String, Object> getBackupStatus(Principal principal);
    
    /**
     * Create system backup
     */
    void createSystemBackup(Principal principal);
    
    /**
     * Restore system from backup
     */
    void restoreSystemFromBackup(Principal principal, String backupId);
    
    /**
     * Get system performance metrics
     */
    Map<String, Object> getPerformanceMetrics(Principal principal);
    
    /**
     * Get user activity analytics
     */
    Map<String, Object> getUserActivityAnalytics(Principal principal, String period);
    
    /**
     * Get revenue analytics and trends
     */
    Map<String, Object> getRevenueAnalytics(Principal principal, String period);
    
    /**
     * Get program analytics and trends
     */
    Map<String, Object> getProgramAnalytics(Principal principal, String period);
    
    /**
     * Send system-wide notification
     */
    void sendSystemNotification(Principal principal, String message, String type);
    
    /**
     * Get system audit trail
     */
    List<Object> getSystemAuditTrail(Principal principal, String action, String user);
}
