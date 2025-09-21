package com.java.moveminds.controllers.admin;

import com.java.moveminds.dto.requests.admin.AdminBulkActionRequest;
import com.java.moveminds.dto.requests.admin.AdminUserManagementRequest;
import com.java.moveminds.dto.response.AdminUserResponse;
import com.java.moveminds.entities.UserEntity;
import com.java.moveminds.enums.Roles;
import com.java.moveminds.repositories.UserEntityRepository;
import com.java.moveminds.services.admin.AdminUserManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * REST Controller for comprehensive admin user management operations.
 * Follows RESTful principles and provides comprehensive user management capabilities.
 */
@Slf4j
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminUserManagementController {
    
    private final AdminUserManagementService adminUserManagementService;
    private final UserEntityRepository userRepository;
    
    /**
     * Get paginated list of users with advanced filtering
     */
    @GetMapping
    public ResponseEntity<Page<AdminUserResponse>> getAllUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "id") String sort,
            @RequestParam(value = "direction", defaultValue = "desc") String direction,
            @RequestParam(value = "role", required = false) Roles role,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "isVerified", required = false) Boolean isVerified,
            Principal principal) {
        
        log.info("Admin {} requesting users list with filters: role={}, search={}, isVerified={}", 
                principal.getName(), role, search, isVerified);
        
        Sort sortObj = Sort.by(Sort.Direction.fromString(direction), sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);
        
        Page<AdminUserResponse> users = adminUserManagementService.getAllUsers(
                principal, pageable, role, search, isVerified);
        
        return ResponseEntity.ok(users);
    }
    
    /**
     * Debug endpoint to check user verification status
     */
    @GetMapping("/debug/{userId}")
    public ResponseEntity<Map<String, Object>> debugUserStatus(@PathVariable Integer userId, Principal principal) {
        log.info("Admin {} requesting debug info for user ID: {}", principal.getName(), userId);
        
        Map<String, Object> debugInfo = new HashMap<>();
        
        try {
            UserEntity user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                debugInfo.put("userId", user.getId());
                debugInfo.put("username", user.getUsername());
                debugInfo.put("isVerified", user.isVerified());
                debugInfo.put("email", user.getEmail());
                debugInfo.put("role", user.getRole());
            } else {
                debugInfo.put("error", "User not found");
            }
        } catch (Exception e) {
            debugInfo.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(debugInfo);
    }
    
    /**
     * Get user details by ID
     */
    @GetMapping("/{userId}")
    public ResponseEntity<AdminUserResponse> getUserById(
            @PathVariable Integer userId,
            Principal principal) {
        
        log.info("Admin {} requesting user details for ID: {}", principal.getName(), userId);
        
        AdminUserResponse user = adminUserManagementService.getUserById(principal, userId);
        return ResponseEntity.ok(user);
    }
    
    /**
     * Create new user account
     */
    @PostMapping
    public ResponseEntity<AdminUserResponse> createUser(
            @Valid @RequestBody AdminUserManagementRequest request,
            Principal principal) {
        
        log.info("Admin {} creating new user: {}", principal.getName(), request.getUsername());
        
        AdminUserResponse createdUser = adminUserManagementService.createUser(principal, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    
    /**
     * Update user information
     */
    @PutMapping("/{userId}")
    public ResponseEntity<AdminUserResponse> updateUser(
            @PathVariable Integer userId,
            @Valid @RequestBody AdminUserManagementRequest request,
            Principal principal) {
        
        log.info("Admin {} updating user ID: {}", principal.getName(), userId);
        
        AdminUserResponse updatedUser = adminUserManagementService.updateUser(principal, userId, request);
        return ResponseEntity.ok(updatedUser);
    }
    
    /**
     * Update user role
     */
    @PutMapping("/{userId}/role")
    public ResponseEntity<AdminUserResponse> updateUserRole(
            @PathVariable Integer userId,
            @RequestParam Roles newRole,
            Principal principal) {
        
        log.info("Admin {} updating role for user ID: {} to {}", 
                principal.getName(), userId, newRole);
        
        AdminUserResponse updatedUser = adminUserManagementService.updateUserRole(principal, userId, newRole);
        return ResponseEntity.ok(updatedUser);
    }
    
    /**
     * Update user status (activate/deactivate)
     */
    @PutMapping("/{userId}/status")
    public ResponseEntity<AdminUserResponse> updateUserStatus(
            @PathVariable Integer userId,
            @RequestParam boolean isActive,
            Principal principal) {
        
        log.info("Admin {} updating status for user ID: {} to {}", 
                principal.getName(), userId, isActive);
        
        AdminUserResponse updatedUser = adminUserManagementService.updateUserStatus(principal, userId, isActive);
        return ResponseEntity.ok(updatedUser);
    }
    
    /**
     * Soft delete user account
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> softDeleteUser(
            @PathVariable Integer userId,
            Principal principal) {
        
        log.info("Admin {} soft deleting user ID: {}", principal.getName(), userId);
        
        adminUserManagementService.softDeleteUser(principal, userId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Hard delete user account
     */
    @DeleteMapping("/{userId}/permanent")
    public ResponseEntity<Void> hardDeleteUser(
            @PathVariable Integer userId,
            Principal principal) {
        
        log.info("Admin {} permanently deleting user ID: {}", principal.getName(), userId);
        
        adminUserManagementService.hardDeleteUser(principal, userId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Restore soft-deleted user
     */
    @PostMapping("/{userId}/restore")
    public ResponseEntity<AdminUserResponse> restoreUser(
            @PathVariable Integer userId,
            Principal principal) {
        
        log.info("Admin {} restoring user ID: {}", principal.getName(), userId);
        
        AdminUserResponse restoredUser = adminUserManagementService.restoreUser(principal, userId);
        return ResponseEntity.ok(restoredUser);
    }
    
    /**
     * Promote user to instructor
     */
    @PostMapping("/{userId}/promote-instructor")
    public ResponseEntity<AdminUserResponse> promoteToInstructor(
            @PathVariable Integer userId,
            Principal principal) {
        
        log.info("Admin {} promoting user ID: {} to instructor", principal.getName(), userId);
        
        AdminUserResponse updatedUser = adminUserManagementService.promoteToInstructor(principal, userId);
        return ResponseEntity.ok(updatedUser);
    }
    
    /**
     * Demote instructor to regular user
     */
    @PostMapping("/{userId}/demote-instructor")
    public ResponseEntity<AdminUserResponse> demoteFromInstructor(
            @PathVariable Integer userId,
            Principal principal) {
        
        log.info("Admin {} demoting instructor ID: {} to regular user", principal.getName(), userId);
        
        AdminUserResponse updatedUser = adminUserManagementService.demoteFromInstructor(principal, userId);
        return ResponseEntity.ok(updatedUser);
    }
    
    /**
     * Get all instructors
     */
    @GetMapping("/instructors")
    public ResponseEntity<List<AdminUserResponse>> getAllInstructors(Principal principal) {
        
        log.info("Admin {} requesting all instructors", principal.getName());
        
        List<AdminUserResponse> instructors = adminUserManagementService.getAllInstructors(principal);
        return ResponseEntity.ok(instructors);
    }
    
    /**
     * Get all admins
     */
    @GetMapping("/admins")
    public ResponseEntity<List<AdminUserResponse>> getAllAdmins(Principal principal) {
        
        log.info("Admin {} requesting all admins", principal.getName());
        
        List<AdminUserResponse> admins = adminUserManagementService.getAllAdmins(principal);
        return ResponseEntity.ok(admins);
    }
    
    /**
     * Perform bulk actions on users
     */
    @PostMapping("/bulk-action")
    public ResponseEntity<Void> performBulkAction(
            @Valid @RequestBody AdminBulkActionRequest request,
            Principal principal) {
        
        log.info("Admin {} performing bulk action: {} on {} users", 
                principal.getName(), request.getAction(), request.getUserIds().size());
        
        adminUserManagementService.performBulkAction(principal, request);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Export user data
     */
    @GetMapping(value = "/export", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> exportUserData(
            @RequestParam List<Integer> userIds,
            @RequestParam(defaultValue = "csv") String format,
            Principal principal) {
        
        log.info("Admin {} exporting user data for {} users in {} format", 
                principal.getName(), userIds.size(), format);
        
        byte[] data = adminUserManagementService.exportUserData(principal, userIds, format);
        
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=users_export." + format)
                .body(data);
    }
    
    /**
     * Get user activity history
     */
    @GetMapping("/{userId}/activity")
    public ResponseEntity<List<Object>> getUserActivityHistory(
            @PathVariable Integer userId,
            Principal principal) {
        
        log.info("Admin {} requesting activity history for user ID: {}", principal.getName(), userId);
        
        List<Object> activityHistory = adminUserManagementService.getUserActivityHistory(principal, userId);
        return ResponseEntity.ok(activityHistory);
    }
    
    /**
     * Get user statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Object> getUserStatistics(Principal principal) {
        
        log.info("Admin {} requesting user statistics", principal.getName());
        
        Object statistics = adminUserManagementService.getUserStatistics(principal);
        return ResponseEntity.ok(statistics);
    }
}
