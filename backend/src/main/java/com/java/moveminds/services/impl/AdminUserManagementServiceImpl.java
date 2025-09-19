package com.java.moveminds.services.impl;

import com.java.moveminds.dto.requests.admin.AdminBulkActionRequest;
import com.java.moveminds.dto.requests.admin.AdminUserManagementRequest;
import com.java.moveminds.dto.response.AdminUserResponse;
import com.java.moveminds.entities.UserEntity;
import com.java.moveminds.enums.Roles;
import com.java.moveminds.exceptions.UserNotFoundException;
import com.java.moveminds.repositories.UserEntityRepository;
import com.java.moveminds.repositories.specifications.AdminUserSpecification;
import com.java.moveminds.services.admin.AdminUserManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of AdminUserManagementService with comprehensive business logic.
 * Follows industry best practices for user management operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserManagementServiceImpl implements AdminUserManagementService {
    
    private final UserEntityRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Page<AdminUserResponse> getAllUsers(Principal principal, Pageable pageable, 
                                             Roles role, String search, Boolean isActivated) {
        log.info("Admin {} requesting users with filters: role={}, search={}, isActivated={}", 
                principal.getName(), role, search, isActivated);
        
        // Build specification for filtering
        Specification<UserEntity> spec = AdminUserSpecification.buildSpecification(
                role, search, isActivated, null, null, null, null, null);
        
        Page<UserEntity> users = userRepository.findAll(spec, pageable);
        
        log.info("Found {} users out of {} total", users.getNumberOfElements(), users.getTotalElements());
        
        return users.map(this::convertToAdminUserResponse);
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AdminUserResponse getUserById(Principal principal, Integer userId) {
        log.info("Admin {} requesting user details for ID: {}", principal.getName(), userId);
        
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        return convertToAdminUserResponse(user);
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AdminUserResponse createUser(Principal principal, AdminUserManagementRequest request) {
        log.info("Admin {} creating new user: {}", principal.getName(), request.getUsername());
        
        validateUserData(request);
        
        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }
        
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }
        
        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(request.getRole());
        user.setBiography(request.getBiography());
        user.setAvatarUrl(request.getAvatarUrl());
        user.setActivated(request.getIsActivated() != null ? request.getIsActivated() : true);
        user.setPassword(passwordEncoder.encode("TempPassword123!")); // Default password
        
        UserEntity savedUser = userRepository.save(user);
        
        log.info("Admin {} successfully created user with ID: {}", principal.getName(), savedUser.getId());
        
        return convertToAdminUserResponse(savedUser);
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AdminUserResponse updateUser(Principal principal, Integer userId, AdminUserManagementRequest request) {
        log.info("Admin {} updating user ID: {}", principal.getName(), userId);
        
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        validateUserData(request);
        
        // Check username uniqueness (excluding current user)
        userRepository.findByUsername(request.getUsername())
                .ifPresent(existingUser -> {
                    if (!existingUser.getId().equals(userId)) {
                        throw new IllegalArgumentException("Username already exists: " + request.getUsername());
                    }
                });
        
        // Check email uniqueness (excluding current user)
        userRepository.findByEmail(request.getEmail())
                .ifPresent(existingUser -> {
                    if (!existingUser.getId().equals(userId)) {
                        throw new IllegalArgumentException("Email already exists: " + request.getEmail());
                    }
                });
        
        // Update user fields
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setBiography(request.getBiography());
        user.setAvatarUrl(request.getAvatarUrl());
        
        if (request.getIsActivated() != null) {
            user.setActivated(request.getIsActivated());
        }
        
        UserEntity updatedUser = userRepository.save(user);
        
        log.info("Admin {} successfully updated user ID: {}", principal.getName(), userId);
        
        return convertToAdminUserResponse(updatedUser);
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AdminUserResponse updateUserRole(Principal principal, Integer userId, Roles newRole) {
        log.info("Admin {} updating role for user ID: {} to {}", principal.getName(), userId, newRole);
        
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        // Prevent admin from changing their own role
        if (user.getUsername().equals(principal.getName()) && newRole != Roles.ADMIN) {
            throw new IllegalArgumentException("Cannot change your own role");
        }
        
        user.setRole(newRole);
        UserEntity updatedUser = userRepository.save(user);
        
        log.info("Admin {} successfully updated role for user ID: {} to {}", 
                principal.getName(), userId, newRole);
        
        return convertToAdminUserResponse(updatedUser);
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AdminUserResponse updateUserStatus(Principal principal, Integer userId, boolean isActive) {
        log.info("Admin {} updating status for user ID: {} to {}", principal.getName(), userId, isActive);
        
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        // Prevent admin from deactivating themselves
        if (user.getUsername().equals(principal.getName()) && !isActive) {
            throw new IllegalArgumentException("Cannot deactivate your own account");
        }
        
        user.setActivated(isActive);
        UserEntity updatedUser = userRepository.save(user);
        
        log.info("Admin {} successfully updated status for user ID: {} to {}", 
                principal.getName(), userId, isActive);
        
        return convertToAdminUserResponse(updatedUser);
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void softDeleteUser(Principal principal, Integer userId) {
        log.info("Admin {} soft deleting user ID: {}", principal.getName(), userId);
        
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        // Prevent admin from deleting themselves
        if (user.getUsername().equals(principal.getName())) {
            throw new IllegalArgumentException("Cannot delete your own account");
        }
        
        if (!canDeleteUser(userId)) {
            throw new IllegalArgumentException("Cannot delete user with active programs or enrollments");
        }
        
        user.setActivated(false);
        userRepository.save(user);
        
        log.info("Admin {} successfully soft deleted user ID: {}", principal.getName(), userId);
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void hardDeleteUser(Principal principal, Integer userId) {
        log.info("Admin {} permanently deleting user ID: {}", principal.getName(), userId);
        
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        // Prevent admin from deleting themselves
        if (user.getUsername().equals(principal.getName())) {
            throw new IllegalArgumentException("Cannot delete your own account");
        }
        
        if (!canDeleteUser(userId)) {
            throw new IllegalArgumentException("Cannot delete user with active programs or enrollments");
        }
        
        userRepository.delete(user);
        
        log.info("Admin {} successfully permanently deleted user ID: {}", principal.getName(), userId);
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AdminUserResponse restoreUser(Principal principal, Integer userId) {
        log.info("Admin {} restoring user ID: {}", principal.getName(), userId);
        
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        user.setActivated(true);
        UserEntity restoredUser = userRepository.save(user);
        
        log.info("Admin {} successfully restored user ID: {}", principal.getName(), userId);
        
        return convertToAdminUserResponse(restoredUser);
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AdminUserResponse promoteToInstructor(Principal principal, Integer userId) {
        log.info("Admin {} promoting user ID: {} to instructor", principal.getName(), userId);
        
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        if (user.getRole() == Roles.INSTRUCTOR) {
            throw new IllegalArgumentException("User is already an instructor");
        }
        
        user.setRole(Roles.INSTRUCTOR);
        UserEntity updatedUser = userRepository.save(user);
        
        log.info("Admin {} successfully promoted user ID: {} to instructor", principal.getName(), userId);
        
        return convertToAdminUserResponse(updatedUser);
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AdminUserResponse demoteFromInstructor(Principal principal, Integer userId) {
        log.info("Admin {} demoting instructor ID: {} to regular user", principal.getName(), userId);
        
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        if (user.getRole() != Roles.INSTRUCTOR) {
            throw new IllegalArgumentException("User is not an instructor");
        }
        
        user.setRole(Roles.USER);
        UserEntity updatedUser = userRepository.save(user);
        
        log.info("Admin {} successfully demoted instructor ID: {} to regular user", principal.getName(), userId);
        
        return convertToAdminUserResponse(updatedUser);
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<AdminUserResponse> getAllInstructors(Principal principal) {
        log.info("Admin {} requesting all instructors", principal.getName());
        
        List<UserEntity> instructors = userRepository.findByRole(Roles.INSTRUCTOR);
        
        return instructors.stream()
                .map(this::convertToAdminUserResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<AdminUserResponse> getAllAdmins(Principal principal) {
        log.info("Admin {} requesting all admins", principal.getName());
        
        List<UserEntity> admins = userRepository.findByRole(Roles.ADMIN);
        
        return admins.stream()
                .map(this::convertToAdminUserResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void performBulkAction(Principal principal, AdminBulkActionRequest request) {
        log.info("Admin {} performing bulk action: {} on {} users", 
                principal.getName(), request.getAction(), request.getUserIds().size());
        
        List<UserEntity> users = userRepository.findAllById(request.getUserIds());
        
        if (users.size() != request.getUserIds().size()) {
            throw new IllegalArgumentException("Some users not found");
        }
        
        switch (request.getAction()) {
            case UPDATE_ROLE:
                if (request.getNewRole() == null) {
                    throw new IllegalArgumentException("New role is required for role update");
                }
                users.forEach(user -> {
                    if (!user.getUsername().equals(principal.getName())) {
                        user.setRole(request.getNewRole());
                    }
                });
                break;
                
            case ACTIVATE_ACCOUNTS:
                users.forEach(user -> {
                    if (!user.getUsername().equals(principal.getName())) {
                        user.setActivated(true);
                    }
                });
                break;
                
            case DEACTIVATE_ACCOUNTS:
                users.forEach(user -> {
                    if (!user.getUsername().equals(principal.getName())) {
                        user.setActivated(false);
                    }
                });
                break;
                
            case DELETE_ACCOUNTS:
                users.forEach(user -> {
                    if (!user.getUsername().equals(principal.getName()) && canDeleteUser(user.getId())) {
                        user.setActivated(false);
                    }
                });
                break;
                
            default:
                throw new IllegalArgumentException("Unsupported bulk action: " + request.getAction());
        }
        
        userRepository.saveAll(users);
        
        log.info("Admin {} successfully completed bulk action: {}", principal.getName(), request.getAction());
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public byte[] exportUserData(Principal principal, List<Integer> userIds, String format) {
        log.info("Admin {} exporting user data for {} users in {} format", 
                principal.getName(), userIds.size(), format);
        
        // Implementation would depend on the export format (CSV, Excel, PDF)
        // For now, return empty byte array as placeholder
        return new byte[0];
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<Object> getUserActivityHistory(Principal principal, Integer userId) {
        log.info("Admin {} requesting activity history for user ID: {}", principal.getName(), userId);
        
        // Implementation would query activity logs for the user
        // For now, return empty list as placeholder
        return List.of();
    }
    
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Object getUserStatistics(Principal principal) {
        log.info("Admin {} requesting user statistics", principal.getName());
        
        // Implementation would calculate various user statistics
        // For now, return empty object as placeholder
        return new Object();
    }
    
    @Override
    public void validateUserData(AdminUserManagementRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        
        if (request.getRole() == null) {
            throw new IllegalArgumentException("Role is required");
        }
        
        // Additional validation logic can be added here
    }
    
    @Override
    public boolean canDeleteUser(Integer userId) {
        // Check if user has active programs, enrollments, etc.
        // For now, return true as placeholder
        return true;
    }
    
    private AdminUserResponse convertToAdminUserResponse(UserEntity user) {
        AdminUserResponse response = modelMapper.map(user, AdminUserResponse.class);
        
        // Get counts using separate queries to avoid lazy loading issues
        try {
            // Count fitness programs created by this user (if they're an instructor)
            long programCount = userRepository.countFitnessProgramsByInstructorId(user.getId());
            response.setProgramCount(programCount);
            
            // Count user programs (enrollments) for this user
            long enrollmentCount = userRepository.countUserProgramsByUserId(user.getId());
            response.setEnrollmentCount(enrollmentCount);
        } catch (Exception e) {
            log.warn("Error getting counts for user {}: {}", user.getId(), e.getMessage());
            response.setProgramCount(0L);
            response.setEnrollmentCount(0L);
        }
        
        // Set activity count (placeholder - would need actual activity count from database)
        response.setActivityCount(0L);
        response.setStudentCount(0L);
        
        // Set status based on activation
        response.setStatus(user.isActivated() ? "ACTIVE" : "INACTIVE");
        
        return response;
    }
}
