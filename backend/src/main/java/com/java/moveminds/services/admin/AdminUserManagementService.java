package com.java.moveminds.services.admin;

import com.java.moveminds.dto.requests.admin.AdminBulkActionRequest;
import com.java.moveminds.dto.requests.admin.AdminUserManagementRequest;
import com.java.moveminds.dto.response.AdminUserResponse;
import com.java.moveminds.enums.Roles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.Principal;
import java.util.List;

/**
 * Service interface for comprehensive admin user management operations.
 * Follows single responsibility principle and provides clear separation of concerns.
 */
public interface AdminUserManagementService {
    
    /**
     * Get paginated list of users with advanced filtering and search capabilities
     */
    Page<AdminUserResponse> getAllUsers(Principal principal, Pageable pageable, 
                                       Roles role, String search, Boolean isVerified);
    
    /**
     * Get user details by ID with comprehensive information
     */
    AdminUserResponse getUserById(Principal principal, Integer userId);
    
    /**
     * Create a new user account with admin privileges
     */
    AdminUserResponse createUser(Principal principal, AdminUserManagementRequest request);
    
    /**
     * Update user information with validation and audit trail
     */
    AdminUserResponse updateUser(Principal principal, Integer userId, AdminUserManagementRequest request);
    
    /**
     * Update user role with proper authorization checks
     */
    AdminUserResponse updateUserRole(Principal principal, Integer userId, Roles newRole);
    
    /**
     * Activate or deactivate user account
     */
    AdminUserResponse updateUserStatus(Principal principal, Integer userId, boolean isActive);
    
    /**
     * Soft delete user account (mark as deleted, preserve data)
     */
    void softDeleteUser(Principal principal, Integer userId);
    
    /**
     * Hard delete user account (permanent removal)
     */
    void hardDeleteUser(Principal principal, Integer userId);
    
    /**
     * Restore soft-deleted user account
     */
    AdminUserResponse restoreUser(Principal principal, Integer userId);
    
    /**
     * Promote regular user to instructor role
     */
    AdminUserResponse promoteToInstructor(Principal principal, Integer userId);
    
    /**
     * Demote instructor to regular user role
     */
    AdminUserResponse demoteFromInstructor(Principal principal, Integer userId);
    
    /**
     * Get all instructors with their performance metrics
     */
    List<AdminUserResponse> getAllInstructors(Principal principal);
    
    /**
     * Get all admins in the system
     */
    List<AdminUserResponse> getAllAdmins(Principal principal);
    
    /**
     * Perform bulk actions on multiple users
     */
    void performBulkAction(Principal principal, AdminBulkActionRequest request);
    
    /**
     * Export user data in various formats (CSV, Excel, PDF)
     */
    byte[] exportUserData(Principal principal, List<Integer> userIds, String format);
    
    /**
     * Get user activity history and audit trail
     */
    List<Object> getUserActivityHistory(Principal principal, Integer userId);
    
    /**
     * Validate user data and business rules
     */
    void validateUserData(AdminUserManagementRequest request);
    
    /**
     * Check if user can be deleted (no active programs, enrollments, etc.)
     */
    boolean canDeleteUser(Integer userId);
    
    /**
     * Get user statistics and metrics
     */
    Object getUserStatistics(Principal principal);
}
